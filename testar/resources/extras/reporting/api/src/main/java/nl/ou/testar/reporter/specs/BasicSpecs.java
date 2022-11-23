package nl.ou.testar.reporter.specs;

import nl.ou.testar.reporter.entitiy.BaseEntity;
import nl.ou.testar.reporter.entitiy.BaseEntity_;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;

public class BasicSpecs {

    public static <T extends BaseEntity> Specification<T> byId(Integer id/*, Class<T> clazz*/) {
        return ((root, query, cb) -> cb.equal(root.get(BaseEntity_.id), id));
    }

    public static <T extends BaseEntity> Specification<T> byIds(Collection<Integer> ids/*, Class<T> clazz*/) {
        return ((root, query, cb) -> root.get(BaseEntity_.id).in(ids));
    }

    public static <T extends BaseEntity> Specification<T> like(final SingularAttribute<? super T, String> attribute, final String searchFilter, final boolean caseSensitive) {
        if (caseSensitive) {
            return (root, query, cb) -> cb.like(root.get(attribute), "%" + searchFilter + "%");
        } else {
            return (root, query, cb) -> cb.like(cb.lower(root.get(attribute)), "%" + searchFilter.toLowerCase() + "%");
        }
    }
}
