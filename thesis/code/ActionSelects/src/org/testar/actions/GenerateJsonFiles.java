package org.testar.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.testar.actions.checks.ActionCheck;
import org.testar.actions.checks.EventType;
import org.testar.actions.filters.ActionFilter;
import org.testar.actions.selects.ActionSelect;
import org.testar.actions.selects.ActionSelectSequence;
import org.testar.actions.selects.ActionType;

public class GenerateJsonFiles {

	public static void main(String[] args) {
		ActionSelect actionSelectAdd = new ActionSelect();
		actionSelectAdd.setWidgetName("Add");
		actionSelectAdd.setWidgetNameMatchType(MatchType.EQUALS);
		List<String> neighbourWidgets1 = new ArrayList<String>();
		neighbourWidgets1.add("Complete");
		actionSelectAdd.setNeighbours(neighbourWidgets1);
		actionSelectAdd.setActionType(ActionType.LEFTCLICK);
		
		ActionSelect actionSelectName = new ActionSelect();
		actionSelectName.setWidgetName("Name");
		actionSelectName.setRoleName("UIAEdit");
		actionSelectName.setWidgetNameMatchType(MatchType.STARTSWITH);
		List<String> neighbourWidgets2 = new ArrayList<String>();
		neighbourWidgets2.add("Description");
		actionSelectName.setNeighbours(neighbourWidgets2);
		actionSelectName.setActionType(ActionType.TYPEINTO);
		actionSelectName.setValue("Maintenance action ${unique}");
		actionSelectName.setMaxWait(100);

		ActionSelect actionSelect3 = new ActionSelect();
		actionSelect3.setWidgetName("Location Data");
		actionSelect3.setWidgetNameMatchType(MatchType.STARTSWITH);
		List<String> neighbourWidgets3 = new ArrayList<String>();
		neighbourWidgets3.add("Description");
		actionSelect3.setNeighbours(neighbourWidgets3);
		actionSelect3.setActionType(ActionType.LEFTCLICK);
		
		List<ActionSelect> actionSelects = Arrays.asList(actionSelectAdd, actionSelectName, actionSelect3);
		ActionSelectSequence sequence = new ActionSelectSequence();
		sequence.setDescription("Add unique action");
		sequence.setActionSelects(actionSelects);
		sequence.setProbabilities(Arrays.asList(new Integer[] {100,75,50}));
		
		ActionFilter actionFilterConfirm = new ActionFilter();
		actionFilterConfirm.setWidgetName("Confirm");
		actionFilterConfirm.setWidgetNameMatchType(MatchType.STARTSWITH);
		actionFilterConfirm.setRoleName("testRole");
		actionFilterConfirm.setNeighbours(Arrays.asList("Yes"));
        System.out.println("Action filter 1: " + actionFilterConfirm.stringRepresentation());

        ActionFilter actionFilterAttributes1 = new ActionFilter();
        actionFilterAttributes1.setId("Attr1");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("role", "dialog");
        attributes.put("class", "window");
        actionFilterAttributes1.setAttributes(attributes);
        actionFilterAttributes1.setNeighbours(Arrays.asList("Yes"));
        System.out.println("Action filter 2: " + actionFilterAttributes1.stringRepresentation());

        ActionFilter actionFilterAttributes2 = new ActionFilter();
        actionFilterAttributes2.setId("Attr2");
        attributes = new HashMap<>();
        attributes.put("type", "input");
        actionFilterAttributes2.setAttributes(attributes);
        actionFilterAttributes2.setNeighbours(Arrays.asList("Yes"));
        System.out.println("Action filter 3: " + actionFilterAttributes2.stringRepresentation());

        ActionCheck actionCheck = new ActionCheck();
        actionCheck.setId("actionCheck");
        actionCheck.setEventType(EventType.RESTRICTED);
        actionCheck.setSelectType(SelectType.NAME_ROLE);
        actionCheck.setWidgetName("Confirm");
        actionCheck.setWidgetNameMatchType(MatchType.STARTSWITH);
        actionCheck.setRoleName("testRole");
        actionCheck.setNeighbours(Arrays.asList("No"));

        ActionCheck actionCheckAttributes = new ActionCheck();
        actionCheckAttributes.setId("AttrCheck");
        actionCheckAttributes.setEventType(EventType.MENU_OPEN);
        actionCheckAttributes.setSelectType(SelectType.ATTRIBUTES);
        Map<String, String> attributesCheck = new HashMap<>();
        attributesCheck.put("class", "window");
        actionCheckAttributes.setAttributes(attributesCheck);
        actionCheckAttributes.setNeighbours(Arrays.asList("Menu2"));
        
        ObjectMapper mapper = new ObjectMapper(); 
	    mapper.setSerializationInclusion(Include.NON_NULL);
	    ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

		try {
			writer.writeValue(new File("actionfilter.json"), actionFilterConfirm);
            writer.writeValue(new File("actionfilter1.json"), actionFilterAttributes1);
            writer.writeValue(new File("actionfilter2.json"), actionFilterAttributes2);
		} catch (IOException e) {
			System.out.println("Failed to write file: " + e.getMessage());
		}

		try {
			writer.writeValue(new File("actionselectsequence.json"), sequence);
		} catch (IOException e) {
			System.out.println("Failed to write file: " + e.getMessage());
		}

        try {
            writer.writeValue(new File("actioncheck.json"), actionCheck);
            writer.writeValue(new File("actioncheckattr.json"), actionCheckAttributes);
        } catch (IOException e) {
            System.out.println("Failed to write file: " + e.getMessage());
        }
	}
}
