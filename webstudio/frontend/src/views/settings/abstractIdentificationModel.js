export function selectedAbstractStateTags(value) {
    return new Set((value || "").split(",").map((key) => key.trim()).filter(Boolean));
}

export function updatedAbstractStateTags(value, key, checked) {
    const selected = selectedAbstractStateTags(value);
    if (checked) {
        selected.add(key);
    } else {
        selected.delete(key);
    }
    return [...selected].join(",");
}

export function defaultAbstractStateTags(options) {
    return options.filter((option) => option.defaultSelected)
        .map((option) => option.key)
        .join(",");
}
