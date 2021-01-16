package nl.ou.testar.visualvalidation.matcher;

import java.awt.Rectangle;

public class MatchLocation {
    final public int margin;
    final public Rectangle location;

    public MatchLocation(int margin, Rectangle location) {
        this.margin = margin;
        this.location = location;
    }

    @Override
    public String toString() {
        return "MatchLocation{" +
                "margin=" + margin +
                ", location=" + location +
                '}';
    }
}
