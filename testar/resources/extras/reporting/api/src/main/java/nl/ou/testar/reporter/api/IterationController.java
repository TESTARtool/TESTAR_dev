package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.model.Iteration;
import nl.ou.testar.reporter.service.IterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/iterations")
public class IterationController {

    @Autowired
    IterationService service;

    @Operation(summary = "Get all iterations (sequences)")
    @GetMapping
    public ResponseEntity<PagedModel<Iteration>> getAllActions(
            @RequestParam(required = false)
            @Parameter(description = "Filter by one or more iteration IDs")
            Collection<Long> ids,
            @RequestParam(required = false)
            @Parameter(description = "Filter by containing reports")
            Collection<Long> reportIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by exact info")
            String info,
            @RequestParam(required = false)
            @Parameter(description = "Filter by info substring (case insensitive)")
            String infoLike,
            @RequestParam(required = false)
            @Parameter(description = "Filter by exact severity")
            Integer severity,
            @RequestParam(required = false)
            @Parameter(description = "Filter by severity greater than a value")
            Integer severityGreaterThan,
            @RequestParam(required = false)
            @Parameter(description = "Filter by severity greater than or equal to a value")
            Integer severityGreaterThanOrEqual,
            @RequestParam(required = false)
            @Parameter(description = "Filter by severity less than a value")
            Integer severityLessThan,
            @RequestParam(required = false)
            @Parameter(description = "Filter by severity less than or equal to a value")
            Integer severityLessThanOrEqual,
            @RequestParam(required = false)
            @Parameter(description = "Filter by last executed actions")
            Collection<Long> lastExecutedActionIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by last states (sequence items)")
            Collection<Long> lastStateIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by related actions")
            Collection<Long> actionIds,
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Response page index")
            Integer pageNo,
            @RequestParam(defaultValue = "50")
            @Parameter(description = "Response page size")
            Integer pageSize,
            @RequestParam(defaultValue = "false")
            @Parameter(description = "Show related actions in detail (otherwise just IDs)")
            Boolean expandActions,
            @RequestParam(defaultValue = "false")
            @Parameter(description = "Show last executed action in detail (otherwise just ID)")
            Boolean expandLastExecutedAction,
            @RequestParam(defaultValue = "false")
            @Parameter(description = "Show last state (sequence item) in detail (otherwise just ID)")
            Boolean expandLastState,
            PagedResourcesAssembler<IterationEntity> assembler
    ) {
        return new ResponseEntity<>(service.getAllIterations(ids, reportIds, info, infoLike, severity,
                severityGreaterThan, severityGreaterThanOrEqual, severityLessThan, severityLessThanOrEqual,
                lastExecutedActionIds, lastStateIds, actionIds,
                pageNo, pageSize, expandActions, expandLastExecutedAction, expandLastState, assembler), HttpStatus.OK);
    }
}
