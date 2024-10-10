package org.testar.statemodel;

import java.util.HashSet;
import java.util.Set;

import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TagsBase;

public class StateModelTags extends TagsBase  {

	private StateModelTags () {}

	/**
	 * The interest that user has to execute an action
	 */
	public static final Tag<Integer> UserInterest = from("UserInterest", Integer.class);

	private static Set<Tag<?>> stateModelTags = new HashSet<Tag<?>>() {
		{
			add(UserInterest);
		}
	};

	public static Set<Tag<?>> getStateModelTags() {
		return stateModelTags;
	}

}