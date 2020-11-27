package nl.ou.testar.visualvalidation.ocr;

import java.awt.image.BufferedImage;

public interface OcrEngineInterface {
    void ScanImage(BufferedImage image);

    void Destroy();
}
