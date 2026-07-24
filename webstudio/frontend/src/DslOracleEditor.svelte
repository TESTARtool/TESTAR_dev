<script context="module">
    let testarDslLanguageRegistered = false;
    let monacoModulePromise = null;

    function loadMonaco() {
        if (!monacoModulePromise) {
            monacoModulePromise = Promise.all([
                import("monaco-editor/editor/editor.api"),
                import("monaco-editor/editor/contrib/suggest/browser/suggestController"),
                import("monaco-editor/editor/contrib/snippet/browser/snippetController2"),
                import("monaco-editor/editor/contrib/hover/browser/hoverContribution")
            ]).then(([monaco]) => monaco);
        }

        return monacoModulePromise;
    }
</script>

<script>
    import { onMount } from "svelte";
    import editorWorker from "monaco-editor/editor/editor.worker?worker";
    import {
        normalizedDslOracleMetadata,
        dslCompletionGroupsForContext,
        dslLocalDiagnostics,
        dslMonacoMarkerData
    } from "./testOraclesModel.js";

    export let value = "";
    export let disabled = false;
    export let dslMetadata = null;
    export let serverDiagnostics = [];
    export let onChange = () => {};

    let editorContainer;
    let editor;
    let model;
    let monacoApi;
    let applyingExternalValue = false;

    $: normalizedMetadata = normalizedDslOracleMetadata(dslMetadata);

    if (typeof self !== "undefined" && !self.MonacoEnvironment) {
        self.MonacoEnvironment = {
            getWorker() {
                return new editorWorker();
            }
        };
    }

    $: if (model && value !== model.getValue()) {
        applyingExternalValue = true;
        model.setValue(value || "");
        applyingExternalValue = false;
        updateMarkers();
    }

    $: if (editor) {
        editor.updateOptions({
            readOnly: disabled
        });
    }

    $: if (model) {
        serverDiagnostics;
        updateMarkers();
    }

    onMount(() => {
        let disposed = false;

        loadMonaco().then((loadedMonaco) => {
            if (disposed) {
                return;
            }

            monacoApi = loadedMonaco;
            registerTestarDslLanguage(monacoApi);

            model = monacoApi.editor.createModel(value || "", "testar-dsl");
            editor = monacoApi.editor.create(editorContainer, {
                model,
                automaticLayout: true,
                fontFamily: "\"JetBrains Mono\", \"Cascadia Code\", monospace",
                fontSize: 13,
                lineNumbers: "on",
                minimap: {
                    enabled: false
                },
                quickSuggestions: {
                    other: true,
                    comments: false,
                    strings: true
                },
                readOnly: disabled,
                scrollBeyondLastLine: false,
                suggest: {
                    showFields: true,
                    showKeywords: true,
                    showSnippets: true,
                    showWords: false
                },
                suggestOnTriggerCharacters: true,
                theme: "testar-oracle",
                wordBasedSuggestions: "off"
            });

            const changeSubscription = model.onDidChangeContent(() => {
                if (!applyingExternalValue) {
                    onChange(model.getValue());
                }
                updateMarkers();
            });

            editor.addCommand(
                monacoApi.KeyMod.CtrlCmd | monacoApi.KeyCode.Space,
                () => editor.trigger("keyboard", "editor.action.triggerSuggest", {})
            );

            editor.onDidDispose(() => {
                changeSubscription.dispose();
            });

            updateMarkers();
        });

        return () => {
            disposed = true;
            if (editor) {
                editor.dispose();
            }
            if (model) {
                model.dispose();
            }
        };
    });

    function registerTestarDslLanguage(monaco) {
        if (testarDslLanguageRegistered) {
            return;
        }

        monaco.languages.register({
            id: "testar-dsl"
        });

        monaco.languages.setMonarchTokensProvider("testar-dsl", {
            keywords: normalizedMetadata.keywords,
            elementTypes: normalizedMetadata.widgetTypes,
            fields: normalizedMetadata.fieldNames,
            tokenizer: {
                root: [
                    [/\/\/.*$/, "comment"],
                    [/"([^"\\]|\\.)*$/, "string.invalid"],
                    [/"([^"\\]|\\.)*"/, "string"],
                    [/\b\d+\b/, "number"],
                    [/[a-zA-Z_]\w*/, {
                        cases: {
                            "@keywords": "keyword",
                            "@elementTypes": "type.identifier",
                            "@fields": "attribute.name",
                            "@default": "identifier"
                        }
                    }],
                    [/[{}()[\]]/, "@brackets"],
                    [/[;,.]/, "delimiter"],
                    [/[@`]/, "invalid"]
                ]
            }
        });

        monaco.languages.setLanguageConfiguration("testar-dsl", {
            comments: {
                lineComment: "//"
            },
            brackets: [
                ["{", "}"],
                ["[", "]"],
                ["(", ")"]
            ],
            autoClosingPairs: [
                { open: "{", close: "}" },
                { open: "[", close: "]" },
                { open: "(", close: ")" },
                { open: "\"", close: "\"" }
            ],
            surroundingPairs: [
                { open: "{", close: "}" },
                { open: "[", close: "]" },
                { open: "(", close: ")" },
                { open: "\"", close: "\"" }
            ],
            wordPattern: /[A-Za-z_]\w*/g
        });

        monaco.languages.registerCompletionItemProvider("testar-dsl", {
            triggerCharacters: [" ", ".", ",", "\""],
            provideCompletionItems(monacoModel, position) {
                const completionGroups = dslCompletionGroupsForContext(
                    monacoModel.getValue(),
                    position.lineNumber,
                    position.column,
                    normalizedMetadata
                );
                const word = monacoModel.getWordUntilPosition(position);
                const range = {
                    startLineNumber: position.lineNumber,
                    endLineNumber: position.lineNumber,
                    startColumn: word.startColumn,
                    endColumn: word.endColumn
                };

                return {
                    suggestions: [
                        ...completionItems(completionGroups.keywords, monaco.languages.CompletionItemKind.Keyword, range),
                        ...completionItems(completionGroups.widgetTypes, monaco.languages.CompletionItemKind.Class, range),
                        ...completionItems(completionGroups.fieldNames, monaco.languages.CompletionItemKind.Field, range),
                        ...completionItems(completionGroups.locales, monaco.languages.CompletionItemKind.Value, range),
                        ...snippetItems(monaco, range, normalizedMetadata, completionGroups.snippets)
                    ]
                };
            }
        });

        monaco.editor.defineTheme("testar-oracle", {
            base: "vs",
            inherit: true,
            rules: [
                { token: "keyword", foreground: "A53534", fontStyle: "bold" },
                { token: "type.identifier", foreground: "0F5B78", fontStyle: "bold" },
                { token: "attribute.name", foreground: "7B5E00" },
                { token: "string", foreground: "237A3B" },
                { token: "string.invalid", foreground: "A53534" },
                { token: "invalid", foreground: "A53534", fontStyle: "bold underline" },
                { token: "comment", foreground: "6B7280", fontStyle: "italic" }
            ],
            colors: {
                "editor.background": "#fffaf4",
                "editor.lineHighlightBackground": "#fff1df"
            }
        });

        testarDslLanguageRegistered = true;
    }

    function completionItems(values, kind, range) {
        return values.map((item) => ({
            label: item,
            kind,
            insertText: item,
            range
        }));
    }

    function snippetItems(monaco, range, metadata, enabled) {
        if (!enabled) {
            return [];
        }

        const widgetType = metadata.widgetTypes[0];
        const fieldName = metadata.fieldNames[0];
        if (!widgetType || !fieldName) {
            return [];
        }

        return [
            {
                label: "assert for all",
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: `assert for all \${1:${widgetType}}\n  it.\${2:${fieldName}}\n  "\${3:DSL: describe the invariant}".`,
                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                documentation: "Create a for-all DSL oracle assertion.",
                range
            },
            {
                label: "conditional assert",
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: `assert \${1:${widgetType} "Widget label"} is \${2:${fieldName}}\n  "\${3:DSL: describe the invariant}".`,
                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                documentation: "Create a conditional DSL oracle assertion.",
                range
            },
            {
                label: "package",
                kind: monaco.languages.CompletionItemKind.Snippet,
                insertText: "package dsl_generated.${1:oracle_name};",
                insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
                documentation: "Declare the generated Java package.",
                range
            }
        ];
    }

    function updateMarkers() {
        if (!monacoApi || !model) {
            return;
        }

        const diagnostics = [
            ...dslLocalDiagnostics(model.getValue(), normalizedMetadata),
            ...serverDiagnostics
        ];
        const markers = dslMonacoMarkerData(diagnostics).map((marker) => ({
            ...marker,
            severity: markerSeverity(marker.severity)
        }));

        monacoApi.editor.setModelMarkers(model, "testar-dsl", markers);
    }

    function markerSeverity(severity) {
        if (severity === "ERROR") {
            return monacoApi.MarkerSeverity.Error;
        }

        if (severity === "WARNING") {
            return monacoApi.MarkerSeverity.Warning;
        }

        return monacoApi.MarkerSeverity.Info;
    }
</script>

<div class="oracle-monaco-editor" bind:this={editorContainer}></div>
