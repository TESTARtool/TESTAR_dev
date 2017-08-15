package nl.ou.testar.a11y.wcag2;

import static org.junit.Assert.*;

import org.junit.Test;

import nl.ou.testar.a11y.wcag2.AbstractPrinciple;

public class PrincipleTest {

	@Test
	public void testGetNr() {
		AbstractPrinciple p = new PerceivablePrinciple();
		assertEquals("1", p.getNr());
	}

}
