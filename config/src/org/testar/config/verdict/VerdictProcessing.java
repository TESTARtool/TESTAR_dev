/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2026 Open Universiteit - www.ou.nl
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

package org.testar.config.verdict;

import org.testar.config.TestarDirectories;
import org.testar.core.serialisation.LogSerialiser;
import org.testar.config.ConfigTags;
import org.testar.core.verdict.Verdict;
import org.testar.config.settings.Settings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VerdictProcessing {

    private static final String LIST_VERDICTS_FAILURES_FILENAME = "list_of_verdicts_with_failures.txt";

    private final boolean ignoreDuplicatedVerdicts;
    private final List<String> verdictIgnoreList = new ArrayList<>();
    private final File verdictIgnoreFile;

    public VerdictProcessing(Settings settings) {
        ignoreDuplicatedVerdicts = settings.get(ConfigTags.IgnoreDuplicatedVerdicts, false);
        verdictIgnoreFile = resolveVerdictIgnoreFile();

        if (!ignoreDuplicatedVerdicts || verdictIgnoreFile == null || !verdictIgnoreFile.exists()) {
            return;
        }

        try {
            Path path = verdictIgnoreFile.toPath();
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !verdictIgnoreList.contains(trimmed)) {
                    verdictIgnoreList.add(trimmed);
                }
            }
        } catch (IOException e) {
            LogSerialiser.log("Unable to read verdict ignore file: " + verdictIgnoreFile.getAbsolutePath() + "\n",
                    LogSerialiser.LogLevel.Info);
        }
    }

    public List<Verdict> filterDuplicates(List<Verdict> verdicts) {
        if (verdicts == null || verdicts.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        List<Verdict> filtered = new ArrayList<>();

        for (Verdict verdict : verdicts) {
            if (verdict == null) {
                continue;
            }

            if (shouldIgnorePersistedDuplicate(verdict)) {
                continue;
            }

            filtered.add(verdict);
        }

        if (filtered.isEmpty()) {
            return Collections.singletonList(Verdict.OK);
        }

        if (areAllVerdictsOk(filtered)) {
            return Collections.singletonList(Verdict.OK);
        }

        return clearOkIfFailurePresent(filtered);
    }

    public void storeNewVerdicts(List<Verdict> verdicts) {
        if (verdicts == null || verdicts.isEmpty()) {
            return;
        }
        for (Verdict verdict : verdicts) {
            storeVerdictFailInfoIfNew(verdict);
        }
    }

    public static File resolveVerdictIgnoreFile() {
        if (TestarDirectories.getSelectedSse() != null && !TestarDirectories.getSelectedSse().isEmpty()) {
            return new File(TestarDirectories.getSettingsDir() + TestarDirectories.getSelectedSse(),
                    LIST_VERDICTS_FAILURES_FILENAME);
        }
        String settingsPath = Settings.getSettingsPath();
        if (settingsPath != null && !settingsPath.isEmpty()) {
            return new File(settingsPath, LIST_VERDICTS_FAILURES_FILENAME);
        }
        return null;
    }

    private boolean isDuplicateVerdictInfo(String verdictInfo) {
        String normalized = normalizeVerdictInfo(verdictInfo);
        if (normalized.isEmpty()) {
            return false;
        }
        return verdictIgnoreList.stream().anyMatch(info -> info.contains(normalized));
    }

    private void storeVerdictFailInfoIfNew(Verdict verdict) {
        if (!ignoreDuplicatedVerdicts || verdict == null) {
            return;
        }
        if (verdict.isCompletion()) {
            return;
        }
        if (verdict.isCritical()) {
            return;
        }
        if (verdict.severity() <= Verdict.Severity.OK.getValue()) {
            return;
        }
        String normalized = normalizeVerdictInfo(verdict.info());
        if (normalized.isEmpty() || verdictIgnoreList.contains(normalized)) {
            return;
        }
        verdictIgnoreList.add(normalized);

        if (verdictIgnoreFile == null) {
            return;
        }
        try {
            Files.write(verdictIgnoreFile.toPath(),
                    Collections.singletonList(normalized),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            LogSerialiser.log("Unable to update verdict ignore file: " + verdictIgnoreFile.getAbsolutePath() + "\n",
                    LogSerialiser.LogLevel.Info);
        }
    }

    private String normalizeVerdictInfo(String verdictInfo) {
        return verdictInfo == null ? "" : verdictInfo.replace("\n", " ").trim();
    }

    private boolean shouldIgnorePersistedDuplicate(Verdict verdict) {
        return ignoreDuplicatedVerdicts
                && !verdict.isCompletion()
                && !verdict.isCritical()
                && verdict.severity() > Verdict.Severity.OK.getValue()
                && isDuplicateVerdictInfo(verdict.info());
    }

    private boolean areAllVerdictsOk(List<Verdict> verdicts) {
        for (Verdict verdict : verdicts) {
            if (verdict.severity() > Verdict.Severity.OK.getValue()) {
                return false;
            }
        }
        return true;
    }

    private List<Verdict> clearOkIfFailurePresent(List<Verdict> verdicts) {
        boolean hasFailureVerdict = false;
        for (Verdict verdict : verdicts) {
            if (verdict != null && verdict.severity() > Verdict.Severity.OK.getValue()) {
                hasFailureVerdict = true;
                break;
            }
        }
        if (!hasFailureVerdict) {
            return verdicts;
        }
        List<Verdict> filtered = new ArrayList<>();
        for (Verdict verdict : verdicts) {
            if (verdict != null && verdict.severity() > Verdict.Severity.OK.getValue()) {
                filtered.add(verdict);
            }
        }
        return filtered.isEmpty() ? verdicts : filtered;
    }

}
