package org.testar.settingsdialog.codeanalysis;

import java.math.BigDecimal;

public class RepositoryLanguage {
    private SupportedLanguage supportedLanguage;
    private int linesOfCode;
    private BigDecimal percentage;

    public RepositoryLanguage(SupportedLanguage supportedLanguage) {
        this.supportedLanguage = supportedLanguage;
        linesOfCode = 0;
        percentage = new BigDecimal(0);
    }

    public SupportedLanguage getSupportedLanguage() {
        return supportedLanguage;
    }

    public void setSupportedLanguage(SupportedLanguage supportedLanguage) {
        this.supportedLanguage = supportedLanguage;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public void addLinesOfCode(int linesOfCodeToAdd) {
        this.linesOfCode += linesOfCodeToAdd;
    }
}
