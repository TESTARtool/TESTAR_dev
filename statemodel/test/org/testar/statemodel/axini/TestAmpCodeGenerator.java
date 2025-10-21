package org.testar.statemodel.axini;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.Test;

public class TestAmpCodeGenerator {

	@Test
	public void testAmpCodeGeneratorFromJsonString() throws Exception {
		String stateModelJson = String.join(System.lineSeparator(),
				"{",
				"  \"InitialUrl\": \"https://para.testar.org/parabank/index.htm\",",
				"  \"InitialPage\": \"ParaBank | Welcome | Online Banking\",",
				"  \"InitialIdentifier\": \"s1\",",
				"  \"ConcreteState\": [",
				"    { \"AbstractID\": \"s1\", \"WebTitle\": \"ParaBank | Welcome | Online Banking\" },",
				"    { \"AbstractID\": \"s2\", \"WebTitle\": \"ParaBank | Site Map\" },",
				"    { \"AbstractID\": \"s3\", \"WebTitle\": \"ParaBank | Contact Us\" }",
				"  ],",
				"  \"ConcreteAction\": [",
				"    { \"AbstractID\": \"a1\", \"Desc\": \"Click 'Site Map'\", \"WebCssSelector\": \"[href='sitemap.htm']\" },",
				"    { \"AbstractID\": \"a2\", \"Desc\": \"Click 'Home'\", \"WebCssSelector\": \"[href='home.htm']\" },",
				"    { \"AbstractID\": \"a3\", \"Desc\": \"Type testar_text\", \"WebCssSelector\": \"[type='text']\", \"InputText\": \"testar_text\" },",
				"    { \"AbstractID\": \"a4\", \"Desc\": \"Click 'Contact'\", \"WebCssSelector\": \"[href='contact.htm']\" }",
				"  ],",
				"  \"ConcreteTransitions\": [",
				"    { \"Source\": \"s1\", \"Action\": \"a1\", \"Target\": \"s2\" },",
				"    { \"Source\": \"s2\", \"Action\": \"a2\", \"Target\": \"s1\" },",
				"    { \"Source\": \"s1\", \"Action\": \"a3\", \"Target\": \"s1\" },",
				"    { \"Source\": \"s1\", \"Action\": \"a4\", \"Target\": \"s3\" }",
				"  ]",
				"}"
				);

		GuiStateModel guiModel = GuiModelLoader.loadFromJson(stateModelJson);
		ProcessDefinition process = AmpBuilder.buildFrom(guiModel);
		String ampCode = AmpCodeGenerator.generate(process);

		String expectedAmpCode = String.join(System.lineSeparator(),
				"timeout 10.0",
				"external 'extern'",
				"",
				"def Click_Site_Map_to_ParaBank_Site_Map()",
				"  receive 'click', constraint: %(css == \"[href='sitemap.htm']\" && text == \"Site Map\")",
				"  send 'page_title', constraint: %(_title == \"ParaBank | Site Map\")",
				"end",
				"",
				"def Click_Home_to_ParaBank_Welcome_Online_Banking()",
				"  receive 'click', constraint: %(css == \"[href='home.htm']\" && text == \"Home\")",
				"  send 'page_title', constraint: %(_title == \"ParaBank | Welcome | Online Banking\")",
				"end",
				"",
				"def Type_testar_text_to_ParaBank_Welcome_Online_Banking()",
				"  receive 'fill_in', constraint: %(selector == \"[type='text']\" && value == \"testar_text\")",
				"  send 'page_title', constraint: %(_title == \"ParaBank | Welcome | Online Banking\")",
				"end",
				"",
				"def Click_Contact_to_ParaBank_Contact_Us()",
				"  receive 'click', constraint: %(css == \"[href='contact.htm']\" && text == \"Contact\")",
				"  send 'page_title', constraint: %(_title == \"ParaBank | Contact Us\")",
				"end",
				"",
				"process('testar') {",
				"",
				"  channel('extern') {",
				"    stimulus 'visit', '_url' => :string",
				"    stimulus 'click', 'css' => :string, 'text' => :string",
				"    stimulus 'fill_in', 'selector' => :string, 'value' => :string",
				"    response 'page_title', '_title' => :string, '_url' => :string",
				"  }",
				"",
				"  behavior('launch') {",
				"    receive 'visit',",
				"    constraint: \"_url == initial_url\"",
				"    send 'page_title',",
				"    constraint: %(_title == \"ParaBank | Welcome | Online Banking\")",
				"  }",
				"",
				"  behavior('ParaBank | Welcome | Online Banking - s1', :non_terminating) {",
				"    repeat {",
				"      o { Click_Site_Map_to_ParaBank_Site_Map(); behave_as 'ParaBank | Site Map - s2' }",
				"      o { Type_testar_text_to_ParaBank_Welcome_Online_Banking(); behave_as 'ParaBank | Welcome | Online Banking - s1' }",
				"    }",
				"  }",
				"",
				"  behavior('ParaBank | Site Map - s2', :non_terminating) {",
				"    repeat {",
				"      o { Click_Home_to_ParaBank_Welcome_Online_Banking(); behave_as 'ParaBank | Welcome | Online Banking - s1' }",
				"    }",
				"  }",
				"",
				"  var 'initial_url', :string, \"https://para.testar.org/parabank/index.htm\"",
				"",
				"  call 'launch'",
				"",
				"  behave_as 'ParaBank | Welcome | Online Banking - s1'",
				"",
				"}"
				);

		assertAmpEqualsByLine(expectedAmpCode, ampCode);
	}

	private void assertAmpEqualsByLine(String expected, String actual) {
		List<String> exp = toLines(expected);
		List<String> act = toLines(actual);

		int max = Math.min(exp.size(), act.size());
		for (int i = 0; i < max; i++) {
			if (!Objects.equals(exp.get(i), act.get(i))) {
				throw new AssertionError(String.format(
						"AMP mismatch at line %d:%nEXPECTED: %s%nACTUAL  : %s",
						i + 1, exp.get(i), act.get(i)));
			}
		}
		if (exp.size() != act.size()) {
			throw new AssertionError(String.format(
					"Different number of lines. EXPECTED: %d, ACTUAL: %d",
					exp.size(), act.size()));
		}
	}

	private List<String> toLines(String s) {
		return Arrays.asList(normalizeLineEndings(s).split("\n", -1));
	}

	private String normalizeLineEndings(String input) {
		return input.replace("\r\n", "\n").trim();
	}

}
