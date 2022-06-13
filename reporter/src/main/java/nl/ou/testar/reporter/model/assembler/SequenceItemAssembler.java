package nl.ou.testar.reporter.model.assembler;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.model.SequenceItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SequenceItemAssembler implements RepresentationModelAssembler<SequenceItemEntity, SequenceItem> {

    @Autowired
    private ActionAssembler actionAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

    @Override
    public SequenceItem toModel(SequenceItemEntity entity) {
        return toModel(entity, true);
    }

    public SequenceItem toModel(SequenceItemEntity entity, boolean includeActions) {
        return SequenceItem.builder()
                .id(entity.getId())
                .concreteId(entity.getConcreteId())
                .abstractId(entity.getAbstractId())
                .abstractRId(entity.getAbstractRId())
                .abstractRTId(entity.getAbstractRTId())
                .abstractRTPId(entity.getAbstractRTPId())
                .actionIds(!includeActions || assemblingFlags.isExpandActions() ? null :
                        entity.getActions().stream().map(ActionEntity::getId).collect(Collectors.toList()))
                .actions(!includeActions || !assemblingFlags.isExpandActions() ? null :
                        entity.getActions().stream().map(actionEntity -> actionAssembler.toModel(actionEntity))
                                .collect(Collectors.toList()))
                .build();
    }
}
