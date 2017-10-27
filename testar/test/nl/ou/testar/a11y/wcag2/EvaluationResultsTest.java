package nl.ou.testar.a11y.wcag2;

import static org.junit.Assert.*;

import org.fruit.alayer.Verdict;
import org.junit.Test;

import nl.ou.testar.a11y.wcag2.EvaluationResult.Type;
import nl.ou.testar.a11y.wcag2.SuccessCriterion.Level;

public class EvaluationResultsTest {
	
	private final double DELTA = 0.01;

	@Test
	public void testGetOverallVerdict() {
		AbstractPrinciple p = new PerceivablePrinciple();
		AbstractGuideline g = new TextAlternativesGuideline(p);
		SuccessCriterion s1 = new SuccessCriterion(1, "Foo", g, Level.A),
				s2 = new SuccessCriterion(2, "Bar", g, Level.AA),
				s3 = new SuccessCriterion(3, "Baz", g, Level.AAA);
		EvaluationResult r1 = new EvaluationResult(s1, Type.OK),
				r2 = new EvaluationResult(s2, Type.WARNING),
				r3 = new EvaluationResult(s1, Type.WARNING),
				r4 = new EvaluationResult(s3, Type.ERROR),
				r5 = new EvaluationResult(s2, Type.ERROR),
				r6 = new EvaluationResult(s1, Type.ERROR);
		double os1, os2, os3;
		EvaluationResults results = new EvaluationResults();
		
		// no results
		assertEquals(Verdict.OK.severity(), results.getOverallVerdict().severity(), DELTA);
		// one result that is OK
		results.add(r1);
		os1 = results.getOverallVerdict().severity();
		assertEquals(Verdict.OK.severity(), os1, DELTA);
		// a warning, should be less severe than the lowest error but more severe than no problem
		results.add(r2);
		os2 = results.getOverallVerdict().severity();
		assertTrue(s3.getVerdictSeverity() > os2 + DELTA);
		assertTrue(os1 < os2);
		// a warning from a higher-level success criterion, should not affect the severity
		results.add(r3);
		os3 = results.getOverallVerdict().severity();
		assertEquals(os2, os3, DELTA);
		// a level 3 problem
		results.add(r4);
		assertEquals(s3.getVerdictSeverity(), results.getOverallVerdict().severity(), DELTA);
		// a level 2 problem
		results.add(r5);
		assertEquals(s2.getVerdictSeverity(), results.getOverallVerdict().severity(), DELTA);
		// a level 1 error
		results.add(r6);
		assertEquals(s1.getVerdictSeverity(), results.getOverallVerdict().severity(), DELTA);
	}

}
