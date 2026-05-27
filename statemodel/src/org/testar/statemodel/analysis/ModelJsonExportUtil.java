/***************************************************************************************************
 *
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.statemodel.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ModelJsonExportUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ModelJsonExportUtil() {
    }

    public static ModelJsonExportResult exportModelFiles(String outputDir,
                                                         String modelIdentifier,
                                                         String hybridFilename,
                                                         Object hybridJsonModel,
                                                         String abstractFilename,
                                                         Object abstractJsonModel,
                                                         List<ScreenshotExport> screenshotExports) {
        String safeHybridFilename = (hybridFilename == null || hybridFilename.trim().isEmpty()) ? "model_hybrid.json" : hybridFilename;
        String safeAbstractFilename = (abstractFilename == null || abstractFilename.trim().isEmpty()) ? "model_abstract.json" : abstractFilename;
        String screenshotFolderName = removeJsonExtension(safeHybridFilename) + "_screenshots";
        File modelFolder = ensureOutputSubFolder(outputDir, modelIdentifier);
        File screenshotFolder = ensureOutputSubFolder(outputDir, modelIdentifier + File.separator + screenshotFolderName);
        File hybridOutputFile = new File(modelFolder, safeHybridFilename);
        File abstractOutputFile = new File(modelFolder, safeAbstractFilename);

        ModelJsonExportResult result = new ModelJsonExportResult();
        result.hybridFilename = safeHybridFilename;
        result.abstractFilename = safeAbstractFilename;
        result.exportFolder = modelFolder.getAbsolutePath();
        result.screenshotFolder = screenshotFolder.getAbsolutePath();
        result.screenshotCount = 0;
        result.missingScreenshots = new ArrayList<>();

        try {
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(hybridOutputFile, hybridJsonModel);
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValue(abstractOutputFile, abstractJsonModel);
        } catch (IOException e) {
            throw new RuntimeException("Unable to write model json exports", e);
        }

        if (screenshotExports != null) {
            for (ScreenshotExport screenshotExport : screenshotExports) {
                if (screenshotExport == null) {
                    continue;
                }

                String sourceImageName = screenshotExport.SourceImageName;
                String outputImageName = screenshotExport.OutputImageName;

                if (sourceImageName == null || sourceImageName.trim().isEmpty()) {
                    continue;
                }
                if (outputImageName == null || outputImageName.trim().isEmpty()) {
                    continue;
                }

                File sourceFile = new File(modelFolder, sourceImageName);
                File targetFile = new File(screenshotFolder, outputImageName);

                if (!sourceFile.isFile()) {
                    result.missingScreenshots.add(sourceImageName);
                    continue;
                }

                try {
                    copyFile(sourceFile.toPath(), targetFile.toPath());
                    result.screenshotCount++;
                } catch (IOException e) {
                    throw new RuntimeException("Unable to copy model export screenshot " + sourceImageName, e);
                }
            }
        }

        return result;
    }

    public static File ensureOutputSubFolder(String outputDir, String subFolderName) {
        File subFolder = new File(getOutputBaseDir(outputDir), subFolderName);
        if (!subFolder.isDirectory() && !subFolder.mkdirs()) {
            throw new RuntimeException("Unable to create output folder " + subFolder.getAbsolutePath());
        }
        return subFolder;
    }

    private static File getOutputBaseDir(String outputDir) {
        String normalizedOutputDir = outputDir;
        if (!normalizedOutputDir.substring(normalizedOutputDir.length() - 1).equals(File.separator)) {
            normalizedOutputDir += File.separator;
        }
        return new File(normalizedOutputDir);
    }

    private static String removeJsonExtension(String filename) {
        if (filename == null) {
            return "model_hybrid";
        }
        if (filename.toLowerCase(Locale.ROOT).endsWith(".json")) {
            return filename.substring(0, filename.length() - 5);
        }
        return filename;
    }

    private static void copyFile(Path sourceFile, Path targetFile) throws IOException {
        Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
    }

    public static class ScreenshotExport {
        public String SourceImageName;
        public String OutputImageName;
    }

    public static class ModelJsonExportResult {
        public String hybridFilename;
        public String abstractFilename;
        public String exportFolder;
        public String screenshotFolder;
        public int screenshotCount;
        public List<String> missingScreenshots;
    }
}
