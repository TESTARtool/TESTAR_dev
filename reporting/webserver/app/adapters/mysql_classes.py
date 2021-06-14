import os
from time import sleep
from typing import List
from datetime import datetime
from mysql.connector import connect, Error

from .abstract_classes import (
    AbstractReport, AbstractSequence, AbstractAction)

db_connection = None


def setup_db_connection():
    """Create database connection from enviroment variables"""
    global db_connection

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

    db_connection = connect(**config)


def does_exist(name: str, id: int) -> bool:
    global db_connection
    query = f'SELECT EXISTS(SELECT * FROM {name} WHERE id=%s);'

    # This is
    if db_connection is None:
        setup_db_connection()
        #raise Error('Not connected to the database')
    with db_connection.cursor() as cursor:

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

    def _get_property(self, name):
        query = f'SELECT {name} FROM actions WHERE id=%s;'
        with db_connection.cursor() as cursor:
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

    def get_actions(self) -> List[AbstractAction]:
        query = 'SELECT id FROM actions WHERE iteration_id=%s'
        actions = []

        with db_connection.cursor() as cursor:
            cursor.execute(query, (self._id,))
            for row in cursor.fetchall():
                actions.append(Sequence(row[0], check=False))
        return actions

    def get_severity(self) -> float:
        query = 'SELECT severity FROM reports WHERE id=%s;'
        with db_connection.cursor() as cursor:
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

    def get_sequences(self) -> List[AbstractSequence]:
        query = 'SELECT id FROM iterations WHERE report_id=%s'
        sequences = []

        with db_connection.cursor() as cursor:
            cursor.execute(query, (self._id,))
            for row in cursor.fetchall():
                sequences.append(Sequence(row[0], check=False))
        return sequences

    def get_id(self) -> int:
        return self._id

    def get_url(self) -> str:
        query = 'SELECT url FROM reports WHERE id=%s;'
        with db_connection.cursor() as cursor:
            cursor.execute(query, (self._id,))
            result = cursor.fetchone()

            return result[0]

    def get_actions_per_sequence(self) -> int:
        query = 'SELECT actions_per_sequence FROM reports WHERE id=%s;'
        with db_connection.cursor() as cursor:
            cursor.execute(query, (self._id,))
            result = cursor.fetchone()

            return result[0]

    def get_sequence_by_id(self, id: int) -> AbstractSequence:
        query = 'SELECT EXISTS(SELECT * FROM iterations WHERE report_id=%s and id=%s);'
        with db_connection.cursor() as cursor:

            # Format own id into the string
            cursor.execute(query, (self._id, id,))
            result = cursor.fetchone()

            # Verify if it has been found
            if result[0] == 1:
                return Sequence(id)
        raise Error(f'No child sequence found with id: {id}')

    @classmethod
    def get_reports(cls) -> List:
        query = 'SELECT id FROM reports'
        reports = []

        with db_connection.cursor() as cursor:
            cursor.execute(query)
            for result in cursor.fetchall():
                reports.append(Report(result[0]))

        return reports


def test():
    """Really simple test if the connection works"""
    print("Testing database connection..")
    setup_db_connection()

    print("Show the tables")
    cursor = db_connection.cursor()
    cursor.execute("SHOW TABLES;")
    for table in cursor:
        print(table)

    print("Closing the connection")
    cursor.close()
    db_connection.close()


if __name__ == "__main__":
    test()
