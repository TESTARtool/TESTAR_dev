package nl.ou.testar.visualvalidation;

import nl.ou.testar.visualvalidation.matcher.VisualMatcher;
import nl.ou.testar.visualvalidation.matcher.VisualMatcherFactory;
import nl.ou.testar.visualvalidation.ocr.OcrConfiguration;
import nl.ou.testar.visualvalidation.ocr.OcrEngineFactory;
import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;
import nl.ou.testar.visualvalidation.ocr.OcrResultCallback;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.alayer.AWTCanvas;
import org.fruit.alayer.State;
import org.testar.Logger;

import java.util.List;

public class VisualValidator implements VisualValidationManager, OcrResultCallback {
    private final OcrEngineInterface ocrEngine;
    private final VisualMatcher matcher;
    private final String TAG = "VisualValidator";
    private int analysisId = 0;
    private final Boolean _ocrResultReceived = false;
    private final Boolean _expectedTextReceived = false;
    private List<RecognizedElement> _ocrItems = null;

    public VisualValidator(VisualValidationSettings settings) {
        OcrConfiguration ocrConfig = settings.ocrConfiguration;
        if (ocrConfig.enabled) {
            ocrEngine = OcrEngineFactory.createOcrEngine(ocrConfig);
        } else {
            ocrEngine = null;
        }

        matcher = VisualMatcherFactory.createDummyMatcher();
    }

    @Override
    public void AnalyzeImage(State state, @Nullable AWTCanvas screenshot) {
        // Create new session
        startNewAnalysis();

        // Start ocr analysis, provide callback once finished.
        parseScreenshot(screenshot);

        // Start extracting text, provide callback once finished.
        extractExpectedText(state);

        // Wait for both results.
        matchText();

        updateVerdict(state);

        storeAnalysis();
    }

    private void startNewAnalysis() {
        analysisId++;
        Logger.log(Level.INFO, TAG, "Starting new analysis {}", analysisId);
    }

    private void parseScreenshot(@Nullable AWTCanvas screenshot) {
        if (screenshot != null) {
            ocrEngine.AnalyzeImage(screenshot.image(), this);

        } else {
            Logger.log(Level.ERROR, TAG, "No screenshot for current state");
        }
    }

    private void extractExpectedText(State state) {
        Logger.log(Level.INFO, TAG, "Extracting text");
    }

    private void matchText() {
        synchronized (_ocrResultReceived) {
            try {
                _ocrResultReceived.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Logger.log(Level.INFO, TAG, "Matching {} with {}", _ocrItems, "api");
    }

    private void updateVerdict(State state) {
        Logger.log(Level.INFO, TAG, "Updating verdict {}");
    }

    private void storeAnalysis() {
        Logger.log(Level.INFO, TAG, "Storing analysis");
    }

    @Override
    public void Destroy() {
        matcher.destroy();
        if (ocrEngine != null) {
            ocrEngine.Destroy();
        }
    }

    @Override
    public void reportResult(List<RecognizedElement> items) {
        synchronized (_ocrResultReceived) {
            _ocrItems = items;
            _ocrResultReceived.notify();
            Logger.log(Level.INFO, TAG, "Received {} result", items.size());
        }
    }
}
