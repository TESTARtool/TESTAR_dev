package nl.ou.testar.visualvalidation.ocr.tesseract;

import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.tesseract.ResultIterator;

import java.awt.Rectangle;
import java.util.function.Supplier;

/**
 * Convert a tesseract result into a {@link RecognizedElement}
 */
public class TesseractResult {

    /**
     * Convert a {@link TesseractResult} into a {@link RecognizedElement}
     *
     * @param recognizedElement The recognized element.
     * @param granularity       The granularity of recognized element, range from a block till individual character.
     *                          See tesseract::PageIteratorLevel for more information.
     * @return An {@link RecognizedElement}.
     */
    static RecognizedElement Extract(ResultIterator recognizedElement, int granularity) {
        Supplier<IntPointer> intPointerSupplier = () -> new IntPointer(new int[1]);

        BytePointer ocrResult = recognizedElement.GetUTF8Text(granularity);
        String recognizedText = ocrResult.getString().trim();

        float confidence = recognizedElement.Confidence(granularity);
        IntPointer left = intPointerSupplier.get();
        IntPointer top = intPointerSupplier.get();
        IntPointer right = intPointerSupplier.get();
        IntPointer bottom = intPointerSupplier.get();
        boolean foundRectangle = recognizedElement.BoundingBox(granularity, left, top, right, bottom);

        if (!foundRectangle) {
            throw new IllegalArgumentException("Could not find any rectangle for this element");
        }

        // Upper left coordinate = 0,0
        int width = right.get() - left.get();
        int height = bottom.get() - top.get();
        Rectangle location = new Rectangle(left.get(), top.get(), width, height);
        RecognizedElement result = new RecognizedElement(location, confidence, recognizedText);

        left.deallocate();
        top.deallocate();
        right.deallocate();
        bottom.deallocate();
        ocrResult.deallocate();

        return result;
    }
}
