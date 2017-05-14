package org.fruit.a11y.wcag;

import static org.junit.Assert.*;

import org.junit.Test;

public class GuidelineTest {

	@Test
	public void testGetNr() {
		Principle p = new Principle(1, "Foo");
		AbstractGuideline g = new KeyboardAccessibleGuideline(2, p);
		assertEquals(g.getNr(), "1.2");
	}

}
