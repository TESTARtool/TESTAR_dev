package org.testar.rascal;

import org.rascalmpl.debug.IRascalMonitor;
import org.rascalmpl.interpreter.Evaluator;
import org.rascalmpl.interpreter.NullRascalMonitor;
import org.rascalmpl.interpreter.env.GlobalEnvironment;
import org.rascalmpl.interpreter.env.ModuleEnvironment;
import org.rascalmpl.uri.URIUtil;
import org.rascalmpl.values.ValueFactoryFactory;

import io.usethesource.vallang.IValueFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DslOracleLoader {

    private final IValueFactory vf;
    private final Evaluator eval;
    private final Path modulePath;

    public IValueFactory getValueFactory() {
        return vf;
    }

    public Evaluator getEvaluator() {
        return eval;
    }

    public Path getModulePath() {
        return modulePath;
    }

    public DslOracleLoader() throws Exception {
        vf = ValueFactoryFactory.getValueFactory();
        GlobalEnvironment heap = new GlobalEnvironment();
        ModuleEnvironment module = new ModuleEnvironment("MAIN", heap);
        IRascalMonitor monitor = new NullRascalMonitor();
        eval = new Evaluator(vf, System.in, System.err, System.out, monitor, module, heap);

        // 1) Rascal standard library
        eval.addRascalSearchPath(URIUtil.rootLocation("std"));

        // 2) Resolve the dev path, if not, the installDist path
        // "testar-oracle" is the dev directory (e.g., to run the dsl loader from the IDE)
        Path resolved = Paths.get("").toAbsolutePath().resolve("testar-oracle").normalize();
        if (!Files.isDirectory(resolved)) {
            // Resolve the installDist path 
            resolved = resolveRascalModuleDir();
        }
        modulePath = resolved;

        // 3) Add folder (if present) to Rascal search path
        if (Files.isDirectory(modulePath)) {
            String u = modulePath.toUri().toString();
            if (!u.endsWith("/")) u += "/";
            eval.addRascalSearchPath(vf.sourceLocation(URI.create(u)));
        } else {
            System.err.println("[DslOracleLoader] WARNING: Rascal modules folder not found: " + modulePath);
        }

        // 4) Include classpath root (in case modules are ever embedded)
        eval.addRascalSearchPath(vf.sourceLocation("classpath", "", "/"));

        // 5) Import the entry module
        eval.doImport(monitor, "lang::testar::Bridge");
    }

    private Path resolveRascalModuleDir() {
        // Resolve path for TESTAR installDist release
        String appHomeEnv = System.getenv("APP_HOME");
        if (appHomeEnv != null && !appHomeEnv.isBlank()) {
            return Paths.get(appHomeEnv, "rascal-modules", "testar-oracle").toAbsolutePath().normalize();
        }

        // If launched with workingDir .../install/testar/bin, go up one to .../install/testar
        Path cwd = Paths.get("").toAbsolutePath().normalize();
        Path maybeAppHome = cwd.getParent();
        if (maybeAppHome != null && Files.isDirectory(maybeAppHome)) {
            Path p = maybeAppHome.resolve("rascal-modules").resolve("testar-oracle").normalize();
            if (Files.isDirectory(p)) return p;
        }

        // Final fallback for local builds
        return Paths.get("target", "install", "testar", "rascal-modules", "testar-oracle")
                .toAbsolutePath().normalize();
    }
}
