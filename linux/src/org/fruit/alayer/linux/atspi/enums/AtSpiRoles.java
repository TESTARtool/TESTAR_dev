package org.fruit.alayer.linux.atspi.enums;


/**
 * Enumerates possible AT-SPI roles used to specify the UI role of an accessible object.
 */
public enum AtSpiRoles {


    /**
     * A role indicating an error condition, such as uninitialized Role data.
     */
    Invalid,                //A Role indicating an error condition, such as uninitialized Role data.
    AcceleratorLabel,       //Object is a label indicating the keyboard accelerators for the parent.
    Alert,                  //Object is used to alert the user about something.
    Animation,              //Object contains a dynamic or moving image of some kind.
    Arrow,                  //Object is a 2d directional indicator.
    Calendar,               //Object contains one or more dates, usually arranged into a 2d list.
    Canvas,                 //Object that can be drawn into and is used to trap events.
    CheckBox,               //A choice that can be checked or unchecked and provides a separate indicator for the current state.
    CheckMenuItem,          //A menu item that behaves like a check box
    ColorChooser,           //A specialized dialog that lets the user choose a color.
    ColumnHeader,           //The header for a column of data.
    ComboBox,               //A list of choices the user can select from.
    DateEditor,             //An object which allows entry of a date.
    DesktopIcon,            //An inconifed internal frame within a DESKTOP_PANE.
    DesktopFrame,           //A pane that supports internal frames and iconified versions of those internal frames.
    Dial,                   //An object that allows a value to be changed via rotating a visual element, or which displays a value via such a rotating element.
    Dialog,                 //A top level window with title bar and a border.
    DirectoryPane,          //A pane that allows the user to navigate through and select the contents of a directory.
    DrawingArea,            //A specialized dialog that displays the files in the directory and lets the user select a file, browse a different directory, or specify a filename.
    FileChooser,            //An object used for drawing custom user interface elements.
    Filler,                 //A object that fills up space in a user interface.
    FocusTraversable,       //reserved for future use.
    FontChooser,            //Allows selection of a display font.
    Frame,                  //A top level window with a title bar, border, menubar, etc.
    GlassPane,              //A pane that is guaranteed to be painted on top of all panes beneath it.
    HTMLContainer,          //A document container for HTML, whose children represent the document content.
    Icon,                   //A small fixed size picture, typically used to decorate components.
    Image,                  //An image, typically static.
    InternalFrame,          //A frame-like object that is clipped by a desktop pane.
    Label,                  //An object used to present an icon or short string in an interface.
    LayeredPane,            //A specialized pane that allows its children to be drawn in layers, providing a form of stacking order.
    List,                   //An object that presents a list of objects to the user and allows the user to select one or more of them.
    ListItem,               //An object that represents an element of a list.
    Menu,                   //An object usually found inside a menu bar that contains a list of actions the user can choose from.
    MenuBar,                //An object usually drawn at the top of the primary dialog box of an application that contains a list of menus the user can choose from.
    MenuItem,               //An object usually contained in a menu that presents an action the user can choose.
    OptionPane,             //A specialized pane whose primary use is inside a DIALOG.
    PageTab,                //An object that is a child of a page tab list.
    PageTabList,            //An object that presents a series of panels (or page tabs), one at a time, through some mechanism provided by the object.
    Panel,                  //A generic container that is often used to group objects.
    PasswordText,           //A text object uses for passwords, or other places where the text content is not shown visibly to the user.
    PopupMenu,              //A temporary window that is usually used to offer the user a list of choices, and then hides when the user selects one of those choices.
    ProgressBar,            //An object used to indicate how much of a task has been completed.
    PushButton,             //An object the user can manipulate to tell the application to do something.
    RadioButton,            //A specialized check box that will cause other radio buttons in the same group to become uncghecked when this one is checked.
    RadioMenuItem,          //Object is both a menu item and a "radio button"
    RootPane,               //A specialized pane that has a glass pane and a layered pane as its children.
    RowHeader,              //The header for a row of data.
    ScrollBar,              //An object usually used to allow a user to incrementally view a large amount of data by moving the bounds of a viewport along a one-dimensional axis.
    ScrollPane,             //An object that allows a user to incrementally view a large amount of information.
    Separator,              //An object usually contained in a menu to provide a visible and logical separation of the contents in a menu.
    Slider,                 //An object that allows the user to select from a bounded range.
    SpinButton,             //An object which allows one of a set of choices to be selected, and which displays the current choice.
    SplitPane,              //A specialized panel that presents two other panels at the same time.
    StatusBar,              //Object displays non-quantitative status information
    Table,                  //An object used to repesent information in terms of rows and columns.
    TableCell,              //A 'cell' or discrete child within a Table.
    TableColumnHeader,      //An object which labels a particular column in a Table.
    TableRowHeader,         //An object which labels a particular row in a Table.
    TearoffMenuItem,        //Object allows menu to be removed from menubar and shown in its own window.
    Terminal,               //An object that emulates a terminal.
    Text,                   //An object that presents text to the user, of nonspecific type.
    ToggleButton,           //A specialized push button that can be checked or unchecked, but does not procide a separate indicator for the current state.
    ToolBar,                //A bar or palette usually composed of push buttons or toggle buttons.
    ToolTip,                //An object that provides information about another object.
    Tree,                   //An object used to repsent hierarchical information to the user.
    TreeTable,              //An object that presents both tabular and hierarchical info to the user.
    Unknown,                //The object contains some Accessible information, but its role is not known.
    Viewport,               //An object usually used in a scroll pane, or to otherwise clip a larger object or content renderer to a specific onscreen viewport.
    Window,                 //A ¨top level window¨ with no title or border.
    Extended,               //means that the role for this item is known, but not included in the core enumeration
    Header,                 //An object that serves as a document header.
    Footer,                 //An object that serves as a document footer.
    Paragraph,              //An object which is contains a single paragraph of text content.
    Ruler,                  //An object which describes margins and tab stops, etc.
    Application,            //An object corresponding to the toplevel accessible of an application, which may contain ROLE_FRAME objects or other accessible objects.
    Autocomplete,           //The object is a dialog or list containing items for insertion into an entry widget, for instance a list of words for completion of a text entry.
    EditBar,                //The object is an editable text object in a toolbar.
    Embedded,               //The object is an embedded component container.
    Entry,                  //The object is a component whose textual content may be entered or modified by the user, provided STATE_EDITABLE is present.
    Chart,                  //The object is a graphical depiction of quantitative data.
    Caption,                //The object contains descriptive information, usually textual, about another user interface element such as a table, chart, or image.
    DocumentFrame,          //The object is a visual frame or container which contains a view of document content.
    Heading,                //The object serves as a heading for content which follows it in a document.
    Page,                   //The object is a containing instance which encapsulates a page of information.
    Section,                //The object is a containing instance of document content which constitutes a particular 'logical' section of the document.
    RedundantObject,        //The object is redundant with another object in the hierarchy, and is exposed for purely technical reasons. Objects of this role should be ignored by clients, if they are encountered at all.
    Form,                   //The object is a containing instance of document content which has within it components with which the user can interact in order to input information; i.e. the object is a container for pushbuttons, comboboxes, text input fields, and other 'GUI' components. ATSPI_ROLE_FORM should not, in general, be used for toplevel GUI containers or dialogs, but should be reserved for 'GUI' containers which occur within document content, for instance within Web documents, presentations, or text documents. Unlike other GUI containers and dialogs which occur inside application instances, ATSPI_ROLE_FORM containers' components are associated with the current document, rather than the current foreground application or viewer instance.
    Link,                   //The object is a hypertext anchor, i.e. a "link" in a hypertext document. Such objects are distinct from 'inline' content which may also use the AtspiHypertext/AtspiHyperlink interfacesto indicate the range/location within a text object where an inline or embedded object lies.
    InputMethodWindow,      //The object is a window or similar viewport which is used to allow composition or input of a 'complex character', in other words it is an "input method window".
    TableRow,               //A row in a table.
    TreeItem,               //An object that represents an element of a tree.
    DocumentSpreadsheet,    //A document frame which contains a spreadsheet.
    DocumentPresentation,   //A document frame which contains a presentation or slide
    DocumentText,           //A document frame which contains textual content, such as found in a word processing application.
    DocumentWeb,            //A document frame which contains HTML or other markup suitable for display in a web browser.
    DocumentEmail,          //A document frame which contains email content to be displayed or composed either in plain text or HTML.
    Comment,                //An object found within a document and designed to present a comment, note, or other annotation. In some cases, this object might not be visible until activated.
    ListBox,                //A non-collapsible list of choices the user can select from.
    Grouping,               //A group of related widgets. This group typically has a label.
    ImageMap,               //An image map object. Usually a graphic with multiple hotspots, where each hotspot can be activated resulting in the loading of another document or section of a document.
    Notification,           //A transitory object designed to present a message to the user, typically at the desktop level rather than inside a particular application.
    InfoBar,                //An object designed to present a message to the user within an existing window.
    LevelBar,               //A bar that serves as a level indicator to, for instance, show the strength of a password or the state of a battery. Since: 2.8
    TitleBar,               //A bar that serves as the title of a window or a dialog. Since : 2.12
    BlockQuote,             //An object which contains a text section that is quoted from another source. Since : 2.12
    Audio,                  //An object which represents an audio element. Since : 2.1
    Video,                  //An object which represents an video element. Since : 2.1
    Definition,             //A definition of a term or concept. Since : 2.12
    Article,                //A section of a page that consists of a composition that forms an independent part of a document, page, or site. Examples: A blog entry, a news story, a forum post. Since : 2.12
    Landmark,               //A region of a web page intended as a navigational landmark. This is designed to allow Assistive Technologies to provide quick navigation among key regions within a document. Since : 2.12
    Log,                    //A text widget or container holding log content, such as chat history and error logs. In this role there is a relationship between the arrival of new items in the log and the reading order. The log contains a meaningful sequence and new information is added only to the end of the log, not at arbitrary points. Since : 2.12
    Marquee,                //A container where non-essential information changes frequently. Common usages of marquee include stock tickers and ad banners. The primary difference between a marquee and a log is that logs usually have a meaningful order or sequence of important content changes. Since : 2.12
    Math,                   //A text widget or container that holds a mathematical expression. Since : 2.12
    Rating,                 //A widget whose purpose is to display a rating, such as the number of stars associated with a song in a media player. Objects of this role should also implement AtspiValue. Since : 2.12
    Timer,                  //An object containing a numerical counter which indicates an amount of elapsed time from a start point, or the time remaining until an end point. Since : 2.12
    Static,                 //A generic non-container object whose purpose is to display a brief amount of information to the user and whose role is known by the implementor but lacks semantic value for the user. Examples in which ATSPI_ROLE_STATIC is appropriate include the message displayed in a message box and an image used as an alternative means to display text. ATSPI_ROLE_STATIC should not be applied to widgets which are traditionally interactive, objects which display a significant amount of content, or any object which has an accessible relation pointing to another object. The displayed information, as a general rule, should be exposed through the accessible name of the object. For labels which describe another widget, see ATSPI_ROLE_LABEL . For text views, see ATSPI_ROLE_TEXT . For generic containers, see ATSPI_ROLE_PANEL . For objects whose role is not known by the implementor, see ATSPI_ROLE_UNKNOWN . Since : 2.16.
    MathFraction,           //An object that represents a mathematical fraction.
    MathRoot,               //An object that represents a mathematical expression displayed with a radical. Since : 2.16.
    Subscript,              //An object that contains text that is displayed as a subscript. Since : 2.16.
    Superscript,            //An object that contains text that is displayed as a superscript. Since : 2.16.
    LastDefined,            //Not a valid role, used for finding end of enumeration.








}