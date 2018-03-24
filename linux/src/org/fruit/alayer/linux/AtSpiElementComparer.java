package org.fruit.alayer.linux;

import org.fruit.alayer.Rect;

import java.util.Comparator;


/**
 * Compares two AtSpiElements - used when sorting a list of AtSpiElements.
 */
public class AtSpiElementComparer implements Comparator<AtSpiElement> {


    private final static int WORSE = 1, BETTER = -1, EVEN = 0;


    /**
     * Compares two AtSpiElements and returns a result to be able to sort the elements in a list.
     * @param o1 The first AtSpiElement.
     * @param o2 The second AtSpiElement.
     * @return 1 if worse, -1 if better, 0 if even.
     */
    @Override
    public int compare(AtSpiElement o1, AtSpiElement o2) {
        if(o1.zIndex < o2.zIndex){
            return WORSE;
        }else if (o1.zIndex > o2.zIndex){
            return BETTER;
        }else{
            if(o1.boundingBoxOnScreen != null){
                if(o2.boundingBoxOnScreen != null){
                    double area1 = Rect.area(o1.boundingBoxOnScreen);
                    double area2 = Rect.area(o2.boundingBoxOnScreen);
                    return area1 < area2 ? BETTER : (area1 > area2 ? WORSE : EVEN);
                }else{
                    return BETTER;
                }
            }else{
                return WORSE;
            }
        }
    }


}