import fs from "node:fs";
import path from "node:path";

const repositoryRoot = path.resolve(import.meta.dirname, "../..");
const webstudioDocsRoot = path.resolve(repositoryRoot, "docs/webstudio");
const tracePath = path.join(webstudioDocsRoot, "WEBSTUDIO_SPEC_TRACE.md");
const requirementSpecificationPaths = [
    path.join(webstudioDocsRoot, "WEBSTUDIO_FUNCTIONAL_SPEC.md"),
    path.join(webstudioDocsRoot, "WEBSTUDIO_UX_SPEC.md")
];
const acceptanceScenarioPath = path.join(webstudioDocsRoot, "WEBSTUDIO_ACCEPTANCE_SCENARIOS.md");

const scannedCodeRoots = [
    path.join(repositoryRoot, "webstudio/src"),
    path.join(repositoryRoot, "webstudio/test"),
    path.join(repositoryRoot, "webstudio/frontend/src"),
    path.join(repositoryRoot, "webstudio/frontend/test")
];

const requirementPattern = /\bWS-(?:FUNC|UX)-[A-Z0-9-]+-\d+\b/g;
const scenarioPattern = /\bWS-SCENARIO-[A-Z0-9-]+-\d+\b/g;
const traceabilityPattern = /Traceability:\s*\[`(WS-(?:FUNC|UX)-[A-Z0-9-]+-\d+)`\]\(([^)]+)\)/g;
const traceHeadingPattern = /^##\s+(WS-(?:FUNC|UX)-[A-Z0-9-]+-\d+)\s+[-\u2013\u2014]\s+(.+)$/gm;
const markdownLinkPattern = /\[[^\]]+\]\(([^)]+)\)/g;

const errors = [];
const warnings = [];

function readText(filePath) {
    return fs.readFileSync(filePath, "utf8");
}

function fail(message) {
    errors.push(message);
}

function warn(message) {
    warnings.push(message);
}

function collectTraceabilityIds() {
    const ids = [];
    for (const specificationPath of requirementSpecificationPaths) {
        const text = readText(specificationPath);
        for (const match of text.matchAll(traceabilityPattern)) {
            ids.push({
                id: match[1],
                link: match[2],
                file: specificationPath
            });
        }
    }

    return ids;
}

function collectTraceEntries(traceText) {
    return Array.from(traceText.matchAll(traceHeadingPattern)).map((match) => ({
        id: match[1],
        title: match[2],
        content: traceEntryContent(traceText, match.index)
    }));
}

function traceEntryContent(traceText, headingIndex) {
    const nextHeadingIndex = traceText.indexOf("\n## ", headingIndex + 1);
    if (nextHeadingIndex === -1) {
        return traceText.slice(headingIndex);
    }

    return traceText.slice(headingIndex, nextHeadingIndex);
}

function collectRequirementIdsFromCode() {
    const ids = new Set();
    for (const root of scannedCodeRoots) {
        if (!fs.existsSync(root)) {
            continue;
        }

        for (const filePath of walkFiles(root)) {
            if (!isSourceLikeFile(filePath)) {
                continue;
            }

            const text = readText(filePath);
            for (const match of text.matchAll(requirementPattern)) {
                ids.add(match[0]);
            }
        }
    }

    return ids;
}

function collectScenarioIds() {
    if (!fs.existsSync(acceptanceScenarioPath)) {
        return [];
    }

    return Array.from(readText(acceptanceScenarioPath).matchAll(scenarioPattern))
        .map((match) => match[0]);
}

function collectScenarioBlocks() {
    if (!fs.existsSync(acceptanceScenarioPath)) {
        return [];
    }

    const text = readText(acceptanceScenarioPath);
    const scenarioHeadingPattern = /^###\s+(WS-SCENARIO-[A-Z0-9-]+-\d+)\s+[-\u2013\u2014]\s+(.+)$/gm;
    return Array.from(text.matchAll(scenarioHeadingPattern)).map((match) => ({
        id: match[1],
        title: match[2],
        content: scenarioContent(text, match.index)
    }));
}

function scenarioContent(text, headingIndex) {
    const nextHeadingIndex = text.indexOf("\n### ", headingIndex + 1);
    if (nextHeadingIndex === -1) {
        return text.slice(headingIndex);
    }

    return text.slice(headingIndex, nextHeadingIndex);
}

