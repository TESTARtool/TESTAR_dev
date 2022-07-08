package org.testar.settingsdialog.codeanalysis;

import java.nio.file.Path;

public interface CodeAnalysisService {

    RepositoryLanguageComposition scanRepository(Path repositoryPath);
}
