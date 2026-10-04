export function isSettingDisabled(setting, settingsGroups) {
    if (setting.key !== "StateModelExportStaticGraphIncludeWidgetTrees") {
        return false;
    }

    return !settingsGroups?.some((group) => group.settings.some((candidate) =>
        candidate.key === "StateModelExportStaticGraph" && candidate.value === "true"));
}
