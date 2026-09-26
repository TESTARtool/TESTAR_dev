import test from "node:test";
import assert from "node:assert/strict";
import { existsSync } from "node:fs";
import { join } from "node:path";
import { fileURLToPath } from "node:url";
import { aboutPartners, aboutResearchProjects } from "../../src/app/aboutProject.js";

const publicDirectory = fileURLToPath(new URL("../../public/", import.meta.url));

test("about dialog includes the legacy research partner and project logos", () => {
    assert.equal(aboutPartners.length, 2);
    assert.equal(aboutResearchProjects.length, 7);
    assert.equal(existsSync(join(publicDirectory, "about/testar_logo.png")), true);

    for (const logo of [...aboutPartners, ...aboutResearchProjects]) {
        assert.ok(logo.name);
        assert.equal(existsSync(join(publicDirectory, logo.logo.slice(1))), true, `Missing ${logo.logo}`);
    }
});
