package nl.ou.testar.reporter.service;

import nl.ou.testar.reporter.entitiy.ReportEntity;
import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.model.SequenceItem;
import nl.ou.testar.reporter.model.assembler.AssemblingFlags;
import nl.ou.testar.reporter.model.assembler.SequenceItemAssembler;
import nl.ou.testar.reporter.repo.SequenceItemRepo;
import nl.ou.testar.reporter.specs.BasicSpecs;
import nl.ou.testar.reporter.specs.SequenceItemSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SequenceItemService {

    private SequenceItemRepo sequenceItemRepo;
    private SequenceItemAssembler resourceAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

    @Autowired
    public SequenceItemService(SequenceItemRepo sequenceItemRepo, SequenceItemAssembler resourceAssembler) {
        this.sequenceItemRepo = sequenceItemRepo;
        this.resourceAssembler = resourceAssembler;
    }

    public PagedModel<SequenceItem> getAllSequenceItems(
            Collection<Long> ids,
            Collection<String> abstractIds,
            Collection<String> abstractRIds,
            Collection<String> abstractRTIds,
            Collection<String> abstractRTPIds,
            Collection<Long> actionIds,
            Integer pageNo, Integer pageSize,
            boolean expandActions,
            final PagedResourcesAssembler<SequenceItemEntity> pagedResourcesAssembler
    ) {
        assemblingFlags.setExpandActions(expandActions);

        Specification<SequenceItemEntity> specification = null;
        if (ids != null) {
            specification = BasicSpecs.byIds(ids);
        }
        if (abstractIds != null) {
            final Specification<SequenceItemEntity> spec = SequenceItemSpecs.byAbstractIds(abstractIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (abstractRIds != null) {
            final Specification<SequenceItemEntity> spec = SequenceItemSpecs.byAbstractRIds(abstractRIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (abstractRTIds != null) {
            final Specification<SequenceItemEntity> spec = SequenceItemSpecs.byAbstractRTIds(abstractRTIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (abstractRTPIds != null) {
            final Specification<SequenceItemEntity> spec = SequenceItemSpecs.byAbstractRTPIds(abstractRTPIds);
            specification = (specification == null ? spec : specification.and(spec));
        }
        if (actionIds != null) {
            final Specification<SequenceItemEntity> spec = SequenceItemSpecs.byActions(actionIds);
            specification = (specification == null ? spec : specification.and(spec));
        }

        PageRequest paging = PageRequest.of(pageNo, pageSize);
        Page<SequenceItemEntity> pageSource = sequenceItemRepo.findAll(paging);
        return pagedResourcesAssembler.toModel(pageSource, resourceAssembler);
    }
}
