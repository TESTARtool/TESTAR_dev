package nl.ou.testar.visualvalidation.ocr.tesseract;

import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.tesseract.ETEXT_DESC;
import org.bytedeco.tesseract.ResultIterator;
import org.bytedeco.tesseract.TessBaseAPI;
import org.bytedeco.tesseract.global.tesseract;
import org.testar.settings.ExtendedSettingsFactory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

public class TesseractOcrEngine implements OcrEngineInterface {
    private final TessBaseAPI engine;
    static final Logger LOGGER = LogManager.getLogger();

    public TesseractOcrEngine() {
        engine = new TessBaseAPI();
        TesseractSettings config = ExtendedSettingsFactory.createTesseractSetting();

        if (engine.Init(config.dataPath, config.language) != 0) {
            LOGGER.error("Could not initialize tesseract.");
        }

        LOGGER.info("Tesseract engine created; Language:{} Data path:{}", config.language, config.dataPath);
    }

    @Override
    public void ScanImage(BufferedImage image) {
        // TODO TM: Image analysis should be done on separate thread.
        loadImage(image);

        if (engine.Recognize(new ETEXT_DESC()) != 0) {
            // TODO TM: Should we throw or just log and proceed with the application and set the matcher result to unknown-ish
            throw new IllegalArgumentException("could not recognize text");
        }

        List<RecognizedElement> recognizedWords = new ArrayList<>();
        try (ResultIterator recognizedElement = engine.GetIterator()) {
            int level = tesseract.RIL_WORD;
            do {
                recognizedWords.add(TesseractResult.Extract(recognizedElement, level));
            } while (recognizedElement.Next(level));

            recognizedWords.forEach(ocrWord -> LOGGER.info("Found {}", ocrWord));
        }

        engine.Clear();
    }

    private void loadImage(BufferedImage image) {
        DataBuffer dataBuffer = image.getData().getDataBuffer();

        ByteBuffer byteBuffer;
        if (dataBuffer instanceof DataBufferByte) {
            byte[] pixelData = ((DataBufferByte) dataBuffer).getData();
            byteBuffer = ByteBuffer.wrap(pixelData);
        } else if (dataBuffer instanceof DataBufferUShort) {
            short[] pixelData = ((DataBufferUShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        } else if (dataBuffer instanceof DataBufferShort) {
            short[] pixelData = ((DataBufferShort) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 2);
            byteBuffer.asShortBuffer().put(ShortBuffer.wrap(pixelData));
        } else if (dataBuffer instanceof DataBufferInt) {
            int[] pixelData = ((DataBufferInt) dataBuffer).getData();
            byteBuffer = ByteBuffer.allocate(pixelData.length * 4);
            byteBuffer.asIntBuffer().put(IntBuffer.wrap(pixelData));
        } else {
            throw new IllegalArgumentException("Not implemented for data buffer type: " + dataBuffer.getClass());
        }

        // When applicable, alpha is included.
        int bytes_per_pixel = image.getColorModel().getNumComponents();
        int bytes_per_line = bytes_per_pixel * image.getWidth();

        engine.SetImage(byteBuffer, image.getWidth(), image.getHeight(), bytes_per_pixel, bytes_per_line);
        // TODO TM: Figure out which value we should use.
        engine.SetSourceResolution(160);
    }

    @Override
    public void Destroy() {
        engine.End();
        LOGGER.info("Tesseract engine destroyed");
    }
}
