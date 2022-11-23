package nl.ou.testar.reporter.repo;

import nl.ou.testar.reporter.entitiy.ReportEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface ReportRepo extends PagingAndSortingRepository<ReportEntity, Integer>, JpaSpecificationExecutor {
    List<ReportEntity> findByTag(String tag);
}
