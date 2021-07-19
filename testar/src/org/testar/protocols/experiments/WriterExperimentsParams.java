package org.testar.protocols.experiments;

public class WriterExperimentsParams {

    public static class WriterExperimentsParamsBuilder {
        String filename = "";

        String information = "";

        boolean newLine = true;

        public WriterExperimentsParamsBuilder setFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public WriterExperimentsParamsBuilder setInformation(String information) {
            this.information = information;
            return this;
        }

        public WriterExperimentsParamsBuilder setNewLine(boolean newLine) {
            this.newLine = newLine;
            return this;
        }

        public WriterExperimentsParams build(){
            return new WriterExperimentsParams(filename, information, newLine);
        }
    }

    String filename = "";

    String information = "";

    boolean newLine = false;

    public String getFilename() {
        return filename;
    }

    public String getInformation() {
        return information;
    }

    public boolean isNewLine() {
        return newLine;
    }

    private WriterExperimentsParams(String filename, String information, boolean newLine) {
        this.filename = filename;
        this.information = information;
        this.newLine = newLine;
    }
}
