package nl.ou.testar.reporter.model;

import lombok.Builder;
import lombok.Data;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
public class Iteration extends RepresentationModel<Iteration> {
    private Integer id;
    private String info;
    private Double severity;

    private Integer reportId;

    // One of next values should be defined
    private List<Integer> actionIds;
    private List<Action> actions;

    // One of next values should be defined
    private Integer lastExecutedActionId;
    private Action lastExecutedAction;

    // One of next values should be defined
    private Integer lastStateId;
    private SequenceItem lastState;
}
