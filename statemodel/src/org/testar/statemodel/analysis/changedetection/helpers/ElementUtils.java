/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.statemodel.analysis.changedetection.helpers;

import java.util.List;
import java.util.Map;

public class ElementUtils {

    private ElementUtils() { }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getData(Map<String, Object> element) {
        if (element == null) {
            return null;
        }
        Object data = element.get("data");
        if (data instanceof Map) {
            return (Map<String, Object>) data;
        }
        return null;
    }

    public static boolean hasClass(Map<String, Object> element, String cls) {
        Object classes = element != null ? element.get("classes") : null;
        if (classes instanceof List) {
            List<?> list = (List<?>) classes;
            return list.contains(cls);
        }
        return false;
    }

    public static String asString(Object value) {
        if (value instanceof String) {
            String v = (String) value;
            return v.isEmpty() ? null : v;
        }
        return null;
    }

    public static String firstNonNull(String... values) {
        if (values == null) {
            return null;
        }
        for (String v : values) {
            if (v != null && !v.isEmpty()) {
                return v;
            }
        }
        return null;
    }

    public static String extractStateId(Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        Object stateId = data.get("stateId");
        if (stateId instanceof String && !((String) stateId).isEmpty()) {
            return (String) stateId;
        }
        Object id = data.get("id");
        if (id instanceof String && !((String) id).isEmpty()) {
            return (String) id;
        }
        return null;
    }

}
