/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2025 Open Universiteit - www.ou.nl
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


package org.testar;

import org.testar.serialisation.LogSerialiser;
import org.testar.monkey.Util;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

import java.io.*;

public class FileHandling {

    public static String copyClassifiedSequence(String generatedSequence, File currentSeq, Verdict verdict) {
        // Generate the target folder names based on the severity title
        String targetFolder = "sequences_" + verdict.verdictSeverityTitle().toLowerCase();
        String targetSequence = targetFolder + File.separator + generatedSequence;

        LogSerialiser.log(
                String.format("Copying classified sequence (\"%s\") to %s folder...\n", generatedSequence, targetFolder),
                LogSerialiser.LogLevel.Info
                );

        // The .testar sequence file might not exist (e.g., GenerateReplayableSequence is disabled)
        try {
            if (!currentSeq.getCanonicalFile().exists()) {
                LogSerialiser.log(
                        String.format("No sequence file exists to classify for \"%s\".\n", generatedSequence),
                        LogSerialiser.LogLevel.Info
                        );

                LogSerialiser.log(
                        String.format("No sequence copied to output directory <%s>.\n", targetSequence),
                        LogSerialiser.LogLevel.Info
                        );

                return targetSequence;
            }
        } catch (IOException ioe) {
            LogSerialiser.log(
                    "Error checking whether the sequence file exists: " + ioe.getMessage() + "\n",
                    LogSerialiser.LogLevel.Critical
                    );
        }

        // If it exists, copy to the specific classification folder
        try {
            copyToOutputDir(currentSeq, targetFolder);
        } catch (NoSuchTagException | IOException e) {
            LogSerialiser.log("Error copying classified test sequence: " + e.getMessage() + "\n", 
                    LogSerialiser.LogLevel.Critical
                    );
        }

        LogSerialiser.log(
                String.format("Copied classified sequence to output <%s> directory!\n", targetSequence),
                LogSerialiser.LogLevel.Info
                );

        return targetSequence;
    }

    /**
     * Helper method to copy the sequence file to the specified output directory.
     *
     * @param file The sequence file to copy.
     * @param folderName The target folder name.
     * @throws IOException If an I/O error occurs.
     * @throws NoSuchTagException If the specified tag does not exist.
     */
    private static void copyToOutputDir(File file, String folderName) throws IOException, NoSuchTagException {
        Util.copyToDirectory(
                file.getCanonicalPath(),
                OutputStructure.outerLoopOutputDir + File.separator + folderName,
                true
                );
    }
}
