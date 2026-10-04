package org.testar.statemodel.analysis;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.testar.statemodel.analysis.export.ModelExportOptions;
import org.testar.statemodel.analysis.export.ModelExportProgress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ModelExportServletTest {

    private ModelExportServlet servlet;
    private AnalysisManager analysis;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @Before
    public void prepareServlet() throws Exception {
        ServletConfig config = mock(ServletConfig.class);
        ServletContext context = mock(ServletContext.class);
        analysis = mock(AnalysisManager.class);
        when(config.getServletContext()).thenReturn(context);
        when(context.getAttribute("analysisManager")).thenReturn(analysis);
        servlet = new ModelExportServlet();
        servlet.init(config);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    public void servesUtf8SnapshotWithoutCachingOrClientFilesystemPaths() throws Exception {
        when(request.getParameter("modelIdentifier")).thenReturn("model-1");
        ObjectNode snapshot = new ObjectMapper().createObjectNode().put("title", "caf\u00e9");
        when(analysis.fetchExportSnapshot("model-1", new ModelExportOptions("hybrid", false, true))).thenReturn(snapshot);
        StringWriter output = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(output));

        servlet.doGet(request, response);

        assertEquals(snapshot, new ObjectMapper().readTree(output.toString()));
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setHeader("Cache-Control", "no-store");
        verify(analysis).fetchExportSnapshot("model-1", new ModelExportOptions("hybrid", false, true));
    }

    @Test
    public void rejectsMissingAndUnsafeModelIdentifiersBeforeReadingAnalysis() throws Exception {
        for (String identifier : new String[] {null, "../outside", "model\"<script>"}) {
            HttpServletResponse invalidResponse = mock(HttpServletResponse.class);
            when(request.getParameter("modelIdentifier")).thenReturn(identifier);

            servlet.doGet(request, invalidResponse);

            verify(invalidResponse).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid model identifier.");
        }
        verifyNoInteractions(analysis);
    }

    @Test
    public void passesConfirmedChoicesToSharedPreparation() throws Exception {
        when(request.getParameter("modelIdentifier")).thenReturn("model-1");
        when(request.getParameter("includeWidgetTrees")).thenReturn("true");
        when(request.getParameter("includeScreenshots")).thenReturn("false");
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        for (String format : new String[] {"abstract", "hybrid", "concrete", "traces"}) {
            when(request.getParameter("format")).thenReturn(format);
            ModelExportOptions options = new ModelExportOptions(format, true, false);
            when(analysis.fetchExportSnapshot("model-1", options)).thenReturn(new ObjectMapper().createObjectNode());
            servlet.doGet(request, response);
            verify(analysis).fetchExportSnapshot("model-1", options);
        }
    }

    @Test
    public void invalidFormatsAndBooleanValuesAreRejectedBeforePreparation() throws Exception {
        when(request.getParameter("modelIdentifier")).thenReturn("model-1");
        for (String format : new String[] {"snapshot", "unknown"}) {
            HttpServletResponse invalid = mock(HttpServletResponse.class);
            when(request.getParameter("format")).thenReturn(format);
            servlet.doGet(request, invalid);
            verify(invalid).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid model export options.");
        }
        when(request.getParameter("format")).thenReturn("hybrid");
        when(request.getParameter("includeWidgetTrees")).thenReturn("yes");
        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid model export options.");
        verifyNoInteractions(analysis);
    }

    @Test
    public void failureReturnsSafeFeedbackInsteadOfDatastoreDetails() throws Exception {
        when(request.getParameter("modelIdentifier")).thenReturn("model-1");
        when(analysis.fetchExportSnapshot("model-1", new ModelExportOptions("hybrid", false, true)))
                .thenThrow(new IOException("private datastore path"));

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to prepare model export data.");
    }

    @Test
    public void streamsFlushedPreparationCountsBeforeTheFinalUtf8Snapshot() throws Exception {
        when(request.getParameter("modelIdentifier")).thenReturn("model-1");
        when(request.getParameter("progress")).thenReturn("true");
        when(request.getParameter("includeWidgetTrees")).thenReturn("true");
        StringWriter output = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(output));
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode snapshot = mapper.createObjectNode().put("title", "caf\u00e9");
        doAnswer(invocation -> {
            Consumer<ModelExportProgress> progress = invocation.getArgument(2);
            progress.accept(new ModelExportProgress("trees", 1, 500));
            assertTrue(output.toString().contains("\"completed\":1"));
            assertFalse(output.toString().contains("\"snapshot\""));
            progress.accept(new ModelExportProgress("trees", 500, 500));
            return snapshot;
        }).when(analysis).fetchExportSnapshot(eq("model-1"), eq(new ModelExportOptions("hybrid", true, true)), any());

        servlet.doGet(request, response);

        String[] events = output.toString().strip().split("\\R");
        assertEquals(5, events.length);
        assertEquals("graph", mapper.readTree(events[0]).path("phase").asText());
        assertEquals(1, mapper.readTree(events[1]).path("completed").asInt());
        assertEquals(500, mapper.readTree(events[2]).path("completed").asInt());
        assertEquals("transfer", mapper.readTree(events[3]).path("phase").asText());
        assertEquals("snapshot", mapper.readTree(events[4]).path("type").asText());
        assertEquals(snapshot, mapper.readTree(events[4]).path("snapshot"));
        verify(response).setContentType("application/x-ndjson");
        verify(response).setCharacterEncoding("UTF-8");
        verify(response).setHeader("Cache-Control", "no-store");
    }

    @Test
    public void streamedFailureEndsWithSafeErrorWithoutAPartialSnapshot() throws Exception {
        when(request.getParameter("modelIdentifier")).thenReturn("model-1");
        when(request.getParameter("progress")).thenReturn("true");
        StringWriter output = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(output));
        when(analysis.fetchExportSnapshot(eq("model-1"), eq(new ModelExportOptions("hybrid", false, true)), any()))
                .thenThrow(new IOException("private datastore path"));

        servlet.doGet(request, response);

        String[] events = output.toString().strip().split("\\R");
        assertEquals("error", new ObjectMapper().readTree(events[1]).path("type").asText());
        assertTrue(output.toString().contains("Unable to prepare model export data."));
        assertFalse(output.toString().contains("private datastore path"));
        assertFalse(output.toString().contains("\"snapshot\""));
    }

    @Test
    public void rejectsInvalidProgressOptionBeforePreparation() throws Exception {
        when(request.getParameter("modelIdentifier")).thenReturn("model-1");
        when(request.getParameter("progress")).thenReturn("yes");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid model export options.");
        verifyNoInteractions(analysis);
    }
}
