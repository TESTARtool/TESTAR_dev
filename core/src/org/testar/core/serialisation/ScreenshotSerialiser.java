/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2013-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018-2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.serialisation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

import org.testar.core.Assert;
import org.testar.core.alayer.AWTCanvas;

/**
 * SUT screenshots serialiser
 */
public class ScreenshotSerialiser extends Thread {

    public static final String SCRSHOTS = "scrshots";
    private static String testSequenceFolder = null;
    private static String scrshotOutputFolder = null;
    private static LinkedList<ScrshotRecord> scrshotSavingQueue =  new LinkedList<ScrshotRecord>();
    private static final int QUEUE_LIMIT = 16;
    private static ScreenshotSerialiser singletonScreenshotSerialiser;
    private static boolean alive;
    private static boolean queueBoost;

    private static class ScrshotRecord {
        String scrshotPath;
        AWTCanvas scrshot;

        public ScrshotRecord(String scrshotPath, AWTCanvas scrshot) {
            this.scrshotPath = scrshotPath;
            this.scrshot = scrshot;
        }
    }

    private ScreenshotSerialiser() { }

    private ScreenshotSerialiser(String scrshotOutputFolder, String testSequenceFolder) {
        ScreenshotSerialiser.testSequenceFolder = testSequenceFolder;
        ScreenshotSerialiser.scrshotOutputFolder = scrshotOutputFolder;
        (new File(scrshotOutputFolder + File.separator + testSequenceFolder)).mkdirs();
    }

    public static void start(String outputFolder, String testSequenceFolder) {
        Assert.isTrue(!alive);
        Assert.isTrue(scrshotSavingQueue.isEmpty());
        alive = true;
        queueBoost = false;
        singletonScreenshotSerialiser = new ScreenshotSerialiser(outputFolder, testSequenceFolder);
        singletonScreenshotSerialiser.setPriority(Thread.MIN_PRIORITY);
        singletonScreenshotSerialiser.start();
    }

    public static void finish() {
        alive = false;
    }

    public static boolean isSavingQueueEmpty() {
        if (scrshotSavingQueue.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        while (alive || !scrshotSavingQueue.isEmpty()) {
            while (alive && scrshotSavingQueue.isEmpty()) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e1) {
                }
            }
            if (!scrshotSavingQueue.isEmpty()) {
                if (!queueBoost && scrshotSavingQueue.size() > QUEUE_LIMIT) {
                    this.setPriority(NORM_PRIORITY);
                    queueBoost = true;
                } else if (queueBoost && scrshotSavingQueue.size() < QUEUE_LIMIT / 2) {
                    this.setPriority(MIN_PRIORITY);
                    queueBoost = false;
                }
                ScrshotRecord r;
                synchronized (scrshotSavingQueue) {
                    r = scrshotSavingQueue.removeFirst();
                }
                try {
                    // Write to a temp file, then atomically move/replace it to the final name.
                    Path finalPath = Paths.get(r.scrshotPath);
                    Path tmpPath = finalPath.resolveSibling(finalPath.getFileName().toString() + ".part");
                    r.scrshot.saveAsPng(tmpPath.toString());
                    try {
                        Files.move(tmpPath, finalPath,
                                StandardCopyOption.ATOMIC_MOVE,
                                StandardCopyOption.REPLACE_EXISTING);
                    } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                        // fall back to a regular replace if ATOMIC_MOVE not available (e.g., some filesystems)
                        Files.move(tmpPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    LogSerialiser.log("I/O exception saving screenshot <" + r.scrshotPath + ">\n", LogSerialiser.LogLevel.Critical);
                    try {
                        Files.deleteIfExists(Paths.get(r.scrshotPath + ".part"));
                    } catch (IOException ignore) {
                    }
                }
            }
        }
        synchronized (testSequenceFolder) {
            singletonScreenshotSerialiser = null;
            testSequenceFolder.notifyAll();
        }
    }

    public static String saveStateshot(String stateID, AWTCanvas stateshot) {
        String statePath = scrshotOutputFolder + File.separator + testSequenceFolder + File.separator + stateID + ".png";
        if (!new File(statePath).exists()) {
            savethis(statePath, stateshot);
        }
        return statePath;
    }

    public static String saveActionshot(String stateID, String actionID, final AWTCanvas actionshot) {
        String actionPath = scrshotOutputFolder + File.separator + testSequenceFolder + File.separator + stateID + "_" + actionID + ".png";
        if (!new File(actionPath).exists()) {
            savethis(actionPath, actionshot);
        }
        return actionPath;
    }

    private static void savethis(String scrshotPath, AWTCanvas scrshot) {
        if (alive) {
            synchronized (scrshotSavingQueue) {
                scrshotSavingQueue.add(new ScrshotRecord(scrshotPath, scrshot));
            }
        }
    }

    public static void exit() {
        if (singletonScreenshotSerialiser != null) {
            ScreenshotSerialiser.finish();
            try {
                synchronized (testSequenceFolder) {
                    while (singletonScreenshotSerialiser != null) {
                        try {
                            testSequenceFolder.wait(10);
                        } catch (InterruptedException e) {
                            System.out.println("ScreenshotSerialiser exit interrupted");
                        }
                    }
                }
            } catch (Exception e) {
            } // testSequenceFolder may be set to null when we try to sync on it
            testSequenceFolder = null;
        }
    }

    public static int queueLength() {
        return scrshotSavingQueue.size();
    }

}
