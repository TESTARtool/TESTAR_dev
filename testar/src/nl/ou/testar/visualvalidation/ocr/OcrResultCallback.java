package nl.ou.testar.visualvalidation.ocr;

import java.util.List;

public interface OcrResultCallback {
    void reportResult(List<RecognizedElement> items);
}
