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

package org.testar.verdicts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.testar.monkey.Pair;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.Roles;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;
import org.testar.monkey.alayer.webdriver.WdDriver;
import org.testar.monkey.alayer.webdriver.enums.WdRoles;
import org.testar.monkey.alayer.webdriver.enums.WdTags;

import com.google.common.collect.Comparators;

/**
 * All public methods in this class must start with "verdict" naming. 
 */
public class WebVerdict {

	// By default consider that all the verdicts of the class are enabled
	public static List<String> enabledVerdicts = Arrays.stream(WebVerdict.class.getDeclaredMethods())
			.filter(classMethod -> java.lang.reflect.Modifier.isPublic(classMethod.getModifiers()))
			.map(java.lang.reflect.Method::getName)
			.collect(Collectors.toList());

	public static Verdict verdictAlertSuspiciousMessage(State state, String pattern, Action lastExecutedAction) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict alertVerdict = Verdict.OK;
		if(!WdDriver.alertMessage.isEmpty()) {
			Matcher matcher = Pattern.compile(pattern).matcher(WdDriver.alertMessage);
			if (matcher.find()) {
				// The widget to remark is the state by default
				Widget w = state;
				// But if the alert was prompt by executing an action in a widget, remark this widget
				if(lastExecutedAction != null  && lastExecutedAction.get(Tags.OriginWidget, null) != null) {
					w = lastExecutedAction.get(Tags.OriginWidget);
				}

				String verdictMsg = String.format("Detected an alert with a suspicious message %s ! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
						WdDriver.alertMessage, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

				alertVerdict = alertVerdict.join(new Verdict(Verdict.SEVERITY_SUSPICIOUS_ALERT, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return alertVerdict;
	}

	public static Verdict verdictNumberWithLotOfDecimals(State state, int maxDecimals) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict decimalsVerdict = Verdict.OK;
		for(Widget w : state) {
			// If the widget contains a web text that is a double number
			if(!w.get(WdTags.WebTextContent, "").isEmpty() && isNumeric(w.get(WdTags.WebTextContent))) {
				// Count the decimal places of the text number
				String number = w.get(WdTags.WebTextContent).replace(",", ".");
				int decimalPlaces = number.length() - number.indexOf('.') - 1;

				if(number.contains(".") && decimalPlaces > maxDecimals) {
					String verdictMsg = String.format("Widget with more than %s decimals! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
							maxDecimals, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

					decimalsVerdict = decimalsVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_DECIMALS, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
				}
			}
		}
		return decimalsVerdict;
	}

	private static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		strNum = strNum.trim().replace("\u0024", "").replace("\u20AC", "");
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	// TODO: Improve visualization highlighting the specific rows
	public static Verdict verdictDetectDuplicatedRowsInTable(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict duplicateRowsInTableVerdict = Verdict.OK;
		for(Widget w : state) {
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				List<Pair<Widget, String>> rowElementsDescription = new ArrayList<>();
				extractAllRowDescriptionsFromTable(w, rowElementsDescription);

				// https://stackoverflow.com/a/52296246 + ChatGPT
				List<Pair<Widget, String>> duplicatedDescriptions = 
						rowElementsDescription.stream()
						.collect(Collectors.groupingBy(Pair::right))
						.entrySet().stream()
						.filter(e -> e.getValue().size() > 1)
						.flatMap(e -> e.getValue().stream())
						.collect(Collectors.toList());

				// If the list of duplicated descriptions contains a matching prepare the verdict
				if(!duplicatedDescriptions.isEmpty()) {
					for(Pair<Widget, String> duplicatedWidget : duplicatedDescriptions) {
						String verdictMsg = String.format("Detected a duplicated rows in a Table! Role: %s , WebId: %s, Description: %s", 
								duplicatedWidget.left().get(Tags.Role), duplicatedWidget.left().get(WdTags.WebId, ""), duplicatedWidget.right());

						duplicateRowsInTableVerdict = duplicateRowsInTableVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_TABLE_ROWS, verdictMsg, Arrays.asList((Rect)duplicatedWidget.left().get(Tags.Shape))));
					}
				}

			}
		}

		return duplicateRowsInTableVerdict;
	}

	private static void extractAllRowDescriptionsFromTable(Widget w, List<Pair<Widget, String>> rowElementsDescription) {
		if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdTR)) {
			rowElementsDescription.add(new Pair<Widget, String>(w, obtainWidgetTreeDescription(w)));
		}

