package nl.ou.testar.reporter.service;

import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.model.Iteration;
import nl.ou.testar.reporter.model.assembler.AssemblingFlags;
import nl.ou.testar.reporter.model.assembler.IterationAssembler;
import nl.ou.testar.reporter.repo.IterationRepo;
import nl.ou.testar.reporter.specs.BasicSpecs;
import nl.ou.testar.reporter.specs.IterationSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class IterationService {

    private IterationRepo iterationRepo;
    private IterationAssembler resourceAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

    @Autowired
    public IterationService(IterationRepo iterationRepo, IterationAssembler resourceAssembler) {
        this.iterationRepo = iterationRepo;
        this.resourceAssembler = resourceAssembler;
    }

    public PagedModel<Iteration> getAllIterations(
            Collection<Long> ids,
            Collection<Long> reportIds,
            String info,
            String infoLike,
            Integer severity,
            Integer severityGreaterThan,
            Integer severityGreaterThanOrEqual,
            Integer severityLessThan,
            Integer severityLessThanOrEqual,
            Collection<Long> lastExecutedActionIds,
            Collection<Long> lastStateIds,
            Collection<Long> actionIds,
            Integer pageNo, Integer pageSize,
            boolean expandActions, boolean expandLastExecutedAction, boolean expandLastState,
            final PagedResourcesAssembler<IterationEntity> pagedResourcesAssembler
    ) {
        assemblingFlags.setExpandActions(expandActions);
        assemblingFlags.setExpandLastExecutedAction(expandLastExecutedAction);
        assemblingFlags.setExpandLastState(expandLastState);

        Specification<IterationEntity> specification = null;
        if (ids != null) {
            specification = BasicSpecs.byIds(ids);
        }
        if (reportIds != null) {
            final Specification<IterationEntity> spec = IterationSpecs.byReports(reportIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (info != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withInfo(info);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (infoLike != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withInfoLike(infoLike, false);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (severity != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withSeverity(severity);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (severityGreaterThan != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withSeverityGreaterThan(severityGreaterThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (severityGreaterThanOrEqual != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withSeverityGreaterThanOrEqual(severityGreaterThanOrEqual);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (severityLessThan != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withSeverityLessThan(severityLessThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (severityLessThanOrEqual != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withSeverityLessThanOrEqual(severityLessThanOrEqual);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (lastExecutedActionIds != null) {
            final Specification<IterationEntity> spec = IterationSpecs.byLastExecutedActions(lastExecutedActionIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (lastStateIds != null) {
            final Specification<IterationEntity> spec = IterationSpecs.byLastStates(lastStateIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionIds != null) {
            final Specification<IterationEntity> spec = IterationSpecs.containingActions(actionIds);
            specification = (specification == null ? spec : specification.and(spec));
        }

        PageRequest paging = PageRequest.of(pageNo, pageSize);
        Page<IterationEntity> pageSource = iterationRepo.findAll(specification, paging);
        return pagedResourcesAssembler.toModel(pageSource, resourceAssembler);
    }
}
