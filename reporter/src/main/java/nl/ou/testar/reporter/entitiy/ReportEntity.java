package nl.ou.testar.reporter.entitiy;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "reports")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ReportEntity extends BaseEntity {

    private String tag;
    private Timestamp time;
    private String url;

    @Column(name = "actions_per_sequence")
    private int actionsPerSequence;

    @Column(name = "total_sequences")
    private int totalSequences;

    @OneToMany(mappedBy = "report")
    private List<IterationEntity> iterations;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getActionsPerSequence() {
        return actionsPerSequence;
    }

    public void setActionsPerSequence(int actionsPerSequence) {
        this.actionsPerSequence = actionsPerSequence;
    }

    public int getTotalSequences() {
        return totalSequences;
    }

    public void setTotalSequences(int totalSequences) {
        this.totalSequences = totalSequences;
    }

    public List<IterationEntity> getIterations() {
        return iterations;
    }

    public void setIterations(List<IterationEntity> iterations) {
        this.iterations = iterations;
    }

    public ReportEntity() {
        super();
    }

    public ReportEntity(Long id, String tag, Timestamp time, String url, int actionsPerSequence,
                        int totalSequences, List<IterationEntity> iterations) {
        super(id);
        this.tag = tag;
        this.time = time;
        this.url = url;
        this.actionsPerSequence = actionsPerSequence;
        this.totalSequences = totalSequences;
        this.iterations = iterations;
    }
}
