package nl.ou.testar.reporter.entitiy;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "iterations")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class IterationEntity extends BaseEntity {

    private String info;
    private Double severity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private ReportEntity report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_executed_action_id")
    private ActionEntity lastExecutedAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_state_id")
    private SequenceItemEntity lastState;

    @OneToMany(mappedBy = "iteration")
    private List<ActionEntity> actions;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Double getSeverity() {
        return severity;
    }

    public void setSeverity(Double severity) {
        this.severity = severity;
    }

    public ReportEntity getReport() {
        return report;
    }

    public void setReport(ReportEntity report) {
        this.report = report;
    }

    public ActionEntity getLastExecutedAction() {
        return lastExecutedAction;
    }

    public void setLastExecutedAction(ActionEntity lastExecutedAction) {
        this.lastExecutedAction = lastExecutedAction;
    }

    public SequenceItemEntity getLastState() {
        return lastState;
    }

    public void setLastState(SequenceItemEntity lastState) {
        this.lastState = lastState;
    }

    public List<ActionEntity> getActions() {
        return actions;
    }

    public void setActions(List<ActionEntity> actions) {
        this.actions = actions;
    }

    public IterationEntity() {
        super();
    }

    public IterationEntity(Integer id, String info, Double severity, ReportEntity report,
                           ActionEntity lastExecutedAction, SequenceItemEntity lastState,
                           List<ActionEntity> actions) {
        super(id);
        this.info = info;
        this.severity = severity;
        this.report = report;
        this.lastExecutedAction = lastExecutedAction;
        this.lastState = lastState;
        this.actions = actions;
    }
}