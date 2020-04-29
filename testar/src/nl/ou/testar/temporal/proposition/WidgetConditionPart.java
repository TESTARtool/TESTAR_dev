package nl.ou.testar.temporal.proposition;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import nl.ou.testar.temporal.foundation.InferrableExpression;
import nl.ou.testar.temporal.foundation.PairBean;

import java.util.Set;

public class WidgetConditionPart extends PropositionSelector {


    public WidgetConditionPart() {
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

