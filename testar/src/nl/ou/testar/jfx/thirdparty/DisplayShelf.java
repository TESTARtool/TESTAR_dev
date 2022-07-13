/*
 * Copyright (c) 2008, 2013 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nl.ou.testar.jfx.thirdparty;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Simple 7 segment LED style digit. It supports the numbers 0 through 9.
 */
/**
 * A ui control which displays a browsable display shelf of images
 */
public class DisplayShelf extends Region {

    // Added more duration constant (IVS-150)
    // @author Michael Tikhonenko (Marviq B.V)
    private final Duration NORMAL_DURATION = Duration.millis(500);
    private final Duration LONG_DURATION = Duration.millis(2000);
    private final Duration SHORT_DURATION =  Duration.millis(250);
    private final int SLOW_SCROLL_MILLIS = 2100;
    // End of changes by IVS-150

    private final Interpolator INTERPOLATOR = Interpolator.EASE_BOTH;
    private final double LEFT_OFFSET = -110;
    private final double RIGHT_OFFSET = 110;
    private PerspectiveImage[] items;
    private Group centered = new Group();
    private Group left = new Group();
    private Group center = new Group();
    private Group right = new Group();
    private int centerIndex = 0;
    private Timeline timeline;

    // Scroll bar has been removed
    // @author Michael Tikhonenko (Marviq B.V)

    private Rectangle clip = new Rectangle();

    // These settings were modified in tickets IVS-128, IVS-150
    // Also added some extra variables and a simple helper method
    // for scrolling
    // @author Michael Tikhonenko (Marviq B.V)
    private final double SPACING = 100;
    private final double SCALE_LARGE = 0.9;
    private final double SCALE_SMALL = 0.5;
    private final double TOP_OFFSET = 80;
    private final double ASPECT_RATIO = 0.25;
    private final double MAX_MOUSE_OFFSET = 200;
    private final int SIDE_ITEMS_COUNT = 2;
    private final int BUFFER_ITEMS_COUNT = 2;

    private int itemsCount = 0;
    private double pressedX;
    private boolean autoScrollActive = true;

    private void scrollTo(int pos, Duration duration) {
        shiftToCenter(items[pos], duration);
    }
    // End of changes by IVS-128, IVS 150

