package org.fruit.alayer;

import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.JsonBuilder;
import org.fruit.alayer.actions.StdActionCompiler;
import org.junit.Test;
import java.lang.reflect.Field;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Validate the way an action is constructed.
 * Created by floren on 25-5-2017.
 */
public class ActionTest {

    private StdActionCompiler actionCompiler = new AnnotatingActionCompiler();

    @Test
    public void getActionRepresentation() throws Exception {

        Action action = actionCompiler.leftClick();
        assertNotNull(action);

        Iterable<Tag<?>> tags = action.tags();

        StringBuilder jsonBuilder = new StringBuilder("{ ");
        for(Tag<?> tag : tags) {
            jsonBuilder.append(JsonBuilder.build(tag.name(),action.get(tag))).append(",");
        }
        jsonBuilder.append(" }");

        System.out.println(jsonBuilder.toString());



    }

}