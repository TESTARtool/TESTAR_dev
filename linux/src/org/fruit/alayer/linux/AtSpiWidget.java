package org.fruit.alayer.linux;


import org.fruit.Assert;
import org.fruit.Drag;
import org.fruit.Util;
import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.NoSuchTagException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import static org.fruit.alayer.linux.AtSpiTags.*;


/**
 * Represents an element in an application - wraps properties of an AT-SPI node in a Testar defined way.
 *
 * A Widget is usually a control element of an SUT.
 * Widgets have exactly one parent and can have several children.
 * They are attached to a State and form a Widget Tree.
 * In fact a State is a Widget itself and is the root
 * of the Widget Tree.
 *
 */
public class AtSpiWidget implements Widget, Serializable {


    //region Properties


    AtSpiState root;
    public AtSpiWidget parent;
    public AtSpiElement element;


    private Map<Tag<?>, Object> tags = Util.newHashMap();
    public ArrayList<AtSpiWidget> children = new ArrayList<>();


    //endregion


    //region Constructors


    /**
     * Constructs a new AtSpiWidget with a root, parent and linked to an AtSpiElement.
     * @param root The root of the State tree.
     * @param parent The parent of the AtSpiWidget.
     * @param element The element this AtSpiWidget links to.
     */
    AtSpiWidget(AtSpiState root, AtSpiWidget parent, AtSpiElement element){


        this.parent = parent;
        this.element = element;
        this.root = root;


        if(parent != null) {
            parent.children.add(this);
        }


    }


    //endregion


    //region Widget implementation


    /**
     * Gets the root of the widget tree.
     * @return The root of the widget tree.
     */
    @Override
    public AtSpiState root() {
        return root;
    }


    /**
     * Gets the parent of the widget tree.
     * @return The parent of the widget tree.
     */
    @Override
    public Widget parent() {
        return parent;
    }


    /**
     * Returns the child by index.
     * @param i The index of the child.
     * @return The child at index i.
     */
    @Override
    public Widget child(int i) {
        return children.get(i);
    }


    /**
     * Adds a child to widget - Method is never used.
     * @return The widget that got added.
     */
    @Override
    public Widget addChild() {
        return new AtSpiWidget(root, this, null);
    }


    //region Other necessary functionality


    /**
     * Adds a new widget to the State (widget tree) as a child of this widget.
     * @param element The AtSpiElement that will be linked to the new AtSpiWidget.
     * @return The newly created AtSpiWidget that's linked with the AtSpiElement.
     */
    AtSpiWidget addChild(AtSpiElement element){
        return new AtSpiWidget(root, this, element);
    }


    //endregion


    /**
     * Gets the number of children.
     * @return The number of children.
     */
    @Override
    public int childCount() {
        return children.size();
    }


    /**
     * Removes the widget and its children from the State tree - it resets the connections to null.
     */
    @Override
    public void remove() {

        Assert.isTrue(this != root, "You cannot remove the root!");
        assert(parent != null);
        parent.children.remove(this);

        invalidate(this);

    }


    /**
     * Sets this widget to be a child of the supplied widget.
     * @param p The new parent widget of this widget.
     * @param idx The index at which this widget will be added as a child.
     */
    @Override
    public void moveTo(Widget p, int idx) {

        Assert.notNull(p);
        Assert.isTrue(p instanceof AtSpiWidget);
        Assert.isTrue(this != root, "You cannot set the root's parent!");
        assert(parent != null);


        AtSpiWidget atSpiParent = (AtSpiWidget) p;
        Assert.isTrue(atSpiParent.root == root);
        Assert.isTrue(!Util.isAncestorOf(this, p), "The parent is a descendent of this widget!");


        // Set this widget to the child of the supplied widget.
        parent.children.remove(this);
        atSpiParent.children.add(idx, this);
        parent = atSpiParent;

    }


