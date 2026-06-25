export function shouldShowBlankSelectOption(setting) {
    if (setting?.type === "enum") {
        return false;
    }

    if (setting?.options?.length > 0) {
        return false;
    }

    return true;
}
