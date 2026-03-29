package org.testar.core.state;

/**
 * A state describes an <code>SUT</code>'s state. It includes the currently
 * visible widgets which form a widget tree. Each of these widgets has
 * properties like "Title", "Enabled", "Shape", ... which can be queried.
 * However, states are not restricted to visual entities and can contain
 * various kinds of additional data.
 * 
 * A state forms a so called widget tree with the root node of that tree representing
 * the system itself. For example
 *
 */
public interface State extends Widget, Iterable<Widget> {
}
