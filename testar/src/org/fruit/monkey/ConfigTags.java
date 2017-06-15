/**
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 * *
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 * *
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *
 * @author Sebastian Bauersfeld
 */

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import org.fruit.Pair;
import org.fruit.alayer.Tag;

import java.util.List;

public final class ConfigTags {
    public static final Tag<String> ProcessesToKillDuringTest = Tag.from("ProcessesToKillDuringTest", String.class);
    public static final Tag<Boolean> ShowVisualSettingsDialogOnStartup = Tag.from("ShowVisualSettingsDialogOnStartup", Boolean.class);
    public static final Tag<Integer> LogLevel = Tag.from("LogLevel", Integer.class);
    public static final Tag<String> SuspiciousTitles = Tag.from("SuspiciousTitles", String.class);
    public static final Tag<String> ClickFilter = Tag.from("ClickFilter", String.class);
    public static final Tag<String> OutputDir = Tag.from("OutputDir", String.class);
    public static final Tag<String> TempDir = Tag.from("TempDir", String.class);
    public static final Tag<Boolean> OnlySaveFaultySequences = Tag.from("OnlySaveFaultySequences", Boolean.class);
    public static final Tag<Boolean> ForceForeground = Tag.from("ForceForeground", Boolean.class);
    public static final Tag<Double> ActionDuration = Tag.from("ActionDuration", Double.class);
    public static final Tag<Double> FaultThreshold = Tag.from("FaultThreshold", Double.class);
    public static final Tag<Double> TimeToWaitAfterAction = Tag.from("TimeToWaitAfterAction", Double.class);
    public static final Tag<Boolean> VisualizeActions = Tag.from("VisualizeActions", Boolean.class);
    public static final Tag<Boolean> VisualizeSelectedAction = Tag.from("VisualizeSelectedAction", Boolean.class);
    public static final Tag<Boolean> DrawWidgetUnderCursor = Tag.from("DrawWidgetUnderCursor", Boolean.class);
    public static final Tag<Boolean> DrawWidgetInfo = Tag.from("DrawWidgetInfo", Boolean.class);
    public static final Tag<Boolean> ExecuteActions = Tag.from("ExecuteActions", Boolean.class);
    public static final Tag<String> PathToReplaySequence = Tag.from("PathToReplaySequence", String.class);
    public static final Tag<AbstractProtocol.Modes> Mode = Tag.from("Mode", AbstractProtocol.Modes.class);
    public static final Tag<String> SUTConnectorValue = Tag.from("SUTConnectorValue", String.class);
    public static final Tag<Integer> SequenceLength = Tag.from("SequenceLength", Integer.class);
    public static final Tag<Integer> Sequences = Tag.from("Sequences", Integer.class);
    public static final Tag<Double> ReplayRetryTime = Tag.from("ReplayRetryTime", Double.class);
    public static final Tag<Double> MaxTime = Tag.from("MaxTime", Double.class);
    public static final Tag<Double> StartupTime = Tag.from("StartupTime", Double.class);
    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> Delete = Tag.from("Delete", (Class<List<String>>) (Class<?>) List.class);
    @SuppressWarnings("unchecked")
    public static final Tag<List<Pair<String, String>>> CopyFromTo = Tag.from("CopyFromTo", (Class<List<Pair<String, String>>>) (Class<?>) List.class);
    @SuppressWarnings("unchecked")
    public static final Tag<List<String>> MyClassPath = Tag.from("MyClassPath", (Class<List<String>>) (Class<?>) List.class);
    //public static final Tag<String> OracleClass = Tag.from("OracleClass", String.class);
    //public static final Tag<String> ActionBuilderClass = Tag.from("ActionBuilderClass", String.class);
    //public static final Tag<String> SystemBuilderClass = Tag.from("SystemBuilderClass", String.class);
    //public static final Tag<String> StateBuilderClass = Tag.from("StateBuilderClass", String.class);
    //public static final Tag<String> ActionSelectorClass = Tag.from("ActionSelectorClass", String.class);
    //public static final Tag<String> CanvasBuilderClass = Tag.from("CanvasBuilderClass", String.class);
    public static final Tag<String> ProtocolClass = Tag.from("ProtocolClass", String.class);
    public static final Tag<Boolean> UseRecordedActionDurationAndWaitTimeDuringReplay = Tag.from("UseRecordedActionDurationAndWaitTimeDuringReplay", Boolean.class);
    public static final Tag<Boolean> StopGenerationOnFault = Tag.from("StopGenerationOnFault", Boolean.class);
    public static final Tag<Double> TimeToFreeze = Tag.from("TimeToFreeze", Double.class);
    public static final Tag<Boolean> ShowSettingsAfterTest = Tag.from("ShowSettingsAfterTest", Boolean.class);

    // begin by urueda

    public static final Tag<String> SUTConnector = Tag.from("SUTConnector", String.class);
    public static final Tag<String> TestGenerator = Tag.from("TestGenerator", String.class);
    public static final Tag<Double> MaxReward = Tag.from("MaxReward", Double.class);
    public static final Tag<Double> Discount = Tag.from("Discount", Double.class);
    public static final Tag<Boolean> AlgorithmFormsFilling = Tag.from("AlgorithmFormsFilling", Boolean.class);

    public static final Tag<Boolean> DrawWidgetTree = Tag.from("DrawWidgetTree", Boolean.class);

    public static final Tag<Integer> ExplorationSampleInterval = Tag.from("ExplorationSampleInterval", Integer.class);
    public static final Tag<Boolean> GraphsActivated = Tag.from("GraphsActivated", Boolean.class);
    public static final Tag<Boolean> PrologActivated = Tag.from("PrologActivated", Boolean.class);

    public static final Tag<Boolean> GraphResuming = Tag.from("GraphResuming", Boolean.class);
    public static final Tag<Boolean> ForceToSequenceLength = Tag.from("ForceToSequenceLength", Boolean.class);

    public static final Tag<Integer> NonReactingUIThreshold = Tag.from("NonReactingUIThreshold", Integer.class);

    public static final Tag<Boolean> OfflineGraphConversion = Tag.from("OfflineGraphConversion", Boolean.class);

    // end by urueda

    // begin by florendegier
    public static final Tag<Boolean> GraphDBEnabled = Tag.from("GraphDBEnabled", Boolean.class);
    public static final Tag<String>  GraphDBUrl = Tag.from("GraphDBUrl", String.class);
    public static final Tag<String>  GraphDBUser = Tag.from("GraphDBUser", String.class);
    public static final Tag<String>  GraphDBPassword = Tag.from("GraphDBPassword",String.class);

}