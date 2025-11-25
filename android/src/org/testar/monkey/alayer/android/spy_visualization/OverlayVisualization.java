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

import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.android.enums.AndroidTags;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class OverlayVisualization extends JLayeredPane {
    private final MobileVisualizationAndroid mobileVisualizationAndroid;
    private final JLabel imageLabel = new JLabel();
    private Integer depth = 5;
    public ArrayList<OverlayBox> boxTrackSetNotDisplayed = new ArrayList<>();
    public ArrayList<OverlayBox> boxTrackSetDisplayed = new ArrayList<>();
    private LinkedList<DefaultMutableTreeNode> queue = new LinkedList<>();
    private ArrayList<DefaultMutableTreeNode> listOfNodes = new ArrayList<>();
    private int originalHeight;
    private int originalWidth;
    private int width = 440;
    private int height = 880;

    public OverlayVisualization(MobileVisualizationAndroid mobileVisualizationAndroid) {
        this.mobileVisualizationAndroid = mobileVisualizationAndroid;
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.white);
        this.add(imageLabel, Integer.valueOf(1));
        imageLabel.setBounds(0,0,width,height);
    }

    public void updateSc(String screenshotPath, JTree tree) {
    	ImageIcon tempImageIcon = new ImageIcon(screenshotPath);
    	originalHeight = tempImageIcon.getIconHeight();
    	originalWidth = tempImageIcon.getIconWidth();

    	Image stateImage = tempImageIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT);
    	ImageIcon stateIcon = new ImageIcon(stateImage);
    	imageLabel.setIcon(stateIcon);

        for (OverlayBox i: boxTrackSetNotDisplayed) {
            this.remove(i);
        }

        for (OverlayBox i: boxTrackSetDisplayed) {
            this.remove(i);
        }


        boxTrackSetNotDisplayed = new ArrayList<>();
        boxTrackSetDisplayed = new ArrayList<>();
        listOfNodes = new ArrayList<>();
        queue = new LinkedList<>();

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)tree.getModel().getRoot();

        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)rootNode.getChildAt(i);
            queue.add(node);
            listOfNodes.add(node);
        }

        while (queue.size() > 0) {
            DefaultMutableTreeNode node = queue.remove();
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)node.getChildAt(i);
                queue.add(childNode);
                listOfNodes.add(childNode);
            }
        }
        createOverLayBoxes();
    }

    public void createOverLayBoxes() {
        //First sort the listOfNodes based on the sizes of the
        Collections.sort(listOfNodes, new Comparator<DefaultMutableTreeNode>() {
            @Override
            public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
                Widget widget1 = (Widget)o1.getUserObject();
                Widget widget2 = (Widget)o2.getUserObject();
                Double area1 = Rect.area(widget1.get(AndroidTags.AndroidBounds));
                Double area2 = Rect.area(widget2.get(AndroidTags.AndroidBounds));
                int compareResult = area1.compareTo(area2);
                if (compareResult != 0) {
                    return compareResult;
                } else {
                    if (o1.getDepth() < o2.getDepth()) {
                        return -1;
                    } else if (o1.getDepth() > o2.getDepth()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        // Now for every node in the list create the box overlay
        //Reverse list first
        Collections.reverse(listOfNodes);
        for (DefaultMutableTreeNode node: listOfNodes) {
            try {
                OverlayBox tempOverlayBox = new OverlayBox();
                tempOverlayBox.create(this, node);
                boxTrackSetNotDisplayed.add(tempOverlayBox);
                depth++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method which is called when a click on the hierarchy tree takes place. And the overlay box needs to turn on.
    public void treeClick(DefaultMutableTreeNode node) {
        ArrayList<OverlayBox> totalList = new ArrayList<>();
        totalList.addAll(boxTrackSetNotDisplayed);
        totalList.addAll(boxTrackSetDisplayed);
        for (OverlayBox i: totalList) {
            if (i.node == node) {
                i.executeTreeClick();
                break;
            }
        }
    }

    public static class OverlayBox extends JLabel{
        private OverlayVisualization overlay;
        public DefaultMutableTreeNode node;
        public boolean displayed = false;
        public OverlayBox instanceOverlayBox = this;

        @Override
        public void paintComponent (Graphics g) {
            this.myPaint(g);
        }

        private void myPaint(Graphics g) {
            super.paintComponent(g);
            Widget widget = (Widget)node.getUserObject();
            //TODO PROBABLY USE HTE CLICKABLE AND WRITABLE FUNCTIONS IN THE GENERIC ANDROID PROTOCOL HERE!
            String className = widget.get(AndroidTags.AndroidClassName);
            Boolean clickable = widget.get(AndroidTags.AndroidClickable) && !(className.equals("android.widget.EditText"));

            if (className.equals("android.widget.EditText")) {
                g.setColor(new Color(0, 0, 255, 130));
                g.drawOval((int)(getWidth()/2.0),(int)((getHeight()/2.0)-1), 12, 12);
                g.setColor(new Color(0, 0, 255, 130));
                g.fillOval((int)(getWidth()/2.0), (int)((getHeight()/2.0)-1), 12, 12);
            } else if (clickable) {
                g.setColor(new Color(0, 255, 0, 130));
                g.drawOval((int)(getWidth()/2.0),(int)((getHeight()/2.0)-1), 12, 12);
                g.setColor(new Color(0, 255, 0, 130));
                g.fillOval((int)(getWidth()/2.0), (int)((getHeight()/2.0)-1), 12, 12);
            }

        }

        public void fakeMouseClick() {
            this.setBackground(new Color(0, 0, 255, 0));
            displayed = false;
            overlay.boxTrackSetNotDisplayed.add(this);
            overlay.boxTrackSetDisplayed.remove(this);
        }

        public void executeTreeClick () {
            // Highlight (blue) on the left screen the selected component of the widget-tree
            this.setOpaque(true);
            this.setBackground(new Color(0, 0, 255, 100));
            displayed = true;
            overlay.boxTrackSetDisplayed.add(instanceOverlayBox);
            overlay.boxTrackSetNotDisplayed.remove(instanceOverlayBox);
            if (overlay.boxTrackSetDisplayed.size() > 1) {
                OverlayBox tempOverlayBox = overlay.boxTrackSetDisplayed.get(0);
                tempOverlayBox.fakeMouseClick();
            }
            overlay.repaint();

        }

        public void create(OverlayVisualization overlay, DefaultMutableTreeNode node) {
            this.overlay = overlay;
            this.node = node;
            Widget widget = (Widget)node.getUserObject();
            Rect bounds = widget.get(AndroidTags.AndroidBounds);
            // Reduce 1 pixel of the sides of the widget that we highlight with colors
            // This will avoid opaque bounds to cover the state image
            this.setBounds((int)((((double)overlay.width)/overlay.originalWidth)*bounds.x()) +1 ,
                    (int)((((double)overlay.height)/overlay.originalHeight)*bounds.y()) +1 ,
                    (int)((((double)overlay.width)/overlay.originalWidth)*bounds.width()) -1 ,
                    (int)((((double)overlay.height)/overlay.originalHeight)*bounds.height()) -1);

            //this.setOpaque(true);
            this.setBackground(new Color(0, 0, 255, 0));

            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    if (!displayed) {
                        //instanceOverlayBox.setOpaque(true);
                        instanceOverlayBox.setBackground(new Color(0, 0, 255, 100));
                        displayed = true;
                        overlay.boxTrackSetDisplayed.add(instanceOverlayBox);
                        overlay.boxTrackSetNotDisplayed.remove(instanceOverlayBox);
                        if (overlay.boxTrackSetDisplayed.size() > 1) {
                            OverlayBox tempOverlayBox = overlay.boxTrackSetDisplayed.get(0);
                            tempOverlayBox.fakeMouseClick();
                        }
                        overlay.repaint();

                        overlay.mobileVisualizationAndroid.overlayToTree(node);
                    } else {
                        instanceOverlayBox.setBackground(new Color(0, 0, 255, 0));
                        displayed = false;
                        overlay.repaint();
                        overlay.boxTrackSetNotDisplayed.add(instanceOverlayBox);
                        overlay.boxTrackSetDisplayed.remove(instanceOverlayBox);
                    }
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    // Highlight (yellow) on the left screen the focused component with the mouse
                    if (!displayed) {
                        instanceOverlayBox.setOpaque(true);
                        instanceOverlayBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        instanceOverlayBox.setBackground(new Color(255, 255, 0, 100));
                        overlay.repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    // Remove the highlight (yellow) of the component that has lost the mouse focus
                    if (!displayed) {
                        instanceOverlayBox.setOpaque(true);
                        instanceOverlayBox.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        instanceOverlayBox.setBackground(new Color(255, 255, 0, 0));
                        overlay.repaint();
                    }
                }
            });

            overlay.add(this, overlay.depth);
        }

    }
}
