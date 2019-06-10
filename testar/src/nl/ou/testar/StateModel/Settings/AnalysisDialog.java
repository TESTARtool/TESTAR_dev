package nl.ou.testar.StateModel.Settings;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import nl.ou.testar.StateModel.Analysis.AnalysisManager;
import nl.ou.testar.StateModel.Analysis.Representation.AbstractStateModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class AnalysisDialog extends JDialog {

    private AnalysisManager analysisManager;

    public AnalysisDialog(AnalysisManager analysisManager) {
        this.analysisManager = analysisManager;
        setSize(800, 600);
        setLayout(null);
        setVisible(true);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // tell the manager to shut down its connection
                analysisManager.shutdown();
                super.windowClosing(e);
            }
        });

        init();
    }

    public void init() {
        test();
    }

    private void test() {
        List<AbstractStateModel> abstractStateModels = analysisManager.fetchModels();

        StringBuilder rowSpecBuilder = new StringBuilder("pref");
        for (int i=0; i < abstractStateModels.size(); i++) {
            rowSpecBuilder.append(", 15dlu, pref");
        }
        FormLayout layout = new FormLayout(
                "pref, 6dlu, 70dlu, 6dlu, 70dlu, 6dlu, pref, 6dlu, 50dlu",  // columns
                rowSpecBuilder.toString());           // rows
        ArrayList<JLabel> headerLabels = new ArrayList<>();
        JPanel panel = new JPanel(layout);
        JLabel label1 = new JLabel("Identifier");
        panel.add(label1, CC.xy(1, 1));
        JLabel label2 = new JLabel("ApplicationName");
        panel.add(label2, CC.xy(3, 1));
        JLabel label3 = new JLabel("Version");
        panel.add(label3, CC.xy(5, 1));
        JLabel label4 = new JLabel("Attributes");
        panel.add(label4, CC.xy(7, 1));
        JLabel label5 = new JLabel("Action");
        panel.add(label5, CC.xy(9, 1));
        headerLabels.add(label1);
        headerLabels.add(label2);
        headerLabels.add(label3);
        headerLabels.add(label4);
        headerLabels.add(label5);

        // change the head label font and size
        headerLabels.forEach(label -> label.setFont(new Font(label.getFont().getFontName(), Font.BOLD, (int)(label.getFont().getSize() * 1.2) )));
        int x = 2;
        for (AbstractStateModel abstractStateModel : abstractStateModels) {
            panel.add(new JLabel(abstractStateModel.getModelIdentifier()), CC.xy(1, x));
            panel.add(new JLabel(abstractStateModel.getApplicationName()), CC.xy(3, x));
            panel.add(new JLabel(abstractStateModel.getApplicationVersion()), CC.xy(5, x));
            panel.add(new JLabel(abstractStateModel.getAbstractionAttributes().toString()), CC.xy(7, x));
            panel.add(getModelButton(abstractStateModel.getModelIdentifier()), CC.xy(9, x));
            x += 2;
        }

        panel.setVisible(true);
        panel.setBounds(10, 10, 750, 550);
        add(panel);
    }

    private JButton getModelButton(String identifier) {
        JButton modelButton = new JButton("Load");
        modelButton.setBounds(0, 0, 80, 27);
        modelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(identifier);
//                analysisManager.fetchGraphForModel(identifier);
            }
        });
        return modelButton;
    }



}
