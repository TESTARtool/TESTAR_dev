package org.testar.settingsdialog.codeanalysis;

import org.bridj.cpp.com.IDispatch;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class RepositoryLanguageCompositionBuilder {

    private List<RepositoryLanguage> repositoryLanguages;
    private List<String> extensionsToOmit;
    private int linesOfCode;
    public static final String OTHERS_NAME = "Others";
    public static final String OTHERS_EXT = "";
    private static final String PROPERTIES_FILE = "languages.properties";
    private static final String PROPERTIES_SUPPORTED_LANGUAGES = "supportedLanguages";
    private static final String PROPERTIES_EXTENSIONS_TO_OMIT = "extensionsToOmit";
    private static final String[] PROPERTIES_STRINGS_TO_REMOVE = new String[]{ "{", "}", "'"};
    private static final String PROPERTIES_ROW_SEPARATOR = ",";
    private static final String PROPERTIES_KEY_VALUE_SEPARATOR = ":";

    public RepositoryLanguageCompositionBuilder() {
        try {
            List<SupportedLanguage> supportedLanguages = loadSupportedLanguages();
            List<RepositoryLanguage> repositoryLanguages = supportedLanguages.stream()
                    .map(RepositoryLanguage::new)
                    .collect(Collectors.toList());
            this.repositoryLanguages = repositoryLanguages;
            this.extensionsToOmit = loadExtensionsToOmit();
            this.linesOfCode = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<SupportedLanguage> loadSupportedLanguages() throws IOException {
        Properties languagesProperties = new Properties();
        languagesProperties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        String languagesMap = languagesProperties.getProperty(PROPERTIES_SUPPORTED_LANGUAGES);
        return parseLanguages(languagesMap);
    }

    private List<SupportedLanguage> parseLanguages(String languagesMap) {
        String[] languagesMapRows = extractMapRows(languagesMap);
        return Arrays.stream(languagesMapRows).map((pairString) -> pairString.split(PROPERTIES_KEY_VALUE_SEPARATOR))
                .map((pair) -> new SupportedLanguage(pair[0].trim(), pair[1].trim()))
                .collect(Collectors.toList());
    }

    private String[] extractMapRows(String languagesMap) {
        for(String stringToDelete: PROPERTIES_STRINGS_TO_REMOVE) {
            languagesMap = languagesMap.replace(stringToDelete, "");
        }
        return languagesMap.split(PROPERTIES_ROW_SEPARATOR);
    }

    private List<String> loadExtensionsToOmit() throws IOException {
        Properties extensionsProperties = new Properties();
        extensionsProperties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
        String extensionsList = extensionsProperties.getProperty(PROPERTIES_EXTENSIONS_TO_OMIT);
        return parseExtensions(extensionsList);
    }

    private List<String> parseExtensions(String extensionsList) {
        String[] splitExtensions = extensionsList.split(PROPERTIES_ROW_SEPARATOR);
        List<String> extensions = new ArrayList<>();
        for(String extension: splitExtensions) {
            extensions.add(extension.trim());
        }
        return extensions;
    }

    public RepositoryLanguageCompositionBuilder addLinesOfCode(int linesOfCodeToAdd, String fileExtension) {
        Optional<RepositoryLanguage> foundLanguage = repositoryLanguages.stream()
                .filter((repositoryLanguage -> repositoryLanguage.getSupportedLanguage().getFileExtension().equals(fileExtension)))
                .findFirst();

        Optional<String> foundExtensionToOmit = extensionsToOmit.stream().filter((extensionToOmit) -> extensionToOmit.equals(fileExtension)).findFirst();
        if(foundLanguage.isPresent()) {
            foundLanguage.get().addLinesOfCode(linesOfCodeToAdd);
            this.linesOfCode += linesOfCodeToAdd;
        } else if(!foundExtensionToOmit.isPresent()) {
            addLinesOfCodeToOthers(linesOfCodeToAdd);
            this.linesOfCode += linesOfCodeToAdd;
        }
        return this;
    }

    private void addLinesOfCodeToOthers(int linesOfCode) {
        Optional<RepositoryLanguage> foundOthers = repositoryLanguages.stream()
                .filter((repositoryLanguage -> repositoryLanguage.getSupportedLanguage().getName().equals(OTHERS_NAME)))
                .findFirst();
        if(foundOthers.isPresent()) {
            foundOthers.get().addLinesOfCode(linesOfCode);
        } else {
            RepositoryLanguage others = new RepositoryLanguage(new SupportedLanguage(OTHERS_NAME, OTHERS_EXT));
            others.addLinesOfCode(linesOfCode);
            repositoryLanguages.add(others);
        }
    }

    public RepositoryLanguageCompositionBuilder trim() {
        List<RepositoryLanguage> trimmedRepositoryLanguages = this.repositoryLanguages.stream()
                .filter((repositoryLanguage -> repositoryLanguage.getLinesOfCode()>0))
                .collect(Collectors.toList());
        this.repositoryLanguages = trimmedRepositoryLanguages;
        return this;
    }

    public RepositoryLanguageCompositionBuilder calculatePercentages() {
        this.repositoryLanguages = this.repositoryLanguages.stream()
                .map(this::calculatePercentageOfLanguage)
                .collect(Collectors.toList());
        return this;
    }

    private RepositoryLanguage calculatePercentageOfLanguage(RepositoryLanguage repositoryLanguage) {
        double percentage = 100.0*((double)repositoryLanguage.getLinesOfCode()/linesOfCode);
        repositoryLanguage.setPercentage(new BigDecimal(percentage));
        return repositoryLanguage;
    }

    public RepositoryLanguageComposition build() {
        return new RepositoryLanguageComposition(this.repositoryLanguages, linesOfCode);
    }
}
