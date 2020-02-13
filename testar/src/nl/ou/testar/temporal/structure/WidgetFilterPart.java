package nl.ou.testar.temporal.structure;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import nl.ou.testar.temporal.util.InferrableExpression;
import nl.ou.testar.temporal.util.PairBean;
import org.fruit.alayer.Role;
import org.fruit.alayer.windows.UIARoles;

import java.util.LinkedHashSet;
import java.util.Set;

public class WidgetFilterPart extends APSelector{


    public WidgetFilterPart() {
        super();
    }
    @JsonGetter("conditionAttributes")
    public Set<String> getSelectedAttributes() {
        return super.getSelectedAttributes();
    }
    @JsonSetter("conditionAttributes")
    public void setSelectedAttributes(Set<String> selectedAttributes) {
        super.setSelectedAttributes(selectedAttributes);

    }
    @JsonGetter("conditionExpressions")
    public Set<PairBean<InferrableExpression, String>> getSelectedExpressions() {

        return super.getSelectedExpressions();
    }
    @JsonSetter("conditionExpressions")
    public void setSelectedExpressions(Set<PairBean<InferrableExpression, String>> selectedExpressions) {
        super.setSelectedExpressions(selectedExpressions);
    }

}

