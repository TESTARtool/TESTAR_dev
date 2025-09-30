package org.testar.rascal;

import io.usethesource.vallang.*;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.staticErrors.StaticError;
import org.rascalmpl.interpreter.staticErrors.SyntaxError;
import org.testar.rascal.studio.DslKeywordHighlighter;
import org.testar.rascal.studio.DslKeywordService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

import java.awt.*;
import java.io.IOException;
import java.nio.file.*;

/**
 * Minimal Swing-based DSL mini-IDE for TESTAR oracles (Rascal-backed).
 * - Ato-validate the DSL every 500ms.
 * - See diagnosis errors below (line:col + message).
 * - Generate -> writes to oracles/generated/RuntimeGeneratedOracles.java (package generated).
 */
public class DslOracleStudio extends JFrame {

    private static final long serialVersionUID = -6937249664465472363L;

    private final DslOracleLoader dslOracleLoader;
    private final IValueFactory vf;
    private final Evaluator eval;

    // Syntax-coloring
    private DslKeywordService keywordService;
    private DslKeywordHighlighter keywordHighlighter;

    public DslOracleStudio() throws Exception {
        super("TESTAR DSL Oracle Studio");

        System.out.println("Loading the TESTAR DSL Oracle Studio...");
        
        dslOracleLoader = new DslOracleLoader();
        vf = dslOracleLoader.getValueFactory();
        eval = dslOracleLoader.getEvaluator();

        buildUI();
    }

    private JTextPane editor;
    private JTextArea errorsArea;
    private JLabel status;
    private JFileChooser chooser;

