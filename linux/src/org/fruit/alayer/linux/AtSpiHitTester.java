package org.fruit.alayer.linux;


import org.fruit.alayer.HitTester;


/**
 * An object that can execute an hit test action on an AtSpiElement - determining if the element can be clicked on
 * at a certain point on the screen.
 */
public class AtSpiHitTester implements HitTester {


    //region Global variables


    // Used to determine the class during serialization.
    private static final long serialVersionUID = 3216549871296454556L;


    private final AtSpiElement _element;


    //endregion


    //region Constructors


    /**
     * Default constructor wrapping an AtSpiElement to hit test.
     * @param element The AtSpiElement to hit test.
     */
    AtSpiHitTester(AtSpiElement element) {
        _element = element;
    }


    //endregion


    //region HitTester implementation


    /**
     * Runs the hit test action for a certain point on the element.
     * @param x The x-coordiante of the point.
     * @param y The y-coordinate of the point.
     * @return True if the element can be hit at the supplied point on the screen; False otherwise.
     */
    @Override
    public boolean apply(double x, double y) {
        return _element.root.visibleAt(_element, x, y);
    }


    /**
     * Runs the hit test action for a certain point on the element.
     * @param x The x-coordiante of the point.
     * @param y The y-coordinate of the point.
     * @param obscuredByChildFeature The element is obscured by a child??
     * @return True if the element can be hit at the supplied point on the screen; False otherwise.
     */
    @Override
    public boolean apply(double x, double y, boolean obscuredByChildFeature) {
        return _element.root.visibleAt(_element, x, y, obscuredByChildFeature);
    }


    //endregion


    //region Object overrides


    @Override
    public String toString() {

        if (_element == null) {
            return "AtSpiHitTester";
        } else {
            return "AtSpiHitTester for: " + _element.name + " bounding: " + _element.boundingBoxOnScreen.toString();
        }

    }


    //endregion


}