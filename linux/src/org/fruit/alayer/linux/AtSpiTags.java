package org.fruit.alayer.linux;

import org.fruit.Util;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Tag;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;
import org.fruit.alayer.linux.enums.AtSpiElementOrientations;

import java.util.Collections;
import java.util.Set;


/**
 * Creates and holds tags specific to AT-SPI info.
 */
public class AtSpiTags {


    //region Global variables


    private static final Set<Tag<?>> _tagSet = Util.newHashSet();


    //endregion


    //region Properties - Tags


    // Notes:
    //      - Unused Windows tags have not been converted.
    //      - ControlType uses CtrlId which is equal to the Role enumeration id for AT-SPI.
    //      - FrameWorkId equals ToolkitName.
    //      - Not implemented in AT-SPI:
    //              + UIAClassName
    //              + UIAHelpText
    //              + UIAAutomationID
    //              + UIAWindowInteractionState
    //              + UIAWindowVisualState


    // General info.
    public static final Tag<String> AtSpiName = from("AtSpiName", String.class);
    public static final Tag<String> AtSpiDescription = from("AtSpiDescription", String.class);
    public static final Tag<AtSpiRoles> AtSpiRole = from("AtSpiRole", AtSpiRoles.class);
    public static final Tag<AtSpiElementOrientations> AtSpiOrientation = from("AtSpiOrientation", AtSpiElementOrientations.class);
    public static final Tag<String> AtSpiToolkitName = from("AtSpiToolkitName", String.class);



    // Scroll info.
    public static final Tag<Boolean> AtSpiCanScroll = from("AtSpiCanScroll", Boolean.class);
    public static final Tag<Boolean> AtSpiCanScrollHorizontally = from("AtSpiCanScrollHorizontally", Boolean.class);
    public static final Tag<Boolean> AtSpiCanScrollVertically = from("AtSpiCanScrollVertically", Boolean.class);
    public static final Tag<Double> AtSpiHorizontalScrollViewSizePercentage = from("AtSpiHorizontalScrollViewSizePercentage", Double.class);
    public static final Tag<Double> AtSpiVerticalScrollViewSizePercentage = from("AtSpiVerticalScrollViewSizePercentage", Double.class);
    public static final Tag<Double> AtSpiHorizontalScrollPercentage = from("AtSpiHorizontalScrollPercentage", Double.class);
    public static final Tag<Double> AtSpiVerticalScrollPercentage = from("AtSpiVerticalScrollPercentage", Double.class);


    // State info.
    public static final Tag<Boolean> AtSpiIsModal = from("AtSpiIsModal", Boolean.class);
    public static final Tag<Boolean> AtSpiHasFocus = from("AtSpiHasFocus", Boolean.class);
    public static final Tag<Boolean> AtSpiIsFocusable = from("AtSpiIsFocusable", Boolean.class);


    //endregion


    //region Helper functions


    /**
     * Creates a new Tag with a name and type.
     * @param name The name of the tag.
     * @param valueType The type of the value associated with the tag.
     * @param <T> The type of the value associated with the tag.
     * @return The created AtSpiTag.
     */
    private static <T> Tag<T> from(String name, Class<T> valueType){
        Tag<T> ret = Tag.from(name, valueType);
        _tagSet.add(ret);
        return ret;
    }


    /**
     * Returns the set of tags as an unmodifiable set.
     * @return An unmodifiable set of the tags.
     */
    public static Set<Tag<?>> tagSet(){ return Collections.unmodifiableSet(_tagSet); }


    //endregion


}