package org.testar.protocols;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;
import org.fruit.monkey.codeanalysis.CodeAnalysisService;
import org.fruit.monkey.codeanalysis.CodeAnalysisServiceImpl;
import org.fruit.monkey.dialog.ProgressDialog;

import java.nio.file.Path;

public class SonarqubeProtocol extends GenericUtilsProtocol {

    private Path repositoriesDir;
    private CodeAnalysisService codeAnalysisService;

    @Override
    protected void preSequencePreparations() {
        codeAnalysisService = new CodeAnalysisServiceImpl();
    }

    @Override
    protected void initialize(Settings settings) {
        ProgressDialog progressDialog = new ProgressDialog();
        if (settings.get(ConfigTags.GitUrl).isEmpty()) {
            progressDialog.setStatusString("No Git URL detected.");
            closeTestSession();
        }

        codeAnalysisService.scanRepository(repositoriesDir);
    }



}