		// Iterate through the form element widgets
		for(int i = 0; i < w.childCount(); i++) {
			extractAllRowDescriptionsFromTable(w.child(i), rowElementsDescription);
		}
	}

	private static String obtainWidgetTreeDescription(Widget w) {
		String widgetDesc = w.get(WdTags.WebTextContent, "");

		// Iterate through the form element widgets
		for(int i = 0; i < w.childCount(); i++) {
			widgetDesc = widgetDesc + "_" + obtainWidgetTreeDescription(w.child(i));
		}

		return widgetDesc;
	}

	public static Verdict verdictEmptySelectItemsVerdict(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict emptySelectListVerdict = Verdict.OK;
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return document.getElementById('%s').length", elementId);
				Long selectItemsLength = (Long) WdDriver.executeScript(query);
				// Verify that contains at least one item element
				if (selectItemsLength.intValue() <= 1) {
					String verdictMsg = String.format("Empty or Unique Select element detected! Role: %s , Path: %s , Desc: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

					emptySelectListVerdict = new Verdict(Verdict.SEVERITY_WARNING_ORPHAN_ITEM, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return emptySelectListVerdict;
	}

	public static Verdict verdictUnsortedSelectOptionsVerdict(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict unsortedSelectElementVerdict = Verdict.OK;
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return [...document.getElementById('%s').options].map(o => o.value)", elementId);
				@SuppressWarnings("unchecked")
				ArrayList<String> selectOptionsList = (ArrayList<String>) WdDriver.executeScript(query);

				// Now that we have collected all the array list of the option values verify that is sorted 
				if(!isSorted(selectOptionsList)) {

					String verdictMsg = String.format("Detected a Select web element with unsorted elements! Role: %s , Path: %s , WebId: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

					return new Verdict(Verdict.SEVERITY_WARNING_UNSORTED, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return unsortedSelectElementVerdict;
	}

	private static boolean isSorted(List<String> listOfStrings) {
		return Comparators.isInOrder(listOfStrings, Comparator.<String> naturalOrder());
	}

	public static Verdict verdictTextAreaWithoutLength(State state, List<Role> roles) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict textAreaVerdict = Verdict.OK;
		for(Widget w : state) {
			if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.get(WdTags.WebMaxLength) == 0) {

				String verdictMsg = String.format("TextArea Widget with 0 Length detected! Role: %s , Path: %s , WebId: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

				textAreaVerdict = textAreaVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_NON_WRITABLE, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return textAreaVerdict;
	}

	//TODO: Check bug fixed by Robin (for example some div exists and will be filled later)
	// TODO: Improve this element without children using white list and black list using the class name to filtering out
	public static Verdict verdictElementWithoutChildren(State state, List<Role> roles) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict emptyChildrenVerdict = Verdict.OK;
		for(Widget w : state) {
			if(roles.contains(w.get(Tags.Role, Roles.Widget)) && w.childCount() < 1) {

				String verdictMsg = String.format("Detected a Web element without child elements! Role: %s , Path: %s , WebId: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

				emptyChildrenVerdict = emptyChildrenVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_ORPHAN_ITEM, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return emptyChildrenVerdict;
	}

	// TODO: Improve with Robin code
	public static Verdict verdictUniqueRadioInput(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict radioInputVerdict = Verdict.OK;
		for(Widget w : state) {
			if(isRadioInput(w) && !siblingRoleElement(w, WdRoles.WdINPUT)) {

				String verdictMsg = String.format("Detected a Web radio input element with a Unique option! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));

				radioInputVerdict = radioInputVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_ORPHAN_ITEM, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return radioInputVerdict;
	}

	private static boolean isRadioInput(Widget w) {
		return w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdINPUT) && w.get(WdTags.WebType, "").equalsIgnoreCase("radio");
	}

	/**
	 * Check if a widget contains a sibling element.
	 */
	private static boolean siblingRoleElement(Widget w, Role role) {
		if(w.parent() == null) return false;
		Widget parent = w.parent();
		for(int i=0; i < parent.childCount(); i++) {
			// If the parent contains a widget child that is not the current widget, return true
			if(parent.child(i).get(Tags.Role, Roles.Widget).equals(role) && parent.child(i) != w) {
				return true;
			}
		}
		return false;
	}

}