package nl.ou.testar.visualvalidation;

import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.extractor.ExpectedTextCallback;
import nl.ou.testar.visualvalidation.extractor.ExtractorFactory;
import nl.ou.testar.visualvalidation.extractor.TextExtractorInterface;
import nl.ou.testar.visualvalidation.matcher.ContentMatchResult;
import nl.ou.testar.visualvalidation.matcher.MatcherResult;
import nl.ou.testar.visualvalidation.matcher.VisualMatcherInterface;
import nl.ou.testar.visualvalidation.matcher.VisualMatcherFactory;
import nl.ou.testar.visualvalidation.ocr.OcrConfiguration;
import nl.ou.testar.visualvalidation.ocr.OcrEngineFactory;
import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;
import nl.ou.testar.visualvalidation.ocr.OcrResultCallback;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.Action;
import org.fruit.alayer.State;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Widget;
import org.testar.Logger;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static nl.ou.testar.visualvalidation.VisualValidationTag.VisualValidationVerdict;
import static nl.ou.testar.visualvalidation.VisualValidationVerdict.*;

public class VisualValidator implements VisualValidationManager, OcrResultCallback, ExpectedTextCallback {
    static final Color darkOrange = new Color(255, 128, 0);
    private final String TAG = "VisualValidator";
    private final VisualMatcherInterface _matcher;
    private final OcrEngineInterface _ocrEngine;
    private final Object _ocrResultSync = new Object();
    private final AtomicBoolean _ocrResultReceived = new AtomicBoolean();
    private final TextExtractorInterface _extractor;
    private final Object _expectedTextSync = new Object();
    private final AtomicBoolean _expectedTextReceived = new AtomicBoolean();
    private final VisualValidationSettings _settings;
    private int analysisId = 0;
    private MatcherResult _matcherResult = null;
    private List<RecognizedElement> _ocrItems = null;
    private List<ExpectedElement> _expectedText = null;

