package org.testar.rascal.studio;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.Set;

public final class DslKeywordHighlighter {

    private final JTextComponent editor;
    private final java.util.function.Supplier<Set<String>> keywordsSupplier;
    private final Timer debounce;
    private final boolean colorStrings;

    private static final Color KW_COLOR  = new Color(138, 43, 226); // purple
    private static final Color STR_COLOR = new Color(255, 165, 0);  // orange

    // Fallback underline painters if we don't have a StyledDocument
    private final Highlighter.HighlightPainter KW_PAINTER  =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(138, 43, 226, 60));
    private final Highlighter.HighlightPainter STR_PAINTER =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 165, 0, 60));
    private final java.util.List<Object> underlineTags = new java.util.ArrayList<>();

    public DslKeywordHighlighter(JTextComponent editor,
            java.util.function.Supplier<Set<String>> keywordsSupplier,
            int debounceMs,
            boolean colorStrings) {
        this.editor = editor;
        this.keywordsSupplier = keywordsSupplier;
        this.colorStrings = colorStrings;
        this.debounce = new Timer(debounceMs, e -> apply());
        this.debounce.setRepeats(false);
    }

    public void start() {
        editor.getDocument().addDocumentListener(new DocumentListener() {
            private void arm() { debounce.restart(); }
            @Override public void insertUpdate(DocumentEvent e) { arm(); }
            @Override public void removeUpdate(DocumentEvent e) { arm(); }
            @Override public void changedUpdate(DocumentEvent e) { /* ignore style-only changes */ }
        });
        SwingUtilities.invokeLater(this::apply);
    }

    /** Re-tokenize and color. Safe from any thread. */
    public void apply() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::apply);
            return;
        }

        // If we have a StyledDocument, do true foreground coloring.
        Document doc = editor.getDocument();
        if (doc instanceof StyledDocument) {
            applyWithStyledDocument((StyledDocument) doc);
        } else {
            // Fallback (e.g., JTextArea): underline/marked backgrounds
            applyWithUnderlines();
        }
    }

    // ---------- Foreground text coloring on StyledDocument ----------
    private void applyWithStyledDocument(StyledDocument doc) {
        try {
            int len = doc.getLength();
            String text = doc.getText(0, len);
            Set<String> kws = keywordsSupplier.get();

            // 1) Reset foreground color for the whole document.
            // Use editor's default foreground so we don't hardcode a theme.
            SimpleAttributeSet reset = new SimpleAttributeSet();
            StyleConstants.setForeground(reset, editor.getForeground());
            doc.setCharacterAttributes(0, len, reset, false);

            // 2) Build attribute sets for keywords and strings.
            SimpleAttributeSet kwAttr  = new SimpleAttributeSet();
            StyleConstants.setForeground(kwAttr, KW_COLOR);
            SimpleAttributeSet strAttr = new SimpleAttributeSet();
            StyleConstants.setForeground(strAttr, STR_COLOR);

            // 3) Tokenize and apply attributes.
            final int n = text.length();
            int i = 0;
            while (i < n) {
                char c = text.charAt(i);

                // Strings
                if (colorStrings && c == '"') {
                    int j = i + 1;
                    boolean esc = false;
                    while (j < n) {
                        char cj = text.charAt(j);
                        if (esc) { esc = false; j++; continue; }
                        if (cj == '\\') { esc = true; j++; continue; }
                        if (cj == '"') { j++; break; }
                        j++;
                    }
                    int end = Math.min(j, n);
                    doc.setCharacterAttributes(i, end - i, strAttr, false);
                    i = end;
                    continue;
                }

                // Skip comments
                if (c == '/' && i + 1 < n) {
                    char c2 = text.charAt(i + 1);
                    if (c2 == '/') {
                        int j = i + 2;
                        while (j < n && text.charAt(j) != '\n' && text.charAt(j) != '\r') j++;
                        i = j; continue;
                    } else if (c2 == '*') {
                        int j = i + 2;
                        while (j + 1 < n && !(text.charAt(j) == '*' && text.charAt(j + 1) == '/')) j++;
                        i = Math.min(n, j + 2); continue;
                    }
                }

                // Ident / keyword
                if (c == '_' || Character.isLetter(c)) {
                    int j = i + 1;
                    while (j < n) {
                        char cj = text.charAt(j);
                        if (cj == '_' || Character.isLetterOrDigit(cj)) j++; else break;
                    }
                    String word = text.substring(i, j);
                    if (kws.contains(word)) {
                        doc.setCharacterAttributes(i, j - i, kwAttr, false);
                    }
                    i = j; continue;
                }

                i++;
            }
        } catch (BadLocationException ignore) {
            // safe to ignore; next edit will re-apply
        }
    }

    // ---------- Fallback underline implementation (non-styled docs) ----------
    private void applyWithUnderlines() {
        Highlighter h = editor.getHighlighter();
        for (Object tag : underlineTags) {
            try { h.removeHighlight(tag); } catch (Exception ignore) {}
        }
        underlineTags.clear();

        String s = editor.getText();
        Set<String> kws = keywordsSupplier.get();
        final int n = s.length();
        int i = 0;
        while (i < n) {
            char c = s.charAt(i);

            if (colorStrings && c == '"') {
                int j = i + 1;
                boolean esc = false;
                while (j < n) {
                    char cj = s.charAt(j);
                    if (esc) { esc = false; j++; continue; }
                    if (cj == '\\') { esc = true; j++; continue; }
                    if (cj == '"') { j++; break; }
                    j++;
                }
                add(h, i, Math.min(j, n), STR_PAINTER);
                i = Math.min(j, n);
                continue;
            }

            if (c == '/' && i + 1 < n) {
                char c2 = s.charAt(i + 1);
                if (c2 == '/') {
                    int j = i + 2;
                    while (j < n && s.charAt(j) != '\n' && s.charAt(j) != '\r') j++;
                    i = j; continue;
                } else if (c2 == '*') {
                    int j = i + 2;
                    while (j + 1 < n && !(s.charAt(j) == '*' && s.charAt(j + 1) == '/')) j++;
                    i = Math.min(n, j + 2); continue;
                }
            }

            if (c == '_' || Character.isLetter(c)) {
                int j = i + 1;
                while (j < n) {
                    char cj = s.charAt(j);
                    if (cj == '_' || Character.isLetterOrDigit(cj)) j++; else break;
                }
                String word = s.substring(i, j);
                if (kws.contains(word)) add(editor.getHighlighter(), i, j, KW_PAINTER);
                i = j; continue;
            }

            i++;
        }
    }

    private void add(Highlighter h, int start, int end, Highlighter.HighlightPainter p) {
        try { underlineTags.add(h.addHighlight(start, end, p)); }
        catch (BadLocationException ignore) {}
    }
}
