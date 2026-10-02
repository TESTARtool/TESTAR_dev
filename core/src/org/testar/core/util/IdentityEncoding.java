/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.core.util;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

/** Unambiguous identity fields and platform-independent hashing. */
public final class IdentityEncoding {

    private IdentityEncoding() { }

    public static String encode(String... fields) {
        StringBuilder identity = new StringBuilder();
        for (String field : fields) {
            if (field == null) {
                identity.append("-1:");
            } else {
                identity.append(field.length()).append(':').append(field);
            }
        }
        return identity.toString();
    }

    public static String hash(String identity) {
        CRC32 crc32 = new CRC32();
        crc32.update(identity.getBytes(StandardCharsets.UTF_8));
        return Integer.toUnsignedString(identity.hashCode(), Character.MAX_RADIX)
                + Integer.toHexString(identity.length()) + crc32.getValue();
    }
}
