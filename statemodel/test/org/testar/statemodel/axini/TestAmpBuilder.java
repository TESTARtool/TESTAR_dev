package org.testar.statemodel.axini;

import org.junit.Test;
import org.testar.monkey.Assert;

public class TestAmpBuilder {

	@Test
	public void testAmpBuilderFromJsonString() throws Exception {
		String stateModelJson = String.join(System.lineSeparator(),
				"{",
				"  \"InitialUrl\": \"https://para.testar.org/parabank/index.htm\",",
				"  \"InitialPage\": \"ParaBank | Welcome | Online Banking\",",
				"  \"ConcreteState\": [",
				"    { \"AbstractID\": \"s1\", \"WebTitle\": \"ParaBank | Welcome | Online Banking\" },",
				"    { \"AbstractID\": \"s2\", \"WebTitle\": \"ParaBank | Site Map\" }",
				"  ],",
				"  \"ConcreteAction\": [",
				"    { \"AbstractID\": \"a1\", \"Desc\": \"Click Site Map\", \"WebCssSelector\": \"a[href*='sitemap.htm']\" }",
				"  ],",
				"  \"ConcreteTransitions\": [",
				"    { \"Source\": \"s1\", \"Action\": \"a1\", \"Target\": \"s2\" }",
				"  ]",
				"}"
				);

		GuiStateModel guiModel = GuiModelLoader.loadFromJson(stateModelJson);
		ProcessDefinition process = AmpBuilder.buildFrom(guiModel);

		Assert.isEquals("testar", process.getName());
		Assert.isEquals(1, process.getActions().size());
		Assert.isEquals(1, process.getBehaviors().size());
	}

	@Test
	public void testAmpBuilderFromJsonStringDeduplication() throws Exception {
		String stateModelJson = String.join(System.lineSeparator(),
				"{",
				"  \"InitialUrl\": \"https://para.testar.org/parabank/index.htm\",",
				"  \"InitialPage\": \"ParaBank | Welcome | Online Banking\",",
				"  \"ConcreteState\": [",
				"    { \"AbstractID\": \"s1\", \"WebTitle\": \"ParaBank | Welcome | Online Banking\" },",
				"    { \"AbstractID\": \"s2\", \"WebTitle\": \"ParaBank | Site Map\" }",
				"  ],",
				"  \"ConcreteAction\": [",
				"    { \"AbstractID\": \"a1\", \"Desc\": \"Click Site Map\", \"WebCssSelector\": \"a[href*='sitemap.htm']\" },",
				"    { \"AbstractID\": \"a1\", \"Desc\": \"Click Site Map\", \"WebCssSelector\": \"a[href*='sitemap.htm']\" }",
				"  ],",
				"  \"ConcreteTransitions\": [",
				"    { \"Source\": \"s1\", \"Action\": \"a1\", \"Target\": \"s2\" },",
				"    { \"Source\": \"s1\", \"Action\": \"a1\", \"Target\": \"s2\" }",
				"  ]",
				"}"
				);

		GuiStateModel guiModel = GuiModelLoader.loadFromJson(stateModelJson);
		ProcessDefinition process = AmpBuilder.buildFrom(guiModel);

		Assert.isEquals("testar", process.getName());
		Assert.isEquals(1, process.getActions().size());
		Assert.isEquals(1, process.getBehaviors().size());
	}
}