    private void buildUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(900, 700));

        // Editor
        editor = new JTextPane();
        editor.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 14));
        setTabSize(editor, 2);
        JScrollPane spEditor = new JScrollPane(editor);
        spEditor.setBorder(BorderFactory.createTitledBorder("Oracle DSL"));

        // Errors (diagnostics)
        errorsArea = new JTextArea(6, 20);
        errorsArea.setEditable(false);
        errorsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        errorsArea.setLineWrap(true);
        errorsArea.setWrapStyleWord(true);
        JScrollPane spErrors = new JScrollPane(errorsArea);
        spErrors.setBorder(BorderFactory.createTitledBorder("Diagnostics"));
        spErrors.setPreferredSize(new Dimension(200, 160));

        // Status bar
        status = new JLabel("Ready");
        status.setBorder(new EmptyBorder(4, 8, 4, 8));

        // Top bar
        JButton btnOpen = new JButton("Open");
        JButton btnSaveDsl = new JButton("Save DSL");
        JButton btnValidate = new JButton("Validate");
        JButton btnGenerate = new JButton("Generate Java");

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(btnOpen);
        top.add(btnSaveDsl);
        top.add(btnValidate);
        top.add(btnGenerate);

        // Split pane: editor (top) + diagnostics (bottom)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, spEditor, spErrors);
        split.setOneTouchExpandable(true);
        split.setResizeWeight(0.75); // editor gets ~75% of space

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH); // the status bar at the very bottom

        // File chooser
        chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("TESTAR DSL (*.testar)", "testar"));

        // --- VALIDATION DEBOUNCE (single timer; ignore style-only changes) ---
        javax.swing.Timer validationTimer = new javax.swing.Timer(500, e -> validateInBackground(false));
        validationTimer.setRepeats(false);
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void schedule() {
                status.setText("Typing...");
                validationTimer.restart();
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { schedule(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { schedule(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Ignore attribute changes from coloring
            }
        });

        // Actions
        btnOpen.addActionListener(e -> openDsl());
        btnSaveDsl.addActionListener(e -> saveDsl());
        btnValidate.addActionListener(e -> validateInBackground(true));
        btnGenerate.addActionListener(e -> generateInBackground());

        // Seed content (optional)
        editor.setText("assert button \"Submit\" is enabled \"button Submit must be enabled\".");

        // === Syntax coloring (Oracle keywords, strings) ===
        keywordService = new DslKeywordService(dslOracleLoader.getModulePath());
        keywordHighlighter = new DslKeywordHighlighter(
                editor,
                () -> keywordService.getKeywords(),
                500,   // debounce for coloring
                true  // color strings too
                );
        keywordHighlighter.start();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Set divider after realized so 75% sticks nicely
        SwingUtilities.invokeLater(() -> split.setDividerLocation(0.75));

        // First validation
        validateInBackground(false);
    }

    private static void setTabSize(JTextPane editor, int spacesPerTab) {
        if (spacesPerTab < 1) spacesPerTab = 1;

        // Compute tab width in pixels based on current font
        java.awt.FontMetrics fm = editor.getFontMetrics(editor.getFont());
        int charW = fm.charWidth(' ');
        int tabW  = Math.max(charW, charW * spacesPerTab);

        // Build a TabSet (enough stops for wide lines)
        int stops = 50; // adjust if you expect extremely long lines
        javax.swing.text.TabStop[] tabStops = new javax.swing.text.TabStop[stops];
        for (int i = 0; i < stops; i++) {
            tabStops[i] = new javax.swing.text.TabStop((i + 1) * tabW);
        }
        javax.swing.text.TabSet tabSet = new javax.swing.text.TabSet(tabStops);

        // Apply as paragraph attribute to the whole doc
        javax.swing.text.StyledDocument doc = editor.getStyledDocument();
        javax.swing.text.SimpleAttributeSet attrs = new javax.swing.text.SimpleAttributeSet();
        javax.swing.text.StyleConstants.setTabSet(attrs, tabSet);
        doc.setParagraphAttributes(0, doc.getLength(), attrs, false);
    }

    // ---- File ops ----
    private void openDsl() {
        int r = chooser.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            Path p = chooser.getSelectedFile().toPath();
            try {
                editor.setText(Files.readString(p));
                status.setText("Opened: " + p.getFileName());
                validateInBackground(false);
            } catch (IOException ex) {
                status.setText("Open failed: " + ex.getMessage());
            }
        }
    }
    private void saveDsl() {
        int r = chooser.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            Path p = chooser.getSelectedFile().toPath();
            if (!p.toString().endsWith(".testar")) p = p.resolveSibling(p.getFileName() + ".testar");
            try {
                Files.createDirectories(p.getParent());
                Files.writeString(p, editor.getText());
                status.setText("Saved: " + p.getFileName());
            } catch (IOException ex) {
                status.setText("Save failed: " + ex.getMessage());
            }
        }
    }

    private void validateInBackground(boolean explicit) {
        final String dsl = editor.getText(); // snapshot
        status.setText("Validating...");
        errorsArea.setText("");

        new javax.swing.SwingWorker<java.util.List<HlRange>, Void>() {
            String diagnostics = "OK";

            @Override protected java.util.List<HlRange> doInBackground() {
                java.util.List<HlRange> ranges = new java.util.ArrayList<>();
                try {

                    // Rascal: validateAtWithModel(oracleFile, modelFile) -> list[diag]
                    ISourceLocation oracleLoc = DslOracleHelpers.writeDslToTemp(vf, "RuntimeGeneratedOracles", dsl);
                    Path modelPath = Paths.get(dslOracleLoader.getModulePath() + "/lang/testar/testar.model");
                    ISourceLocation modelLoc = vf.sourceLocation(modelPath.toUri());
                    IList diags = (IList) eval.call("validateAtWithModel", oracleLoc, modelLoc);

                    if (diags.length() == 0) {
                        diagnostics = "OK";
                    } else {
                        StringBuilder sb = new StringBuilder();
                        int[] lineStarts = buildLineStarts(dsl);

                        for (IValue v : diags) {
                            io.usethesource.vallang.IConstructor d = (io.usethesource.vallang.IConstructor) v; // diag(Severity, loc, str)
                            String sevRaw = d.get(0).toString();       // SevError() | SevWarning() | SevInfo()
                            String sev    = formatSeverity(sevRaw);    // ERROR | WARNING | INFO
                            io.usethesource.vallang.ISourceLocation where = (io.usethesource.vallang.ISourceLocation) d.get(1);
                            String msg    = ((io.usethesource.vallang.IString) d.get(2)).getValue();

                            int bl = where.hasLineColumn() ? where.getBeginLine()   : 1;
                            int bc = where.hasLineColumn() ? where.getBeginColumn() : 1;
                            int el = where.hasLineColumn() ? where.getEndLine()     : bl;
                            int ec = where.hasLineColumn() ? where.getEndColumn()   : bc + 1;

                            int start = safeOffset(dsl, lineStarts, bl, bc);
                            int end   = safeOffset(dsl, lineStarts, el, ec);
                            if (end <= start) end = Math.min(dsl.length(), start + 1);

                            ranges.add(new HlRange(start, end, painterFor(sevRaw)));

                            sb.append(sev)
                            .append(" at ").append(bl).append(":").append(bc)
                            .append(" — ").append(msg).append("\n");
                        }
                        diagnostics = sb.toString();
                    }

                } catch (org.rascalmpl.interpreter.staticErrors.SyntaxError se) {
                    // Build a highlight from the exception location
                    io.usethesource.vallang.ISourceLocation loc = se.getLocation();
                    if (loc != null && loc.hasLineColumn()) {
                        int[] ls = buildLineStarts(dsl);
                        int bl = loc.getBeginLine(), bc = loc.getBeginColumn();
                        int el = loc.getEndLine(),   ec = loc.getEndColumn();
                        int start = safeOffset(dsl, ls, bl, bc);
                        int end   = safeOffset(dsl, ls, el, ec);
                        if (end <= start) end = Math.min(dsl.length(), start + 1);
                        ranges.add(new HlRange(start, end, ERR_PAINTER));
                    }
                    diagnostics = fmtSyntaxError(se);

                } catch (org.rascalmpl.interpreter.staticErrors.StaticError ste) {
                    io.usethesource.vallang.ISourceLocation loc = ste.getLocation();
                    if (loc != null && loc.hasLineColumn()) {
                        int[] ls = buildLineStarts(dsl);
                        int bl = loc.getBeginLine(), bc = loc.getBeginColumn();
                        int el = loc.getEndLine(),   ec = loc.getEndColumn();
                        int start = safeOffset(dsl, ls, bl, bc);
                        int end   = safeOffset(dsl, ls, el, ec);
                        if (end <= start) end = Math.min(dsl.length(), start + 1);
                        ranges.add(new HlRange(start, end, ERR_PAINTER));
                    }
                    diagnostics = fmtStaticError(ste);

                } catch (org.rascalmpl.exceptions.Throw th) {
                    // Handle Rascal-level Throw, e.g., Throw(ParseError(|file|(...)))
                    io.usethesource.vallang.IValue ex = th.getException();
                    io.usethesource.vallang.ISourceLocation where = null;
                    String msg = th.getMessage();

                    try {
                        if (ex instanceof io.usethesource.vallang.IConstructor) {
                            io.usethesource.vallang.IConstructor c = (io.usethesource.vallang.IConstructor) ex;

                            // try positional arg 0
                            if (c.arity() > 0 && c.get(0) instanceof io.usethesource.vallang.ISourceLocation) {
                                where = (io.usethesource.vallang.ISourceLocation) c.get(0);
                            }
                            // try keyword parameter "loc"
                            if (where == null && c.mayHaveKeywordParameters()) {
                                @SuppressWarnings("unchecked")
                                io.usethesource.vallang.IWithKeywordParameters<io.usethesource.vallang.IConstructor> kw =
                                (io.usethesource.vallang.IWithKeywordParameters<io.usethesource.vallang.IConstructor>) c;
                                io.usethesource.vallang.IValue kv = kw.getParameter("loc");
                                if (kv instanceof io.usethesource.vallang.ISourceLocation) {
                                    where = (io.usethesource.vallang.ISourceLocation) kv;
                                }
                            }

                            if (msg == null || msg.isEmpty()) {
                                msg = c.getName(); // e.g., "ParseError"
                            }
                        }
                    } catch (Throwable ignore) {
                        // If introspection fails, fall through to generic messaging
                    }

                    if (where != null && where.hasLineColumn()) {
                        int[] ls = buildLineStarts(dsl);
                        int bl = where.getBeginLine(), bc = where.getBeginColumn();
                        int el = where.getEndLine(),   ec = where.getEndColumn();
                        int start = safeOffset(dsl, ls, bl, bc);
                        int end   = safeOffset(dsl, ls, el, ec);
                        if (end <= start) end = Math.min(dsl.length(), start + 1);
                        ranges.add(new HlRange(start, end, ERR_PAINTER));

                        diagnostics = "ERROR at " + bl + ":" + bc + " - " + (msg != null ? msg : "Parse error");
                    } else {
                        // no loc available; at least show the message
                        diagnostics = "Error: Throw - " + (msg != null ? msg : ex.toString());
                    }

                } catch (Throwable t) {
                    diagnostics = "Error: " + t.getClass().getSimpleName() + " - " + t.getMessage();
                }
                return ranges;
            }

            @Override protected void done() {
                java.util.List<HlRange> ranges = java.util.Collections.emptyList();
                try { ranges = get(); } catch (Exception ignore) {}

                if ("OK".equals(diagnostics)) {
                    status.setText("Valid");
                    errorsArea.setForeground(new java.awt.Color(0,128,0));
                } else {
                    status.setText("Errors found");
                    errorsArea.setForeground(java.awt.Color.RED.darker());
                }
                errorsArea.setText(diagnostics);

                try {
                    javax.swing.text.Highlighter h = editor.getHighlighter();
                    h.removeAllHighlights();
                    for (HlRange r : ranges) {
                        h.addHighlight(r.start, r.end, r.painter);
                    }
                    if (!ranges.isEmpty()) {
                        int caret = ranges.get(0).start;
                        editor.setCaretPosition(Math.min(caret, editor.getDocument().getLength()));
                        java.awt.Rectangle view = editor.modelToView(caret);
                        if (view != null) editor.scrollRectToVisible(view);
                    }
                    // ensure syntax colors are visible (they were cleared by removeAllHighlights)
                    keywordHighlighter.apply();
                } catch (Exception ignore) {}
            }
        }.execute();
    }

    /** Rascal Bridge severities are SevError()/SevWarning()/SevInfo(); render as ERROR/WARNING/INFO */
    private static String formatSeverity(String sevRaw) {
        // e.g., "SevError()" -> "ERROR"
        String s = sevRaw.replace("()", "");
        if (s.startsWith("Sev")) s = s.substring(3);
        return s.toUpperCase();
    }

    private void generateInBackground() {
        String dsl = editor.getText();
        status.setText("Generating...");
        errorsArea.setText("");
        new SwingWorker<Path, Void>() {
            String diagnostics = "OK";
            @Override protected Path doInBackground() {
                try {
                    ISourceLocation loc = DslOracleHelpers.writeDslToTemp(vf, "RuntimeGeneratedOracles", dsl);
                    String javaSrc = DslOracleHelpers.compileAt(eval, loc);
                    Path out = DslOracleHelpers.saveGenerated(javaSrc);
                    return out;
                } catch (SyntaxError se) {
                    diagnostics = fmtSyntaxError(se);
                } catch (StaticError ste) {
                    diagnostics = fmtStaticError(ste);
                } catch (Throwable t) {
                    diagnostics = "Error: " + t.getClass().getSimpleName() + " - " + t.getMessage();
                }
                return null;
            }
            @Override protected void done() {
                try {
                    Path out = get();
                    if (out != null) {
                        status.setText("Generated: " + out.getFileName());
                        errorsArea.setForeground(new Color(0,128,0));
                        errorsArea.setText("OK, Wrote: " + out.toString());
                    } else {
                        status.setText("Generation failed");
                        errorsArea.setForeground(Color.RED.darker());
                        errorsArea.setText(diagnostics);
                    }
                } catch (Exception e) {
                    status.setText("Generation failed");
                    errorsArea.setForeground(Color.RED.darker());
                    errorsArea.setText("Error: " + e.getMessage());
                }
            }
        }.execute();
    }

    // ---- error formatting (line/col) ----
    private static String fmtSyntaxError(SyntaxError se) {
        var loc = se.getLocation();
        int line = (loc != null) ? loc.getBeginLine() : -1;
        int col  = (loc != null) ? loc.getBeginColumn() : -1;
        return "SyntaxError at " + line + ":" + col + " - " + se.getMessage();
    }
    private static String fmtStaticError(StaticError ste) {
        var loc = ste.getLocation();
        int line = (loc != null) ? loc.getBeginLine() : -1;
        int col  = (loc != null) ? loc.getBeginColumn() : -1;
        return ste.getClass().getSimpleName() + " at " + line + ":" + col + " - " + ste.getMessage();
    }

    // painters for highlights
    private final Highlighter.HighlightPainter ERR_PAINTER  =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 0, 0, 96));
    private final Highlighter.HighlightPainter WARN_PAINTER =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 165, 0, 96));
    private final Highlighter.HighlightPainter INFO_PAINTER =
            new DefaultHighlighter.DefaultHighlightPainter(new Color(30, 144, 255, 64));

    private static class HlRange {
        final int start, end; final javax.swing.text.Highlighter.HighlightPainter painter;
        HlRange(int s, int e, javax.swing.text.Highlighter.HighlightPainter p){ this.start=s; this.end=e; this.painter=p; }
    }

    private static int[] buildLineStarts(String text){
        // line i (1-based) begins at lineStarts[i-1]
        java.util.ArrayList<Integer> starts = new java.util.ArrayList<>();
        starts.add(0);
        for (int i = 0; i < text.length(); i++) if (text.charAt(i) == '\n') starts.add(i + 1);
        // sentinel end
        starts.add(text.length());
        return starts.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int safeOffset(String text, int[] lineStarts, int line, int col){
        // Rascal lines/cols are 1-based; clamp to text bounds
        line = Math.max(1, Math.min(line, lineStarts.length - 1));
        int lineStart = lineStarts[line - 1];
        int lineEnd   = lineStarts[line] - 1; // position of '\n' or last char
        int off = lineStart + Math.max(0, col - 1);
        return Math.max(lineStart, Math.min(off, Math.max(lineStart, lineEnd + 1)));
    }

    private Highlighter.HighlightPainter painterFor(String sevRaw) {
        String s = sevRaw.replace("()", "");
        if (s.startsWith("Sev")) s = s.substring(3);
        switch (s.toUpperCase()) {
        case "WARNING": return WARN_PAINTER;
        case "INFO":    return INFO_PAINTER;
        default:        return ERR_PAINTER;
        }
    }

    // Main method for IDE development execution
    public static void main(String[] args) throws Exception {
        // Keep UI on system LAF if available
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignore) {}
        SwingUtilities.invokeLater(() -> {
            try { new DslOracleStudio(); } catch (Exception e) { e.printStackTrace(); }
        });
    }
}
