package nl.ou.testar.reporter.api;

import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.entitiy.ReportEntity;
import nl.ou.testar.reporter.model.Report;
import nl.ou.testar.reporter.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    ReportService service;

    @GetMapping
    public ResponseEntity<PagedModel<Report>> getAllReports(
            @RequestParam(required = false) Collection<Long> ids,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String tagLike,
            @RequestParam(required = false) LocalDateTime reportedAt,
            @RequestParam(required = false) LocalDateTime reportedAtOrBefore,
            @RequestParam(required = false) LocalDateTime reportedBefore,
            @RequestParam(required = false) LocalDateTime reportedAtOrAfter,
            @RequestParam(required = false) LocalDateTime reportedAfter,
            @RequestParam(required = false) Integer actionsPerSequence,
            @RequestParam(required = false) Integer actionsPerSequenceGreaterThan,
            @RequestParam(required = false) Integer actionsPerSequenceGreaterThanOrEqual,
            @RequestParam(required = false) Integer actionsPerSequenceLessThan,
            @RequestParam(required = false) Integer actionsPerSequenceLessThanOrEqual,
            @RequestParam(required = false) Integer totalSequences,
            @RequestParam(required = false) Integer totalSequencesGreaterThan,
            @RequestParam(required = false) Integer totalSequencesGreaterThanOrEqual,
            @RequestParam(required = false) Integer totalSequencesLessThan,
            @RequestParam(required = false) Integer totalSequencesLessThanOrEqual,
            @RequestParam(required = false) Collection<Long> iterationIds,
            @RequestParam(required = false) Collection<Long> actionIds,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(defaultValue = "false") Boolean expandIterations,
            @RequestParam(defaultValue = "false") Boolean expandActions,
            @RequestParam(defaultValue = "false") Boolean expandLastExecutedAction,
            @RequestParam(defaultValue = "false") Boolean expandLastState,
            PagedResourcesAssembler<ReportEntity> assembler
    ) {
        return new ResponseEntity<>(service.getAllReports(ids, tag, tagLike, reportedAt, reportedAtOrBefore,
                reportedBefore, reportedAtOrAfter, reportedAfter, actionsPerSequence, actionsPerSequenceGreaterThan,
                actionsPerSequenceGreaterThanOrEqual, actionsPerSequenceLessThan, actionsPerSequenceLessThanOrEqual,
                totalSequences, totalSequencesGreaterThan, totalSequencesGreaterThanOrEqual, totalSequencesLessThan,
                totalSequencesLessThanOrEqual, iterationIds, actionIds,
                pageNo, pageSize, expandIterations, expandActions, expandLastExecutedAction, expandLastState, assembler),
                HttpStatus.OK);
    }
}
