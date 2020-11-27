package nl.ou.testar.visualvalidation.ocr.tesseract;

import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.tesseract.TessBaseAPI;
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
        BytePointer result = engine.GetUTF8Text();
        if (result != null) {
            LOGGER.info("Tesseract has found: {}", result.getString());
            result.deallocate();
        } else {
            LOGGER.warn("Failed to analyze image");
        }

    }

    @Override
    public void Destroy() {
        engine.End();
        LOGGER.info("Tesseract engine destroyed");
    }
}
