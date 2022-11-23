package nl.ou.testar.reporter.specs;

import nl.ou.testar.reporter.entitiy.ActionEntity_;
import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.entitiy.IterationEntity_;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity_;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

public class ActionSpecs {
    public static Specification<ActionEntity> forIteration(Integer iterationId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.iteration)
                .get(IterationEntity_.id), iterationId));
    }
    public static Specification<ActionEntity> forIterations(Collection<Integer> iterationIds) {
        return ((root, query, criteriaBuilder) -> root.get(ActionEntity_.iteration).in(iterationIds));
    }
    public static Specification<ActionEntity> forState(Integer stateId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.sequenceItem)
        .get(SequenceItemEntity_.id), stateId));
    }
    public static Specification<ActionEntity> forStates(Collection<Integer> stateIds) {
        return ((root, query, criteriaBuilder) -> root.get(ActionEntity_.sequenceItem).get(SequenceItemEntity_.id)
            .in(stateIds));
    }
    public static Specification<ActionEntity> forWidget(String widgetPath) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.iteration), widgetPath));
    }
    public static Specification<ActionEntity> forWidgetLike(String part, boolean caseSensitive) {
        return BasicSpecs.like(ActionEntity_.widgetPath, part, caseSensitive);
    }
    public static Specification<ActionEntity> withName(String name) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.name), name));
    }
    public static Specification<ActionEntity> withNameLike(String part, boolean caseSensitive) {
        return BasicSpecs.like(ActionEntity_.name, part, caseSensitive);
    }
    public static Specification<ActionEntity> withDescription(String description) {
        return((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.description), description));
    }
    public static Specification<ActionEntity> withDescriptionLike(String part, boolean caseSensitive) {
        return BasicSpecs.like(ActionEntity_.description, part, caseSensitive);
    }
    public static Specification<ActionEntity> withStatus(String status) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.status), status));
    }
    public static Specification<ActionEntity> withStatuses(Collection<String> statuses) {
        return ((root, query, criteriaBuilder) -> root.get(ActionEntity_.status).in(statuses));
    }
    public static Specification<ActionEntity> selected(boolean selected) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.selected), selected));
    }
    public static Specification<ActionEntity> visited(boolean visited) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.visited), visited));
    }
    public static  Specification<ActionEntity> startedAt(LocalDateTime time) {
        return((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ActionEntity_.startTime), Timestamp.valueOf(time)));
    }
    public static  Specification<ActionEntity> startedAtOrBefore(LocalDateTime time) {
        return((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(ActionEntity_.startTime), Timestamp.valueOf(time)));
    }
    public static  Specification<ActionEntity> startedBefore(LocalDateTime time) {
        return((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(ActionEntity_.startTime), Timestamp.valueOf(time)));
    }
    public static  Specification<ActionEntity> startedAtOrAfter(LocalDateTime time) {
        return((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(ActionEntity_.startTime), Timestamp.valueOf(time)));
    }
    public static  Specification<ActionEntity> startedAfter(LocalDateTime time) {
        return((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(ActionEntity_.startTime), Timestamp.valueOf(time)));
    }
}
