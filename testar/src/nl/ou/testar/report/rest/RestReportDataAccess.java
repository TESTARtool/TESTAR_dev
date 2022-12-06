package nl.ou.testar.report.rest;

import nl.ou.testar.report.ReportDataAccess;
import nl.ou.testar.report.ReportDataException;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.*;

import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.testar.monkey.Settings;

/**
 * RestDataAccess
 */
public class RestReportDataAccess implements ReportDataAccess {

    final CloseableHttpClient httpClient = HttpClientBuilder.create().build();

    private final String reportsUrl;
    private final String iterationsUrl;
    private final String actionsUrl;
    private final String statesUrl;
    private final String screenshotsUrl;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    final static String UNKNOWN_ERROR = "Unknown error";
    final static String UNKNOWN_SOURCE = "N/A";

    public RestReportDataAccess(String rootUrl, Settings settings) {
        reportsUrl = rootUrl + "/reports/";
        iterationsUrl = rootUrl + "/iterations/";
        actionsUrl = rootUrl + "/actions/";
        statesUrl = rootUrl + "/states/";
        screenshotsUrl = rootUrl + "/screenshots/";
    }

    @Override
    public int registerReport(String tag) throws ReportDataException {
        String responseString = null;
        try {
            responseString = postParams(reportsUrl, new BasicNameValuePair("tag", tag));
        }
        catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to register report: " + e.getMessage());
        }
        try {
            return new JSONObject(responseString).getInt("id");
        } catch (JSONException e) {
            System.err.println("Cannot find report ID in the response: " + responseString);
            e.printStackTrace();
            throw new ReportDataException("Failed to register report: unexpected response");
        }
    }

    @Override
    public int registerIteration(int reportId) throws ReportDataException {
        String responseString = null;
        try {
            responseString = postParams(iterationsUrl,
                    new BasicNameValuePair("reportId", String.valueOf(reportId)));
            return new JSONObject(responseString).getInt("id");
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to register iteration: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Cannot find iteration ID in the response: " + responseString);
            e.printStackTrace();
            throw new ReportDataException("Cannot riteration report: unexpected response");
        }
    }

    @Override
    public int registerIteration(int reportId, String info, Double severity) throws ReportDataException {
        String responseString = null;
        try {
        responseString = postParams(iterationsUrl,
                new BasicNameValuePair("reportId", String.valueOf(reportId)),
                new BasicNameValuePair("info", info),
                new BasicNameValuePair("severity", String.valueOf(severity)));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to register iteration: " + e.getMessage());
        }
        try {
            return new JSONObject(responseString).getInt("id");
        } catch (JSONException e) {
            System.err.println("Cannot find iteration ID in the response: " + responseString);
            e.printStackTrace();
            throw new ReportDataException("Cannot register iteration: unexpected response");
        }
    }

    @Override
    public int registerAction(String name, String description, String status, String screenshot, Timestamp startTime,
            boolean selected, int stateId, int targetStateId, String widgetPath) throws ReportDataException {
        FileBody fileBody = new FileBody(new File(screenshot), ContentType.IMAGE_JPEG);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.addPart("file", fileBody);
        HttpEntity screenshotEntity = builder.build();
        String result = null;
        try{
        result = postEntity(screenshotsUrl, screenshotEntity);
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to upload screenshot: " + e.getMessage());
        }
        String screenshotUri = null;
        try {
            screenshotUri = new JSONObject(result).getString("uri");
        } catch (JSONException e) {
            System.err.println("Cannot find uploaded screenshot URI in the response: " + result);
            e.printStackTrace();
            throw new ReportDataException("Failed to upload screenshot: unexpected response");
        }

        String responseString = null;
        try {
        responseString = postParams(actionsUrl,
                new BasicNameValuePair("name", name),
                new BasicNameValuePair("description", description),
                new BasicNameValuePair("status", status),
                new BasicNameValuePair("screenshot", screenshotUri),
                new BasicNameValuePair("startTime", formatter.format(startTime.toLocalDateTime())),
                new BasicNameValuePair("selected", String.valueOf(selected)),
                new BasicNameValuePair("stateId", String.valueOf(stateId)),
                new BasicNameValuePair("targetStateId", String.valueOf(targetStateId)),
                new BasicNameValuePair("widgetPath", widgetPath));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to register action: " + e.getMessage());
        }
        try {
            return new JSONObject(responseString).getInt("id");
        } catch (JSONException e) {
            System.err.println("Cannot find action ID in the response: " + responseString);
            e.printStackTrace();
            throw new ReportDataException("Failed to register action: unexpected response");
        }
    }

    @Override
    public int registerState(String concreteIdCustom, String abstractId, String abstractRId, String abstractRTId,
                             String abstractRTPId, Double severity, String status) throws ReportDataException {
        String responseString = null;
        try {
            responseString = postParams(statesUrl,
                new BasicNameValuePair("concreteId", concreteIdCustom),
                new BasicNameValuePair("abstractId", abstractId),
                new BasicNameValuePair("abstractRId", abstractRId),
                new BasicNameValuePair("abstractRTId", abstractRTId),
                new BasicNameValuePair("abstractRTPId", abstractRTPId),
                new BasicNameValuePair("severity", String.valueOf(severity)),
                new BasicNameValuePair("status", status));
            return new JSONObject(responseString).getInt("id");
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to register state: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Cannot find state ID in the response: " + responseString);
            e.printStackTrace();
            throw new ReportDataException("Failed to register state: unexpected response");
        }
    }

    @Override
    public int registerStateAction(int stateId, int actionId, boolean visited) throws ReportDataException {
        try {
        putParams(actionsUrl,
                new BasicNameValuePair("actionId", String.valueOf(actionId)),
                new BasicNameValuePair("stateId", String.valueOf(stateId)),
                new BasicNameValuePair("visited", String.valueOf(visited)));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to register state for action: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public void registerTargetState(int actionId, int stateId) throws ReportDataException {
        try {
        putParams(actionsUrl,
                new BasicNameValuePair("actionId", String.valueOf(actionId)),
                new BasicNameValuePair("targetStateId", String.valueOf(stateId)));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to register target state: " + e.getMessage());
        }
    }

    @Override
    public void addActionToIteration(int actionId, int iterationId) throws ReportDataException {
        try {
        putParams(actionsUrl,
                new BasicNameValuePair("actionId", String.valueOf(actionId)),
                new BasicNameValuePair("iterationId", String.valueOf(iterationId)));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Faile do add action to an iteration: " + e.getMessage());
        }
    }

    @Override
    public void addStateToIteration(int stateId, int iterationId) throws ReportDataException {
        try {
            putParams(statesUrl,
                    new BasicNameValuePair("stateId", String.valueOf(stateId)),
                    new BasicNameValuePair("iterationId", String.valueOf(iterationId)));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Faile do add state to an iteration: " + e.getMessage());
        }
    }

    @Override
    public void setSelectionInIteration(int iterationId, int lastExecutedActionId, int lastStateId)
            throws ReportDataException {
        try {
        putParams(iterationsUrl,
                new BasicNameValuePair("iterationId", String.valueOf(iterationId)),
                new BasicNameValuePair("lastExecutedActionId", String.valueOf(lastExecutedActionId)));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to set selection in iteration: " + e.getMessage());
        }
    }

    @Override
    public void storeVerdict(int iterationId, String info, Double severity) throws ReportDataException {
        try {
        putParams(iterationsUrl,
                new BasicNameValuePair("iterationId", String.valueOf(iterationId)),
                new BasicNameValuePair("severity", String.valueOf(severity)));
        } catch (ReportNetworkException | ReportParseException e) {
        throw new ReportDataException("Failed to store verdict: " + e.getMessage());
    }
    }

    @Override
    public void finalizeReport(int reportId, int actionsPerSequence, int totalSequences, String url)
            throws ReportDataException {
        try {
        putParams(reportsUrl,
                new BasicNameValuePair("reportId", String.valueOf(reportId)),
                new BasicNameValuePair("actionsPerSequence", String.valueOf(actionsPerSequence)),
                new BasicNameValuePair("totalSequences", String.valueOf(totalSequences)),
                new BasicNameValuePair("url", url));
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to finalize report: " + e.getMessage());
    }
    }

    @Override
    public int findState(String concreteIdCustom, String abstractId) throws ReportDataException {
        try {
            JSONObject state = getFirstResultByParams(statesUrl, "sequenceItems",
                    new BasicNameValuePair("concreteIds", concreteIdCustom),
                    new BasicNameValuePair("abstractIds", abstractId));
            if (state == null) {
                return 0;
            }
            return state.getInt("id");
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to find state: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Failed to find state: malformed JSON obtained");
            e.printStackTrace();
            throw new ReportDataException("Malformed JSON obtained when looking for state: " + e.getMessage());
        }
    }

    @Override
    public int getReportId(String reportTag) throws ReportDataException {
        try {
            JSONObject report = getFirstResultByParams(reportsUrl, "reports",
                    new BasicNameValuePair("tag", reportTag));
            if (report == null) {
                return 0;
            }
            return report.getInt("id");
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to find report: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Failed to get report ID");
            e.printStackTrace();
            throw new ReportDataException("Malformed JSON obtained when looking for report: " + e.getMessage());
        }
    }

    @Override
    public int getFirstIterationId(int reportId) throws ReportDataException {
        try {
            JSONObject iteration = getFirstResultByParams(iterationsUrl, "iterations",
                    new BasicNameValuePair("reportIds", String.valueOf(reportId)));
            if (iteration == null) {
                return 0;
            }
            return iteration.getInt("id");
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to find first iteration: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Failed to get first iteration ID");
            e.printStackTrace();
            throw new ReportDataException("Malformed JSON obtained when looking for first iteration: " + e.getMessage());
        }
    }

    @Override
    public int getNextIterationId(int reportId, int iterationId) throws ReportDataException {
        try {
            JSONObject iteration = getFirstResultByParams(iterationsUrl, "iterations",
                    new BasicNameValuePair("reportIds", String.valueOf(reportId)),
                    new BasicNameValuePair("idGreaterThan", String.valueOf(iterationId)));
            if (iteration == null) {
                return 0;
            }
            return iteration.getInt("id");
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to find next iteration: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Failed to get next iteration ID");
            e.printStackTrace();
            throw new ReportDataException(
                    "Malformed JSON obtained when looking for next iteration: " + e.getMessage());
        }
    }

    @Override
    public ActionData getFirstAction(int iterationId) throws ReportDataException {
        try {
            JSONObject action = getFirstResultByParams(actionsUrl, "actions",
                    new BasicNameValuePair("iterationIds", String.valueOf(iterationId)));
            if (action == null) {
                return null;
            }
            return actionDataFromJson(action);
        } catch (ReportNetworkException | ReportParseException e) {
            System.err.println("Failed to get first action ID");
            e.printStackTrace();
            throw new ReportDataException("Failed to find first action: " + e.getMessage());
        }
    }

    @Override
    public ActionData getNextAction(int iterationId, Timestamp actionTime) throws ReportDataException {
        try {
            JSONObject action = getFirstResultByParams(actionsUrl, "actions",
                    new BasicNameValuePair("iterationIds", String.valueOf(iterationId)),
                    new BasicNameValuePair("startedAfter", formatter.format(actionTime.toLocalDateTime())));
            if (action == null) {
                return null;
            }
            return actionDataFromJson(action);
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to get next action: " + e.getMessage());
        }
    }

    @Override
    public List<IterationData> getAllIterations(int reportId) throws ReportDataException {
        try {
            JSONArray iterations = getAllResultsByParams(iterationsUrl, "iterations",
                    new BasicNameValuePair("reportIds", String.valueOf(reportId)));
            if (iterations == null) {
                return null;
            }
            int size = iterations.length();
            ArrayList<IterationData> iterationDataList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                iterationDataList.add(iterationDataFromJson(iterations.getJSONObject(i)));
            }
            return iterationDataList;
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to find iterations: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Failed to get iterations");
            e.printStackTrace();
            throw new ReportDataException("Malformed JSON obtained when looking for iterations: " + e.getMessage());
        }
    }

    @Override
    public List<ActionData> getAllActions(int iterationId) throws ReportDataException {
        try {
            JSONArray actions = getAllResultsByParams(actionsUrl, "actions",
                    new BasicNameValuePair("iterationIds", String.valueOf(iterationId)));
            if (actions == null) {
                return null;
            }
            int size = actions.length();
            ArrayList<ActionData> actionDataList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                actionDataList.add(actionDataFromJson(actions.getJSONObject(i)));
            }
            return actionDataList;
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to find actions: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Failed to get all actions");
            e.printStackTrace();
            throw new ReportDataException("Malformed JSON obtained when looking for actions: " + e.getMessage());
        }
    }

    @Override
    public List<ActionData> getSelectedActions(int iterationId) throws ReportDataException {
        try {
            JSONArray actions = getAllResultsByParams(actionsUrl, "actions",
                    new BasicNameValuePair("iterationIds", String.valueOf(iterationId)),
                    new BasicNameValuePair("selected", "true"));
            if (actions == null) {
                return null;
            }
            int size = actions.length();
            ArrayList<ActionData> actionDataList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                actionDataList.add(actionDataFromJson(actions.getJSONObject(i)));
            }
            return actionDataList;
        } catch (ReportNetworkException | ReportParseException e) {
            throw new ReportDataException("Failed to find selected actions: " + e.getMessage());
        } catch (JSONException e) {
            System.err.println("Failed to get selected actions");
            e.printStackTrace();
            throw new ReportDataException("Malformed JSON obtained when looking for selected actions: " + e.getMessage());
        }
    }

    private JSONArray getAllResultsByParams(String uri, String listName, NameValuePair... params)
            throws ReportNetworkException, ReportParseException {
        List<String> paramStrings = Stream.of(params)
                .map(item -> String.format("%s=%s", item.getName(), item.getValue())).collect(Collectors.toList());
        String urlWithParams = String.format("%s?%s", uri, String.join("&", paramStrings));
        final HttpGet request = new HttpGet(urlWithParams);

        ClassicHttpResponse response = null;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            throw new ReportNetworkException(UNKNOWN_ERROR, urlWithParams);
        }
        if (response.getCode() != HttpStatus.SC_OK) {
            throw new ReportNetworkException(response.getReasonPhrase(), urlWithParams);
        }
        String responseString = null;
        try {
            responseString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new ReportNetworkException("I/O error", urlWithParams);
        } catch (ParseException e) {
            throw new ReportParseException(UNKNOWN_ERROR, UNKNOWN_SOURCE);
        }
        try {
            JSONObject responseObject = new JSONObject(responseString);
            if (!responseObject.has("_embedded")) {
                return null;
            }
            JSONObject embedded = responseObject.getJSONObject("_embedded");
            if (!embedded.has(listName)) {
                return null;
            }
            return embedded.getJSONArray(listName);
        } catch (JSONException e) {
            throw new ReportParseException(UNKNOWN_ERROR, responseString);
        }
    }

    private JSONObject getFirstResultByParams(String uri, String listName, NameValuePair... params)
            throws ReportNetworkException, ReportParseException {
        JSONArray items = getAllResultsByParams(uri, listName, params);
        if (items == null || items.length() == 0) {
            return null;
        }
        try {
            return items.getJSONObject(0);
        } catch (JSONException e) {
            throw new ReportParseException(UNKNOWN_ERROR, UNKNOWN_SOURCE);
        }
    }

    private String postParams(String uri, NameValuePair... params) throws ReportNetworkException,
            ReportParseException {
        final List<NameValuePair> paramList = Arrays.asList(params);
        return postEntity(uri, new UrlEncodedFormEntity(paramList));
    }

    private String postEntity(String uri, HttpEntity entity) throws ReportNetworkException, ReportParseException {
        final HttpPost request = new HttpPost(uri);
        request.setEntity(entity);
        try {
            ClassicHttpResponse response = httpClient.execute(request);
            if (response.getCode() != HttpStatus.SC_OK) {
                System.err.println(String.format("Failed to POST data to %s: %d %s", uri, response.getCode(),
                        response.getReasonPhrase()));
                throw new ReportNetworkException(response.getReasonPhrase(), uri);
            }
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            System.err.println(String.format("Failed to POST data to %s: %s", uri, e.getMessage()));
            e.printStackTrace();
            throw new ReportNetworkException(e.getMessage(), uri);
        } catch (ParseException e) {
            System.err.println(String.format("Failed to parse response: " + e.getMessage()));
            e.printStackTrace();
            throw new ReportParseException(e.getMessage(), UNKNOWN_SOURCE);
        }
    }

    private String putParams(String uri, NameValuePair... params) throws ReportNetworkException, ReportParseException {
        final List<NameValuePair> paramList = Arrays.asList(params);
        return putEntity(uri, new UrlEncodedFormEntity(paramList));
    }

    private String putEntity(String uri, HttpEntity entity) throws ReportNetworkException, ReportParseException {
        final HttpPut request = new HttpPut(uri);
        request.setEntity(entity);
        try {
            ClassicHttpResponse response = httpClient.execute(request);
            if (response.getCode() != HttpStatus.SC_OK) {
                System.err.println(String.format("Failed to PUT data to %s: %d %s", uri, response.getCode(), response.getReasonPhrase()));
                throw new ReportNetworkException(response.getReasonPhrase(), uri);
            }
            String responseString = EntityUtils.toString(response.getEntity());
            return responseString;
        } catch (IOException e) {
            System.err.println(String.format("Failed to PUT data to %s: %s", uri, e.getMessage()));
            e.printStackTrace();
            throw new ReportNetworkException(e.getMessage(), uri);
        } catch (ParseException e) {
            System.err.println(String.format("Cannot parse PUT response from %", uri));
            e.printStackTrace();
            throw new ReportParseException(e.getMessage(), UNKNOWN_SOURCE);
        }
    }

    private ActionData actionDataFromJson(JSONObject action) throws ReportParseException {
        try {
            return new ActionData(
                    action.getInt("id"),
                    action.getInt("iterationId"),
                    action.getString("name"),
                    action.getString("description"),
                    action.getString("status"),
                    action.getString("screenshot"),
                    Timestamp.valueOf(action.getString("startTime")),
                    action.getString("widgetPath"));
        } catch (JSONException e) {
            System.err.println(String.format("Failed to parse action data %s: %s", action.toString(), e.getMessage()));
            e.printStackTrace();
            throw new ReportParseException(e.getMessage(), action.toString());
        }
    }

    private IterationData iterationDataFromJson(JSONObject iteration) throws ReportParseException {
        try {
            return new IterationData(
                    iteration.getInt("id"),
                    iteration.getInt("reportId"),
                    iteration.getString("info"),
                    iteration.getDouble("severity"));
        } catch (JSONException e) {
            System.err.println(String.format("Failed to parse iteration data %s: %s", iteration.toString(), e.getMessage()));
            e.printStackTrace();
            throw new ReportParseException(e.getMessage(), iteration.toString());
        }
    }
}
