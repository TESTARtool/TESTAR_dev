package nl.ou.testar.reporter.model.assembler;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.model.Iteration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class IterationAssembler implements RepresentationModelAssembler<IterationEntity, Iteration> {

    @Autowired
    private ActionAssembler actionAssembler;

    @Autowired
    private SequenceItemAssembler sequenceItemAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

    @Override
    public Iteration toModel(IterationEntity entity) {
        return Iteration.builder()
                .id(entity.getId())
                .info(entity.getInfo())
                .severity(entity.getSeverity())
                .actionIds(assemblingFlags.isExpandActions() ? null :
                        entity.getActions().stream().map(ActionEntity::getId).collect(Collectors.toList()))
                .actions(assemblingFlags.isExpandActions() ?
                        entity.getActions().stream().map(actionEntity -> actionAssembler.toModel(actionEntity))
                                .collect(Collectors.toList()) :
                        null)
                .lastExecutedActionId(assemblingFlags.isExpandLastExecutedAction() ? null :
                        entity.getLastExecutedAction().getId())
                .lastExecutedAction(assemblingFlags.isExpandLastExecutedAction() ?
                        actionAssembler.toModel(entity.getLastExecutedAction()) : null)
                .lastStateId(assemblingFlags.isExpandLastState() ? null : entity.getLastState().getId())
                .lastState(assemblingFlags.isExpandLastState() ? sequenceItemAssembler.toModel(entity.getLastState(), false) :
                        null)
                .build();
    }
}
