/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************************************/

package org.testar.monkey.alayer.android.enums;

import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;

import java.util.*;

public class AndroidRoles {
	private AndroidRoles() {}

	private static final Map<String, Role> tagToRole = new HashMap<>();

	public static final Role
	AndroidWidget = from("android.widget", "AndroidWidget", Roles.Widget),
	AndroidButton = from("android.widget.Button", "AndroidButton", AndroidWidget, Roles.Control),
	AndroidImageButton = from("android.widget.ImageButton", "AndroidImageButton", AndroidWidget, Roles.Control),
	AndroidRadioButton = from("android.widget.RadioButton", "AndroidRadioButton", AndroidWidget, Roles.Control),
	AndroidQuickContactBadge = from("android.widget.QuickContactBadge", "AndroidQuickContactBadge", AndroidWidget, Roles.Control),
	AndroidToast = from("android.widget.Toast", "AndroidToast", AndroidWidget, Roles.Control),
	AndroidCheckBox = from("android.widget.CheckBox", "AndroidCheckBox", AndroidWidget, Roles.Control),
	AndroidDatePicker = from("android.widget.DatePicker", "AndroidDatePicker", AndroidWidget, Roles.Control),
	AndroidSpace = from("android.widget.Space", "AndroidSpace", AndroidWidget, Roles.Control),
	AndroidTwoLineListItem = from("android.widget.TwoLineListItem", "AndroidTwoLineListItem", AndroidWidget, Roles.Control),
	AndroidGridLayout = from("android.widget.GridLayout", "AndroidGridLayout", AndroidWidget, Roles.Control),
	AndroidGridView = from("android.widget.GridView", "AndroidGridView", AndroidWidget, Roles.Control),
	AndroidImageView = from("android.widget.ImageView", "AndroidImageView", AndroidWidget, Roles.Control),
	AndroidRatingBar = from("android.widget.RatingBar", "AndroidRatingBar", AndroidWidget, Roles.Control),
	AndroidProgressBar = from("android.widget.ProgressBar", "AndroidProgressBar", AndroidWidget, Roles.Control),
	AndroidListView = from("android.widget.ListView", "AndroidListView", AndroidWidget, Roles.Control),
	AndroidExpandableListView = from("android.widget.ExpandableListView", "AndroidExpandableListView", AndroidWidget, Roles.Control),
	AndroidGallery = from("android.widget.Gallery", "AndroidGallery", AndroidWidget, Roles.Control),
	AndroidActionMenuView = from("android.widget.ActionMenuView", "AndroidActionMenuView", AndroidWidget, Roles.Control),
	AndroidPopupMenu = from("android.widget.PopupMenu", "AndroidPopupMenu", AndroidWidget, Roles.Control),
	AndroidListPopupWindow = from("android.widget.ListPopupWindow", "AndroidListPopupWindow", AndroidWidget, Roles.Control),
	AndroidPopupWindow = from("android.widget.PopupWindow", "AndroidPopupWindow", AndroidWidget, Roles.Control),
	AndroidSlidingDrawer = from("android.widget.SlidingDrawer", "AndroidSlidingDrawer", AndroidWidget, Roles.Control),
	AndroidMagnifier = from("android.widget.Magnifier", "AndroidMagnifier", AndroidWidget, Roles.Control),
	AndroidNumberPicker = from("android.widget.NumberPicker", "AndroidNumberPicker", AndroidWidget, Roles.Control),
	AndroidTimePicker = from("android.widget.TimePicker", "AndroidTimePicker", AndroidWidget, Roles.Control),
	AndroidCalendarView = from("android.widget.CalendarView", "AndroidCalendarView", AndroidWidget, Roles.Control),
	AndroidRadioGroup = from("android.widget.RadioGroup", "AndroidRadioGroup", AndroidWidget, Roles.Control),
	AndroidTableRow = from("android.widget.TableRow", "AndroidTableRow", AndroidWidget, Roles.Control),
	AndroidScrollView = from("android.widget.ScrollView", "AndroidScrollView", AndroidWidget, Roles.Control),
	AndroidHorizontalScrollView = from("android.widget.HorizontalScrollView", "AndroidHorizontalScrollView", AndroidWidget, Roles.Control),
	AndroidSearchView = from("android.widget.SearchView", "AndroidSearchView", AndroidWidget, Roles.Control),
	AndroidSeekBar = from("android.widget.SeekBar", "AndroidSeekBar", AndroidWidget, Roles.Control),
	AndroidSpinner = from("android.widget.Spinner", "AndroidSpinner", AndroidWidget, Roles.Control),
	AndroidSwitch = from("android.widget.Switch", "AndroidSwitch", AndroidWidget, Roles.Control),
	AndroidTableLayout = from("android.widget.TableLayout", "AndroidTableLayout", AndroidWidget, Roles.Control),
	AndroidTextView = from("android.widget.TextView", "AndroidTextView", AndroidWidget, Roles.Control),
	AndroidChronometer = from("android.widget.Chronometer", "AndroidChronometer", AndroidWidget, Roles.Control),
	AndroidTextClock = from("android.widget.TextClock", "AndroidTextClock", AndroidWidget, Roles.Control),
	AndroidEditText = from("android.widget.EditText", "AndroidEditText", AndroidWidget, Roles.Control),
	AndroidAutoCompleteTextView = from("android.widget.AutoCompleteTextView", "AndroidAutoCompleteTextView", AndroidWidget, Roles.Control),
	AndroidMultiAutoCompleteTextView = from("android.widget.MultiAutoCompleteTextView", "AndroidMultiAutoCompleteTextView", AndroidWidget, Roles.Control),
	AndroidCheckedTextView = from("android.widget.CheckedTextView", "AndroidCheckedTextView", AndroidWidget, Roles.Control),
	AndroidToggleButton = from("android.widget.ToggleButton", "AndroidToggleButton", AndroidWidget, Roles.Control),
	AndroidToolbar = from("android.widget.Toolbar", "AndroidToolbar", AndroidWidget, Roles.Control),
	AndroidVideoView = from("android.widget.VideoView", "AndroidVideoView", AndroidWidget, Roles.Control),
	AndroidFrameLayout = from("android.widget.FrameLayout", "AndroidFrameLayout", AndroidWidget, Roles.Control),
	AndroidLinearLayout = from("android.widget.LinearLayout", "AndroidLinearLayout", AndroidWidget, Roles.Control),
	AndroidRelativeLayout = from("android.widget.RelativeLayout", "AndroidRelativeLayout", AndroidWidget, Roles.Control),
	AndroidView = from("android.view.View", "AndroidView", AndroidWidget, Roles.Control),
	AndroidViewGroup = from("android.view.ViewGroup", "AndroidViewGroup", AndroidWidget, Roles.Control),
	AndroidMediaController = from("android.widget.MediaController", "AndroidMediaController", AndroidWidget, Roles.Control),
	AndroidStackView = from("android.widget.StackView", "AndroidStackView", AndroidWidget, Roles.Control),
	AndroidUnknown = from("-1", "AndroidUnknown",Roles.Control);

