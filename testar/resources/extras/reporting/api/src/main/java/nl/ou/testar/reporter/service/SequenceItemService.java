package nl.ou.testar.reporter.service;

import nl.ou.testar.reporter.entitiy.SequenceItemEntity;
import nl.ou.testar.reporter.exceptions.SaveEntityException;
import nl.ou.testar.reporter.model.PostEntityResponse;
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
import java.util.Optional;

@Service
public class SequenceItemService {

    @Autowired
    private SequenceItemRepo sequenceItemRepo;

    @Autowired
    private SequenceItemAssembler resourceAssembler;

    @Autowired
    private AssemblingFlags assemblingFlags;

    public PostEntityResponse createSequenceItem(
            String concreteId,
            String abstractId,
            String abstractRId,
            String abstractRTId,
            String abstractRTPId) throws SaveEntityException {

        SequenceItemEntity sequenceItemEntity = new SequenceItemEntity();
        Integer sequenceItemId = storeSequenceItem(sequenceItemEntity, concreteId, abstractId, abstractRId,
                abstractRTId, abstractRTPId).getId();
        return PostEntityResponse.builder().id(sequenceItemId).build();
    }

    public void updateSequenceItem(
            Integer sequenceItemId,
            String concreteId,
            String abstractId,
            String abstractRId,
            String abstractRTId,
            String abstractRTPId) throws SaveEntityException {

        Optional<SequenceItemEntity> sequenceItem = sequenceItemRepo.findById(sequenceItemId);
        if (sequenceItem.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find sequence item with ID %d", sequenceItemId));
        }
        storeSequenceItem(sequenceItem.get(), concreteId, abstractId, abstractRId, abstractRTId, abstractRTPId);
    }

    public void deleteSequenceItem(Integer sequenceItemId) throws SaveEntityException {
        Optional<SequenceItemEntity> sequenceItem = sequenceItemRepo.findById(sequenceItemId);
        if (sequenceItem.isEmpty()) {
            throw new SaveEntityException(String.format("Cannot find sequence item with ID %d", sequenceItemId));
        }
        sequenceItemRepo.delete(sequenceItem.get());
    }

    private SequenceItemEntity storeSequenceItem(
            SequenceItemEntity sequenceItemEntity,
            String concreteId,
            String abstractId,
            String abstractRId,
            String abstractRTId,
            String abstractRTPId) throws SaveEntityException {

        if (concreteId != null) {
            sequenceItemEntity.setConcreteId(concreteId);
        }
        if (abstractId != null) {
            sequenceItemEntity.setAbstractId(abstractId);
        }
        if (abstractRId != null) {
            sequenceItemEntity.setAbstractRId(abstractRId);
        }
        if (abstractRTId != null) {
            sequenceItemEntity.setAbstractRTId(abstractRTId);
        }
        if (abstractRTPId != null) {
            sequenceItemEntity.setAbstractRTPId(abstractRTPId);
        }
        return sequenceItemRepo.save(sequenceItemEntity);
    }

    public PagedModel<SequenceItem> getAllSequenceItems(
            Collection<Integer> ids,
            Collection<String> concreteIds,
            Collection<String> abstractIds,
            Collection<String> abstractRIds,
            Collection<String> abstractRTIds,
            Collection<String> abstractRTPIds,
            Collection<Integer> actionIds,
            Integer pageNo, Integer pageSize,
            boolean expandActions,
            final PagedResourcesAssembler<SequenceItemEntity> pagedResourcesAssembler) {
        assemblingFlags.setExpandActions(expandActions);

        Specification<SequenceItemEntity> specification = null;
        if (ids != null) {
            specification = BasicSpecs.byIds(ids);
        }
        if (concreteIds != null) {
            final Specification<SequenceItemEntity> spec = SequenceItemSpecs.byConcreteIds(concreteIds);
            specification = (specification == null ? spec : specification.and(spec));
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
        Page<SequenceItemEntity> pageSource = sequenceItemRepo.findAll(specification, paging);
        return pagedResourcesAssembler.toModel(pageSource, resourceAssembler);
    }
}
