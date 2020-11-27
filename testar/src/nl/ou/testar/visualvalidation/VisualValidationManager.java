package nl.ou.testar.visualvalidation;

import java.awt.image.BufferedImage;

public interface VisualValidationManager {
    void AnalyzeImage(BufferedImage image);

    void Close();
}