    // The constructor is substantially modified to loop scroll
    // and do without a scroll bar (IVS-150)
    // @author Michael Tikhonenko (Marviq B.V)
    public DisplayShelf(Image[] images) {

        // set clip
        setClip(clip);

        // create items
        int displayableCount = 2 * (SIDE_ITEMS_COUNT + BUFFER_ITEMS_COUNT) + 1;
        itemsCount = images.length * ((displayableCount - 1) / images.length + 1);
        items = new PerspectiveImage[itemsCount];
        int j = 0;
        for (int i = 0; i < itemsCount; i++) {
            final PerspectiveImage item =
                    items[i] = new PerspectiveImage(images[j]);
            final int index = j;
            item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent me) {
                    scrollTo(index, NORMAL_DURATION);
                }
            });

            item.setAngle(90);

            if (++j >= images.length) {
                j = 0;
            }
        }

        // create content
        centered.getChildren().addAll(left, center, right);
        getChildren().addAll(centered/*, scrollBar*/);
        // listen for keyboard events
        setFocusTraversable(true);
        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.LEFT) {
                    int target = centerIndex + 1;
                    if (target >= itemsCount) {
                        target -= itemsCount;
                    }
                    scrollTo(target, NORMAL_DURATION);
                } else if (ke.getCode() == KeyCode.RIGHT) {
                    int target = centerIndex - 1;
                    if (target < 0) {
                        target += itemsCount;
                    }
                    scrollTo(target, NORMAL_DURATION);
                }
            }
        });

        // Next 4 methods enable panning with mouse
        setOnMousePressed(event -> {
            pressedX = event.getX();
            autoScrollActive = false;
            update(0, SHORT_DURATION);
        });

        setOnMouseReleased(event -> {
            autoScrollActive = true;
        });

        setOnMouseDragged(event -> {
            Double offset = event.getX() - pressedX;
            if (offset < -MAX_MOUSE_OFFSET) {
                offset = -MAX_MOUSE_OFFSET;
            }
            else if (offset > MAX_MOUSE_OFFSET) {
                offset = MAX_MOUSE_OFFSET;
            }
            while (offset >= SPACING) {
                offset -= SPACING;
                pressedX += SPACING;
                int newCenterIndex = centerIndex - 1;
                if (newCenterIndex < 0) {
                    newCenterIndex += itemsCount;
                }
                scrollTo(newCenterIndex, NORMAL_DURATION);
            }
            while (offset < -SPACING) {
                offset += SPACING;
                pressedX -= SPACING;
                int newCenterIndex = centerIndex + 1;
                if (newCenterIndex >= itemsCount) {
                    newCenterIndex -= itemsCount;
                }
                scrollTo(newCenterIndex, NORMAL_DURATION);
            }
        });

        // update
        update();

        // Starting auto scroll
        final Thread autoScrollThread = new Thread(() -> {
            do {
                try {
                    Thread.sleep(SLOW_SCROLL_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (autoScrollActive) {
                    Platform.runLater(() -> {
                        int target = centerIndex - 1;
                        if (target < 0) {
                            target += itemsCount;
                        }
                        scrollTo(target, LONG_DURATION);
                    });
                }
            } while (true);
        });
        autoScrollThread.start();
    }
    // End of changes by IVS-128, IVS 150

    @Override
    protected void layoutChildren() {

        // update clip to our size
        clip.setWidth(getWidth());
        clip.setHeight(getHeight());
        // keep centered centered
        centered.setLayoutY((getHeight() - PerspectiveImage.HEIGHT) / 2);
        centered.setLayoutX((getWidth() - PerspectiveImage.WIDTH) / 2);

        // Scroll bar has been removed
        // @author Michael Tikhonenko (Marviq B.V)
    }

    // Update and shift code substantially modified (IVS-150)
    // @author Michael Tikhonenko (Marviq B.V)
    private void update() {
        update(0, NORMAL_DURATION);
    }

    private void update(int shiftAmount) {
        update(shiftAmount, NORMAL_DURATION);
    }

    private void update(int shiftAmount, Duration duration) {
        // move items to new homes in groups
        left.getChildren().clear();
        center.getChildren().clear();
        right.getChildren().clear();

        int leftItemsCount = SIDE_ITEMS_COUNT;
        int rightItemsCount = SIDE_ITEMS_COUNT;
        leftItemsCount += BUFFER_ITEMS_COUNT;
        rightItemsCount += BUFFER_ITEMS_COUNT;//shiftAmount;

        int index = itemsCount + centerIndex - leftItemsCount;
        if (index < 0) {
            index += itemsCount;
        }
        for (int i = 0; i < leftItemsCount; i++) {
            if (index >= itemsCount) {
                index -= itemsCount;
            }
            left.getChildren().add(items[index++]);
        };

        center.getChildren().add(items[centerIndex]);

        index = centerIndex + 1;
        for (int i = 0; i < rightItemsCount; i++) {
            if (index >= itemsCount) {
                index = 0;
            }
            right.getChildren().add(items[index++]);
        }

        // stop old timeline if there is one running
        if (timeline != null) {
            timeline.stop();
        }
        // create timeline to animate to new positions
        timeline = new Timeline();
        // add keyframes for left items
        final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
        ObservableList<Node> leftItems = left.getChildren();

        for (int i = 0; i < leftItemsCount; i++) {
            final PerspectiveImage it = (PerspectiveImage)leftItems.get(i);//items[i];
            boolean isHidden = false;
            if (shiftAmount > 0 && i <= 2) {
                it.setTranslateX(-left.getChildren().size()
                        * SPACING + SPACING + LEFT_OFFSET);
                it.setTranslateY(TOP_OFFSET);
                it.setScaleX(SCALE_SMALL);
                it.setScaleY(ASPECT_RATIO * SCALE_SMALL);
                it.setAngle(45.0);
                it.setOpacity(0.0);
                isHidden = (i < 2);
            }
            else if (shiftAmount < 0 && i < 2) {
                isHidden = true;
            }

            double newX = -left.getChildren().size()
                    * SPACING + SPACING * i + LEFT_OFFSET;

            keyFrames.add(new KeyFrame(duration,
                    // These settings were changed/added in ticket IVS-128 to conform our style
                    // @author Michael Tikhonenko (Marviq B.V)
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.translateYProperty(), TOP_OFFSET, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), ASPECT_RATIO * SCALE_SMALL, INTERPOLATOR),
                    // End of changes by IVS-128
                    new KeyValue(it.angle, 45.0, INTERPOLATOR),
                    new KeyValue(it.opacityProperty(), (isHidden ? 0.0 : 1.0), INTERPOLATOR)));
        }
        // add keyframe for center item
        final PerspectiveImage centerItem = items[centerIndex];
        keyFrames.add(new KeyFrame(duration,
                // These settings were changed/added in ticket IVS-128 to conform our style
                // @author Michael Tikhonenko (Marviq B.V)
                new KeyValue(centerItem.translateXProperty(), 0, INTERPOLATOR),
                new KeyValue(centerItem.translateYProperty(), TOP_OFFSET, INTERPOLATOR),
                new KeyValue(centerItem.scaleXProperty(), SCALE_LARGE, INTERPOLATOR),
                new KeyValue(centerItem.scaleYProperty(), ASPECT_RATIO * SCALE_LARGE, INTERPOLATOR),
                // End of changes by IVS-128
                new KeyValue(centerItem.angle, 90.0, INTERPOLATOR),
                new KeyValue(centerItem.opacityProperty(), 1.0, INTERPOLATOR)));
        // add keyframes for right items
        ObservableList<Node> rightItems = right.getChildren();
        for (int i = 0; i < rightItemsCount; i++) {
            final PerspectiveImage it = (PerspectiveImage) rightItems.get(i);
            boolean isHidden = false;
            if (shiftAmount < 0 && i >= SIDE_ITEMS_COUNT - 1) {
                it.setTranslateX(rightItems.size()
                        * SPACING - SPACING + RIGHT_OFFSET);
                it.setTranslateY(TOP_OFFSET);
                it.setScaleX(SCALE_SMALL);
                it.setScaleY(ASPECT_RATIO * SCALE_SMALL);
                it.setAngle(135.0);
                it.setOpacity(0.0);
                isHidden = (i > SIDE_ITEMS_COUNT - 1);
            }
            else if (shiftAmount > 0 && i >= SIDE_ITEMS_COUNT) {
                isHidden = true;
            }

            final double newX = rightItems.size()
                    * SPACING - SPACING * (rightItemsCount - i - 1) + RIGHT_OFFSET;
            keyFrames.add(new KeyFrame(duration,
                    // These settings were changed/added in ticket IVS-128 to conform our style
                    // @author Michael Tikhonenko (Marviq B.V)
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.translateYProperty(), TOP_OFFSET, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), ASPECT_RATIO * SCALE_SMALL, INTERPOLATOR),
                    // End of changes by IVS-128
                    new KeyValue(it.angle, 135.0, INTERPOLATOR),
                    new KeyValue(it.opacityProperty(), (isHidden ? 0.0 : 1.0), INTERPOLATOR)));
        }
        // play animation
        timeline.play();
    }

    // A simple helper method

    private void shiftToCenter(PerspectiveImage item, Duration duration) {
        int index = 0;
        for (PerspectiveImage _item: items) {
            if (_item == item) {
                shift(centerIndex - index, duration);
                return;
            }
            index++;
        }
    }

    public void shift(int shiftAmount, Duration duration) {
        if (shiftAmount <= -itemsCount + 1) {
            shiftAmount += itemsCount;
        }
        else if (shiftAmount >= itemsCount - 1) {
            shiftAmount -= itemsCount;
        }

        centerIndex -= shiftAmount;
        if (centerIndex < 0) {
            centerIndex += itemsCount;
        }
        else if (centerIndex >= itemsCount) {
            centerIndex -= itemsCount;
        }
        update(shiftAmount, duration);
    }
    // End of changes by IVS-128, IVS 150
}