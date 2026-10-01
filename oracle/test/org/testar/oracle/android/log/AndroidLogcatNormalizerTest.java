/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.oracle.android.log;

import org.junit.Assert;
import org.junit.Test;

public class AndroidLogcatNormalizerTest {

    @Test
    public void normalizesDynamicPathsAndObjectIds() {
        String first = "BitmapFactory: java.io.FileNotFoundException: "
                + "/data/user/0/com.example.app/cache/sentry/83a56134754ad7e27d5f94754e5a842865257057/"
                + "replay_053d15c452f042f9a7049bb22a6860ca/1783338523682.jpg at Worker@6d8322a";
        String second = "BitmapFactory: java.io.FileNotFoundException: "
                + "/data/user/0/com.other.app/cache/sentry/9f3d44b21234ad7e27d5f94754e5a842812345678/"
                + "replay_77aa22bb33cc44dd55ee66ff77889900/1888888888888.jpg at Worker@8de45f6";

        String expected = "BitmapFactory: java.io.FileNotFoundException: "
                + "/data/user/<num>/<package>/cache/<path>/<id>/replay_<id>/<file>.jpg at Worker@<id>";
        Assert.assertEquals(expected, AndroidLogcatNormalizer.normalize(first));
        Assert.assertEquals(expected, AndroidLogcatNormalizer.normalize(second));
    }

    @Test
    public void preservesMeaningfulNumbersAndStableFilenames() {
        String message = "Worker: Exception code 1, http status 404 opening "
                + "/data/data/com.example.app/files/reports/settings-2024.db";

        Assert.assertEquals("Worker: Exception code 1, http status 404 opening "
                + "/data/data/<package>/files/reports/settings-2024.db",
                AndroidLogcatNormalizer.normalize(message));
    }

    @Test
    public void stripsThreadtimeBeforeMatching() {
        String line = "02-09 08:59:33.844 17550 17575 E ViewRootImpl: Exception @1:207875";

        Assert.assertEquals("ViewRootImpl: Exception @1:207875",
                AndroidLogcatNormalizer.stripThreadtime(line));
        Assert.assertEquals("ViewRootImpl: Exception @<num>:<num>",
                AndroidLogcatNormalizer.normalize(AndroidLogcatNormalizer.stripThreadtime(line)));
    }

    @Test
    public void preservesDistinctExceptionTypes() {
        Assert.assertEquals("Worker: IllegalArgumentException: invalid state",
                AndroidLogcatNormalizer.normalize("Worker: IllegalArgumentException: invalid state"));
        Assert.assertEquals("Worker: NullPointerException: missing state",
                AndroidLogcatNormalizer.normalize("Worker: NullPointerException: missing state"));
    }
}
