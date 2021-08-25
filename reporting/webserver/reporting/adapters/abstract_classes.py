from abc import ABC, abstractclassmethod, abstractmethod
from datetime import datetime
from typing import List
from enum import Enum

# TODO: Implement states


class Veredict(Enum):
    SEVERITY_OK = 0
    SEVERITY_FAIL = 1
    SEVERITY_NOT_RESPONDING = 0.99999990
    SEVERITY_NOT_RUNNING = 0.99999999
    SEVERITY_SUSPICIOUS_TITLE = 0.00000009
    SEVERITY_WARNING = 0.00000001

    def __str__(self) -> str:
        return self.name

    def is_oracle(self) -> bool:
        return self.value == 1 or self.value == 0.00000009

    def is_issue(self) -> bool:
        return not self.is_oracle() and self.value > 0

    @staticmethod
    def from_float(real: float):
        try:
            return Veredict[real]
        except KeyError:
            raise ValueError()


class AbstractAction(ABC):
    @abstractmethod
    def get_screenshot(self) -> str:
        pass

    @abstractmethod
    def get_description(self) -> str:
        pass

    @abstractmethod
    def get_status(self) -> str:
        pass

    @abstractmethod
    def get_start_time(self) -> datetime:
        pass

    @abstractmethod
    def get_id(self) -> int:
        pass

    @abstractmethod
    def get_name(self) -> str:
        pass


class AbstractSequence(ABC):
    @abstractmethod
    def get_actions(self) -> List[AbstractAction]:
        pass

    @abstractmethod
    def get_severity(self) -> float:
        pass

    @abstractmethod
    def get_id(self) -> int:
        pass

    @abstractmethod
    def get_info(self) -> int:
        pass

    def action_count(self) -> int:
        return len(self.get_actions())

    def issue_count(self) -> int:

        # Return zero when the severity is zero
        if self.get_severity() == 0:
            return 0

        actions = self.get_actions()
        issues = 0

        # Don't check for issues in the last action
        for i in range(len(actions) - 1):
            action = actions[i]
            if action.get_status() != "OK":
                issues += 1

        return issues

    def has_oracle(self) -> bool:
        severity = self.get_severity()
        veredict = Veredict(severity)
        return veredict.is_oracle()

    def veredict_count(self) -> int:
        # Return zero when the severity is zero
        if self.get_severity() == 0:
            return 0

        counter = 0
        for action in self.get_actions():
            if action.get_status() != "OK":
                counter += 1

        return counter

    def get_veredict(self) -> Veredict:
        return Veredict(self.get_severity())


class AbstractReport(ABC):
    @abstractmethod
    def get_sequences(self) -> List[AbstractSequence]:
        pass

    @abstractmethod
    def get_id(self) -> int:
        pass

    @abstractmethod
    def get_url(self) -> str:
        pass

    @abstractmethod
    def get_actions_per_sequence(self) -> int:
        pass

    @abstractmethod
    def get_sequence_by_id(self, id: int) -> AbstractSequence:
        pass

    @abstractmethod
    def get_start_time(self) -> int:
        pass

    @abstractmethod
    def get_name(self) -> str:
        """Retrieve a human readable report descriptor

        Returns:
            str: Report name
        """
        pass

    @abstractclassmethod
    def get_reports(cls) -> List:
        pass

    def veredict_count(self) -> int:
        print("--" * 1000)
        veredict = 0
        for sequence in self.get_sequences():
            veredict += sequence.veredict_count()
        return veredict

    def sequence_count(self) -> int:
        return len(self.get_sequences())

    def total_actions(self) -> int:
        action_count = 0
        for sequence in self.get_sequences():
            action_count += sequence.action_count()
        return action_count

    def get_ok_sequence_count(self) -> int:
        output = 0
        for sequence in self.get_sequences():
            if sequence.get_severity() == 0:
                output += 1
        return output

    def oracle_count(self) -> int:
        output = 0
        for sequence in self.get_sequences():
            if sequence.get_veredict().is_oracle():
                output += 1
        return output
