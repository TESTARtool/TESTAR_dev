package org.testar.settings.backend;

import java.io.Serializable;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(
    name = "WebSocket",
    category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE,
    printObject = true
)
public class WebSocketAppender extends AbstractAppender {

    protected WebSocketAppender(
        String name,
        Filter filter,
        Layout<? extends Serializable> layout,
        boolean ignoreExceptions
    ) {
        super(name, filter, layout, ignoreExceptions, Property.EMPTY_ARRAY);
    }

    @PluginFactory
    public static WebSocketAppender createAppender(
        @PluginAttribute("name") String name,
        @PluginAttribute(
            value = "ignoreExceptions",
            defaultBoolean = true
        ) boolean ignoreExceptions,
        @PluginElement("Layout") Layout<? extends Serializable> layout,
        @PluginElement("Filter") Filter filter
    ) {
        if (name == null) {
            LOGGER.error("No name provided for WebSocketAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }

        return new WebSocketAppender(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        try {
            byte[] bytes = getLayout().toByteArray(event);
            String logMessage = new String(bytes);
            WebSocket.broadcastLog(logMessage);
        } catch (Exception e) {
            // Log4j will handle this based on ignoreExceptions setting
            error("Failed to broadcast log via WebSocket", event, e);
        }
    }
}
