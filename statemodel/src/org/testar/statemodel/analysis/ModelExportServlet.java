/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.statemodel.analysis;

import java.io.IOException;
import java.io.PrintWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.testar.statemodel.analysis.export.ModelExportOptions;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Provides model data, not client-supplied filesystem paths, to the browser exporter. */
public class ModelExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String modelIdentifier = request.getParameter("modelIdentifier");
        if (modelIdentifier == null || !modelIdentifier.matches("[A-Za-z0-9_-]+")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid model identifier.");
            return;
        }
        AnalysisManager analysis = (AnalysisManager) getServletContext().getAttribute("analysisManager");
        if (analysis == null) {
            response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Model analysis is unavailable.");
            return;
        }
        boolean streaming = false;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String format = request.getParameter("format");
            ModelExportOptions options = new ModelExportOptions(format == null ? "hybrid" : format,
                    booleanParameter(request, "includeWidgetTrees", false),
                    booleanParameter(request, "includeScreenshots", !"inventory".equals(format)));
            if ("snapshot".equals(options.format())) {
                throw new IllegalArgumentException("Unsupported download format.");
            }
            streaming = booleanParameter(request, "progress", false) && !"inventory".equals(options.format());
            if (streaming) {
                response.setContentType("application/x-ndjson");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Cache-Control", "no-store");
                PrintWriter writer = response.getWriter();
                writeEvent(writer, mapper.createObjectNode().put("type", "progress").put("phase", "graph"));
                ObjectNode snapshot = analysis.fetchExportSnapshot(modelIdentifier, options, progress -> {
                    ObjectNode event = mapper.valueToTree(progress);
                    event.put("type", "progress");
                    writeEvent(writer, event);
                });
                writeEvent(writer, mapper.createObjectNode().put("type", "progress").put("phase", "transfer"));
                writeEvent(writer, mapper.createObjectNode().put("type", "snapshot").set("snapshot", snapshot));
                return;
            }
            String snapshot = analysis.fetchExportSnapshot(modelIdentifier, options).toString();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-store");
            response.getWriter().write(snapshot);
        } catch (IllegalArgumentException exception) {
            if (streaming) {
                writeEvent(response.getWriter(), mapper.createObjectNode().put("type", "error").put("message", "Invalid model export options."));
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid model export options.");
            }
        } catch (IOException | RuntimeException exception) {
            StateModelDebugLog.log("Unable to prepare state model JSON export.", exception);
            if (streaming) {
                response.getWriter().println(mapper.createObjectNode().put("type", "error").put("message", "Unable to prepare model export data."));
                response.getWriter().flush();
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to prepare model export data.");
            }
        }
    }

    private static void writeEvent(PrintWriter writer, ObjectNode event) {
        writer.println(event);
        writer.flush();
        if (writer.checkError()) {
            throw new IllegalStateException("Export connection closed.");
        }
    }

    private static boolean booleanParameter(HttpServletRequest request, String name, boolean defaultValue) {
        String value = request.getParameter(name);
        if (value == null) {
            return defaultValue;
        }
        if (!value.equals("true") && !value.equals("false")) {
            throw new IllegalArgumentException("Invalid boolean option.");
        }
        return Boolean.parseBoolean(value);
    }
}
