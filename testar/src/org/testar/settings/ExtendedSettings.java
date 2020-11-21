package org.testar.settings;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Generic XML root element.
 */
@XmlRootElement(name = "root")
@XmlAccessorType(XmlAccessType.FIELD)
class ExtendedSettingsXml implements Serializable {
    // Holds all the XML elements found in the file.
    @XmlAnyElement(lax = true)
    public List<Object> any;

    public Integer version;

    public ExtendedSettingsXml() {
        any = new ArrayList<>();
        version = 1;
    }
}

/**
 * Helper class for reading XML data from disk.
 */
class ExtractionResult {
    final JAXBContext Context;
    final ExtendedSettingsXml Data;
    final Boolean FileNotFound;

    public ExtractionResult(JAXBContext context, ExtendedSettingsXml data, Boolean fileNotFound) {
        Context = context;
        Data = data;
        FileNotFound = fileNotFound;
    }
}

public class ExtendedSettings implements Serializable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String FileName = "ExtendedSettings.xml";
    private final String _absolutePath;
    private final ReentrantReadWriteLock _fileAccessMutex;

    /**
     * Stores a deep copy of the object up on reading. The {@link #load(Class)} returns a reference to the XML element.
     * If we modify that reference and want to save it, we need to replace the old value. We use {@code #_loadedValue}
     * to find and update the tag in the XML file.
     */
    private Object _loadedValue = null;

    /**
     * Constructor, each specialization must have a unique implementation of this class.
     *
     * @param fileLocation    The absolute path the the XML file.
     * @param fileAccessMutex Mutex for thread-safe access.
     */
    protected ExtendedSettings(@NonNull String fileLocation, @NonNull ReentrantReadWriteLock fileAccessMutex) {
        _fileAccessMutex = fileAccessMutex;
        _absolutePath = System.getProperty("user.dir") +
                (fileLocation.startsWith(".") ? fileLocation.substring(1) : (fileLocation.startsWith(File.separator)
                        ? fileLocation : File.separator + fileLocation));
    }

    /**
     * Try to load the requested data element from the XML file.
     *
     * @param clazz The class type of the element we want to load.
     * @param <T>   The class type of the element we want to load.
     * @return When found in the XML the requested element, otherwise null.
     */
    @SuppressWarnings("unchecked")
    public <T> T load(@SuppressWarnings("rawtypes") @NonNull Class clazz) {
        T result = null;

        // Check if file exits
        ExtractionResult rd = readFile(clazz);

        if (Objects.nonNull(rd.Data)) {
            // Try to find the section of interest.
            result = (T) rd.Data.any.stream()
                    .filter(element -> element.getClass() == clazz)
                    .findFirst()
                    .orElse(null);

            // We only support loading a single element for now.
            if (rd.Data.any.stream().filter(element -> element.getClass() == clazz).count() > 1) {
                LOGGER.error("Duplicate elements found for {}, returning first element ", clazz);
            }

            // Store the current content, so we can replace it when needed.
            _loadedValue = SerializationUtils.clone((Serializable) result);
        }
        if (result == null) {
            LOGGER.info("Did not found XML element for class: {}", clazz);
        }

        return result;
    }

    /**
     * Try to load the requested data element from the XML file.
     * If not found, the default configuration is written to disk and returned.
     *
     * @param clazz          The class type of the element we want to load.
     * @param defaultFunctor The function to create the default configuration for class #clazz.
     * @param <T>            The class type of the element we want to load.
     * @return Either the element found in the file otherwise the default configuration.
     */
    public <T> T load(@SuppressWarnings("rawtypes") @NonNull Class clazz, @NonNull IExtendedSettingDefaultValue<T> defaultFunctor) {
        T result = load(clazz);

        if (result == null) {
            LOGGER.info("Writing default values for {}", clazz);
            save(defaultFunctor.CreateDefault());
            return load(clazz);
        }

        return result;
    }

    /**
     * Save the data to the XML file.
     * The file is created if it does not exist.
     *
     * @param data The data we need to store.
     */
    public void save(@NonNull Object data) {
        if (!(data instanceof Comparable)) {
            LOGGER.error("Object {} is not extending Comparable", data);
            return;
        }

        ExtractionResult result = readFile(data.getClass());
        updateFile(data, result.Context, result.Data);
    }

    @SuppressWarnings("rawtypes")
    private ExtractionResult extractContent(@NonNull Class clazz) {
        JAXBContext context = null;
        ExtendedSettingsXml data = null;
        boolean fileNotFound = false;

        try {
            FileInputStream xmlFile = null;
            context = JAXBContext.newInstance(ExtendedSettingsXml.class, clazz);
            Unmarshaller um = context.createUnmarshaller();

            try {
                _fileAccessMutex.readLock().lock();
                // Load latest version of XML, other settings may have been updated in the mean time.
                xmlFile = new FileInputStream(_absolutePath);
                data = (ExtendedSettingsXml) um.unmarshal(xmlFile);
            } catch (FileNotFoundException e) {
                fileNotFound = true;
            } finally {
                if (Objects.nonNull(xmlFile)) {
                    xmlFile.close();
                }
            }
        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        } finally {
            _fileAccessMutex.readLock().unlock();
        }
        return new ExtractionResult(context, data, fileNotFound);
    }

    @SuppressWarnings("rawtypes")
    private ExtractionResult readFile(@NonNull Class clazz) {

        ExtractionResult result = extractContent(clazz);
        if (result.FileNotFound) {
            createExtendedSettingsFile();
            result = extractContent(clazz);
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    private void updateFile(@NonNull Object data, JAXBContext context, ExtendedSettingsXml xmlData) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(xmlData);

        try {
            _fileAccessMutex.writeLock().lock();
            OutputStream os = null;
            try {
                os = new FileOutputStream(_absolutePath);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                // Try to find the old XML tag.
                Object found = null;
                for (Object element : xmlData.any) {
                    if (element.getClass() == data.getClass()) {
                        if (ObjectUtils.compare((Comparable) element, (Comparable) _loadedValue) == 0) {
                            found = element;
                        }
                    }
                }

                // Replace old content with new.
                xmlData.any.remove(found);
                xmlData.any.add(data);

                // Update the file.
                marshaller.marshal(xmlData, os);
            } catch (JAXBException e) {
                e.printStackTrace();
            } finally {
                if (Objects.nonNull(os)) {
                    os.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            _fileAccessMutex.writeLock().unlock();
        }
    }

    private void createExtendedSettingsFile() {
        try {
            _fileAccessMutex.writeLock().lock();
            OutputStream os = null;
            try {
                os = new FileOutputStream(_absolutePath);
                JAXBContext context = JAXBContext.newInstance(ExtendedSettingsXml.class);
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(new ExtendedSettingsXml(), os);
                LOGGER.info("Created extended settings file: {}", _absolutePath);
            } catch (JAXBException e) {
                e.printStackTrace();
            } finally {
                if (Objects.nonNull(os)) {
                    os.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            _fileAccessMutex.writeLock().unlock();
        }
    }
}
