package nl.ou.testar.temporal.model.proposition;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import nl.ou.testar.temporal.util.foundation.PairBean;

import java.util.Set;

/**
 * Combination of an attribute(s) condition and expressions
 * comparable to a complete WHERE clause in SQL
 */
public class PropositionConditionPart extends PropositionSelector {


    public PropositionConditionPart() {
        super();
    }

    /**
     * @return list of the attributes that must pass the condition in the widget filter.
     * comparable to column names in a WHERE clause in SQL
     */
    @JsonGetter("conditionAttributes")
    public Set<String> getSelectedAttributes() {
        return super.getSelectedAttributes();
    }

    /**
     * used for writing a default proposition manager
     * @param selectedAttributes
     */
    @JsonSetter("conditionAttributes")
    public void setSelectedAttributes(Set<String> selectedAttributes) {
        super.setSelectedAttributes(selectedAttributes);

    }

    /**
     * @return list of the expression that must pass the condition in the widget filter.
     * comparable to column values in a WHERE clause in SQL
     */
    @JsonGetter("conditionExpressions")
    public Set<PairBean<InferrableExpression, String>> getSelectedExpressions() {

        return super.getSelectedExpressions();
    }

    /**
     * used for writing a default proposition manager
     * @param selectedExpressions
     */
    @JsonSetter("conditionExpressions")
    public void setSelectedExpressions(Set<PairBean<InferrableExpression, String>> selectedExpressions) {
        super.setSelectedExpressions(selectedExpressions);
    }

}

