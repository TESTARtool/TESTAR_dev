package org.fruit.a11y.wcag;

import static org.junit.Assert.*;

import org.junit.Test;

public class SuccessCriterionTest {

	@Test
	public void testGetNr() {
		Principle p = new Principle(1, "Foo");
		AbstractGuideline g = new KeyboardAccessibleGuideline(2, p);
		SuccessCriterion s = new SuccessCriterion(3, "Bar", g, Level.A);
		assertEquals(s.getNr(), "1.2.3");
	}

}
