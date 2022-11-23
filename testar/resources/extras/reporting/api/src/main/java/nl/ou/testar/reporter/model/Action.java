package nl.ou.testar.reporter.model;

import lombok.Builder;
import lombok.Data;
import nl.ou.testar.reporter.entitiy.ActionEntity;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
public class Action extends RepresentationModel<Action> {
    private Integer id;

    private String name;
    private String description;
    private String status;
    private String screenshot;
    private Boolean selected;
    private Boolean visited;
    private LocalDateTime startTime;
    private String widgetPath;

    private Integer iterationId;
    private Integer sequenceItemId;
	private Integer targetSequenceItemId;
}
