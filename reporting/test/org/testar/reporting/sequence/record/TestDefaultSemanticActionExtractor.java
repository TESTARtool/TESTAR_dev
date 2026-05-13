package org.testar.reporting.sequence.record;

import org.junit.Assert;
import org.junit.Test;
import org.testar.core.action.ActionRoles;
import org.testar.core.action.Type;
import org.testar.core.alayer.Role;
import org.testar.core.tag.Tags;

public class TestDefaultSemanticActionExtractor {

    @Test
    public void testExtractNullActionReturnsEmptyRecord() {
        DefaultSemanticActionExtractor extractor = new DefaultSemanticActionExtractor();

        SemanticActionRecord record = extractor.extract(null);

        Assert.assertEquals("", record.getRole());
        Assert.assertEquals("", record.getDescription());
        Assert.assertEquals("", record.getInput());
    }

    @Test
    public void testExtractUsesActionRoleAndDescription() {
        DefaultSemanticActionExtractor extractor = new DefaultSemanticActionExtractor();
        Type action = new Type("secret");
        action.set(Tags.Role, Role.from("TypeAction", ActionRoles.Action));
        action.set(Tags.Desc, "Type password");
        action.set(Tags.InputText, "secret");

        SemanticActionRecord record = extractor.extract(action);

        Assert.assertEquals("typeaction", record.getRole());
        Assert.assertEquals("Type password", record.getDescription());
        Assert.assertEquals("secret", record.getInput());
    }
}
