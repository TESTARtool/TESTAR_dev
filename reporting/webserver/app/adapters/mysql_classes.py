import os
from time import sleep
from typing import List
from datetime import datetime
from mysql.connector.pooling import MySQLConnectionPool

from .abstract_classes import (
    AbstractReport, AbstractSequence, AbstractAction)


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
    """A Decorator to create cursors without adding redundant code everywhere

    Args:
        fn (function): Function which need the decorator
    """
    def inner(*args, **kwargs):
        with db_pool.get_connection() as connection:
            with connection.cursor(buffered=True) as cursor:
                return fn(*args, cursor=cursor, **kwargs)
    return inner


@db_cursor
def does_exist(name: str, id: int, cursor: MySQLConnectionPool) -> bool:
    global db_connection
    query = f'SELECT EXISTS(SELECT * FROM {name} WHERE id=%s);'

    # Format own id into the string
    cursor.execute(query, (id,))
    result = cursor.fetchone()

    # Verify if it has been found
    return result[0] == 1


class Action(AbstractAction):
    # TODO: TEST EACH FEATURE
    def __init__(self, action_id: int, check: bool = True):
        self._id = id

        # Verify existance in the database
        if check and not does_exist('actions', self._id):
            raise ValueError('Report not found')

    @db_cursor
    def _get_property(self, name, cursor: MySQLConnectionPool):
        query = f'SELECT {name} FROM actions WHERE id=%s;'
        cursor.execute(query, (self._id,))
        result = cursor.fetchone()

        return result[0]

    def get_description(self) -> str:
        return self._get_property('description')

    def get_screenshot(self) -> str:
        return self._get_property('screenshot')

    def get_status(self) -> str:
        return self._get_property('status')

    def get_start_time(self) -> datetime:
        return self._get_property('start_time')

    def get_name(self) -> str:
        return self._get_property('name')

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
    def get_actions(self, cursor: MySQLConnectionPool) -> List[Action]:
        query = 'SELECT id FROM actions WHERE iteration_id=%s'
        actions = []

        cursor.execute(query, (self._id,))
        for row in cursor.fetchall():
            actions.append(Sequence(row[0], check=False))
        return actions

    @db_cursor
    def get_severity(self, cursor: MySQLConnectionPool) -> float:
        query = 'SELECT severity FROM reports WHERE id=%s;'
        cursor.execute(query, (self._id,))
        result = cursor.fetchone()

        return result[0]

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
    def get_sequences(self, cursor: MySQLConnectionPool) -> List[Sequence]:
        query = 'SELECT id FROM iterations WHERE report_id=%s'
        sequences = []

        cursor.execute(query, (self._id,))
        for row in cursor.fetchall():
            sequences.append(Sequence(row[0], check=False))
        return sequences

    def get_id(self) -> int:
        return self._id

    @db_cursor
    def get_url(self, cursor: MySQLConnectionPool) -> str:
        query = 'SELECT url FROM reports WHERE id=%s;'
        cursor.execute(query, (self._id,))
        result = cursor.fetchone()

        return result[0]

    @db_cursor
    def get_actions_per_sequence(self, cursor: MySQLConnectionPool) -> int:
        query = 'SELECT actions_per_sequence FROM reports WHERE id=%s;'
        cursor.execute(query, (self._id,))
        result = cursor.fetchone()

        return result[0]

    @db_cursor
    def get_sequence_by_id(self, id: int, cursor: MySQLConnectionPool) -> Sequence:
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
    def get_reports(cls, cursor: MySQLConnectionPool) -> List:
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
