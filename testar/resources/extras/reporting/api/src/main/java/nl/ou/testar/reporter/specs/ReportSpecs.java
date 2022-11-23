package nl.ou.testar.reporter.specs;

import nl.ou.testar.reporter.entitiy.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.ListJoin;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

public class ReportSpecs {
    public static Specification<ReportEntity> withTag(String tag) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ReportEntity_.tag), tag));
    }
    public static Specification<ReportEntity> withTagLike(String tagPart, boolean caseSensitive) {
        return BasicSpecs.like(ReportEntity_.tag, tagPart, caseSensitive);
    }
    public static Specification<ReportEntity> reportedAt(LocalDateTime time) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ReportEntity_.time),
                Timestamp.valueOf(time)));
    }
    public static Specification<ReportEntity> reportedAtOrBefore(LocalDateTime time) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(ReportEntity_.time),
                Timestamp.valueOf(time)));
    }
    public static Specification<ReportEntity> reportedBefore(LocalDateTime time) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get(ReportEntity_.time),
                Timestamp.valueOf(time)));
    }
    public static Specification<ReportEntity> reportedAtOrAfter(LocalDateTime time) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(ReportEntity_.time),
                Timestamp.valueOf(time)));
    }
    public static Specification<ReportEntity> reportedAfter(LocalDateTime time) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(ReportEntity_.time),
                Timestamp.valueOf(time)));
    }
    public static Specification<ReportEntity> withActionsPerSequence(int actionsPerSequence) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ReportEntity_.actionsPerSequence),
                actionsPerSequence));
    }
    public static Specification<ReportEntity> withActionsPerSequenceGreaterThan(int actionsPerSequence) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get(ReportEntity_.actionsPerSequence),
                actionsPerSequence));
    }
    public static Specification<ReportEntity> withActionsPerSequenceGreaterThanOrEqual(int actionsPerSequence) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.ge(root.get(ReportEntity_.actionsPerSequence),
                actionsPerSequence));
    }
    public static Specification<ReportEntity> withActionsPerSequenceLessThan(int actionsPerSequence) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lt(root.get(ReportEntity_.actionsPerSequence),
                actionsPerSequence));
    }
    public static Specification<ReportEntity> withActionsPerSequenceLessThanOrEqual(int actionsPerSequence) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.le(root.get(ReportEntity_.actionsPerSequence),
                actionsPerSequence));
    }
    public static Specification<ReportEntity> withTotalSequences(int totalSequences) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(ReportEntity_.totalSequences),
                totalSequences));
    }
    public static Specification<ReportEntity> withTotalSequencesGreaterThan(int totalSequences) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.gt(root.get(ReportEntity_.totalSequences),
                totalSequences));
    }
    public static Specification<ReportEntity> withTotalSequencesGreaterThanOrEqual(int totalSequences) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.ge(root.get(ReportEntity_.totalSequences),
                totalSequences));
    }
    public static Specification<ReportEntity> withTotalSequencesLessThan(int totalSequences) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.lt(root.get(ReportEntity_.totalSequences),
                totalSequences));
    }
    public static Specification<ReportEntity> withTotalSequencesLessThanOrEqual(int totalSequences) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.le(root.get(ReportEntity_.totalSequences),
                totalSequences));
    }
    public static Specification<ReportEntity> containingIteration(Integer iterationId) {
        return((root, query, criteriaBuilder) -> {
            ListJoin<ReportEntity, IterationEntity> join = root.join(ReportEntity_.iterations);
            query.distinct(true);

            return criteriaBuilder.equal(join.get(IterationEntity_.id), iterationId);
        });
    }
    public static Specification<ReportEntity> containingIterations(Collection<Integer> iterationIds) {
        return((root, query, criteriaBuilder) -> {
            ListJoin<ReportEntity, IterationEntity> join = root.join(ReportEntity_.iterations);
            query.distinct(true);

            return join.get(IterationEntity_.id).in(iterationIds);
        });
    }
    public static Specification<ReportEntity> containingAction(Integer actionId) {
        return((root, query, criteriaBuilder) -> {
            ListJoin<IterationEntity, ActionEntity> join = root.join(ReportEntity_.iterations).join(IterationEntity_.actions);
            query.distinct(true);

            return criteriaBuilder.equal(join.get(ActionEntity_.id), actionId);
        });
    }
    public static Specification<ReportEntity> containingActions(Collection<Integer> actionIds) {
        return((root, query, criteriaBuilder) -> {
            ListJoin<IterationEntity, ActionEntity> join = root.join(ReportEntity_.iterations).join(IterationEntity_.actions);
            query.distinct(true);

            return join.get(ActionEntity_.id).in(actionIds);
        });
    }
}
