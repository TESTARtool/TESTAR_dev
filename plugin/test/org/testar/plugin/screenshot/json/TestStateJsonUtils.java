package org.testar.plugin.screenshot.json;

import org.junit.Test;
import org.testar.core.Assert;
import org.testar.stub.StateStub;

public class TestStateJsonUtils {

	@Test
	public void testJsonWithEmptyState() {
		// Test that an empty state does not break TESTAR when using JsonUtils
		StateStub state = new StateStub();
		boolean created = JsonUtils.createWidgetInfoJsonFile(state);
		Assert.isTrue(!created);
		created = JsonUtils.createFullWidgetTreeJsonFile(state, "");
		Assert.isTrue(!created);
	}

}
