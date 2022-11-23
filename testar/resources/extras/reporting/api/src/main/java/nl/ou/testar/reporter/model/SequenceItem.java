package nl.ou.testar.reporter.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@Builder
public class SequenceItem extends RepresentationModel<SequenceItem> {
    private Integer id;

    private String concreteId;
    private String abstractId;
    private String abstractRId;
    private String abstractRTId;
    private String abstractRTPId;

    // One of next values should be defined
    private List<Integer> actionIds;
    private List<Action> actions;
}
