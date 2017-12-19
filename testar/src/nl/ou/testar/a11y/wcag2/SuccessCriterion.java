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

package nl.ou.testar.a11y.wcag2;

import org.fruit.Assert;
import org.fruit.alayer.Verdict;

/**
 * A WCAG success criterion
 * @author Davy Kager
 *
 */
public final class SuccessCriterion extends ItemBase {
	
	private static final long serialVersionUID = 2042413918878837966L;

	/**
	 * WCAG success criterion conformance levels
	 */
	public enum Level {
		/**
		 * Level A: minimum conformance / highest priority guidelines.
		 */
		A,
		
		/**
		 * Level AA: medium priority guidelines.
		 */
		AA,
		
		/**
		 * Level AAA: maximum conformance / lowest priority guidelines.
		 */
		AAA;
	}
	
	/**
	 * The base for building anchor URLs of success criteria
	 */
	public static final String URL_BASE =
			"https://www.w3.org/WAI/GL/2013/WD-wcag2ict-20130905/accordion#";
	
	private static final int NLEVELS = 3;
	private static final double SEVERITY_STEP = (Verdict.SEVERITY_MAX - Verdict.SEVERITY_MIN) / NLEVELS;
	
	private final Level level;
	private final String urlSuffix;
	
	/**
	 * Constructs a new success criterion
	 * @param nr The number of the success criterion.
	 * @param name The name (short description) of the success criterion.
	 * @param parent The guideline (parent) this success criterion belongs to.
	 * @param level The level of the success criterion.
	 * @param urlSuffix The anchor URL suffix on the W3C website.
	 */
	SuccessCriterion(int nr, String name, AbstractGuideline parent, Level level, String urlSuffix) {
		super(nr, name, Assert.notNull(parent));
		Assert.notNull(level);
		Assert.hasText(urlSuffix);
		this.level = level;
		this.urlSuffix = urlSuffix;
	}
	
	/**
	 * Gets the conformance level of this success criterion
	 * @return The conformance level.
	 */
	public Level getLevel() {
		return level;
	}
	
	/**
	 * Gets the anchor URL suffix of this success criterion
	 * @return The URL suffix.
	 */
	public String getURLSuffix() {
		return urlSuffix;
	}
	
	/**
	 * Gets the severity of violating this success criterion as used in verdicts
	 * The severity depends on the conformance level of the success criterion.
	 * A low level (A) corresponds to a high severity
	 * and a high level (AAA) to a low severity.
	 * @return The severity.
	 */
	public double getVerdictSeverity() {
		return Verdict.SEVERITY_MAX - (level.ordinal() * SEVERITY_STEP);
	}
	
}
