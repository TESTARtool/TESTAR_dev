package nl.ou.testar.visualvalidation;

import nl.ou.testar.visualvalidation.extractor.ExpectedElement;
import nl.ou.testar.visualvalidation.extractor.ExpectedTextCallback;
import nl.ou.testar.visualvalidation.extractor.ExtractorFactory;
import nl.ou.testar.visualvalidation.extractor.TextExtractorInterface;
import nl.ou.testar.visualvalidation.matcher.MatcherResult;
import nl.ou.testar.visualvalidation.matcher.VisualMatcher;
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
import org.fruit.alayer.AbsolutePosition;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Pen;
import org.fruit.alayer.State;
import org.fruit.alayer.StrokePattern;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.visualizers.TextVisualizer;
import org.testar.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class VisualValidator implements VisualValidationManager, OcrResultCallback, ExpectedTextCallback {
    private final String TAG = "VisualValidator";
    private int analysisId = 0;

    private final VisualMatcher _matcher;
    private MatcherResult _matcherResult = null;

    private final OcrEngineInterface _ocrEngine;
    private final Object _ocrResultSync = new Object();
    private final AtomicBoolean _ocrResultReceived = new AtomicBoolean();
    private List<RecognizedElement> _ocrItems = null;

    private final TextExtractorInterface _extractor;
    private final Object _expectedTextSync = new Object();
    private final AtomicBoolean _expectedTextReceived = new AtomicBoolean();
    private List<ExpectedElement> _expectedText = null;

    protected final static Pen RedPen = Pen.newPen().setColor(Color.Red).
            setFillPattern(FillPattern.None).setStrokePattern(StrokePattern.Solid).build();

    public VisualValidator(@NonNull VisualValidationSettings settings) {
        OcrConfiguration ocrConfig = settings.ocrConfiguration;
        if (ocrConfig.enabled) {
            _ocrEngine = OcrEngineFactory.createOcrEngine(ocrConfig);
        } else {
            _ocrEngine = null;
        }

        _extractor = ExtractorFactory.CreateTextExtractor();

        _matcher = VisualMatcherFactory.createLocationMatcher();
    }

    @Override
    public void AnalyzeImage(State state, @Nullable AWTCanvas screenshot) {
        // Create new session
        startNewAnalysis();

        // Start ocr analysis, provide callback once finished.
        parseScreenshot(screenshot);

        // Start extracting text, provide callback once finished.
        extractExpectedText(state);

        // Match the expected text with the detected text.
        matchText();

        updateVerdict(state);

        storeAnalysis();
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

    private void parseScreenshot(@Nullable AWTCanvas screenshot) {
        if (screenshot != null) {
            _ocrEngine.AnalyzeImage(screenshot.image(), this);

        } else {
            Logger.log(Level.ERROR, TAG, "No screenshot for current state");
        }
    }

    private void extractExpectedText(State state) {
        _extractor.ExtractExpectedText(state, this);
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

    private void updateVerdict(State state) {

        if (_matcherResult != null) {
            // Analysis the raw result and create a verdict.
            _matcherResult.getMatches();

            Verdict result = new Verdict(Verdict.SEVERITY_WARNING, "Not all texts has been recognized",
                    new TextVisualizer(new AbsolutePosition(10, 10), "->", RedPen));

        } else {
            // Set verdict to failure we should have a matcher result as minimal input.
        }
        Logger.log(Level.INFO, TAG, "Updating verdict {}");
    }

    private void storeAnalysis() {
        Logger.log(Level.INFO, TAG, "Storing analysis");
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
