package nl.ou.testar.visualvalidation.ocr.dummy;

import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;
import nl.ou.testar.visualvalidation.ocr.OcrResultCallback;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DummyOcrEngine implements OcrEngineInterface {
    @Override
    public void AnalyzeImage(BufferedImage image, OcrResultCallback callback) {
        // Immediately trigger the callback.
        callback.reportResult(new ArrayList<>());
    }

    @Override
    public void Destroy() {

    }
}
