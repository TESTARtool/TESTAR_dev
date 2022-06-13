package nl.ou.testar.reporter.specs;

import nl.ou.testar.reporter.entitiy.ActionEntity;
import nl.ou.testar.reporter.entitiy.ActionEntity_;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import java.util.Collection;
import java.util.function.Predicate;

public class SequenceItemSpecs {
    public static Specification byAbstractId(String abstractId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SequenceItemEntity_.abstractId),
                abstractId));
    }
    public static Specification byAbstractIds(Collection<String> abstractIds) {
        return((root, query, criteriaBuilder) -> root.get(SequenceItemEntity_.abstractId).in(abstractIds));
    }
    public static Specification byAbstractRId(String abstractRId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SequenceItemEntity_.abstractRId),
                abstractRId));
    }
    public static Specification byAbstractRIds(Collection<String> abstractRIds) {
        return((root, query, criteriaBuilder) -> root.get(SequenceItemEntity_.abstractRId).in(abstractRIds));
    }
    public static Specification byAbstractRTId(String abstractRTId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SequenceItemEntity_.abstractRTId),
                abstractRTId));
    }
    public static Specification byAbstractRTIds(Collection<String> abstractRTIds) {
        return((root, query, criteriaBuilder) -> root.get(SequenceItemEntity_.abstractRTId).in(abstractRTIds));
    }
    public static Specification byAbstractRTPId(String abstractRTPId) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SequenceItemEntity_.abstractRTPId),
                abstractRTPId));
    }
    public static Specification byAbstractRTPIds(Collection<String> abstractRTPIds) {
        return((root, query, criteriaBuilder) -> root.get(SequenceItemEntity_.abstractRTPId).in(abstractRTPIds));
    }
    public static Specification byAction(long actionId) {
        return((root, query, criteriaBuilder) -> {
            ListJoin<SequenceItemEntity, ActionEntity> join = root.join(SequenceItemEntity_.actions, JoinType.INNER);
            query.distinct(true);

            return criteriaBuilder.equal(join.get(ActionEntity_.id), actionId);
        });
    }
    public static Specification byActions(Collection<Long> actionIds) {
        return((root, query, criteriaBuilder) -> {
            ListJoin<SequenceItemEntity, ActionEntity> join = root.join(SequenceItemEntity_.actions, JoinType.INNER);
            query.distinct(true);

            return join.get(ActionEntity_.id).in(actionIds);
        });
    }
}
