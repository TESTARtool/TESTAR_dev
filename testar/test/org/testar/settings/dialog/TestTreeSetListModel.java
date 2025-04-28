package org.testar.settings.dialog;


import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.Set;


public class TestTreeSetListModel {
    @Test
    public void testAddElement() {
        TreeSetListModel<String> model = new TreeSetListModel<>(Comparator.naturalOrder());
        model.add("b");
        model.add("a");

        Assert.assertEquals("Model should contain two elements.", 2, model.getSize());
        Assert.assertEquals("First element should be 'a' after natural ordering.", "a", model.getElementAt(0));
        Assert.assertEquals("Second element should be 'b'.", "b", model.getElementAt(1));
    }


    @Test
    public void testClear() {
        TreeSetListModel<String> model = new TreeSetListModel<>(Comparator.naturalOrder());
        model.add("a");
        model.add("b");

        model.clear();

        Assert.assertEquals("Model should be empty after clear.", 0, model.getSize());
    }

    @Test
    public void testAddAll() {
        TreeSetListModel<String> model = new TreeSetListModel<>(Comparator.naturalOrder());
        model.addAll(Set.of("c", "a", "b"));

        Assert.assertEquals("Model should contain three elements after addAll.", 3, model.getSize());
        Assert.assertEquals("a", model.getElementAt(0));
        Assert.assertEquals("b", model.getElementAt(1));
        Assert.assertEquals("c", model.getElementAt(2));
    }

    @Test
    public void testAsSet() {
        TreeSetListModel<String> model = new TreeSetListModel<>(Comparator.naturalOrder());
        model.add("a");
        model.add("b");

        Set<String> set = model.asSet();

        Assert.assertTrue("Set should contain element 'a'.", set.contains("a"));
        Assert.assertTrue("Set should contain element 'b'.", set.contains("b"));
        Assert.assertEquals("Set should have size two.", 2, set.size());
    }

    @Test
    public void testContains() {
        TreeSetListModel<String> model = new TreeSetListModel<>(Comparator.naturalOrder());
        model.add("a");

        Assert.assertTrue("Model should contain 'a'.", model.contains("a"));
        Assert.assertFalse("Model should not contain 'b'.", model.contains("b"));
    }

    @Test
    public void testGetElementAtOutOfBounds() {
        TreeSetListModel<String> model = new TreeSetListModel<>(Comparator.naturalOrder());
        model.add("a");

        try {
            model.getElementAt(1);
            Assert.fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }
}
