package nl.ou.testar.visualvalidation.ocr;

import java.util.List;

/**
 * Callback function for sharing the detected text by an OCR engine.
 */
public interface OcrResultCallback {
    /**
     * Report the detected text to the caller.
     *
     * @param detectedText The detected text by the OCR engine.
     */
    void reportResult(List<RecognizedElement> detectedText);
}
