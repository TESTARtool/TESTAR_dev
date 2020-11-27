package nl.ou.testar.visualvalidation;

import nl.ou.testar.visualvalidation.matcher.VisualMatcher;
import nl.ou.testar.visualvalidation.matcher.VisualMatcherFactory;
import nl.ou.testar.visualvalidation.ocr.OcrConfiguration;
import nl.ou.testar.visualvalidation.ocr.OcrEngineFactory;
import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;

import java.awt.image.BufferedImage;

public class VisualValidator implements VisualValidationManager {
    private final OcrEngineInterface ocrEngine;
    private final VisualMatcher matcher;

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
    public void AnalyzeImage(BufferedImage image) {

        ocrEngine.ScanImage(image);
    }

    @Override
    public void Close() {
        matcher.destroy();
        if (ocrEngine != null) {
            ocrEngine.Destroy();
        }
    }
}
