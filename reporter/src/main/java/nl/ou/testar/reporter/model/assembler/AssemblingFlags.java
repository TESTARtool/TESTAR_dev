package nl.ou.testar.reporter.model.assembler;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


@Component
@RequestScope
@Data
public class AssemblingFlags {
    private boolean expandActions = false;
    private boolean expandIterations = false;
    private boolean expandLastExecutedAction = false;
    private boolean expandLastState = false;
}
