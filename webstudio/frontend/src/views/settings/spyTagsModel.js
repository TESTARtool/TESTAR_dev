// Implements WS-UX-TEST-SETTINGS-004: keeps Spy tag edits in the settings draft.
export function selectedSpyTags(value) {
    return new Set((value || "").split(";").map((name) => name.trim()).filter(Boolean));
}

export function updateSpyTags(value, names, included) {
    const selected = selectedSpyTags(value);

    for (const name of names) {
        if (included) {
            selected.add(name);
        } else {
            selected.delete(name);
        }
    }

    return [...selected].sort().join(";");
}

export function defaultSpyTags(options) {
    return options.filter((option) => option.defaultSelected)
        .map((option) => option.key)
        .sort()
        .join(";");
}

export function availableSpyTags(options, value) {
    const knownNames = new Set(options.map((option) => option.key));
    const unknownOptions = [...selectedSpyTags(value)]
        .filter((name) => !knownNames.has(name))
        .map((key) => ({ key, group: "Other", defaultSelected: false }));

    return [...options, ...unknownOptions];
}
