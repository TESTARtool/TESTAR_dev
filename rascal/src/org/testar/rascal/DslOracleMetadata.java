/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.rascal;

import java.util.List;
import java.util.Map;

public final class DslOracleMetadata {

    private final List<String> keywords;
    private final List<String> widgetTypes;
    private final Map<String, List<DslOracleModelField>> fieldsByWidgetType;
    private final List<String> fieldNames;
    private final List<String> rootKeywords;
    private final List<String> conditionOperators;
    private final List<String> connectorKeywords;
    private final List<String> locales;

    public DslOracleMetadata(List<String> keywords,
                             List<String> widgetTypes,
                             Map<String, List<DslOracleModelField>> fieldsByWidgetType,
                             List<String> fieldNames,
                             List<String> rootKeywords,
                             List<String> conditionOperators,
                             List<String> connectorKeywords,
                             List<String> locales) {
        this.keywords = List.copyOf(keywords);
        this.widgetTypes = List.copyOf(widgetTypes);
        this.fieldsByWidgetType = Map.copyOf(fieldsByWidgetType);
        this.fieldNames = List.copyOf(fieldNames);
        this.rootKeywords = List.copyOf(rootKeywords);
        this.conditionOperators = List.copyOf(conditionOperators);
        this.connectorKeywords = List.copyOf(connectorKeywords);
        this.locales = List.copyOf(locales);
    }

    public List<String> keywords() {
        return keywords;
    }

    public List<String> widgetTypes() {
        return widgetTypes;
    }

    public Map<String, List<DslOracleModelField>> fieldsByWidgetType() {
        return fieldsByWidgetType;
    }

    public List<String> fieldNames() {
        return fieldNames;
    }

    public List<String> rootKeywords() {
        return rootKeywords;
    }

    public List<String> conditionOperators() {
        return conditionOperators;
    }

    public List<String> connectorKeywords() {
        return connectorKeywords;
    }

    public List<String> locales() {
        return locales;
    }
}
