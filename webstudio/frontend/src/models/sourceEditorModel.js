// Implements WS-FUNC-WORKSPACE-SOURCE-EDITOR-001 and WS-UX-SOURCE-EDITOR-001:
// source class/reference helpers used by Java composition nodes and policies.
export function sourceClassName(sourceFile) {
    if (!sourceFile?.name) {
        return "";
    }

    return sourceFile.name.endsWith(".java")
        ? sourceFile.name.slice(0, -".java".length)
        : sourceFile.name;
}

export function referencedClasses(workspaceDocument, referenceGroup) {
    if (!workspaceDocument?.references?.[referenceGroup]) {
        return [];
    }

    return workspaceDocument.references[referenceGroup];
}

export function sourceFilesForReferencedClasses(sourceFiles, workspaceDocument, referenceGroup) {
    const configuredClassNames = new Set(referencedClasses(workspaceDocument, referenceGroup));
    return (sourceFiles || []).filter((sourceFile) => configuredClassNames.has(sourceClassName(sourceFile)));
}

export function sourceFilesNotInReferenceSet(sourceFiles, workspaceDocument, referenceGroup) {
    const configuredClassNames = new Set(referencedClasses(workspaceDocument, referenceGroup));
    return (sourceFiles || []).filter((sourceFile) => !configuredClassNames.has(sourceClassName(sourceFile)));
}

export function policyInterfaceSimpleName(propertyKey) {
    const interfaceByPropertyKey = {
        clickablePolicies: "ClickablePolicy",
        typeablePolicies: "TypeablePolicy",
        scrollablePolicies: "ScrollablePolicy",
        selectablePolicies: "SelectablePolicy",
        enabledPolicies: "EnabledPolicy",
        blockedPolicies: "BlockedPolicy",
        widgetFilterPolicies: "WidgetFilterPolicy",
        visiblePolicies: "VisiblePolicy",
        topLevelPolicies: "TopLevelPolicy"
    };

    return interfaceByPropertyKey[propertyKey] || "";
}

export function inferPolicyPropertyKey(sourceFile, policyDefinitions = []) {
    const className = sourceClassName(sourceFile);
    for (const policyDefinition of policyDefinitions || []) {
        if ((policyDefinition.configuredClassNames || []).includes(className)) {
            return policyDefinition.propertyKey;
        }
    }

    const content = sourceFile?.content || "";
    for (const policyDefinition of policyDefinitions || []) {
        const interfaceName = policyInterfaceSimpleName(policyDefinition.propertyKey);
        if (interfaceName && content.includes(`implements ${interfaceName}`)) {
            return policyDefinition.propertyKey;
        }
    }

    return "";
}

export function updatedPolicyPropertiesContent(properties, propertyKey, className, enablePolicy) {
    const existingValues = (properties[propertyKey] || "")
        .split(";")
        .map((value) => value.trim())
        .filter((value) => value !== "" && value !== className);

    if (enablePolicy) {
        existingValues.push(className);
    }

    return {
        ...properties,
        [propertyKey]: existingValues.join("; ")
    };
}
