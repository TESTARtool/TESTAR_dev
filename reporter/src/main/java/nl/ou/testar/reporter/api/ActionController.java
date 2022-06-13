package nl.ou.testar.reporter.api;

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

    @GetMapping
    public ResponseEntity<PagedModel<Action>> getAllActions(
            @RequestParam(required = false) Collection<Long> ids,
            @RequestParam(required = false) Collection<Long> iterationIds,
            @RequestParam(required = false) Collection<Long> stateIds,
            @RequestParam(required = false) String widget,
            @RequestParam(required = false) String widgetLike,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String nameLike,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String descriptionLike,
            @RequestParam(required = false) Collection<String> statuses,
            @RequestParam(required = false) Boolean selected,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAtOrBefore,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedBefore,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAtOrAfter,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startedAfter,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "50") Integer pageSize,
            PagedResourcesAssembler<ActionEntity> assembler
    ) {
        return new ResponseEntity<>(service.getAllActions(ids, iterationIds, stateIds, widget, widgetLike, name,
                nameLike, description, descriptionLike, statuses, selected, startedAt, startedAtOrBefore, startedBefore,
                startedAtOrAfter, startedAfter, pageNo, pageSize, assembler), HttpStatus.OK);
    }
}
