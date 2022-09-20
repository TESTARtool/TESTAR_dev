package org.testar.monkey.alayer.ios.spy_visualization;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.ios.IOSAppiumFramework;
import org.testar.monkey.alayer.ios.enums.IOSTags;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class OverlayVisualization extends JLayeredPane {
    private final MobileVisualizationIOS mobileVisualizationIOS;
    private ImageIcon imageIcon;
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

    public OverlayVisualization(MobileVisualizationIOS mobileVisualizationIOS) {
        this.mobileVisualizationIOS = mobileVisualizationIOS;
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(Color.white);
        this.add(imageLabel, Integer.valueOf(1));
        imageLabel.setBounds(0,0,width,height);
    }

    public void updateSc(String screenshotPath, JTree tree) {
        ImageIcon tempImageIcon = new ImageIcon(new ImageIcon(screenshotPath).getImage());
        Pair screenDim = IOSAppiumFramework.getScreenSize();
        originalHeight = (int)screenDim.right();
        originalWidth = (int)screenDim.left();
        imageIcon = new ImageIcon(tempImageIcon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));

        imageLabel.setIcon(imageIcon);

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

            // iOS specific make sure the widget is actually on the screen otherwise do not add it.
            // For now use the height specified at the top as limit.
            Widget widget = (Widget)node.getUserObject();
            double heightLocation = widget.get(IOSTags.iosBounds).y();
            if (heightLocation < (880*1.0) && heightLocation >= 0.0) {
                queue.add(node);
                listOfNodes.add(node);
            }
        }

        while (queue.size() > 0) {
            DefaultMutableTreeNode node = queue.remove();
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)node.getChildAt(i);
                Widget widget = (Widget)childNode.getUserObject();
                double heightLocation = widget.get(IOSTags.iosBounds).y();
                if (heightLocation < (880*1.0) && heightLocation >= 0.0) {
                    queue.add(childNode);
                    listOfNodes.add(childNode);
                }
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
                Double area1 = Rect.area(widget1.get(IOSTags.iosBounds));
                Double area2 = Rect.area(widget2.get(IOSTags.iosBounds));
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
            OverlayBox tempOverlayBox = new OverlayBox();
            tempOverlayBox.create(this, node);
            boxTrackSetNotDisplayed.add(tempOverlayBox);
            depth++;
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
            //TODO fix this
            String className = widget.get(IOSTags.iosClassName);
            Boolean clickable = className.equals("XCUIElementTypeButton") || className.equals("XCUIElementTypeCell");
            Boolean typeable = className.equals("XCUIElementTypeTextField") || widget.get(IOSTags.iosAccessibilityId).equals("MONEY_TRANSFER_AMOUND_FIELD");


            if (typeable) {
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
            Rect bounds = widget.get(IOSTags.iosBounds);
            this.setBounds((int)((((double)overlay.width)/overlay.originalWidth)*bounds.x()),
                    (int)((((double)overlay.height)/overlay.originalHeight)*bounds.y()),
                    (int)((((double)overlay.width)/overlay.originalWidth)*bounds.width()),
                    (int)((((double)overlay.height)/overlay.originalHeight)*bounds.height()));

            this.setOpaque(true);
            this.setBackground(new Color(0, 0, 255, 0));

            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    if (!displayed) {
                        instanceOverlayBox.setOpaque(true);
                        instanceOverlayBox.setBackground(new Color(0, 0, 255, 100));
                        displayed = true;
                        overlay.boxTrackSetDisplayed.add(instanceOverlayBox);
                        overlay.boxTrackSetNotDisplayed.remove(instanceOverlayBox);
                        if (overlay.boxTrackSetDisplayed.size() > 1) {
//                            System.out.println("GOT INTO THE DISABLE OVERLAY SECTION");
                            OverlayBox tempOverlayBox = overlay.boxTrackSetDisplayed.get(0);
                            tempOverlayBox.fakeMouseClick();
                        }
                        overlay.repaint();

                        Widget w = (Widget)node.getUserObject();
                        System.out.println("XPATH: " + w.get(IOSTags.iosXpath));
                        System.out.println("ACCESS ID: " + w.get(IOSTags.iosAccessibilityId));

                        overlay.mobileVisualizationIOS.overlayToTree(node);
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
                    if (!displayed) {
                        instanceOverlayBox.setOpaque(true);
                        instanceOverlayBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        instanceOverlayBox.setBackground(new Color(255, 255, 0, 100));
                        overlay.repaint();
                    }

                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
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

