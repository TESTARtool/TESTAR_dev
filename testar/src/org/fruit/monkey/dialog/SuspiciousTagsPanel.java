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

package org.fruit.monkey.dialog;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import org.fruit.alayer.VerdictTags;
import org.fruit.alayer.webdriver.enums.WdVerdictTags;

public class SuspiciousTagsPanel extends JDialog {

	private static final long serialVersionUID = 4974345380215573368L;
	
	private Set<JCheckBox> allTagsCheckBoxes = new LinkedHashSet<>();
	
	// General Tags
	private JCheckBox tagsTitleCheckBox;
	private JCheckBox tagsValuePatternCheckBox;
	private JCheckBox tagsTextCheckBox;
	
	// Webdriver Tags
	private JCheckBox wdTagsWebLocalizedControlTypeCheckBox;
	private JCheckBox wdTagsWebItemTypeCheckBox;
	private JCheckBox wdTagsWebItemStatusCheckBox;
	private JCheckBox wdTagsWebTagNameCheckBox;
	private JCheckBox wdTagsWebIdCheckBox;
	// private JCheckBox wdTagsWebCssClassesCheckBox;  We don't want to detect error-form
	private JCheckBox wdTagsWebHelpTextCheckBox;
	private JCheckBox wdTagsWebNameCheckBox;
	private JCheckBox wdTagsWebTitleCheckBox;
	private JCheckBox wdTagsWebTextContentCheckBox;
	private JCheckBox wdTagsWebHrefCheckBox;
	private JCheckBox wdTagsWebValueCheckBox;
	private JCheckBox wdTagsWebStyleCheckBox;
	private JCheckBox wdTagsWebTargetCheckBox;
	private JCheckBox wdTagsWebAltCheckBox;
	private JCheckBox wdTagsWebDisplayCheckBox;
	private JCheckBox wdTagsWebTypeCheckBox;
	private JCheckBox wdTagsWebSrcCheckBox;
	
	public SuspiciousTagsPanel() {
		initialize();
	}
		
