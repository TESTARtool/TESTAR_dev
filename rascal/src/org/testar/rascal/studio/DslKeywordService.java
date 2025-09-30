package org.testar.rascal.studio;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public final class DslKeywordService {

    private final Path oracleFile;
    private volatile Set<String> cached = Collections.emptySet();

    public DslKeywordService(Path modulePath) {
        this.oracleFile = Paths.get(modulePath.toString(), "lang", "testar", "Oracle.rsc");
        this.cached = loadOnce();
    }

    public Set<String> getKeywords() { return cached; }

    private Set<String> loadOnce() {
        try {
            String src = Files.readString(oracleFile, StandardCharsets.UTF_8);
            // Find Rascal string literals: "...." (group 1 is content)
            Matcher m = Pattern.compile("\"((?:\\\\.|[^\"])*)\"").matcher(src);
            Set<String> out = new HashSet<>();
            while (m.find()) {
                String lit = unescape(m.group(1));
                // keep only identifier-shaped literals (skip punctuation like ";" or "=>")
                if (lit.matches("[A-Za-z_][A-Za-z0-9_]*")) out.add(lit);
            }
            return Collections.unmodifiableSet(out);
        } catch (Throwable t) {
            return Collections.emptySet();
        }
    }

    // Minimal unescape for common escapes used in Rascal source
    private static String unescape(String s) {
        StringBuilder b = new StringBuilder(s.length());
        boolean esc = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!esc) {
                if (c == '\\') esc = true;
                else b.append(c);
            } else {
                switch (c) {
                case 'b': b.append('\b'); break;
                case 'n': b.append('\n'); break;
                case 'r': b.append('\r'); break;
                case 'f': b.append('\f'); break;
                case 't': b.append('\t'); break;
                case '\\': b.append('\\'); break;
                case '"': b.append('\"'); break;
                default: b.append(c); break;
                }
                esc = false;
            }
        }
        return b.toString();
    }
}
