package nl.ou.testar.reporter.model;

import lombok.Builder;
import lombok.Data;
import nl.ou.testar.reporter.entitiy.ActionEntity;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Builder
public class Action extends RepresentationModel<Action> {
    private Long id;

    private String name;
    private String description;
    private String status;
    private String screenshot;
    private boolean selected;
    private LocalDateTime startTime;
    private String widgetPath;

    private Long iterationId;
    private Long sequenceItemId;
}
