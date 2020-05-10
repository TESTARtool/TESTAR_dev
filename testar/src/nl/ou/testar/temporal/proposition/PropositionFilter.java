package nl.ou.testar.temporal.proposition;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIATags;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nl.ou.testar.temporal.proposition.PropositionSelector.*;

public class PropositionFilter {

    private  String comment;
    private List<PropositionConditionPart> propositionConditionParts;
    private PropositionSelector propositionSelectorPart;

    public PropositionFilter() {
        propositionConditionParts = new ArrayList<>();
        propositionSelectorPart = new PropositionSelector();
    }


    @JsonIgnore
    public void updateComment(String comment) {
        this.comment = comment;
    }
    public String getComment() {
        return comment;
    }
    public List<PropositionConditionPart> getPropositionConditionParts() {
        return propositionConditionParts;
    }
    @SuppressWarnings("unused")
    public void setPropositionConditionParts(List<PropositionConditionPart> propositionConditionParts) {
        this.propositionConditionParts = propositionConditionParts;
    }
    @JsonGetter("propositionSelectorPart")
    private PropositionSelector getPropositionSelectorPart() {
        return propositionSelectorPart;
    }

    public Set<String> getPropositionsOfAttribute(String apkey, String transitionProperty, String value) {
        return getPropositionSelectorPart().getPropositionStrings(apkey, transitionProperty, value);
    }
    @SuppressWarnings("unused")
    public void setPropositionSelectorPart(PropositionSelector propositionSelectorPart) {
        this.propositionSelectorPart = propositionSelectorPart;
    }

    public void  setDefaultWidgetFilter() {
        updateComment("==================This is a Sample Filter, with 3 filter conditions");
        PropositionConditionPart wfp = new PropositionConditionPart();
        Set<String> temp= new HashSet<>();
        temp.add(Tags.Role.name());
        wfp.setSelectedAttributes(temp);
        wfp.setSelectedExpressions(useRoleConditionalExpressions());
        propositionConditionParts.add(wfp);

        PropositionConditionPart wfp1 = new PropositionConditionPart();
        temp= new  HashSet<String>(){{add(Tags.Path.name());}};
        wfp1.setSelectedAttributes(temp);
        wfp1.setSelectedExpressions(usePathConditionalExpressions());
        propositionConditionParts.add(wfp1);

        PropositionConditionPart wfp2 = new PropositionConditionPart();
        temp= new  HashSet<String>(){{add(Tags.Title.name());}};
        wfp2.setSelectedAttributes(temp);
        wfp2.setSelectedExpressions(useTitleConditionalExpressions());
        propositionConditionParts.add(wfp2);

        propositionSelectorPart.setSelectedAttributes(useBasicAttributes());
        propositionSelectorPart.setSelectedExpressions(useBasicSelectedExpressions());
    }
    public void  setDefaultVKEdgeFilter() {
        updateComment("==================This is a Sample EdgeFilter to filter on Virtual Keypress");
        PropositionConditionPart wfp = new PropositionConditionPart();
        Set<String> temp= new HashSet<>();
        temp.add(Tags.Role.name());
        wfp.setSelectedAttributes(temp);
        wfp.setSelectedExpressions(useVirtualKeyConditionalExpressions());
        propositionConditionParts.add(wfp);
        propositionSelectorPart.setSelectedAttributes(useMinimalTransAttributes());
        propositionSelectorPart.setSelectedExpressions(useVKSelectedExpressions());
    }
    public void  setDefaultStateFilter() {
        updateComment("==================This is a Sample StateFilter to collect the "+useMinimalAttributes()+" propositions");
        PropositionConditionPart wfp = new PropositionConditionPart();
        Set<String> temp= new HashSet<>();
        temp.add("*");
        wfp.setSelectedAttributes(temp);
        wfp.setSelectedExpressions(useCatchAllConditionalExpressions());
        propositionConditionParts.add(wfp);
        propositionSelectorPart.setSelectedAttributes(useMinimalAttributes());
        propositionSelectorPart.setSelectedExpressions(useMinimalSelectedExpressions());
    }

    public void  setCatchAllEdgeFilter() {
        updateComment("==================This is a Sample EdgeFilter with a 'catch all' condition");
        PropositionConditionPart wfp = new PropositionConditionPart();
        Set<String> temp= new HashSet<>();
        temp.add("*");
        wfp.setSelectedAttributes(temp);
        wfp.setSelectedExpressions(useCatchAllConditionalExpressions());
        propositionConditionParts.add(wfp);
        propositionSelectorPart.setSelectedAttributes(useMinimalTransAttributes());
        propositionSelectorPart.setSelectedExpressions(useDefaultTransSelectedExpressions());
    }


    public void setMinimalWidgetFilter() {
        updateComment("==================This is a Sample Filter, with 1 filter condition");
        PropositionConditionPart wfp2 = new PropositionConditionPart();
        Set<String>   temp= new  HashSet<String>(){{add(UIATags.UIAControlType.name());}};
        wfp2.setSelectedAttributes(temp);
        wfp2.setSelectedExpressions(useMinimalSelectedExpressions());
        propositionConditionParts.add(wfp2);

        propositionSelectorPart.setSelectedAttributes(useMinimalAttributes());
        propositionSelectorPart.setSelectedExpressions(useMinimalSelectedExpressions());
    }
}

