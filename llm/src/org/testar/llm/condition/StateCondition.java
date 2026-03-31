/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2024-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2024-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.llm.condition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.InvalidArgumentException;
import org.testar.core.state.State;
import org.testar.core.tag.Tag;
import org.testar.core.state.Widget;
import org.testar.webdriver.tag.WdTags;
import org.testar.statemodel.StateModelManager;
import org.testar.statemodel.util.QueryHelper;

/**
 * Simple condition that searches for a string in all states.
 */
public class StateCondition extends TestCondition {
    static final Logger logger = LogManager.getLogger();

    private final String field;
    private final String searchMessage;

    /**
     * Creates a new StateCondition.
     * @param field The field in the state model to search. Example: WebInnerHTML.
     * @param searchMessage The string to search for.
     * @param comparator The result of the query is compared to the threshold value using the selected operator.
     * @param threshold The result of the query is compared to this value.
     */
    public StateCondition(String field, String searchMessage, ConditionComparator comparator, int threshold) {
        super(comparator, threshold);
        this.field = field;
        this.searchMessage = searchMessage;
    }

    /**
     * Returns the field in the table of the state model to select.
     * @return Selected field.
     */
    public String getField() {
        return field;
    }

    /**
     * Returns the string to search in the set field.
     * @return Selected search message.
     */
    public String getSearchMessage() {
        return searchMessage;
    }

    /**
     * Evaluates whether this condition is true.
     * @param modelIdentifier modelIdentifier of the current test sequence.
     * @param stateModelManager stateModelManager of the current test sequence.
     * @return True if condition is met.
     */
    @Override
    public boolean evaluate(String modelIdentifier, StateModelManager stateModelManager) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT COUNT(*) ");
        queryBuilder.append("AS found ");
        queryBuilder.append("FROM ConcreteState ");
        queryBuilder.append("WHERE uid LIKE '" + modelIdentifier + "%' ");
        queryBuilder.append("AND " + getField() + " containstext '" + getSearchMessage() + "'");

        String query = queryBuilder.toString();
        String result = stateModelManager.queryStateModel(query);

        int matches = QueryHelper.parseCountQueryResponse(result, "found");
        int threshold = getThreshold();

        switch(getComparator()) {
            case EQUAL:
                return matches == threshold;
            case LESS_THAN:
                return matches < threshold;
            case GREATER_THAN:
                return matches > threshold;
            case LESS_THAN_EQUALS:
                return matches <= threshold;
            case GREATER_THAN_EQUALS:
                return matches >= threshold;
            default:
                throw new InvalidArgumentException("Invalid comparator for condition!");
        }
    }

    @Override
    public boolean evaluate(State state) {
    	for(Widget widget : state) {
    		// For web apps check the widget is visible
        	// The default value is true to avoid blocking SUT systems other than web apps
    		if(widget.get(WdTags.WebIsFullOnScreen, true) && widget.get(WdTags.WebIsDisplayed, true)) {
    	    	for(Tag<?> tag : widget.tags()){
    	    		if(tag.name().equals(getField()) && widget.get(tag, null) != null){
    	    			try {
    	    				String tagValue = widget.get(tag).toString();
    	    				if(tagValue.contains(searchMessage)) {
    	    					logger.info("State Condition Match for Tag Value: " + tagValue);
    	    					return true;
    	    				}
    	    			} catch (Exception e) {
    	    				// Continue with next tag
    	    			}
    	    		}
    	    	}
    		}
    	}

    	return false;
    }
}
