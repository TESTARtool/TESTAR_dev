/***************************************************************************************************
 *
 * Copyright (c) 2018 - 2021 Open Universiteit - www.ou.nl
 * Copyright (c) 2018 - 2021 Universitat Politecnica de Valencia - www.upv.es
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


package org.testar.reporting;

import org.apache.commons.lang.StringEscapeUtils;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.Verdict;
import org.testar.OutputStructure;
import org.testar.monkey.alayer.exceptions.NoSuchTagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Set;

public class HtmlSequenceReport implements Reporting{

    private boolean firstStateAdded = false;
    private boolean firstActionsAdded = false;

    private static final String[] HEADER = new String[] {
    		"<!DOCTYPE html>",
    		"<html>",
    		// Script function that allows to reverse the order of the states
    		"<script>",
    		"function reverse(){",
    		"let direction = document.getElementById('main').style.flexDirection;",
    		"if(direction === 'column') document.getElementById('main').style.flexDirection = 'column-reverse';",
    		"else document.getElementById('main').style.flexDirection = 'column';}",
    		"</script>",
    		"<head>",
    		"<title>TESTAR execution sequence report</title>",
    		"</head>",
    		"<body>"
    };

    private PrintWriter out;
    private static final String REPORT_FILENAME_MID = "_sequence_";
    private static final String REPORT_FILENAME_AFT = ".html";
    private static String htmlFilename;
    private static String FINAL_VERDICT_FILENAME = "OK";

    private int innerLoopCounter = 0;

    public HtmlSequenceReport() {
        try{
            //TODO put filename into settings, name with sequence number
            // creating a new file for the report
        	htmlFilename = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString+"_"
                    + OutputStructure.executedSUTname + REPORT_FILENAME_MID + OutputStructure.sequenceInnerLoopCount
                    + REPORT_FILENAME_AFT;

            out = new PrintWriter(htmlFilename, HTMLReporter.CHARSET);
            for(String s:HEADER){
                write(s);
            }

            write("<h1>TESTAR execution sequence report for sequence " + OutputStructure.sequenceInnerLoopCount + "</h1>");
            // HTML button to invoke reverse function
            write("<button id='reverseButton' onclick='reverse()'>Reverse order</button>");
            // Initialize the main div container to apply the reverse order
            write("<div id='main' style='display:flex;flex-direction:column'>");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor for Replay mode. 
     *
     * @param pathReplayedSequence
     */
    public HtmlSequenceReport(String pathReplayedSequence) {
        try {
        	htmlFilename = OutputStructure.htmlOutputDir + File.separator + OutputStructure.startInnerLoopDateString+"_"
                    + OutputStructure.executedSUTname + REPORT_FILENAME_MID + OutputStructure.sequenceInnerLoopCount
                    + REPORT_FILENAME_AFT;

            out = new PrintWriter(htmlFilename, HTMLReporter.CHARSET);
            for(String s:HEADER) {
                write(s);
            }

            write("<h1>TESTAR replay sequence report for file " + pathReplayedSequence + "</h1>");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void addTitle(int h, String text){
        write("<h"+h+">"+text+"</h"+h+">");
    }

    //TODO: This method is not used, check and delete
    public void addSequenceStep(State state, String actionImagePath){
    	try {
    		String imagePath = state.get(Tags.ScreenshotPath);
    		// repairing the file paths:
    		if(imagePath.contains("./output")){
    			imagePath = imagePath.replace("./output","../");
    		}
    		write("<h4>State:</h4>");
    		write("<p><img src=\""+imagePath+"\"></p>");
    		write("<h4>Action:</h4>");
    		write("<p><img src=\""+actionImagePath+"\"></p>");
    	}catch(NullPointerException | NoSuchTagException e) {
    		System.out.println("ERROR: Adding the Sequence step " + innerLoopCounter + " in the HTML report");
    		write("<h2>ERROR Adding current Sequence step " + innerLoopCounter + "</h2>");
    	}
    }

    public void addState(State state){
        if(firstStateAdded){
            if(firstActionsAdded){
                writeStateIntoReport(state);
            }else if(state.get(Tags.OracleVerdict, Verdict.OK).severity() > Verdict.SEVERITY_OK){
                //if the first state contains a failure, write the same state in case it was a login
                writeStateIntoReport(state);
            }else{
                //don't write the state as it is the same - getState is run twice in the beginning, before the first action
            }
        }else{
            firstStateAdded = true;
            writeStateIntoReport(state);
        }
    }

    private void writeStateIntoReport(State state){
    	try {
    		String imagePath = state.get(Tags.ScreenshotPath);
    		if(imagePath.contains("./output")){
    			int indexStart = imagePath.indexOf("./output");
    			int indexScrn = imagePath.indexOf("scrshots");
    			String replaceString = imagePath.substring(indexStart,indexScrn);
    			imagePath = imagePath.replace(replaceString,"../");
    		}
    		write("<div id='block' style='display:flex;flex-direction:column'>"); // Open state block container
    		write("<h2>State "+innerLoopCounter+"</h2>");
    		write("<h4>ConcreteIDCustom="+state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")+"</h4>");
    		write("<h4>AbstractIDCustom="+state.get(Tags.AbstractIDCustom, "NoAbstractIdCustomAvailable")+"</h4>");
    		write("<p><img src=\""+imagePath+"\"></p>");
    		write("</div>"); // Close state block container
    	}catch(NullPointerException | NoSuchTagException e) {
    		System.out.println("ERROR: Adding the State number " + innerLoopCounter + " in the HTML report");
    		write("<h2>ERROR Adding current State " + innerLoopCounter + "</h2>");
    	}
    	innerLoopCounter++;
    }

    public void addActions(Set<Action> actions){
        if(!firstActionsAdded) firstActionsAdded = true;
        write("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
        write("<h4>Set of actions:</h4><ul>");
        for(Action action:actions){
            write("<li>");
            try{
            	if(action.get(Tags.Desc)!=null) {
            		String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            		write("<b>"+ escaped +"</b>  || ");
            	}
            }catch(NullPointerException | NoSuchTagException e){
                e.printStackTrace();
            }

            write(StringEscapeUtils.escapeHtml(action.toString()));
            write(" || ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable"));
            try{if(action.get(Tags.AbstractIDCustom)!=null) write(" || AbstractIDCustom="+action.get(Tags.AbstractIDCustom));}catch(NullPointerException | NoSuchTagException e){e.printStackTrace();}
            write("</li>");
        }
        write("</ul>");
        write("</div>"); // Close derived actions block container
    }

    public void addActionsAndUnvisitedActions(Set<Action> actions, Set<String> concreteIdsOfUnvisitedActions){
        if(!firstActionsAdded) firstActionsAdded = true;
        if(actions.size()==concreteIdsOfUnvisitedActions.size()){
        	write("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
            write("<h4>Set of actions (all unvisited - a new state):</h4><ul>");
            for(Action action:actions){
                write("<li>");
                
                try{
                	if(action.get(Tags.Desc)!=null) {
                		String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
                		write("<b>" + escaped + "</b>");
                	}
                }catch(NullPointerException | NoSuchTagException e){}

                write(" || ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")
                + " || " + StringEscapeUtils.escapeHtml(action.toString()));
                
                write("</li>");
            }
            write("</ul>");
            write("</div>"); // Close derived actions block container
        }else if(concreteIdsOfUnvisitedActions.size()==0){
        	write("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
            write("<h4>All actions have been visited, set of available actions:</h4><ul>");
            for(Action action:actions){
            	write("<li>");

            	try{
            		if(action.get(Tags.Desc)!=null) {
            			String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            			write("<b>" + escaped + "</b>");
            		}
            	}catch(NullPointerException | NoSuchTagException e){}

            	write(" || ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")
            	+ " || " + StringEscapeUtils.escapeHtml(action.toString()));

            	write("</li>");
            }
            write("</ul>");
            write("</div>"); // Close derived actions block container
        }else{
        	write("<div id='block' style='display:flex;flex-direction:column'>"); // Open derived actions block container
            write("<h4>"+concreteIdsOfUnvisitedActions.size()+" out of "+actions.size()+" actions have not been visited yet:</h4><ul>");
            for(Action action:actions){
            	if(concreteIdsOfUnvisitedActions.contains(action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable"))){
            		//action is unvisited -> showing:
            		write("<li>");

            		try{
            			if(action.get(Tags.Desc)!=null) {
            				String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
            				write("<b>" + escaped + "</b>");
            			}
            		}catch(NullPointerException | NoSuchTagException e){}

            		write(" || ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable")
            		+ " || " + StringEscapeUtils.escapeHtml(action.toString()));

            		write("</li>");
            	}
            }
            write("</ul>");
            write("</div>"); // Close derived actions block container
        }
    }

    public void addSelectedAction(State state, Action action){
    	String screenshotDir = OutputStructure.screenshotsOutputDir;

    	if(screenshotDir.contains("./output")){
        	int indexStart = screenshotDir.indexOf("./output");
        	int indexScrn = screenshotDir.indexOf("scrshots");
        	String replaceString = screenshotDir.substring(indexStart,indexScrn);
        	screenshotDir = screenshotDir.replace(replaceString,"../");
        }

        String actionPath = screenshotDir + File.separator 
        		+ OutputStructure.startInnerLoopDateString + "_" + OutputStructure.executedSUTname
        		+ "_sequence_" + OutputStructure.sequenceInnerLoopCount 
        		+ File.separator + state.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable") + "_" + action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable") + ".png";

        write("<div id='block' style='display:flex;flex-direction:column'>"); // Open executed action block container
        write("<h2>Selected Action "+innerLoopCounter+" leading to State "+innerLoopCounter+"\"</h2>");
        write("<h4>ConcreteIDCustom="+action.get(Tags.ConcreteIDCustom, "NoConcreteIdCustomAvailable"));

        try{
        	if(action.get(Tags.Desc)!=null) {
        		String escaped = StringEscapeUtils.escapeHtml(action.get(Tags.Desc));
        		write(" || "+ escaped);
        	}
        }catch(NullPointerException | NoSuchTagException e){}

        write("</h4>");
        if(actionPath.contains("./output")){
            actionPath = actionPath.replace("./output","..");
        }
        write("<p><img src=\""+actionPath+"\"></p>");
        write("</div>"); // Close executed action block container
    }

    public void addTestVerdict(Verdict verdict){
    	String verdictInfo = verdict.info();
    	if(verdict.severity() > Verdict.OK.severity())
    		verdictInfo = verdictInfo.replace(Verdict.OK.info(), "");

        write("<div id='block' style='display:flex;flex-direction:column'>"); // Open verdict block container
        write("<h2>Test verdict for this sequence: "+verdictInfo+"</h2>");
        write("<h4>Severity: "+verdict.severity()+"</h4>");
        write("</div>"); // Close verdict block container

        FINAL_VERDICT_FILENAME = verdict.verdictSeverityTitle();
    }

    public void close() {
    	write("</div>"); // Close the main div container
    	for(String s:HTMLReporter.FOOTER){
    		write(s);
    	}
    	out.close();

    	// Finally rename the HTML report by indicating the final Verdict
    	// sequence_1.html to sequence_1_OK.html
    	String htmlFilenameVerdict = htmlFilename.replace(".html", "_" + FINAL_VERDICT_FILENAME + ".html");
    	new File(htmlFilename).renameTo(new File (htmlFilenameVerdict));
    }

    private void write(String s) {
        out.println(s);
        out.flush();
    }

    private String start(String tag) {
        return "<" + tag + ">";
    }

    private String end(String tag) {
        return "</" + tag + ">";
    }
}
