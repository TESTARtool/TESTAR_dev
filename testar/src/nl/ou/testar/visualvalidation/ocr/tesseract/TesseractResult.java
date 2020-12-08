package nl.ou.testar.visualvalidation.ocr.tesseract;

import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.tesseract.ResultIterator;

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
        IntPointer x1 = intPointerSupplier.get();
        IntPointer y1 = intPointerSupplier.get();
        IntPointer x2 = intPointerSupplier.get();
        IntPointer y2 = intPointerSupplier.get();
        boolean foundRectangle = recognizedElement.BoundingBox(granularity, x1, y1, x2, y2);

        if (!foundRectangle) {
            throw new IllegalArgumentException("Could not find any rectangle for this element");
        }

        RecognizedElement result = new RecognizedElement(x1.get(), y1.get(), x2.get(), y2.get(), confidence, recognizedText);

        x1.deallocate();
        y1.deallocate();
        x2.deallocate();
        y2.deallocate();
        ocrResult.deallocate();

        return result;
    }
}
