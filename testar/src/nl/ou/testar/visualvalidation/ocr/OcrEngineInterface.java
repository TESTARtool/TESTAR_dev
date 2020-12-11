package nl.ou.testar.visualvalidation.ocr;

import java.awt.image.BufferedImage;

public interface OcrEngineInterface {
    /**
     * Analyze the given image with an OCR engine and return the detected text via the callback.
     *
     * @param image    The image we want to analyze.
     * @param callback Callback function for returning the detected text.
     */
    void AnalyzeImage(BufferedImage image, OcrResultCallback callback);

    /**
     * Destroy the OCR engine.
     */
    void Destroy();
}
