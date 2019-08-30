package org.testar.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ActionBase {
    @JsonIgnore
    private static final Logger logger = LoggerFactory.getLogger(ActionBase.class);

    protected SelectType selectType;
    protected String widgetName;
    protected MatchType widgetNameMatchType;
    protected String roleName;
    protected Map<String, String> attributes;
    protected List<String> neighbours;
    protected List<String> widgetIds;
	@JsonIgnore
	protected int counter = 0;

    /**
	 * Gets the widget selection type.
	 *
	 * @return the widget selection type
	 */
	public SelectType getSelectType() {
        return selectType;
    }

    /**
     * Sets the widget selection type.
     *
     * @param selectType the new widget selection type
     */
    public void setSelectType(SelectType selectType) {
        this.selectType = selectType;
    }

    /**
     * Gets the widget name.
     *
     * @return the widgetName
     */
	public String getWidgetName() {
		return widgetName;
	}

	/**
	 * Sets the widget name.
	 *
	 * @param widgetName the widgetName to set
	 */
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}
	
	/**
	 * Gets the widget name match type.
	 *
	 * @return the widgetNameMatchType
	 */
	public MatchType getWidgetNameMatchType() {
		return widgetNameMatchType;
	}

	/**
	 * Sets the widget name match type.
	 *
	 * @param widgetNameMatchType the widgetNameMatchType to set
	 */
	public void setWidgetNameMatchType(MatchType widgetNameMatchType) {
		this.widgetNameMatchType = widgetNameMatchType;
	}

	/**
	 * Gets the role name.
	 *
	 * @return the roleName
	 */
	public String getRoleName() {
	    return roleName;
	}
	
	/**
	 * Sets the role name.
	 *
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
	    this.roleName = roleName;
	}
	
	/**
	 * Gets the attributes.
	 *
	 * @return the attributes
	 */
	public Map<String,String> getAttributes() {
        return attributes;
    }

    /**
     * Sets the attributes.
     *
     * @param attributes the attributes
     */
    public void setAttributes(Map<String,String> attributes) {
        this.attributes = new HashMap<>(attributes);
    }

    /**
     * Gets the neighbour widgets.
     *
     * @return the neighbour widgets
     */
	public List<String> getNeighbours() {
		return neighbours;
	}

	/**
	 * Sets the neighbour widgets.
	 *
	 * @param neighbours the neighbour widgets to set
	 */
	public void setNeighbours(List<String> neighbours) {
		this.neighbours = new ArrayList<>(neighbours);
	}

    /**
     * Gets the widget ids.
     *
     * @return the widget ids
     */
    public List<String> getWidgetIds() {
        return widgetIds;
    }

    /**
     * Sets the widget ids.
     *
     * @param widgetId the new widget ids
     */
    public void setWidgetIds(List<String> widgetIds) {
        this.widgetIds = new ArrayList<>(widgetIds);
    }

    /**
     * Adds a widget id to the list of widget ids
     *
     * @param widgetId the widget id
     */
    public void addWidgetId(String widgetId) {
        if (widgetIds == null) {
            widgetIds = new ArrayList<>();
        }
        widgetIds.add(widgetId);
    }

	/**
	 * Get counter
	 * 
	 * @return counter
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * Set counter
	 * 
	 * @param counter
	 */
	public void setCounter(int counter) {
		this.counter = counter;
	}

	/**
	 * Increment counter with 1.
	 */
	public void incrementCounter() {
		this.counter += 1;
	}
	
	/**
	 * Return a string representation of this action base.
	 *
	 * @return the string representation
	 */
	public String stringRepresentation() {
        ObjectMapper mapper = new ObjectMapper(); 
        mapper.setSerializationInclusion(Include.NON_NULL);
        ObjectWriter writer = mapper.writer();
        try {
            return writer.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error("Error stringRepresentation: " + e.getMessage(), e);
            return this.toString();
        }
    }
}
