package nl.ou.testar.a11y.wcag2;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.ou.testar.a11y.wcag2.AbstractGuideline;
import nl.ou.testar.a11y.wcag2.KeyboardAccessibleGuideline;
import nl.ou.testar.a11y.wcag2.Principle;

public class GuidelineTest {

	@Test
	public void testGetNr() {
		Principle p = new Principle(1, "Foo");
		AbstractGuideline g = new KeyboardAccessibleGuideline(2, p);
		assertEquals("1.2", g.getNr());
	}

}
