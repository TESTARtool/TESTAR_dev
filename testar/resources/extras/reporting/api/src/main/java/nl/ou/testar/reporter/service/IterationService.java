package nl.ou.testar.reporter.service;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.entitiy.ReportEntity;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.Iteration;
import nl.ou.testar.reporter.model.PostEntityResponse;
import nl.ou.testar.reporter.model.assembler.AssemblingFlags;
import nl.ou.testar.reporter.model.assembler.IterationAssembler;
import nl.ou.testar.reporter.repo.ActionRepo;
import nl.ou.testar.reporter.repo.IterationRepo;
import nl.ou.testar.reporter.repo.ReportRepo;
import nl.ou.testar.reporter.repo.SequenceItemRepo;
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
import java.util.Optional;

@Service
public class IterationService {

    @Autowired
    private IterationRepo iterationRepo;

    @Autowired
    private ReportRepo reportRepo;

    @Autowired
    private ActionRepo actionRepo;

    @Autowired
    private SequenceItemRepo sequenceItemRepo;

    @Autowired
    private IterationAssembler resourceAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

    public PostEntityResponse createIteration(
            Integer reportId,
            String info,
            Double severity,
            Integer lastExecutedActionId,
            Integer lastStateId) throws SaveEntityException {

        IterationEntity iterationEntity = new IterationEntity();
        Integer storeIterationId = storeIteration(iterationEntity, reportId, info, severity, lastExecutedActionId,
                lastStateId).getId();
        return PostEntityResponse.builder().id(storeIterationId).build();
    }

    public void updateIteration(
            Integer iterationId,
            Integer reportId,
            String info,
            Double severity,
            Integer lastExecutedActionId,
            Integer lastStateId) throws SaveEntityException {

        Optional<IterationEntity> iteration = iterationRepo.findById(iterationId);
        if (iteration.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find iteration with ID %d", iterationId));
        }
        storeIteration(iteration.get(), reportId, info, severity, lastExecutedActionId, lastStateId);
    }

    public void deleteIteration(Integer iterationId) throws SaveEntityException {
        Optional<IterationEntity> iteration = iterationRepo.findById(iterationId);
        if (iteration.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find iteration with ID %d", iterationId));
        }
        iterationRepo.delete(iteration.get());
    }

    private IterationEntity storeIteration(
            IterationEntity iterationEntity,
            Integer reportId,
            String info,
            Double severity,
            Integer lastExecutedActionId,
            Integer lastStateId) throws SaveEntityException {

        if (info != null) {
            iterationEntity.setInfo(info);
        }
        if (severity != null) {

            iterationEntity.setSeverity(severity);
            ;
        }

        if (reportId != null) {
            Optional<ReportEntity> report = reportRepo.findById(reportId);
            if (report.isEmpty()) {
                throw new SaveEntityException(String.format("Cannot find report with ID %d", reportId));
            }
            iterationEntity.setReport(report.get());
        }

        if (lastExecutedActionId != null && lastExecutedActionId != 0) {
            Optional<ActionEntity> lastExecutedAction = actionRepo.findById(lastExecutedActionId);
            if (lastExecutedAction.isEmpty()) {
                throw new SaveEntityException(
                        String.format("Cannot find last executed action with ID %d", lastExecutedActionId));
            }
            iterationEntity.setLastExecutedAction(lastExecutedAction.get());
        }

        if (lastStateId != null && lastStateId != 0) {
            Optional<SequenceItemEntity> lastState = sequenceItemRepo.findById(lastStateId);
            if (lastState.isEmpty()) {
                throw new SaveEntityException(String.format("Cannot find last state with ID %d", lastStateId));
            }
            iterationEntity.setLastState(lastState.get());
        }

        return iterationRepo.save(iterationEntity);
    }

    public PagedModel<Iteration> getAllIterations(
            Collection<Integer> ids,
            Integer idGreaterThan,
            Integer idLessThan,
            Collection<Integer> reportIds,
            String info,
            String infoLike,
            Integer severity,
            Integer severityGreaterThan,
            Integer severityGreaterThanOrEqual,
            Integer severityLessThan,
            Integer severityLessThanOrEqual,
            Collection<Integer> lastExecutedActionIds,
            Collection<Integer> lastStateIds,
            Collection<Integer> actionIds,
            Integer pageNo, Integer pageSize,
            boolean expandActions, boolean expandLastExecutedAction, boolean expandLastState,
            final PagedResourcesAssembler<IterationEntity> pagedResourcesAssembler) {
        assemblingFlags.setExpandActions(expandActions);
        assemblingFlags.setExpandLastExecutedAction(expandLastExecutedAction);
        assemblingFlags.setExpandLastState(expandLastState);

        Specification<IterationEntity> specification = null;
        if (ids != null) {
            specification = BasicSpecs.byIds(ids);
        }
        if (idGreaterThan != null) {
            final Specification<IterationEntity> spec = IterationSpecs.byIdGreaterThan(idGreaterThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (idLessThan != null) {
            final Specification<IterationEntity> spec = IterationSpecs.byIdLessThan(idLessThan);
            specification = (specification == null ? spec : specification.and(spec));
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
            final Specification<IterationEntity> spec = IterationSpecs
                    .withSeverityGreaterThanOrEqual(severityGreaterThanOrEqual);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (severityLessThan != null) {
            final Specification<IterationEntity> spec = IterationSpecs.withSeverityLessThan(severityLessThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (severityLessThanOrEqual != null) {
            final Specification<IterationEntity> spec = IterationSpecs
                    .withSeverityLessThanOrEqual(severityLessThanOrEqual);
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
