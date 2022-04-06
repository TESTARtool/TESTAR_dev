package org.testar.visualvalidation;

import org.testar.monkey.alayer.Tag;
import org.testar.monkey.alayer.TagsBase;
import org.testar.monkey.alayer.Verdict;

public class VisualValidationTag extends TagsBase {
    public static final Tag<Verdict> VisualValidationVerdict = from("VisualValidationVerdict", Verdict.class);
}
