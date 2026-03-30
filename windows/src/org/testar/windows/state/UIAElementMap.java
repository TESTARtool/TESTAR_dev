/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2017-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.windows.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.testar.core.Assert;
import org.testar.core.alayer.Rect;

public final class UIAElementMap implements Serializable {
    private static final long serialVersionUID = 8336577831205889395L;
    final List<UIAElement> elements;

    private static class ElementComp implements Comparator<UIAElement>{
        final static int WORSE = 1, BETTER = -1, EVEN = 0;
        public int compare(UIAElement o1, UIAElement o2) {
            if(o1.zindex < o2.zindex){
                return WORSE;
            }else if (o1.zindex > o2.zindex){
                return BETTER;
            }else{
                if(o1.rect != null){
                    if(o2.rect != null){
                        double area1 = Rect.area(o1.rect);
                        double area2 = Rect.area(o2.rect);
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

    public static Builder newBuilder(){ return new Builder(); }

    public static final class Builder{
        final List<UIAElement> elements = new ArrayList<UIAElement>();

        public Builder addElement(UIAElement element){
            Assert.notNull(element);
            if(element.rect != null)
                elements.add(element);        
            return this;
        }

        public UIAElementMap build(){
            elements.sort(new ElementComp());
            return new UIAElementMap(this);
        }
    }


    private UIAElementMap(Builder builder){
        this.elements = builder.elements;
    }

    public UIAElement at(double x, double y){
        for(UIAElement element : elements){
            if(element.rect.contains(x, y))
                return element;
        }
        return null;
    }

    public boolean obstructed(UIAElement element, double x, double y){
        for(UIAElement obstacle : elements){
            if(obstacle.zindex <= element.zindex || obstacle == element)
                break;
            if(obstacle.rect.contains(x, y))
                return true;
        }
        return false;
    }
}
