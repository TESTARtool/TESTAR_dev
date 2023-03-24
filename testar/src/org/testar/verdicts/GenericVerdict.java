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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.RuleMatch;
import org.testar.monkey.Main;
import org.testar.monkey.alayer.Rect;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.monkey.alayer.Widget;


public class GenericVerdict {

	// By default consider that all the verdicts of the class are enabled
	public static List<String> enabledVerdicts = Arrays.stream(GenericVerdict.class.getDeclaredMethods())
			.filter(classMethod -> java.lang.reflect.Modifier.isPublic(classMethod.getModifiers()))
			.map(java.lang.reflect.Method::getName)
			.collect(Collectors.toList());

	// Load the ignore file from the settings directory
	public static String spellingIgnoreFile = Main.settingsDir + File.separator + "SpellingIgnoreList.txt";

	// Custom list to ignore spelling checker words
	private static List<String> spellingIgnoreList = new ArrayList<>();
	
    private static void loadSpellingIgnoreListFromFile()
    {
        try
        {
        	spellingIgnoreList.clear();
            File file = new File(spellingIgnoreFile);
            if(file.exists() && !file.isDirectory()) 
            {
              Scanner s = new Scanner(file);
           
              while (s.hasNextLine()){
            	  spellingIgnoreList.add(s.nextLine());
              }
              
              s.close();
            }
        }
        catch(java.io.FileNotFoundException ex)
        {
            // ignore errors
        }
        Collections.sort(spellingIgnoreList);
    }

	// TODO: It should be easy to open/edit the ignore from the Functional Verdicts tab.
    private static void saveSpellingIgnoreListToFile()
    {    
        try
        {
        	Collections.sort(spellingIgnoreList);
	        FileWriter writer = new FileWriter(spellingIgnoreFile); 
	        for(String str: spellingIgnoreList) {
	          writer.write(str + System.lineSeparator());
        }
        writer.close();
        }
        catch(java.io.IOException ex)
        {
            // ignore errors
        }
    }
	
    protected static boolean isNotOnIgnoreList(String text)
    {
        return !text.isEmpty() && !spellingIgnoreList.contains(text);
    }
    
	public static Verdict SpellChecker(State state, Tag<String> tagTextChecker, Language languageChecker, String ignorePatternRegEx) {
		// If this method is NOT enabled, just return verdict OK
		String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
		if(!enabledVerdicts.contains(methodName)) return Verdict.OK;

		loadSpellingIgnoreListFromFile();
		
		Pattern ignorePattern = Pattern.compile(ignorePatternRegEx);
		
		// If it is enabled, then execute the verdict implementation
		Verdict spellCheckerVerdict = Verdict.OK;
		JLanguageTool langTool = new JLanguageTool(languageChecker);
		// Iterate through all the widgets of the state to apply the spell checker in the desired String Tag
		for(Widget w : state) {
			String tagTextContent = w.get(tagTextChecker, "");
			
			// If there is text that is not ignored, apply the spell checking
			if (isNotOnIgnoreList(tagTextContent)) {
				
				Matcher ignoreMatcher = ignorePattern.matcher(tagTextContent);
				
				if ((ignorePatternRegEx == "" || !ignoreMatcher.find())) {
					try {
						List<RuleMatch> matches = langTool.check(tagTextContent);
						for (RuleMatch match : matches) {
	
							String errorMsg = "Potential error at characters " + match.getFromPos() + "-" + match.getToPos() + " : " + match.getMessage();
							String correctMsg = "Suggested correction(s): " + match.getSuggestedReplacements();
	
							String verdictMsg = "Widget Title ( " + tagTextContent + " ) " + errorMsg + " " + correctMsg;
							String spellingSuspect = "//" + tagTextContent;
							if (!spellingIgnoreList.contains(spellingSuspect)) {
								spellingIgnoreList.add(spellingSuspect);
							}
							spellCheckerVerdict = spellCheckerVerdict.join(new Verdict(Verdict.SEVERITY_WARNING_UI_TRANSLATION_OR_SPELLING_ISSUE, verdictMsg, Arrays.asList((Rect)w.get(Tags.Shape))));
						}
	
					}
					catch (IOException ioe) {
						System.err.println("JLanguageTool error checking widget: " + tagTextContent);
					}
				}
			}
		}

		spellCheckerVerdict.addDescription("spell_checking_verdict");
		saveSpellingIgnoreListToFile();
		return spellCheckerVerdict;
	}
}
