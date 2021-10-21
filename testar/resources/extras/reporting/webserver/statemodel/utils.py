import os
import re
from external import OrientDB

orientdb = OrientDB()


def concrete_states() -> list:
    states = orientdb.query('SELECT format("%s", @rid), * FROM ConcreteState')

    return _states_to_object(states)


def concrete_state_from_cid(cid):
    states = orientdb.query(
        f'SELECT format("%s", @rid), * FROM ConcreteState WHERE ConcreteID="{cid}"'
    )

    # Verify if the state exists
    if not states:
        return None

    return _state_to_object(states[0])


def concrete_state_traverse(cid_from: str, cid_to: str):
    """Attempt to find the shortest to traverse between 2 concrete states.

    Args:
        cid_from (str): Path starting location.
        cid_to (str): Path goal location.

    Returns:
        list: Steps of concrete states to reach the given concrete state.
        None:  When one of the given states is not found.
    """
    state_from = concrete_state_from_cid(cid_from)
    state_to = concrete_state_from_cid(cid_to)

    # Returns none when one of the given states is not found.
    if state_from is None or state_to is None:
        return None

    rid_from = state_from["rid"]
    rid_to = state_to["rid"]

    query = "SELECT format('%s', @rid), * FROM (SELECT flatten(ASTAR) FROM (SELECT ASTAR(?, ?, 'ConcreteAction', {'direction': 'OUT', 'parallel' : 1 }) FROM ConcreteState LIMIT 1))"
    vertex_traversed = orientdb.query(query, rid_from, rid_to)
    return [_state_to_object(x) for x in vertex_traversed]


def _edge_to_object(uid: str):
    query = "SELECT format('%s', @rid), out.ConcreteID, in.ConcreteID, in.oracleVerdictCode , `Shape`, `ConcreteID`, `Desc`, `ValuePattern` FROM ConcreteAction WHERE uid='?';"
    edge = orientdb.query(query, uid)
    # Select first if it exists or return nothing
    if edge:
        edge = edge[0]
    else:
        return

    if not "Shape" in edge.oRecordData:
        return

    shape_source = edge.oRecordData["Shape"]
    shape = [int(float(x.group())) for x in re.finditer(r"\d*\.0", shape_source)]

    return {
        "rid": edge.format,
        "shape": shape,
        "description": edge.oRecordData["Desc"],
        "fromState": edge.oRecordData["out"],
        "toState": edge.oRecordData["in"],
        "value": edge.oRecordData["ValuePattern"],
        "ConcreteID": edge.oRecordData["ConcreteID"],
        "toOracle": edge.oRecordData["in2"] != 1,
    }


def _edges_to_object(edges):
    """Convert a list of edges uids to a list of edge objects.

    Args:
        edges (list<uid>): A list with edge uids
    """
    output = []

    for edge in edges:

        # Extract uid from OrientRecord
        if hasattr(edge, "oRecordData"):
            edge = edge.oRecordData["uid"]

        edge_object = _edge_to_object(edge)

        # Ignore empty edges
        if edge_object is None:
            continue

        output.append(edge_object)
    return output


def _states_to_object(states):
    """Convert a list of oRecord states to a list of state objects.

    Args:
        states (list<oRecordData>): A list with edge uids
    """
    output = []
    for state in states:

        state_object = _state_to_object(state)

        # Ignore empty edges
        if state_object is None:
            continue

        output.append(state_object)
    return output


def _state_to_object(state):
    rid = state.format

    # Retrieve edge orient objects
    edges_in = orientdb.query(
        "SELECT `uid` FROM (TRAVERSE inE() FROM ?) WHERE @class='ConcreteAction'", rid
    )
    edges_out = orientdb.query(
        "SELECT `uid` FROM (TRAVERSE outE() FROM ?) WHERE @class='ConcreteAction'", rid
    )

    # Convert to dicts
    edges_in = _edges_to_object(edges_in)
    edges_out = _edges_to_object(edges_out)

    # Extract shape
    if not hasattr(state, "Shape"):
        return

    shape = [int(float(x.group())) for x in re.finditer(r"\d*\.0", state.Shape)]

    return {
        "cid": state.ConcreteID,
        "rid": rid,
        "screenshot": "/static" + state.ScreenshotPath[1:],
        "shape": shape,
        "description": state.Desc,
        "edges_out": edges_out,
        "edges_in": edges_in,
        "oracle": state.oRecordData["oracleVerdictCode"] != 1,
    }
