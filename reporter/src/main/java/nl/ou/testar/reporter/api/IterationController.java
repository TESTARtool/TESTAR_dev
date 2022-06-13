package nl.ou.testar.reporter.api;

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

    @GetMapping
    public ResponseEntity<PagedModel<Iteration>> getAllActions(
            @RequestParam(required = false) Collection<Long> ids,
            @RequestParam(required = false) Collection<Long> reportIds,
            @RequestParam(required = false) String info,
            @RequestParam(required = false) String infoLike,
            @RequestParam(required = false) Integer severity,
            @RequestParam(required = false) Integer severityGreaterThan,
            @RequestParam(required = false) Integer severityGreaterThanOrEqual,
            @RequestParam(required = false) Integer severityLessThan,
            @RequestParam(required = false) Integer severityLessThanOrEqual,
            @RequestParam(required = false) Collection<Long> lastExecutedActionIds,
            @RequestParam(required = false) Collection<Long> lastStateIds,
            @RequestParam(required = false) Collection<Long> actionIds,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer pageSize,
            @RequestParam(defaultValue = "false") Boolean expandActions,
            @RequestParam(defaultValue = "false") Boolean expandLastExecutedAction,
            @RequestParam(defaultValue = "false") Boolean expandLastState,
            PagedResourcesAssembler<IterationEntity> assembler
    ) {
        return new ResponseEntity<>(service.getAllIterations(ids, reportIds, info, infoLike, severity,
                severityGreaterThan, severityGreaterThanOrEqual, severityLessThan, severityLessThanOrEqual,
                lastExecutedActionIds, lastStateIds, actionIds,
                pageNo, pageSize, expandActions, expandLastExecutedAction, expandLastState, assembler), HttpStatus.OK);
    }
}
