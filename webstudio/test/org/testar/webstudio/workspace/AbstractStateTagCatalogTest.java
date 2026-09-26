package org.testar.webstudio.workspace;

import org.junit.Assert;
import org.junit.Test;

public class AbstractStateTagCatalogTest {

    @Test
    public void exposesSettingsKeysWithGroupsAndDefaultSelection() {
        Assert.assertTrue(AbstractStateTagCatalog.options().stream().anyMatch(option ->
            option.key().equals("WidgetControlType")
                && option.group().equals("Common")
                && option.defaultSelected()));
        Assert.assertTrue(AbstractStateTagCatalog.options().stream().anyMatch(option ->
            option.key().equals("WidgetItemType") && option.group().equals("Windows")));
        Assert.assertTrue(AbstractStateTagCatalog.options().stream().anyMatch(option ->
            option.group().equals("WebDriver") && !option.key().isBlank()));
        Assert.assertTrue(AbstractStateTagCatalog.options().stream().anyMatch(option ->
            option.key().equals("AndroidWidgetResourceId") && option.group().equals("Android")));
        Assert.assertFalse(AbstractStateTagCatalog.options().stream().anyMatch(option ->
            option.key().equals("WidgetValuePattern")));
    }
}
