package org.testar.config.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testar.config.TestarDirectories;
import org.testar.core.policy.VisiblePolicy;

public class ExternalJavaClassSupportTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void loadClassCompilesOnlyRequestedSourceFile() throws Exception {
        File sourceRoot = temporaryFolder.newFolder("workspace");
        File runtimeRoot = temporaryFolder.newFolder("runtime");
        File resourceFile = new File(sourceRoot, "composition.properties");
        Files.writeString(resourceFile.toPath(), "", StandardCharsets.UTF_8);

        Files.writeString(
                new File(sourceRoot, "GoodVisiblePolicy.java").toPath(),
                """
                import org.testar.core.policy.VisiblePolicy;
                import org.testar.core.state.Widget;

                public final class GoodVisiblePolicy implements VisiblePolicy {
                    public boolean isVisible(Widget widget) {
                        return true;
                    }
                }
                """,
                StandardCharsets.UTF_8
        );

        Files.writeString(
                new File(sourceRoot, "ScriptlessOnlyCapability.java").toPath(),
                """
                import org.testar.scriptless.capability.SettingsCapability;

                public final class ScriptlessOnlyCapability implements SettingsCapability {
                }
                """,
                StandardCharsets.UTF_8
        );

        String previousTestarDirectory = TestarDirectories.getTestarDir();
        try {
            TestarDirectories.setTestarDir(runtimeRoot.getAbsolutePath());

            Class<?> loadedClass = ExternalJavaClassSupport.loadClass(
                    "GoodVisiblePolicy",
                    Optional.of(resourceFile.getAbsolutePath())
            );

            Assert.assertTrue(VisiblePolicy.class.isAssignableFrom(loadedClass));
        } finally {
            TestarDirectories.setTestarDir(previousTestarDirectory);
        }
    }
}
