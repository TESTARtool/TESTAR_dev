package nl.ou.testar.a11y.wcag;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.ou.testar.a11y.wcag.AbstractGuideline;
import nl.ou.testar.a11y.wcag.KeyboardAccessibleGuideline;
import nl.ou.testar.a11y.wcag.Principle;

public class GuidelineTest {

	@Test
	public void testGetNr() {
		Principle p = new Principle(1, "Foo");
		AbstractGuideline g = new KeyboardAccessibleGuideline(2, p);
		assertEquals("1.2", g.getNr());
	}

}
