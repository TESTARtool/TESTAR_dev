package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.model.SequenceItem;
import nl.ou.testar.reporter.service.SequenceItemService;
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
@RequestMapping("/states")
public class SequenceItemController {

    @Autowired
    SequenceItemService service;

    @GetMapping
    public ResponseEntity<PagedModel<SequenceItem>> getAllSequenceItems(
            @RequestParam(required = false)
            @Parameter(description = "Filter by one or more state (sequence item) IDs")
            Collection<Long> ids,
            @RequestParam(required = false)
            @Parameter(description = "Filter by abstract IDs")
            Collection<String> abstractIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by abstract R IDs")
            Collection<String> abstractRIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by abstract RT IDs")
            Collection<String> abstractRTIds,
            @RequestParam(required = false)
            @Parameter(description = "Filter by abstract RTP IDs")
            Collection<String> abstractRTPIds,
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
            PagedResourcesAssembler<SequenceItemEntity> assembler
    ) {
        return new ResponseEntity<>(service.getAllSequenceItems(ids, abstractIds, abstractRIds, abstractRTIds,
                abstractRTPIds, actionIds,
                pageNo, pageSize, expandActions, assembler), HttpStatus.OK);
    }
}
