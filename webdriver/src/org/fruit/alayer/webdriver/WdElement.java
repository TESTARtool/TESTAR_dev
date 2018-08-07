package org.fruit.alayer.webdriver;

import org.fruit.alayer.Rect;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WdElement implements Serializable {
  private static final long serialVersionUID = 2695983969893321255L;

  // TODO Access
  List<WdElement> children = new ArrayList<>();
  WdElement parent;
  WdRootElement root;
  WdWidget backRef;

  // TODO These might need to be fetched
  public boolean blocked;
  long culture = 0L;
  boolean isModal = false; // i.c.w. access key

  public String id, name, tagName, textContent, helpText;
  public List<String> cssClasses = new ArrayList<>();
  public String display, type;

  boolean enabled, ignore;
  public boolean isClickable;
  boolean isContentElement, isControlElement;
  boolean hasKeyboardFocus, isKeyboardFocusable;
  String acceleratorKey, accessKey;
  String valuePattern;

  double zindex;
  Rect rect;
  boolean scrollPattern, hScroll, vScroll;
  public double hScrollViewSize, vScrollViewSize, hScrollPercent, vScrollPercent;

  // Keep these here for fillScrollValues
  // TODO Check overflow with Firefox
  protected String overflowX, overflowY;
  protected long innerWidth, innerHeight;
  protected long clientWidth, clientHeight;
  public long scrollWidth, scrollHeight;
  public long scrollLeft, scrollTop;

  @SuppressWarnings("unchecked")
  public WdElement(Map<String, Object> packedElement,
                   WdRootElement root, WdElement parent) {
    this.root = root;
    this.parent = parent;

    id = (String) packedElement.get("id");
    name = (String) packedElement.get("name");
    tagName = (String) packedElement.get("tagName");
    try {
      textContent = ((String) packedElement.get("textContent"))
          .replaceAll("\\s+", " ").trim();
    }
    catch (NullPointerException npe) {
      System.out.println();
      System.out.println("Hier !!!!!");
      System.out.println(textContent);

      textContent = "";
    }
    helpText = (String) packedElement.get("title");
    valuePattern = (String) packedElement.getOrDefault("href", "");
    if (valuePattern == null || valuePattern.equals("")) {
      valuePattern = String.valueOf(packedElement.getOrDefault("value", ""));
    }

    String tmp = (String) packedElement.getOrDefault("cssClasses", "");
    if (tmp != null) {
      cssClasses.addAll(Arrays.asList(tmp.split(" ")));
    }
    display = (String) packedElement.get("display");
    type = (String) packedElement.get("type");

    zindex = (double) (long) packedElement.get("zIndex");
    fillRect(packedElement);
    fillDimensions(packedElement);

    blocked = (Boolean) packedElement.get("isBlocked");
    isClickable = (Boolean) packedElement.get("isClickable");
    isKeyboardFocusable = getIsFocusable();
    // TODO Check if this works
    hasKeyboardFocus = (Boolean) packedElement.get("hasKeyboardFocus");

    // TODO Also check for clickable / writeable
    enabled = !Constants.hiddenTags.contains(tagName);
    if (display != null && display.toLowerCase().equals("none")) {
      enabled = false;
    }

    List<Map<String, Object>> wrappedChildren =
        (List<Map<String, Object>>) packedElement.get("wrappedChildren");
    for (Map<String, Object> wrappedChild : wrappedChildren) {
      WdElement child = new WdElement(wrappedChild, root, this);
      if (!Constants.hiddenTags.contains(child.tagName) &&
          !Constants.ignoredTags.contains(child.tagName)) {
        children.add(child);
      }
    }

    setName();
    fillScrollValues();
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }

  private void readObject(ObjectInputStream ois)
      throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
  }

  // TODO Check if this is correct
  private void setName() {
    Map<String, String> labelmap = WdStateFetcher.getLabelmap();

    name = labelmap.getOrDefault(name, name);
    if (name == null || name.equals("null") || name.isEmpty()) {
      if (id != null && !id.isEmpty()) {
        name = id;
      }
      else {
        name = textContent;
      }
    }
  }

  public boolean getIsFocusable() {
    // TODO It's much more complex than this
    // https://allyjs.io/data-tables/focusable.html#document-elements
    // https://allyjs.io/tests/focusable/test.html
    // https://gist.github.com/jamiewilson/c3043f8c818b6b0ccffd
    // https://www.w3.org/TR/html5/editing.html#focus

    List<String> focusableTags =
        Arrays.asList("input", "select", "textarea", "a", "button", "area");
    return focusableTags.contains(tagName);
  }

  protected void fillScrollValues() {
    List<String> scrollOn = Arrays.asList("auto", "scroll", "visible");
    hScroll = scrollOn.contains(overflowX) && scrollWidth > clientWidth;
    if (scrollWidth != clientWidth) {
      hScrollPercent = 100.0 * scrollLeft / (scrollWidth - clientWidth);
    }
    hScrollViewSize = 100.0 * clientWidth / scrollWidth;

    vScroll = scrollOn.contains(overflowY) && scrollHeight > clientHeight;
    if (scrollHeight != clientHeight) {
      vScrollPercent = 100.0 * scrollTop / (scrollHeight - clientHeight);
    }
    vScrollViewSize = 100.0 * clientHeight / scrollHeight;

    scrollPattern = hScroll || vScroll;
  }

  public boolean visibleAt(double x, double y) {
    int scrollLeft = (root == null) ? 0 : (int) root.scrollLeft;
    int scrollHeight = (root == null) ? 0 : (int) root.scrollHeight;
    return rect != null && rect.contains(x - scrollLeft, y - scrollHeight);
  }

  public boolean visibleAt(double x, double y, boolean obscuredByChildFeature) {
    return visibleAt(x, y);
  }

  @SuppressWarnings("unchecked")
  /*
   * This gets the position relative to the viewport
   */
  private void fillRect(Map<String, Object> packedElement) {
    List<Long> rect = (List<Long>) packedElement.get("rect");
    this.rect = Rect.from(rect.get(0), rect.get(1), rect.get(2), rect.get(3));
  }

  @SuppressWarnings("unchecked")
  private void fillDimensions(Map<String, Object> packedElement) {
    Map<String, Object> dims = (Map<String, Object>) packedElement.get("dimensions");
    overflowX = (String) dims.get("overflowX");
    overflowY = (String) dims.get("overflowY");
    // TODO Not used?
    innerWidth = (long) dims.get("innerWidth");
    innerHeight = (long) dims.get("innerHeight");
    clientWidth = (long) dims.get("clientWidth");
    clientHeight = (long) dims.get("clientHeight");
    scrollWidth = (long) dims.get("scrollWidth");
    scrollHeight = (long) dims.get("scrollHeight");
    scrollLeft = (long) dims.get("scrollLeft");
    scrollTop = (long) dims.get("scrollTop");
  }
}
