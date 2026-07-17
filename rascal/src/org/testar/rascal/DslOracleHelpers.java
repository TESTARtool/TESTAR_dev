package org.testar.rascal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rascalmpl.interpreter.Evaluator;

import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IString;
import io.usethesource.vallang.IValueFactory;

public class DslOracleHelpers {

    public static ISourceLocation writeDslToTemp(IValueFactory vf, String pseudoClassName, String dsl) throws IOException {
        String withPkg = ensureDslContainsPackage(dsl);
        Path dir = Files.createTempDirectory("testar-dsl-");
        Path file = dir.resolve(pseudoClassName + ".testar");
        Files.writeString(file, withPkg);
        return vf.sourceLocation(file.toUri());
    }

    private static String ensureDslContainsPackage(String dsl) {
        String[] lines = dsl.split("\\R");
        for (String line : lines) {
            String t = line.trim();
            if (t.isEmpty()) {
                continue;
            }
            if (t.startsWith("//") || t.startsWith("/*") || t.startsWith("*")) {
                continue;
            }
            if (t.startsWith("package ") && t.endsWith(";")) {
                // If DSL assert already has a package, just maintain
                return dsl;
            }
            break;
        }
        // If DSL assert does not contain a package, add a default one
        return "package generated;\n\n" + dsl;
    }

    public static String compileAt(Evaluator eval, ISourceLocation loc) {
        IString javaSrc = (IString) eval.call("compileAt", loc);
        return javaSrc.getValue();
    }

    // ---- Transform & save to target path with package generated; ----
    public static String detectPackage(String javaSource) {
        for (String line : javaSource.split("\\R")) {
            String t = line.trim();
            if (t.startsWith("package ") && t.endsWith(";")) {
                return t.substring("package ".length(), t.length() - 1).trim();
            }
            if (t.startsWith("import ") || t.startsWith("public ") || t.startsWith("class ")) break;
        }
        return "";
    }

    public static String detectPublicClass(String javaSource) {
        Matcher m = Pattern.compile("\\bpublic\\s+class\\s+([A-Za-z_][\\w$]*)").matcher(javaSource);
        return m.find() ? m.group(1) : null;
    }

    public static String forcePackageAndClass(String src) {
        final String targetPkg = "generated";
        final String targetClass = "RuntimeGeneratedOracles";
        String pkg = detectPackage(src);
        String currentClass = detectPublicClass(src);

        if (pkg.isEmpty()) {
            src = "package " + targetPkg + ";\n\n" + src;
        } else if (!pkg.equals(targetPkg)) {
            src = src.replaceFirst("\\bpackage\\s+" + Pattern.quote(pkg) + "\\s*;",
                    "package " + targetPkg + ";");
        }
        if (currentClass != null && !currentClass.equals(targetClass)) {
            src = src.replaceFirst("\\bpublic\\s+class\\s+" + Pattern.quote(currentClass),
                    "public class " + targetClass);
        }
        return src;
    }

    public static Path saveGenerated(String javaSource) throws IOException {
        String transformed = forcePackageAndClass(javaSource);
        Path out = Paths.get("oracles", "generated", "RuntimeGeneratedOracles.java")
                .toAbsolutePath().normalize();
        Files.createDirectories(out.getParent());
        Files.writeString(out, transformed);
        return out;
    }

}
