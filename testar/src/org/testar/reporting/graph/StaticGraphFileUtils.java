package org.testar.reporting.graph;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

final class StaticGraphFileUtils {

    private StaticGraphFileUtils() {}

    static void copyDirectory(Path src, Path dst) throws IOException {
        try {
            Files.walk(src).forEach(path -> {
                try {
                    Path relative = src.relativize(path);
                    Path target = dst.resolve(relative);
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(target);
                    } else {
                        Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    static void writeElementsJs(Path elementsJson, Path elementsJs) throws IOException {
        byte[] json = Files.readAllBytes(elementsJson);
        StringBuilder sb = new StringBuilder();
        sb.append("window.__TESTAR_ELEMENTS__ = ");
        sb.append(new String(json));
        sb.append(";\n");
        Files.write(elementsJs, sb.toString().getBytes());
    }

    static String stripExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx > 0 ? name.substring(0, idx) : name;
    }

    static String toDataUrl(Path file) {
        try {
            byte[] bytes = Files.readAllBytes(file);
            String base64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            StaticGraphExporter.log("Failed to read image for data URL: " + file);
            return null;
        }
    }
}
