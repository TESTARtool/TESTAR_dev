/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2025-2026 Open Universiteit - www.ou.nl
 * Copyright (c) 2025-2026 Universitat Politecnica de Valencia - www.upv.es
 */

package org.testar.dialog;

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

import org.testar.oracle.OracleSelection;

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
