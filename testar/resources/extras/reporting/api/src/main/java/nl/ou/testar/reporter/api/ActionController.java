package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.Action;
import nl.ou.testar.reporter.model.PostEntityResponse;
import nl.ou.testar.reporter.service.ActionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/actions")
public class ActionController {

    @Autowired
    ActionService service;

    @Operation(summary = "Create new action")
    @PostMapping
    public ResponseEntity<PostEntityResponse> createAction(
            @RequestParam(required = true) @Parameter(description = "Action name") @NotEmpty String name,
            @RequestParam(required = false) @Parameter(description = "Iteration ID") Integer iterationId,
            @RequestParam(required = false) @Parameter(description = "Action description") String description,
            @RequestParam(required = false) @Parameter(description = "Status") String status,
            @RequestParam(required = false) @Parameter(description = "Screenshot") String screenshot,
            @RequestParam(required = false) @Parameter(description = "Start time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @Parameter(description = "Action selected") Boolean selected,
            @RequestParam(required = false) @Parameter(description = "Action visited") Boolean visited,
            @RequestParam(required = false) @Parameter(description = "State ID") Integer stateId,
            @RequestParam(required = false) @Parameter(description = "Target state ID") Integer targetStateId,
            @RequestParam(required = false) @Parameter(description = "Widget path") String widgetPath) {

        PostEntityResponse response = null;
        Timestamp startTimestamp = (startTime == null ? null : Timestamp.valueOf(startTime));
        try {
            response = service.createAction(name, iterationId, description, status, screenshot, startTimestamp,
                    selected, visited, stateId, targetStateId, widgetPath);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update action")
    @PutMapping
    public ResponseEntity<PostEntityResponse> updateAction(
            @RequestParam(required = true) @Parameter(description = "Action ID") Integer actionId,
            @RequestParam(required = false) @Parameter(description = "Iteration ID") Integer iterationId,
            @RequestParam(required = false) @Parameter(description = "Action description") String description,
            @RequestParam(required = false) @Parameter(description = "Status") String status,
            @RequestParam(required = false) @Parameter(description = "Screenshot") String screenshot,
            @RequestParam(required = false) @Parameter(description = "Start time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @Parameter(description = "Action selected") Boolean selected,
            @RequestParam(required = false) @Parameter(description = "Action visited") Boolean visited,
            @RequestParam(required = false) @Parameter(description = "State ID") Integer stateId,
            @RequestParam(required = false) @Parameter(description = "Target state ID") Integer targetStateId,
            @RequestParam(required = false) @Parameter(description = "Widget path") String widgetPath) {
        Timestamp startTimestamp = (startTime == null ? null : Timestamp.valueOf(startTime));
        try {
            service.updateAction(actionId, widgetPath, iterationId, description, status, screenshot, startTimestamp, selected,
                    visited, stateId, targetStateId, widgetPath);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Delete action")
    @DeleteMapping
    public ResponseEntity<PostEntityResponse> deleteAction(
            @RequestParam(required = true) @Parameter(description = "Action ID") @NotEmpty Integer id) {
        try {
            service.deleteAction(id);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest().body(PostEntityResponse.builder().message(e.getMessage()).build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Get all actions")
    @GetMapping
    public ResponseEntity<PagedModel<Action>> getAllActions(
            @RequestParam(required = false) @Parameter(description = "Filter by one or more action IDs") Collection<Integer> ids,
            @RequestParam(required = false) @Parameter(description = "Filter by containing iterations (sequences)") Collection<Integer> iterationIds,
            @RequestParam(required = false) @Parameter(description = "Filter by related states (sequence items)") Collection<Integer> stateIds,
            @RequestParam(required = false) @Parameter(description = "Filter by exact widget path") String widget,
            @RequestParam(required = false) @Parameter(description = "Filter by widget path substring (case insensitive)") String widgetLike,
            @RequestParam(required = false) @Parameter(description = "Filter by exact name") String name,
            @RequestParam(required = false) @Parameter(description = "Filter by name substring (case insensitive)") String nameLike,
            @RequestParam(required = false) @Parameter(description = "Filter by exact description") String description,
            @RequestParam(required = false) @Parameter(description = "Filter by description substring (case insensitive)") String descriptionLike,
            @RequestParam(required = false) @Parameter(description = "Filter by statuses") Collection<String> statuses,
            @RequestParam(required = false) @Parameter(description = "Filter selected/unselected") Boolean selected,
            @RequestParam(required = false) @Parameter(description = "Filter visited/unvisited") Boolean visited,
            @RequestParam(required = false) @Parameter(description = "Filter by exact start time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAt,
            @RequestParam(required = false) @Parameter(description = "Filter started at the moment or before") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtOrBefore,
            @RequestParam(required = false) @Parameter(description = "Filter started before the moment") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedBefore,
            @RequestParam(required = false) @Parameter(description = "Filter started at the moment or after") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAtOrAfter,
            @RequestParam(required = false) @Parameter(description = "Filter started after the moment") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startedAfter,
            @RequestParam(defaultValue = "0") @Parameter(description = "Response page index") Integer pageNo,
            @RequestParam(defaultValue = "50") @Parameter(description = "Response page size") Integer pageSize,
            PagedResourcesAssembler<ActionEntity> assembler) {
        return new ResponseEntity<>(service.getAllActions(ids, iterationIds, stateIds, widget, widgetLike, name,
                nameLike, description, descriptionLike, statuses, selected, visited, startedAt, startedAtOrBefore,
                startedBefore, startedAtOrAfter, startedAfter, pageNo, pageSize, assembler), HttpStatus.OK);
    }
}
