/**
 * Copyright (c) 2023 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2023 Open Universiteit - www.ou.nl
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
 *
 */

package org.testar.settingsdialog.dialog;

import java.util.Arrays;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import org.testar.monkey.Settings;
import org.testar.settingsdialog.SettingsPanel;
import org.testar.verdicts.WebVerdict;

public class FunctionalVerdictPanel extends SettingsPanel {

	private static final long serialVersionUID = -6397375336958507515L;

	public FunctionalVerdictPanel() {
		setLayout(null);

		/**
		 * Add the textual label
		 */

		JLabel functionalVerdictLabel = new JLabel("Enable or disable the functional verdicts");
		functionalVerdictLabel.setBounds(10, 5, 600, 27);
		add(functionalVerdictLabel);

		/**
		 * Load the dynamic table elements
		 */

		System.out.println("FunctionalVerdictPanel Initial WebVerdict.enabledVerdicts: " + Arrays.toString(WebVerdict.enabledVerdicts.toArray()));

		// Prepare the table model
		MyTableModel webVerdictsModel = new MyTableModel();
		// Add the interactive CheckBox Rows
		for(String webVerdictName : WebVerdict.enabledVerdicts) {
			webVerdictsModel.addRow(new Object[]{webVerdictName, true});
		}

		// Create a panel on which we are going to dynamically add the existing verdict methods
		JScrollPane webVerdictsScrollPanel = new JScrollPane();
		webVerdictsScrollPanel.setViewportView(new JTable(webVerdictsModel));
		webVerdictsScrollPanel.setBounds(10, 35, 600, 300);
		webVerdictsScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		webVerdictsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(webVerdictsScrollPanel);

	}

	/**
	 * Populate Oracle Fields from Settings structure.
	 * @param settings The settings to load.
	 */
	@Override
	public void populateFrom(final Settings settings) {

	}

	/**
	 * Retrieve information from the Oracle GUI.
	 * @param settings reference to the object where the settings will be stored.
	 */
	@Override
	public void extractInformation(final Settings settings) {
		System.out.println("FunctionalVerdictPanel Final Customized WebVerdict.enabledVerdicts: " + Arrays.toString(WebVerdict.enabledVerdicts.toArray()));
	}

}

class MyTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 5044738094695892624L;

	int checkBoxColumn = 1;

	public MyTableModel() {
		super(new String[]{"Web Verdict Name", "Enable Web Verdict"}, 0);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == checkBoxColumn) return Boolean.class;
		return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column == checkBoxColumn;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		if (value instanceof Boolean && column == checkBoxColumn) {
			// Update the visual checkbox element
			Vector<Object> rowData = (Vector<Object>) getDataVector().get(row);
			rowData.set(column, (boolean)value);
			fireTableCellUpdated(row, column);
			// Update the web verdict enabling/disabling list
			String webVerdictName = (String) super.getValueAt(row, 0);
			if((boolean)value) WebVerdict.enabledVerdicts.add(webVerdictName);
			else WebVerdict.enabledVerdicts.remove(webVerdictName);
		}
	}

}