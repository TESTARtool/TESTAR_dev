package nl.ou.testar.reporter.repo;

import nl.ou.testar.reporter.entitiy.IterationEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface IterationRepo extends PagingAndSortingRepository<IterationEntity, Integer>, JpaSpecificationExecutor {
}