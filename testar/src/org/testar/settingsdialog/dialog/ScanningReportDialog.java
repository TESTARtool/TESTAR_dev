package org.testar.settingsdialog.dialog;

import org.testar.settingsdialog.codeanalysis.RepositoryLanguage;
import org.testar.settingsdialog.codeanalysis.RepositoryLanguageComposition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.math.RoundingMode;

public class ScanningReportDialog extends JFrame {
    private RepositoryLanguageComposition repositoryLanguageComposition;
    private JTable reportTable;
    private JScrollPane reportTableContainer;
    private JButton closeButton;
    private final static String CLOSE_BUTTON = "Close";
    private final static String WINDOW_TITLE = "Full report";
    private final static Object[] COLUMN_NAMES = new Object[]{"No", "Language", "Composition"};
    private final static int[] WINDOW_SIZE = new int[]{400, 400};

    public ScanningReportDialog(RepositoryLanguageComposition repositoryLanguageComposition) throws HeadlessException {
        this.repositoryLanguageComposition = repositoryLanguageComposition;
        initCloseButton();
        initTable();
        initLayoutProperties();
        initLayoutContent();
        calculateWindowLocation();
    }

    private void initCloseButton() {
        closeButton = new JButton(CLOSE_BUTTON);
        closeButton.addActionListener((event) -> {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void initTable() {
        reportTableContainer = new JScrollPane();
        reportTable = new JTable(getTableData(), COLUMN_NAMES);
        reportTableContainer.setViewportView(reportTable);
    }


    private Object[][] getTableData() {
        Object[][] tableData = new Object[repositoryLanguageComposition.getRepositoryLanguages().size()][];
            for (int i = 0; i < tableData.length; i++) {
                RepositoryLanguage language = repositoryLanguageComposition.getRepositoryLanguages().get(i);
                tableData[i] = new Object[]{i, language.getSupportedLanguage().getName(), language.getPercentage().setScale(0, RoundingMode.HALF_UP).toString() + "%"};
            }
        return tableData;
    }

    private void initLayoutProperties() {
        setTitle(WINDOW_TITLE);
        setSize(WINDOW_SIZE[0], WINDOW_SIZE[1]);
        setVisible(true);
        setResizable(false);
    }

    private void initLayoutContent() {
        getContentPane().add(reportTable.getTableHeader());
        getContentPane().add(reportTableContainer);
        getContentPane().add(Box.createRigidArea(new Dimension(WINDOW_SIZE[0], 6)));
        getContentPane().add(closeButton);
        getContentPane().add(Box.createRigidArea(new Dimension(WINDOW_SIZE[0], 6)));
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    }

    private void calculateWindowLocation() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }
}

