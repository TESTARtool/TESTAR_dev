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
        states = self.query("SELECT * FROM ConcreteState")
        
        return [self._state_to_object(x) for x in states]

    def concrete_state_from_cid(self, cid):
        states = self.query(f'SELECT * FROM ConcreteState WHERE ConcreteID="{cid}"')

        # Verify if the state exists
        if not states:
            return None

        return self._state_to_object(states[0])

    def _is_valid_edge(self, edge):
        if 'ConcreteID' not in edge._OrientRecord__o_storage:
            return False
        else:
            return True

    def _edge_to_object(self, uid):
        edge = self.query(f'SELECT out.ConcreteID, in.ConcreteID, Shape, ConcreteID, `Desc`, ValuePattern FROM ConcreteAction WHERE uid="{uid}";')[0]

        shape_source = edge.oRecordData['Shape'] if 'Shape' in edge.oRecordData else edge._OrientRecord__o_storage['WebBoundingRectangle']
        shape = [int(float(x.group())) for x in re.finditer(r"\d*\.0", shape_source)]
        
        return {    
            "class_type" : edge.oRecordData,
            "shape" : shape,
            "description" : edge.oRecordData['Desc'],
            "fromState": edge.oRecordData['out'],
            "toState": edge.oRecordData['in'],
            "value": edge.oRecordData['ValuePattern'],
            "ConcreteID": edge.oRecordData['ConcreteID']

        }

    def _state_to_object(self, state):
        rid = state._OrientRecord__rid

        # Retrieve edge orient objects
        edges_in = self.query(
                f'SELECT uid FROM (TRAVERSE inE() FROM {rid}) WHERE @class="ConcreteAction"'
            )
        edges_out = self.query(
                f'SELECT uid FROM (TRAVERSE outE() FROM {rid}) WHERE @class="ConcreteAction"'
            )

        # Convert to dicts
        edges_in = [self._edge_to_object(x.oRecordData['uid']) for x in edges_in]
        edges_out = [self._edge_to_object(x.oRecordData['uid']) for x in edges_out]
        
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