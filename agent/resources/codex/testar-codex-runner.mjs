import { mkdir, readFile, writeFile } from "node:fs/promises";
import { createWriteStream } from "node:fs";
import path from "node:path";
import process from "node:process";
import { Codex } from "@openai/codex-sdk";

function normalizeValue(value) {
    if (value === null || value === undefined) {
        return undefined;
    }

    if (typeof value === "string" && value.trim() === "") {
        return undefined;
    }

    return value;
}

function compactText(text) {
    if (!text) {
        return "";
    }

    return String(text).replace(/\s+/g, " ").trim();
}

function firstOutputLine(text) {
    const compact = compactText(text);
    if (!compact) {
        return "";
    }

    return compact.length > 220 ? compact.slice(0, 220) + "..." : compact;
}

function itemSummary(item) {
    if (!item) {
        return {};
    }

    if (item.type === "command_execution") {
        return {
            command: item.command,
            exitCode: item.exit_code ?? null,
            status: item.status
        };
    }

    if (item.type === "mcp_tool_call") {
        return {
            server: item.server,
            tool: item.tool,
            status: item.status,
            error: item.error?.message ?? ""
        };
    }

    if (item.type === "file_change") {
        return {
            status: item.status,
            changes: item.changes ?? []
        };
    }

    if (item.type === "agent_message" || item.type === "reasoning" || item.type === "error") {
        return {
            text: item.text ?? item.message ?? ""
        };
    }

    if (item.type === "todo_list") {
        return {
            items: item.items ?? []
        };
    }

    if (item.type === "web_search") {
        return {
            query: item.query ?? ""
        };
    }

    return {};
}

function logEvent(event) {
    if (event.type === "thread.started") {
        console.log("[THREAD] started " + (event.thread_id ?? ""));
        return;
    }

    if (event.type === "turn.started") {
        console.log("[TURN] started");
        return;
    }

    if (event.type === "turn.completed") {
        const usage = event.usage ?? {};
        console.log(
                "[TURN] completed tokens"
                + " in=" + (usage.input_tokens ?? 0)
                + " cached=" + (usage.cached_input_tokens ?? 0)
                + " out=" + (usage.output_tokens ?? 0)
                + " reasoning=" + (usage.reasoning_output_tokens ?? 0)
        );
        return;
    }

    if (event.type === "turn.failed") {
        console.log("[TURN] failed " + compactText(event.error?.message ?? "Turn failed."));
        return;
    }

    if (event.type === "error") {
        console.log("[ERROR] " + compactText(event.message ?? "Stream error."));
        return;
    }

    if (event.type === "item.started" && event.item?.type === "command_execution") {
        console.log("[CMD] start " + compactText(event.item.command ?? ""));
        return;
    }

    if (event.type !== "item.completed") {
        return;
    }

    const item = event.item;
    if (!item) {
        return;
    }

    if (item.type === "agent_message") {
        console.log("[MSG] " + firstOutputLine(item.text));
        return;
    }

    if (item.type === "reasoning") {
        console.log("[REASON] " + firstOutputLine(item.text));
        return;
    }

    if (item.type === "command_execution") {
        const commandLine = compactText(item.command ?? "");
        const prefix = item.status === "completed" ? "[CMD] completed" : "[CMD] failed";
        console.log(prefix + " exit=" + (item.exit_code ?? "") + " " + commandLine);
        const output = firstOutputLine(item.aggregated_output);
        if (output) {
            console.log("[CMD-OUT] " + output);
        }
        return;
    }

    if (item.type === "mcp_tool_call") {
        const prefix = item.status === "completed" ? "[MCP] completed" : "[MCP] failed";
        console.log(prefix + " " + compactText(item.server ?? "") + " :: " + compactText(item.tool ?? ""));
        if (item.error?.message) {
            console.log("[MCP-ERR] " + firstOutputLine(item.error.message));
        }
        return;
    }

    if (item.type === "file_change") {
        console.log("[FILE] " + compactText(item.status ?? "") + " changes=" + ((item.changes ?? []).length));
        return;
    }

    if (item.type === "todo_list") {
        console.log("[TODO] updated items=" + ((item.items ?? []).length));
        return;
    }

    if (item.type === "web_search") {
        console.log("[WEB] " + compactText(item.query ?? ""));
        return;
    }

    if (item.type === "error") {
        console.log("[ITEM-ERR] " + firstOutputLine(item.message));
    }
}

