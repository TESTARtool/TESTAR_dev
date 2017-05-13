package org.fruit.a11y.wcag;

import static org.junit.Assert.*;

import org.junit.Test;

public class PrincipleTest {

	@Test
	public void testCtor() {
		Principle p = new Principle(1, "Foo");
		assertEquals(p.getNr(), "1");
	}

}
