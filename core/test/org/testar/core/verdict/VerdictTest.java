package org.testar.core.verdict;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Rect;
import org.testar.core.visualizers.ShapeVisualizer;

public class VerdictTest {

    private final ShapeVisualizer failVisualizer = new ShapeVisualizer(
                    Pen.PEN_RED, 
                    Rect.from(0, 0, 10, 10), 
                    "Fail Visualizer", 
                    0.5, 0.5);

    @Test
    public void testToString() {
        Verdict v = new Verdict(Verdict.Severity.OK, "This is a test verdict");
        assertEquals("The string representation of a Verdict shall include its severity and its info",
                "severity: 0.0 info: This is a test verdict", v.toString());
    }

    @Test
    public void testVerdictHelperAreOK() {
        Verdict ok = Verdict.OK;
        Verdict warn = new Verdict(Verdict.Severity.SUSPICIOUS_TAG, "Issue", failVisualizer);

        assertTrue(Verdict.helperAreAllVerdictsOK(Arrays.asList(ok)));
        assertFalse(Verdict.helperAreAllVerdictsOK(Arrays.asList(warn)));
    }

    @Test
    public void testEquals() {
        Verdict v1 = Verdict.OK;
        Verdict v2 = Verdict.FAIL;
        Verdict v3 = new Verdict(Verdict.Severity.FAIL, "Failure");
        Verdict v4 = new Verdict(Verdict.Severity.FAIL, "Different failure");

        assertTrue(Verdict.OK.equals(Verdict.OK));
        assertTrue(v1.equals(Verdict.OK));
        assertTrue(Verdict.OK.equals(v1));
        assertTrue(v2.equals(Verdict.FAIL));
        assertTrue(Verdict.FAIL.equals(v2));
        assertFalse(v1.equals(Verdict.FAIL));
        assertFalse(v3.equals(Verdict.FAIL));
        assertFalse(v3.equals(v4));
        assertFalse(v4.equals(v3));
    }

}
