/***************************************************************************************************
 *
 * Copyright (c) 2013 - 2020 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2018 - 2020 Open Universiteit - www.ou.nl
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


package org.fruit.monkey.dialog;

import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.Settings;

import javax.swing.*;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static org.fruit.monkey.dialog.ToolTipTexts.label1TTT;
import static org.fruit.monkey.dialog.ToolTipTexts.label2TTT;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;

public class FilterPanel extends JPanel {

	private static final long serialVersionUID = 1572649050808020748L;

	private JTextArea txtProcessFilter;

	private String allFilterTags[] = {"Title", "Desc", "Text", "ValuePattern",
			"WebId", "WebHref", "WebName", "WebTagName", "WebTitle", "WebAlt", "WebTextContent", "WebSrc"};
	private JComboBox<String> comboBoxAllFilterTags = new JComboBox<>(allFilterTags);
	private String selectedTag = comboBoxAllFilterTags.getSelectedItem().toString();
	
	private JLabel filterByTagLabel = new JLabel(
			"Filter actions by the widget its " + selectedTag + " property (regular expression):");
	
	private Set<JTextArea> allTextAreas = new LinkedHashSet<>();

	// General Filters
	private JTextArea txtActionFilterWidgetTitle;
	private JTextArea txtActionFilterWidgetDesc;
	private JTextArea txtActionFilterWidgetText;
	private JTextArea txtActionFilterWidgetValuePattern;

	// Webdrivers Filters
	private JTextArea txtActionFilterWebId;
	private JTextArea txtActionFilterWebHref;
	private JTextArea txtActionFilterWebName;
	private JTextArea txtActionFilterWebTagName;
	private JTextArea txtActionFilterWebTitle;
	private JTextArea txtActionFilterWebAlt;
	private JTextArea txtActionFilterWebTextContent;
	private JTextArea txtActionFilterWebSrc;

	public FilterPanel() {
		filterByTagLabel.setToolTipText(label1TTT);
		JLabel killProcessByNameLabel = new JLabel(
				"Kill processes by name (regular expression):");
		killProcessByNameLabel.setToolTipText(label2TTT);

		JScrollPane filterByTitlePane = new JScrollPane();
		JScrollPane killProcessByNamePane = new JScrollPane();

		GroupLayout gl_jPanelFilters = new GroupLayout(this);
		this.setLayout(gl_jPanelFilters);
		gl_jPanelFilters.setHorizontalGroup(
				gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(gl_jPanelFilters.createSequentialGroup()
						.addGroup(gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addGroup(gl_jPanelFilters.createSequentialGroup()
										.addGap(24)
										.addComponent(killProcessByNamePane, PREFERRED_SIZE, 572, PREFERRED_SIZE))
								.addGroup(gl_jPanelFilters.createSequentialGroup()
										.addGap(24)
										.addComponent(filterByTitlePane, PREFERRED_SIZE, 572, PREFERRED_SIZE))
								.addGroup(gl_jPanelFilters.createSequentialGroup()
										.addContainerGap()
										.addGroup(gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(killProcessByNameLabel, PREFERRED_SIZE, 357, PREFERRED_SIZE)
												.addComponent(filterByTagLabel, PREFERRED_SIZE, 381, PREFERRED_SIZE))))
						.addContainerGap(24, Short.MAX_VALUE))
				);
		gl_jPanelFilters.setVerticalGroup(
				gl_jPanelFilters.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(gl_jPanelFilters.createSequentialGroup()
						.addContainerGap()
						.addComponent(filterByTagLabel)
						.addGap(18)
						.addComponent(filterByTitlePane, DEFAULT_SIZE, 108, Short.MAX_VALUE)
						.addGap(18)
						.addComponent(killProcessByNameLabel)
						.addGap(18)
						.addComponent(killProcessByNamePane, PREFERRED_SIZE, 121, PREFERRED_SIZE)
						.addGap(22))
				);

		txtProcessFilter = new JTextArea();
		txtProcessFilter.setLineWrap(true);
		killProcessByNamePane.setViewportView(txtProcessFilter);

		generalTextAreasFilters();
		webdriverTextAreasFilters();

		filterByTitlePane.setViewportView(selectedFilter());

		comboBoxAllFilterTags.setBounds(400, 5, 150, 30);
		comboBoxAllFilterTags.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				filterByTitlePane.setViewportView(selectedFilter());
				filterByTagLabel.setText(
						"Filter actions by the widget its " + selectedTag + " property (regular expression):");
			}
		});
		this.add(comboBoxAllFilterTags);
	}

	private void generalTextAreasFilters() {
		txtActionFilterWidgetTitle = new JTextArea();
		txtActionFilterWidgetTitle.setName("Title");
		txtActionFilterWidgetTitle.setLineWrap(true);
		allTextAreas.add(txtActionFilterWidgetTitle);

		txtActionFilterWidgetDesc = new JTextArea();
		txtActionFilterWidgetDesc.setName("Desc");
		txtActionFilterWidgetDesc.setLineWrap(true);
		allTextAreas.add(txtActionFilterWidgetDesc);

		txtActionFilterWidgetText = new JTextArea();
		txtActionFilterWidgetText.setName("Text");
		txtActionFilterWidgetText.setLineWrap(true);
		allTextAreas.add(txtActionFilterWidgetText);

		txtActionFilterWidgetValuePattern = new JTextArea();
		txtActionFilterWidgetValuePattern.setName("ValuePattern");
		txtActionFilterWidgetValuePattern.setLineWrap(true);
		allTextAreas.add(txtActionFilterWidgetValuePattern);
	}
	
	private void webdriverTextAreasFilters() {
		txtActionFilterWebId = new JTextArea();
		txtActionFilterWebId.setName("WebId");
		txtActionFilterWebId.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebId);
		
		txtActionFilterWebHref = new JTextArea();
		txtActionFilterWebHref.setName("WebHref");
		txtActionFilterWebHref.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebHref);
		
		txtActionFilterWebName = new JTextArea();
		txtActionFilterWebName.setName("WebName");
		txtActionFilterWebName.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebName);
		
		txtActionFilterWebTagName = new JTextArea();
		txtActionFilterWebTagName.setName("WebTagName");
		txtActionFilterWebTagName.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebTagName);

		txtActionFilterWebTitle = new JTextArea();
		txtActionFilterWebTitle.setName("WebTitle");
		txtActionFilterWebTitle.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebTitle);
		
		txtActionFilterWebAlt = new JTextArea();
		txtActionFilterWebAlt.setName("WebAlt");
		txtActionFilterWebAlt.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebAlt);
		
		txtActionFilterWebTextContent = new JTextArea();
		txtActionFilterWebTextContent.setName("WebTextContent");
		txtActionFilterWebTextContent.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebTextContent);
		
		txtActionFilterWebSrc = new JTextArea();
		txtActionFilterWebSrc.setName("WebSrc");
		txtActionFilterWebSrc.setLineWrap(true);
		allTextAreas.add(txtActionFilterWebSrc);
	}

	private Component selectedFilter() {
		selectedTag = comboBoxAllFilterTags.getSelectedItem().toString();
		for(JTextArea jTextArea : allTextAreas) {
			if(jTextArea.getName().equals(selectedTag)) {
				return jTextArea;
			}
		}
		selectedTag = "Title";
		return txtActionFilterWidgetTitle;
	}

	/**
	 * Populate Filter Fields from Settings structure.
	 * @param settings The settings to load.
	 */
	public void populateFrom(final Settings settings) {
		txtProcessFilter.setText(settings.get(ConfigTags.ProcessesToKillDuringTest));

		// General Filters
		txtActionFilterWidgetTitle.setText(settings.get(ConfigTags.ActionFilterWidgetTitle));
		txtActionFilterWidgetDesc.setText(settings.get(ConfigTags.ActionFilterWidgetDesc));
		txtActionFilterWidgetText.setText(settings.get(ConfigTags.ActionFilterWidgetText));
		txtActionFilterWidgetValuePattern.setText(settings.get(ConfigTags.ActionFilterWidgetValuePattern));

		// Webdriver Filters
		txtActionFilterWebId.setText(settings.get(ConfigTags.ActionFilterWebId));
		txtActionFilterWebHref.setText(settings.get(ConfigTags.ActionFilterWebHref));
		txtActionFilterWebName.setText(settings.get(ConfigTags.ActionFilterWebName));
		txtActionFilterWebTagName.setText(settings.get(ConfigTags.ActionFilterWebTagName));
		txtActionFilterWebTitle.setText(settings.get(ConfigTags.ActionFilterWebTitle));
		txtActionFilterWebAlt.setText(settings.get(ConfigTags.ActionFilterWebAlt));
		txtActionFilterWebTextContent.setText(settings.get(ConfigTags.ActionFilterWebTextContent));
		txtActionFilterWebSrc.setText(settings.get(ConfigTags.ActionFilterWebSrc));
	}

	/**
	 * Retrieve information from the Filter  GUI.
	 * @param settings reference to the object where the settings will be stored.
	 */
	public void extractInformation(final Settings settings) {
		settings.set(ConfigTags.ProcessesToKillDuringTest, txtProcessFilter.getText());
		
		// General Filters
		settings.set(ConfigTags.ActionFilterWidgetTitle, txtActionFilterWidgetTitle.getText());
		settings.set(ConfigTags.ActionFilterWidgetDesc, txtActionFilterWidgetDesc.getText());
		settings.set(ConfigTags.ActionFilterWidgetText, txtActionFilterWidgetText.getText());
		settings.set(ConfigTags.ActionFilterWidgetValuePattern, txtActionFilterWidgetValuePattern.getText());
		
		// Webdriver Filters
		settings.set(ConfigTags.ActionFilterWebId, txtActionFilterWebId.getText());
		settings.set(ConfigTags.ActionFilterWebHref, txtActionFilterWebHref.getText());
		settings.set(ConfigTags.ActionFilterWebName, txtActionFilterWebName.getText());
		settings.set(ConfigTags.ActionFilterWebTagName, txtActionFilterWebTagName.getText());
		settings.set(ConfigTags.ActionFilterWebTitle, txtActionFilterWebTitle.getText());
		settings.set(ConfigTags.ActionFilterWebAlt, txtActionFilterWebAlt.getText());
		settings.set(ConfigTags.ActionFilterWebTextContent, txtActionFilterWebTextContent.getText());
		settings.set(ConfigTags.ActionFilterWebSrc, txtActionFilterWebSrc.getText());
	}
}
