package org.testar.reporting.graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class StaticGraphIndexPageBuilder {

    private static final Pattern URL_PATTERN = Pattern.compile("(https?://[^\\s<>\\\"']+)");

    private StaticGraphIndexPageBuilder() {}

    static void writeIndexPage(Path publicRoot, Path runsRoot) throws IOException {
        Files.createDirectories(publicRoot);
        Files.createDirectories(runsRoot);

        String[] runDirs = runsRoot.toFile().list((dir, name) -> new File(dir, name).isDirectory());
        if (runDirs == null) runDirs = new String[0];
        Arrays.sort(runDirs, Comparator.reverseOrder());

        int completeCount = 0;
        int failCount = 0;
        StaticGraphRunMeta[] metas = new StaticGraphRunMeta[runDirs.length];
        for (int i = 0; i < runDirs.length; i++) {
            metas[i] = StaticGraphRunMetadataProvider.readRunMeta(runsRoot.resolve(runDirs[i]));
            if (metas[i].isComplete()) completeCount++; else failCount++;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n");
        sb.append("<html lang=\"en\">\n");
        sb.append("<head>\n");
        sb.append("  <meta charset=\"UTF-8\" />\n");
        sb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n");
        sb.append("  <title>TESTAR State Models</title>\n");
        sb.append("  <style>\n");
        sb.append("    :root{--bg:#f6f7fb;--card:#fff;--line:#d9d9e3;--txt:#222;--muted:#666;--ok:#0f7b42;--bad:#b00020;--accent:#1c9099;}\n");
        sb.append("    body{font-family:Segoe UI,Tahoma,Verdana,sans-serif;margin:24px;background:var(--bg);color:var(--txt);}\n");
        sb.append("    h1{font-size:22px;margin:0 0 10px 0;}\n");
        sb.append("    .top{display:flex;gap:12px;flex-wrap:wrap;align-items:stretch;margin-bottom:16px;}\n");
        sb.append("    .card{background:var(--card);border:1px solid var(--line);border-radius:10px;padding:12px;}\n");
        sb.append("    .summary{display:flex;gap:12px;align-items:center;flex-wrap:wrap;}\n");
        sb.append("    .pill{display:inline-flex;gap:8px;align-items:center;padding:6px 10px;border-radius:999px;font-weight:800;font-size:12px;}\n");
        sb.append("    .pill.ok{background:#e7f6ee;color:var(--ok);}\n");
        sb.append("    .pill.bad{background:#fde8ec;color:var(--bad);}\n");
        sb.append("    .bar{height:10px;border-radius:999px;background:#ececf5;overflow:hidden;flex:1;min-width:220px;}\n");
        sb.append("    .bar > span{display:block;height:100%;float:left;}\n");
        sb.append("    .bar .ok{background:var(--ok);}\n");
        sb.append("    .bar .bad{background:var(--bad);}\n");
        sb.append("    .runs{list-style:none;padding:0;margin:0;display:flex;flex-direction:column;gap:10px;}\n");
        sb.append("    .run-item{display:flex;flex-direction:column;gap:8px;}\n");
        sb.append("    .run{display:flex;gap:10px;align-items:center;}\n");
        sb.append("    .name{font-weight:800;flex:1;word-break:break-word;}\n");
        sb.append("    .meta{font-size:12px;color:var(--muted);}\n");
        sb.append("    .badge{font-size:11px;font-weight:900;padding:3px 8px;border-radius:999px;}\n");
        sb.append("    .badge.ok{background:#e7f6ee;color:var(--ok);}\n");
        sb.append("    .badge.bad{background:#fde8ec;color:var(--bad);}\n");
        sb.append("    .iconlink{display:inline-flex;align-items:center;justify-content:center;width:34px;height:34px;border-radius:8px;border:1px solid var(--line);background:#fbfbff;}\n");
        sb.append("    .iconlink:hover{border-color:#b8b8c7;}\n");
        sb.append("    .iconbtn{display:inline-flex;align-items:center;justify-content:center;width:34px;height:34px;border-radius:8px;border:1px solid var(--line);background:#fbfbff;cursor:pointer;}\n");
        sb.append("    .iconbtn:hover{border-color:#b8b8c7;}\n");
        sb.append("    .icon{width:18px;height:18px;}\n");
        sb.append("    .details{display:none;padding:10px;border:1px dashed #d4d5e2;border-radius:8px;background:#fafaff;font-size:12px;line-height:1.4;}\n");
        sb.append("    .details.open{display:block;}\n");
        sb.append("    .goal-list{margin:6px 0 0 0;padding-left:18px;}\n");
        sb.append("    .goal-list li{margin:8px 0;}\n");
        sb.append("    .goal-line{margin-left:8px;color:#333;line-height:1.5;}\n");
        sb.append("    .muted{color:var(--muted);font-size:12px;}\n");
        sb.append("  </style>\n");
        sb.append("</head>\n");
        sb.append("<body>\n");
        sb.append("  <h1>TESTAR State Models</h1>\n");
        sb.append("  <div class=\"top\">\n");
        sb.append("    <div class=\"card summary\">\n");
        sb.append("      <span class=\"pill ok\">&#10003; Complete: ").append(completeCount).append("</span>\n");
        sb.append("      <span class=\"pill bad\">&#10007; Other: ").append(failCount).append("</span>\n");
        double total = Math.max(1.0, completeCount + failCount);
        int okPct = (int)Math.round((completeCount / total) * 100.0);
        int badPct = Math.max(0, 100 - okPct);
        sb.append("      <div class=\"bar\" title=\"").append(okPct).append("% complete\">\n");
        sb.append("        <span class=\"ok\" style=\"width:").append(okPct).append("%\"></span>\n");
        sb.append("        <span class=\"bad\" style=\"width:").append(badPct).append("%\"></span>\n");
        sb.append("      </div>\n");
        sb.append("    </div>\n");
        sb.append("  </div>\n");

        if (runDirs.length == 0) {
            sb.append("  <div class=\"muted\">No runs found.</div>\n");
        } else {
            sb.append("  <ul class=\"runs\">\n");
            for (int i = 0; i < runDirs.length; i++) {
                String run = runDirs[i];
                StaticGraphRunMeta meta = metas[i];
                String verdictId = "verdict-" + i;
                String goalsId = "goals-" + i;
                sb.append("    <li class=\"card run-item\">\n");
                sb.append("      <div class=\"run\">\n");
                sb.append("      <span class=\"badge ").append(meta.isComplete() ? "ok" : "bad").append("\">")
                        .append(escapeHtml(meta.getVerdictTitle())).append("</span>\n");
                sb.append("      <div style=\"flex:1; min-width:0;\">\n");
                sb.append("        <div class=\"name\">").append(escapeHtml(run)).append("</div>\n");
                if (meta.getModelIdentifier() != null && !meta.getModelIdentifier().isEmpty()) {
                    sb.append("        <div class=\"meta\">Model: ").append(escapeHtml(meta.getModelIdentifier())).append("</div>\n");
                }
                sb.append("      </div>\n");
                sb.append("      <button class=\"iconbtn\" type=\"button\" title=\"Toggle LlmTestGoals\" onclick=\"toggleDetails('")
                        .append(goalsId).append("')\">").append(svgGoals()).append("</button>\n");
                sb.append("      <button class=\"iconbtn\" type=\"button\" title=\"Toggle oracle verdict\" onclick=\"toggleDetails('")
                        .append(verdictId).append("')\">").append(svgVerdict()).append("</button>\n");
                sb.append("      <a class=\"iconlink\" href=\"runs/").append(run).append("/index.html\" title=\"Open model\" target=\"_blank\" rel=\"noopener noreferrer\">\n");
                sb.append("        ").append(svgEye()).append("\n");
                sb.append("      </a>\n");
                sb.append("      </div>\n");
                sb.append("      <div id=\"").append(verdictId).append("\" class=\"details\"><strong>Oracle verdict</strong><br>")
                        .append(escapeHtml(meta.getOracleVerdict())).append("</div>\n");
                sb.append("      <div id=\"").append(goalsId).append("\" class=\"details\"><strong>LlmTestGoals</strong>");
                List<String> goals = StaticGraphRunMetadataProvider.splitGoals(meta.getLlmGoalsText());
                if (goals.isEmpty()) {
                    sb.append("<div class=\"muted\">No goals found.</div>");
                } else {
                    sb.append("<ul class=\"goal-list\">");
                    for (String goal : goals) {
                        String[] lines = goal.split("\\r?\\n");
                        String firstLine = lines.length > 0 ? lines[0] : "";
                        sb.append("<li><div>").append(escapeAndLinkifyUrls(firstLine)).append("</div>");
                        for (int lineIndex = 1; lineIndex < lines.length; lineIndex++) {
                            String extraLine = lines[lineIndex] == null ? "" : lines[lineIndex].trim();
                            if (!extraLine.isEmpty()) {
                                sb.append("<div class=\"goal-line\">").append(escapeAndLinkifyUrls(extraLine)).append("</div>");
                            }
                        }
                        sb.append("</li>");
                    }
                    sb.append("</ul>");
                }
                sb.append("</div>\n");
                sb.append("    </li>\n");
            }
            sb.append("  </ul>\n");
        }

        sb.append("<script>\n");
        sb.append("function toggleDetails(id){var el=document.getElementById(id); if(!el)return; el.classList.toggle('open');}\n");
        sb.append("</script>\n");
        sb.append("</body>\n");
        sb.append("</html>\n");

        Files.write(publicRoot.resolve("index.html"), sb.toString().getBytes());
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private static String escapeAndLinkifyUrls(String text) {
        if (text == null || text.isEmpty()) return "";
        Matcher matcher = URL_PATTERN.matcher(text);
        int cursor = 0;
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            if (start > cursor) {
                result.append(escapeHtml(text.substring(cursor, start)));
            }
            String url = matcher.group(1);
            String safeUrl = escapeHtml(url);
            result.append("<a href=\"")
                    .append(safeUrl)
                    .append("\" target=\"_blank\" rel=\"noopener noreferrer\">")
                    .append(safeUrl)
                    .append("</a>");
            cursor = end;
        }
        if (cursor < text.length()) {
            result.append(escapeHtml(text.substring(cursor)));
        }
        return result.toString();
    }

    private static String svgEye() {
        return "<svg class=\"icon\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">" +
                "<path d=\"M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12z\" stroke=\"#1c9099\" stroke-width=\"2\"/>" +
                "<circle cx=\"12\" cy=\"12\" r=\"3\" stroke=\"#1c9099\" stroke-width=\"2\"/>" +
                "</svg>";
    }

    private static String svgVerdict() {
        return "<svg class=\"icon\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">" +
                "<path d=\"M4 12l5 5L20 6\" stroke=\"#1c9099\" stroke-width=\"2\"/>" +
                "<circle cx=\"12\" cy=\"12\" r=\"9\" stroke=\"#1c9099\" stroke-width=\"1.6\"/>" +
                "</svg>";
    }

    private static String svgGoals() {
        return "<svg class=\"icon\" viewBox=\"0 0 24 24\" fill=\"none\" xmlns=\"http://www.w3.org/2000/svg\">" +
                "<rect x=\"4\" y=\"5\" width=\"16\" height=\"14\" rx=\"2\" stroke=\"#1c9099\" stroke-width=\"2\"/>" +
                "<path d=\"M8 9h8M8 12h8M8 15h5\" stroke=\"#1c9099\" stroke-width=\"2\"/>" +
                "</svg>";
    }
}
