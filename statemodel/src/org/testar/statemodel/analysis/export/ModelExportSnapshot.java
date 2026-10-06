/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis.export;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testar.statemodel.analysis.AnalysisManager;
import org.testar.statemodel.analysis.jsonformat.Element;

/** Prepares credential-free graph data, property inventories, and explicitly requested artifacts. */
public final class ModelExportSnapshot {

    private ModelExportSnapshot() { }

    public static ObjectNode read(AnalysisManager analysis, Path graphRoot, String modelIdentifier,
                                  ModelExportOptions options) throws IOException {
        return read(analysis, graphRoot, modelIdentifier, options, null);
    }

    public static ObjectNode read(AnalysisManager analysis, Path graphRoot, String modelIdentifier,
                                  ModelExportOptions options, Consumer<ModelExportProgress> progress) throws IOException {
        if (modelIdentifier == null || !modelIdentifier.matches("[A-Za-z0-9_-]+")) {
            throw new IOException("Invalid model identifier.");
        }
        report(progress, "graph", 0, 0);
        String graphFile = analysis.fetchGraphForModel(modelIdentifier, true, true, true, false, false);
        if (graphFile == null || !graphFile.matches("[A-Za-z0-9_.-]+") || graphFile.equals("..")) {
            throw new IOException("Graph generation did not return a valid graph file.");
        }
        Path model = graphRoot.resolve(modelIdentifier);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode elements = mapper.readTree(model.resolve(graphFile).toFile());
        if (elements == null || !elements.isArray() || elements.isEmpty()) {
            throw new IOException("No graph elements were generated for this model.");
        }
        ObjectNode snapshot = mapper.createObjectNode();
        snapshot.putObject("metadata").put("modelIdentifier", modelIdentifier).put("modelScope", "accumulated")
                .put("widgetTreesCaptured", options.includeWidgetTrees()).put("screenshotsCaptured", options.includeScreenshots());
        if ("inventory".equals(options.format())) {
            snapshot.set("propertyInventory", propertyInventory(mapper, elements, analysis.fetchWidgetPropertyNames(modelIdentifier)));
            return snapshot;
        }
        List<String> concreteStates = new ArrayList<>();
        Set<String> selectedImages = new HashSet<>();
        Map<String, String> abstractStates = new LinkedHashMap<>();
        Map<String, String> stateAbstraction = new LinkedHashMap<>();
        Map<String, String> concreteAbstraction = new LinkedHashMap<>();
        Map<List<String>, String> actionAbstraction = new LinkedHashMap<>();
        for (JsonNode element : elements) {
            JsonNode data = element.path("data");
            if (hasClass(element, "AbstractState")) {
                abstractStates.put(data.path("id").asText(), data.path("stateId").asText());
            }
            if (hasClass(element, "isAbstractedBy")) {
                stateAbstraction.put(data.path("source").asText(), data.path("target").asText());
            }
        }
        for (JsonNode element : elements) {
            JsonNode data = element.path("data");
            if (hasClass(element, "ConcreteState")) {
                String id = data.path("id").asText();
                concreteAbstraction.put(id, abstractStates.getOrDefault(stateAbstraction.get(id), data.path("AbstractID").asText()));
            }
            if ("abstract".equals(options.format()) && hasClass(element, "AbstractAction")) {
                JsonNode ids = data.path("concreteActionIds");
                List<String> concreteIds = new ArrayList<>();
                if (ids.isArray()) {
                    ids.forEach(id -> concreteIds.add(id.asText()));
                } else {
                    for (String id : ids.asText().replaceAll("^\\[|\\]$", "").split(",")) {
                        if (!id.trim().isEmpty()) {
                            concreteIds.add(id.trim());
                        }
                    }
                }
                for (String id : concreteIds) {
                    List<String> key = List.of(abstractStates.getOrDefault(data.path("source").asText(), ""),
                            abstractStates.getOrDefault(data.path("target").asText(), ""), id);
                    String actionId = data.path("actionId").asText();
                    String previous = actionAbstraction.putIfAbsent(key, actionId);
                    if (previous != null && !previous.equals(actionId)) {
                        throw new IOException("Cannot uniquely resolve a concrete action's abstract identity.");
                    }
                }
            }
        }
        Map<String, String> representativeStates = new LinkedHashMap<>();
        Map<String, String> representativeActions = new LinkedHashMap<>();
        List<JsonNode> orderedElements = new ArrayList<>();
        elements.forEach(orderedElements::add);
        orderedElements.sort(Comparator.comparing(element -> element.path("data").path("id").asText()));
        Set<String> traceRecords = "traces".equals(options.format()) ? traceRecords(orderedElements) : null;
        for (JsonNode element : orderedElements) {
            JsonNode data = element.path("data");
            String id = data.path("id").asText();
            if (traceRecords != null && !traceRecords.contains(id)) {
                continue;
            }
            if (hasClass(element, "ConcreteState")) {
                String abstractId = concreteAbstraction.get(id);
                if (!"abstract".equals(options.format()) || representativeStates.putIfAbsent(abstractId, id) == null) {
                    concreteStates.add(id);
                    selectedImages.add(id);
                }
            }
            if (hasClass(element, "ConcreteAction")) {
                List<String> key = List.of(concreteAbstraction.getOrDefault(data.path("source").asText(), ""),
                        concreteAbstraction.getOrDefault(data.path("target").asText(), ""), data.path("actionId").asText());
                String abstractId = actionAbstraction.getOrDefault(key, id);
                if (!"abstract".equals(options.format()) || representativeActions.putIfAbsent(abstractId, id) == null) {
                    selectedImages.add(id);
                }
            }
        }
        concreteStates.sort(String::compareTo);
        snapshot.set("elements", elements);
        if (options.includeWidgetTrees()) {
            report(progress, "trees", 0, concreteStates.size());
            Map<String, List<Element>> trees = progress == null ? analysis.fetchWidgetTrees(concreteStates)
                    : analysis.fetchWidgetTrees(concreteStates, progress);
            snapshot.set("widgetTrees", mapper.valueToTree(trees));
        }
        if (options.includeScreenshots()) {
            List<String> imageIds = new ArrayList<>(selectedImages);
            imageIds.sort(String::compareTo);
            report(progress, "screenshots", 0, imageIds.size());
            snapshot.set("images", mapper.valueToTree(progress == null ? analysis.fetchExportImages(imageIds)
                    : analysis.fetchExportImages(imageIds, progress)));
        } else {
            snapshot.putObject("images");
        }
        return snapshot;
    }