    /**
     * Creates an array of drag points - ?points where can be clicked and then the element will scroll?.
     * @param scrollArrowSize The size of scrolling arrows.
     * @param scrollThick The scroller thickness.
     * @return An array of drag points.
     */
    @SuppressWarnings("Duplicates")
    @Override
    public Drag[] scrollDrags(double scrollArrowSize, double scrollThick) {


        boolean hasScroll = get(AtSpiCanScroll, null);
        if (!hasScroll) {
            return null;
        }


        Drag[] hDrags = null, vDrags = null;


        boolean hScroll = get(AtSpiCanScrollHorizontally, Boolean.FALSE);
        if (hScroll){

            double hViewSize = get(AtSpiHorizontalScrollViewSizePercentage, Double.MIN_VALUE);

            if (hViewSize > 0){

                double hScrollPercent = get(AtSpiHorizontalScrollPercentage, -1.0);
                Shape shape = get(Tags.Shape, null);

                if (shape != null){
                    hDrags = getDrags(shape,true, hViewSize, hScrollPercent, scrollArrowSize, scrollThick);
                }

            }

        }


        boolean vScroll = get(AtSpiCanScrollVertically, Boolean.FALSE);
        if (vScroll){

            double vViewSize = get(AtSpiVerticalScrollViewSizePercentage, Double.MIN_VALUE);

            if (vViewSize > 0){

                double vScrollPercent = get(AtSpiVerticalScrollPercentage, -1.0);
                Shape shape = get(Tags.Shape, null);

                if (shape != null){
                    vDrags = getDrags(shape,false, vViewSize, vScrollPercent, scrollArrowSize, scrollThick);
                }

            }
        }


        return Util.join(hDrags,vDrags);


    }


    /**
     * Creates a string representation for the widget.
     * @param tab The TAB string to use - to be able to define different spacings most likely.
     * @return Returns a string representation for the widget.
     */
    @Override
    public String getRepresentation(String tab) {

        return (tab + "WIDGET = CONCRETE_" + this.get(Tags.ConcreteID) + " ABSTRACT(R)_" + this.get(Tags.Abstract_R_ID) +
                " ABSTRACT(R,T)_" + this.get(Tags.Abstract_R_T_ID) + " ABSTRACT(R,T,P)_" + this.get(Tags.Abstract_R_T_P_ID) + "\n") +
                getPropertiesRepresentation(tab);

    }


    /**
     * Creates a string representation of the AtSpiWidget.
     * @param tags The tags to include in the string representation.
     * @return A String representation of the AtSpiWidget.
     */
    @Override
    public String toString(Tag<?>[] tags) {
        return Util.treeDesc(this, 2, tags);
    }


    //region Widget Helper functions


    /**
     * Disconnects a widget and its children from the State tree.
     * @param w The widget to disconnect.
     */
    private void invalidate(AtSpiWidget w){


        // Reset the connection for the current widget.
        if(w.element != null) {
            w.element.backRef = null;
        }
        w.root = null;


        // Also reset the connections for its children.
        for(AtSpiWidget c : w.children) {
            invalidate(c);
        }


    }


    /**
     * Creates a string representation for the widget.
     * @param tab The TAB string to use - to be able to define different spacings most likely.
     * @return Returns a string representation for the widget.
     */
    @SuppressWarnings("StringConcatenationInsideStringBufferAppend")
    private String getPropertiesRepresentation(String tab){


        StringBuilder pr = new StringBuilder();


        Role role = this.get(Tags.Role, null);
        if (role != null) {
            pr.append(tab + "ROLE = " + role.toString() + "\n");
        }



        String title = this.get(Tags.Title, null);
        if (title != null) {
            pr.append(tab + "TITLE = " + title + "\n");
        }



        Shape shape = this.get(Tags.Shape, null);
        if (shape != null) {
            pr.append(tab + "SHAPE = " + shape.toString() + "\n");
        }



        pr.append(tab + "CHILDREN = " + this.childCount() + "\n");
        pr.append(tab + "PATH = " + this.get(Tags.Path) + "\n");


        return pr.toString();


    }


    /**
     * Gets drag points depending on orientation and certain scroll properties.
     * @param shape Shape of the scrollable element??
     * @param scrollOrientation True = horizontal, False = vertical.
     * @param viewSize Viewport percentage shown.
     * @param scrollPercent Percentage scrolled.
     * @param scrollArrowSize Size of the arrow.
     * @param scrollThick Size of the scroll thumb.
     * @return An array of drag points.
     */
    @SuppressWarnings("Duplicates")
    private Drag[] getDrags(Shape shape, boolean scrollOrientation, double viewSize, double scrollPercent,
                            double scrollArrowSize, double scrollThick) {

        double scrollableSize = (scrollOrientation ? shape.width() : shape.height()) - scrollArrowSize * 2;
        double fixedH, fixedV;

        if (scrollOrientation){

            // Horizontal.
            fixedH = shape.x() + scrollArrowSize +
                    scrollableSize*scrollPercent/100.0 +
                    (scrollPercent < 50.0 ? scrollThick/2 : -3*scrollThick/2);
            fixedV = shape.y() + shape.height() - scrollThick/2;

        } else {

            // Vertical.
            fixedH = shape.x() + shape.width() - scrollThick/2;
            fixedV = shape.y() + scrollArrowSize +
                    scrollableSize*scrollPercent/100.0 +
                    (scrollPercent < 50.0 ? scrollThick/2 : -3*scrollThick/2);

        }


        int dragC = (int)Math.ceil(100.0 / viewSize) - 1;
        if (dragC < 1) {
            return null;
        }


        double[] emptyDragPoints = calculateScrollDragPoints(dragC,
                scrollOrientation ? fixedH-shape.x() : fixedV-shape.y(),
                scrollableSize/(double)dragC);


        Drag[] drags = new Drag[dragC];
        for (int i=0; i<dragC; i++){
            drags[i] = new Drag(fixedH, fixedV,
                    scrollOrientation ? shape.x() + scrollArrowSize + emptyDragPoints[i] : fixedH,
                    scrollOrientation ? fixedV : shape.y() + scrollArrowSize + emptyDragPoints[i]
            );
        }


        return drags;


    }


