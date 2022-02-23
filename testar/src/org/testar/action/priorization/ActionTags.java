/***************************************************************************************************
 *
 * Copyright (c) 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 Open Universiteit - www.ou.nl
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

package org.testar.action.priorization;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.Roles;
import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;
import org.fruit.alayer.windows.Windows;

public class ActionTags extends TagsBase  {

	public enum ActionGroupType {
		UIAWidget, UIAAppBar, UIAButton, UIACalendar, UIACheckBox, UIAComboBox,
		UIACustomControl, UIADataGrid, UIADataItem, UIADocument, UIAEdit,
		UIAGroup, UIAHeader, UIAHeaderItem, UIAHyperlink, UIAImage, UIAList,
		UIAListItem, UIAMenuBar, UIAMenu, UIAMenuItem, UIAPane, UIAProgressBar,
		UIARadioButton, UIAScrollBar, UIASemanticZoom, UIASeparator, UIASlider,
		UIASpinner, UIASplitButton, UIAStatusBar, UIATabControl, UIATabItem,
		UIATable, UIAText, UIAThumb, UIATitleBar, UIAToolBar, UIAToolTip,
		UIATree, UIATreeItem, UIAWindow;
	}

	private ActionTags() {}

	public static final Tag<Integer> SimilarityValue = from("SimilarityValue", Integer.class);

	public static final Tag<Double> QLearning = from("QLearning", Double.class);

	public static final Tag<Integer> ZIndex = from("ZIndex", Integer.class);

	public static final Tag<ActionGroupType> ActionGroup = from("ActionGroup", ActionGroupType.class);
	// <-- **

	private static Set<Tag<Integer>> actionTags = new HashSet<Tag<Integer>>() {
		{
			add(SimilarityValue);
			add(ZIndex);
		}
	};

	// ** -->
	private static Set<Tag<Double>> doubleActionTags = new HashSet<Tag<Double>>() {
		{
			add(QLearning);
		}
	};

	private static Set<Tag<ActionGroupType>> StringActionTags = new HashSet<Tag<ActionGroupType>>() {
		{
			add(ActionGroup);
		}
	};
	// <-- **

	public static Set<Tag<Integer>> getActionTags() {
		return actionTags;
	}

}
