/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2022 Open Universiteit - www.ou.nl
 * Copyright (c) 2020 - 2022 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.protocols;

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.windows.UIARoles;
import org.testar.stub.StateStub;
import org.testar.stub.WidgetStub;

import static org.junit.Assert.*;

public class GetWidgetIsClickable {

	private static GenericUtilsProtocol protocol;

	@BeforeClass
	public static void setup() {
		// To avoid issues with java awt robot, we only execute this unit tests in windows environments.
		Assume.assumeTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
		protocol = new GenericUtilsProtocol();
	}

	@Test
	public void isClickableMenu() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAMenu);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableMenuItem() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAMenuItem);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableButton() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAButton);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableCheckbox() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIACheckBox);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableRadioButton() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIARadioButton);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableComboBox() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAComboBox);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableList() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAList);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableListItem() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAListItem);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableTabItem() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIATabItem);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableHyperLink() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAHyperlink);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableDataItem() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIADataItem);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableTree() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIATree);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableTreeItem() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIATreeItem);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableSlider() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIASlider);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableSpinner() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIASpinner);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableScrollBar() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIAScrollBar);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableSplitButton() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIASplitButton);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isClickableCustomControl() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);
		widget.set(Tags.Role, UIARoles.UIACustomControl);

		// Find the widget by the Role value
		assertTrue(protocol.isClickable(widget));
	}

	@Test
	public void isNotClickable() {
		StateStub state = new StateStub();
		WidgetStub widget = new WidgetStub();
		state.addChild(widget);
		widget.setParent(state);

		widget.set(Tags.Role, UIARoles.UIAAppBar);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIACalendar);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIADataGrid);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIADocument);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAEdit);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAGroup);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAHeader);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAHeaderItem);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAImage);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAMenuBar);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAPane);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAProgressBar);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIASemanticZoom);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIASeparator);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAStatusBar);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIATabControl);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIATable);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAText);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAThumb);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIATitleBar);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAToolBar);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAToolTip);
		assertFalse(protocol.isClickable(widget));

		widget.set(Tags.Role, UIARoles.UIAWindow);
		assertFalse(protocol.isClickable(widget));
	}
}
