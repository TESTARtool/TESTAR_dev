package nl.ou.testar.reporter.model;

import lombok.Builder;
import lombok.Data;
import nl.ou.testar.reporter.entitiy.IterationEntity;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Report extends RepresentationModel<Report> {
    private Integer id;

    private String tag;
    private LocalDateTime time;
    private String url;
    private Integer actionsPerSequence;
    private Integer totalSequences;

    // One of next values should be defined
    private List<Integer> iterationIds;
    private List<Iteration> iterations;
}