	private static Role from(String tag, String name, Role... inheritFrom) {
		Role ret = Role.from(name, inheritFrom);
		tagToRole.put(tag, ret);
		return ret;
	}

	public static Role fromTypeId(String tag) {
		Role ret = tagToRole.get(tag);
		return (ret == null) ? AndroidUnknown : ret;
	}

	public static Collection<Role> rolesSet() {
		return tagToRole.values();
	}

	public static Role[] nativeClickableRoles() {
		return new Role[]{
				AndroidButton,
				AndroidImageButton,
				AndroidRadioButton,
				AndroidQuickContactBadge,
				AndroidToast,
				AndroidCheckBox,
				AndroidDatePicker,
				AndroidSpace,
				AndroidTwoLineListItem,
				AndroidImageView,
				AndroidRatingBar,
				AndroidProgressBar,
				AndroidActionMenuView,
				AndroidPopupMenu,
				AndroidListPopupWindow,
				AndroidPopupWindow,
				AndroidSlidingDrawer,
				AndroidMagnifier,
				AndroidNumberPicker,
				AndroidTimePicker,
				AndroidCalendarView,
				AndroidRadioGroup,
				AndroidSeekBar,
				AndroidSpinner,
				AndroidSwitch,
				AndroidTextView,
				AndroidChronometer,
				AndroidTextClock,
				AndroidCheckedTextView,
				AndroidToggleButton,
				AndroidToolbar,
				AndroidVideoView,
				AndroidFrameLayout,
				AndroidLinearLayout,
				AndroidRelativeLayout,
				AndroidView,
				AndroidViewGroup,
				AndroidMediaController
		};
	}

	public static Role[] nativeTypeableRoles() {
		return new Role[]{
				AndroidEditText,
				AndroidAutoCompleteTextView,
				AndroidMultiAutoCompleteTextView,
				AndroidSearchView
		};
	}

}
