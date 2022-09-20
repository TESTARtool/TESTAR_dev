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
                .startTime(entity.getStartTime().toLocalDateTime())
                .widgetPath(entity.getWidgetPath())
                .iterationId(entity.getIteration().getId())
                .sequenceItemId(entity.getSequenceItem().getId())
                .build();
    }
}
