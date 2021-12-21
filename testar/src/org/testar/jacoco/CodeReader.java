package org.testar.jacoco;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jacoco.core.analysis.IBundleCoverage;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.core.analysis.IPackageCoverage;
import org.jacoco.report.IReportGroupVisitor;
import org.jacoco.report.ISourceFileLocator;

public class CodeReader implements IReportGroupVisitor {
	
	private static final CounterEntity[] COUNTERS = { CounterEntity.INSTRUCTION,
			CounterEntity.BRANCH, CounterEntity.LINE, CounterEntity.COMPLEXITY,
			CounterEntity.METHOD };

	private final String groupName;
	
	private List<String> codeInfo = new ArrayList<>();

	public List<String> getCodeInfo() {
		return codeInfo;
	}

	public CodeReader() {
		this(null);
	}

	private CodeReader(final String groupName) {
		this.groupName = groupName;
	}

	public void visitBundle(final IBundleCoverage bundle, final ISourceFileLocator locator) throws IOException {
		for (final IPackageCoverage p : bundle.getPackages()) {
			final String packageName = p.getName();
			for (final IClassCoverage c : p.getClasses()) {
				if (c.containsCode()) {
					String coverageLine = "";
					// org/cesilko/rachota/gui/InactivityReminderDialog$14nulljava/lang/Object //[Ljava.lang.String;@6fe0d143
					String className = c.getName()+c.getSignature()+c.getSuperName();//+c.getInterfaceNames();
					coverageLine = coverageLine.concat("Package: " + packageName + ", code class name: " + className + " -");
					for (final CounterEntity entity : COUNTERS) {
						final ICounter counter = c.getCounter(entity);
						coverageLine = coverageLine.concat(" " + entity.name() + ": miss " + counter.getMissedCount() + " cover " + counter.getCoveredCount());
					}
					codeInfo.add(coverageLine);
				}
			}
		}
	}

	public IReportGroupVisitor visitGroup(final String name)
			throws IOException {
		return new CodeReader(appendName(name));
	}

	private String appendName(final String name) {
		return groupName == null ? name : (groupName + "/" + name);
	}
}
