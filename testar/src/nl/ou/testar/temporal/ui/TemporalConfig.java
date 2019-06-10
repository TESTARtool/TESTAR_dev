/*
 * Created by JFormDesigner on Sun Jun 09 11:26:49 CEST 2019
 */

package nl.ou.testar.temporal.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * @author unknown
 */
public class TemporalConfig extends JDialog {
    public TemporalConfig(Frame owner) {
        super(owner);
        initComponents();
    }

    public TemporalConfig(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - c sengers
        button2 = new JButton();
        tabbedPane1 = new JTabbedPane();
        graphpanel = new JPanel();
        button1 = new JButton();
        label1 = new JLabel();
        minerPanel = new JPanel();
        label2 = new JLabel();
        textField1 = new JTextField();
        button3 = new JButton();
        button4 = new JButton();
        textField2 = new JTextField();
        label3 = new JLabel();
        label4 = new JLabel();
        checkBox1 = new JCheckBox();
        checkBox2 = new JCheckBox();
        checkBox3 = new JCheckBox();
        checkBox4 = new JCheckBox();
        checkBox5 = new JCheckBox();
        checkerPanel = new JPanel();
        visualizerPanel = new JPanel();

        //======== this ========
        Container contentPane = getContentPane();

        //---- button2 ----
        button2.setText("text");

        //======== tabbedPane1 ========
        {
            tabbedPane1.setBorder(LineBorder.createBlackLineBorder());

            //======== graphpanel ========
            {

                // JFormDesigner evaluation mark
                graphpanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), graphpanel.getBorder())); graphpanel.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                graphpanel.setLayout(null);

                //---- button1 ----
                button1.setText("Test GraphDB Connection ");
                graphpanel.add(button1);
                button1.setBounds(5, 35, 145, 25);

                //---- label1 ----
                label1.setText("dbConnectionLog");
                graphpanel.add(label1);
                label1.setBounds(185, 20, 336, 125);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < graphpanel.getComponentCount(); i++) {
                        Rectangle bounds = graphpanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = graphpanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    graphpanel.setMinimumSize(preferredSize);
                    graphpanel.setPreferredSize(preferredSize);
                }
            }
            tabbedPane1.addTab("GraphDb", graphpanel);

            //======== minerPanel ========
            {
                minerPanel.setLayout(null);

                //---- label2 ----
                label2.setText("PatternCollection:");
                minerPanel.add(label2);
                label2.setBounds(20, 70, 100, 30);

                //---- textField1 ----
                textField1.setText("temporalPatternFileName");
                minerPanel.add(textField1);
                textField1.setBounds(140, 70, 285, textField1.getPreferredSize().height);

                //---- button3 ----
                button3.setText("Select File");
                minerPanel.add(button3);
                button3.setBounds(new Rectangle(new Point(465, 70), button3.getPreferredSize()));

                //---- button4 ----
                button4.setText("Select File");
                minerPanel.add(button4);
                button4.setBounds(470, 180, 86, 30);

                //---- textField2 ----
                textField2.setText("temporalFormulaFileName");
                minerPanel.add(textField2);
                textField2.setBounds(140, 180, 285, 30);

                //---- label3 ----
                label3.setText("FormulaFile:");
                minerPanel.add(label3);
                label3.setBounds(20, 180, 75, 30);

                //---- label4 ----
                label4.setText("FormalismFilter:");
                minerPanel.add(label4);
                label4.setBounds(20, 125, 95, 30);

                //---- checkBox1 ----
                checkBox1.setText("LTL");
                minerPanel.add(checkBox1);
                checkBox1.setBounds(new Rectangle(new Point(145, 135), checkBox1.getPreferredSize()));

                //---- checkBox2 ----
                checkBox2.setText("LTL_TRACE_P");
                minerPanel.add(checkBox2);
                checkBox2.setBounds(195, 135, 90, 18);

                //---- checkBox3 ----
                checkBox3.setText("CTL");
                minerPanel.add(checkBox3);
                checkBox3.setBounds(430, 135, 55, 18);

                //---- checkBox4 ----
                checkBox4.setText("MUCALC");
                minerPanel.add(checkBox4);
                checkBox4.setBounds(510, 135, 70, 18);

                //---- checkBox5 ----
                checkBox5.setText("LTL_TRACE_Q");
                minerPanel.add(checkBox5);
                checkBox5.setBounds(305, 135, 100, 18);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < minerPanel.getComponentCount(); i++) {
                        Rectangle bounds = minerPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = minerPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    minerPanel.setMinimumSize(preferredSize);
                    minerPanel.setPreferredSize(preferredSize);
                }
            }
            tabbedPane1.addTab("Miner", minerPanel);

            //======== checkerPanel ========
            {
                checkerPanel.setLayout(null);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < checkerPanel.getComponentCount(); i++) {
                        Rectangle bounds = checkerPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = checkerPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    checkerPanel.setMinimumSize(preferredSize);
                    checkerPanel.setPreferredSize(preferredSize);
                }
            }
            tabbedPane1.addTab("Checker", checkerPanel);

            //======== visualizerPanel ========
            {
                visualizerPanel.setLayout(null);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < visualizerPanel.getComponentCount(); i++) {
                        Rectangle bounds = visualizerPanel.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = visualizerPanel.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    visualizerPanel.setMinimumSize(preferredSize);
                    visualizerPanel.setPreferredSize(preferredSize);
                }
            }
            tabbedPane1.addTab("Visualizer", visualizerPanel);
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(button2)
                    .addGap(70, 70, 70))
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(38, 38, 38)
                    .addComponent(tabbedPane1, GroupLayout.PREFERRED_SIZE, 698, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
            contentPaneLayout.createParallelGroup()
                .addGroup(contentPaneLayout.createSequentialGroup()
                    .addGap(150, 150, 150)
                    .addComponent(tabbedPane1, GroupLayout.DEFAULT_SIZE, 598, Short.MAX_VALUE)
                    .addGap(18, 18, 18)
                    .addComponent(button2)
                    .addGap(22, 22, 22))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - c sengers
    private JButton button2;
    private JTabbedPane tabbedPane1;
    private JPanel graphpanel;
    private JButton button1;
    private JLabel label1;
    private JPanel minerPanel;
    private JLabel label2;
    private JTextField textField1;
    private JButton button3;
    private JButton button4;
    private JTextField textField2;
    private JLabel label3;
    private JLabel label4;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;
    private JCheckBox checkBox4;
    private JCheckBox checkBox5;
    private JPanel checkerPanel;
    private JPanel visualizerPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
