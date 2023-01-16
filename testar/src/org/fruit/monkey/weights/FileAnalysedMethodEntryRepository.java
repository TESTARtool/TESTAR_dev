package org.fruit.monkey.weights;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileAnalysedMethodEntryRepository implements AnalysedMethodEntryRepository {

    private final String filePath;
    private HashMap<String, ArrayList<AnalysedMethodEntry>> methodEntries;

    private static final String DEFAULT_REPOSITORY_FILE_PATH = "static-analysis-repository";

    @Override
    public boolean staticAnalysisResultAvailable() {
        return Files.exists(Path.of(filePath));
    }

    public FileAnalysedMethodEntryRepository(String filePath) {
        this.filePath = filePath;
    }

    public FileAnalysedMethodEntryRepository() {
        this.filePath = Paths.get(System.getProperty("user.dir"))
                             .getParent()
                             .resolve(DEFAULT_REPOSITORY_FILE_PATH)
                             .toString();
    }

    @Override
    public List<AnalysedMethodEntry> findByClassName(String className) {
        if (methodEntries == null) {
            readMethodEntries();
        }
        return methodEntries.get(className);
    }

    private void readMethodEntries() {
        try (
                FileInputStream fileInputStream = new FileInputStream(filePath);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        ) {
            this.methodEntries = (HashMap<String, ArrayList<AnalysedMethodEntry>>) objectInputStream.readObject();
        } catch (IOException e) {
            throw FileRepositoryException.repositoryFileNotFound(filePath, e);
        } catch (ClassNotFoundException e) {
            throw FileRepositoryException.repositoryFileDeserializationIssue(filePath, e);
        }
    }

    @Override
    public void saveAll(HashMap<String, List<AnalysedMethodEntry>> entries) {
        try (
                var fileOutputStream = new FileOutputStream(filePath);
                var objectOutputStream = new ObjectOutputStream(fileOutputStream);
        ) {
            objectOutputStream.writeObject(convertLists(entries));
            objectOutputStream.flush();
        } catch (IOException e) {
            throw FileRepositoryException.repositoryFileNotFound(filePath, e);
        }
    }

    private HashMap<String, ArrayList<AnalysedMethodEntry>> convertLists(HashMap<String, List<AnalysedMethodEntry>> entries) {
        return entries.entrySet()
                      .stream()
                      .map(entry -> Map.entry(entry.getKey(), new ArrayList<AnalysedMethodEntry>(entry.getValue())))
                      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, HashMap::new));
    }
}
