import os
import re
import pyorient

_SANITIZE_EXPRESSION = re.compile(r"['\"`-]+")


class OrientDB:
    """Singletron database connector"""

    _instance = None

    def __del__(self):
        """Close open database connections"""
        try:
            self.database.close()  # Untested
            self._client.close()
        except:
            pass

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(OrientDB, cls).__new__(cls)
            # Initialization
            cls._instance._connect()
        return cls._instance

    @staticmethod
    def _sanitize(query: str, *args) -> str:
        """This is a non recommended way to sanitize orientdb queries.
        Please replace this with proper sanitization

        Args:
            query (str): input query
            args (tuple): arguments which should be inserted into the query

        Returns:
            str: "Santized" query
        """
        for arg in args:
            if isinstance(arg, str):
                sanitized = re.sub(_SANITIZE_EXPRESSION, "", arg)
                query = re.sub("\?", sanitized, query, 1)
            else:
                # Integers, floats
                query = re.sub("\?", sanitized, query, 1)
        return query

    def _connect(self):
        """Create connection to OrientDB"""
        host = os.environ.get("ORIENTDB_HOST", "orientdb")
        user = os.environ.get("ORIENTDB_USER", "root")
        password = os.environ.get("ORIENTDB_PASSWORD", "testar")
        database = os.environ.get("ORIENTDB_DATABASE", "testar")
        port = int(os.environ.get("ORIENTDB_PORT", 2424))

        # Note: the connection can fail.
        self._client = pyorient.OrientDB(host, port)
        self.database = self._client.db_open(database, user, password)

    def _is_active(self):
        """Test the connection by requesting all databases"""
        try:
            self._client.db_list()
            return True
        except:
            return False

    def _refresh_connection_if_needed(self):
        if not self._is_active:
            self._connect

    def query(self, query, *args, **kwargs) -> list:
        self._refresh_connection_if_needed()
        sanitized_query = self._sanitize(query, *args)
        return self._client.query(sanitized_query, **kwargs)
