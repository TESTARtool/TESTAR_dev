package org.fruit.monkey.codeanalysis;

import java.util.List;

public class RepositoryLanguageComposition {

    private List<RepositoryLanguage> repositoryLanguages;
    private int linesOfCode;

    public RepositoryLanguageComposition(List<RepositoryLanguage> repositoryLanguages, int linesOfCode) {
        this.repositoryLanguages = repositoryLanguages;
        this.linesOfCode = linesOfCode;
    }

    public List<RepositoryLanguage> getRepositoryLanguages() {
        return repositoryLanguages;
    }

    public void setRepositoryLanguages(List<RepositoryLanguage> repositoryLanguages) {
        this.repositoryLanguages = repositoryLanguages;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }
}
