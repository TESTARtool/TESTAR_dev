package org.fruit.monkey.codeanalysis;

import java.nio.file.Path;

public interface CodeAnalysisService {

    RepositoryLanguageComposition scanRepository(Path repositoryPath);
}
