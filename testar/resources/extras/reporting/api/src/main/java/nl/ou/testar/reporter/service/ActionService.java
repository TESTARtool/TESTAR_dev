package nl.ou.testar.reporter.service;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.model.Action;
import nl.ou.testar.reporter.model.assembler.ActionAssembler;
import nl.ou.testar.reporter.repo.ActionRepo;
import nl.ou.testar.reporter.specs.ActionSpecs;
import nl.ou.testar.reporter.specs.BasicSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ActionService {

	@Autowired
	private ActionRepo actionRepo;
	
	@Autowired
    private ActionAssembler resourceAssembler;

//    @Autowired
//    public ActionService(ActionRepo actionRepo, ActionAssembler resourceAssembler) {
//        this.actionRepo = actionRepo;
//        this.resourceAssembler = resourceAssembler;
//    }

    public PagedModel<Action> getAllActions(
            Collection<Long> ids,
            Collection<Long> iterationIds,
            Collection<Long> stateIds,
            String widget,
            String widgetLike,
            String name,
            String nameLike,
            String description,
            String descriptionLike,
            Collection<String> statuses,
            Boolean selected,
            LocalDateTime startedAt,
            LocalDateTime startedAtOrBefore,
            LocalDateTime startedBefore,
            LocalDateTime startedAtOrAfter,
            LocalDateTime startedAfter,
            Integer pageNo,
            Integer pageSize,
            final PagedResourcesAssembler<ActionEntity> pagedResourcesAssembler) {

        Specification<ActionEntity> specification = null;
        if (ids != null) {
            specification = BasicSpecs.byIds(ids);
        }
        if (iterationIds != null) {
            final Specification<ActionEntity> spec = ActionSpecs.forIterations(iterationIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (stateIds != null) {
            final Specification<ActionEntity> spec = ActionSpecs.forStates(stateIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (widget != null) {
            final Specification<ActionEntity> spec = ActionSpecs.forWidget(widget);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (widgetLike != null) {
            final Specification<ActionEntity> spec = ActionSpecs.forWidgetLike(widgetLike, false);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (name != null) {
            final Specification<ActionEntity> spec = ActionSpecs.withName(name);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (nameLike != null) {
            final Specification<ActionEntity> spec = ActionSpecs.withNameLike(nameLike, false);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (description != null) {
            final Specification<ActionEntity> spec = ActionSpecs.withDescription(description);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (descriptionLike != null) {
            final Specification<ActionEntity> spec = ActionSpecs.withDescriptionLike(description, false);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (statuses != null) {
            final Specification<ActionEntity> spec = ActionSpecs.withStatuses(statuses);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (selected != null) {
            final Specification<ActionEntity> spec = ActionSpecs.selected(selected);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (startedAt != null) {
            final Specification<ActionEntity> spec = ActionSpecs.startedAt(startedAt);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (startedAtOrBefore != null) {
            final Specification<ActionEntity> spec = ActionSpecs.startedAtOrBefore(startedAtOrBefore);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (startedBefore != null) {
            final Specification<ActionEntity> spec = ActionSpecs.startedBefore(startedBefore);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (startedAtOrAfter != null) {
            final Specification<ActionEntity> spec = ActionSpecs.startedAtOrAfter(startedAtOrAfter);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (startedAfter != null) {
            final Specification<ActionEntity> spec = ActionSpecs.startedAfter(startedAfter);
            specification = (specification == null ? spec : specification.and(spec));
        }

        PageRequest paging = PageRequest.of(pageNo, pageSize);
        Page<ActionEntity> pageSource = actionRepo.findAll(specification, paging);
        return pagedResourcesAssembler.toModel(pageSource, resourceAssembler);
    }
}
