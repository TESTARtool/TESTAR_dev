from datetime import datetime
from typing import List
import numpy as np

from .abstract_classes import (
    AbstractAction, AbstractReport, AbstractSequence)

# Example for fake data generation
URLS = ['https://example.com', 'https://www.ou.nl/', 'https://testar.org/',
        'https://duckduckgo.com/', 'http://github.com/', 'https://marviq.com/']
SEED = 0
WORDS = ['lorem', 'ipsum', 'dolor', 'sit', 'amet', 'consectetur', 'adipiscing', 'elit', 'sed',
         'do', 'eiusmod', 'tempor', 'incididunt', 'ut', 'labore', 'et', 'dolore', 'magna', 'aliqua']


class Action(AbstractAction):

    def __init__(self, id: int):
        super().__init__()
        self._id = id
        self._random = np.random.RandomState(id)

        # Generate random datetime
        self._started = datetime.fromtimestamp(
            self._random.randint(20e7, 20e8))

        self._name = self._random.choice(WORDS)

        # Generate description
        temp = []
        for _ in range(self._random.randint(3, 15)):
            temp.append(self._random.choice(WORDS))
        self._description = ' '.join(temp)

        # Generate status
        temp = []
        if self._random.randint(1, 5) == 1:
            for _ in range(self._random.randint(1, 5)):
                temp.append(self._random.choice(WORDS))
            self._status = ' '.join(temp)
        else:
            self._status = 'OK'

    def get_screenshot(self) -> str:
        return '/static/screenshot/test.png'

    def get_description(self) -> str:
        return self._description

    def get_status(self) -> str:
        return self._status

    def get_start_time(self) -> datetime:
        return self._started

    def get_id(self) -> int:
        return self._id

    def get_name(self) -> str:
        return self._name


class Sequence(AbstractSequence):

    def __init__(self, id: int, max_actions: int):
        super().__init__()
        self._random = np.random.RandomState(id)
        self._id = id

        # Generate actions
        self._actions = []
        for _ in range(self._random.randint(1, max_actions)):
            action = Action(self._random.randint(0, 1e5))
            self._actions.append(action)

        # Random severity
        self._severity = self._random.choice(
            [1, 0, 0.99999990, 0.99999999, 0.00000009, 0.00000001])

    def get_severity(self) -> float:
        return self._severity

    def get_actions(self) -> List[Action]:
        return self._actions

    def get_id(self) -> int:
        return self._id


class Report(AbstractReport):

    def __init__(self, id: int):
        super().__init__()
        self._id = id
        self._random = np.random.RandomState(id)
        self._sequences = []
        self._url = self._random.choice(URLS)
        self._actions_per_sequence = self._random.randint(5, 100)

        # Generate a random amount of sequences
        for _ in range(self._random.randint(0, 25)):
            sequence = Sequence(self._random.randint(
                0, 1e5), self._actions_per_sequence)
            self._sequences.append(sequence)

    def get_sequences(self) -> List[Sequence]:
        return self._sequences

    def get_url(self) -> str:
        return self._url

    def get_actions_per_sequence(self) -> int:
        return self._actions_per_sequence

    def get_id(self) -> int:
        return self._id

    def get_sequence_by_id(self, id) -> Sequence:
        return Sequence(id, self._actions_per_sequence)

    @classmethod
    def get_reports(cls) -> list:
        np.random.seed(SEED)
        report_count = np.random.randint(0, 25)
        reports = []

        # Generate a random amount of reports
        for _ in range(report_count):
            report_id = np.random.randint(0, 1e5)
            reports.append(Report(report_id))
        return reports

    @classmethod
    def get_report_by_id(cls, id: int):
        return Report(id)
