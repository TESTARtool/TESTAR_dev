import os
from time import sleep

from mysql.connector.pooling import MySQLConnectionPool
from mysql.connector.cursor import MySQLCursorBuffered


class MySqlDatabase():
    """Singletron database connector"""
    _instance = None

    def __del__(self):
        """Close open database connections"""
        try:
            self.db_pool.close()
        except:
            pass

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super(MySqlDatabase, cls).__new__(cls)
            # Initialization
            cls._instance.connect()
        return cls._instance


    def connect(self):
        """Create pooled database connection from enviroment variables"""

        # TODO: Database takes time to start...
        sleep(int(os.environ.get("MYSQL_WAIT", 0)))

        # Extract settings
        config = {
            "host": os.environ.get("MYSQL_HOST", "localhost"),
            "port": int(os.environ.get("MYSQL_PORT", 13306)),
            "user": os.environ.get("MYSQL_USER", "testar"),
            "password": os.environ.get("MYSQL_PASSWORD", "testar"),
            "database": os.environ.get("MYSQL_DATABASE", "testar"),
            "raise_on_warnings": True,
        }
        self.db_pool = MySQLConnectionPool(**config)

    def cursor(self, fn):
        """A Decorator to create cursors without adding redundant code everywhere.

        Args:
            fn (function): Function which need to access a cursor.
        """

        def inner(*args, **kwargs):
            with self.db_pool.get_connection() as connection:
                with connection.cursor(buffered=True) as cursor:
                    return fn(*args, cursor=cursor, **kwargs)
        return inner

    def does_exist(self,name: str, id: int) -> bool:
        """Verify if an object exists in the database.

        Args:
            name (str): Name of the table.
            id (int): Id of the object.
            cursor (MySQLCursorBuffered): Ignore. Provided by decorator.

        Returns:
            bool: True if exists.
        """

        @self.cursor
        def internal(cursor: MySQLCursorBuffered) -> bool:
            # Name is not injectable by the user.
            query = f"SELECT EXISTS(SELECT * FROM {name} WHERE id=%s);"

            # Format object id into the string
            cursor.execute(query, (id,))
            result = cursor.fetchone()

            # Verify if it has been found
            return result[0] == 1
        return internal()

    def get_property(
        self, field: str, table: str, object_id: int) -> object:
        """Retrieve a single property from a single row.

        Args:
            field (str): Name of the column in the table.
            table (str): Name of the table.
            id (int): Id of the row where the data should be retrieved from.
            cursor (MySQLCursorBuffered): Ignore provided by decorator.

        Returns:
            object: Any kind of data returned by the database
        """
        
        @self.cursor
        def internal(cursor: MySQLCursorBuffered) -> object:
            query = f"SELECT {field} FROM {table} WHERE id=%s"
            cursor.execute(query, (object_id,))
            result = cursor.fetchone()
            return result[0]
        return internal()