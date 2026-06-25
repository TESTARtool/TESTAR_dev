const verdictPattern = /_V\d+_([^.]+)\.html$/i;
const verdictColorPalette = [
    "#c26a2d",
    "#d44949",
    "#7d5bd6",
    "#2b7a9a",
    "#8f4da8",
    "#d27b2c",
    "#4f7d47",
    "#9c4f36",
    "#5a6fc7",
    "#b6577e"
];

export function formatResultFileLabel(fileName) {
    if (!fileName) {
        return "";
    }

    const trimmedExtension = fileName.replace(/\.html?$/i, "");
    const sequenceIndex = trimmedExtension.indexOf("_sequence_");
    if (sequenceIndex >= 0) {
        return trimmedExtension.substring(sequenceIndex + 1);
    }

    return trimmedExtension;
}

export function summarizeResultGroup(resultGroup) {
    if (!resultGroup) {
        return {
            okCount: 0,
            failedSequenceCount: 0,
            failedVerdictCount: 0,
            verdictGroups: []
        };
    }

    const verdictCounts = new Map();
    const failedSequences = new Set();
    let explicitOkCount = 0;

    for (const resultFile of resultGroup.files || []) {
        const verdictMatch = resultFile.name.match(verdictPattern);
        if (!verdictMatch) {
            continue;
        }

        const verdictKey = verdictMatch[1];
        if (verdictKey === "OK") {
            explicitOkCount += 1;
            continue;
        }

        verdictCounts.set(verdictKey, (verdictCounts.get(verdictKey) || 0) + 1);

        const sequenceMatch = resultFile.name.match(/_sequence_(\d+)/i);
        if (sequenceMatch) {
            failedSequences.add(sequenceMatch[1]);
        }
    }

    const totalSequences = resultGroup.totalSequenceCount || 0;
    const okCount = Math.max(explicitOkCount, Math.max(0, totalSequences - failedSequences.size));
    const verdictGroups = Array.from(verdictCounts.entries())
        .map(([key, count]) => ({
            key,
            label: key,
            count,
            color: colorForVerdict(key)
        }))
        .sort((left, right) => right.count - left.count || left.label.localeCompare(right.label));

    return {
        okCount,
        failedSequenceCount: failedSequences.size,
        failedVerdictCount: verdictGroups.reduce((total, group) => total + group.count, 0),
        verdictGroups
    };
}

export function sortedResultGroups(groups, sortMode = "latest") {
    const sortedGroups = [...(groups || [])];
    sortedGroups.sort((left, right) => {
        if (sortMode === "oldest") {
            return left.name.localeCompare(right.name);
        }

        if (sortMode === "name") {
            return left.name.localeCompare(right.name);
        }

        return right.name.localeCompare(left.name);
    });

    return sortedGroups;
}

export function filterResultGroups(groups, filterMode = "all") {
    return (groups || []).filter((group) => {
        if (filterMode === "failed") {
            return group.status === "failed";
        }

        if (filterMode === "ok") {
            return group.status !== "failed";
        }

        return true;
    });
}

export function filterResultFiles(files, filterMode = "all") {
    return (files || []).filter((file) => {
        if (filterMode === "failed") {
            return file.status === "failed";
        }

        if (filterMode === "ok") {
            return file.status !== "failed";
        }

        return true;
    });
}

function colorForVerdict(verdictKey) {
    if (!verdictKey) {
        return verdictColorPalette[0];
    }

    let hash = 0;
    for (let index = 0; index < verdictKey.length; index += 1) {
        hash = ((hash << 5) - hash) + verdictKey.charCodeAt(index);
        hash |= 0;
    }

    return verdictColorPalette[Math.abs(hash) % verdictColorPalette.length];
}
