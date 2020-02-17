/***************************************************************************************************
*
* Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
* Copyright (c) 2019 Open Universiteit - www.ou.nl
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

import nl.ou.testar.ScreenshotJsonFile.JsonUtils;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.fruit.alayer.*;
import org.fruit.alayer.exceptions.*;
import org.fruit.monkey.ConfigTags;
import org.fruit.monkey.RuntimeControlsProtocol.Modes;
import org.testar.OutputStructure;
import org.testar.json.object.JsonArtefactTestResults;
import org.testar.protocols.DesktopProtocol;

/**
 * This is a small change to Desktop Generic Protocol to create JSON files describing the widgets
 *  and their location into output/scrshots folder.
 *
 *  It only changes the getState() method.
 */
public class Protocol_desktop_generic_json extends DesktopProtocol {

	SortedSet<String> sequencesOutputDir = new TreeSet<>();
	SortedSet<String> htmlOutputDir = new TreeSet<>();
	SortedSet<String> logsOutputDir = new TreeSet<>();
	SortedSet<String> sequencesVerdicts = new TreeSet<>();
	
	/**
	 * This method is called when the TESTAR requests the state of the SUT.
	 * Here you can add additional information to the SUT's state or write your
	 * own state fetching routine.
	 *
	 * Here we don't change the default behaviour, but we add one more step to
	 * create a JSON file from the state information.
	 *
	 * @return  the current state of the SUT with attached oracle.
	 */
	@Override
	protected State getState(SUT system) throws StateBuildException{
		State state = super.getState(system);
		// Creating a JSON file with information about widgets and their location on the screenshot:
		if(settings.get(ConfigTags.Mode) == Modes.Generate)
			JsonUtils.createWidgetInfoJsonFile(state);
		
		return state;
	}
	
	/**
	 * This methods is called after each test sequence, allowing for example using external profiling software on the SUT
	 *
	 * super.postSequenceProcessing() is adding test verdict into the HTML sequence report
	 */
	@Override
	protected void postSequenceProcessing() {
		super.postSequenceProcessing();
		sequencesOutputDir.add(getGeneratedSequenceName());
		logsOutputDir.add(getGeneratedLogName());
		htmlOutputDir.add(htmlReport.getGeneratedHTMLName());
		sequencesVerdicts.add(verdictInfo);
	}

	/**
	 *  This methods is called after finishing the last sequence
	 */
	@Override
	protected void closeTestSession() {
		JsonArtefactTestResults.createTestResultsArtefact(settings, sequencesOutputDir,
				logsOutputDir, htmlOutputDir, sequencesVerdicts);
	}
}
