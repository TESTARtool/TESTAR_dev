package nl.ou.testar.a11y.wcag2;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.ou.testar.a11y.wcag2.AbstractGuideline;
import nl.ou.testar.a11y.wcag2.AbstractPrinciple;

public class GuidelineTest {

	@Test
	public void testGetNr() {
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TimeBasedMediaGuideline(p);
		assertEquals("1.2", g.getNr());
	}
	
	@Test
	public void testGetSuccessCriterionByName() {
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TimeBasedMediaGuideline(p);
		// first
		assertEquals(g.getSuccessCriteria().get(0),
				g.getSuccessCriterionByName("Audio-only and Video-only (Prerecorded)"));
		// middle
		assertEquals(g.getSuccessCriteria().get(2),
				g.getSuccessCriterionByName("Audio Description or Media Alternative (Prerecorded)"));
		// last
		assertEquals(g.getSuccessCriteria().get(4),
				g.getSuccessCriterionByName("Audio Description (Prerecorded)"));
		// case-insensitive
		assertEquals(g.getSuccessCriteria().get(4),
				g.getSuccessCriterionByName("audio description (prerecorded)"));
		// invalid
		assertNull(g.getSuccessCriterionByName("No such criterion"));
		assertNull(g.getSuccessCriterionByName(""));
		assertNull(g.getSuccessCriterionByName(""));
	}

}
