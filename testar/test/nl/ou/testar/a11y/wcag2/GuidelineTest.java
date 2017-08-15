package nl.ou.testar.a11y.wcag2;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.ou.testar.a11y.wcag2.AbstractGuideline;
import nl.ou.testar.a11y.wcag2.KeyboardAccessibleGuideline;
import nl.ou.testar.a11y.wcag2.AbstractPrinciple;

public class GuidelineTest {

	@Test
	public void testGetNr() {
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TimeBasedMediaGuideline(p);
		assertEquals("1.2", g.getNr());
	}

}