    /**
     * Creates drag points relative to a fixed point.
     * @param dragC Number of drag points to create.
     * @param fixedPoint The fixed point.
     * @param fragment The length two drag points will be apart.
     * @return Returns relative points.
     */
    @SuppressWarnings("Duplicates")
    private double[] calculateScrollDragPoints(int dragC, double fixedPoint, double fragment) {


        double dragP = 0.0;
        double[] dragPoints = new double[dragC];


        for (int i=0; i<dragC; i++){
            if (Math.abs(fixedPoint - dragP) < fragment)
                dragP += fragment;
            dragPoints[i] = dragP;
            dragP += fragment;
        }
        dragPoints[dragC-1] -= 5;


        return dragPoints;


    }


    //endregion


    //endregion


    //region Taggable implementation


    /**
     * Retrieves the value associated with a tag from this AtSpiWidget.
     * @param tag Tag to retrieve.
     * @param <T> The type of the value associated with the tag.
     * @return The value associated with the tag.
     * @throws NoSuchTagException When the tag could not be found.
     */
    @Override
    public <T> T get(Tag<T> tag) throws NoSuchTagException {

        T ret = get(tag, null);

        if(ret == null) {
            throw new NoSuchTagException(tag);
        }

        return ret;

    }


    /**
     * Retrieves the value associated with a tag from this AtSpiWidget.
     * @param tag Tag to retrieve.
     * @param <T> The type of the value associated with the tag.
     * @param defaultValue Default value which will be returned if the tag cannot be found.
     * @return The value associated with the tag.
     */
    @Override
    public <T> T get(Tag<T> tag, T defaultValue) {


        // Check if the value for the tag is cached.
        Object ret = tags.get(tag);


        // Cached - return value.
        // No element to retrieve the value from or non-existing tag - return default value.
        if(ret != null){
            //noinspection unchecked
            return (T)ret;
        }else if(element == null || tags.containsKey(tag)){
            return defaultValue;
        }


        // Retrieve the value for the tag by returning the value from the linked AtSpiElement.
        // Notes:
        //      - Not implemented in AT-SPI:
        //          + ToolTipText
        //          + UIAWindowInteractionState
        //          + UIAWindowVisualState
        //          + UIAAutomationId
        //          + UIAHelpText
        //          + UIAClassName
        //      - UIAControlType = AtSpiRole.
        if(tag.equals(Tags.Desc)){
            ret = element.name;
        }else if(tag.equals(Tags.Role)){
            ret = AtSpiRolesWrapper.fromTypeId(element.role.ordinal());
        }else if(tag.equals(Tags.HitTester)){
            ret = new AtSpiHitTester(element);
        }else if(tag.equals(Tags.Shape)){
            ret = element.boundingBoxOnScreen;
        }else if(tag.equals(Tags.Blocked)){
            ret = element.isBlocked;
        }else if(tag.equals(Tags.Enabled)){
            ret = element.isEnabled;
        }else if(tag.equals(Tags.Title)){
            ret = element.name;
        }else if(tag.equals(Tags.PID)){
            ret = this == root ? ((AtSpiRootElement)element).pid : null;
        }else if(tag.equals(Tags.IsRunning)){
            ret = this == root ? ((AtSpiRootElement)element).isRunning : null;
        }else if(tag.equals(Tags.TimeStamp)){
            ret = this == root ? ((AtSpiRootElement)element).timeStamp : null;
        }else if(tag.equals(Tags.Foreground)){
            ret = this == root ? ((AtSpiRootElement)element).isActive : null;
        }else if(tag.equals(Tags.HasStandardKeyboard)){
            ret = this == root ? ((AtSpiRootElement)element).hasStandardKeyboard : null;
        }else if(tag.equals(Tags.HasStandardMouse)){
            ret = this == root ? ((AtSpiRootElement)element).hasStandardMouse : null;
        }else if(tag.equals(AtSpiTags.AtSpiName)){
            ret = element.name;
        }else if(tag.equals(AtSpiTags.AtSpiOrientation)){
            ret = element.orientation;
        }else if(tag.equals(Tags.ZIndex)){
            ret = element.zIndex;
        }else if(tag.equals(AtSpiTags.AtSpiIsModal)){
            ret = element.isModal;
        }else if(tag.equals(AtSpiTags.AtSpiCanScroll)){
            ret = element.canScroll;
        }else if(tag.equals(AtSpiTags.AtSpiCanScrollHorizontally)){
            ret = element.canScrollHorizontally;
        }else if(tag.equals(AtSpiTags.AtSpiCanScrollVertically)){
            ret = element.canScrollVertically;
        }else if(tag.equals(AtSpiTags.AtSpiHorizontalScrollViewSizePercentage)){
            ret = element.hScrollViewSizePercentage;
        }else if(tag.equals(AtSpiTags.AtSpiVerticalScrollViewSizePercentage)){
            ret = element.vScrollViewSizePercentage;
        }else if(tag.equals(AtSpiTags.AtSpiHorizontalScrollPercentage)){
            ret = element.hScrollPercentage;
        }else if(tag.equals(AtSpiTags.AtSpiVerticalScrollPercentage)){
            ret = element.vScrollPercentage;
        }else if(tag.equals(AtSpiTags.AtSpiRole)){
            ret = element.role;
        }else if(tag.equals(AtSpiTags.AtSpiToolkitName)){
            ret = element.toolkitName;
        }else if(tag.equals(AtSpiTags.AtSpiHasFocus)){
            ret = element.hasFocus;
        }else if(tag.equals(AtSpiTags.AtSpiIsFocusable)){
            ret = element.isFocusable;
        }else if(tag.equals(AtSpiTags.AtSpiDescription)){
            ret = element.description;
        }


        // Cache the value for a next time.
        tags.put(tag, ret);


        //noinspection unchecked
        return (ret == null) ? defaultValue : (T)ret;


    }


