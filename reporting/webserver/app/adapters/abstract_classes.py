from abc import ABC, abstractclassmethod, abstractmethod
from typing import List
from enum import Enum

class VerdictLevel(Enum):
    OK = 0
    SUSPICIOUS_TITLE = 1  

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
    def get_start(self) -> str:
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

    @abstractmethod
    def get_url(self) -> str:
        pass

    @abstractmethod
    def get_actions_per_sequence(self) -> int:
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

        
