/*
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Universitat Politecnica de Valencia - www.upv.es
 * Copyright (c) 2026 Open Universiteit - www.ou.nl
 */

package org.testar.engine.service;

import org.testar.config.ConfigTags;
import org.testar.config.settings.Settings;
import org.testar.core.Assert;
import org.testar.core.alayer.Color;
import org.testar.core.alayer.FillPattern;
import org.testar.core.alayer.Pen;
import org.testar.core.alayer.Roles;
import org.testar.core.alayer.Shape;
import org.testar.core.alayer.StrokePattern;
import org.testar.core.service.OracleEvaluationService;
import org.testar.core.state.SUT;
import org.testar.core.state.State;
import org.testar.core.state.Widget;
import org.testar.core.tag.Tag;
import org.testar.core.tag.Tags;
import org.testar.core.util.Util;
import org.testar.core.verdict.Verdict;
import org.testar.core.visualizers.ShapeVisualizer;
import org.testar.core.visualizers.Visualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DefaultOracleEvaluationService implements OracleEvaluationService {

    private final Settings settings;
    private final Map<String, Matcher> suspiciousTitlesMatchers = new WeakHashMap<String, Matcher>();
    private Pattern suspiciousTitlesPattern;

    public DefaultOracleEvaluationService(Settings settings) {
        this.settings = Assert.notNull(settings);
    }

    @Override
    public List<Verdict> getVerdicts(SUT system, State state) {
        Assert.notNull(state);

        List<Verdict> verdicts = new ArrayList<Verdict>();

        if (!state.get(Tags.IsRunning, false)) {
            verdicts.add(new Verdict(
                    Verdict.Severity.UNEXPECTEDCLOSE,
                    "System is offline! Closed Unexpectedly! I assume it crashed!"
            ));
            state.set(Tags.OracleVerdicts, verdicts);
            return verdicts;
        }

        if (state.get(Tags.NotResponding, false)) {
            verdicts.add(new Verdict(
                    Verdict.Severity.NOT_RESPONDING,
                    "System is unresponsive! I assume something is wrong!"
            ));
            state.set(Tags.OracleVerdicts, verdicts);
            return verdicts;
        }

        if (suspiciousTitlesPattern == null) {
            suspiciousTitlesPattern = Pattern.compile(
                    settings.get(ConfigTags.SuspiciousTags),
                    Pattern.UNICODE_CHARACTER_CLASS
            );
        }

        for (Widget widget : state) {
            Verdict suspiciousValueVerdict = evaluateSuspiciousWidget(widget);
            if (suspiciousValueVerdict.severity() == Verdict.Severity.SUSPICIOUS_TAG.getValue()) {
                verdicts.add(suspiciousValueVerdict);
            }
        }

        if (verdicts.isEmpty()) {
            verdicts.add(Verdict.OK);
        }

        state.set(Tags.OracleVerdicts, verdicts);
        return verdicts;
    }

    @Override
    public void addVerdict(State state, Verdict verdict) {
        Assert.notNull(state, verdict);

        List<Verdict> verdicts = state.get(Tags.OracleVerdicts, new ArrayList<Verdict>());
        if (verdicts.size() == 1 && verdicts.contains(Verdict.OK)) {
            verdicts = new ArrayList<Verdict>();
        }

        verdicts.add(verdict);
        state.set(Tags.OracleVerdicts, verdicts);
    }

    private Verdict evaluateSuspiciousWidget(Widget widget) {
        for (String tagForSuspiciousOracle : settings.get(ConfigTags.TagsForSuspiciousOracle)) {
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
            if (ignoreEditWidget(widget, tag)) {
                continue;
            }

            if (widget.get(tag, null) != null && tag.name().equals(tagForSuspiciousOracle)) {
                return widget.get(tag).toString().replace("\n", " ").replace("\r", " ");
            }
        }

        return "";
    }

    private boolean ignoreEditWidget(Widget widget, Tag<?> tag) {
        return tag.name().equals("ValuePattern")
                && widget.get(Tags.Role, Roles.Widget).name().equals("UIAEdit");
    }

    private Visualizer buildSuspiciousTagVisualizer(Widget widget) {
        Shape shape = widget.get(Tags.Shape, null);
        if (shape == null) {
            return Util.NullVisualizer;
        }

        Pen redPen = Pen.newPen()
                .setColor(Color.Red)
                .setFillPattern(FillPattern.None)
                .setStrokePattern(StrokePattern.Solid)
                .build();

        return new ShapeVisualizer(redPen, shape, "Suspicious Tag", 0.5, 0.5);
    }
}
