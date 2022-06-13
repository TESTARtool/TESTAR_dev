package nl.ou.testar.reporter.entitiy;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sequence_items")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class SequenceItemEntity extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "concrete_id")
    private String concreteId;

    @Column(name = "abstract_id")
    private String abstractId;

    @Column(name = "abstract_r_id")
    private String abstractRId;

    @Column(name = "abstract_r_t_id")
    private String abstractRTId;

    @Column(name = "abstract_r_t_p_id")
    private String abstractRTPId;

    @OneToMany(mappedBy = "sequenceItem")
    private List<ActionEntity> actions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConcreteId() {
        return concreteId;
    }

    public void setConcreteId(String concreteId) {
        this.concreteId = concreteId;
    }

    public String getAbstractId() {
        return abstractId;
    }

    public void setAbstractId(String abstractId) {
        this.abstractId = abstractId;
    }

    public String getAbstractRId() {
        return abstractRId;
    }

    public void setAbstractRId(String abstractRId) {
        this.abstractRId = abstractRId;
    }

    public String getAbstractRTId() {
        return abstractRTId;
    }

    public void setAbstractRTId(String abstractRTId) {
        this.abstractRTId = abstractRTId;
    }

    public String getAbstractRTPId() {
        return abstractRTPId;
    }

    public void setAbstractRTPId(String abstractRTPId) {
        this.abstractRTPId = abstractRTPId;
    }

    public List<ActionEntity> getActions() {
        return actions;
    }

    public void setActions(List<ActionEntity> actions) {
        this.actions = actions;
    }

    public SequenceItemEntity() {
        super();
    }

    public SequenceItemEntity(Long id, String concreteId, String abstractId, String abstractRId,
                              String abstractRTId, String abstractRTPId, List<ActionEntity> actions) {
        super(id);
        this.concreteId = concreteId;
        this.abstractId = abstractId;
        this.abstractRId = abstractRId;
        this.abstractRTId = abstractRTId;
        this.abstractRTPId = abstractRTPId;
        this.actions = actions;
    }
}