    private static void report(Consumer<ModelExportProgress> progress, String phase, int completed, int total) {
        if (progress != null) {
            progress.accept(new ModelExportProgress(phase, completed, total));
        }
    }

    private static Set<String> traceRecords(List<JsonNode> elements) {
        Set<String> records = new HashSet<>();
        Map<String, String> occurrenceStates = new LinkedHashMap<>();
        List<JsonNode> states = elements.stream().filter(element -> hasClass(element, "ConcreteState")).toList();
        List<JsonNode> actions = elements.stream().filter(element -> hasClass(element, "ConcreteAction")).toList();
        for (JsonNode occurrence : elements.stream().filter(element -> hasClass(element, "SequenceNode")).toList()) {
            String occurrenceId = occurrence.path("data").path("id").asText();
            String stateId = occurrence.path("data").path("concreteStateId").asText();
            List<String> targets = elements.stream().filter(element -> hasClass(element, "Accessed")
                    && occurrenceId.equals(element.path("data").path("source").asText()))
                    .map(element -> element.path("data").path("target").asText()).toList();
            List<JsonNode> matches = states.stream().filter(state -> {
                JsonNode data = state.path("data");
                boolean sameState = !stateId.isEmpty() && (stateId.equals(data.path("stateId").asText())
                        || stateId.equals(data.path("ConcreteID").asText()));
                return (targets.isEmpty() ? sameState : targets.contains(data.path("id").asText()))
                        && (stateId.isEmpty() || sameState);
            }).toList();
            if (matches.size() == 1) {
                String recordId = matches.get(0).path("data").path("id").asText();
                occurrenceStates.put(occurrenceId, recordId);
                records.add(recordId);
            }
        }
        for (JsonNode step : elements.stream().filter(element -> hasClass(element, "SequenceStep")).toList()) {
            JsonNode data = step.path("data");
            String source = occurrenceStates.get(data.path("source").asText());
            String target = occurrenceStates.get(data.path("target").asText());
            String actionId = data.path("concreteActionId").asText();
            String actionUid = data.path("concreteActionUid").asText();
            if (source == null || target == null || actionId.isEmpty()) {
                continue;
            }
            List<JsonNode> matches = actions.stream().filter(action -> {
                JsonNode actionData = action.path("data");
                return source.equals(actionData.path("source").asText()) && target.equals(actionData.path("target").asText())
                        && actionId.equals(actionData.path("actionId").asText())
                        && (actionUid.isEmpty() || actionUid.equals(actionData.path("uid").asText()));
            }).toList();
            if (matches.size() == 1) {
                records.add(matches.get(0).path("data").path("id").asText());
            }
        }
        return records;
    }

    private static ObjectNode propertyInventory(ObjectMapper mapper, JsonNode elements, List<String> widgetProperties) {
        Map<String, Set<String>> groups = new LinkedHashMap<>();
        for (String group : List.of("states", "actions", "transitions", "widgets", "sequences")) {
            groups.put(group, new TreeSet<>());
        }
        Set<String> identities = Set.of("id", "source", "target", "parent", "stateId", "actionId",
                "AbstractID", "ConcreteID", "concreteActionIds", "isInitial");
        Set<String> sequenceIdentities = Set.of("sequenceId", "nodeId", "stepId", "nodeNr", "timestamp",
                "startDateTime", "concreteStateId", "concreteActionId", "concreteActionUid", "finalVerdicts", "finalStateOccurrenceId");
        for (JsonNode element : elements) {
            String group = hasClass(element, "AbstractState") || hasClass(element, "ConcreteState") ? "states"
                    : hasClass(element, "ConcreteAction") ? "actions"
                    : hasClass(element, "AbstractAction") || hasClass(element, "UnvisitedAbstractAction")
                        || hasClass(element, "SequenceStep") ? "transitions"
                    : hasClass(element, "TestSequence") || hasClass(element, "SequenceNode") ? "sequences" : null;
            if (group != null) {
                boolean sequenceRecord = hasClass(element, "TestSequence") || hasClass(element, "SequenceNode")
                        || hasClass(element, "SequenceStep");
                element.path("data").fieldNames().forEachRemaining(name -> {
                    if (!sequenceRecord || !sequenceIdentities.contains(name)) {
                        groups.get(group).add(name);
                        if (hasClass(element, "ConcreteAction")) {
                            groups.get("transitions").add(name);
                        }
                    }
                });
            }
        }
        groups.get("widgets").addAll(widgetProperties);
        groups.values().forEach(names -> names.removeAll(identities));
        return mapper.valueToTree(groups);
    }

    private static boolean hasClass(JsonNode element, String className) {
        JsonNode classes = element.path("classes");
        if (classes.isArray()) {
            for (JsonNode value : classes) {
                if (className.equals(value.asText())) {
                    return true;
                }
            }
            return false;
        }
        return List.of(classes.asText().split("\\s+")).contains(className);
    }
}
