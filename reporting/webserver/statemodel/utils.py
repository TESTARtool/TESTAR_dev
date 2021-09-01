import os
import re
import pyorient

class OrientDB:
    def __init__(self, host: str, port: int, user: str, database: str, password: str):
        self._client = pyorient.OrientDB(host, port)
        self.database = self._client.db_open(database, user, password)

    def query(self, command, **kwargs) -> list:
        return self._client.query(command, **kwargs)

    def concrete_states(self) -> list:
        states = self.query('SELECT format("%s", @rid), * FROM ConcreteState')
        
        return [self._state_to_object(x) for x in states]

    def concrete_state_from_cid(self, cid):
        states = self.query(f'SELECT format("%s", @rid), * FROM ConcreteState WHERE ConcreteID="{cid}"')

        # Verify if the state exists
        if not states:
            return None

        return self._state_to_object(states[0])

    def concrete_state_traverse(self, cid_from: str, cid_to: str):
        """Attempt to find the shortest to traverse between 2 concrete states.

        Args:
            cid_from (str): Path starting location.
            cid_to (str): Path goal location.

        Returns:
            list: Steps of concrete states to reach the given concrete state.
            None:  When one of the given states is not found.
        """
        state_from = self.concrete_state_from_cid(cid_from)
        state_to = self.concrete_state_from_cid(cid_to)

        # Returns none when one of the given states is not found.
        if state_from is None or state_to is None:
            return None

        rid_from = state_from['rid']
        rid_to = state_to['rid']

        query = f"SELECT format(\"%s\", @rid), * FROM (SELECT flatten(ASTAR) FROM (SELECT ASTAR({rid_from}, {rid_to}, 'ConcreteAction', {{'direction': 'OUT', 'parallel' : 1 }}) FROM ConcreteState LIMIT 1))"
        vertex_traversed = self.query(query)
        return [self._state_to_object(x) for x in vertex_traversed]

    def _edge_to_object(self, uid):
        edge = self.query(f'SELECT format("%s", @rid), out.ConcreteID, in.ConcreteID, in.oracleVerdictCode , Shape, ConcreteID, `Desc`, ValuePattern FROM ConcreteAction WHERE uid="{uid}";')

        # Select first if it exists or return nothing
        if edge:
            edge = edge[0]
        else:
            return

        if not 'Shape' in edge.oRecordData:
            return

        shape_source = edge.oRecordData['Shape']
        shape = [int(float(x.group())) for x in re.finditer(r"\d*\.0", shape_source)]
        
        return {    
            "rid" : edge.format,
            "shape" : shape,
            "description" : edge.oRecordData['Desc'],
            "fromState": edge.oRecordData['out'],
            "toState": edge.oRecordData['in'],
            "value": edge.oRecordData['ValuePattern'],
            "ConcreteID": edge.oRecordData['ConcreteID'],
            "toOracle": edge.oRecordData['in2'] != 1

        }

    def _edges_to_object(self, edges):
        """Convert a list of edges uids to a list of edge objects.

        Args:
            edges (list<uid>): A list with edge uids
        """
        output = []
        for edge in edges:

            # Extract uid from OrientRecord
            if hasattr(edge, 'oRecordData'):
                edge = edge.oRecordData['uid']

            edge_object = self._edge_to_object(edge)

            # Ignore empty edges
            if edge_object is None:
                continue

            output.append(edge_object)
        return output


    def _state_to_object(self, state):
        rid = state.format

        # Retrieve edge orient objects
        edges_in = self.query(
                f'SELECT uid FROM (TRAVERSE inE() FROM {rid}) WHERE @class="ConcreteAction" '
            )
        edges_out = self.query(
                f'SELECT uid FROM (TRAVERSE outE() FROM {rid}) WHERE @class="ConcreteAction"'
            )

        # Convert to dicts
        edges_in = self._edges_to_object(edges_in)
        edges_out = self._edges_to_object(edges_out)
        
        # Extract shape
        shape_source = state.Shape
        shape = [int(float(x.group())) for x in re.finditer(r"\d*\.0", shape_source)]

        return {
            "cid": state.ConcreteID,
            "rid": rid,
            "screenshot": '/static' + state.ScreenshotPath[1:],
            "shape": shape,
            "description": state.Desc,
            "edges_out": edges_out,
            "edges_in": edges_in,
            "oracle": state.oRecordData['oracleVerdictCode'] != 1
        }

    @classmethod
    def from_env(cls):
        host = os.environ.get("ORIENTDB_HOST", "orientdb")
        user = os.environ.get("ORIENTDB_USER", "root")
        password = os.environ.get("ORIENTDB_PASSWORD", "testar")
        database = os.environ.get("ORIENTDB_DATABASE", "testar")
        port = int(os.environ.get("ORIENTDB_PORT", 2424))
        return cls(host, port, user, database, password)