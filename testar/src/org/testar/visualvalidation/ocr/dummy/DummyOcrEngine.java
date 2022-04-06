package org.testar.visualvalidation.ocr.dummy;

import org.testar.visualvalidation.ocr.OcrEngineInterface;
import org.testar.visualvalidation.ocr.OcrResultCallback;

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
