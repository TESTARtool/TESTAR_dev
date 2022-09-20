package nl.ou.testar.reporter.entitiy;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "actions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ActionEntity extends BaseEntity {

    private String name;
    private String description;
    private String status;
    private String screenshot;
    private boolean selected;

    @Column(name = "start_time")
    private Timestamp startTime;

    @Column(name = "widget_path")
    private String widgetPath;

    @ManyToOne
    @JoinColumn(name = "iteration_id")
    private IterationEntity iteration;

    @ManyToOne
    @JoinColumn(name = "sequence_item_id")
    private SequenceItemEntity sequenceItem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public String getWidgetPath() {
        return widgetPath;
    }

    public void setWidgetPath(String widgetPath) {
        this.widgetPath = widgetPath;
    }

    public IterationEntity getIteration() {
        return iteration;
    }

    public void setIteration(IterationEntity iteration) {
        this.iteration = iteration;
    }

    public SequenceItemEntity getSequenceItem() {
        return sequenceItem;
    }

    public void setSequenceItem(SequenceItemEntity sequenceItem) {
        this.sequenceItem = sequenceItem;
    }

    public ActionEntity() {
        super();
    };

    public ActionEntity(Long id, String name, String description, String status, String screenshot,
                        boolean selected, Timestamp startTime, String widgetPath, IterationEntity iteration,
                        SequenceItemEntity sequenceItem) {
        super(id);
        this.name = name;
        this.description = description;
        this.status = status;
        this.screenshot = screenshot;
        this.selected = selected;
        this.startTime = startTime;
        this.widgetPath = widgetPath;
        this.iteration = iteration;
        this.sequenceItem = sequenceItem;
    }
}
