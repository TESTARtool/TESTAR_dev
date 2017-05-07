/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

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