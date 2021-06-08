from abc import ABC, abstractclassmethod, abstractmethod
from datetime import datetime
from typing import List
# TODO: Data model is probably going to look different


class AbstractVerdict(ABC):
    @abstractmethod
    def get_id(self) -> int:
        pass

    @abstractmethod
    def get_tag(self) -> str:
        pass

    @abstractmethod
    def get_severity(self) -> float:
        pass


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
    def get_started(self) -> datetime:
        pass

    @abstractmethod
    def get_id(self) -> int:
        pass


class AbstractSequence(ABC):
    @abstractmethod
    def get_actions(self) -> List[AbstractAction]:
        pass

    @abstractmethod
    def get_verdicts(self) -> List[AbstractVerdict]:
        pass

    @abstractmethod
    def get_id(self) -> int:
        pass

    def action_count(self) -> int:
        return len(self.get_actions())

    def verdict_count(self) -> int:
        return len(self.get_verdicts())

    def has_verdicts(self) -> bool:
        return bool(self.verdict_count())


class AbstractReport(ABC):
    @abstractmethod
    def get_sequences(self) -> List[AbstractSequence]:
        pass

    @abstractclassmethod
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

    @abstractclassmethod
    def get_reports(cls) -> List:
        pass

    @abstractclassmethod
    def get_report_by_id(cls, id: int) -> List:
        pass

    def verdict_count(self) -> int:
        verdict = 0
        for sequence in self.get_sequences():
            verdict += sequence.verdict_count()
        return verdict

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
            if not sequence.has_verdicts():
                output += 1
        return output

    def get_oracle_sequences_count(self) -> int:
        return self.sequence_count() - self.get_ok_sequence_count()