	public void initialize() {
		setTitle("Select Widget Tags to apply Suspicious Patterns");

		setSize(600, 600);
		setLayout(null);
		setLocationRelativeTo(null);
		setResizable(false);
		
		this.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosed(WindowEvent e) {
		        setVisible(false);
		    }
		});

		generalCheckBoxesTags();
		webdriverCheckBoxesTags();
		loadCheckBoxes();
	}
	
	/**
	 * Add general components checkboxes to the all HashSet list and the GUI
	 */
	private void generalCheckBoxesTags() {
		tagsTitleCheckBox = new JCheckBox("Title");
		tagsTitleCheckBox.setBounds(10, 10, 190, 20);
		allTagsCheckBoxes.add(tagsTitleCheckBox);
		add(tagsTitleCheckBox);
		
		tagsValuePatternCheckBox = new JCheckBox("ValuePattern");
		tagsValuePatternCheckBox.setBounds(10, 40, 190, 20);
		allTagsCheckBoxes.add(tagsValuePatternCheckBox);
		add(tagsValuePatternCheckBox);
		
		tagsTextCheckBox = new JCheckBox("Text");
		tagsTextCheckBox.setBounds(10, 70, 190, 20);
		allTagsCheckBoxes.add(tagsTextCheckBox);
		add(tagsTextCheckBox);
	}
	
	/**
	 * Add webdriver components checkboxes to the all HashSet list and the GUI
	 */
	private void webdriverCheckBoxesTags() {
		wdTagsWebLocalizedControlTypeCheckBox = new JCheckBox("WebLocalizedControlType");
		wdTagsWebLocalizedControlTypeCheckBox.setBounds(300, 10, 240, 20);
		allTagsCheckBoxes.add(wdTagsWebLocalizedControlTypeCheckBox);
		add(wdTagsWebLocalizedControlTypeCheckBox);
		
		wdTagsWebItemTypeCheckBox = new JCheckBox("WebItemType");
		wdTagsWebItemTypeCheckBox.setBounds(300, 40, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebItemTypeCheckBox);
		add(wdTagsWebItemTypeCheckBox);
		
		wdTagsWebItemStatusCheckBox = new JCheckBox("WebItemStatus");
		wdTagsWebItemStatusCheckBox.setBounds(300, 70, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebItemStatusCheckBox);
		add(wdTagsWebItemStatusCheckBox);
		
		wdTagsWebTagNameCheckBox = new JCheckBox("WebTagName");
		wdTagsWebTagNameCheckBox.setBounds(300, 100, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebTagNameCheckBox);
		add(wdTagsWebTagNameCheckBox);
		
		wdTagsWebIdCheckBox = new JCheckBox("WebId");
		wdTagsWebIdCheckBox.setBounds(300, 130, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebIdCheckBox);
		add(wdTagsWebIdCheckBox);
		
		wdTagsWebHelpTextCheckBox = new JCheckBox("WebHelpText");
		wdTagsWebHelpTextCheckBox.setBounds(300, 160, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebHelpTextCheckBox);
		add(wdTagsWebHelpTextCheckBox);
		
		wdTagsWebNameCheckBox = new JCheckBox("WebName");
		wdTagsWebNameCheckBox.setBounds(300, 190, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebNameCheckBox);
		add(wdTagsWebNameCheckBox);
		
		wdTagsWebTitleCheckBox = new JCheckBox("WebTitle");
		wdTagsWebTitleCheckBox.setBounds(300, 220, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebTitleCheckBox);
		add(wdTagsWebTitleCheckBox);
		
		wdTagsWebTextContentCheckBox = new JCheckBox("WebText");
		wdTagsWebTextContentCheckBox.setBounds(300, 250, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebTextContentCheckBox);
		add(wdTagsWebTextContentCheckBox);
		
		wdTagsWebHrefCheckBox = new JCheckBox("WebHref");
		wdTagsWebHrefCheckBox.setBounds(300, 280, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebHrefCheckBox);
		add(wdTagsWebHrefCheckBox);
		
		wdTagsWebValueCheckBox = new JCheckBox("WebValue");
		wdTagsWebValueCheckBox.setBounds(300, 310, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebValueCheckBox);
		add(wdTagsWebValueCheckBox);
		
		wdTagsWebStyleCheckBox = new JCheckBox("WebStyle");
		wdTagsWebStyleCheckBox.setBounds(300, 340, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebStyleCheckBox);
		add(wdTagsWebStyleCheckBox);
		
		wdTagsWebTargetCheckBox = new JCheckBox("WebTarget");
		wdTagsWebTargetCheckBox.setBounds(300, 370, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebTargetCheckBox);
		add(wdTagsWebTargetCheckBox);
		
		wdTagsWebAltCheckBox = new JCheckBox("WebAlt");
		wdTagsWebAltCheckBox.setBounds(300, 400, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebAltCheckBox);
		add(wdTagsWebAltCheckBox);
		
		wdTagsWebDisplayCheckBox = new JCheckBox("WebDisplay");
		wdTagsWebDisplayCheckBox.setBounds(300, 430, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebDisplayCheckBox);
		add(wdTagsWebDisplayCheckBox);
		
		wdTagsWebTypeCheckBox = new JCheckBox("WebType");
		wdTagsWebTypeCheckBox.setBounds(300, 460, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebTypeCheckBox);
		add(wdTagsWebTypeCheckBox);
		
		wdTagsWebSrcCheckBox = new JCheckBox("WebSrc");
		wdTagsWebSrcCheckBox.setBounds(300, 490, 190, 20);
		allTagsCheckBoxes.add(wdTagsWebSrcCheckBox);
		add(wdTagsWebSrcCheckBox);
	}
	
	/**
	 * Check the maps of VerdictTags and WdVerdictTags,
	 * to set the check box as selected if tag is enabled
	 */
	public void loadCheckBoxes() {
		// General Tags
		tagsTitleCheckBox.setSelected(VerdictTags.isGeneralStringVerdictTagEnabled("Title"));
		tagsValuePatternCheckBox.setSelected(VerdictTags.isGeneralStringVerdictTagEnabled("ValuePattern"));
		tagsTextCheckBox.setSelected(VerdictTags.isGeneralStringVerdictTagEnabled("Text"));
		
		// Webdriver Tags
		wdTagsWebLocalizedControlTypeCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebLocalizedControl"));
		wdTagsWebItemTypeCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebItemType"));
		wdTagsWebItemStatusCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebItemStatus"));
		wdTagsWebTagNameCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebTagName"));
		wdTagsWebIdCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebId"));
		wdTagsWebHelpTextCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebHelpText"));
		wdTagsWebNameCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebName"));
		wdTagsWebTitleCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebTitle"));
		wdTagsWebTextContentCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebTextContent"));
		wdTagsWebHrefCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebHref"));
		wdTagsWebValueCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebValue"));
		wdTagsWebStyleCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebStyle"));
		wdTagsWebTargetCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebTarget"));
		wdTagsWebAltCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebAlt"));
		wdTagsWebDisplayCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebDisplay"));
		wdTagsWebTypeCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebType"));
		wdTagsWebSrcCheckBox.setSelected(WdVerdictTags.isWebdriverStringVerdictTagEnabled("WebSrc"));
	}
	
	/**
	 * Return a List with that contains the Text of the selected checkBoxes
	 * @return
	 */
	public List<String> extractInformation() {
		StringBuilder selectedSuspiciousTagsBuilder = new StringBuilder("");
		
		for(JCheckBox checkBox : allTagsCheckBoxes) {
			if(checkBox.isSelected()) {
				selectedSuspiciousTagsBuilder.append(checkBox.getText() + ", ");
			}
		}

		if(selectedSuspiciousTagsBuilder.toString().isEmpty()){
			System.out.println("WARNING: NO Widget Tags to apply Suspicious Patterns was selected");
			System.out.println("WARNING: Default value is Title");
			selectedSuspiciousTagsBuilder.append("Title" + ", ");
		}
		
		//Remove last comma +  space
		selectedSuspiciousTagsBuilder.setLength(selectedSuspiciousTagsBuilder.length() - 2);
		
		String selectedSuspiciousTags = selectedSuspiciousTagsBuilder.toString();

		return Arrays.asList(selectedSuspiciousTags);
	}
}
