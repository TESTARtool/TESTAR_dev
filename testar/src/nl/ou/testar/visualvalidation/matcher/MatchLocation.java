package nl.ou.testar.visualvalidation.matcher;

import nl.ou.testar.visualvalidation.Location;

public class MatchLocation {
    final public int margin;
    final public Location location;

    public MatchLocation(int margin, Location location) {
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
