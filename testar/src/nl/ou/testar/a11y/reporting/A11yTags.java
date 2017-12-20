/*************************************************************************************
 *
 * COPYRIGHT (2017):
 *
 * Open Universiteit
 * www.ou.nl<http://www.ou.nl>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of mosquitto nor the names of its
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
 ************************************************************************************/

package nl.ou.testar.a11y.reporting;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;

/**
 * Accessibility tags
 * @author Davy Kager
 *
 */
public final class A11yTags extends TagsBase {

	private A11yTags() {}
	
	public static final Tag<EvaluationResults> A11yEvaluationResults = from("A11yEvaluationResults", EvaluationResults.class);
	public static final Tag<Integer> A11yResultCount = from("A11yResultCount", Integer.class);
	public static final Tag<Integer> A11yPassCount = from("A11yPassCount", Integer.class);
	public static final Tag<Integer> A11yWarningCount = from("A11yWarningCount", Integer.class);
	public static final Tag<Integer> A11yErrorCount = from("A11yErrorCount", Integer.class);
	public static final Tag<Boolean> A11yHasViolations = from("A11yHasViolations", Boolean.class);

}
