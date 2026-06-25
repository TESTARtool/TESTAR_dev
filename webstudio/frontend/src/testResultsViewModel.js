const verdictPattern = /_V\d+_([^.]+)\.html$/i;
const verdictColorPalette = [
    "#d44949",
    "#f47b32",
    "#f3bc2f",
    "#64a83f",
    "#28b4b7",
    "#2f85dd",
    "#5a6fc7",
    "#7d5bd6",
    "#a95ad8",
    "#b6577e",
    "#8b6f61",
    "#6f7d8c"
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
            count
        }))
        .sort((left, right) => right.count - left.count || left.label.localeCompare(right.label))
        .map((group, index) => ({
            ...group,
            color: verdictColorPalette[index % verdictColorPalette.length]
        }));

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

