// Implements WS-FUNC-TEST-SETTINGS-004: preserves inspector structure beside selected Spy properties.
export function visibleSpyProperties(widget) {
    if (!widget) {
        return [];
    }

    const metadata = [
        ["WidgetId", widget.id || ""],
        ["ParentId", widget.parentId || ""],
        ["X", String(Math.round(widget.x || 0))],
        ["Y", String(Math.round(widget.y || 0))],
        ["Width", String(Math.round(widget.width || 0))],
        ["Height", String(Math.round(widget.height || 0))]
    ];

    return [...metadata, ...Object.entries(widget.properties || {})]
        .filter(([, value]) => value !== null && value !== undefined && String(value).trim() !== "");
}
