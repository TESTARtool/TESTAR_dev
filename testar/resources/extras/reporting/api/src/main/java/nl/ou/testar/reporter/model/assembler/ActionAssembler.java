package nl.ou.testar.reporter.model.assembler;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.model.Action;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ActionAssembler implements RepresentationModelAssembler<ActionEntity, Action> {

    @Override
    public Action toModel(ActionEntity entity) {
        return Action.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .screenshot(entity.getScreenshot())
                .selected(entity.isSelected())
                .visited(entity.isVisited())
			    .startTime(entity.getStartTime() == null ? null : entity.getStartTime().toLocalDateTime())
                .widgetPath(entity.getWidgetPath())
			    .iterationId(entity.getIteration() == null ? null : entity.getIteration().getId())
			    .sequenceItemId(entity.getSequenceItem() == null ? null : entity.getSequenceItem().getId())
			    .targetSequenceItemId(entity.getTargetSequenceItem() == null ? null : entity.getTargetSequenceItem().getId())
                .build();
    }
}
