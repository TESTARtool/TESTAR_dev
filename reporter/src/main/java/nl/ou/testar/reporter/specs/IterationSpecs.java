package nl.ou.testar.reporter.specs;

import nl.ou.testar.reporter.entitiy.*;
import nl.ou.testar.reporter.model.Iteration;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import java.util.Collection;

public class IterationSpecs {
    public static Specification<IterationEntity> byReport(long reportId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(IterationEntity_.report)
                .get(ReportEntity_.id), reportId));
    }
    public static Specification<IterationEntity> byReports(Collection<Long> reportIds) {
        return ((root, query, criteriaBuilder) -> root.get(IterationEntity_.report).get(ReportEntity_.id)
                .in(reportIds));
    }
    public static Specification<IterationEntity> withInfo(String info) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(IterationEntity_.info), info));
    }
    public static Specification<IterationEntity> withInfoLike(String infoPart, boolean caseSensitive) {
        return BasicSpecs.like(IterationEntity_.info, infoPart, caseSensitive);
    }
    public static Specification<IterationEntity> withSeverity(int severity) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(IterationEntity_.severity), severity));
    }
    public static Specification<IterationEntity> withSeverityLessThan(int severity) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lt(root.get(IterationEntity_.severity), severity));
    }
    public static Specification<IterationEntity> withSeverityLessThanOrEqual(int severity) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.le(root.get(IterationEntity_.severity), severity));
    }
    public static Specification<IterationEntity> withSeverityGreaterThan(int severity) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get(IterationEntity_.severity), severity));
    }
    public static Specification<IterationEntity> withSeverityGreaterThanOrEqual(int severity) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.ge(root.get(IterationEntity_.severity), severity));
    }
    public static Specification<IterationEntity> byLastExecutedAction(long actionId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(IterationEntity_.lastExecutedAction)
            .get(ActionEntity_.id), actionId));
    }
    public static Specification<IterationEntity> byLastExecutedActions(Collection<Long> actionIds) {
        return ((root, query, criteriaBuilder) -> root.get(IterationEntity_.lastExecutedAction).get(ActionEntity_.id)
                .in(actionIds));
    }
    public static Specification<IterationEntity> byLastState(long lastStateId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(IterationEntity_.lastState)
            .get(SequenceItemEntity_.id), lastStateId));
    }
    public static Specification<IterationEntity> byLastStates(Collection<Long> lastStateIds) {
        return ((root, query, criteriaBuilder) -> root.get(IterationEntity_.lastState).get(SequenceItemEntity_.id)
                .in(lastStateIds));
    }
    public static Specification<IterationEntity> containingAction(long actionId) {
        return ((root, query, criteriaBuilder) -> {
            ListJoin<IterationEntity, ActionEntity> join = root.join(IterationEntity_.actions, JoinType.INNER);
            query.distinct(true);

            return criteriaBuilder.equal(join.get(ActionEntity_.id), actionId);
        });
    }
    public static Specification<IterationEntity> containingActions(Collection<Long> actionIds) {
        return ((root, query, criteriaBuilder) -> {
            ListJoin<IterationEntity, ActionEntity> join = root.join(IterationEntity_.actions, JoinType.INNER);
            query.distinct(true);

            return join.get(ActionEntity_.id).in(actionIds);
        });
    }
}
