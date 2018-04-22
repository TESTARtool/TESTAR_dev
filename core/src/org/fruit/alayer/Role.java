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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.fruit.Assert;

public final class Role implements Serializable{
	private static final long serialVersionUID = 4301814192425648282L;	
	private final static ConcurrentHashMap<Role, Role> existingRoles = new ConcurrentHashMap<Role, Role>();
	private Set<Role> parents;
	transient private Set<Role> ancestors;
	private final String name;
	private int hashcode = 0;

	public static boolean isOneOf(Role r, Role... oneOf){
		Assert.notNull(r, oneOf);
		for(Role o : oneOf){
			if(r.isA(o))
				return true;
		}
		return false;
	}
	
	// begin by urueda@STaQ
    
	public static boolean isOneOf(Role r, Collection<Role> oneOf){
		Assert.notNull(r, oneOf);
		for(Role o : oneOf){
			if(r.isA(o))
				return true;
		}
		return false;		
	}
	
	public static boolean isAnyOneOf(List<Widget> widgets, Role... oneOf) {
		Role r;
		for(Widget w : widgets) {
			r = w.get(Tags.Role, null);
			if (r != null) {
				for (Role o : oneOf) {
					if (r.isA(o)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// end by urueda@STaQ	
		
	public static Role from(String name, Role... inheritFrom){
		Assert.notNull(name, inheritFrom);
		Role ret = new Role(name, inheritFrom);
		Role existing = existingRoles.putIfAbsent(ret, ret);
		if(existing != null)
			return existing;
		return ret;
	}

	private Role(final String name, final Role... inheritFrom){
		this.name = name;
		parents = new HashSet<Role>();
		ancestors = new HashSet<Role>();
		
		for(Role r : inheritFrom){
			parents.add(r);
			calculateAncestors(r.parents(), ancestors);
		}
		parents.removeAll(ancestors);
		parents = Collections.unmodifiableSet(parents);
		ancestors.addAll(parents);
		ancestors = Collections.unmodifiableSet(ancestors);
	}
	
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
		ois.defaultReadObject();
		ancestors = Collections.unmodifiableSet(calculateAncestors(parents, new HashSet<Role>()));
	}
	
	private Object readResolve() throws ObjectStreamException{
		Role existing = existingRoles.putIfAbsent(this, this);
		return existing == null ? this : existing;
	}
	
	public Set<Role> parents() { return parents; }
	public Iterable<Role> ancestors(){ return ancestors; }
	public boolean isA(Role other) { return equals(other) || ancestors.contains(other); }
	public String toString(){ return name(); }
	public String name() { return name; }
	
	public boolean equals(Object other){
		if(other == this) return true;
		if(other instanceof Role){
			Role otherR = (Role) other;
			return name.equals(otherR.name) &&
					parents.equals(otherR.parents);
		}
		return false;
	}
	
	public int hashCode(){
		int ret = hashcode;
		if(ret == 0){
			ret = name.hashCode() + 31 * parents.hashCode();
			hashcode = ret;
		}
		return ret;			
	}
	
	private Set<Role> calculateAncestors(Set<Role> parents, Set<Role> out){
		for(Role parent : parents){
			out.add(parent);
			calculateAncestors(parent.parents(), out);
		}
		return out;
	}	
}
