package org.fruit.monkey.codeanalysis;

import java.io.*;
import java.nio.file.Path;

public class CodeAnalysisServiceImpl implements CodeAnalysisService {

    public CodeAnalysisServiceImpl() {}

    @Override
    public RepositoryLanguageComposition scanRepository(Path repositoryPath) {
        File repositoryDirectory = new File(repositoryPath.toAbsolutePath().toString());
        RepositoryLanguageCompositionBuilder compositionBuilder = new RepositoryLanguageCompositionBuilder();
        scanDirectory(repositoryDirectory, compositionBuilder);
        return compositionBuilder
                .trim()
                .calculatePercentages()
                .build();
    }


    public void scanFile(File file, RepositoryLanguageCompositionBuilder compositionBuilder) {
        try {
            int fileLines = extractLinesCount(file);
            String fileExtension = extractFileExtension(file);
            compositionBuilder.addLinesOfCode(fileLines, fileExtension);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int extractLinesCount(File file) throws IOException {
        LineNumberReader reader = new LineNumberReader(new FileReader(file));
        reader.skip(Long.MAX_VALUE);
        return reader.getLineNumber() + 1;
    }

    private String extractFileExtension(File file) {
        String[] splitFileName = file.getName().split("\\.");
        String fileExtension = "." + splitFileName[splitFileName.length - 1];
        return fileExtension;
    }

    public void scanDirectory(File directory, RepositoryLanguageCompositionBuilder compositionBuilder) {
        for(File directoryFile: directory.listFiles()) {
            if(directoryFile.isDirectory()) {
                scanDirectory(directoryFile, compositionBuilder);
            } else {
                scanFile(directoryFile, compositionBuilder);
            }
        }
    }
}
