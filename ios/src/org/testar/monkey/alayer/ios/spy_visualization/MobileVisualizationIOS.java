package org.testar.monkey.alayer.ios.spy_visualization;

import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.ios.IOSProtocolUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class MobileVisualizationIOS {
    private State usedState;
    private State newState;
    private final JPanel leftside;
    private final JPanel rightside;
    private final int width = 1100;
    private final int height = 950;
    private final int compWidth = 500;
    private final int compHight = 920;
    private final JFrame frame;
    private OverlayVisualization imagePanel;
    private final JPanel treeVisualizationPanel = new JPanel();
    private TreeVisualizationIOS treeVizInstance;
    public boolean closedSpyVisualization = false;

    /** Initialization of the android spy mode visualization. */
    public MobileVisualizationIOS(String screenshotPath, State state) {
        this.usedState = state;
        frame = new JFrame("Information screen SUT");

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                closedSpyVisualization = true;
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setSize(width,height);
        frame.setResizable(false);

        leftside = new JPanel();
        rightside = new JPanel();
        this.start(screenshotPath);
    }


    /** method which creates all the components in the additional java frame which is created. */
    private void start(String screenshotPath) {

        try {
            TimeUnit.MILLISECONDS.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        leftside.setSize(compWidth, compHight);
        rightside.setSize(compHight,compHight);
        leftside.setVisible(true);
        rightside.setVisible(true);

        // Creates left and right hand side in the screen
        JSplitPane splitPane = new JSplitPane();
        splitPane.setSize(width-10, height-10);
        splitPane.setDividerSize(0);
        splitPane.setDividerLocation(compWidth);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(leftside);
        splitPane.setRightComponent(rightside);
        splitPane.setVisible(true);

        // Creates and adds button to right hand side screen
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateScreen();
            }
        });

        rightside.add(updateButton);

        //Sets the initial state tree of the SUT
        treeVizInstance = new TreeVisualizationIOS(this, (usedState.root()));
        treeVisualizationPanel.add(treeVizInstance);
        rightside.add(treeVisualizationPanel);

        // Sets the initial screenshot of the SUT
        imagePanel = new OverlayVisualization(this);
        imagePanel.updateSc(screenshotPath, treeVizInstance.tree);
        leftside.add(imagePanel);

        updateStateVisualization(usedState);

        frame.add(splitPane);
        frame.setVisible(true);
    }


    /** Method called when the overlay needs to be updated when the state changes. */
    private void updateScreen() {
        // Updates screenshot and overlay

        LocalTime startTime = LocalTime.now();
        System.out.println("start updateScreen spy mode: " + startTime);
        String screenshotPath = IOSProtocolUtil.getStateshotSpyMode(usedState);
        imagePanel.updateSc(screenshotPath, treeVizInstance.tree);
        LocalTime endTime = LocalTime.now();
        System.out.println("end updateScreen spy mode: " + endTime);
        long timeBetween = ChronoUnit.MILLIS.between(startTime, endTime);
        System.out.println("Time between updateScreen spy mode: " + timeBetween);

        frame.revalidate();
        frame.repaint();
    }




    /** Method called from SPY loop to update the tree and overlay of the MobileVisualizationIOS. */
    public void updateStateVisualization(State state) {
        this.newState = state;

        // Updates the tree of IOS widgets if changes occur in the SUT
        LocalTime startTime = LocalTime.now();
        System.out.println("start compareTree spy mode: " + startTime);
        boolean updated = treeVizInstance.createCompareTree(this.newState.root());
        LocalTime endTime = LocalTime.now();
        System.out.println("end compareTree spy mode: " + endTime);
        long timeBetween = ChronoUnit.MILLIS.between(startTime, endTime);
        System.out.println("Time between compareTree spy mode: " + timeBetween);

        if (updated) {
            this.usedState = state;
            System.out.println("ACTUALLY UPDATING updateStateVisualization");

            // Makes sure the screenshot and overlay are updated according to the changes in the widget tree
            updateScreen();
        }
    }


    /** Method to pass the click in overlay from overlay to tree */
    public void overlayToTree(DefaultMutableTreeNode node) {
        treeVizInstance.triggerTreeClick(node);
    }


    /** Method to pass the click in tree from tree to overlay */
    public void treeToOverlay(DefaultMutableTreeNode node) {
        imagePanel.treeClick(node);
    }
}

