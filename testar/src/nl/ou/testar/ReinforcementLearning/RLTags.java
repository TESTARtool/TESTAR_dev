/***************************************************************************************************
 *
 * Copyright (c) 2019 Open Universiteit - www.ou.nl
 * Copyright (c) 2019 Universitat Politecnica de Valencia - www.upv.es
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


package nl.ou.testar.ReinforcementLearning;

import java.util.HashSet;
import java.util.Set;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;

public class RLTags extends TagsBase  {

	private RLTags () {}
	
	/** 
	 * Reinforcement Learning Sarsa Value
	 */
	public static final Tag<Float> SarsaValue = from("sarsaValue", Float.class);

	public static final Tag<Integer> Counter = from("counter", Integer.class);

	public static final Tag<Integer> ActionCounter = from("actionCounter", Integer.class);

	public static final Tag<Float> QLearningValue = from("qvalue", Float.class);

	private static Set<Tag<?>> reinforcementLearningTags = new HashSet<Tag<?>>() {
		{
			add(SarsaValue);
			add(Counter);
			add(ActionCounter);
			add(QLearningValue);
		}
	};

	public static Tag<?> getTag(String tagName){
		for(Tag<?> tag: reinforcementLearningTags){
			if(tag.name().equals(tagName)){
				System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*");
				System.out.println(tag);
				return tag;
			}
		}
		return SarsaValue;
	}

	public static Set<Tag<?>> getReinforcementLearningTags() {
		return reinforcementLearningTags;
	}
}
