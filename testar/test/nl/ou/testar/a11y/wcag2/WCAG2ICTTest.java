package nl.ou.testar.a11y.wcag2;

import static org.junit.Assert.*;

import org.junit.Test;

public class WCAG2ICTTest {

	@Test
	public void testNumbering() {
		WCAG2ICT wcag = new WCAG2ICT();
		int pNr = 1;
		for (AbstractPrinciple p : wcag.getPrinciples()) {
			if (p.nr != pNr)
				fail("Principles should be numbered sequentially starting at 1");
			int gNr = 1;
			for (AbstractGuideline g : p.getGuidelines()) {
				if (g.nr != gNr)
					fail("Guidelines should be numbered sequentially starting at 1");
				int sNr = 1;
				for (SuccessCriterion s : g.getSuccessCriteria()) {
					if (s.nr != sNr)
						fail("Success criteria should be numbered sequentially starting at 1");
					sNr++;
				}
				gNr++;
			}
			pNr++;
		}
	}

}
