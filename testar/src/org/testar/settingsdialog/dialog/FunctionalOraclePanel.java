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

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.io.FilenameUtils;
import org.testar.monkey.Main;
import org.testar.monkey.Settings;
import org.testar.settingsdialog.OracleEditor;
import org.testar.settingsdialog.SettingsDialog;
import org.testar.settingsdialog.SettingsPanel;

public class FunctionalOraclePanel extends SettingsPanel {

	private static final long serialVersionUID = -6397375336958507515L;

	private SettingsDialog settingsDialog;
	
	private GenericTableModel genericVerdictsModel = new GenericTableModel(new String[]{"Generic Verdict Name", "Enable Generic Verdict", "Edit Oracle", "Severity"});
	private DesktopTableModel desktopVerdictsModel = new DesktopTableModel(new String[]{"Desktop Verdict Name", "Enable Desktop Verdict", "Edit Oracle", "Severity"});
	private WebTableModel webVerdictsModel = new WebTableModel(new String[]{"Web Verdict Name", "Enable Web Verdict", "Edit Oracle", "Severity"});

	private List<OracleEditor> genericList = new ArrayList<>();
	private List<OracleEditor> desktopList = new ArrayList<>();
	private List<OracleEditor> webList = new ArrayList<>();

	public FunctionalOraclePanel(SettingsDialog settingsDialog) {
		this.settingsDialog = settingsDialog;
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
		loadExistingVerdicts("generic", genericList);
		loadExistingVerdicts("desktop", desktopList);
		loadExistingVerdicts("web", webList);

		// Add all oracles based on the existing list files
		for(OracleEditor oracle : genericList) {
			genericVerdictsModel.addRow(new Object[]{FilenameUtils.getName(oracle.getOracleFileName()), true, FilenameUtils.getName(oracle.getOracleFileName())});
		}
		for(OracleEditor oracle : desktopList) {
			desktopVerdictsModel.addRow(new Object[]{FilenameUtils.getName(oracle.getOracleFileName()), true, FilenameUtils.getName(oracle.getOracleFileName())});
		}
		for(OracleEditor oracle : webList) {
			webVerdictsModel.addRow(new Object[]{FilenameUtils.getName(oracle.getOracleFileName()), true, FilenameUtils.getName(oracle.getOracleFileName())});
		}

		// Create a the tables on which we are going to dynamically add the existing oracles		
		createVerdictTable(genericVerdictsModel, new Rectangle(10, 35, 600, 100));
		createVerdictTable(desktopVerdictsModel, new Rectangle(10, 145, 600, 100));
		createVerdictTable(webVerdictsModel, new Rectangle(10, 255, 600, 100));
	}

	private void loadExistingVerdicts(String verdictFolder, List<OracleEditor> list) {
		File folder = new File(Main.functionalOraclesDir + File.separator + verdictFolder);
		if(folder.exists()) {
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".java")) {
					File file = new File(Main.functionalOraclesDir + File.separator + verdictFolder + File.separator + listOfFiles[i].getName());
					String absolutePath = file.getAbsolutePath();
					OracleEditor oracleEditor = new OracleEditor(settingsDialog, Main.functionalOraclesDir, absolutePath.replace(".java", ""));
					list.add(oracleEditor);
				}
			}
		}
	}

	private void createVerdictTable(DefaultTableModel tableModel, Rectangle bounds) {
		JTable verdicstTable = new JTable(tableModel);
		verdicstTable.setRowHeight(20);
		verdicstTable.setDefaultRenderer(TableButtonRenderer.class, new TableButtonRenderer());
		verdicstTable.setDefaultEditor(TableButtonRenderer.class, new TableButtonEditor());

		List<OracleEditor> allVerdicts = new ArrayList<>();
		allVerdicts.addAll(genericList);
		allVerdicts.addAll(desktopList);
		allVerdicts.addAll(webList);

		// Iterate trough the Oracles cells to associate the different OracleEditors
		for(int i = 0; i < verdicstTable.getRowCount(); i++) {
			String oracleName = (String) verdicstTable.getModel().getValueAt(i, 0);
			TableButtonEditor buttonOracle = (TableButtonEditor) verdicstTable.getCellEditor(i, 2);

			OracleEditor foundOracleEditor = allVerdicts.stream()
					.filter(p -> p.getOracleFileName().contains(oracleName))
					.findFirst()
					.orElse(null);

			if(foundOracleEditor != null) {
				buttonOracle.setOracleEditor(foundOracleEditor);
				buttonOracle.getButton().addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						// We need to check which editor button was selected to open only one OracleEditor
						String clickedOracle = ((TableButtonRenderer)e.getSource()).value.toString();
						if(foundOracleEditor.getOracleFileName().contains(clickedOracle)) {
							foundOracleEditor.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
							foundOracleEditor.setVisible(true);
						}
					}
				});
			}
		}

		JScrollPane verdictsScrollPanel = new JScrollPane();
		verdictsScrollPanel.setViewportView(verdicstTable);
		verdictsScrollPanel.setBounds(bounds);
		verdictsScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		verdictsScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(verdictsScrollPanel);
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

	}

}

class GenericTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 5044738094695892624L;

	int labelColumn = 0;
	int checkBoxEnableVerdictColumn = 1;
	int buttonEditOracleColumn = 2;
	int dropdownSeverityColumn = 3;

	public GenericTableModel(Object[] columnNames) {
		super(columnNames, 0);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if(columnIndex == checkBoxEnableVerdictColumn) return Boolean.class;
		if(columnIndex == buttonEditOracleColumn) return TableButtonRenderer.class;
		if(columnIndex == dropdownSeverityColumn) return JComboBox.class;
		return String.class;
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return column >= 1 && column <= 3;
	}

	@Override
	public void setValueAt(Object value, int row, int column) {
		if (value instanceof Boolean && column == checkBoxEnableVerdictColumn) {
			// Update the visual checkbox element
			Vector<Object> rowData = (Vector<Object>) getDataVector().get(row);
			rowData.set(column, (boolean)value);
			fireTableCellUpdated(row, column);
			// Update the functional verdict enabling/disabling list
			//updateVerdict(value, row);
		}
	}

	//	protected void updateVerdict(Object value, int row) {
	//		String genericVerdictName = (String) super.getValueAt(row, 0);
	//		if((boolean)value) GenericVerdict.enabledVerdicts.add(genericVerdictName);
	//		else GenericVerdict.enabledVerdicts.remove(genericVerdictName);
	//	}
}

class DesktopTableModel extends GenericTableModel {

	private static final long serialVersionUID = 870552870418011092L;

	public DesktopTableModel(Object[] columnNames) {
		super(columnNames);
	}

	//	@Override
	//	protected void updateVerdict(Object value, int row) {
	//		String webVerdictName = (String) super.getValueAt(row, 0);
	//		if((boolean)value) WebVerdict.enabledWebVerdicts.add(webVerdictName);
	//		else WebVerdict.enabledWebVerdicts.remove(webVerdictName);
	//	}
}

class WebTableModel extends GenericTableModel {

	private static final long serialVersionUID = 870552870418011092L;

	public WebTableModel(Object[] columnNames) {
		super(columnNames);
	}

	//	@Override
	//	protected void updateVerdict(Object value, int row) {
	//		String webVerdictName = (String) super.getValueAt(row, 0);
	//		if((boolean)value) WebVerdict.enabledWebVerdicts.add(webVerdictName);
	//		else WebVerdict.enabledWebVerdicts.remove(webVerdictName);
	//	}
}

class TableButtonRenderer extends JButton implements TableCellRenderer {

	private static final long serialVersionUID = 5904750324984028617L;

	OracleEditor oracleEditor;
	Object value;

	public OracleEditor getOracleEditor() { return oracleEditor; }

	public TableButtonRenderer() {
		setOpaque(true);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setBackground(table.getSelectionBackground());
		} else {
			setBackground(table.getBackground());
		}
		setText((value == null) ? "" : value.toString());
		return this;
	}
}

class TableButtonEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = -4823584662620561504L;

	TableButtonRenderer button;
	Object value;
	String label;
	OracleEditor oracleEditor;

	public TableButtonRenderer getButton() { return button; }

	public void setOracleEditor(OracleEditor oracleEditor) { this.button.oracleEditor = oracleEditor; }

	public TableButtonEditor() {
		super();
		button = new TableButtonRenderer();
		button.setOpaque(true);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setBackground(table.getBackground());
		}
		this.value = value;
		button.value = value;
		label = (value == null) ? "" : value.toString();
		button.setText(label);
		return button;
	}

	@Override
	public Object getCellEditorValue() {
		return value;
	}
}
