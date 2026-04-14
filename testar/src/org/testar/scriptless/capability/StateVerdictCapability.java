/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.scriptless.capability;

import org.testar.config.ConfigTags;
import org.testar.core.alayer.Color;
import org.testar.core.alayer.FillPattern;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Roles;
import org.testar.core.alayer.Shape;
import org.testar.core.alayer.StrokePattern;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.ShapeVisualizer;
import org.testar.core.visualizers.Visualizer;
import org.testar.oracle.Oracle;
import org.testar.scriptless.ComposedProtocol;
import org.testar.windows.alayer.UIARoles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testar.core.tag.Tags.IsRunning;

/**
 * Shared oracle evaluation for protocol implementations.
 */
public final class StateVerdictCapability {

    private final Map<String, Matcher> suspiciousTitlesMatchers = new WeakHashMap<String, Matcher>();
    private Pattern suspiciousTitlesPattern;

    public List<Verdict> evaluateVerdicts(ComposedProtocol protocol, State state) {
        List<Verdict> verdicts = new ArrayList<Verdict>();

        if (protocol.isProcessListenerOracleEnabled() && protocol.runtimeContext().processListenerOracle() != null) {
            List<Verdict> processVerdicts = protocol.runtimeContext().processListenerOracle().getVerdicts(state);
            for (Verdict processVerdict : processVerdicts) {
                if (processVerdict.severity() == Verdict.Severity.SUSPICIOUS_PROCESS.getValue()) {
                    verdicts.add(processVerdict);
                }
            }
        }

        if (!state.get(IsRunning, false)) {
            verdicts.add(new Verdict(Verdict.Severity.UNEXPECTEDCLOSE, "System is offline! Closed Unexpectedly! I assume it crashed!"));
            return verdicts;
        }

        if (state.get(Tags.NotResponding, false)) {
            verdicts.add(new Verdict(Verdict.Severity.NOT_RESPONDING, "System is unresponsive! I assume something is wrong!"));
            return verdicts;
        }

        ensureSuspiciousTitlesPattern(protocol);

        for (Widget widget : state) {
            Verdict suspiciousValueVerdict = evaluateSuspiciousWidget(protocol, widget);
            if (suspiciousValueVerdict.severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue()) {
                verdicts.add(suspiciousValueVerdict);
            }
        }

        if (protocol.isLogOracleEnabled() && protocol.runtimeContext().logOracle() != null) {
            List<Verdict> logVerdicts = protocol.runtimeContext().logOracle().getVerdicts(state);
            for (Verdict logVerdict : logVerdicts) {
                if (logVerdict.severity() == Verdict.Severity.SUSPICIOUS_LOG.getValue()) {
                    verdicts.add(logVerdict);
                }
            }
        }

        if (protocol.runtimeContext().extendedOraclesList() != null) {
            for (Oracle extendedOracle : protocol.runtimeContext().extendedOraclesList()) {
                List<Verdict> extendedVerdicts = extendedOracle.getVerdicts(state);
                if (extendedVerdicts != null) {
                    verdicts.addAll(extendedVerdicts);
                }
            }
        }

        if (verdicts.isEmpty()) {
            verdicts.add(Verdict.OK);
        }

        return verdicts;
    }

    private void ensureSuspiciousTitlesPattern(ComposedProtocol protocol) {
        if (suspiciousTitlesPattern == null) {
            suspiciousTitlesPattern = Pattern.compile(
                    protocol.settings().get(ConfigTags.SuspiciousTags),
                    Pattern.UNICODE_CHARACTER_CLASS
            );
        }
    }

    private Verdict evaluateSuspiciousWidget(ComposedProtocol protocol, Widget widget) {
        for (String tagForSuspiciousOracle : protocol.settings().get(ConfigTags.TagsForSuspiciousOracle)) {
            String tagValue = findSuspiciousTagValue(widget, tagForSuspiciousOracle);

            if (tagValue.isEmpty()) {
                continue;
            }

            Matcher matcher = suspiciousTitlesMatchers.get(tagValue);
            if (matcher == null) {
                matcher = suspiciousTitlesPattern.matcher(tagValue);
                suspiciousTitlesMatchers.put(tagValue, matcher);
            }

            if (matcher.matches()) {
                return new Verdict(
                        Verdict.Severity.SUSPICIOUS_TAG,
                        "Discovered suspicious widget '" + tagForSuspiciousOracle + "' : '" + tagValue + "'.",
                        buildSuspiciousTagVisualizer(widget)
                );
            }
        }

        return Verdict.OK;
    }

    private String findSuspiciousTagValue(Widget widget, String tagForSuspiciousOracle) {
        for (Tag<?> tag : widget.tags()) {
            if (tag.name().equals("ValuePattern") && widget.get(Tags.Role, Roles.Widget).equals(UIARoles.UIAEdit)) {
                continue;
            }

            if (widget.get(tag, null) != null && tag.name().equals(tagForSuspiciousOracle)) {
                return widget.get(tag).toString().replace("\n", " ").replace("\r", " ");
            }
        }

        return "";
    }

    private Visualizer buildSuspiciousTagVisualizer(Widget widget) {
        Shape shape = widget.get(Tags.Shape, null);
        if (shape == null) {
            return org.testar.core.util.Util.NullVisualizer;
        }

        Pen redPen = Pen.newPen()
                .setColor(Color.Red)
                .setFillPattern(FillPattern.None)
                .setStrokePattern(StrokePattern.Solid)
                .build();

        return new ShapeVisualizer(redPen, shape, "Suspicious Tag", 0.5, 0.5);
    }
}
