/***************************************************************************************************
 *
 * Copyright (c) 2020 - 2025 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020 - 2025 Open Universiteit - www.ou.nl
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


package org.testar.monkey.alayer.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.commons.text.StringEscapeUtils;
import org.testar.monkey.Assert;
import org.testar.monkey.alayer.Action;
import org.testar.monkey.alayer.Role;
import org.testar.monkey.alayer.SUT;
import org.testar.monkey.alayer.State;
import org.testar.monkey.alayer.TaggableBase;
import org.testar.monkey.alayer.Tags;
import org.testar.monkey.alayer.exceptions.ActionFailedException;

public final class PasteText extends TaggableBase implements Action {

	private static final long serialVersionUID = 1033277478070938602L;
	private static final CharsetEncoder CharEncoder = Charset.forName("UTF-32").newEncoder();
	private final String text;

	public PasteText(String text) {
		Assert.hasText(text);
		checkEncoder(text);
		this.text = text;
	}

	@Override
	public void run(SUT system, State state, double duration) throws ActionFailedException {
		Assert.isTrue(duration >= 0);
		Assert.notNull(system);

		// Tag used to change the final text of PasteText actions
		// This is necessary for LLMs decisions but may alter the actions abstraction
		String inputText = this.get(Tags.InputText, this.text);

		StringSelection selection = new StringSelection(inputText);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		try {
		    clipboard.setContents(selection, null);
		} catch (IllegalStateException ise) {
		    throw new ActionFailedException("PasteText action execution failed due to currently unavailable clipboard!", ise);
		}

		system.get(Tags.StandardKeyboard).paste();
	}

	public static void checkEncoder(String text) {
		if (!CharEncoder.canEncode(text))
			throw new IllegalArgumentException("This string is not an UTF-32 string!");
	}

	public String toString(){ return "Pasted text '" + StringEscapeUtils.escapeHtml4(this.get(Tags.InputText, this.text)) + "'"; }

	@Override
	public String toString(Role... discardParameters) {
		for (Role r : discardParameters){
			if (r.name().equals(ActionRoles.Type.name()))
				return "Text pasted";
		}
		return toString();
	}	

	@Override
	public String toShortString() {
		Role r = get(Tags.Role, null);
		if (r != null)
			return r.toString();
		else
			return toString();
	}

	@Override
	public String toParametersString() {
		return "(" + this.get(Tags.InputText, this.text) + ")";
	}

}
