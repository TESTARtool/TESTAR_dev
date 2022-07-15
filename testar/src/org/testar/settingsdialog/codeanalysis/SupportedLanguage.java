package org.testar.settingsdialog.codeanalysis;

public class SupportedLanguage {
    private String name;
    private String fileExtension;

    public SupportedLanguage(String name, String fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}
