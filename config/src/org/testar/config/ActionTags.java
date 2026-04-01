/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2020-2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2020-2026 Open Universiteit - www.ou.nl
 */

package org.testar.config;

import java.util.HashSet;
import java.util.Set;

import org.testar.core.tag.Tag;
import org.testar.core.tag.TagsBase;

public class ActionTags extends TagsBase  {

	private ActionTags() {}

	public static final Tag<Integer> SimilarityValue = from("SimilarityValue", Integer.class);

	private static Set<Tag<Integer>> actionTags;
	static {
		actionTags = new HashSet<Tag<Integer>>();
		actionTags.add(SimilarityValue);
	}

	public static Set<Tag<Integer>> getActionTags() {
		return actionTags;
	}

}
