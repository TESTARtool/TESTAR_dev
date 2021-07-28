package nl.ou.testar.visualvalidation;

import org.fruit.alayer.Tag;
import org.fruit.alayer.TagsBase;
import org.fruit.alayer.Verdict;

public class VisualValidationTag extends TagsBase {
    public static final Tag<Verdict> VisualValidationVerdict = from("VisualValidationVerdict", Verdict.class);
}
