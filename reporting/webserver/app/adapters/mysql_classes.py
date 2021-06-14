import os
from time import sleep
from typing import List
from datetime import datetime
from mysql.connector import connect, Error

from .abstract_classes import (
    AbstractReport, AbstractSequence)

db_connection = None


class Report(AbstractReport):

    def __init__(self, id: int):
        self._id = id

        # Verify existance in the database
        if not self._does_exist():
            raise ValueError('Report not found')
        

    def _does_exist(self) -> bool:
        query = 'SELECT EXISTS(SELECT * FROM reports WHERE id=%s);'
        with db_connection.cursor() as cursor:

            # Format own id into the string
            cursor.execute(query, (self._id,))
            result = cursor.fetchone()

            # Verify if it has been found
            return result[0] == 1

    def get_sequences(self) -> List[AbstractSequence]:
        with db_connection.cursor() as cursor:
            cursor.execute('')

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
