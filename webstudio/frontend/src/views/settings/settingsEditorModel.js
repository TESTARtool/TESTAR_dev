// Implements WS-FUNC-TEST-SETTINGS-001 and WS-UX-TEST-SETTINGS-001:
// converts visual setting edits into test.settings content and setting-level feedback state.
function escapeRegExp(text) {
    return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

export function shouldPersistVisualSetting(setting) {
    const settingValue = (setting?.value ?? "").trim();
    const settingType = setting?.type || "string";

    if (settingValue !== "") {
        return true;
    }

    return settingType === "string" || settingType === "list";
}

export function buildTestSettingsContent(currentContent, settingsGroups) {
    const settingsEntries = [];

    for (const settingsGroup of settingsGroups || []) {
        for (const setting of settingsGroup.settings || []) {
            settingsEntries.push({
                key: setting.key,
                value: setting.value ?? "",
                persist: shouldPersistVisualSetting(setting)
            });
        }
    }

    let nextContent = currentContent || "";
    const missingEntries = [];

    for (const settingEntry of settingsEntries) {
        const key = settingEntry.key;
        const value = settingEntry.value;
        const escapedKey = escapeRegExp(key);
        const propertyPattern = new RegExp(`^\\s*${escapedKey}\\s*=.*(?:\\r?\\n|$)`, "gm");

        if (settingEntry.persist) {
            if (propertyPattern.test(nextContent)) {
                nextContent = nextContent.replace(propertyPattern, `${key} = ${value}\n`);
            } else {
                missingEntries.push(`${key} = ${value}`);
            }
        } else {
            nextContent = nextContent.replace(propertyPattern, "");
        }
    }

    if (missingEntries.length > 0) {
        if (nextContent.length === 0) {
            nextContent = `${missingEntries.join("\n")}\n`;
        } else {
            const separator = nextContent.endsWith("\n") ? "" : "\n";
            nextContent = `${nextContent}${separator}\n${missingEntries.join("\n")}\n`;
        }
    }

    return nextContent;
}

export function canRestoreSettingDefault(setting) {
    if (!setting?.key) {
        return false;
    }

    if (setting.key === "SuspiciousTags" || setting.key === "SuspiciousProcessOutput") {
        return (setting.value || "").trim() === "";
    }

    return true;
}

export function restoredSettingDefaultValue(setting) {
    return canRestoreSettingDefault(setting) ? setting.defaultValue || "" : setting?.value;
}

export function updatedRegexValidationResults(validationResults, settingKey, validationResult) {
    return {
        ...(validationResults || {}),
        [settingKey]: validationResult
    };
}
