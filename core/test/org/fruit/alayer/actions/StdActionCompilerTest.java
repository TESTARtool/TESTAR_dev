/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
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

package org.fruit.alayer.actions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.fruit.alayer.Action;
import org.fruit.alayer.devices.KBKeys;
import org.junit.Before;
import org.junit.Test;

public class StdActionCompilerTest {
	
	private StdActionCompiler compiler;

	@Before
	public void setUp() {
		compiler = new StdActionCompiler();
	}

	@Test
	public void testHitShortcutKey() {
		List<KBKeys> keys = new ArrayList<>();
		keys.add(KBKeys.VK_CONTROL);
		keys.add(KBKeys.VK_SHIFT);
		keys.add(KBKeys.VK_F);
		Action action = compiler.hitShortcutKey(keys);
		// TODO: instead of looking at a string, inspect the compound action's component actions
		String exp =
				"(VK_CONTROL)(VK_SHIFT)(VK_F)" // press
				+ "(VK_F)(VK_SHIFT)(VK_CONTROL)"; // release
		assertEquals("All keys should be pressed first, then released in opposite order",
				exp, action.toParametersString());
	}

}
