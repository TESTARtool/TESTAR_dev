package org.testar.monkey.alayer.android;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.android.enums.AndroidRoles;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TestAndroidRoles {

    @Parameterized.Parameters(name = "{index}: {0} -> {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"android.widget.Button", AndroidRoles.AndroidButton},
            {"android.widget.ImageButton", AndroidRoles.AndroidImageButton},
            {"android.widget.RadioButton", AndroidRoles.AndroidRadioButton},
            {"android.widget.QuickContactBadge", AndroidRoles.AndroidQuickContactBadge},
            {"android.widget.Toast", AndroidRoles.AndroidToast},
            {"android.widget.CheckBox", AndroidRoles.AndroidCheckBox},
            {"android.widget.DatePicker", AndroidRoles.AndroidDatePicker},
            {"android.widget.Space", AndroidRoles.AndroidSpace},
            {"android.widget.TwoLineListItem", AndroidRoles.AndroidTwoLineListItem},
            {"android.widget.GridLayout", AndroidRoles.AndroidGridLayout},
            {"android.widget.GridView", AndroidRoles.AndroidGridView},
            {"android.widget.ImageView", AndroidRoles.AndroidImageView},
            {"android.widget.RatingBar", AndroidRoles.AndroidRatingBar},
            {"android.widget.ProgressBar", AndroidRoles.AndroidProgressBar},
            {"android.widget.ListView", AndroidRoles.AndroidListView},
            {"android.widget.ExpandableListView", AndroidRoles.AndroidExpandableListView},
            {"android.widget.Gallery", AndroidRoles.AndroidGallery},
            {"android.widget.ActionMenuView", AndroidRoles.AndroidActionMenuView},
            {"android.widget.PopupMenu", AndroidRoles.AndroidPopupMenu},
            {"android.widget.ListPopupWindow", AndroidRoles.AndroidListPopupWindow},
            {"android.widget.PopupWindow", AndroidRoles.AndroidPopupWindow},
            {"android.widget.SlidingDrawer", AndroidRoles.AndroidSlidingDrawer},
            {"android.widget.Magnifier", AndroidRoles.AndroidMagnifier},
            {"android.widget.NumberPicker", AndroidRoles.AndroidNumberPicker},
            {"android.widget.TimePicker", AndroidRoles.AndroidTimePicker},
            {"android.widget.CalendarView", AndroidRoles.AndroidCalendarView},
            {"android.widget.RadioGroup", AndroidRoles.AndroidRadioGroup},
            {"android.widget.TableRow", AndroidRoles.AndroidTableRow},
            {"android.widget.ScrollView", AndroidRoles.AndroidScrollView},
            {"android.widget.HorizontalScrollView", AndroidRoles.AndroidHorizontalScrollView},
            {"android.widget.SearchView", AndroidRoles.AndroidSearchView},
            {"android.widget.SeekBar", AndroidRoles.AndroidSeekBar},
            {"android.widget.Spinner", AndroidRoles.AndroidSpinner},
            {"android.widget.Switch", AndroidRoles.AndroidSwitch},
            {"android.widget.TableLayout", AndroidRoles.AndroidTableLayout},
            {"android.widget.TextView", AndroidRoles.AndroidTextView},
            {"android.widget.Chronometer", AndroidRoles.AndroidChronometer},
            {"android.widget.TextClock", AndroidRoles.AndroidTextClock},
            {"android.widget.EditText", AndroidRoles.AndroidEditText},
            {"android.widget.AutoCompleteTextView", AndroidRoles.AndroidAutoCompleteTextView},
            {"android.widget.MultiAutoCompleteTextView", AndroidRoles.AndroidMultiAutoCompleteTextView},
            {"android.widget.CheckedTextView", AndroidRoles.AndroidCheckedTextView},
            {"android.widget.ToggleButton", AndroidRoles.AndroidToggleButton},
            {"android.widget.Toolbar", AndroidRoles.AndroidToolbar},
            {"android.widget.VideoView", AndroidRoles.AndroidVideoView},
            {"android.widget.FrameLayout", AndroidRoles.AndroidFrameLayout},
            {"android.widget.LinearLayout", AndroidRoles.AndroidLinearLayout},
            {"android.widget.RelativeLayout", AndroidRoles.AndroidRelativeLayout},
            {"android.view.View", AndroidRoles.AndroidView},
            {"android.view.ViewGroup", AndroidRoles.AndroidViewGroup},
            {"android.widget.MediaController", AndroidRoles.AndroidMediaController},
            {"android.widget.StackView", AndroidRoles.AndroidStackView},
            {"some.unknown.Class", AndroidRoles.AndroidUnknown},
        });
    }

    @Parameterized.Parameter(0)
    public String className;

    @Parameterized.Parameter(1)
    public Role expectedRole;

    @Test
    public void testRoleMapping() {
        AndroidState androidState = new AndroidState(null);
        AndroidElement androidElement = new AndroidElement();
        androidElement.className = className;

        AndroidWidget androidWidget = new AndroidWidget(androidState, androidState, androidElement);

        assertEquals("Role for " + className + " should match", expectedRole, androidWidget.get(Tags.Role));
    }

}