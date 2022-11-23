package nl.ou.testar.reporter.service;

import nl.ou.testar.reporter.entitiy.ReportEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.PostEntityResponse;
import nl.ou.testar.reporter.model.Report;
import nl.ou.testar.reporter.model.assembler.AssemblingFlags;
import nl.ou.testar.reporter.model.assembler.ReportAssembler;
import nl.ou.testar.reporter.repo.ReportRepo;
import nl.ou.testar.reporter.specs.BasicSpecs;
import nl.ou.testar.reporter.specs.ReportSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Service
public class ReportService {

	@Autowired
	private ReportRepo reportRepo;
	
	@Autowired
    private ReportAssembler resourceAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

//    @Autowired
//    public ReportService(ReportRepo reportRepo, ReportAssembler resourceAssembler) {
//        this.reportRepo = reportRepo;
//        this.resourceAssembler = resourceAssembler;
//    }

    public PostEntityResponse createReport(
            String tag,
            Timestamp time,
            Integer actionsPerSequence,
            Integer totalSequences,
            String url) throws SaveEntityException {
        if (reportRepo.findByTag(tag).size() > 0) {
            throw new SaveEntityException(String.format("A report with tag \"%s\" already exists", tag));
        }
        ReportEntity reportEntity = new ReportEntity();

        Integer reportId = storeReportEntity(reportEntity, tag, time, actionsPerSequence, totalSequences, url).getId();
        return PostEntityResponse.builder().id(reportId).build();
    }

    public void updateReport(
            Integer reportId,
            String tag,
            Timestamp time,
            Integer actionsPerSequence,
            Integer totalSequences,
            String url) throws SaveEntityException {

        Optional<ReportEntity> report = reportRepo.findById(reportId);
        if (report.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find report with ID %d", reportId));
        }
        storeReportEntity(report.get(), tag, time, actionsPerSequence, totalSequences, url);
    }

    public void deleteReport(Integer reportId) throws SaveEntityException {
        Optional<ReportEntity> report = reportRepo.findById(reportId);
        if (report.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find report with ID %d", reportId));
        }
        reportRepo.delete(report.get());
    }

    private ReportEntity storeReportEntity(
            ReportEntity reportEntity,
            String tag,
            Timestamp time,
            Integer actionsPerSequence,
            Integer totalSequences,
            String url) throws SaveEntityException {

        if (tag != null) {
            reportEntity.setTag(tag);
        }
        if (time != null) {
            reportEntity.setTime(Timestamp.from(Instant.now()));
        }
        if (actionsPerSequence != null) {
            reportEntity.setActionsPerSequence(actionsPerSequence);
        }
        if (totalSequences != null) {
            reportEntity.setTotalSequences(totalSequences);
        }
        if (url != null) {
            reportEntity.setUrl(url);
        }

        return reportRepo.save(reportEntity);
    }

    public PagedModel<Report> getAllReports(
            Collection<Integer> ids,
            String tag,
            String tagLike,
            LocalDateTime reportedAt,
            LocalDateTime reportedAtOrBefore,
            LocalDateTime reportedBefore,
            LocalDateTime reportedAtOrAfter,
            LocalDateTime reportedAfter,
            Integer actionsPerSequence,
            Integer actionsPerSequenceGreaterThan,
            Integer actionsPerSequenceGreaterThanOrEqual,
            Integer actionsPerSequenceLessThan,
            Integer actionsPerSequenceLessThanOrEqual,
            Integer totalSequences,
            Integer totalSequencesGreaterThan,
            Integer totalSequencesGreaterThanOrEqual,
            Integer totalSequencesLessThan,
            Integer totalSequencesLessThanOrEqual,
            Collection<Integer> iterationIds,
            Collection<Integer> actionIds,
            Integer pageNo, Integer pageSize,
            boolean expandIterations, boolean expandActions,
            boolean expandLastExecutedAction, boolean expandLastState,
            final PagedResourcesAssembler<ReportEntity> pagedResourcesAssembler
    ) {
        assemblingFlags.setExpandIterations(expandIterations);
        assemblingFlags.setExpandActions(expandActions);
        assemblingFlags.setExpandLastExecutedAction(expandLastExecutedAction);
        assemblingFlags.setExpandLastState(expandLastState);

        Specification<ReportEntity> specification = null;
        if (ids != null) {
            specification = BasicSpecs.byIds(ids);
        }
        if (tag != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withTag(tag);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (tagLike != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withTagLike(tagLike, false);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (reportedAt != null) {
            final Specification<ReportEntity> spec = ReportSpecs.reportedAt(reportedAt);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (reportedAtOrBefore != null) {
            final Specification<ReportEntity> spec = ReportSpecs.reportedAtOrBefore(reportedAtOrBefore);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (reportedBefore != null) {
            final Specification<ReportEntity> spec = ReportSpecs.reportedBefore(reportedBefore);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (reportedAtOrAfter != null) {
            final Specification<ReportEntity> spec = ReportSpecs.reportedAtOrAfter(reportedAtOrAfter);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (reportedAfter != null) {
            final Specification<ReportEntity> spec = ReportSpecs.reportedAfter(reportedAfter);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionsPerSequence != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withActionsPerSequence(actionsPerSequence);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionsPerSequenceGreaterThan != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withActionsPerSequenceGreaterThan(actionsPerSequenceGreaterThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionsPerSequenceGreaterThanOrEqual != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withActionsPerSequenceGreaterThanOrEqual(actionsPerSequenceGreaterThanOrEqual);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionsPerSequenceLessThan != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withActionsPerSequenceLessThan(actionsPerSequenceLessThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionsPerSequenceLessThanOrEqual != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withActionsPerSequenceLessThanOrEqual(actionsPerSequenceLessThanOrEqual);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (totalSequences != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withTotalSequences(totalSequences);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (totalSequencesGreaterThan != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withTotalSequencesGreaterThan(totalSequencesGreaterThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (totalSequencesGreaterThanOrEqual != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withTotalSequencesGreaterThanOrEqual(totalSequencesGreaterThanOrEqual);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (totalSequencesLessThan != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withTotalSequencesLessThan(totalSequencesLessThan);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (totalSequencesLessThanOrEqual != null) {
            final Specification<ReportEntity> spec = ReportSpecs.withTotalSequencesLessThanOrEqual(totalSequencesLessThanOrEqual);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (iterationIds != null) {
            final Specification<ReportEntity> spec = ReportSpecs.containingIterations(iterationIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionIds != null) {
            final Specification<ReportEntity> spec = ReportSpecs.containingActions(actionIds);
            specification = (specification == null ? spec : specification.and(spec));
        }

        PageRequest paging = PageRequest.of(pageNo, pageSize);
        Page<ReportEntity> pageSource = reportRepo.findAll(specification, paging);
        return pagedResourcesAssembler.toModel(pageSource, resourceAssembler);
    }
}
