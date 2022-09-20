package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.model.Action;
import nl.ou.testar.reporter.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/actions")
public class ActionController {

    @Autowired
    ActionService service;

    @Operation(summary = "Get all actions")
    @GetMapping
    public ResponseEntity<PagedModel<Action>> getAllActions(
            @RequestParam(required = false)
            @Parameter(description = "Filter by one or more action IDs")
            Collection<Long> ids,
            @RequestParam(required = false)
            @Parameter(description = "Filter by containing iterations (sequences)")
            Collection<Long> iterationIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by related states (sequence items)")
            Collection<Long> stateIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by exact widget path")
            String widget,
            @RequestParam(required = false)
            @Parameter(description = "Filter by widget path substring (case insensitive)")
            String widgetLike,
            @RequestParam(required = false)
            @Parameter(description = "Filter by exact name")
            String name,
            @RequestParam(required = false)
            @Parameter(description = "Filter by name substring (case insensitive)")
            String nameLike,
            @RequestParam(required = false)
            @Parameter(description = "Filter by exact description")
            String description,
            @RequestParam(required = false)
            @Parameter(description = "Filter by description substring (case insensitive)")
            String descriptionLike,
            @RequestParam(required = false)
            @Parameter(description = "Filter by statuses")
            Collection<String> statuses,
            @RequestParam(required = false)
            @Parameter(description = "Filter selected/unselected")
            Boolean selected,
            @RequestParam(required = false)
            @Parameter(description = "Filter by exact start time")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAt,
            @RequestParam(required = false)
            @Parameter(description = "Filter started at the moment or before")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAtOrBefore,
            @RequestParam(required = false)
            @Parameter(description = "Filter started before the moment")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedBefore,
            @RequestParam(required = false)
            @Parameter(description = "Filter started at the moment or after")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAtOrAfter,
            @RequestParam(required = false)
            @Parameter(description = "Filter started after the moment")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAfter,
            @RequestParam(defaultValue = "0")
            @Parameter(description = "Response page index")
            Integer pageNo,
            @RequestParam(defaultValue = "50")
            @Parameter(description = "Response page size")
            Integer pageSize,
            PagedResourcesAssembler<ActionEntity> assembler
    ) {
        return new ResponseEntity<>(service.getAllActions(ids, iterationIds, stateIds, widget, widgetLike, name,
                nameLike, description, descriptionLike, statuses, selected, startedAt, startedAtOrBefore, startedBefore,
                startedAtOrAfter, startedAfter, pageNo, pageSize, assembler), HttpStatus.OK);
    }
}