    public VisualValidator(@NonNull VisualValidationSettings settings) {
        _settings = settings;
        OcrConfiguration ocrConfig = _settings.ocrConfiguration;
        if (ocrConfig.enabled) {
            _ocrEngine = OcrEngineFactory.createOcrEngine(ocrConfig);
        } else {
            _ocrEngine = null;
        }

        if (_settings.protocol.contains("webdriver_generic")) {
            _extractor = ExtractorFactory.CreateExpectedTextExtractorWebdriver();
        } else {
            _extractor = ExtractorFactory.CreateExpectedTextExtractorDesktop();
        }

        _matcher = VisualMatcherFactory.createLocationMatcher(_settings.matcherConfiguration);
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    @Override
    public MatcherResult AnalyzeImage(State state, @Nullable AWTCanvas screenshot) {
        return AnalyzeImage(state, screenshot, null);
    }

    @Override
    public MatcherResult AnalyzeImage(State state, @Nullable AWTCanvas screenshot, @Nullable Widget widget) {
        // Create new session
        startNewAnalysis();

        if (screenshot != null) {
            // Start ocr analysis, provide callback once finished.
            parseScreenshot(screenshot);

            // Start extracting text, provide callback once finished.
            extractExpectedText(state, widget);

            // Match the expected text with the detected text.
            matchText();

            // Calculate the verdict and create a screenshot with annotations which can be used by the HTML reporter.
            processMatchResult(state, screenshot);
        } else {
            Logger.log(Level.ERROR, TAG, "No screenshot for current state, skipping visual validation");
        }
        return _matcherResult;
    }

    private void startNewAnalysis() {
        analysisId++;
        synchronized (_ocrResultSync) {
            _ocrResultReceived.set(false);
        }
        synchronized (_expectedTextSync) {
            _expectedTextReceived.set(false);
        }
        _matcherResult = null;
        Logger.log(Level.INFO, TAG, "Starting new analysis {}", analysisId);
    }

    private void parseScreenshot(AWTCanvas screenshot) {
        _ocrEngine.AnalyzeImage(screenshot.image(), this);
    }

    private void extractExpectedText(State state, @Nullable Widget widget) {
        _extractor.ExtractExpectedText(state, widget, this);
    }

    private void matchText() {
        waitForResults();

        _matcherResult = _matcher.Match(_ocrItems, _expectedText);
    }

    private void waitForResult(@NonNull AtomicBoolean receivedFlag, Object syncObject) {
        if (!receivedFlag.get()) {
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (syncObject) {
                try {
                    syncObject.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void waitForResults() {
        waitForResult(_ocrResultReceived, _ocrResultSync);
        waitForResult(_expectedTextReceived, _expectedTextSync);
    }

    private void processMatchResult(State state, AWTCanvas screenshot) {
        // Create a copy, so we can annotate the results without modifying the original one.
        final AWTCanvas copy = new AWTCanvas(
                screenshot.width(),
                screenshot.height(),
                screenshot.deepCopyImage(),
                AWTCanvas.StorageFormat.PNG,
                1.0);

        final Verdict[] validationVerdict = {Verdict.OK};
        if (_matcherResult != null) {
            // Draw a rectangle on each expected text element that has been recognized.
            final ContentMatchResult[] lowestMatch = {null};
            _matcherResult.getResult().forEach(result -> {
                        java.awt.Color penColor;
                        Verdict verdict;
                        if (result.matchedPercentage == 100) {
                            penColor = java.awt.Color.green;
                            verdict = createSuccessVerdict(result);
                        } else if (isBetween(result.matchedPercentage,
                                _settings.matcherConfiguration.failedToMatchPercentageThreshold,
                                99)) {
                            penColor = java.awt.Color.yellow;
                            verdict = createAlmostMatchedVerdict(result);
                        } else {
                            penColor = darkOrange;
                            verdict = createHardlyMatchedVerdict(result);
                        }

                        // Store the result with the lowest percentage including the corresponding verdict.
                        if ((lowestMatch[0] == null) ||
                                (lowestMatch[0].matchedPercentage > result.matchedPercentage)) {
                            lowestMatch[0] = result;
                            validationVerdict[0] = verdict;
                        }

                        drawRectangle(copy, result.foundLocation, penColor);
                    }
            );
            _matcherResult.getNoLocationMatches().forEach(result ->
                    {
                        if (result instanceof ExpectedElement) {
                            drawRectangle(copy, result._location, java.awt.Color.red);
                            // Overwrite the verdict if we couldn't find a location match for expected text.
                            validationVerdict[0] = createFailedToMatchVerdict(result);
                        } else {
                            drawRectangle(copy, result._location, Color.magenta);
                        }
                    }
            );
        }
        // Store the validation verdict for this run.
        state.set(VisualValidationVerdict, validationVerdict[0]);

        // Store the annotated screenshot, so they can be used by the HTML report generator.
        String stateId = state.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable");
        Action action = state.get(Tags.ExecutedAction, null);
        if (action != null) {
            String actionID = action.get(Tags.ConcreteIDCustom, "NoConcreteIdAvailable");
            ScreenshotSerialiser.saveActionshot(stateId, actionID + MatcherResult.ScreenshotPostFix, copy);
        } else {
            ScreenshotSerialiser.saveStateshot(stateId + MatcherResult.ScreenshotPostFix, copy);
        }

        Logger.log(Level.INFO, TAG, "Processed match results");
    }

    private void drawRectangle(AWTCanvas screenshot, Rectangle location, java.awt.Color color) {
        Graphics2D graphics = screenshot.image().createGraphics();
        graphics.setColor(color);
        graphics.drawRect(location.x, location.y, location.width - 1, location.height - 1);
        graphics.dispose();
    }

    @Override
    public void Destroy() {
        _matcher.destroy();
        _extractor.Destroy();
        if (_ocrEngine != null) {
            _ocrEngine.Destroy();
        }
    }

    @Override
    public void reportResult(@NonNull List<RecognizedElement> detectedText) {
        synchronized (_ocrResultSync) {
            _ocrItems = detectedText;
            _ocrResultReceived.set(true);
            _ocrResultSync.notifyAll();
            Logger.log(Level.INFO, TAG, "Received {} OCR result", detectedText.size());
        }
    }

    @Override
    public void ReportExtractedText(@NonNull List<ExpectedElement> expectedText) {
        synchronized (_expectedTextSync) {
            _expectedText = expectedText;
            _expectedTextReceived.set(true);
            _expectedTextSync.notifyAll();
            Logger.log(Level.INFO, TAG, "Received {} expected result", expectedText.size());
        }
    }
}
