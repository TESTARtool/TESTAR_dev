package nl.ou.testar.temporal.selector;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.fruit.alayer.Tags;
import org.fruit.alayer.windows.UIATags;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static nl.ou.testar.temporal.selector.APSelector.*;

public class WidgetFilter {//extends APSelector {


    private  String freeFormatText;
    private List<WidgetFilterPart> widgetFilterParts;
    private APSelector widgetSelectorPart;




    public WidgetFilter() {
        super();
        widgetFilterParts = new ArrayList<WidgetFilterPart>();
        widgetSelectorPart = new APSelector();
    }
    @JsonIgnore
    public void updateFreeFormatText(String freeFormatText) {
        this.freeFormatText = freeFormatText;
    }
    public String getFreeFormatText() {
        return freeFormatText;
    }
    public List<WidgetFilterPart> getWidgetFilterParts() {
        return widgetFilterParts;
    }

    public void setWidgetFilterParts(List<WidgetFilterPart> widgetFilterParts) {
        this.widgetFilterParts = widgetFilterParts;
    }
    public APSelector getWidgetSelectorPart() {
        return widgetSelectorPart;
    }

    public void setWidgetSelectorPart(APSelector widgetSelectorPart) {
        this.widgetSelectorPart = widgetSelectorPart;
    }

    public void  setDefaultWidgetFilter() {
        updateFreeFormatText("==================This is a Sample WidgetFilter, with 3 filter requirements");
        WidgetFilterPart wfp = new WidgetFilterPart();
        Set<String> temp= new  HashSet<String>();
        temp.add(Tags.Role.name());
        wfp.setSelectedAttributes(temp);
        wfp.setSelectedExpressions(useRoleConditionalExpressions());
        widgetFilterParts.add(wfp);

        WidgetFilterPart wfp1 = new WidgetFilterPart();
        temp= new  HashSet<String>(){{add(Tags.Path.name());}};
        wfp1.setSelectedAttributes(temp);
        wfp1.setSelectedExpressions(usePathConditionalExpressions());
        widgetFilterParts.add(wfp1);

        WidgetFilterPart wfp2 = new WidgetFilterPart();
        temp= new  HashSet<String>(){{add(Tags.Title.name());}};
        wfp2.setSelectedAttributes(temp);
        wfp2.setSelectedExpressions(useTitleConditionalExpressions());
        widgetFilterParts.add(wfp2);

        widgetSelectorPart.setSelectedAttributes(useBasicAttributes());
        widgetSelectorPart.setSelectedExpressions(useBasicSelectedExpressions());
    }

    public void setMinimalWidgetFilter() {
        updateFreeFormatText("==================This is a Sample WidgetFilter, with 1 filter requirements");
        WidgetFilterPart wfp2 = new WidgetFilterPart();
        Set<String>   temp= new  HashSet<String>(){{add(UIATags.UIAControlType.name());}};
        wfp2.setSelectedAttributes(temp);
        wfp2.setSelectedExpressions(useMinimalSelectedExpressions());
        widgetFilterParts.add(wfp2);

        widgetSelectorPart.setSelectedAttributes(useMinimalAttributes());
        widgetSelectorPart.setSelectedExpressions(useMinimalSelectedExpressions());
    }
}

