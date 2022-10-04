package nl.ou.testar.reporter.model.assembler;

import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.entitiy.ReportEntity;
import nl.ou.testar.reporter.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ReportAssembler implements RepresentationModelAssembler<ReportEntity, Report> {
    @Autowired
    private IterationAssembler iterationAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

    @Override
    public Report toModel(ReportEntity entity) {
        return Report.builder()
                .id(entity.getId())
                .time(entity.getTime().toLocalDateTime())
                .url(entity.getUrl())
                .actionsPerSequence(entity.getActionsPerSequence())
                .totalSequences(entity.getTotalSequences())
                .iterationIds(assemblingFlags.isExpandIterations() ? null :
                        entity.getIterations().stream().map(IterationEntity::getId).collect(Collectors.toList()))
                .iterations(assemblingFlags.isExpandIterations() ?
                        entity.getIterations().stream().map(iterationEntity ->
                                iterationAssembler.toModel(iterationEntity)).collect(Collectors.toList()) :
                        null)
                .build();
    }
}
