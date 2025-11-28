/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.monkey.alayer.android.spy_visualization;

import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.android.AndroidProtocolUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;
import java.util.function.Function;

public class MobileVisualizationAndroid {
    private State usedState;
    private State newState;
    private Function<State, Set<Action>> deriveActionsFunction;
    private final JPanel leftside;
    private final JPanel rightside;
    private final int width = 1100;
    private final int height = 950;
    private final int compWidth = 500;
    private final int compHight = 920;
    private final JFrame frame;
    private OverlayVisualization imagePanel;
    private final JPanel treeVisualizationPanel = new JPanel();
    private TreeVisualizationAndroid treeVizInstance;
    public boolean closedSpyVisualization = false;

    /** Initialization of the android spy mode visualization. */
    public MobileVisualizationAndroid(String screenshotPath, State state, Function<State, Set<Action>> deriveActionsFunction) {
        this.usedState = state;
        this.deriveActionsFunction = deriveActionsFunction;

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
        treeVizInstance = new TreeVisualizationAndroid(this, usedState);
        treeVisualizationPanel.add(treeVizInstance);
        rightside.add(treeVisualizationPanel);

        // Sets the initial screenshot of the SUT
        imagePanel = new OverlayVisualization(this);
        updateScreen();
        leftside.add(imagePanel);

        frame.add(splitPane);
        frame.setVisible(true);
    }

    //Implement this method if we want to click the last selected widget.
//    private void clickWidget() {
//
//        new AndroidActionClick(usedState, widget,
//                widget.get(AndroidTags.AndroidText,""),
//                widget.get(AndroidTags.AndroidAccessibilityId,""),
//                widget.get(AndroidTags.AndroidClassName, ""));
//    }


    /** Method called when the overlay needs to be updated when the state changes. */
    private void updateScreen() {
        // Updates screenshot and overlay
        String screenshotPath = AndroidProtocolUtil.getStateshotSpyMode(usedState);
        imagePanel.updateSc(screenshotPath, treeVizInstance.tree, deriveActionsFunction.apply(usedState));

        frame.revalidate();
        frame.repaint();
    }


    /** Method called from SPY loop to update the tree and overlay of the MobileVisualizationAndroid. */
    public void updateVisualization(State state) {
        this.newState = state;

        // Updates the tree of android widgets if changes occur in the SUT
        boolean updated = treeVizInstance.compareUpdateTree(this.newState);
        if (updated) {
            this.usedState = state;

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
