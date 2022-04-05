package nl.ou.testar.visualvalidation.extractor;


import nl.ou.testar.visualvalidation.Location;
import org.apache.logging.log4j.Level;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.fruit.Environment;
import org.fruit.Util;
import org.fruit.alayer.Roles;
import org.fruit.alayer.Shape;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Tags;
import org.fruit.alayer.TagsBase;
import org.fruit.alayer.Widget;
import org.testar.Logger;
import org.testar.settings.ExtendedSettingsFactory;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fruit.alayer.Tags.*;

public class ExpectedTextExtractorBase extends Thread implements TextExtractorInterface {
    private static final String TAG = "ExpectedTextExtractor";
    /**
     * Lookup table to map the name of the name of a {@link Tag}, to the actual {@link Tag}.
     * Holds all the available tag's which could hold text (String types, only). The key represents the {@link Tag} in String type, value
     */
    @SuppressWarnings("unchecked")
    private static final Map<String, Tag<String>> _tag = TagsBase.tagSet().stream()
            .filter(tag -> tag.type().equals(String.class))
            .collect(Collectors.toMap(Tag::name, tag -> (Tag<String>) tag));
    private final Boolean _threadSync = true;
    final private Tag<String> defaultTag;
    private final boolean _loggingEnabled;
    /**
     * A blacklist for {@link Widget}'s which should be ignored based on their {@code Role} because they don't contain
     * readable text. Optional when the value (which represents the ancestor path) is set, the {@link Widget} should
     * only be ignored when the ancestor path is equal with the {@link Widget} under investigation.
     */
    private final Map<String, List<String>> _blacklist = new HashMap<>();
    /**
     * A lookup table which indicates based on the {@code Role} which {@link Tag} should be used to extract the text.
     */
    private final Map<String, String> _lookupTable = new HashMap<>();
    AtomicBoolean running = new AtomicBoolean(true);
    private org.fruit.alayer.State _state = null;
    private ExpectedTextCallback _callback = null;
    private Widget _widget = null;

    ExpectedTextExtractorBase(Tag<String> defaultTag) {
        WidgetTextConfiguration config = ExtendedSettingsFactory.createWidgetTextConfiguration();
        // Load the extractor configuration into a lookup table for quick access.
        try {
            config.widget.forEach(it -> {
                if (it.ignore) {
                    List<String> ancestor = it.ancestor.isEmpty() ?
                            Collections.emptyList() : Collections.singletonList(it.ancestor);
                    _blacklist.merge(it.role, ancestor, (list1, list2) ->
                            Stream.concat(list1.stream(), list2.stream()).collect(Collectors.toList()));
                } else {
                    _lookupTable.put(it.role, it.tag);
                }
            });
        } catch (NullPointerException ignored) {
        }

        _loggingEnabled = config.loggingEnabled;

        this.defaultTag = defaultTag;

        setName(TAG);
        start();
    }

    static Rectangle getLocation(Widget widget) {
        Shape dimension = widget.get(Shape, null);

        int x = dimension != null ? (int) dimension.x() : 0;
        int y = dimension != null ? (int) dimension.y() : 0;
        int width = dimension != null ? (int) dimension.width() : 0;
        int height = dimension != null ? (int) dimension.height() : 0;

        return new Rectangle(x, y, width, height);
    }

    @Override
    public void ExtractExpectedText(org.fruit.alayer.State state, @Nullable Widget widget, ExpectedTextCallback callback) {
        synchronized (_threadSync) {
            _state = state;
            _widget = widget;
            _callback = callback;
            if (_loggingEnabled) {
                Logger.log(Level.TRACE, TAG, "Queue new text extract.");
            }
            _threadSync.notifyAll();
        }
    }

    @Override
    public void run() {
        while (running.get()) {
            synchronized (_threadSync) {
                try {
                    // Wait until we need to inspect a new image.
                    _threadSync.wait();
                    if (!running.get()) {
                        break;
                    }
                    extractText();

                } catch (InterruptedException e) {
                    Logger.log(Level.ERROR, TAG, "Wait interrupted");
                    e.printStackTrace();
                }
            }
        }
    }

    private void extractText() {
        if (_state == null || _callback == null) {
            Logger.log(Level.ERROR, TAG, "Should not try to extract text on empty state/callback");
            return;
        }

        // Acquire the absolute location of the SUT on the screen.
        Rectangle applicationPosition = null;
        for (Widget widget : _state) {
            String rootElementPath = "[0]";
            if (widget.get(Path).contentEquals(rootElementPath)) {
                applicationPosition = getLocation(widget);
                break;
            }
        }
        Objects.requireNonNull(applicationPosition);

        double displayScale = Environment.getInstance().getDisplayScale(_state.get(Tags.HWND, (long) 0));
        List<ExpectedElement> expectedElements = new ArrayList<>();
        if (_widget != null) {
            extractedText(getLocation(_widget), displayScale, expectedElements, _widget);
        } else {
            Rectangle finalApplicationPosition = applicationPosition;
            _state.forEach(widget -> extractedText(finalApplicationPosition, displayScale, expectedElements, widget));
        }

        _callback.ReportExtractedText(expectedElements);
    }

    private void extractedText(Rectangle applicationPosition, double displayScale, List<ExpectedElement> expectedElements, Widget widget) {
        String widgetRole = widget.get(Role).name();
        String text = widget.get(getVisualTextTag(widgetRole), "");
        StringBuilder sb = new StringBuilder();
        Util.ancestors(widget).forEach(it -> sb.append(WidgetTextSetting.ANCESTOR_SEPARATOR).append(it.get(Role, Roles.Widget)));
        String ancestors = sb.toString();
        boolean ignored = true;

        if (widgetIsIncluded(widget, widgetRole, ancestors)) {
            if (text != null && !text.isEmpty()) {
                Rectangle absoluteLocation = getLocation(widget);
                Location relativeLocation = new Location(
                        (int) ((absoluteLocation.x - applicationPosition.x) * displayScale),
                        (int) ((absoluteLocation.y - applicationPosition.y) * displayScale),
                        (int) (absoluteLocation.width * displayScale),
                        (int) (absoluteLocation.height * displayScale));
                expectedElements.add(new ExpectedElement(relativeLocation, text));
                ignored = false;
            }
        }

        if (_loggingEnabled) {
            Logger.log(Level.INFO, TAG, "Widget {} with role {} and ancestors {} is {}",
                    text, widgetRole, ancestors, ignored ? "ignored" : "added");
        }
    }

    protected boolean widgetIsIncluded(Widget widget, String role, String ancestors) {
        boolean containsReadableText = true;
        if (_blacklist.containsKey(role)) {
            containsReadableText = false;
            try {
                List<String> blacklistedAncestors = _blacklist.get(role);
                if (!blacklistedAncestors.isEmpty()) {
                    // Check if we should ignore this widget based on its ancestors.
                    containsReadableText = blacklistedAncestors.stream().noneMatch(ancestors::equals);
                }
            } catch (NullPointerException ignored) {

            }
        }
        return containsReadableText;
    }

    private Tag<String> getVisualTextTag(String widgetRole) {
        return _tag.getOrDefault(_lookupTable.getOrDefault(widgetRole, ""), defaultTag);
    }

    @Override
    public void Destroy() {
        stopAndJoinThread();
        if (_loggingEnabled) {
            Logger.log(Level.DEBUG, TAG, "Extractor destroyed.");
        }
    }

    private void stopAndJoinThread() {
        synchronized (_threadSync) {
            running.set(false);
            _threadSync.notifyAll();
        }

        try {
            join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
