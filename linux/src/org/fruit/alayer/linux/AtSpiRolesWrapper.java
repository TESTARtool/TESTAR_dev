package org.fruit.alayer.linux;


import org.fruit.Util;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.linux.atspi.enums.AtSpiRoles;

import java.util.Map;


/**
 * Wraps native roles - it maps the native At-SPI roles to Testar defined ones.
 */
public final class AtSpiRolesWrapper {


    //region Variables


    // The mapping of the navive Role wrappers with an ID (the AtSpiRoles enum value).
    private final static Map<Long, Role> typeIdToRole = Util.newHashMap();


    // Define all native role wrappers.
    public static final Role

            // First the non-existent - they are defined purely to better map with Testar.
            AtSpiWidget = from(-1, "AtSpiWidget", Roles.Widget),

            // The rest maps AtSpiRoles enum ids to a custom name and a Testar defined (parent) Role(s).
            AtSpiInvalid = from(AtSpiRoles.Invalid.ordinal(), "AtSpi" + AtSpiRoles.Invalid.toString(), Roles.Invalid),
            AtSpiAcceleratorLabel = from(AtSpiRoles.AcceleratorLabel.ordinal(), "AtSpi" + AtSpiRoles.AcceleratorLabel.toString(), AtSpiWidget, Roles.Control),
                    AtSpiAlert = from(AtSpiRoles.Alert.ordinal(), "AtSpi" + AtSpiRoles.Alert.toString(), AtSpiWidget, Roles.Control),
                    AtSpiAnimation = from(AtSpiRoles.Animation.ordinal(), "AtSpi" + AtSpiRoles.Animation.toString(), AtSpiWidget, Roles.Control),
                    AtSpiArrow = from(AtSpiRoles.Arrow.ordinal(), "AtSpi" + AtSpiRoles.Arrow.toString(), AtSpiWidget, Roles.Control),
                    AtSpiCalendar = from(AtSpiRoles.Calendar.ordinal(), "AtSpi" + AtSpiRoles.Calendar.toString(), AtSpiWidget, Roles.Control),
                    AtSpiCanvas = from(AtSpiRoles.Canvas.ordinal(), "AtSpi" + AtSpiRoles.Canvas.toString(), AtSpiWidget, Roles.Control),
                    AtSpiCheckBox = from(AtSpiRoles.CheckBox.ordinal(), "AtSpi" + AtSpiRoles.CheckBox.toString(), AtSpiWidget, Roles.ToggleButton),
                    AtSpiCheckMenuItem = from(AtSpiRoles.CheckMenuItem.ordinal(), "AtSpi" + AtSpiRoles.CheckMenuItem.toString(), AtSpiWidget, Roles.ToggleButton),
                    AtSpiColorChooser = from(AtSpiRoles.ColorChooser.ordinal(), "AtSpi" + AtSpiRoles.ColorChooser.toString(), AtSpiWidget, Roles.Dialog),
                    AtSpiColumnHeader = from(AtSpiRoles.ColumnHeader.ordinal(), "AtSpi" + AtSpiRoles.ColumnHeader.toString(), AtSpiWidget, Roles.Control),
                    AtSpiComboBox = from(AtSpiRoles.ComboBox.ordinal(), "AtSpi" + AtSpiRoles.ComboBox.toString(), AtSpiWidget, Roles.ItemContainer),
                    AtSpiDateEditor = from(AtSpiRoles.DateEditor.ordinal(), "AtSpi" + AtSpiRoles.DateEditor.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDesktopIcon = from(AtSpiRoles.DesktopIcon.ordinal(), "AtSpi" + AtSpiRoles.DesktopIcon.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDesktopFrame = from(AtSpiRoles.DesktopFrame.ordinal(), "AtSpi" + AtSpiRoles.DesktopFrame.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDial = from(AtSpiRoles.Dial.ordinal(), "AtSpi" + AtSpiRoles.Dial.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDialog = from(AtSpiRoles.Dialog.ordinal(), "AtSpi" + AtSpiRoles.Dialog.toString(), AtSpiWidget, Roles.Dialog),
                    AtSpiDirectoryPane = from(AtSpiRoles.DirectoryPane.ordinal(), "AtSpi" + AtSpiRoles.DirectoryPane.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDrawingArea = from(AtSpiRoles.DrawingArea.ordinal(), "AtSpi" + AtSpiRoles.DrawingArea.toString(), AtSpiWidget, Roles.Dialog),
                    AtSpiFileChooser = from(AtSpiRoles.FileChooser.ordinal(), "AtSpi" + AtSpiRoles.FileChooser.toString(), AtSpiWidget, Roles.Dialog),
                    AtSpiFiller = from(AtSpiRoles.Filler.ordinal(), "AtSpi" + AtSpiRoles.Filler.toString(), AtSpiWidget, Roles.Control),
                    AtSpiFocusTraversable = from(AtSpiRoles.FocusTraversable.ordinal(), "AtSpi" + AtSpiRoles.FocusTraversable.toString(), Roles.Invalid),
                    AtSpiFontChooser = from(AtSpiRoles.FontChooser.ordinal(), "AtSpi" + AtSpiRoles.FontChooser.toString(), AtSpiWidget, Roles.Dialog),
                    AtSpiFrame = from(AtSpiRoles.Frame.ordinal(), "AtSpi" + AtSpiRoles.Frame.toString(), AtSpiWidget, Roles.Control),
                    AtSpiGlassPane = from(AtSpiRoles.GlassPane.ordinal(), "AtSpi" + AtSpiRoles.GlassPane.toString(), AtSpiWidget, Roles.Control),
                    AtSpiHtmlContainer = from(AtSpiRoles.HTMLContainer.ordinal(), "AtSpi" + AtSpiRoles.HTMLContainer.toString(), AtSpiWidget, Roles.Control),
                    AtSpiIcon = from(AtSpiRoles.Icon.ordinal(), "AtSpi" + AtSpiRoles.Icon.toString(), AtSpiWidget, Roles.Control),
                    AtSpiImage = from(AtSpiRoles.Image.ordinal(), "AtSpi" + AtSpiRoles.Image.toString(), AtSpiWidget, Roles.Control),
                    AtSpiInternalFrame = from(AtSpiRoles.InternalFrame.ordinal(), "AtSpi" + AtSpiRoles.InternalFrame.toString(), AtSpiWidget, Roles.Control),
                    AtSpiLabel = from(AtSpiRoles.Label.ordinal(), "AtSpi" + AtSpiRoles.Label.toString(), AtSpiWidget, Roles.Text),
                    AtSpiLayeredPane = from(AtSpiRoles.LayeredPane.ordinal(), "AtSpi" + AtSpiRoles.LayeredPane.toString(), AtSpiWidget, Roles.Control),
                    AtSpiList = from(AtSpiRoles.List.ordinal(), "AtSpi" + AtSpiRoles.List.toString(), AtSpiWidget, Roles.ItemContainer),
                    AtSpiListItem = from(AtSpiRoles.ListItem.ordinal(), "AtSpi" + AtSpiRoles.ListItem.toString(), AtSpiWidget, Roles.Item),
                    AtSpiMenu = from(AtSpiRoles.Menu.ordinal(), "AtSpi" + AtSpiRoles.Menu.toString(), AtSpiWidget, Roles.ItemContainer),
                    AtSpiMenuBar = from(AtSpiRoles.MenuBar.ordinal(), "AtSpi" + AtSpiRoles.MenuBar.toString(), AtSpiWidget, Roles.ItemContainer),
                    AtSpiMenuItem = from(AtSpiRoles.MenuItem.ordinal(), "AtSpi" + AtSpiRoles.MenuItem.toString(), AtSpiWidget, Roles.Item),
                    AtSpiOptionPane = from(AtSpiRoles.OptionPane.ordinal(), "AtSpi" + AtSpiRoles.OptionPane.toString(), AtSpiWidget, Roles.Control),
                    AtSpiPageTab = from(AtSpiRoles.PageTab.ordinal(), "AtSpi" + AtSpiRoles.PageTab.toString(), AtSpiWidget, Roles.Control),
                    AtSpiPageTabList = from(AtSpiRoles.PageTabList.ordinal(), "AtSpi" + AtSpiRoles.PageTabList.toString(), AtSpiWidget, Roles.Control),
                    AtSpiPanel = from(AtSpiRoles.Panel.ordinal(), "AtSpi" + AtSpiRoles.Panel.toString(), AtSpiWidget, Roles.Control),
                    AtSpiPasswordText = from(AtSpiRoles.PasswordText.ordinal(), "AtSpi" + AtSpiRoles.PasswordText.toString(), AtSpiWidget, Roles.Text),
                    AtSpiPopupMenu = from(AtSpiRoles.PopupMenu.ordinal(), "AtSpi" + AtSpiRoles.PopupMenu.toString(), AtSpiWidget, Roles.Control),
                    AtSpiProgressBar = from(AtSpiRoles.ProgressBar.ordinal(), "AtSpi" + AtSpiRoles.ProgressBar.toString(), AtSpiWidget, Roles.Control),
                    AtSpiPushButton = from(AtSpiRoles.PushButton.ordinal(), "AtSpi" + AtSpiRoles.PushButton.toString(), AtSpiWidget, Roles.Button),
                    AtSpiRadioButton = from(AtSpiRoles.RadioButton.ordinal(), "AtSpi" + AtSpiRoles.RadioButton.toString(), AtSpiWidget, Roles.ToggleButton, Roles.ItemContainer),
                    AtSpiRadioButtonMenuItem = from(AtSpiRoles.RadioMenuItem.ordinal(), "AtSpi" + AtSpiRoles.RadioMenuItem.toString(), AtSpiWidget, Roles.Item),
                    AtSpiRootPane = from(AtSpiRoles.RootPane.ordinal(), "AtSpi" + AtSpiRoles.RootPane.toString(), AtSpiWidget, Roles.Control),
                    AtSpiRowHeader = from(AtSpiRoles.RowHeader.ordinal(), "AtSpi" + AtSpiRoles.RowHeader.toString(), AtSpiWidget, Roles.Control),
                    AtSpiScrollBar = from(AtSpiRoles.ScrollBar.ordinal(), "AtSpi" + AtSpiRoles.ScrollBar.toString(), AtSpiWidget, Roles.Control),
                    AtSpiScrollPane = from(AtSpiRoles.ScrollPane.ordinal(), "AtSpi" + AtSpiRoles.ScrollPane.toString(), AtSpiWidget, Roles.Control),
                    AtSpiSeparator = from(AtSpiRoles.Separator.ordinal(), "AtSpi" + AtSpiRoles.Separator.toString(), AtSpiWidget, Roles.Control),
                    AtSpiSlider = from(AtSpiRoles.Slider.ordinal(), "AtSpi" + AtSpiRoles.Slider.toString(), AtSpiWidget, Roles.Slider),
                    AtSpiSpinButton = from(AtSpiRoles.SpinButton.ordinal(), "AtSpi" + AtSpiRoles.SpinButton.toString(), AtSpiWidget, Roles.Button),
                    AtSpiSplitPane = from(AtSpiRoles.SplitPane.ordinal(), "AtSpi" + AtSpiRoles.SplitPane.toString(), AtSpiWidget, Roles.Control),
                    AtSpiStatusBar = from(AtSpiRoles.StatusBar.ordinal(), "AtSpi" + AtSpiRoles.StatusBar.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTable = from(AtSpiRoles.Table.ordinal(), "AtSpi" + AtSpiRoles.Table.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTableCell = from(AtSpiRoles.TableCell.ordinal(), "AtSpi" + AtSpiRoles.TableCell.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTableColumnHeader = from(AtSpiRoles.TableColumnHeader.ordinal(), "AtSpi" + AtSpiRoles.TableColumnHeader.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTableRowHeader = from(AtSpiRoles.TableRowHeader.ordinal(), "AtSpi" + AtSpiRoles.TableRowHeader.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTearOffMenuItem = from(AtSpiRoles.TearoffMenuItem.ordinal(), "AtSpi" + AtSpiRoles.TearoffMenuItem.toString(), AtSpiWidget, Roles.ItemContainer),
                    AtSpiTerminal = from(AtSpiRoles.Terminal.ordinal(), "AtSpi" + AtSpiRoles.Terminal.toString(), AtSpiWidget, Roles.Control),
                    AtSpiText = from(AtSpiRoles.Text.ordinal(), "AtSpi" + AtSpiRoles.Text.toString(), AtSpiWidget, Roles.Text),
                    AtSpiToggleButton = from(AtSpiRoles.ToggleButton.ordinal(), "AtSpi" + AtSpiRoles.ToggleButton.toString(), AtSpiWidget, Roles.ToggleButton),
                    AtSpiToolBar = from(AtSpiRoles.ToolBar.ordinal(), "AtSpi" + AtSpiRoles.ToolBar.toString(), AtSpiWidget, Roles.Control),
                    AtSpiToolTip = from(AtSpiRoles.ToolTip.ordinal(), "AtSpi" + AtSpiRoles.ToolTip.toString(), AtSpiWidget, Roles.Text),
                    AtSpiTree = from(AtSpiRoles.Tree.ordinal(), "AtSpi" + AtSpiRoles.Tree.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTreeTable = from(AtSpiRoles.TreeTable.ordinal(), "AtSpi" + AtSpiRoles.TreeTable.toString(), AtSpiWidget, Roles.Control),
                    AtSpiUnknown = from(AtSpiRoles.Unknown.ordinal(), "AtSpi" + AtSpiRoles.Unknown.toString(), AtSpiWidget, Roles.Control),
                    AtSpiViewport = from(AtSpiRoles.Viewport.ordinal(), "AtSpi" + AtSpiRoles.Viewport.toString(), AtSpiWidget, Roles.Control),
                    AtSpiWindow = from(AtSpiRoles.Window.ordinal(), "AtSpi" + AtSpiRoles.Window.toString(), AtSpiWidget, Roles.Control),
                    AtSpiExtended = from(AtSpiRoles.Extended.ordinal(), "AtSpi" + AtSpiRoles.Extended.toString(), AtSpiWidget, Roles.Control),
                    AtSpiHeader = from(AtSpiRoles.Header.ordinal(), "AtSpi" + AtSpiRoles.Header.toString(), AtSpiWidget, Roles.Control),
                    AtSpiFooter = from(AtSpiRoles.Footer.ordinal(), "AtSpi" + AtSpiRoles.Footer.toString(), AtSpiWidget, Roles.Control),
                    AtSpiParagraph = from(AtSpiRoles.Paragraph.ordinal(), "AtSpi" + AtSpiRoles.Paragraph.toString(), AtSpiWidget, Roles.Control),
                    AtSpiRuler = from(AtSpiRoles.Ruler.ordinal(), "AtSpi" + AtSpiRoles.Ruler.toString(), AtSpiWidget, Roles.Control),
                    AtSpiApplication = from(AtSpiRoles.Application.ordinal(), "AtSpi" + AtSpiRoles.Application.toString(), AtSpiWidget, Roles.Control, Roles.System),
                    AtSpiAutoComplete = from(AtSpiRoles.Autocomplete.ordinal(), "AtSpi" + AtSpiRoles.Autocomplete.toString(), AtSpiWidget, Roles.Control),
                    AtSpiEditBar = from(AtSpiRoles.EditBar.ordinal(), "AtSpi" + AtSpiRoles.EditBar.toString(), AtSpiWidget, Roles.Text),
                    AtSpiEmbedded = from(AtSpiRoles.Embedded.ordinal(), "AtSpi" + AtSpiRoles.Embedded.toString(), AtSpiWidget, Roles.Control),
                    AtSpiEntry = from(AtSpiRoles.Entry.ordinal(), "AtSpi" + AtSpiRoles.Entry.toString(), AtSpiWidget, Roles.Control),
                    AtSpiChart = from(AtSpiRoles.Chart.ordinal(), "AtSpi" + AtSpiRoles.Chart.toString(), AtSpiWidget, Roles.Control),
                    AtSpiCaption = from(AtSpiRoles.Caption.ordinal(), "AtSpi" + AtSpiRoles.Caption.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDocumentFrame = from(AtSpiRoles.DocumentFrame.ordinal(), "AtSpi" + AtSpiRoles.DocumentFrame.toString(), AtSpiWidget, Roles.Control),
                    AtSpiHeading = from(AtSpiRoles.Heading.ordinal(), "AtSpi" + AtSpiRoles.Heading.toString(), AtSpiWidget, Roles.Text),
                    AtSpiPage = from(AtSpiRoles.Page.ordinal(), "AtSpi" + AtSpiRoles.Page.toString(), AtSpiWidget, Roles.Control),
                    AtSpiSection = from(AtSpiRoles.Section.ordinal(), "AtSpi" + AtSpiRoles.Section.toString(), AtSpiWidget, Roles.Control),
                    AtSpiRedundantObject = from(AtSpiRoles.RedundantObject.ordinal(), "AtSpi" + AtSpiRoles.RedundantObject.toString(), AtSpiWidget, Roles.Control),
                    AtSpiForm = from(AtSpiRoles.Form.ordinal(), "AtSpi" + AtSpiRoles.Form.toString(), AtSpiWidget, Roles.Control),
                    AtSpiLink = from(AtSpiRoles.Link.ordinal(), "AtSpi" + AtSpiRoles.Link.toString(), AtSpiWidget, Roles.Text),
                    AtSpiInputMethodWindow = from(AtSpiRoles.InputMethodWindow.ordinal(), "AtSpi" + AtSpiRoles.InputMethodWindow.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTableRow = from(AtSpiRoles.TableRow.ordinal(), "AtSpi" + AtSpiRoles.TableRow.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTreeItem = from(AtSpiRoles.TreeItem.ordinal(), "AtSpi" + AtSpiRoles.TreeItem.toString(), AtSpiWidget, Roles.Item),
                    AtSpiDocumentSpreadsheet = from(AtSpiRoles.DocumentSpreadsheet.ordinal(), "AtSpi" + AtSpiRoles.DocumentSpreadsheet.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDocumentPresentation = from(AtSpiRoles.DocumentPresentation.ordinal(), "AtSpi" + AtSpiRoles.DocumentPresentation.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDocumentText = from(AtSpiRoles.DocumentText.ordinal(), "AtSpi" + AtSpiRoles.DocumentText.toString(), AtSpiWidget, Roles.Text),
                    AtSpiDocumentWeb = from(AtSpiRoles.DocumentWeb.ordinal(), "AtSpi" + AtSpiRoles.DocumentWeb.toString(), AtSpiWidget, Roles.Text),
                    AtSpiDocumentEmail = from(AtSpiRoles.DocumentEmail.ordinal(), "AtSpi" + AtSpiRoles.DocumentEmail.toString(), AtSpiWidget, Roles.Text),
                    AtSpiComment = from(AtSpiRoles.Comment.ordinal(), "AtSpi" + AtSpiRoles.Comment.toString(), AtSpiWidget, Roles.Text),
                    AtSpiListBox = from(AtSpiRoles.ListBox.ordinal(), "AtSpi" + AtSpiRoles.ListBox.toString(), AtSpiWidget, Roles.ItemContainer),
                    AtSpiGrouping = from(AtSpiRoles.Grouping.ordinal(), "AtSpi" + AtSpiRoles.Grouping.toString(), AtSpiWidget, Roles.Control),
                    AtSpiImageMap = from(AtSpiRoles.ImageMap.ordinal(), "AtSpi" + AtSpiRoles.ImageMap.toString(), AtSpiWidget, Roles.Control),
                    AtSpiNotification = from(AtSpiRoles.Notification.ordinal(), "AtSpi" + AtSpiRoles.Notification.toString(), AtSpiWidget, Roles.Control),
                    AtSpiInfoBar = from(AtSpiRoles.InfoBar.ordinal(), "AtSpi" + AtSpiRoles.InfoBar.toString(), AtSpiWidget, Roles.Control),
                    AtSpiLevelBar = from(AtSpiRoles.LevelBar.ordinal(), "AtSpi" + AtSpiRoles.LevelBar.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTitleBar = from(AtSpiRoles.TitleBar.ordinal(), "AtSpi" + AtSpiRoles.TitleBar.toString(), AtSpiWidget, Roles.Text),
                    AtSpiBlockQuote = from(AtSpiRoles.BlockQuote.ordinal(), "AtSpi" + AtSpiRoles.BlockQuote.toString(), AtSpiWidget, Roles.Control),
                    AtSpiAudio = from(AtSpiRoles.Audio.ordinal(), "AtSpi" + AtSpiRoles.Audio.toString(), AtSpiWidget, Roles.Control),
                    AtSpiVideo = from(AtSpiRoles.Video.ordinal(), "AtSpi" + AtSpiRoles.Video.toString(), AtSpiWidget, Roles.Control),
                    AtSpiDefinition = from(AtSpiRoles.Definition.ordinal(), "AtSpi" + AtSpiRoles.Definition.toString(), AtSpiWidget, Roles.Text),
                    AtSpiArticle = from(AtSpiRoles.Article.ordinal(), "AtSpi" + AtSpiRoles.Article.toString(), AtSpiWidget, Roles.Control),
                    AtSpiLandmark = from(AtSpiRoles.Landmark.ordinal(), "AtSpi" + AtSpiRoles.Landmark.toString(), AtSpiWidget, Roles.Control),
                    AtSpiLog = from(AtSpiRoles.Log.ordinal(), "AtSpi" + AtSpiRoles.Log.toString(), AtSpiWidget, Roles.Text),
                    AtSpiMarquee = from(AtSpiRoles.Marquee.ordinal(), "AtSpi" + AtSpiRoles.Marquee.toString(), AtSpiWidget, Roles.Control),
                    AtSpiMath = from(AtSpiRoles.Math.ordinal(), "AtSpi" + AtSpiRoles.Math.toString(), AtSpiWidget, Roles.Text),
                    AtSpiRating = from(AtSpiRoles.Rating.ordinal(), "AtSpi" + AtSpiRoles.Rating.toString(), AtSpiWidget, Roles.Control),
                    AtSpiTimer = from(AtSpiRoles.Timer.ordinal(), "AtSpi" + AtSpiRoles.Timer.toString(), AtSpiWidget, Roles.Control),
                    AtSpiStatic = from(AtSpiRoles.Static.ordinal(), "AtSpi" + AtSpiRoles.Static.toString(), AtSpiWidget, Roles.Control),
                    AtSpiMathFraction = from(AtSpiRoles.MathFraction.ordinal(), "AtSpi" + AtSpiRoles.MathFraction.toString(), AtSpiWidget, Roles.Control),
                    AtSpiMathRoot = from(AtSpiRoles.MathRoot.ordinal(), "AtSpi" + AtSpiRoles.MathRoot.toString(), AtSpiWidget, Roles.Control),
                    AtSpiSubscript = from(AtSpiRoles.Subscript.ordinal(), "AtSpi" + AtSpiRoles.Subscript.toString(), AtSpiWidget, Roles.Control),
                    AtSpiSuperscript = from(AtSpiRoles.Superscript.ordinal(), "AtSpi" + AtSpiRoles.Superscript.toString(), AtSpiWidget, Roles.Control),
                    AtSpiLastDefined = from(AtSpiRoles.LastDefined.ordinal(), "AtSpi" + AtSpiRoles.LastDefined.toString(), Roles.Invalid);


    //endregion


    //region Constructors


    /**
     * Default empty constructor.
     */
    private AtSpiRolesWrapper() {}


    //endregion


    //region Helper functionality


    // The Role... means Role[] - you can define as many Roles as you like when calling this method and
    // the array will be created automatically.
    /**
     * Creates, adds to the internal type id map and returns a native role wrapper.
     * @param id Id of the native role (AtSpiRoles enum value).
     * @param name Friendly name of the native role.
     * @param inheritFrom The parent(s) of the role.
     * @return The wrapper for the native role.
     */
    private static Role from(long id, String name, Role... inheritFrom){
        Role ret = Role.from(name, inheritFrom);
        typeIdToRole.put(id, ret);
        return ret;
    }


    /**
     * Find a role belonging to a type id.
     * @param typeId The type id of an AtSpiRole.
     * @return The Role wrapping an AtSpiRole.
     */
    public static Role fromTypeId(long typeId){
        Role ret = typeIdToRole.get(typeId);
        return (ret == null) ? AtSpiUnknown : ret;
    }


    //endregion


}