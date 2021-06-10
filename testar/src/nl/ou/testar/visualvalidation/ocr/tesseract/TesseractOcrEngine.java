package nl.ou.testar.visualvalidation.ocr.tesseract;

import nl.ou.testar.visualvalidation.ocr.OcrEngineInterface;
import nl.ou.testar.visualvalidation.ocr.OcrResultCallback;
import nl.ou.testar.visualvalidation.ocr.RecognizedElement;
import org.apache.logging.log4j.Level;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.LeptonicaFrameConverter;
import org.bytedeco.tesseract.ETEXT_DESC;
import org.bytedeco.tesseract.ResultIterator;
import org.bytedeco.tesseract.TessBaseAPI;
import org.bytedeco.tesseract.global.tesseract;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.testar.Logger;
import org.testar.settings.ExtendedSettingsFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.bytedeco.leptonica.global.lept.pixRead;
import static org.bytedeco.tesseract.global.tesseract.PSM_AUTO_OSD;

/**
 * OCR engine implementation dedicated for the Tesseract engine.
 * Analyzes the given image via {@link #AnalyzeImage} in a separate thread.
 * Once the engine has finished the caller shall be informed via the given callback.
 * <p>
 * The current implementation can only process one image at the time. This is enforced by the synchronized statements in
 * {@link #AnalyzeImage} where we set the buffer which is used by the engine and the actual execution of the engine in
 * the {@link #run}.
 */
public class TesseractOcrEngine extends Thread implements OcrEngineInterface {
    private static final String TAG = "Tesseract";
    private final TessBaseAPI _engine;
    private final Boolean _scanSync = true;
    private final int _imageResolution;
    private final boolean _loggingEnabled;
    private final boolean _saveImageBufferToDisk;
    AtomicBoolean running = new AtomicBoolean(true);
    private BufferedImage _image = null;
    private OcrResultCallback _callback = null;

    public TesseractOcrEngine() {
        _engine = new TessBaseAPI();
        TesseractSettings config = ExtendedSettingsFactory.createTesseractSetting();
        _imageResolution = config.imageResolution;
        _loggingEnabled = config.loggingEnabled;
        _saveImageBufferToDisk = config.saveImageBufferToDisk;

        if (_engine.Init(config.dataPath, config.language) != 0) {
            Logger.log(Level.ERROR, TAG, "Could not initialize tesseract.");
        }

        if (_loggingEnabled) {
            Logger.log(Level.INFO, TAG, "Tesseract engine created; Language:{} Data path:{}",
                    config.language, config.dataPath);
        }

        setName(TAG);
        start();
    }

    @Override
    public void AnalyzeImage(BufferedImage image, OcrResultCallback callback) {
        synchronized (_scanSync) {
            _image = image;
            _callback = callback;
            if (_loggingEnabled) {
                Logger.log(Level.TRACE, TAG, "Queue new image scan.");
            }
            _scanSync.notifyAll();
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
                    Logger.log(Level.ERROR, TAG, "Wait interrupted");
                    e.printStackTrace();
                }
            }
        }
    }

    private void recognizeText() {
        if (_image == null || _callback == null) {
            Logger.log(Level.ERROR, TAG, "Should not try to detect text on empty image/callback");
            return;
        }

        List<RecognizedElement> recognizedWords = new ArrayList<>();

        _engine.SetPageSegMode(PSM_AUTO_OSD);

        loadImageIntoEngine(_image);

        if (_engine.Recognize(new ETEXT_DESC()) != 0) {
            Logger.log(Level.ERROR, TAG, "Could not process image.");
        } else {
            try (ResultIterator recognizedElement = _engine.GetIterator()) {
                int level = tesseract.RIL_WORD;
                do {
                    recognizedWords.add(TesseractResult.Extract(recognizedElement, level));
                } while (recognizedElement.Next(level));
            }
        }
        _engine.Clear();

        // Filter out the empty items and notify the callback with the discovered words.
        _callback.reportResult(recognizedWords.stream()
                .filter(recognizedElement -> !recognizedElement._text.isEmpty())
                .collect(Collectors.toList())
        );
    }

    private void loadImageIntoEngine(@NonNull BufferedImage image) {
        // Ideally we use the raw data but unfortunately the webdriver screenshots have a different format which doesn't
        // work with the current conversion for loading a raw buffer.
        switch (image.getType()) {
            case TYPE_INT_ARGB:
                // Works for desktop protocol.
                loadImageAsRawData(image);
                break;
            case TYPE_4BYTE_ABGR:
                // Works for webdriver protocol.
                loadImageAsPix(image);
                break;
            default:
                throw new IllegalArgumentException("Loading OCR image not supported for image type:" + image.getType());
        }
    }

    private void loadImageAsRawData(@NonNull BufferedImage image) {
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

    private void loadImageAsPix(@NonNull BufferedImage image) {
        // Works for web driver
        if (_saveImageBufferToDisk) {
            try {
                File outputFile = File.createTempFile("testar", ".png");
                outputFile.deleteOnExit();
                ImageIO.write(_image, "png", outputFile);
                _engine.SetImage(pixRead(outputFile.getAbsolutePath()));
                _engine.SetSourceResolution(_imageResolution);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (Java2DFrameConverter converter = new Java2DFrameConverter();
                 LeptonicaFrameConverter converter2 = new LeptonicaFrameConverter()) {
                // Convert the buffered image into a PIX.
                _engine.SetImage(converter2.convert(converter.convert(image)));
            } catch (Exception e) {
                Logger.log(Level.ERROR, TAG, "Failed to convert buffered image into PIX");
            }
        }
    }

    @Override
    public void Destroy() {
        stopAndJoinThread();

        _engine.End();
        if (_loggingEnabled) {
            Logger.log(Level.DEBUG, TAG, "Engine destroyed.");
        }
    }

    private void stopAndJoinThread() {
        synchronized (_scanSync) {
            running.set(false);
            _scanSync.notifyAll();
        }

        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
