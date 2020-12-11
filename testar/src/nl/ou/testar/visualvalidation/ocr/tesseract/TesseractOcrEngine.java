package nl.ou.testar.visualvalidation.ocr.tesseract;

import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;
import nl.ou.testar.visualvalidation.ocr.OcrResultCallback;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.bytedeco.tesseract.ETEXT_DESC;
import org.bytedeco.tesseract.ResultIterator;
import org.bytedeco.tesseract.TessBaseAPI;
import org.bytedeco.tesseract.global.tesseract;
import org.testar.Logger;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * OCR engine implementation dedicated for the Tesseract engine.
 * Analyzes the given image via {@link #AnalyzeImage} in a separate thread.
 * Once the engine has finished the caller shall be informed via the given callback.
 *
 * The current implementation can only process one image at the time. This is enforced by the synchronized statements in
 * {@link #AnalyzeImage} where we set the buffer which is used by the engine and the actual execution of the engine in
 * the {@link #run}.
 */
public class TesseractOcrEngine extends Thread implements OcrEngineInterface {
    private static final String TAG = "Tesseract";

    AtomicBoolean running = new AtomicBoolean(true);

    private final TessBaseAPI _engine;
    private final Boolean _scanSync = true;
    private final int _imageResolution;
    private BufferedImage _image = null;
    private OcrResultCallback _callback = null;

    public TesseractOcrEngine() {
        _engine = new TessBaseAPI();
        TesseractSettings config = ExtendedSettingsFactory.createTesseractSetting();
        _imageResolution = config.imageResolution;

        if (_engine.Init(config.dataPath, config.language) != 0) {
            Logger.log(Level.ERROR, TAG, "Could not initialize tesseract.");
        }

        Logger.log(Level.INFO, TAG, "Tesseract engine created; Language:{} Data path:{}",
                config.language, config.dataPath);

        setName(TAG);
        start();
    }

    @Override
    public void AnalyzeImage(BufferedImage image, OcrResultCallback callback) {
        synchronized (_scanSync) {
            _image = image;
            _callback = callback;
            Logger.log(Level.TRACE, TAG, "Queue new image scan.");
            _scanSync.notify();
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            synchronized (_scanSync) {
                try {
                    // Wait until we need to inspect a new image.
                    _scanSync.wait();
                    if (!running.get()) {
                        break;
                    }
                    recognizeText();

                } catch (InterruptedException e) {
                    // Happens if someone interrupts your thread.
                    Logger.log(Level.INFO, TAG, "Wait interrupted");
                    e.printStackTrace();
                }
            }
        }
    }

    private void recognizeText() {
        List<RecognizedElement> recognizedWords = new ArrayList<>();

        loadImageIntoEngine(_image);

        if (_engine.Recognize(new ETEXT_DESC()) != 0) {
            Logger.log(Level.ERROR, TAG, "Could not process image.");
        } else {
            try (ResultIterator recognizedElement = _engine.GetIterator()) {
                int level = tesseract.RIL_WORD;
                do {
                    recognizedWords.add(TesseractResult.Extract(recognizedElement, level));
                } while (recognizedElement.Next(level));

                recognizedWords.forEach(ocrWord -> Logger.log(Level.DEBUG, TAG, "Found {}", ocrWord));
            }
        }
        _engine.Clear();
        // Notify the callback with the discovered words.
        _callback.reportResult(recognizedWords);

        Logger.log(Level.DEBUG, TAG, "Finished image scan found {} elements.", recognizedWords.size());
    }

    private void loadImageIntoEngine(BufferedImage image) {
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

        _engine.SetImage(byteBuffer, image.getWidth(), image.getHeight(), bytes_per_pixel, bytes_per_line);
        _engine.SetSourceResolution(_imageResolution);
    }

    @Override
    public void Destroy() {
        stopAndJoinThread();

        _engine.End();
        Logger.log(Level.DEBUG, TAG, "Engine destroyed.");
    }

    private void stopAndJoinThread() {
        synchronized (_scanSync) {
            running.set(false);
            _scanSync.notify();
        }

        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
