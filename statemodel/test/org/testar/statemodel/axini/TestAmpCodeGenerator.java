package org.testar.statemodel.axini;

import org.junit.Test;
import org.testar.monkey.Assert;

public class TestAmpCodeGenerator {

	@Test
	public void testAmpCodeGeneratorFromJsonString() throws Exception {
		String stateModelJson = String.join(System.lineSeparator(),
				"{",
				"  \"InitialUrl\": \"https://para.testar.org/parabank/index.htm\",",
				"  \"InitialPage\": \"ParaBank | Welcome | Online Banking\",",
				"  \"ConcreteState\": [",
				"    { \"AbstractID\": \"s1\", \"WebTitle\": \"ParaBank | Welcome | Online Banking\" },",
				"    { \"AbstractID\": \"s2\", \"WebTitle\": \"ParaBank | Site Map\" }",
				"  ],",
				"  \"ConcreteAction\": [",
				"    { \"AbstractID\": \"a1\", \"Desc\": \"Click Site Map\", \"WebCssSelector\": \"[href='sitemap.htm']\" },",
				"    { \"AbstractID\": \"a2\", \"Desc\": \"Click Home\", \"WebCssSelector\": \"[href='home.htm']\" },",
				"    { \"AbstractID\": \"a3\", \"Desc\": \"Type testar_text\", \"WebCssSelector\": \"[type='text']\", \"InputText\": \"testar_text\" }",
				"  ],",
				"  \"ConcreteTransitions\": [",
				"    { \"Source\": \"s1\", \"Action\": \"a1\", \"Target\": \"s2\" },",
				"    { \"Source\": \"s2\", \"Action\": \"a2\", \"Target\": \"s1\" },",
				"    { \"Source\": \"s1\", \"Action\": \"a3\", \"Target\": \"s1\" }",
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
				"def Click_href_sitemap_htm_to_ParaBank_Site_Map()",
				"  receive 'click_link', constraint: %(selector == \"[href='sitemap.htm']\")",
				"  send 'page_title', constraint: %(_title == \"ParaBank | Site Map\")",
				"end",
				"",
				"def Click_href_home_htm_to_ParaBank_Welcome_Online_Banking()",
				"  receive 'click_link', constraint: %(selector == \"[href='home.htm']\")",
				"  send 'page_title', constraint: %(_title == \"ParaBank | Welcome | Online Banking\")",
				"end",
				"",
				"def Type_type_text_to_ParaBank_Welcome_Online_Banking()",
				"  receive 'fill_in', constraint: %(selector == \"[type='text']\" && value == \"testar_text\")",
				"  send 'page_title', constraint: %(_title == \"ParaBank | Welcome | Online Banking\")",
				"end",
				"",
				"process('testar') {",
				"",
				"  channel('extern') {",
				"    stimulus 'visit', '_url' => :string",
				"    stimulus 'click_link', 'selector' => :string",
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
				"  behavior('ParaBank | Site Map', :non_terminating) {",
				"    repeat {",
				"      o { Click_href_home_htm_to_ParaBank_Welcome_Online_Banking(); behave_as 'ParaBank | Welcome | Online Banking' }",
				"    }",
				"  }",
				"",
				"  behavior('ParaBank | Welcome | Online Banking', :non_terminating) {",
				"    repeat {",
				"      o { Click_href_sitemap_htm_to_ParaBank_Site_Map(); behave_as 'ParaBank | Site Map' }",
				"      o { Type_type_text_to_ParaBank_Welcome_Online_Banking(); behave_as 'ParaBank | Welcome | Online Banking' }",
				"    }",
				"  }",
				"",
				"  var 'initial_url', :string, \"https://para.testar.org/parabank/index.htm\"",
				"",
				"  call 'launch'",
				"",
				"  behave_as 'ParaBank | Welcome | Online Banking'",
				"",
				"}"
				);

		Assert.isTrue(normalizeLineEndings(ampCode).equals(normalizeLineEndings(expectedAmpCode)));
	}

	private String normalizeLineEndings(String input) {
		return input.replace("\r\n", "\n").trim();
	}
}