async function main() {
    const configPath = process.argv[2];
    if (!configPath) {
        throw new Error("Missing config path argument.");
    }

    const configText = await readFile(configPath, "utf8");
    const config = JSON.parse(configText);
    const outputDirectory = path.resolve(config.outputDirectory ?? ".");
    await mkdir(outputDirectory, { recursive: true });

    const eventsPath = path.join(outputDirectory, "events.jsonl");
    const eventsStream = createWriteStream(eventsPath, { flags: "a" });

    const result = {
        status: "running",
        startedAt: new Date().toISOString(),
        finishedAt: "",
        threadId: "",
        model: config.model ?? "",
        workingDirectory: config.workingDirectory ?? "",
        sandboxMode: config.sandboxMode ?? "",
        approvalPolicy: config.approvalPolicy ?? "",
        reasoningEffort: config.reasoningEffort ?? "",
        prompt: config.prompt ?? "",
        finalResponse: "",
        usage: null,
        completedItems: [],
        errorMessage: ""
    };

    try {
        const apiKeyEnvVarName = normalizeValue(config.apiKeyEnvVarName) ?? "OPENAI_API_KEY";
        const apiKey = normalizeValue(process.env[apiKeyEnvVarName]);
        if (!apiKey) {
            throw new Error("Missing API key environment variable: " + apiKeyEnvVarName);
        }

        const codex = new Codex({
            apiKey,
            baseUrl: normalizeValue(config.baseUrl)
        });

        const thread = codex.startThread({
            model: normalizeValue(config.model),
            sandboxMode: normalizeValue(config.sandboxMode),
            workingDirectory: normalizeValue(config.workingDirectory),
            skipGitRepoCheck: Boolean(config.skipGitRepoCheck),
            modelReasoningEffort: normalizeValue(config.reasoningEffort),
            networkAccessEnabled: Boolean(config.networkAccessEnabled),
            approvalPolicy: normalizeValue(config.approvalPolicy),
            additionalDirectories: Array.isArray(config.additionalDirectories)
                    ? config.additionalDirectories
                    : undefined
        });

        const streamedTurn = await thread.runStreamed(config.prompt ?? "");
        for await (const event of streamedTurn.events) {
            eventsStream.write(JSON.stringify(event) + "\n");
            logEvent(event);

            if (event.type === "thread.started") {
                result.threadId = event.thread_id ?? "";
                continue;
            }

            if (event.type === "turn.completed") {
                result.usage = event.usage ?? null;
                continue;
            }

            if (event.type === "turn.failed") {
                result.status = "failed";
                result.errorMessage = event.error?.message ?? "Turn failed.";
                continue;
            }

            if (event.type === "error") {
                result.status = "failed";
                result.errorMessage = event.message ?? "Stream error.";
                continue;
            }

            if (event.type === "item.completed") {
                result.completedItems.push({
                    id: event.item.id,
                    type: event.item.type,
                    ...itemSummary(event.item)
                });

                if (event.item.type === "agent_message") {
                    result.finalResponse = event.item.text ?? result.finalResponse;
                }
            }
        }

        if (result.status !== "failed") {
            result.status = "completed";
        }
    } catch (error) {
        result.status = "failed";
        result.errorMessage = error instanceof Error ? error.message : String(error);
        console.error(result.errorMessage);
    } finally {
        result.finishedAt = new Date().toISOString();
        await writeFile(
                path.join(outputDirectory, "result.json"),
                JSON.stringify(result, null, 4),
                "utf8"
        );
        eventsStream.end();
    }

    if (result.status !== "completed") {
        process.exitCode = 1;
    }
}

main().catch((error) => {
    console.error(error instanceof Error ? error.stack ?? error.message : String(error));
    process.exitCode = 1;
});
