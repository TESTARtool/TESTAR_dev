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

import java.text.Collator;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
import java.util.HashSet;
import java.util.Set;

/**
 * All public methods in this class must start with "verdict" naming. 
 */
public class WebVerdict {

	// By default consider that all the verdicts of the class are enabled
	public static List<String> enabledWebVerdicts = Arrays.stream(WebVerdict.class.getDeclaredMethods())
			.filter(classMethod -> java.lang.reflect.Modifier.isPublic(classMethod.getModifiers()))
			.map(java.lang.reflect.Method::getName)
			.collect(Collectors.toList());

	public static Verdict AlertSuspiciousMessage(State state, String pattern, Action lastExecutedAction) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

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

	// Detect a number with more than given maxDecimals.
	// For example: maxDecimals=2 and englishCulture=true
	// GOOD: 10.02
	// GOOD: 0
	// GOOD: 10.2
	// BAD: 10.002
	// BAD: 100.0003
	// This is possibly wrong because the number should have been nicely formatted. 
	// In rare cases a number with more than 2 decimals is required.
	public static Verdict NumberWithLotOfDecimals(State state, int maxDecimals, boolean englishCulture) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict decimalsVerdict = Verdict.OK;
		for(Widget w : state) {
			
			
			// If the widget contains a web text that is a double number
			if(!w.get(WdTags.WebTextContent, "").isEmpty()) {

				String number = w.get(WdTags.WebTextContent);
				
				// https://en.wikipedia.org/wiki/Decimal_separator#History
				// Convert number to an English double, such as 1000.50
				if (englishCulture)
				{
					number = number.replace(",",""); // remove "," thousands separator
				}
				else
				{
					number = number.replace(".","").replace(" ","").replace(",", "."); // remove "," and " " thousands separators, and replace "," decimal to "."
				}

				if (isNumeric(number)) {
					// Count the decimal places of the text number
					int decimalPlaces = number.length() - number.indexOf('.') - 1;

					if(number.contains(".") && decimalPlaces > maxDecimals) {
						String verdictMsg = String.format("Widget with more than %s decimals! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
								maxDecimals, w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));
	
						decimalsVerdict = decimalsVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_DECIMALS, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
					}
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

	// Detect duplicated rows in a table by concatenating all visible values with an _ underscore
	// GOOD: A_B_C
	//       D_E_F
	// BAD:  A_B_C
	//       A_B_C
	// It makes no sense to present a user with multiple rows in a table that is precisely the same.
	// The user cannot distinguish one row from another.
	// The underlying bug could be a technical issue (i.e. all cells have an 'undefined' value) or
	// is more functional, such as a missing column which should make the rows unique and distinguishable
	public static Verdict DetectDuplicatedRowsInTable(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

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
			// If the children of the table are not sub-tables
			if(!w.child(i).get(Tags.Role, Roles.Widget).equals(WdRoles.WdTABLE)) {
				extractAllRowDescriptionsFromTable(w.child(i), rowElementsDescription);
			}
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

	// Detect if a select element (such as a listbox or dropdownlist) has no items.
	// It makes no sense to let a user choose an item out of empty list. 
	// It should be better to disable the widget or make it invisible until there are 2 or more items to choose from.
	// The underlying issue could be a technical issue, i.e. a function wasn't called that should fillup the items or
	// a query filter from the database didn't lead to any results	
	public static Verdict EmptySelectItems(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict emptySelectListVerdict = Verdict.OK;
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return ((document.getElementById('%s') != null) ? document.getElementById('%s').length : 3)", elementId, elementId);
				Long selectItemsLength = (Long) WdDriver.executeScript(query);
				// Verify that contains at least one item element
				if (selectItemsLength.intValue() == 0) {
					String verdictMsg = String.format("Empty select element detected! Role: %s , Path: %s , Desc: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

					emptySelectListVerdict = new Verdict(Verdict.SEVERITY_WARNING_ORPHAN_ITEM, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return emptySelectListVerdict;
	}

	// Detects duplicate or repeated text in descriptions of widgets
	// BAD: The quick brown fox jumps over the lazy dogThe quick brown fox jumps over the lazy dog
	//		undefined undefined
	//		undefined, undefined
	//		someid_12;someid_12
	//		somebody@somewhere.com; somebody@somewhere.com
	//		12;12
	// GOOD:The quick brown fox jumps over the lazy dog
	//		undefined
	//		someid_12
	// The idea is that a value or description should not have repeated text, values or words, because in rare cases this is applicable.
	// For example, the TO field of an e-mail should only have unique e-mailadresses, or the Authors field of a report should only have unique authors
	// This could be a technical issue, where a boundary of a loop is off, or concatenating a string value twice.
	public static Verdict DetectDuplicateText(State state, String ignorePatternRegEx)
	{
		// TODO: Add Ignore list support (list or regex?, for false positivies like 'DocDoc', 'DossierDossier', 'GebrGebr', 'RelRel', dateformats: '03-03-2023 10:43:37', '01-01-2023' '00-00-1900'
		
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		Verdict verdict = Verdict.OK;
		// The expression looks only if the start of the text is repeated somewhere else in the text
		String patternRegex = "^(?=\\b(.*\\D.*)(\\s*\\W*\\s*)\\1(\\b|\\W))(?!\\W)"; // ^(?=\b(.*\D.*)(\s*\W*\s*)\1(\b|\W))(?!\W)
		Pattern pattern = Pattern.compile(patternRegex);
		
		Pattern ignorePattern = Pattern.compile(ignorePatternRegEx);
		
		for(Widget w : state) {
			// TODO: Is WebValue a good tag? Should the same tags be used as the suspicious text verdicts which are set in the UI?
			String desc = w.get(WdTags.WebValue, "");
			Matcher matcher = pattern.matcher(desc);
			Matcher ignoreMatcher = ignorePattern.matcher(desc);
			
			if (matcher.find() && (ignorePatternRegEx == "" || !ignoreMatcher.find())) {
				String verdictMsg = String.format("Detected duplicated or repeated text in description of widget! Role: %s , Path: %s , WebId: %s , Desc: %s , WebValue: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.Desc, ""), w.get(WdTags.WebValue, ""));
				verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING_DUPLICATE_ITEMS, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return verdict;
	}
	
	// Detect HTML or XML tags in description of widget.
	// BAD: The%20%3Cb%3Equick%3C%2Fb%3E%20brown%20fox%20jumps%20%3Ci%3Eover%20the%20lazy%3C%2Fi%3E%20dog
	// 		The <b>quick</b> brown fox jumps <i>over the lazy</i> dog
	// GOOD:The quick brown fox jumps over the lazy dog
	// The idea is that a description should not show markup tags, but should probably show the text in bold, italic, and so on.
	// If there are html tags which can be ignored, then this can be specified in the ignorePatternRegEx parameter 
	public static Verdict DetectHTMLOrXMLTagsInText(State state, String ignorePatternRegEx)
	{
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		Verdict verdict = Verdict.OK;
		String patternRegex = "\\&.*\\;|\\%\\w\\w|\\$\\w\\w|<[^>]*>";
		Pattern pattern = Pattern.compile(patternRegex);
		
		Pattern ignorePattern = Pattern.compile(ignorePatternRegEx);
		
		for(Widget w : state) {
			// TODO: Is WebValue a good tag? Should the same tags be used as the suspicious text verdicts which are set in the UI?
			String desc = w.get(WdTags.WebValue, "");
			Matcher matcher = pattern.matcher(desc);
			Matcher ignoreMatcher = ignorePattern.matcher(desc);
			
			if (matcher.find() && (ignorePatternRegEx == "" || !ignoreMatcher.find())) {
				String verdictMsg = String.format("Detected HTML or XML tags in description of widget! Role: %s , Path: %s , WebId: %s , Desc: %s , WebValue: %s", 
						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.Desc, ""), w.get(WdTags.WebValue, ""));
				verdict = verdict.join(new Verdict(Verdict.SEVERITY_WARNING, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
			}
		}
		return verdict;
	}
	
	public static Verdict SingleSelectItems(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		Verdict selectElementVerdict = Verdict.OK;
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return ((document.getElementById('%s') != null) ? document.getElementById('%s').length : 3)", elementId, elementId);
				Long selectItemsLength = (Long) WdDriver.executeScript(query);

				if (selectItemsLength.intValue() == 1) {
					String verdictMsg = String.format("Only one item in select element detected! Role: %s , Path: %s , Desc: %s", 
							w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

					selectElementVerdict = new Verdict(Verdict.SEVERITY_WARNING_ORPHAN_ITEM, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return selectElementVerdict;
	}

	public static Verdict TooManyItemSelectItems(State state, int thresholdValue) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		Verdict selectElementVerdict = Verdict.OK;
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String query = String.format("return ((document.getElementById('%s') != null) ? document.getElementById('%s').length : 3)", elementId, elementId);
				Long selectItemsLength = (Long) WdDriver.executeScript(query);

                // Report error if dropdownlist has more items than thresholdValue
				if (selectItemsLength.intValue() > thresholdValue) {
					String verdictMsg = String.format("Dropdownlist has %d items, which is more than theshold value of %s! Role: %s , Path: %s , Desc: %s", 
							selectItemsLength.intValue(), thresholdValue, w.get(Tags.Role), w.get(Tags.Path), w.get(Tags.Desc, ""));

					selectElementVerdict = new Verdict(Verdict.SEVERITY_WARNING_MANY_ITEMS, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
				}
			}
		}

		return selectElementVerdict;
	}
	
	// TODO: Give optionally a list of Custom comparators, such as 'Security level'
	
	// Detect that items in a dropdownlist are not sorted alphabetically
	// As a rule of thumb, items in a dropdownlist should be listed alphabetically, because:
	//	-          user can pick an item easier by name
	//	-          user can typ the part of the name and the focus is then automatically set to the first item found with the typed characters
	// GOOD: A, B, C
	// BAD:  B, C, A
	// Note that there are different sorting types, such as natural, alphabetic, ascii, numerical, dictionary, logical orders. 
	// This verdict tests with natural order.
	public static Verdict UnsortedSelectItems(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict unsortedSelectElementVerdict = Verdict.OK;
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String querylength = String.format("return ((document.getElementById('%s') != null) ? document.getElementById('%s').length : 0)", elementId, elementId);
				Long selectItemsLength = (Long) WdDriver.executeScript(querylength);

				if (selectItemsLength > 1) { 
					String query = String.format("return [...document.getElementById('%s').options].map(o => o.text)", elementId);
					@SuppressWarnings("unchecked")
					ArrayList<String> selectOptionsList = (ArrayList<String>) WdDriver.executeScript(query);

					// Now that we have collected all the array list of the option values verify that is sorted 
					if(!isSorted(selectOptionsList)) {

						String verdictMsg = String.format("Detected a Select web element with unsorted elements! Role: %s , Path: %s , WebId: %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""));

                        Verdict matchVerdict = new Verdict(Verdict.SEVERITY_WARNING_UNSORTED, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
                        unsortedSelectElementVerdict = unsortedSelectElementVerdict.join(matchVerdict);
					}
				}
			}
		}

		return unsortedSelectElementVerdict;
	}

	// Detect duplicated items in a Select (dropdownlist/listbox)
	// GOOD: One
	//       Two
	// BAD:  One
	//       One
	// It makes no sense to present a user with multiple items in a list that have precisely the same display value.
	// The user cannot distinguish one item from another.
	// The underlying bug could be a technical issue (i.e. all or some items have an 'undefined' value) or
	// is functional, such as the items should have a more distinguishable display value
	public static Verdict DuplicateSelectItems(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict duplicateSelectItemsVerdict = Verdict.OK;
		for(Widget w : state) {
			// For the web select elements with an Id property
			if(w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdSELECT) && !w.get(WdTags.WebId, "").isEmpty()) {
				String elementId = w.get(WdTags.WebId, "");
				String querylength = String.format("return ((document.getElementById('%s') != null) ? document.getElementById('%s').length : 0)", elementId, elementId);
				Long selectItemsLength = (Long) WdDriver.executeScript(querylength);

				if (selectItemsLength > 1) { 
					String queryTexts = String.format("return [...document.getElementById('%s').options].map(o => o.text)", elementId);
					String queryValues = String.format("return [...document.getElementById('%s').options].map(o => o.value)", elementId);
					@SuppressWarnings("unchecked")
					ArrayList<String> selectOptionsTextsList = (ArrayList<String>) WdDriver.executeScript(queryTexts);
					ArrayList<String> selectOptionsValuesList = (ArrayList<String>) WdDriver.executeScript(queryValues);

					Set<String> duplicatesTexts = findDuplicates(selectOptionsTextsList);
					Set<String> duplicatesValues = findDuplicates(selectOptionsValuesList);
					
					// Now that we have collected all the duplicates in a list verify that there are no duplicates
					if(duplicatesTexts.size() > 0)
					{
						String verdictMsg = String.format("Detected a Select web element with duplicate display value elements! Role: %s , Path: %s , WebId: %s , Duplicate item(s): %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), String.join(",", duplicatesTexts));

						return new Verdict(Verdict.SEVERITY_WARNING_DUPLICATE_ITEMS, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
					}
					if(duplicatesValues.size() > 0)
					{
						String verdictMsg = String.format("Detected a Select web element with duplicate underlying value elements! Role: %s , Path: %s , WebId: %s , Duplicate item(s): %s", 
								w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), String.join(",", duplicatesValues));

						return new Verdict(Verdict.SEVERITY_WARNING_DUPLICATE_ITEMS, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape)));
					}
				}
			}
		}

		return duplicateSelectItemsVerdict;
	}

	private static Set<String> findDuplicates(List<String> list) {
	    Set<String> set = new HashSet<>();
	    Set<String> duplicates = new HashSet<>();
	    for (String s : list) {
	        if (set.contains(s)) {
	            duplicates.add(s);
	        }
	        set.add(s);
	    }
	    return duplicates;
	}

	private static boolean isSorted(List<String> listOfStrings) {
		if(Comparators.isInOrder(listOfStrings, Comparator.<String> naturalOrder())) {
			return true;
		} else if(collatorPrimaryOrder(listOfStrings)) {
			return true;
		} else if(monthComparator(listOfStrings)) {
			return true;
		}

		return false;
	}

	/**
	 * This primary order ignores capital letter comparison. 
	 * @param original
	 * @return
	 */
	private static boolean collatorPrimaryOrder(List<String> original) {
		List<String> copyListOfStrings = new ArrayList<>(original);
		Collator coll = Collator.getInstance(Locale.US);
		// Primary: a vs b
		// SECONDARY: a vs �
		// TERTIARY: a vs A
		// IDENTICAL: unicode comparison
		coll.setStrength(Collator.PRIMARY);
		Collections.sort(copyListOfStrings, coll);
		return original.equals(copyListOfStrings);
	}

	private static boolean monthComparator(List<String> original) {
		List<String> copyListOfStrings = new ArrayList<>(original);
		Comparator<String> comp = Comparator.comparing(s -> Month.valueOf(s.toUpperCase()));
		try {
			copyListOfStrings.sort(comp);
		} catch(IllegalArgumentException iae) {
			return false;
		}
		return original.equals(copyListOfStrings);
	}

	public static Verdict TextAreaWithoutLength(State state, List<Role> roles) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

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
	public static Verdict ElementWithoutChildren(State state, List<Role> roles) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

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

	
	public static Verdict SingleRadioInput(State state) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledWebVerdicts.contains(methodName)) return Verdict.OK;

		// If it is enabled, then execute the verdict implementation
		Verdict radioInputVerdict = Verdict.OK;

		for(Widget w : state) {
            if (isRadioInput(w)) {
                Widget form = findParentByRole(w, WdRoles.WdFORM);
                if (form != null)
                {
                   List<Widget> inputFields = findChildsByRole(form, WdRoles.WdINPUT);
                   Boolean otherRadioButtonWithSameNameFound = false;
                   for(Widget inputField : inputFields) {
                       if (isRadioInput(inputField) && w != inputField && w.get(WdTags.Desc, "").equals(inputField.get(WdTags.Desc, "")))
                       {
                           otherRadioButtonWithSameNameFound = true;
                       }
                   }
        			if(!otherRadioButtonWithSameNameFound) {
        
        				String verdictMsg = String.format("Detected a Web radio input element with a Unique option! Role: %s , Path: %s , WebId: %s , WebTextContent: %s", 
        						w.get(Tags.Role), w.get(Tags.Path), w.get(WdTags.WebId, ""), w.get(WdTags.WebTextContent, ""));
        				radioInputVerdict = radioInputVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_ORPHAN_ITEM, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
        			}
                 }
            }
		}
		return radioInputVerdict;
	}
	
    private static Widget findParentByRole(Widget w, Role role)
    {
        if (w.get(Tags.Role, Roles.Widget).equals(role)) return w;
        Widget parent = w.parent();
        if (parent == null) return null;
        return findParentByRole(parent, role);
    }
    
    private static ArrayList<Widget> findChildsByRole(Widget w, Role role)
    {
        ArrayList<Widget> childs = new ArrayList<>();  
        if (w.get(Tags.Role, Roles.Widget).equals(role)) { 
            childs.add(w); 
        }
        for(int i=0; i < w.childCount(); i++) {
            childs.addAll(findChildsByRole(w.child(i), role));
        }
        return childs;
    }
	
	private static boolean isRadioInput(Widget w) {
		return w.get(Tags.Role, Roles.Widget).equals(WdRoles.WdINPUT) && w.get(WdTags.WebType, "").equalsIgnoreCase("radio");
	}
}
