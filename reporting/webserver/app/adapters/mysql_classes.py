import os
from time import sleep
from typing import List
from datetime import datetime
from mysql.connector.pooling import MySQLConnectionPool
from mysql.connector.cursor import MySQLCursorBuffered

from .abstract_classes import (
    AbstractReport, AbstractSequence, AbstractAction)

# TODO: The current state of this file has not been tested.


def setup_db_pool():
    """Create pooled database connection from enviroment variables"""
    global db_pool

    # TODO: Database takes time to start...
    sleep(5)

    # Extract settings
    config = {
        "host": os.environ.get("MYSQL_HOST", "localhost"),
        "port": int(os.environ.get("MYSQL_PORT", 13306)),
        "user": os.environ.get("MYSQL_USER", "testar"),
        "password": os.environ.get("MYSQL_PASSWORD", "testar"),
        "database": os.environ.get("MYSQL_DATABASE", "testar"),
        "raise_on_warnings": True,
    }

    db_pool = MySQLConnectionPool(**config)


def db_cursor(fn):
    """A Decorator to create cursors without adding redundant code everywhere.

    Args:
        fn (function): Function which need to access a cursor.
    """
    def inner(*args, **kwargs):
        with db_pool.get_connection() as connection:
            with connection.cursor(buffered=True) as cursor:
                return fn(*args, cursor=cursor, **kwargs)
    return inner


@db_cursor
def does_exist(name: str, id: int, cursor: MySQLCursorBuffered) -> bool:
    """Verify if an object exists in the database.

    Args:
        name (str): Name of the table.
        id (int): Id of the object.
        cursor (MySQLCursorBuffered): Ignore. Provided by decorator.

    Returns:
        bool: True if exists.
    """
    global db_connection

    # Name is not injectable by the user.
    query = f'SELECT EXISTS(SELECT * FROM {name} WHERE id=%s);'

    # Format object id into the string
    cursor.execute(query, (id,))
    result = cursor.fetchone()

    # Verify if it has been found
    return result[0] == 1


@db_cursor
def get_property(field: str, table: str, object_id: int, cursor: MySQLCursorBuffered) -> object:
    """Retrieve a single property from a single row.

    Args:
        field (str): Name of the column in the table.
        table (str): Name of the table.
        id (int): Id of the row where the data should be retrieved from.
        cursor (MySQLCursorBuffered): Ignore provided by decorator.

    Returns:
        object: Any kind of data returned by the database
    """

    query = f'SELECT {field} FROM {table} WHERE id=%s'
    cursor.execute(query, (object_id,))
    result = cursor.fetchone()

    return result[0]


class Action(AbstractAction):
    # TODO: TEST EACH FEATURE
    def __init__(self, action_id: int, check: bool = True):
        self._id = action_id

        # Verify existance in the database
        if check and not does_exist('actions', self._id):
            raise ValueError('Report not found')

    def get_description(self) -> str:
        return get_property('description', 'actions', self._id)

    def get_screenshot(self) -> str:
        return get_property('screenshot', 'actions', self._id)

    def get_status(self) -> str:
        return get_property('status', 'actions', self._id)

    def get_start_time(self) -> datetime:
        return get_property('start_time', 'actions', self._id)

    def get_name(self) -> str:
        return get_property('name', 'actions', self._id)

    def get_id(self) -> int:
        return self._id


class Sequence(AbstractSequence):
    # TODO: TEST EACH FEATURE
    def __init__(self, sequence_id: int, check: bool = True):
        self._id = sequence_id

        # Verify existance in the database
        if check and not does_exist('iterations', self._id):
            raise ValueError('sequence not found')

    @db_cursor
    def get_actions(self, cursor: MySQLCursorBuffered) -> List[Action]:
        query = 'SELECT id FROM actions WHERE iteration_id=%s'
        actions = []

        cursor.execute(query, (self._id,))
        for row in cursor.fetchall():
            actions.append(Action(row[0], check=False))
        return actions

    def get_severity(self) -> float:
        return get_property('severity', 'iterations', self._id)

    def get_info(self) -> str:
        return get_property('info', 'iterations', self._id)

    def get_id(self) -> int:
        return self._id


class Report(AbstractReport):
    # TODO: TEST EACH FEATURE
    def __init__(self, report_id: int, check: bool = True):
        self._id = report_id

        # Verify existance in the database
        if check and not does_exist('reports', self._id):
            raise ValueError('Report not found')

    @db_cursor
    def get_sequences(self, cursor: MySQLCursorBuffered) -> List[Sequence]:
        query = 'SELECT id FROM iterations WHERE report_id=%s'
        sequences = []

        cursor.execute(query, (self._id,))
        for row in cursor.fetchall():
            sequences.append(Sequence(row[0], check=False))
        return sequences

    def get_id(self) -> int:
        return self._id

    def get_url(self) -> str:
        return get_property('url', 'reports', self._id)

    def get_actions_per_sequence(self) -> int:
        return get_property('actions_per_sequence', 'reports', self._id)

    def get_name(self) -> str:
        return get_property('tag', 'reports', self._id)

    def sequence_count(self) -> int:
        return get_property('total_sequences', 'reports', self._id)

    def get_start_time(self) -> int:
        return get_property('time', 'reports', self._id)

    @db_cursor
    def get_sequence_by_id(self, id: int, cursor: MySQLCursorBuffered) -> Sequence:
        query = 'SELECT EXISTS(SELECT * FROM iterations WHERE report_id=%s and id=%s);'

        # Format own id into the string
        cursor.execute(query, (self._id, id,))
        result = cursor.fetchone()

        # Verify if it has been found
        if result[0] == 1:
            return Sequence(id)
        raise Exception(f'No child sequence found with id: {id}')

    @classmethod
    @db_cursor
    def get_reports(cls, cursor: MySQLCursorBuffered) -> List:
        query = 'SELECT id FROM reports'
        reports = []

        cursor.execute(query)
        for result in cursor.fetchall():
            reports.append(Report(result[0]))

        return reports


if __name__ == "__main__":
    setup_db_pool()
    print('Successfully connected')
    db_pool.close()
