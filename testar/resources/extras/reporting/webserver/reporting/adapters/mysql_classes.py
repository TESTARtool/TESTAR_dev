from typing import List
from datetime import datetime
from mysql.connector.cursor import MySQLCursorBuffered

from external import MySqlDatabase
from .abstract_classes import AbstractReport, AbstractSequence, AbstractAction


database = MySqlDatabase()

class Action(AbstractAction):
    # TODO: TEST EACH FEATURE
    def __init__(self, action_id: int, check: bool = True):
        self._id = action_id

        # Verify existance in the database
        if check and not database.does_exist("actions", self._id):
            raise ValueError("Report not found")

    def get_description(self) -> str:
        return database.get_property("description", "actions", self._id)

    def get_screenshot(self) -> str:
        normal_path = database.get_property("screenshot", "actions", self._id)
        web_path = "/static" + normal_path[1:]
        return web_path

    def get_status(self) -> str:
        return database.get_property("status", "actions", self._id)

    def get_start_time(self) -> datetime:
        return database.get_property("start_time", "actions", self._id)

    def get_name(self) -> str:
        return database.get_property("name", "actions", self._id)

    def get_id(self) -> int:
        return self._id


class Sequence(AbstractSequence):
    def __init__(self, sequence_id: int, check: bool = True):
        self._id = sequence_id

        # Verify existance in the database
        if check and not database.does_exist("iterations", self._id):
            raise ValueError("sequence not found")

    @database.cursor
    def get_actions(self, cursor: MySQLCursorBuffered) -> List[Action]:
        query = "SELECT id FROM actions WHERE iteration_id=%s"
        actions = []

        cursor.execute(query, (self._id,))
        for row in cursor.fetchall():
            actions.append(Action(row[0], check=False))
        return actions

    def get_severity(self) -> float:
        return database.get_property("severity", "iterations", self._id)

    def get_info(self) -> str:
        return database.get_property("info", "iterations", self._id)

    def get_id(self) -> int:
        return self._id


class Report(AbstractReport):
    def __init__(self, report_id: int, check: bool = True):
        self._id = report_id

        # Verify existance in the database
        if check and not database.does_exist("reports", self._id):
            raise ValueError("Report not found")

    @database.cursor
    def get_sequences(self, cursor: MySQLCursorBuffered) -> List[Sequence]:
        query = "SELECT id FROM iterations WHERE report_id=%s"
        sequences = []

        cursor.execute(query, (self._id,))
        for row in cursor.fetchall():
            sequences.append(Sequence(row[0], check=False))
        return sequences

    def get_id(self) -> int:
        return self._id

    def get_url(self) -> str:
        return database.get_property("url", "reports", self._id)

    def get_actions_per_sequence(self) -> int:
        return database.get_property("actions_per_sequence", "reports", self._id)

    def get_name(self) -> str:
        return database.get_property("tag", "reports", self._id)

    def sequence_count(self) -> int:
        return database.get_property("total_sequences", "reports", self._id)

    def get_start_time(self) -> int:
        return database.get_property("time", "reports", self._id)

    @database.cursor
    def get_sequence_by_id(self, id: int, cursor: MySQLCursorBuffered) -> Sequence:
        query = "SELECT EXISTS(SELECT * FROM iterations WHERE report_id=%s and id=%s);"

        # Format own id into the string
        cursor.execute(
            query,
            (
                self._id,
                id,
            ),
        )
        result = cursor.fetchone()

        # Verify if it has been found
        if result[0] == 1:
            return Sequence(id)
        raise Exception(f"No child sequence found with id: {id}")

    @classmethod
    @database.cursor
    def get_reports(cls, cursor: MySQLCursorBuffered) -> List:
        query = "SELECT id FROM reports"
        reports = []

        cursor.execute(query)
        for result in cursor.fetchall():
            reports.append(Report(result[0]))

        return reports