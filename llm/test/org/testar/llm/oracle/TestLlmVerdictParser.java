package org.testar.llm.oracle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class TestLlmVerdictParser {

	@Test
	public void testParseStatusCompleted() {
		String response = "{\"status\":\"COMPLETED\",\"info\":\"objective reached\"}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.COMPLETED, verdict.getDecision());
		assertEquals("objective reached", verdict.getInfo());
		assertNull(verdict.match());
	}

	@Test
	public void testParseStatusInvalid() {
		String response = "{\"status\":\"INVALID\",\"info\":\"unexpected final state\"}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.INVALID, verdict.getDecision());
		assertEquals("unexpected final state", verdict.getInfo());
	}

	@Test
	public void testParseMatchTrue() {
		String response = "{\"match\":true,\"info\":\"done\"}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.COMPLETED, verdict.getDecision());
		assertEquals("done", verdict.getInfo());
	}

	@Test
	public void testParseMatchFalse() {
		String response = "{\"match\":\"false\",\"info\":\"continue\"}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.CONTINUE, verdict.getDecision());
		assertEquals("continue", verdict.getInfo());
	}

	@Test
	public void testParseUnknownFormatResponse() {
		String response = "{\"verdict\":\"completed\",\"info\":\"alias field\"}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.UNKNOWN, verdict.getDecision());
		assertEquals("alias field", verdict.getInfo());
	}

	@Test
	public void testParseUnknownStatusDecision() {
		String response = "{\"status\":\"MAYBE\",\"info\":\"not recognized\"}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.UNKNOWN, verdict.getDecision());
		assertEquals("not recognized", verdict.getInfo());
	}

	@Test
	public void testParseMissingJsonFields() {
		String response = "{}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.UNKNOWN, verdict.getDecision());
		assertEquals("", verdict.getInfo());
		assertNull(verdict.match());
	}

	@Test
	public void testParseIncorrectJsonFormat() {
		String response = "{\"status\":{},\"info\":null,\"match\":{}}";
		LlmVerdict verdict = LlmVerdictParser.parse(response);

		assertEquals(LlmVerdictDecision.UNKNOWN, verdict.getDecision());
		assertEquals("", verdict.getInfo());
		assertNull(verdict.match());
	}

	@Test(expected = NullPointerException.class)
	public void testParseNullInputThrowsException() {
		LlmVerdictParser.parse(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testParseInvalidJsonThrowsException() {
		LlmVerdictParser.parse("not-a-json-object");
	}

	@Test(expected = IllegalStateException.class)
	public void testParseArrayJsonThrowsException() {
		LlmVerdictParser.parse("[]");
	}
}