    /**
     * Gets an iterator for the tags of this widget.
     * @return An iterator to iterate over the tags of this widget.
     */
    @Override
    public Iterable<Tag<?>> tags() {


        final AtSpiWidget self = this;
        Assert.notNull(self);

        final Set<Tag<?>> queryTags = new HashSet<>();
        queryTags.addAll(tags.keySet());
        queryTags.addAll(Tags.tagSet());
        queryTags.addAll(AtSpiTags.tagSet());


        return () -> new Iterator<Tag<?>>(){
            Iterator<Tag<?>> i = queryTags.iterator();
            AtSpiWidget target = self;
            Tag<?> next;


            @SuppressWarnings("Duplicates")
            private Tag<?> fetchNext(){
                if(next == null){
                    while(i.hasNext()){
                        next = i.next();
                        if(target.get(next, null) != null)
                            return next;
                    }
                    next = null;
                }
                return next;
            }


            public boolean hasNext() {
                return fetchNext() != null;
            }


            public Tag<?> next() {
                Tag<?> ret1 = fetchNext();
                if(ret1 == null)
                    throw new NoSuchElementException();
                next = null;
                return ret1;
            }


            public void remove() { throw new UnsupportedOperationException(); }


        };

    }


    /**
     * Sets the value for the supplied tag.
     * @param tag Tag to set/ associate the value for/ with.
     * @param value The value to associate with the tag.
     * @param <T> The type of the value.
     */
    @Override
    public <T> void set(Tag<T> tag, T value) {
        Assert.notNull(tag, value);
        tags.put(tag, value);
    }


    /**
     * Clears the value of the tag - it does not actually remove the Tag object from the Map.
     * @param tag Tag to clear the value for.
     */
    @Override
    public void remove(Tag<?> tag) {
        Assert.notNull(tag);
        tags.put(tag, null);
    }


    //endregion


    //region Serializable functionality


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 456999777888222L;


    // Most likely used to serialize and deserialize an instance of this class - don't know if this is used by Testar though.



    /**
     * Serialize an instance of this object.
     * @param oos The outputstream to write to.
     * @throws IOException An IO error occurred.
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }


    /**
     * Deserialize an instance of this object.
     * @param ois The inputstream to write to.
     * @throws IOException An IO error occurred.
     * @throws ClassNotFoundException Class could not be found.
     */
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
    }


    //endregion


}