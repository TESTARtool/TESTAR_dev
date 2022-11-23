package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.PostEntityResponse;
import nl.ou.testar.reporter.model.SequenceItem;
import nl.ou.testar.reporter.service.SequenceItemService;
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

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/states")
public class SequenceItemController {

    @Autowired
    SequenceItemService service;

    @Operation(summary = "Create new state (sequence item)")
    @PostMapping
    public ResponseEntity<PostEntityResponse> postSequenceItem(
            @RequestParam(required = true) @Parameter(description = "Concrete ID") @NotEmpty String concreteId,
            @RequestParam(required = false) @Parameter(description = "Abstract ID") String abstractId,
            @RequestParam(required = false) @Parameter(description = "Abstract R ID") String abstractRId,
            @RequestParam(required = false) @Parameter(description = "Abstract RT ID") String abstractRTId,
            @RequestParam(required = false) @Parameter(description = "Abstract RTP ID") String abstractRTPId) {
        PostEntityResponse response = null;
        try {
            response = service.createSequenceItem(concreteId, abstractId, abstractRId, abstractRTId, abstractRTPId);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest()
                    .body(PostEntityResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update state (sequence item)")
    @PutMapping
    public ResponseEntity<PostEntityResponse> putSequenceItem(
            @RequestParam(required = true) @Parameter(description = "State (sequence item) ID") Integer id,
            @RequestParam(required = false) @Parameter(description = "Concrete ID") String concreteId,
            @RequestParam(required = false) @Parameter(description = "Abstract ID") String abstractId,
            @RequestParam(required = false) @Parameter(description = "Abstract R ID") String abstractRId,
            @RequestParam(required = false) @Parameter(description = "Abstract RT ID") String abstractRTId,
            @RequestParam(required = false) @Parameter(description = "Abstract RTP ID") String abstractRTPId) {
        try {
            service.updateSequenceItem(id, concreteId, abstractId, abstractRId, abstractRTId, abstractRTPId);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest()
                    .body(PostEntityResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Delete state (sequence item)")
    @DeleteMapping
    public ResponseEntity<PostEntityResponse> deleteSequenceItem(
            @RequestParam(required = true) @Parameter(description = "State (sequence item) ID") Integer id) {
        try {
            service.deleteSequenceItem(id);
        } catch (SaveEntityException e) {
            return ResponseEntity.badRequest()
                    .body(PostEntityResponse.builder()
                            .message(e.getMessage())
                            .build());
        }
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "Get all states (sequence items)")
    @GetMapping
    public ResponseEntity<PagedModel<SequenceItem>> getAllSequenceItems(
            @RequestParam(required = false) @Parameter(description = "Filter by one or more state (sequence item) IDs") Collection<Integer> ids,
            @RequestParam(required = false) @Parameter(description = "Filter by concrete IDs") Collection<String> concreteIds,
            @RequestParam(required = false) @Parameter(description = "Filter by abstract IDs") Collection<String> abstractIds,
            @RequestParam(required = false) @Parameter(description = "Filter by abstract R IDs") Collection<String> abstractRIds,
            @RequestParam(required = false) @Parameter(description = "Filter by abstract RT IDs") Collection<String> abstractRTIds,
            @RequestParam(required = false) @Parameter(description = "Filter by abstract RTP IDs") Collection<String> abstractRTPIds,
            @RequestParam(required = false) @Parameter(description = "Filter by related actions") Collection<Integer> actionIds,
            @RequestParam(defaultValue = "0") @Parameter(description = "Response page index") Integer pageNo,
            @RequestParam(defaultValue = "50") @Parameter(description = "Response page size") Integer pageSize,
            @RequestParam(defaultValue = "false") @Parameter(description = "Show related actions in detail (otherwise just IDs)") Boolean expandActions,
            PagedResourcesAssembler<SequenceItemEntity> assembler) {
        return new ResponseEntity<>(service.getAllSequenceItems(ids, concreteIds, abstractIds, abstractRIds, abstractRTIds,
                abstractRTPIds, actionIds,
                pageNo, pageSize, expandActions, assembler), HttpStatus.OK);
    }
}
