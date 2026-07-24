package org.testar.rascal;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DslOracleMetadataGeneratorTest {

    private final DslOracleMetadata metadata = new DslOracleMetadataGenerator(
        Path.of("testar-oracle")
    ).generate();

    @Test
    public void extractsWidgetTypesFromTestarModel() {
        Assert.assertTrue(metadata.widgetTypes().contains("button"));
        Assert.assertTrue(metadata.widgetTypes().contains("static_text"));
        Assert.assertTrue(metadata.widgetTypes().contains("image"));
        Assert.assertTrue(metadata.widgetTypes().contains("table_data"));
    }

    @Test
    public void extractsFieldsAndTypesByWidgetTypeFromTestarModel() {
        Map<String, String> staticTextFields = fieldsByName(metadata.fieldsByWidgetType().get("static_text"));

        Assert.assertEquals("bool", staticTextFields.get("visible"));
        Assert.assertEquals("str", staticTextFields.get("text"));
        Assert.assertEquals("str", staticTextFields.get("fontsize"));
    }

    @Test
    public void extractsKeywordsFromOracleGrammar() {
        Assert.assertTrue(metadata.keywords().contains("assert"));
        Assert.assertTrue(metadata.keywords().contains("context"));
        Assert.assertTrue(metadata.keywords().contains("package"));
        Assert.assertTrue(metadata.keywords().contains("when"));
        Assert.assertTrue(metadata.keywords().contains("unless"));
        Assert.assertFalse(metadata.keywords().contains("t"));
    }

    @Test
    public void extractsRootKeywordsFromOracleGrammar() {
        Assert.assertTrue(metadata.rootKeywords().contains("assert"));
        Assert.assertTrue(metadata.rootKeywords().contains("context"));
        Assert.assertTrue(metadata.rootKeywords().contains("package"));
        Assert.assertTrue(metadata.rootKeywords().contains("import"));
        Assert.assertTrue(metadata.rootKeywords().contains("pattern"));
        Assert.assertFalse(metadata.rootKeywords().contains("when"));
    }

    @Test
    public void exposesConditionOperatorsForEditorSupport() {
        Assert.assertTrue(metadata.conditionOperators().contains("has nonempty"));
        Assert.assertTrue(metadata.conditionOperators().contains("is equal to"));
        Assert.assertTrue(metadata.conditionOperators().contains("spell checks in"));
        Assert.assertFalse(metadata.conditionOperators().contains("("));
    }

    @Test
    public void extractsConnectorKeywordsFromOracleGrammar() {
        Assert.assertTrue(metadata.connectorKeywords().contains("and"));
        Assert.assertTrue(metadata.connectorKeywords().contains("or"));
        Assert.assertTrue(metadata.connectorKeywords().contains("when"));
        Assert.assertTrue(metadata.connectorKeywords().contains("unless"));
    }

    @Test
    public void extractsLocalesFromLocaleSource() {
        Assert.assertTrue(metadata.locales().contains("en_US"));
        Assert.assertTrue(metadata.locales().contains("es_ES"));
    }

    private Map<String, String> fieldsByName(List<DslOracleModelField> fields) {
        return fields.stream()
            .collect(Collectors.toMap(DslOracleModelField::name, DslOracleModelField::type));
    }
}
