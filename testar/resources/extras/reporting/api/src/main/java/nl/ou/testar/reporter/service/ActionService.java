package nl.ou.testar.reporter.service;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.entitiy.IterationEntity;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.Action;
import nl.ou.testar.reporter.model.PostEntityResponse;
import nl.ou.testar.reporter.model.assembler.ActionAssembler;
import nl.ou.testar.reporter.repo.ActionRepo;
import nl.ou.testar.reporter.repo.IterationRepo;
import nl.ou.testar.reporter.repo.SequenceItemRepo;
import nl.ou.testar.reporter.specs.ActionSpecs;
import nl.ou.testar.reporter.specs.BasicSpecs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ActionService {
	
    @Autowired
    private ActionRepo actionRepo;

    @Autowired
    private IterationRepo iterationRepo;

    @Autowired
    private SequenceItemRepo sequenceItemRepo;

    @Autowired
    private ActionAssembler resourceAssembler;

    public PostEntityResponse createAction(
            String name,
            Integer iterationId,
            String description,
            String status,
            String screenshot,
            Timestamp startTime,
            Boolean selected,
            Boolean visited,
            Integer stateId,
            Integer targetStateId,
            String widgetPath) throws SaveEntityException {

        ActionEntity actionEntity = new ActionEntity();

        Integer actionId = storeAction(actionEntity, name, iterationId, description, status, screenshot, startTime,
                selected, visited, stateId, targetStateId, widgetPath).getId();
        return PostEntityResponse.builder().id(actionId).build();
    }

    public void updateAction(
            Integer actionId,
            String name,
            Integer iterationId,
            String description,
            String status,
            String screenshot,
            Timestamp startTime,
            Boolean selected,
            Boolean visited,
            Integer stateId,
            Integer targetStateId,
            String widgetPath) throws SaveEntityException {

        Optional<ActionEntity> action = actionRepo.findById(actionId);
        if (action.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find action with ID %d", actionId));
        }
        storeAction(action.get(), name, iterationId, description, status, screenshot, startTime, selected, visited, stateId,
                targetStateId, widgetPath);
    }

    public void deleteAction(Integer actionId) throws SaveEntityException {
        Optional<ActionEntity> action = actionRepo.findById(actionId);
        if (action.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find action with ID %d", actionId));
        }
        actionRepo.delete(action.get());
    }

    private ActionEntity storeAction(
            ActionEntity actionEntity,
            String name,
            Integer iterationId,
            String description,
            String status,
            String screenshot,
            Timestamp startTime,
            Boolean selected,
            Boolean visited,
            Integer stateId,
            Integer targetStateId,
            String widgetPath) throws SaveEntityException {

        if (name != null) {
            actionEntity.setName(name);
        }
        if (description != null) {
            actionEntity.setDescription(description);
        }
        if (status != null) {
            actionEntity.setStatus(status);
        }
        if (screenshot != null) {
            actionEntity.setScreenshot(screenshot);
        }
        if (startTime != null) {
            actionEntity.setStartTime(startTime);
        }
        if (selected != null) {
            actionEntity.setSelected(selected);
        }
        if (visited != null) {
            actionEntity.setSelected(visited);
        }
        if (widgetPath != null) {
            actionEntity.setWidgetPath(widgetPath);
        }

        if (iterationId != null && iterationId != 0) {
            Optional<IterationEntity> iteration = iterationRepo.findById(iterationId);
            if (iteration.isEmpty()) {
                throw new SaveEntityException(String.format("Cannot find iteration with ID %d", iterationId));
            }
            actionEntity.setIteration(iteration.get());
        }

        if (stateId != null && stateId != 0) {
            Optional<SequenceItemEntity> state = sequenceItemRepo.findById(stateId);
            if (state.isEmpty()) {
                throw new SaveEntityException(String.format("Cannot find state with ID %d", stateId));
            }
            actionEntity.setSequenceItem(state.get());
        }

        if (targetStateId != null && targetStateId != 0) {
            Optional<SequenceItemEntity> targetState = sequenceItemRepo.findById(targetStateId);
            if (targetState.isEmpty()) {
                throw new SaveEntityException(String.format("Cannot find target state with ID %d", targetStateId));
            }
            actionEntity.setTargetSequenceItem(targetState.get());
        }

        return actionRepo.save(actionEntity);
    }

    public PagedModel<Action> getAllActions(
            Collection<Integer> ids,
            Collection<Integer> iterationIds,
            Collection<Integer> stateIds,
            String widget,
            String widgetLike,
            String name,
            String nameLike,
            String description,
            String descriptionLike,
            Collection<String> statuses,
            Boolean selected,
            Boolean visited,
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
        if (visited != null) {
            final Specification<ActionEntity> spec = ActionSpecs.visited(visited);
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
