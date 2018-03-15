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


/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.alayer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fruit.Assert;
import org.fruit.Pair;
import org.fruit.Util;
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.NoSuchTagException;

public abstract class SUTBase implements SUT {
	
	private Map<Tag<?>, Object> tagValues = Util.newHashMap();
	boolean allFetched;
	
	protected AutomationCache nativeAutomationCache = null; // by urueda
	
	/**
	 * @author: urueda
	 */
	@Override
	public AutomationCache getNativeAutomationCache() {
		return this.nativeAutomationCache;
	}
	
	public final <T> T get(Tag<T> tag) throws NoSuchTagException {
		T ret = get(tag, null);
		if(ret == null)
			throw new NoSuchTagException(tag);
		return ret;
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(Tag<T> tag, T defaultValue) {
		Assert.notNull(tag);
		T ret = (T) tagValues.get(tag);
		if(ret == null && !tagValues.containsKey(tag))
			ret = fetch(tag);
		return ret == null ? defaultValue : ret;
	}

	public final Iterable<Tag<?>> tags() {
		Set<Tag<?>> domain = Util.newHashSet();
		domain.addAll(tagDomain());
		domain.addAll(tagValues.keySet());
		Set<Tag<?>> ret = Util.newHashSet();

		for(Tag<?> t : domain){
			if(tagValues.containsKey(t)){
				if(tagValues.get(t) != null)
					ret.add(t);
			}else{
				if(fetch(t) != null)
					ret.add(t);
			}
		}
		return ret;
	}

	protected <T> T fetch(Tag<T> tag){ return null; }
	protected Set<Tag<?>> tagDomain(){ return Collections.emptySet(); }

	public <T> void set(Tag<T> tag, T value) {
		Assert.notNull(tag, value);
		Assert.isTrue(tag.type().isInstance(value), "Value not of type required by this tag!");
		tagValues.put(tag, value);
	}

	public void remove(Tag<?> tag) { tagValues.put(Assert.notNull(tag), null); }
	
	/**
	 * Retrieves the running processes.
	 * @return A list of pairs &lt;PID,NAME&gt; with the PID/NAME of running processes.
	 * @author: urueda
	 */
	@Override
	public List<Pair<Long, String>> getRunningProcesses(){
		List<Pair<Long, String>> runningProcesses = Util.newArrayList();
		for(ProcessHandle ph : Util.makeIterable(this.get(Tags.ProcessHandles, Collections.<ProcessHandle>emptyList().iterator())))
			runningProcesses.add(Pair.from(ph.pid(), ph.name()));
		return runningProcesses;
	}

}
