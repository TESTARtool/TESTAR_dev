import fs from "node:fs";
import path from "node:path";

const repositoryRoot = path.resolve(import.meta.dirname, "../..");
const webstudioDocsRoot = path.resolve(repositoryRoot, "docs/webstudio");
const tracePath = path.join(webstudioDocsRoot, "WEBSTUDIO_SPEC_TRACE.md");
const specificationPaths = [
    path.join(webstudioDocsRoot, "WEBSTUDIO_FUNCTIONAL_SPEC.md"),
    path.join(webstudioDocsRoot, "WEBSTUDIO_UX_SPEC.md")
];

const scannedCodeRoots = [
    path.join(repositoryRoot, "webstudio/src"),
    path.join(repositoryRoot, "webstudio/test"),
    path.join(repositoryRoot, "webstudio/frontend/src"),
    path.join(repositoryRoot, "webstudio/frontend/test")
];

const requirementPattern = /\bWS-(?:FUNC|UX)-[A-Z0-9-]+-\d+\b/g;
const traceabilityPattern = /Traceability:\s*\[`(WS-(?:FUNC|UX)-[A-Z0-9-]+-\d+)`\]\(([^)]+)\)/g;
const traceHeadingPattern = /^##\s+(WS-(?:FUNC|UX)-[A-Z0-9-]+-\d+)\s+[—-]\s+(.+)$/gm;
const markdownLinkPattern = /\[[^\]]+\]\(([^)]+)\)/g;

function readText(filePath) {
    return fs.readFileSync(filePath, "utf8");
}

function fail(message) {
    errors.push(message);
}

function collectTraceabilityIds() {
    const ids = [];
    for (const specificationPath of specificationPaths) {
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
        title: match[2]
    }));
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
        fail(`Duplicate ${context} requirement ID: ${duplicate}`);
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

const errors = [];
const traceText = readText(tracePath);
const traceabilityEntries = collectTraceabilityIds();
const specificationIds = traceabilityEntries.map((entry) => entry.id);
const traceEntries = collectTraceEntries(traceText);
const traceIds = new Set(traceEntries.map((entry) => entry.id));
const codeIds = collectRequirementIdsFromCode();

validateDuplicateIds(specificationIds, "specification");
validateDuplicateIds(traceEntries.map((entry) => entry.id), "trace");
validateTraceLinks(traceText);

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

console.log(`WebStudio spec trace validation passed for ${specificationIds.length} requirement IDs.`);