function* walkFiles(root) {
    for (const entry of fs.readdirSync(root, { withFileTypes: true })) {
        const entryPath = path.join(root, entry.name);
        if (entry.isDirectory()) {
            yield* walkFiles(entryPath);
        } else if (entry.isFile()) {
            yield entryPath;
        }
    }
}

function isSourceLikeFile(filePath) {
    return [".java", ".js", ".mjs", ".svelte"].includes(path.extname(filePath));
}

function validateDuplicateIds(ids, context) {
    const seen = new Set();
    const duplicates = new Set();
    for (const id of ids) {
        if (seen.has(id)) {
            duplicates.add(id);
        }
        seen.add(id);
    }

    for (const duplicate of duplicates) {
        fail(`Duplicate ${context} ID: ${duplicate}`);
    }
}

function validateTraceLinks(traceText) {
    for (const match of traceText.matchAll(markdownLinkPattern)) {
        const link = match[1];
        if (link.startsWith("#") || /^[a-z]+:/i.test(link)) {
            continue;
        }

        const [relativePath] = link.split("#");
        const linkedPath = path.resolve(webstudioDocsRoot, relativePath);
        if (!fs.existsSync(linkedPath)) {
            fail(`Trace link points to a missing file: ${link}`);
        }
    }
}

function validateTraceCompleteness(traceEntries) {
    for (const traceEntry of traceEntries) {
        if (!traceEntry.content.includes("**Unit tests**")) {
            warn(`Trace entry has no Unit tests section: ${traceEntry.id}`);
        }

        if (traceEntry.content.includes("**Unit tests**")
            && !traceEntry.content.match(/\*\*Unit tests\*\*[\s\S]*?-\s+\[[^\]]+\]\(/)) {
            warn(`Trace entry has no mapped unit test files: ${traceEntry.id}`);
        }

        if (!traceEntry.content.includes("**Frontend implementation**")
            && !traceEntry.content.includes("**Backend implementation**")) {
            warn(`Trace entry has no implementation section: ${traceEntry.id}`);
        }
    }
}

function validateScenarioVerification(scenarioBlocks) {
    for (const scenarioBlock of scenarioBlocks) {
        if (!scenarioBlock.content.match(/Verification:\s*`[^`]+`/)) {
            warn(`Acceptance scenario has no Verification line: ${scenarioBlock.id}`);
        }
    }
}

const traceText = readText(tracePath);
const traceabilityEntries = collectTraceabilityIds();
const specificationIds = traceabilityEntries.map((entry) => entry.id);
const traceEntries = collectTraceEntries(traceText);
const traceIds = new Set(traceEntries.map((entry) => entry.id));
const codeIds = collectRequirementIdsFromCode();
const scenarioIds = collectScenarioIds();
const scenarioBlocks = collectScenarioBlocks();

validateDuplicateIds(specificationIds, "specification requirement");
validateDuplicateIds(traceEntries.map((entry) => entry.id), "trace requirement");
validateDuplicateIds(scenarioIds, "acceptance scenario");
validateTraceLinks(traceText);
validateTraceCompleteness(traceEntries);
validateScenarioVerification(scenarioBlocks);

for (const entry of traceabilityEntries) {
    if (!traceIds.has(entry.id)) {
        fail(`Specification requirement has no trace entry: ${entry.id}`);
    }

    const expectedTraceAnchor = entry.id.toLowerCase();
    if (!entry.link.includes(expectedTraceAnchor)) {
        fail(`Specification requirement ${entry.id} does not link to its trace entry.`);
    }
}

for (const traceId of traceIds) {
    if (!specificationIds.includes(traceId)) {
        fail(`Trace entry has no specification Traceability link: ${traceId}`);
    }
}

for (const codeId of codeIds) {
    if (!specificationIds.includes(codeId)) {
        fail(`Code or test marker uses requirement ID missing from specifications: ${codeId}`);
    }
}

if (errors.length > 0) {
    console.error("WebStudio spec trace validation failed:");
    for (const error of errors) {
        console.error(`- ${error}`);
    }
    process.exit(1);
}

if (warnings.length > 0) {
    console.warn("WebStudio spec trace validation warnings:");
    for (const warning of warnings) {
        console.warn(`- ${warning}`);
    }
}

console.log(`WebStudio spec trace validation passed for ${specificationIds.length} requirement IDs and ${scenarioIds.length} acceptance scenario IDs.`);
