package nl.ou.testar.temporal.scratch;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Car {

    private String color ;//="black";
    private String type;//="daf";

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    @JsonGetter("tiepe")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Car(String color, String type) {
        this.color = color;
        this.type = type;
    }

    // standard getters setters

}