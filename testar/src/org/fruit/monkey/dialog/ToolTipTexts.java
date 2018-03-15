/***************************************************************************************************
*
* Copyright (c) 2013, 2014, 2015, 2016, 2017 Universitat Politecnica de Valencia - www.upv.es
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
* 2. Redistributions in binary form must reproduce the above copyright
* notice, this list of conditions and the following disclaimer in the
* documentation and/or other materials provided with the distribution.
* 3. Neither the name of the copyright holder nor the names of its
* contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
* ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*******************************************************************************************************/


package org.fruit.monkey.dialog;

public class ToolTipTexts {

  // TTTs for the start buttons
  public static String btnGenerateTTT = "<html>\nStart in Generation-Mode:<br>\n" +
      "This mode will start the SUT and execute a full test.\n</html>";
  public static String btnSpyTTT = "<html>\nStart in Spy-Mode:<br>\n" +
      "This mode does allows you to inspect the GUI of the System under Test. <br>\n" +
      "Simply use the mouse cursor to point on a widget and TESTAR<br>\n" +
      "will display everything it knows about it. The Spy-Mode will also visualize<br>\n" +
      "the set of actions that TESTAR recognizes, so that you can see<br>\n" +
      "which ones will be executed during a test.\n</html>";
  public static String btnReplayTTT = "<html>\nStart in Replay-Mode:<br>\n" +
      "This mode replays a previously recorded sequence.<br>\n" +
      "TESTAR will ask you for the sequence to replay.\n</html>";
  public static String btnViewTTT = "<html>\nStart in View-Mode:<br>\n" +
      "The View-Mode allows you to inspect all steps of a previously recorded<br>\n" +
      "sequence. Contrary to the Replay-Mode, it will not execute any actions,<br>\n" +
      "but only show you the screenshots that were recorded during sequence<br>\n" +
      "generation. This is ideal if a sequence turns out not to be reproducible.\n</html>";

  // TTTs for the general tab
  public static String sutConnectorTTT = "How does TESTAR engage with the SUT";
  public static String suthPathTTT = "<html> Path to the SUT: " +
      "Pick the executable of the SUT or insert a custom command line. </html>";
  public static String nofSequencesTTT = "<html>\nNumber of sequences to generate.\n</html>";
  public static String sequencesActionsTTT = "<html>\nSequence length:<br>\n" +
      "After having executed the given amount of<br>\n" +
      "actions, TESTAR will stop the SUT and proceed with the next<br>\n" +
      "sequence.\n</html>";
  public static String lblLoggingVerbosityTTT = "<html>\nLogging verbosity:<br>\n" +
      "The higher the value, the more information<br>\n" +
      "will be written to TESTAR's log-file.\n</html>";
  public static String loggingVerbosityTTT = "<html> Logging verbosity:<br>\n" +
      "Sets how much information will be written to TESTAR's log-file. </html>";
  public static String checkStopOnFaultTTT = "<html> Stop sequence generation on fault: " +
      "If TESTAR detects and error, it will immediately stop sequence generation. </html>";
  public static String lblCompileTTT = "Always compile protocol on action start";

  // TTTs for the walker tab
  public static String testGeneratorTTT = "Determines how the IU actions are selected during tests";
  public static String checkForceForegroundTTT = "<html> Force the SUT to the foreground: During test generation, windows might get minimized or other<br>  processes might block the SUT's GUI. If you check this option, TESTAR will force the SUT to the<br> foreground. </html>";

  // TTTs for filter panel
  public static String label1TTT = "<html>\nClick-filter:<br>\n" +
      "Certain actions that TESTARs wants to execute might be dangerous or<br>\n" +
      "undesirable, such as printing out documents, creating, moving or deleting files.<br>\n" +
      "TESTAR will not execute clicks on any widget whose title matches the given regular expression.<br>\n" +
      "To see whether or not your expression works, simply start TESTAR in Spy-Mode, which will<br>\n" +
      "visualize the detected actions.\n</html>";
  public static String label2TTT = "<html>\nProcesses to kill:<br>\n" +
      "Some SUTs start other processes during test sequence generation. These might<br>\n" +
      "popup in the foreground and block the SUTs GUI. They might also consume excessive memory, etc.<br>\n" +
      "TESTAR will kill any process whose name matches the given regular expression.\n</html>";

  // TTTs for the oracle panel
  public static String suspiciousTitlesTTT =
      "<html>\nThis is a very simple oracle in the form of a regular expression, " +
          "which is applied to each<br>widget's Title property. " +
          "If TESTAR finds a widget on the screen, whose title matches the given<br>\n" +
          "expression, it will consider the current sequence to be erroneous.\n</html>";

  // TTTs for the timing panel
  public static String actionDurationTTT = "<html>\nAction Duration: " +
      "The higher this value, the longer the execution of actions will take.<br>\n" +
      "Mouse movements and typing become slower, so that it is easier to follow what the<br>\n" +
      "TESTAR is doing. This can be useful during Replay-Mode, in order to replay<br>\n" +
      "a recorded sequence with less speed to better understand a fault.\n</html>";
  public static String actionWaitTimeTTT =
      "<html>\nTime to wait after execution of an action: " +
          "This is the time that TESTAR<br>\n" +
          "pauses after having executed an action in Generation-Mode. Usually, this value<br>\n" +
          "is set to 0. However, sometimes it can make sense to give the GUI of the SUT<br>\n" +
          "more time to react, before executing the next action. If this value is set to a<br>\n" +
          "value > 0, it can greatly enhance reproducibility of sequences at the expense<br>\n" +
          "of longer testing times.\n</html>";
  public static String sutStartupTimeTTT = "<html>\nSUT startup time:<br>\n" +
      "This is the threshold time that TESTAR waits for the SUT to load.<br>\n" +
      "Large and complex SUTs might need more time than small ones.,<br>\n" +
      "Once the SUT UI is ready, TESTAR will start the test sequence.\n</html>";
  public static String maxTestTimeTTT = "<html>\nMaximum test time (seconds):<br>\n" +
      "TESTAR will cease to generate any sequences after this time has elapsed.<br>\n" +
      "This is useful for specifying a test time out, e.g. one hour, one day, one week.\n</html>";
  public static String useRecordedTTT = "<html>\nThis option only affects Replay-Mode. " +
      "If checked, TESTAR will use the action duration and action<br>\n" +
      "wait time that was used during sequence generation. If you uncheck the option, you can specify<br>\n" +
      "your own values.\n</html>";

  // TTTs for the misc panel
  public static String copyFilesTTT = "<html> Files to copy before SUT start. " +
      "It is useful to restore certain<br>  configuration files to their default. " +
      "Therefore you can define pairs of paths (copy from / to).<br> " +
      "TESTAR will copy each specified file from the given source location to " +
      "the given destination.<br> " +
      "Simply double-click the text-area and a file dialog will pop up. </html>";
  public static String deleteFiles = "<html> Files to delete before SUT start: " +
      "Certain SUTs generate configuration files, temporary files and files<br> " +
      "that save the system's state. " +
      "This might be problematic during sequence replay, when you want a<br> " +
      "system to always start in the same state. Therefore, you can specify " +
      "these files, to be deleted<br> before the SUT gets started. " +
      "If you double-click the text-area a file dialog will pop up which allows<br> " +
      "selecting files and directories to be deleted. </html>";

}
