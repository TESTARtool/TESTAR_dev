package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.Iteration;
import nl.ou.testar.reporter.model.PostEntityResponse;
import nl.ou.testar.reporter.service.IterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/iterations")
public class IterationController {

    @Autowired
    IterationService service;

    @Operation(summary = "Create new iteration")
    @PostMapping
    public ResponseEntity<PostEntityResponse> createIteration(
            @RequestParam(required = false) @Parameter(description = "Report ID") Integer reportId,
            @RequestParam(required = false) @Parameter(description = "Iteration info") String info,
            @RequestParam(required = false) @Parameter(description = "Severity") Double severity,
            @RequestParam(required = false) @Parameter(description = "Last executed action ID") Integer lastExecutedActionId,
            @RequestParam(required = false) @Parameter(description = "Last state") Integer lastStateId) {
        PostEntityResponse response = null;
        try {
            response = service.createIteration(reportId, info, severity, lastExecutedActionId, lastStateId);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update iteration")
    @PutMapping
    public ResponseEntity<PostEntityResponse> updateIteration(
            @RequestParam(required = true) @Parameter(description = "Iteration ID") Integer iterationId,
            @RequestParam(required = false) @Parameter(description = "Report ID") Integer reportId,
            @RequestParam(required = false) @Parameter(description = "Iteration info") String info,
            @RequestParam(required = false) @Parameter(description = "Severity") Double severity,
            @RequestParam(required = false) @Parameter(description = "Last executed action ID") Integer lastExecutedActionId,
            @RequestParam(required = false) @Parameter(description = "Last state") Integer lastStateId) {
        try {
            service.updateIteration(iterationId, reportId, info, severity, lastExecutedActionId, lastStateId);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Delete iteration")
    @DeleteMapping
    public ResponseEntity<PostEntityResponse> deleteIteration(
            @RequestParam(required = true) @Parameter(description = "Iteration ID") Integer iterationId) {
        try {
            service.deleteIteration(iterationId);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Get all iterations (sequences)")
    @GetMapping
    public ResponseEntity<PagedModel<Iteration>> getAllActions(
            @RequestParam(required = false) @Parameter(description = "Filter by one or more iteration IDs") Collection<Integer> ids,
            @RequestParam(required = false) @Parameter(description = "Only IDs greater than") Integer idGreaterThan,
            @RequestParam(required = false) @Parameter(description = "Only IDs less than") Integer idLessThan,
            @RequestParam(required = false) @Parameter(description = "Filter by containing reports") Collection<Integer> reportIds,
            @RequestParam(required = false) @Parameter(description = "Filter by exact info") String info,
            @RequestParam(required = false) @Parameter(description = "Filter by info substring (case insensitive)") String infoLike,
            @RequestParam(required = false) @Parameter(description = "Filter by exact severity") Integer severity,
            @RequestParam(required = false) @Parameter(description = "Filter by severity greater than a value") Integer severityGreaterThan,
            @RequestParam(required = false) @Parameter(description = "Filter by severity greater than or equal to a value") Integer severityGreaterThanOrEqual,
            @RequestParam(required = false) @Parameter(description = "Filter by severity less than a value") Integer severityLessThan,
            @RequestParam(required = false) @Parameter(description = "Filter by severity less than or equal to a value") Integer severityLessThanOrEqual,
            @RequestParam(required = false) @Parameter(description = "Filter by last executed actions") Collection<Integer> lastExecutedActionIds,
            @RequestParam(required = false) @Parameter(description = "Filter by last states (sequence items)") Collection<Integer> lastStateIds,
            @RequestParam(required = false) @Parameter(description = "Filter by related actions") Collection<Integer> actionIds,
            @RequestParam(defaultValue = "0") @Parameter(description = "Response page index") Integer pageNo,
            @RequestParam(defaultValue = "50") @Parameter(description = "Response page size") Integer pageSize,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show related actions in detail (otherwise just IDs)") Boolean expandActions,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show last executed action in detail (otherwise just ID)") Boolean expandLastExecutedAction,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show last state (sequence item) in detail (otherwise just ID)") Boolean expandLastState,
            PagedResourcesAssembler<IterationEntity> assembler) {
        return new ResponseEntity<>(service.getAllIterations(ids, idGreaterThan, idLessThan, reportIds, info, infoLike, severity,
                severityGreaterThan, severityGreaterThanOrEqual, severityLessThan, severityLessThanOrEqual,
                lastExecutedActionIds, lastStateIds, actionIds,
                pageNo, pageSize, expandActions, expandLastExecutedAction, expandLastState, assembler), HttpStatus.OK);
    }
}
