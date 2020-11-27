package nl.ou.testar.visualvalidation.ocr;

import nl.ou.testar.visualvalidation.ocr.dummy.DummyOcrEngine;
import nl.ou.testar.visualvalidation.ocr.tesseract.TesseractOcrEngine;

public class OcrEngineFactory {

    public static OcrEngineInterface createOcrEngine(OcrConfiguration settings) {
        if (settings.engine.contentEquals(OcrConfiguration.TESSERACT_ENGINE)) {
            return createTesseractEngine();
        } else {
            return createDummyEngine();
        }
    }

    static OcrEngineInterface createTesseractEngine() {
        return new TesseractOcrEngine();
    }

    static OcrEngineInterface createDummyEngine() {
        return new DummyOcrEngine();
    }
}
