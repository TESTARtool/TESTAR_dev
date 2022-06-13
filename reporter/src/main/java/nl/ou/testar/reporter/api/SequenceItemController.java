package nl.ou.testar.reporter.api;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.model.Action;
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
            @RequestParam(required = false) Collection<Long> ids,
            @RequestParam(required = false) Collection<String> abstractIds,
            @RequestParam(required = false) Collection<String> abstractRIds,
            @RequestParam(required = false) Collection<String> abstractRTIds,
            @RequestParam(required = false) Collection<String> abstractRTPIds,
            @RequestParam(required = false) Collection<Long> actionIds,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(defaultValue = "false") Boolean expandActions,
            PagedResourcesAssembler<SequenceItemEntity> assembler
    ) {
        return new ResponseEntity<>(service.getAllSequenceItems(ids, abstractIds, abstractRIds, abstractRTIds,
                abstractRTPIds, actionIds,
                pageNo, pageSize, expandActions, assembler), HttpStatus.OK);
    }
}
