package nl.ou.testar.reporter.model;

import lombok.Builder;
import lombok.Data;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
public class Iteration extends RepresentationModel<Iteration> {
    private long id;
    private String info;
    private int severity;

    private long reportId;

    // One of next values should be defined
    private List<Long> actionIds;
    private List<Action> actions;

    // One of next values should be defined
    private Long lastExecutedActionId;
    private Action lastExecutedAction;

    // One of next values should be defined
    private Long lastStateId;
    private SequenceItem lastState;
}
