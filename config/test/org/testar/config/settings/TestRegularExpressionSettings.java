package org.testar.config.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.testar.config.ConfigTags;
import org.testar.core.Assert;
import org.testar.core.Pair;
import org.testar.core.tag.Tag;

public class TestRegularExpressionSettings {

    private List<Tag<String>> regularExpressionTags = Arrays.asList(
            ConfigTags.ProcessesToKillDuringTest,
            ConfigTags.ClickFilter,
            ConfigTags.SuspiciousTags,
            ConfigTags.SuspiciousProcessOutput,
            ConfigTags.ProcessLogs,
            ConfigTags.LogOracleRegex,
            ConfigTags.WebPathsAllowed,
            ConfigTags.WebConsoleErrorPattern,
            ConfigTags.WebConsoleWarningPattern
    );

    @Test
    public void validRegularExpressionSetting() {
        List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
        for (Tag<String> tag : regularExpressionTags) {
            tags.add(Pair.from(tag, ".*[aA].*|.*bc.*"));
        }

        Settings settings = new Settings(tags, new Properties());

        for (Tag<String> tag : regularExpressionTags) {
            System.out.println(tag + " valid regex: " + settings.get(tag, ""));
            Assert.isTrue(settings.get(tag, "").equals(".*[aA].*|.*bc.*"));
        }
    }

    @Test
    public void invalidRegularExpressionSetting() {
        for (Tag<String> tag : regularExpressionTags) {
            List<Pair<?, ?>> tags = new ArrayList<Pair<?, ?>>();
            tags.add(Pair.from(tag, ".**[aA].*|.*bc.*"));

            String exceptionMessage = "";
            try {
                new Settings(tags, new Properties());
            } catch (IllegalStateException e) {
                exceptionMessage = e.getMessage();
            }

            System.out.println(exceptionMessage);
            Assert.hasText(exceptionMessage);
            Assert.isTrue(exceptionMessage.contains(tag + " setting is not a valid regular expression"));
        }
    }
}
