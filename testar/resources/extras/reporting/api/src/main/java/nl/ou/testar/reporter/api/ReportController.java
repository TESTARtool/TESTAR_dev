package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.entitiy.ReportEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.PostEntityResponse;
import nl.ou.testar.reporter.model.Report;
import nl.ou.testar.reporter.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    ReportService service;

    @Operation(summary = "Create new report")
    @PostMapping
    public ResponseEntity<PostEntityResponse> postReport(
            @RequestParam(required = false) @Parameter(description = "Report tag") String tag,
            @RequestParam(required = false) @Parameter(description = "Report date") @DateTimeFormat(iso = ISO.DATE_TIME) Date time,
            @RequestParam(required = false) @Parameter(description = "Actions per sequence") Integer actionsPerSequence,
            @RequestParam(required = false) @Parameter(description = "Total sequences") Integer totalSequences,
            @RequestParam(required = false) @Parameter(description = "Reporting URL (optional)") String url) {

        final Timestamp timestamp = new Timestamp(time == null ? System.currentTimeMillis() : time.getTime());
        PostEntityResponse response = null;
        try {
            response = service.createReport(tag, timestamp, actionsPerSequence, totalSequences, url);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update report")
    @PutMapping
    public ResponseEntity<PostEntityResponse> putReport(
            @RequestParam(required = true) @Parameter(description = "Report ID") Integer reportId,
            @RequestParam(required = false) @Parameter(description = "Report date") @DateTimeFormat(iso = ISO.DATE_TIME) Date time,
            @RequestParam(required = false) @Parameter(description = "Actions per sequence") Integer actionsPerSequence,
            @RequestParam(required = false) @Parameter(description = "Total sequences") Integer totalSequences,
            @RequestParam(required = false) @Parameter(description = "Reporting URL (optional)") String url) {
        final Timestamp timestamp = (time == null ? null : new Timestamp(time.getTime()));
        try {
            service.updateReport(reportId, url, timestamp, actionsPerSequence, totalSequences, url);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Delete report")
    @DeleteMapping
    public ResponseEntity<PostEntityResponse> deleteReport(
            @RequestParam(required = true) @Parameter(description = "Report ID") Integer reportId) {
        try {
            service.deleteReport(reportId);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest()
                    .body(PostEntityResponse.builder()
                            .message(e.getMessage()).build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Get all reports")
    @GetMapping
    public ResponseEntity<PagedModel<Report>> getAllReports(
            @RequestParam(required = false) @Parameter(description = "Filter by one or more report IDs") Collection<Integer> ids,
            @RequestParam(required = false) @Parameter(description = "Filter by exact tag") String tag,
            @RequestParam(required = false) @Parameter(description = "Filter by tag substring (case insensitive)") String tagLike,
            @RequestParam(required = false) @Parameter(description = "Filter by exact report time") LocalDateTime reportedAt,
            @RequestParam(required = false) @Parameter(description = "Filter reported at the moment or before") LocalDateTime reportedAtOrBefore,
            @RequestParam(required = false) @Parameter(description = "Filter reported before the moment") LocalDateTime reportedBefore,
            @RequestParam(required = false) @Parameter(description = "Filter reported at the moment or after") LocalDateTime reportedAtOrAfter,
            @RequestParam(required = false) @Parameter(description = "Filter reported after the moment") LocalDateTime reportedAfter,
            @RequestParam(required = false) @Parameter(description = "Filter by exact number of actions per service") Integer actionsPerSequence,
            @RequestParam(required = false) @Parameter(description = "Filter by number of actions per service more than a value") Integer actionsPerSequenceGreaterThan,
            @RequestParam(required = false) @Parameter(description = "Filter by number of actions per service more than or equal to a value") Integer actionsPerSequenceGreaterThanOrEqual,
            @RequestParam(required = false) @Parameter(description = "Filter by number of actions per service less than a value") Integer actionsPerSequenceLessThan,
            @RequestParam(required = false) @Parameter(description = "Filter by number of actions per service less than or equal to a value") Integer actionsPerSequenceLessThanOrEqual,
            @RequestParam(required = false) @Parameter(description = "Filter by exact total number of iterations (sequences)") Integer totalSequences,
            @RequestParam(required = false) @Parameter(description = "Filter by total number of iterations (sequences) more than a value") Integer totalSequencesGreaterThan,
            @RequestParam(required = false) @Parameter(description = "Filter by total number of iterations (sequences) more than or equal to a value") Integer totalSequencesGreaterThanOrEqual,
            @RequestParam(required = false) @Parameter(description = "Filter by total number of iterations (sequences) less than a value") Integer totalSequencesLessThan,
            @RequestParam(required = false) @Parameter(description = "Filter by total number of iterations (sequences) less than or equal to a value") Integer totalSequencesLessThanOrEqual,
            @RequestParam(required = false) @Parameter(description = "Filter by related iterations") Collection<Integer> iterationIds,
            @RequestParam(required = false) @Parameter(description = "Filter by related actions") Collection<Integer> actionIds,
            @RequestParam(defaultValue = "0") @Parameter(description = "Response page index") Integer pageNo,
            @RequestParam(defaultValue = "50") @Parameter(description = "Response page size") Integer pageSize,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show related iterations in detail (otherwise just IDs)") Boolean expandIterations,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show related actions in detail when iterations expanded (otherwise just IDs)") Boolean expandActions,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show last executed action in detail when iterations expanded (otherwise just ID)") Boolean expandLastExecutedAction,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show last state (sequence item) in detail when iterations expanded (otherwise just ID)") Boolean expandLastState,
            PagedResourcesAssembler<ReportEntity> assembler) {
        return new ResponseEntity<>(service.getAllReports(ids, tag, tagLike, reportedAt, reportedAtOrBefore,
                reportedBefore, reportedAtOrAfter, reportedAfter, actionsPerSequence, actionsPerSequenceGreaterThan,
                actionsPerSequenceGreaterThanOrEqual, actionsPerSequenceLessThan, actionsPerSequenceLessThanOrEqual,
                totalSequences, totalSequencesGreaterThan, totalSequencesGreaterThanOrEqual, totalSequencesLessThan,
                totalSequencesLessThanOrEqual, iterationIds, actionIds,
                pageNo, pageSize, expandIterations, expandActions, expandLastExecutedAction, expandLastState,
                assembler),
                HttpStatus.OK);
    }
}
