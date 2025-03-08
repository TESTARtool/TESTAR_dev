/***************************************************************************************************
 *
 * Copyright (c) 2025 Open Universiteit - www.ou.nl
 * Copyright (c) 2025 Universitat Politecnica de Valencia - www.upv.es
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

package org.testar.settings.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.testar.oracles.OracleSelection;

public class ExtendedOraclesDialog extends JDialog {
	private static final long serialVersionUID = 8181334482682809135L;

	private JTable table;
	private DefaultTableModel tableModel;

	private String savedSelectedExtendedOracles;

	Window window = SwingUtilities.getWindowAncestor(this);

	public ExtendedOraclesDialog(String selectedExtendedOracles) {
		this.savedSelectedExtendedOracles = selectedExtendedOracles;

		setSize(500, 400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		init(selectedExtendedOracles);

		setVisible(true);
	}

	private void init(String selectedExtendedOracles) {
		JLabel label = new JLabel("Enable or disable the extended oracles");
		label.setFont(new Font("Arial", Font.BOLD, 14));
		label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(label, BorderLayout.NORTH);

		// Load available oracles dynamically and sort alphabetically
		List<String> availableOracles = OracleSelection.getAvailableOracles();
		Collections.sort(availableOracles);

		// Load activated oracles from settings
		List<String> activatedOracles = List.of(selectedExtendedOracles.split(","));

		// Table Model with Enabled Extended Oracles
		String[] columnNames = {"Extended Oracle Name", "Enabled"};
		Object[][] data = new Object[availableOracles.size()][2];

		for (int i = 0; i < availableOracles.size(); i++) {
			String oracle = availableOracles.get(i);
			data[i][0] = oracle; // Oracle name
			data[i][1] = activatedOracles.contains(oracle); // Checkbox checked if activated
		}

		tableModel = new DefaultTableModel(data, columnNames) {
			private static final long serialVersionUID = 2200448991938484190L;

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return (columnIndex == 1) ? Boolean.class : String.class; // Checkbox in column 1
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1; // Only checkbox column is editable
			}
		};

		table = new JTable(tableModel);
		table.setRowHeight(30);

		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(0).setPreferredWidth(350);
		columnModel.getColumn(1).setPreferredWidth(80);

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);

		JButton saveButton = new JButton("Save and Close");
		saveButton.addActionListener(e -> saveExtendedOracles());

		JPanel bottomPanel = new JPanel();
		bottomPanel.add(saveButton);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public String getSavedExtendedOracles() {
		return savedSelectedExtendedOracles;
	}

	private void saveExtendedOracles() {
		StringBuilder selectedOracles = new StringBuilder();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			boolean isSelected = (boolean) tableModel.getValueAt(i, 1);
			String oracleName = (String) tableModel.getValueAt(i, 0);
			if (isSelected) {
				if (selectedOracles.length() > 0) {
					selectedOracles.append(",");
				}
				selectedOracles.append(oracleName);
			}
		}
		savedSelectedExtendedOracles = selectedOracles.toString();
		dispatchEvent(new WindowEvent(window ,WindowEvent.WINDOW_CLOSING));
		dispose();
	}
}
