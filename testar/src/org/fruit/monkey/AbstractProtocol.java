/******************************************************************************************
 * COPYRIGHT:                                                                             *
 * Universitat Politecnica de Valencia 2013                                               *
 * Camino de Vera, s/n                                                                    *
 * 46022 Valencia, Spain                                                                  *
 * www.upv.es                                                                             *
 *                                                                                        * 
 * D I S C L A I M E R:                                                                   *
 * This software has been developed by the Universitat Politecnica de Valencia (UPV)      *
 * in the context of the european funded FITTEST project (contract number ICT257574)      *
 * of which the UPV is the coordinator. As the sole developer of this source code,        *
 * following the signed FITTEST Consortium Agreement, the UPV should decide upon an       *
 * appropriate license under which the source code will be distributed after termination  *
 * of the project. Until this time, this code can be used by the partners of the          *
 * FITTEST project for executing the tasks that are outlined in the Description of Work   *
 * (DoW) that is annexed to the contract with the EU.                                     *
 *                                                                                        * 
 * Although it has already been decided that this code will be distributed under an open  *
 * source license, the exact license has not been decided upon and will be announced      *
 * before the end of the project. Beware of any restrictions regarding the use of this    *
 * work that might arise from the open source license it might fall under! It is the      *
 * UPV's intention to make this work accessible, free of any charge.                      *
 *****************************************************************************************/

/**
 *  @author Sebastian Bauersfeld
 */
package org.fruit.monkey;

import static org.fruit.alayer.Tags.ActionDelay;
import static org.fruit.alayer.Tags.ActionDuration;
import static org.fruit.alayer.Tags.ActionSet;
import static org.fruit.alayer.Tags.Desc;
import static org.fruit.alayer.Tags.ExecutedAction;
import static org.fruit.alayer.Tags.OracleVerdict;
import static org.fruit.alayer.Tags.Role;
import static org.fruit.alayer.Tags.SystemState;
import static org.fruit.alayer.Tags.Visualizer;
import static org.fruit.monkey.ConfigTags.LogLevel;
import static org.fruit.monkey.ConfigTags.OutputDir;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.fruit.Assert;
import org.fruit.UnProc;
import org.fruit.Util;
import org.fruit.alayer.Action;
import org.fruit.alayer.Canvas;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.Finder;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Point;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Role;
import org.fruit.alayer.Roles;
import org.fruit.alayer.SUT;
import org.fruit.alayer.Shape;
import org.fruit.alayer.State;
import org.fruit.alayer.Tag;
import org.fruit.alayer.Taggable;
import org.fruit.alayer.TaggableBase;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Verdict;
import org.fruit.alayer.Visualizer;
import org.fruit.alayer.Widget;
import org.fruit.alayer.actions.ActionRoles;
import org.fruit.alayer.actions.ActivateSystem;
import org.fruit.alayer.actions.AnnotatingActionCompiler;
import org.fruit.alayer.actions.KillProcess;
import org.fruit.alayer.actions.NOP;
import org.fruit.alayer.devices.AWTMouse;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.devices.Mouse;
import org.fruit.alayer.devices.MouseButtons;
import org.fruit.alayer.devices.ProcessHandle;
import org.fruit.alayer.exceptions.ActionBuildException;
import org.fruit.alayer.exceptions.ActionFailedException;
import org.fruit.alayer.exceptions.NoSuchTagException;
import org.fruit.alayer.exceptions.StateBuildException;
import org.fruit.alayer.exceptions.SystemStartException;
import org.fruit.alayer.exceptions.SystemStopException;
import org.fruit.alayer.exceptions.WidgetNotFoundException;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import es.upv.staq.testar.ActionStatus;
import es.upv.staq.testar.CodingManager;
import es.upv.staq.testar.EventHandler;
import es.upv.staq.testar.FlashFeedback;
import es.upv.staq.testar.IEventListener;
import es.upv.staq.testar.NativeLinker;
import es.upv.staq.testar.graph.Grapher;
import es.upv.staq.testar.graph.IEnvironment;
import es.upv.staq.testar.graph.IGraphState;
import es.upv.staq.testar.prolog.JIPrologWrapper;
import es.upv.staq.testar.protocols.ProtocolUtil;
import es.upv.staq.testar.serialisation.LogSerialiser;
import es.upv.staq.testar.serialisation.ScreenshotSerialiser;
import es.upv.staq.testar.serialisation.TestSerialiser;

public abstract class AbstractProtocol implements UnProc<Settings>,
												  IEventListener { // by urueda
	
	public static enum Modes{
		Spy,
		GenerateManual, // by urueda
		Generate, GenerateDebug, Quit, View, AdhocTest, Replay, ReplayDebug;
	}
	
	protected boolean faultySequence; // by urueda (refactored from DefaultProtocol)

	Set<KBKeys> pressed = EnumSet.noneOf(KBKeys.class);	
	private Settings settings;
	private Modes mode;
	protected Mouse mouse = AWTMouse.build();
	private boolean saveStateSnapshot = false,
					markParentWidget = false; // by urueda
	int actionCount, sequenceCount;
	double startTime;
	
	// begin by urueda
	
	public static final String DATE_FORMAT = "dd.MMMMM.yyyy HH:mm:ss";
	
	protected double passSeverity = Verdict.SEVERITY_OK;
	private int generatedSequenceNumber = -1;
	private Object[] userEvent = null;
	private Action lastExecutedAction = null;
    private long lastStamp = -1;
	protected ProtocolUtil protocolUtil = new ProtocolUtil();
	protected EventHandler eventHandler;
	protected Canvas cv;
	
    protected Pattern clickFilterPattern = null;
    protected Map<String,Matcher> clickFilterMatchers = new WeakHashMap<String,Matcher>();
    protected Pattern suspiciousTitlesPattern = null;
    protected Map<String,Matcher> suspiciousTitlesMatchers = new WeakHashMap<String,Matcher>();
	
	protected JIPrologWrapper jipWrapper;
    private double delay = Double.MIN_VALUE;
    private final static double SLOW_MOTION = 2.0;
    
    protected String forceKillProcess = null;
    protected boolean forceToForeground = false,
    				  forceNextActionESC = false;
    
    private boolean forceToSequenceLengthAfterFail = false;
    private int testFailTimes = 0;
    
    protected boolean nonSuitableAction = false;
    
	protected class ProcessInfo{
		public SUT sut;
		public long pid;
		public long handle;
		public String Desc;
		public ProcessInfo(SUT sut, long pid, long handle, String desc){
			this.sut = sut;
			this.pid = pid;
			this.handle = handle;
			this.Desc = desc;
		}
		public String toString(){
			return "PID <" + this.pid + "> HANDLE <" + this.handle + "> DESC <" + this.Desc + ">";
		}
	}
	protected List<ProcessInfo> contextRunningProcesses = null;
	
	protected List<ProcessInfo> getRunningProcesses(String debugTag){
		List<ProcessInfo> runningProcesses = new ArrayList<ProcessInfo>();
		long pid, handle; String desc;
		List<SUT> runningP = NativeLinker.getNativeProcesses();
		System.out.println("[" + debugTag + "] " + "Running processes (" + runningP.size() + "):");
		int i = 1;
		for (SUT sut : runningP){
			System.out.println("\t[" + (i++) +  "] " + /*sut.toString() + "\t - " +*/ sut.getStatus());			
			pid = sut.get(Tags.PID, Long.MIN_VALUE);
			if (pid != Long.MIN_VALUE){
				handle = sut.get(Tags.HANDLE, Long.MIN_VALUE);
				desc = sut.get(Tags.Desc, null);
				runningProcesses.add(new ProcessInfo(sut,pid,handle,desc));
			}
		}
		return runningProcesses;
	}
	
	final static long MAX_KILL_WINDOW = 10000; // 10 seconds
	
	protected void killTestLaunchedProcesses(){
		boolean kill;
		for (ProcessInfo pi1 : getRunningProcesses("END")){
			kill = true;
			for (ProcessInfo pi2 : this.contextRunningProcesses){
				if (pi1.pid == pi2.pid){
					kill = false;
					break;
				}
			}
			if (kill)
				killProcess(pi1,MAX_KILL_WINDOW);
		}
	}

	// true if the process is not running anymore (killing might not happen)
	protected boolean killRunningProcesses(SUT sut, long KILL_WINDOW){
		boolean allKilled = true;
		for(ProcessHandle ph : Util.makeIterable(sut.get(Tags.ProcessHandles, Collections.<ProcessHandle>emptyList().iterator()))){
			if (ph.name() != null && sut.get(Tags.Desc, "").contains(ph.name())){
				try{
					System.out.println("\tWill kill <" + ph.name() +"> with PID <" + ph.pid() + ">");
					ph.kill();
				} catch (SystemStopException e){
					System.out.println("Exception killing SUT running processes: " + e.getMessage());
					allKilled = false;
				}
			}
		}
		return allKilled;
	}
		
	private boolean killProcess(ProcessInfo pi, long KILL_WINDOW){
		if (pi.sut.isRunning()){
			System.out.println("Will kill process: " + pi.toString());
			long now = System.currentTimeMillis(),
				 elapsed;
			do{
				elapsed = System.currentTimeMillis() - now;
				try {
					NativeLinker.getNativeProcessHandle(pi.pid).kill();
				} catch (Exception e){
					System.out.println("\tException trying to kill process: <" + e.getMessage() + "> after <" + elapsed + "> ms");
					Util.pauseMs(500);
				}
			} while (pi.sut.isRunning() && elapsed < KILL_WINDOW);
			return pi.sut.isRunning();
		} else{
			System.out.println("Did not kill process as it is not running: " + pi.toString());
			return true;
		}
	}
    
	// end by urueda
	
	@Override
	public void keyDown(KBKeys key){
		pressed.add(key);

		// begin by urueda
        if (pressed.contains(KBKeys.VK_SHIFT) && key == KBKeys.VK_SPACE){
        	if (this.delay == Double.MIN_VALUE){
            	this.delay = settings().get(ConfigTags.TimeToWaitAfterAction).doubleValue();
        		settings().set(ConfigTags.TimeToWaitAfterAction, SLOW_MOTION);            	
        	} else{
        		settings().set(ConfigTags.TimeToWaitAfterAction, this.delay);
        		delay = Double.MIN_VALUE;
        	}
        }
        // end by urueda
		
		// state snapshot
		if(key == KBKeys.VK_UP && pressed.contains(KBKeys.VK_SHIFT))
			saveStateSnapshot = true;

		// change mode with shift + right (forward)
		else if(key == KBKeys.VK_RIGHT && pressed.contains(KBKeys.VK_SHIFT))
			nextMode(true);

		// change mode with shift + left (backward)
		else if(key == KBKeys.VK_LEFT && pressed.contains(KBKeys.VK_SHIFT))
			nextMode(false);

		// quit with shift + down
		else if(key == KBKeys.VK_DOWN && pressed.contains(KBKeys.VK_SHIFT)){
			LogSerialiser.log("User requested to stop monkey!\n", LogSerialiser.LogLevel.Info);
			mode = Modes.Quit;
			protocolUtil.stopAdhocServer(); // by urueda
		}

		// toggle action visualization
		else if(key == KBKeys.VK_1 && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.VisualizeActions, !settings().get(ConfigTags.VisualizeActions));		

		// toggle widget mark visualization
		else if(key == KBKeys.VK_2 && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.DrawWidgetUnderCursor, !settings().get(ConfigTags.DrawWidgetUnderCursor));		

		// toggle widget info visualization
		else if(key == KBKeys.VK_3 && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.DrawWidgetInfo, !settings().get(ConfigTags.DrawWidgetInfo));		
		
		// begin by urueda (method structure changed from if* to <if, elseif*, else>)
		
		else if (key == KBKeys.VK_4  && pressed.contains(KBKeys.VK_SHIFT))
			settings().set(ConfigTags.DrawWidgetTree, !settings.get(ConfigTags.DrawWidgetTree));
			
		/*else if (key == KBKeys.VK_ENTER && pressed.contains(KBKeys.VK_SHIFT)){
			protocolUtil.startAdhocServer();
			mode = Modes.AdhocTest;
			LogSerialiser.log("'" + mode + "' mode active.\n", LogSerialiser.LogLevel.Info);
		}*/
		
		else if (!pressed.contains(KBKeys.VK_SHIFT) &&
				mode() == Modes.GenerateManual && userEvent == null){
			//System.out.println("USER_EVENT key_down! " + key.toString());
			userEvent = new Object[]{key}; // would be ideal to set it up at keyUp
		}
		
		if (pressed.contains(KBKeys.VK_ALT) && pressed.contains(KBKeys.VK_SHIFT))
			markParentWidget = !markParentWidget;
		// end by urueda
	}
	
	@Override
	public void keyUp(KBKeys key){
		pressed.remove(key);
	}
	
	@Override
	public void mouseDown(MouseButtons btn, double x, double y){}
	
	@Override
	public void mouseUp(MouseButtons btn, double x, double y){
		// begin by urueda
		if (mode() == Modes.GenerateManual && userEvent == null){
			//System.out.println("USER_EVENT mouse_up!");
		    userEvent = new Object[]{
	        	btn,
	        	new Double(x),
	       		new Double(y)
			};
		}
		// end by urueda				
	}
	
	public synchronized Modes mode(){ return mode; }

	private synchronized void nextMode(boolean forward){
		if(forward){
			switch(mode){
			//case Spy: mode = Modes.Generate; break;
			case Spy: userEvent = null; mode = Modes.GenerateManual; break; // by urueda
			case GenerateManual: mode = Modes.Generate; break; // by urueda
			case Generate: mode = Modes.GenerateDebug; break;
			case GenerateDebug: mode = Modes.Spy; break;
			case AdhocTest: mode = Modes.Spy; protocolUtil.stopAdhocServer(); break; // by urueda
			case Replay: mode = Modes.ReplayDebug; break;
			case ReplayDebug: mode = Modes.Replay; break;
			default: break;
			}		
		}else{
			switch(mode){
			case Spy: mode = Modes.GenerateDebug; break;
			//case Generate: mode = Modes.Spy; break;
			case GenerateManual: mode = Modes.Spy; break; // by urueda
			case Generate: userEvent = null; mode = Modes.GenerateManual; break; // by urueda
			case GenerateDebug: mode = Modes.Generate; break;
			case AdhocTest: mode = Modes.Spy; protocolUtil.stopAdhocServer(); break; // by urueda
			case Replay: mode = Modes.ReplayDebug; break;
			case ReplayDebug: mode = Modes.Replay; break;
			default: break;
			}		
		}
		//logln("'" + mode + "' mode active.", LogLevel.Info);
		// begin by urueda
		String modeParamS = "";
		if (mode == Modes.GenerateManual)
			modeParamS = " (" + settings.get(ConfigTags.TimeToWaitAfterAction) + " wait time between actions)";
		String modeNfo = "'" + mode + "' mode active." + modeParamS;
		LogSerialiser.log(modeNfo + "\n", LogSerialiser.LogLevel.Info);
		FlashFeedback.flash(modeNfo);
		// end by urueda
	}
	
	// by urueda
	protected synchronized void setMode(Modes mode){
		if (mode() == mode) return;
		List<Modes> modesList = Arrays.asList(Modes.values());
		while (mode() != mode)
			nextMode(modesList.indexOf(mode) > modesList.indexOf(mode()));
	}

	protected final double timeElapsed(){ return Util.time() - startTime; }
	protected final Settings settings(){ return settings; }
	protected void beginSequence() {}
	protected void finishSequence(File recordedSequence) {}
	protected abstract SUT startSystem() throws SystemStartException;
	protected abstract void stopSystem(SUT system); // by urueda
	protected abstract State getState(SUT system) throws StateBuildException;
	protected abstract Verdict getVerdict(State state); // by urueda
	protected abstract Set<Action> deriveActions(SUT system, State state) throws ActionBuildException;
	protected abstract Canvas buildCanvas();
	protected abstract boolean moreActions(State state);
	protected abstract boolean moreSequences();
	protected final int actionCount(){ return actionCount; }
	protected final int sequenceCount(){ return sequenceCount; }
	protected void initialize(Settings settings){}
	
	private String lastPrintParentsOf = "null-id"; // by urueda

	//private synchronized void visualizeState(Canvas canvas, State state){
	private synchronized void visualizeState(Canvas canvas, State state, SUT system){ // by urueda
		if((mode() == Modes.Spy
			|| mode() == Modes.GenerateManual // by urueda
			|| mode() == Modes.ReplayDebug) && settings().get(ConfigTags.DrawWidgetUnderCursor)){
			Point cursor = mouse.cursor();
			Widget cursorWidget = Util.widgetFromPoint(state, cursor.x(), cursor.y(), null);

			if(cursorWidget != null){
				Widget rootW = cursorWidget;
				while (rootW.parent() != null && rootW.parent() != rootW)
					rootW = rootW.parent();
				Shape cwShape = cursorWidget.get(Tags.Shape, null);
				if(cwShape != null){
					cwShape.paint(canvas, Pen.PEN_MARK_ALPHA);
					// begin by urueda
					cwShape.paint(canvas, Pen.PEN_MARK_BORDER);
					if (!settings().get(ConfigTags.DrawWidgetInfo) && !settings().get(ConfigTags.DrawWidgetTree) && !markParentWidget){
						String rootText = "State: " + rootW.get(Tags.ConcreteID),
							   widConcreteText = CodingManager.CONCRETE_ID + ": " + cursorWidget.get(Tags.ConcreteID),
							   roleText = "Role: " + cursorWidget.get(Role, Roles.Widget).toString(),
							   idxText = "Path: " + cursorWidget.get(Tags.Path);
						double miniwidgetInfoW = Math.max(Math.max(Math.max(rootText.length(), widConcreteText.length()), roleText.length()),idxText.length()) * 8; if (miniwidgetInfoW < 256) miniwidgetInfoW = 256;
						double miniwidgetInfoH = 80; // 20 * 4
						Shape minicwShape = Rect.from(cwShape.x() + cwShape.width()/2 + 32,
													  cwShape.y() + cwShape.height()/2 + 32,
													  miniwidgetInfoW, miniwidgetInfoH); 
						Shape repositionShape = protocolUtil.calculateWidgetInfoShape(canvas,minicwShape, miniwidgetInfoW, miniwidgetInfoH);
						if (repositionShape != minicwShape){
							double x = repositionShape.x() - repositionShape.width() - 32,
								   y = repositionShape.y() - repositionShape.height() - 32;
							if (x < 0) x = 0; if (y < 0) y = 0;
							minicwShape = Rect.from(x,y,repositionShape.width(), repositionShape.height());
						}
						canvas.rect(Pen.PEN_WHITE_ALPHA, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
						canvas.rect(Pen.PEN_BLACK, minicwShape.x(), minicwShape.y(), miniwidgetInfoW, miniwidgetInfoH);
						canvas.text(Pen.PEN_RED, minicwShape.x(), minicwShape.y(), 0, rootText);
						canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 20, 0, idxText);
						canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 40, 0, roleText);
						canvas.text(Pen.PEN_BLUE, minicwShape.x(), minicwShape.y() + 60, 0, widConcreteText);
					}

					if (markParentWidget){
						String cursorWidgetID = cursorWidget.get(Tags.ConcreteID);
						boolean print = !cursorWidgetID.equals(lastPrintParentsOf); 
						if (print){
							lastPrintParentsOf = cursorWidgetID;
							System.out.println("Parents of: " + cursorWidget.get(Tags.Title));
						}
						int lvls = protocolUtil.markParents(canvas,cursorWidget,protocolUtil.ancestorsMarkingColors.keySet().iterator(),0,print);
						if (lvls > 0){
							Shape legendShape = protocolUtil.repositionShape(canvas,Rect.from(cursor.x(), cursor.y(), 110, lvls*25));
							canvas.rect(Pen.PEN_WHITE_ALPHA, legendShape.x(), legendShape.y(), legendShape.width(), legendShape.height());
							canvas.rect(Pen.PEN_BLACK, legendShape.x(), legendShape.y(), legendShape.width(), legendShape.height());
							int shadow = 2;
							String l;
							Iterator<String> it = protocolUtil.ancestorsMarkingColors.keySet().iterator();
							for (int i=0; i<lvls; i++){
								l = it.next();
								Pen lpen = Pen.newPen().setColor(protocolUtil.ancestorsMarkingColors.get(l)).build();
								canvas.text(lpen, legendShape.x() - shadow, legendShape.y() - shadow + i*25, 0, l);
								canvas.text(lpen, legendShape.x() + shadow, legendShape.y() - shadow + i*25, 0, l);
								canvas.text(lpen, legendShape.x() + shadow, legendShape.y() + shadow + i*25, 0, l);
								canvas.text(lpen, legendShape.x() - shadow, legendShape.y() + shadow + i*25, 0, l);
								canvas.text(Pen.PEN_BLACK, legendShape.x()         , legendShape.y() + i*25         , 0, l);								
							}
						}
					}
					int MAX_ANCESTORS_PERLINE = 6;
					double widgetInfoW = canvas.width()/2; //550;
					double widgetInfoH = (1 + Util.size(cursorWidget.tags()) +
										  Util.size(Util.ancestors(cursorWidget)) / MAX_ANCESTORS_PERLINE)
										 * 20;
					cwShape = protocolUtil.calculateWidgetInfoShape(canvas,cwShape, widgetInfoW, widgetInfoH);
					// end by urueda
					
					if(settings().get(ConfigTags.DrawWidgetInfo)){
						//canvas.rect(wpen, cwShape.x(), cwShape.y() - 20, 550, Util.size(cursorWidget.tags()) * 25);
						//canvas.rect(apen, cwShape.x(), cwShape.y() - 20, 550, Util.size(cursorWidget.tags()) * 25);
						// begin by urueda
						canvas.rect(Pen.PEN_WHITE_ALPHA, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);
						canvas.rect(Pen.PEN_BLACK, cwShape.x(), cwShape.y(), widgetInfoW, widgetInfoH);
						// end by urueda
						
						//canvas.text(Pen.PEN_RED, cwShape.x(), cwShape.y(), 0, "Role: " + cursorWidget.get(Role, Roles.Widget).toString());
						//canvas.text(Pen.PEN_RED, cwShape.x(), cwShape.y() - 20, 0, "Path: " + Util.indexString(cursorWidget));
						int pos = -20;
						StringBuilder sb = new StringBuilder();
						sb.append("Ancestors: ");

						//for(Widget p : Util.ancestors(cursorWidget))
						//	sb.append("::").append(p.get(Role, Roles.Widget));							
						//canvas.text(apen, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
						// begin by urueda (fix too many ancestors)
						int i=0;
						for(Widget p : Util.ancestors(cursorWidget)){
							sb.append("::").append(p.get(Role, Roles.Widget));
							i++;
							if (i >= MAX_ANCESTORS_PERLINE){
								canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
								i=0;
								sb = new StringBuilder();
								sb.append("\t");
							}
						}
						if (i > 0)
							canvas.text(Pen.PEN_BLACK, cwShape.x(), cwShape.y() + (pos+=20), 0, sb.toString());
						// end by urueda
						
						for(Tag<?> t : cursorWidget.tags()){
							canvas.text((t.isOneOf(Tags.Role,Tags.Title,Tags.Shape,Tags.Enabled,Tags.Path,Tags.ConcreteID)) ? Pen.PEN_RED : Pen.PEN_BLACK,
										 cwShape.x(), cwShape.y() + (pos+=20), 0, t.name() + ":   " + Util.abbreviate(Util.toString(cursorWidget.get(t)), 50, "..."));
							// begin by urueda (multi-line display without abbreviation)
							/*final int MAX_TEXT = 50;
							String text = Util.abbreviate(Util.toString(cursorWidget.get(t)), Integer.MAX_VALUE, "NO_SENSE");
							int fragment = 0, limit;
							while (fragment < text.length()){
								limit = fragment + MAX_TEXT > text.length() ? text.length() : fragment + MAX_TEXT;
								canvas.text((t.equals(Tags.Title) || t.equals(Tags.Role)) ? rpen : apen, cwShape.x(), cwShape.y() + (pos+=20), 0, t.name() + ":   " +
									text.substring(fragment,limit));
								fragment = limit;
							}*/
							// end by urueda
						}
					}
					// begin by urueda
					if (settings().get(ConfigTags.DrawWidgetTree)){
						canvas.rect(Pen.PEN_BLACK_ALPHA, 0, 0, canvas.width(), canvas.height());
						protocolUtil.drawWidgetTree(system,canvas,12,12,rootW,cursorWidget,16);						
					}
					if (settings().get(ConfigTags.GraphsActivated) && this.delay != Double.MIN_VALUE){ // slow motion?
						canvas.rect(Pen.PEN_BLACK_ALPHA, 0, 0, canvas.width(), canvas.height());
						IEnvironment env = Grapher.getEnvironment();
						IGraphState gs = env.get(state);
						String wid = cursorWidget.get(Tags.ConcreteID);
						String graphDebug = "Widget <" + wid + "> count = " + gs.getStateWidgetsExecCount().get(wid);
						canvas.text(Pen.PEN_WHITE_TEXT_12px, 10, 10, 0, graphDebug);
					}
					// end by urueda
				}
			}
		}
	}

	// by urueda
	private int getTargetZindex(State state, Action a){
		try{
			String targetID = a.get(Tags.TargetID);
			Widget w;
			if (targetID != null){
				w = getWidget(state,targetID);
				if (w != null)
					return (int)w.get(Tags.ZIndex).doubleValue();
			}
		} catch(NoSuchTagException ex){}
		return 1; // default
	}
	
	protected void visualizeActions(Canvas canvas, State state, Set<Action> actions){
		if((mode() == Modes.Spy ||
			mode() == Modes.GenerateManual || // by urueda
			mode() == Modes.GenerateDebug) && settings().get(ConfigTags.VisualizeActions)){
			// begin by urueda
			IEnvironment env = Grapher.getEnvironment();
			int zindex, minz = Integer.MAX_VALUE, maxz = Integer.MIN_VALUE;
			Map<Action,Integer> zindexes = new HashMap<Action,Integer>();
			// end by urueda
			for(Action a : actions){
				//a.get(Visualizer, Util.NullVisualizer).run(state, canvas, Pen.PEN_IGNORE);
				// begin by urueda
				zindex = getTargetZindex(state,a);
				zindexes.put(a, new Integer(zindex));
				if (zindex < minz)
					minz = zindex;
				if (zindex > maxz)
					maxz = zindex;
			}
			int alfa;
			for(Action a : actions){
				zindex = 1; // default
				Pen vp = Pen.PEN_IGNORE;
				if (env != null){ // graphs enabled
					Integer widgetExeCount = env.get(state).getStateWidgetsExecCount().get(env.get(a).getTargetWidgetID());
					if (widgetExeCount != null && widgetExeCount.intValue() > 0)
						vp = Pen.newPen().setColor(Pen.darken(Color.from(0,0,255,255),1.0/(1 + (widgetExeCount.intValue()/10)))).build(); // mark executed widgets with a different color
					else{
						zindex = zindexes.get(a).intValue();
						if (minz == maxz || zindex == maxz)
							alfa = 255;							
						else if (zindex == minz)
							alfa = 64;
						else
							alfa = 128;
						vp = Pen.newPen().setColor(Pen.darken(Color.from(0,255,0,alfa),1.0)).build(); // color depends on widgets zindex
					}
				}
				a.get(Visualizer, Util.NullVisualizer).run(state, canvas, vp);
			}
			// end by urueda
		}
	}

	private void visualizeSelectedAction(Canvas canvas, State state, Action action){
		if(mode() == Modes.GenerateDebug || mode() == Modes.ReplayDebug){
			Pen redPen = Pen.newPen().setColor(Color.Red).setFillPattern(FillPattern.Solid).setStrokeWidth(20).build();
			Visualizer visualizer = action.get(Visualizer, Util.NullVisualizer);
			//final int BLINK_COUNT = 3;
			//final double BLINK_DELAY = 0.5;
			// begin by urueda
			double actionDuration = settings.get(ConfigTags.ActionDuration);
			final int BLINK_COUNT = 3;
			final double BLINK_DELAY = actionDuration / BLINK_COUNT;			
			// end by urueda
			for(int i = 0; i < BLINK_COUNT; i++){
				Util.pause(BLINK_DELAY);
				canvas.begin();
				visualizer.run(state, canvas, Pen.PEN_IGNORE);
				canvas.end();
				Util.pause(BLINK_DELAY);
				canvas.begin();
				visualizer.run(state, canvas, redPen);
				canvas.end();
			}
		}
	}
    
    // by urueda
	protected Action selectAction(State state, Set<Action> actions){
		Assert.isTrue(actions != null && !actions.isEmpty());
		if (this.forceKillProcess != null){
			LogSerialiser.log("Forcing kill-process <" + this.forceKillProcess + "> action\n", LogSerialiser.LogLevel.Info);
			Action a = KillProcess.byName(this.forceKillProcess, 0);
			a.set(Tags.Desc, "Kill Process with name '" + this.forceKillProcess + "'");
			CodingManager.buildIDs(state, a);
			this.forceKillProcess = null;
			return a;
		} else if (this.forceToForeground){
			LogSerialiser.log("Forcing SUT activation (bring to foreground) action\n", LogSerialiser.LogLevel.Info);
			Action a = new ActivateSystem();
			a.set(Tags.Desc, "Bring the system to the foreground.");
			CodingManager.buildIDs(state, a);
			this.forceToForeground = false;
			return a;
		} else if (this.forceNextActionESC){
			LogSerialiser.log("Forcing ESC action\n", LogSerialiser.LogLevel.Info);
			Action a = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
			CodingManager.buildIDs(state, a);
			this.forceNextActionESC = false;
			return a;
		} else
			return Grapher.selectAction(state,actions);
	}
	
	final static double MAX_ACTION_WAIT_FRAME = 1.0; // by urueda (seconds)
	
	protected boolean executeAction(SUT system, State state, Action action){
		double waitTime = settings.get(ConfigTags.TimeToWaitAfterAction);
		 try{
			// begin by urueda
			double halfWait = waitTime == 0 ? 0.01 : waitTime / 2.0; // seconds
			Util.pause(halfWait); // help for a better match of the state' actions visualization
			// end by urueda
			action.run(system, state, settings.get(ConfigTags.ActionDuration));
			// begin by urueda
			int waitCycles = (int) (MAX_ACTION_WAIT_FRAME / halfWait);
			long actionCPU;
			do {
				long CPU1[] = NativeLinker.getCPUsage(system);
				Util.pause(halfWait);
				long CPU2[] = NativeLinker.getCPUsage(system);
				actionCPU = ( CPU2[0] + CPU2[1] - CPU1[0] - CPU1[1] );
				waitCycles--;
			} while (actionCPU > 0 && waitCycles > 0);
			// end by urueda
			return true;
		}catch(ActionFailedException afe){
			return false;
		}
	}

	// note /by urueda): could be more interesting as XML (instead of Java Serialisation)
	private void saveStateSnapshot(State state){
		try{
			if(saveStateSnapshot){
				//System.out.println(Utils.treeDesc(state, 2, Tags.Role, Tags.Desc, Tags.Shape, Tags.Blocked));
				Taggable taggable = new TaggableBase();
				taggable.set(SystemState, state);
				LogSerialiser.log("Saving state snapshot...\n", LogSerialiser.LogLevel.Debug);
				File file = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir), "state_snapshot");
				ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
				oos.writeObject(taggable);
				oos.close();
				saveStateSnapshot = false;
				LogSerialiser.log("Saved state snapshot to " + file.getAbsolutePath() + "\n", LogSerialiser.LogLevel.Info);
			}
		}catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
	}
	
	// by urueda
	private Action mapUserEvent(State state){
		Assert.notNull(userEvent);		
		if (userEvent[0] instanceof MouseButtons){ // mouse events
			double x = ((Double)userEvent[1]).doubleValue();
			double y = ((Double)userEvent[2]).doubleValue();	
			Widget w = null;
			try {
				w = Util.widgetFromPoint(state, x, y);
				x = 0.5; y = 0.5;
		        if (userEvent[0] == MouseButtons.BUTTON1) // left click
		        	return (new AnnotatingActionCompiler()).leftClickAt(w,x,y);
		        else if (userEvent[0] == MouseButtons.BUTTON3) // right click     
		        	return (new AnnotatingActionCompiler()).rightClickAt(w,x,y);
			} catch (WidgetNotFoundException we){
				System.out.println("Mapping user event ... widget not found @(" + x + "," + y + ")");
				return null;
			}
		} else if (userEvent[0] instanceof KBKeys) // key events
			return (new AnnotatingActionCompiler()).hitKey((KBKeys)userEvent[0]);
		else if (userEvent[0] instanceof String){ // type events
			if (lastExecutedAction == null)
				return null;
			List<Finder> targets = lastExecutedAction.get(Tags.Targets,null);
			if (targets == null || targets.size() != 1)
				return null;
			try {
				Widget w = targets.get(0).apply(state);
				return (new AnnotatingActionCompiler()).clickTypeInto(w,(String)userEvent[0]);
			} catch (WidgetNotFoundException we){
				return null;
			}
		}
			
		return null;
	}
	
	/**
	 * Action execution listeners override.
	 * @param system
	 * @param state
	 * @param action
	 */
	protected abstract void actionExecuted(SUT system, State state, Action action);
	
	// by urueda
	private boolean isESC(Action action){
		Role r = action.get(Tags.Role, null);
		if (r != null && r.isA(ActionRoles.HitKey)){
			String desc = action.get(Tags.Desc, null);
			if (desc != null && desc.contains("VK_ESCAPE"))
				return true;
		}
		return false;
	}
	
	// by urueda
	private boolean isNOP(Action action){
		String as = action.toString();
		if (as != null && as.equals(NOP.NOP_ID))
			return true;
		else
			return false;
	}
	
	// begin by urueda
	private long stampLastExecutedAction = -1;
	private long[] lastCPU; // user x system x frame
	private int escAttempts = 0,
				nopAttempts = 0;
	private static final int MAX_ESC_ATTEMPTS = 99,
							 MAX_NOP_ATTEMPTS = 99;
	private static final long NOP_WAIT_WINDOW = 100; // ms
	private double sutRAMbase, sutRAMpeak, sutCPUpeak, testRAMpeak, testCPUpeak;
	// end by urueda
	
	/**
	 * Waits for an user UI action.
	 * Requirement: Mode must be GenerateManual.
	 * @author urueda
	 */
	private void waitUserActionLoop(Canvas cv, SUT system, State state, ActionStatus actionStatus){
		while (mode() == Modes.GenerateManual && !actionStatus.isUserEventAction()){
			if (userEvent != null){
				actionStatus.setAction(mapUserEvent(state));
				actionStatus.setUserEventAction((actionStatus.getAction() != null));
				userEvent = null;
			}
			synchronized(this){
				try {
					this.wait(10);
				} catch (InterruptedException e) {}
			}
			//cv.begin();
			//Util.clear(cv);
			visualizeState(cv, state, system);
			Set<Action> actions = deriveActions(system,state);
			CodingManager.buildIDs(state, actions);;
			visualizeActions(cv, state, actions);
			//cv.end();
		}		
	}
	
	/**
	 * Waits for an event (UI action) from adhoc-test.
	 * @param state
	 * @param actionStatus
	 * @return 'true' if problems were found.
	 * @author urueda
	 */
	private boolean waitAdhocTestEventLoop(State state, ActionStatus actionStatus){
		while(protocolUtil.adhocTestServerReader == null || protocolUtil.adhocTestServerWriter == null){
			synchronized(this){
				try {
					this.wait(10);
				} catch (InterruptedException e) {}
			}
		}
		int adhocTestInterval = 10; // ms
		while (System.currentTimeMillis() < stampLastExecutedAction + adhocTestInterval){
			synchronized(this){
				try {
					this.wait(adhocTestInterval - System.currentTimeMillis() + stampLastExecutedAction + 1);
				} catch (InterruptedException e) {}
			}
		}
		do{
			System.out.println("AdhocTest waiting for event ...");
			try{
				protocolUtil.adhocTestServerWriter.write("READY\r\n");
				protocolUtil.adhocTestServerWriter.flush();
			} catch (Exception e){
				return true; // AdhocTest client disconnected?
			}
			try{
				String socketData = protocolUtil.adhocTestServerReader.readLine().trim(); // one event per line
				System.out.println("\t... AdhocTest event = " + socketData);
				userEvent = protocolUtil.compileAdhocTestServerEvent(socketData); // hack into userEvent
				if (userEvent == null){
					protocolUtil.adhocTestServerWriter.write("???\r\n"); // not found
					protocolUtil.adhocTestServerWriter.flush();									
				}else{
					actionStatus.setAction(mapUserEvent(state));
					if (actionStatus.getAction() == null){
						protocolUtil.adhocTestServerWriter.write("404\r\n"); // not found
						protocolUtil.adhocTestServerWriter.flush();
					}
				}
				userEvent = null;
			} catch (Exception e){
				userEvent = null;
				return true; // AdhocTest client disconnected?
			}
		} while (actionStatus.getAction() == null);
		CodingManager.buildIDs(state, actionStatus.getAction());
		return false;
	}
	
	/**
	 * Waits for an automatically selected UI action.
	 * @param system
	 * @param state
	 * @param fragment
	 * @param actionStatus
	 * @return
	 * @author urueda
	 */
	private boolean waitAutomaticAction(SUT system, State state, Taggable fragment, ActionStatus actionStatus){
		Set<Action> actions = deriveActions(system, state);
		CodingManager.buildIDs(state,actions);
		
		if(actions.isEmpty()){
			if (mode() != Modes.Spy && escAttempts >= MAX_ESC_ATTEMPTS){ // by urueda
				LogSerialiser.log("No available actions to execute! Tryed ESC <" + MAX_ESC_ATTEMPTS + "> times. Stopping sequence generation!\n", LogSerialiser.LogLevel.Critical);
				actionStatus.setProblems(true); // problems found
			}
			//----------------------------------
			// THERE MUST ALMOST BE ONE ACTION!
			//----------------------------------
			// if we did not find any actions, then we just hit escape, maybe that works ;-)
			Action escAction = new AnnotatingActionCompiler().hitKey(KBKeys.VK_ESCAPE);
			CodingManager.buildIDs(state, escAction);
			actions.add(escAction);
			escAttempts++;
		} else
			escAttempts = 0;
		// end by urueda
		
		fragment.set(ActionSet, actions);
		LogSerialiser.log("Built action set!\n", LogSerialiser.LogLevel.Debug);
		visualizeActions(cv, state, actions);

		if(mode() == Modes.Quit) return actionStatus.isProblems();
		LogSerialiser.log("Selecting action...\n", LogSerialiser.LogLevel.Debug);
		if(mode() == Modes.Spy) return false; // by urueda
		actionStatus.setAction(selectAction(state, actions));
		
		if (actionStatus.getAction() == null){ // by urueda (no suitable actions?)
			nonSuitableAction = true;
			return true; // force test sequence end
		}

		actionStatus.setUserEventAction(false); // by urueda
		
		return false;
	}
	
	// by urueda (refactor run() method)
	 // return: problems?
	private boolean runAction(Canvas cv, SUT system, State state, Taggable fragment){
		ActionStatus actionStatus = new ActionStatus();
		waitUserActionLoop(cv,system,state,actionStatus);

		cv.begin(); Util.clear(cv);
		//visualizeState(cv, state);
		visualizeState(cv, state,system); // by urueda 
		LogSerialiser.log("Building action set...\n", LogSerialiser.LogLevel.Debug);
					
		// begin by urueda
		if (actionStatus.isUserEventAction()){ // user action
			CodingManager.buildIDs(state, actionStatus.getAction());
		} else if (mode() == Modes.AdhocTest){ // adhoc-test action
			if (waitAdhocTestEventLoop(state,actionStatus)){
				cv.end(); // by urueda
				return true; // problems
			}
		} else{ // automatically derived action
			if (waitAutomaticAction(system,state,fragment,actionStatus)){
				cv.end(); // by urueda
				return true; // problems
			} else if (actionStatus.getAction() == null && mode() == Modes.Spy){
				cv.end(); // by urueda
				return false;
			}
		}
		// begin by urueda
		cv.end();
		
		if (actionStatus.getAction() == null)
			return true; // problems
		// end by urueda
		
		if (actionCount == 1 && isESC(actionStatus.getAction())){ // first action in the sequence an ESC?
			System.out.println("First action ESC? Switching to NOP to wait for SUT UI ... " + this.timeElapsed());
			Util.pauseMs(NOP_WAIT_WINDOW); // hold-on for UI to react (e.g. scenario: SUT loading ... logo)
			actionStatus.setAction(new NOP());
			CodingManager.buildIDs(state, actionStatus.getAction());
			nopAttempts++; escAttempts = 0;
		} else
			nopAttempts = 0;
		//System.out.println("Selected action: " + action.toShortString() + " ... count of ESC/NOP = " + escAttempts + "/" + nopAttempts);;
		// end by urueda
		
		LogSerialiser.log("Selected action '" + actionStatus.getAction() + "'.\n", LogSerialiser.LogLevel.Debug);
				
		visualizeSelectedAction(cv, state, actionStatus.getAction());
		
		if(mode() == Modes.Quit) return actionStatus.isProblems();
		
		boolean isTestAction = nopAttempts >= MAX_NOP_ATTEMPTS || !isNOP(actionStatus.getAction()); // by urueda
			
		if(mode() != Modes.Spy){
			// begin by urueda
			String[] actionRepresentation = Action.getActionRepresentation(state,actionStatus.getAction(),"\t");
			int memUsage = NativeLinker.getMemUsage(system);
			if (memUsage < sutRAMbase)
				sutRAMbase = memUsage;
			if (memUsage - sutRAMbase > sutRAMpeak)
				sutRAMpeak = memUsage - sutRAMbase;
			long currentCPU[] = NativeLinker.getCPUsage(system),
				 userms = currentCPU[0] - lastCPU[0],
				 sysms = currentCPU[1] - lastCPU[1],
				 cpuUsage[] = new long[]{ userms, sysms, currentCPU[2]}; // [2] = CPU frame
			lastCPU = currentCPU;
			if (isTestAction)
				Grapher.notify(state,state.get(Tags.ScreenshotPath, null),
							   actionStatus.getAction(),protocolUtil.getActionshot(state,actionStatus.getAction()),actionRepresentation[1],
						   	   memUsage, cpuUsage);
			// end by urueda
			LogSerialiser.log(String.format("Executing (%d): %s...", actionCount,
				actionStatus.getAction().get(Desc, actionStatus.getAction().toString())) + "\n", LogSerialiser.LogLevel.Debug);
			//if((actionSucceeded = executeAction(system, state, action))){
			if (actionStatus.isUserEventAction() ||
				(actionStatus.setActionSucceeded(executeAction(system, state, actionStatus.getAction())))){ // by urueda					
				//logln(String.format("Executed (%d): %s...", actionCount, action.get(Desc, action.toString())), LogLevel.Info);
				// begin by urueda
				cv.begin(); Util.clear(cv); cv.end(); // by urueda (overlay is invalid until new state/actions scan)
				stampLastExecutedAction = System.currentTimeMillis();					
				actionExecuted(system,state,actionStatus.getAction()); // notification
				if (actionStatus.isUserEventAction())
					Util.pause(settings.get(ConfigTags.TimeToWaitAfterAction)); // wait between actions
				double sutCPU = ((cpuUsage[0] + cpuUsage[1]) / (double)cpuUsage[2] * 100);
				if (sutCPU > sutCPUpeak)
					sutCPUpeak = sutCPU;
				String cpuPercent = String.format("%.2f", sutCPU) + "%";
				LogSerialiser.log(String.format("Executed [%d]: %s\n%s",
						actionCount,
						"action = " + actionStatus.getAction().get(Tags.ConcreteID) +
						" (" + actionStatus.getAction().get(Tags.AbstractID) + ") @state = " +
						state.get(Tags.ConcreteID) + " (" + state.get(Tags.Abstract_R_ID) + ")\n\tSUT_KB = " +
						memUsage + ", SUT_ms = " + cpuUsage[0] + " x " + cpuUsage[1] + " x " + cpuPercent,
						actionRepresentation[0]) + "\n",
						LogSerialiser.LogLevel.Info);
				System.out.print(String.format(
						"S[%1$" + (1 + (int)Math.log10((double)settings.get(ConfigTags.Sequences))) + "d=%2$" + (1 + (int)Math.log10((double)generatedSequenceNumber)) + "d]-" + // S = test Sequence
						"A[%3$" + (1 + (int)Math.log10((double)settings().get(ConfigTags.SequenceLength))) + // A = Action
						"d] <%4$3s@%5$3s KCVG>... SR = %6$8d KB / SC = %7$7s ... ", // KCVG = % CVG of Known UI space @ known UI space scale; SR = SUT_RAM; SC = SUT_CPU
						sequenceCount, generatedSequenceNumber, actionCount,
						Grapher.GRAPHS_ACTIVATED ? Grapher.getEnvironment().getExplorationCurveSampleCvg() : -1,
						Grapher.GRAPHS_ACTIVATED ? Grapher.getEnvironment().convertKCVG(Grapher.getEnvironment().getExplorationCurveSampleScale()) : -1,
						memUsage, cpuPercent)); debugResources();
				System.out.print(" ... L/S/T: " + LogSerialiser.queueLength() + "/" + ScreenshotSerialiser.queueLength() + "/" + TestSerialiser.queueLength()); // L/S/T = Log/Scr/Test queues
				//Example: Seq[1]-Action[1] <  0 KCVG>... SUT_RAM =    17292 KB / SUT_CPU =  17.42% ... TESTAR_CPU: 1.550 s / TESTAR_RAM: 491.0 MB ... Log/Scr/Test queues: 2/2/0				
// temp begin
/*if (Grapher.getEnvironment() != null){
	int totalWidgets = 0,
	    totalUnxActions = 0;
	for (IGraphState gs : Grapher.getEnvironment().getGraphStates()){
		totalWidgets += gs.getStateWidgets().size();
		totalUnxActions += gs.getUnexploredActions().size();
	}
	System.out.print("\n\t" + String.format("%7d",totalWidgets) + " x " + String.format("%7d",totalUnxActions) + "\t(widgets x unexplored actions)");
}*/
// temp end
				if (settings().get(ConfigTags.PrologActivated))
					System.out.println(" ... prolog: " + jipWrapper.debugPrologBase());
				else
					System.out.print("\n");
				//logln(Grapher.getExplorationCurveSample(),LogLevel.Info);
				//logln(Grapher.getLongestPath() + "\n",LogLevel.Info);
				if (mode() == Modes.AdhocTest){
					try {
						protocolUtil.adhocTestServerWriter.write("OK\r\n"); // adhoc action executed
						protocolUtil.adhocTestServerWriter.flush();
					} catch (Exception e){} // AdhocTest client disconnected?
				}
				// end by urueda

				if (isTestAction && actionStatus.isActionSucceeded()) // by urueda
					actionCount++;
				fragment.set(ExecutedAction, actionStatus.getAction());
				fragment.set(ActionDuration, settings().get(ConfigTags.ActionDuration));
				fragment.set(ActionDelay, settings().get(ConfigTags.TimeToWaitAfterAction));
				LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
				//oos.writeObject(fragment);
				TestSerialiser.write(fragment); // by urueda
	
				LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
			}else{
				LogSerialiser.log("Execution of action failed!\n");
				try {
					protocolUtil.adhocTestServerWriter.write("FAIL\r\n"); // action execution failed
					protocolUtil.adhocTestServerWriter.flush();
				} catch (Exception e) {} // AdhocTest client disconnected?
			}				
		}
		
		lastExecutedAction = actionStatus.getAction(); // by urueda
		
		if(mode() == Modes.Quit) return actionStatus.isProblems();
		if(!actionStatus.isActionSucceeded()){
			return true;
		}
		
		return actionStatus.isProblems();
	}
	
	// by urueda
	private void debugResources(){
		long nowStamp = System.currentTimeMillis();
		double testRAM =  Runtime.getRuntime().totalMemory()/1048576.0;		
		if (testRAM > testRAMpeak)
			testRAMpeak = testRAM;
		double testCPU = (nowStamp - lastStamp)/1000.0;
		if (testCPU > testCPUpeak)
			testCPUpeak = testCPU;
		System.out.print("TC: " + String.format("%.3f", testCPU) + // TC = TESTAR_CPU
						 " s / TR: " + testRAM + " MB"); // TR = TESTAR_RAM
		lastStamp = nowStamp;
	}

	// by urueda (refactor run() method)
	private void runTest(){		
		// begin by urueda
		LogSerialiser.finish(); LogSerialiser.exit();
		sequenceCount = 1;
		lastStamp = System.currentTimeMillis();
		escAttempts = 0; nopAttempts = 0;
		// end by urueda		
		boolean problems;
		while(mode() != Modes.Quit && moreSequences()){

			String generatedSequence = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir) + File.separator + "sequences", "sequence").getName(); // by urueda
			generatedSequenceNumber = new Integer(generatedSequence.replace("sequence", "")).intValue();
			// begin by urueda

			sutRAMbase = Double.MAX_VALUE; sutRAMpeak = 0.0; sutCPUpeak = 0.0; testRAMpeak = 0.0; testCPUpeak = 0.0;

			try {
				LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
					settings.get(OutputDir) + File.separator + "logs" + File.separator + generatedSequence + ".log")))),
					settings.get(LogLevel));
			} catch (NoSuchTagException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			} catch (FileNotFoundException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
			
			if (mode() == Modes.GenerateManual)
				setMode(Modes.Spy);
			jipWrapper = new JIPrologWrapper();
			
			Grapher.grapher(generatedSequence,
							settings.get(ConfigTags.SequenceLength).intValue(),
							settings.get(ConfigTags.AlgorithmFormsFilling).booleanValue(),
							settings.get(ConfigTags.TypingTextsForExecutedAction).intValue(),
							settings.get(ConfigTags.TestGenerator),
							settings.get(ConfigTags.MaxReward),
							settings.get(ConfigTags.Discount),
							settings.get(ConfigTags.ExplorationSampleInterval),
							settings.get(ConfigTags.GraphsActivated),
							settings.get(ConfigTags.PrologActivated),
							settings.get(ConfigTags.ForceToSequenceLength) && this.forceToSequenceLengthAfterFail ?
									true :
									settings.get(ConfigTags.GraphResuming),
							settings.get(ConfigTags.OfflineGraphConversion),
							settings.get(ConfigTags.Strategy),							
							jipWrapper);
			Grapher.waitEnvironment();
			ScreenshotSerialiser.start(generatedSequence);
			// end by urueda
			
			problems = false;
			if (!forceToSequenceLengthAfterFail) passSeverity = Verdict.SEVERITY_OK; // by urueda
			//actionCount = 0;
			// begin by urueda
			if (this.forceToSequenceLengthAfterFail){
				this.forceToSequenceLengthAfterFail = false;
				this.testFailTimes++;
			} else{
				actionCount = 1;
				this.testFailTimes = 0;
			}
			// end by urueda

			LogSerialiser.log("Creating new sequence file...\n", LogSerialiser.LogLevel.Debug);
			final File currentSeq = new File(settings.get(ConfigTags.TempDir) + File.separator + "tmpsequence");
			try {
				Util.delete(currentSeq);
			} catch (IOException e2) {
				LogSerialiser.log("I/O exception deleting <" + currentSeq + ">\n", LogSerialiser.LogLevel.Critical);
			}
			//oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeq), 50000000));
			//raf = new RandomAccessFile(currentSeq, "rw");
			//oos = new ObjectOutputStream(new FileOutputStream(raf.getFD()));
			// begin by urueda
			try {
				//TestSerialiser.start(new RandomAccessFile(currentSeq, "rw"));
				TestSerialiser.start(new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(currentSeq))));
				LogSerialiser.log("Created new sequence file!\n", LogSerialiser.LogLevel.Debug);
			} catch (IOException e) {
				LogSerialiser.log("I/O exception creating new sequence file\n", LogSerialiser.LogLevel.Critical);
			}
			//} catch (FileNotFoundException e1) {
			//	LogSerialiser.log("File not found exception creating random test file\n", LogSerialiser.LogLevel.Critical);
			//}
			// end by urueda

			LogSerialiser.log("Building canvas...\n", LogSerialiser.LogLevel.Debug);
			//Canvas cv = buildCanvas();
			this.cv = buildCanvas(); // by urueda
			//logln(Util.dateString("dd.MMMMM.yyyy HH:mm:ss") + " Starting system...", LogLevel.Info);
			// begin by urueda
			String startDateString = Util.dateString(DATE_FORMAT);
			LogSerialiser.log(startDateString + " Starting SUT ...\n", LogSerialiser.LogLevel.Info);
			// end by urueda
			
			SUT system = null;
			
			try{ // by urueda

				system = startSystem();

				lastCPU = NativeLinker.getCPUsage(system); // by urueda
				
				//SUT system = WinProcess.fromProcName("firefox.exe");
				//logln("System is running!", LogLevel.Debug);
				LogSerialiser.log("SUT is running!\n", LogSerialiser.LogLevel.Debug); // by urueda
				//logln("Starting sequence " + sequenceCount, LogLevel.Info);
				LogSerialiser.log("Starting sequence " + sequenceCount + " (output as: " + generatedSequence + ")\n\n", LogSerialiser.LogLevel.Info); // by urueda
				beginSequence();
				LogSerialiser.log("Obtaining system state...\n", LogSerialiser.LogLevel.Debug);
				State state = getState(system);
				LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
				saveStateSnapshot(state);
	
				Taggable fragment = new TaggableBase();
				fragment.set(SystemState, state);
							
				Verdict verdict = state.get(OracleVerdict, Verdict.OK);
				if (faultySequence) problems = true;
				fragment.set(OracleVerdict, verdict); // by urueda					
				int waitCycleIdx = 0;
				long[] waitCycles = new long[]{1, 10, 25, 50}; // ms
				long spyCycle = -1;
				String stateID, lastStateID = state.get(Tags.ConcreteID);
				// end by urueda
				while(mode() != Modes.Quit && moreActions(state)){
					if (problems)
						faultySequence = true; // by urueda
					else{
						problems = runAction(cv,system,state,fragment);
						// begin by urueda
						if (mode() == Modes.Spy){
							stateID = state.get(Tags.ConcreteID);
							if (stateID.equals(lastStateID)){
								if (System.currentTimeMillis() - spyCycle > waitCycles[waitCycleIdx]){
									spyCycle = System.currentTimeMillis();
									if (waitCycleIdx < waitCycles.length - 1)
										waitCycleIdx++;
								} else
									continue;
							} else{
								lastStateID = stateID;
								waitCycleIdx = 0;
							}
						}
						// end by urueda
						LogSerialiser.log("Obtaining system state...\n", LogSerialiser.LogLevel.Debug);
						state = getState(system);
						if (faultySequence) problems = true; // by urueda
						LogSerialiser.log("Successfully obtained system state!\n", LogSerialiser.LogLevel.Debug);
						if (mode() != Modes.Spy){ // by urueda
							saveStateSnapshot(state);
							verdict = state.get(OracleVerdict, Verdict.OK);
							fragment.set(OracleVerdict, verdict); // by urueda						
							fragment = new TaggableBase();
							fragment.set(SystemState, state);
						}
					}
				}
	
				//logln("Shutting down system...", LogLevel.Info);
				LogSerialiser.log("Shutting down the SUT...\n", LogSerialiser.LogLevel.Info); // by urueda
				stopSystem(system); // by urueda
				if (system != null && system.isRunning()) // by urueda
					system.stop();
				//logln("System has been shut down!", LogLevel.Debug);
				// begin by urueda
				LogSerialiser.log("... SUT has been shut down!\n", LogSerialiser.LogLevel.Debug);								
				
				ScreenshotSerialiser.finish();
				LogSerialiser.log("Writing fragment to sequence file...\n", LogSerialiser.LogLevel.Debug);
				//oos.writeObject(fragment);
				TestSerialiser.write(fragment);
				TestSerialiser.finish();
				LogSerialiser.log("Wrote fragment to sequence file!\n", LogSerialiser.LogLevel.Debug);
				
				Grapher.walkFinished(!problems,
						 			 mode() == Modes.Spy ? null : state,
						 			 protocolUtil.getStateshot(state));
				
				LogSerialiser.log("Sequence " + sequenceCount + " finished.\n", LogSerialiser.LogLevel.Info);
				if(problems)
					LogSerialiser.log("Sequence contained problems!\n", LogSerialiser.LogLevel.Critical);
								
				finishSequence(currentSeq);
	
				System.out.println("currentseq: " + currentSeq);
				
				Verdict finalVerdict = verdict.join(new Verdict(passSeverity,"",Util.NullVisualizer));
				
				if (!settings().get(ConfigTags.OnlySaveFaultySequences) ||
					finalVerdict.severity() >= settings().get(ConfigTags.FaultThreshold)){ // by urueda{
					//String generatedSequence = Util.generateUniqueFile(settings.get(ConfigTags.OutputDir) + File.separator + "sequences", "sequence").getName();
					LogSerialiser.log("Copying generated sequence (\"" + generatedSequence + "\") to output directory...\n", LogSerialiser.LogLevel.Info);
					try {
						Util.copyToDirectory(currentSeq.getAbsolutePath(),
								settings.get(ConfigTags.OutputDir) + File.separator + "sequences", 
								generatedSequence,
								true); // by urueda
						LogSerialiser.log("Copied generated sequence to output directory!\n", LogSerialiser.LogLevel.Debug);					
					} catch (NoSuchTagException e) {
						LogSerialiser.log("No such tag exception copying test sequence\n", LogSerialiser.LogLevel.Critical);
					} catch (IOException e) {
						LogSerialiser.log("I/O exception copying test sequence\n", LogSerialiser.LogLevel.Critical);
					}
					copyClassifiedSequence(generatedSequence, currentSeq, finalVerdict);
				}
				if(!problems)
					this.forceToSequenceLengthAfterFail = false;
	
				LogSerialiser.log("Releasing canvas...\n", LogSerialiser.LogLevel.Debug);
				cv.release();
				
				saveSequenceMetrics(generatedSequence,problems);
				
				if (ConfigTags.Strategy != null && !ConfigTags.Strategy.equals("")){
					System.out.println("It's a strategy test generator");
					saveStrategyMetrics(generatedSequence,problems);
				}
				
				ScreenshotSerialiser.exit(); final String[] report = Grapher.getReport(); // screenshots must be serialised
				TestSerialiser.exit();
				String stopDateString =  Util.dateString(DATE_FORMAT),
					   durationDateString = Util.diffDateString(DATE_FORMAT, startDateString, stopDateString);
				LogSerialiser.log("TESTAR stopped execution at " + stopDateString + "\n", LogSerialiser.LogLevel.Critical);
				LogSerialiser.log("Test duration was " + durationDateString + "\n", LogSerialiser.LogLevel.Critical);
				LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();

				// save report
				this.saveReportPage(generatedSequence, "clusters", report[0]);
				this.saveReportPage(generatedSequence, "testable", report[1]);
				this.saveReportPage(generatedSequence, "curve", report[2]);
				this.saveReportPage(generatedSequence, "stats", report[3]);
				// end by urueda
				
				sequenceCount++;
				
			// begin by urueda
			} catch(Exception e){
				this.killTestLaunchedProcesses();
				ScreenshotSerialiser.finish();
				TestSerialiser.finish();				
				Grapher.walkFinished(false, null, null);
				ScreenshotSerialiser.exit(); LogSerialiser.log(Grapher.getReport() + "\n", LogSerialiser.LogLevel.Info); // screenshots must be serialised
				LogSerialiser.log("Exception <" + e.getMessage() + "> has been caught\n", LogSerialiser.LogLevel.Critical); // screenshots must be serialised
				int i=1; StringBuffer trace = new StringBuffer();
				for(StackTraceElement t : e.getStackTrace())
				   trace.append("\n\t[" + i++ + "] " + t.toString());
				System.out.println("Exception <" + e.getMessage() + "> has been caught; Stack trace:" + trace.toString());
				if (system != null)
					system.stop();
				TestSerialiser.exit();
				LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();
				this.mode = Modes.Quit; // System.exit(1);
			}
		}
		if (settings().get(ConfigTags.ForceToSequenceLength).booleanValue() &&  // force a test sequence length in presence of FAIL
				this.actionCount <= settings().get(ConfigTags.SequenceLength) && mode() != Modes.Quit){
			this.forceToSequenceLengthAfterFail = true;
			System.out.println("Resuming test after FAIL at action number <" + this.actionCount + ">");
 			runTest(); // continue testing
		} else
			this.forceToSequenceLengthAfterFail = false;			
		// end by urueda
	}

	// by urueda
	private void saveReportPage(String generatedSequence, String fileSuffix, String page){
		try {
			LogSerialiser.start(new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
				settings.get(OutputDir) + File.separator + "logs" + File.separator + generatedSequence + "_" + fileSuffix + ".log")))),
				settings.get(LogLevel));
		} catch (NoSuchTagException e3) {
			e3.printStackTrace();
		} catch (FileNotFoundException e3) {
			e3.printStackTrace();
		}
		LogSerialiser.log(page, LogSerialiser.LogLevel.Critical);
		LogSerialiser.flush(); LogSerialiser.finish(); LogSerialiser.exit();
	}
	
	// by urueda
	private void copyClassifiedSequence(String generatedSequence, File currentSeq, Verdict verdict){
		String targetFolder = "";
		double sev = verdict.severity();
		if (sev == Verdict.SEVERITY_OK)
			targetFolder = "sequences_ok";
		else if (sev ==  Verdict.SEVERITY_WARNING)
			targetFolder = "sequences_warning";
		else if (sev == Verdict.SEVERITY_SUSPICIOUS_TITLE)
			targetFolder = "sequences_suspicioustitle";
		else if (sev == Verdict.SEVERITY_NOT_RESPONDING)
			targetFolder = "sequences_unresponsive";
		else if (sev == Verdict.SEVERITY_NOT_RUNNING)
			targetFolder = "sequences_unexpectedclose";
		else if (sev == Verdict.SEVERITY_FAIL)
			targetFolder = "sequencces_fail";
		else
			targetFolder = "sequences_other";
		LogSerialiser.log("Copying classified sequence (\"" + generatedSequence + "\") to " + targetFolder + " folder...\n", LogSerialiser.LogLevel.Info);
		try {
			Util.copyToDirectory(currentSeq.getAbsolutePath(), 
					settings.get(ConfigTags.OutputDir) + File.separator + targetFolder, 
					generatedSequence,
					true);
		} catch (NoSuchTagException e) {
			LogSerialiser.log("No such tag exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
		} catch (IOException e) {
			LogSerialiser.log("I/O exception copying classified test sequence\n", LogSerialiser.LogLevel.Critical);
		}
		LogSerialiser.log("Copied classified sequence to output <" + targetFolder + "> directory!\n", LogSerialiser.LogLevel.Debug);		
	}
	
	// by urueda
	private void saveSequenceMetrics(String testSequenceName, boolean problems){
		if (Grapher.GRAPHS_ACTIVATED){
			try {
				PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
					settings.get(OutputDir) + File.separator + "metrics" + File.separator + (problems ? "fail_" : "") + testSequenceName + ".csv"))));
				String heading = String.format("%1$7s,%2$5s,%3$9s,%4$8s,%5$7s,%6$12s,%7$15s,%8$13s,%9$12s,%10$10s,%11$9s,%12$11s,%13$10s,%14$7s",
					"verdict",	 // test verdict
					"FAILS",	 // test FAIL count
					"minCvg(%)", // min coverage
					"maxCvg(%)", // max coverage
					"maxpath",	 // longest path
					"graph-states",	 // graph states
					"abstract-states", // abstract states
					"graph-actions", // graph actions
					"test-actions", // test actions
					"SUTRAM(KB)",	 // SUT RAM peak 
					"SUTCPU(%)",	 // SUT CPU peak
					"TestRAM(MB)",	 // TESTAR RAM peak
					"TestCPU(s)",	 // TESTAR CPU peak
					"fitness"		 // fitness
					);
				ps.println(heading);
				IEnvironment env = Grapher.getEnvironment();
				double[] cvgMetrics = env.getCoverageMetrics();
				final int VERDICT_WEIGHT = 	1000,
						  CVG_WEIGHT = 		  10,
						  PATH_WEIGHT = 	 100,
						  STATES_WEIGHT = 	  10,
						  ACTIONS_WEIGHT = 	1000,
						  SUT_WEIGHT = 		   1,
						  TEST_WEIGHT = 	1000;
				double fitness = 1 / // 0.0 (best) .. 1.0 (worse)
					((problems ? 1 : 0) * VERDICT_WEIGHT +
					 cvgMetrics[0] + cvgMetrics[1] * CVG_WEIGHT +
					 env.getLongestPathLength() * PATH_WEIGHT +
					 (env.getGraphStates().size() - 2) * STATES_WEIGHT +
					 (1 / (env.getGraphActions().size() + 1) * ACTIONS_WEIGHT) + // avoid division by 0
					 (sutRAMpeak + sutCPUpeak) * SUT_WEIGHT +
					 (1 / (1 + testRAMpeak + testCPUpeak*1000)) * TEST_WEIGHT // avoid division by 0
					);
				String metrics = String.format("%1$7s,%2$5s,%3$9s,%4$9s,%5$7s,%6$12s,%7$15s,%8$13s,%9$12s,%10$10s,%11$9s,%12$11s,%13$10s,%14$7s",
					(problems ? "FAIL" : "PASS"),		  // verdict
					this.testFailTimes,					  // test FAIL count
					String.format("%.2f", cvgMetrics[0]), // min coverage);
					String.format("%.2f", cvgMetrics[1]), // max coverage
					env.getLongestPathLength(), 			  // longest path
					env.getGraphStates().size() - 2,	  // graph states
					env.getGraphStateClusters().size(),	  // abstract states
					env.getGraphActions().size() - 2,	  // graph actions
					this.actionCount - 1,                 // test actions
					sutRAMpeak,						  	  // SUT RAM peak
					String.format("%.2f",sutCPUpeak),	  // SUT CPU peak
					testRAMpeak,						  // TESTAR RAM peak
					String.format("%.3f",testCPUpeak), 	  // TESTAR CPU peak
					fitness		  // fitness
					);
				ps.print(metrics);
				ps.close();
				System.out.println(heading + "\n" + metrics);
			} catch (NoSuchTagException e) {
				LogSerialiser.log("Metrics serialisation exception" + e.getMessage(), LogSerialiser.LogLevel.Critical);
			} catch (FileNotFoundException e) {
				LogSerialiser.log("Metrics serialisation exception" + e.getMessage(), LogSerialiser.LogLevel.Critical);
			}
		}
	}
	
	private void saveStrategyMetrics(String testSequenceName, boolean problems){
		if (Grapher.GRAPHS_ACTIVATED){
			try {
				String filename;
				if (System.getProperty("Dcounter") == null){
					filename = "ecj_"+testSequenceName;
				} else {
					filename = "ecj_sequence"+System.getProperty("Dcounter");
				}
				
				PrintStream ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(
					settings.get(OutputDir) + File.separator + "metrics" + File.separator + filename + ".csv"))));
				String heading = String.format("%1$7s,%2$5s,%3$9s,%4$8s,%5$7s,%6$12s,%7$15s,%8$13s,%9$12s,%10$10s,%11$9s,%12$11s,%13$10s,%14$14s",
					"verdict",	 // test verdict
					"FAILS",	 // test FAIL count
					"minCvg(%)", // min coverage
					"maxCvg(%)", // max coverage
					"maxpath",	 // longest path
					"graph-states",	 // graph states
					"abstract-states", // abstract states
					"graph-actions", // graph actions
					"test-actions", // test actions
					"SUTRAM(KB)",	 // SUT RAM peak 
					"SUTCPU(%)",	 // SUT CPU peak
					"TestRAM(MB)",	 // TESTAR RAM peak
					"TestCPU(s)",	 // TESTAR CPU peak
					"random-actions"	//Number of random actions instead of selected by strategy 
					);
				ps.println(heading);
				IEnvironment env = Grapher.getEnvironment();
				double[] cvgMetrics = env.getCoverageMetrics();
				TreeMap<String, Double> strategyMetrics = env.getStrategyMetrics();
				String metrics = String.format("%1$7s,%2$5s,%3$9s,%4$9s,%5$7s,%6$12s,%7$15s,%8$13s,%9$12s,%10$10s,%11$9s,%12$11s,%13$10s,%14$7s",
					(problems ? "FAIL" : "PASS"),		  // verdict
					this.testFailTimes,					  // test FAIL count
					String.format("%.2f", cvgMetrics[0]), // min coverage);
					String.format("%.2f", cvgMetrics[1]), // max coverage
					env.getLongestPathLength(), 			  // longest path
					env.getGraphStates().size() - 2,	  // graph states
					env.getGraphStateClusters().size(),	  // abstract states
					env.getGraphActions().size() - 2,	  // graph actions
					this.actionCount - 1,                 // test actions
					sutRAMpeak,						  	  // SUT RAM peak
					String.format("%.2f",sutCPUpeak),	  // SUT CPU peak
					testRAMpeak,						  // TESTAR RAM peak
					String.format("%.3f",testCPUpeak), 	  // TESTAR CPU peak
					strategyMetrics.get("randomactions")  // Random actions
					);
				ps.print(metrics);
				ps.close();
				System.out.println(heading + "\n" + metrics);
			} catch (NoSuchTagException e) {
				LogSerialiser.log("Metrics serialisation exception" + e.getMessage(), LogSerialiser.LogLevel.Critical);
			} catch (FileNotFoundException e) {
				LogSerialiser.log("Metrics serialisation exception" + e.getMessage(), LogSerialiser.LogLevel.Critical);
			}
		}
	}
	
	public final void run(final Settings settings) {		
		startTime = Util.time();
		this.settings = settings;
		mode = settings.get(ConfigTags.Mode);
		initialize(settings);
		eventHandler = new EventHandler(this); // by urueda
		
		try {
			if (!settings.get(ConfigTags.UnattendedTests).booleanValue()){ // by urueda
				LogSerialiser.log("Registering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
				// begin by urueda
				if (GlobalScreen.isNativeHookRegistered())
					GlobalScreen.unregisterNativeHook();
				Logger.getLogger(GlobalScreen.class.getPackage().getName()).setLevel(Level.FINEST); //Level.SEVERE
				// end by urueda
				GlobalScreen.registerNativeHook();
				//GlobalScreen.getInstance().addNativeKeyListener(this);
				GlobalScreen.getInstance().addNativeKeyListener(eventHandler); // by urueda (refactored)
				//GlobalScreen.getInstance().addNativeMouseListener(this);
				GlobalScreen.getInstance().addNativeMouseListener(eventHandler); // by urueda (refactored)
				GlobalScreen.getInstance().addNativeMouseMotionListener(eventHandler); // by urueda
				LogSerialiser.log("Successfully registered keyboard and mouse hooks!\n", LogSerialiser.LogLevel.Debug);
			}

			LogSerialiser.log("'" + mode() + "' mode active.\n", LogSerialiser.LogLevel.Info);

			if(mode() == Modes.View){
				new SequenceViewer(settings).run();
			}else if(mode() == Modes.Replay || mode() == Modes.ReplayDebug){
				replay();
			}else if(mode() == Modes.Generate || mode() == Modes.Spy || mode() == Modes.GenerateDebug){
				runTest();
			}
		} catch (NativeHookException e) {
			LogSerialiser.log("Unable to install keyboard and mouse hooks!\n", LogSerialiser.LogLevel.Critical);
			throw new RuntimeException("Unable to install keyboard and mouse hooks!", e);
		}finally{
			try{
				if (!settings.get(ConfigTags.UnattendedTests).booleanValue()){ // by urueda
					if (GlobalScreen.isNativeHookRegistered()){
						LogSerialiser.log("Unregistering keyboard and mouse hooks\n", LogSerialiser.LogLevel.Debug);
						GlobalScreen.getInstance().removeNativeMouseMotionListener(eventHandler);
						GlobalScreen.getInstance().removeNativeMouseListener(eventHandler);
						GlobalScreen.getInstance().removeNativeKeyListener(eventHandler);
						GlobalScreen.unregisterNativeHook();
					}
				}
				protocolUtil.stopAdhocServer(); // by urueda
			}catch(Exception e){
				e.printStackTrace(); // by urueda
			}
		}
	}

	private void replay(){
		// begin by urueda
		boolean graphsActivated = Grapher.GRAPHS_ACTIVATED,
				prologActivated = Grapher.PROLOG_ACTIVATED;
		Grapher.GRAPHS_ACTIVATED = false;
		Grapher.PROLOG_ACTIVATED = false;
		actionCount = 1;
		// end by urueda
		boolean success = true;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		GZIPInputStream gis = null;
		ObjectInputStream ois = null;
		SUT system = startSystem();
		try{
			File seqFile = new File(settings.get(ConfigTags.PathToReplaySequence));
			//ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(seqFile)));
			// begin by urueda
			fis = new FileInputStream(seqFile);
			bis = new BufferedInputStream(fis);
			gis = new GZIPInputStream(bis);
			ois = new ObjectInputStream(gis);
			// end by urueda

			Canvas cv = buildCanvas();
			State state = getState(system);

			String replayMessage;
			
			double rrt = settings.get(ConfigTags.ReplayRetryTime);
			
			while(success && mode() != Modes.Quit){
				Taggable fragment;
				try{
					fragment = (Taggable) ois.readObject();
				} catch(IOException ioe){
					success = true;
					break;
				}

				success = false;
				int tries = 0;
				double start = Util.time();

				while(!success && (Util.time() - start < rrt)){
					tries++;
					cv.begin(); Util.clear(cv);
					//visualizeState(cv, state);
					visualizeState(cv, state, system); // by urueda
					cv.end();

					if(mode() == Modes.Quit) break;
					Action action = fragment.get(ExecutedAction, new NOP());
					visualizeSelectedAction(cv, state, action);
					if(mode() == Modes.Quit) break;

					double actionDuration = settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay) ? fragment.get(Tags.ActionDuration, 0.0) : settings.get(ConfigTags.ActionDuration);
					double actionDelay = settings.get(ConfigTags.UseRecordedActionDurationAndWaitTimeDuringReplay) ? fragment.get(Tags.ActionDelay, 0.0) : settings.get(ConfigTags.TimeToWaitAfterAction);

					try{
						if(tries < 2){
							// begin by urueda
							replayMessage = String.format("Trying to execute (%d): %s... [time window = " + rrt + "]", actionCount, action.get(Desc, action.toString()));
							System.out.println(replayMessage);
							// end by urueda
							LogSerialiser.log(replayMessage, LogSerialiser.LogLevel.Info);
						 }else{
							if(tries % 50 == 0)
								LogSerialiser.log(".\n", LogSerialiser.LogLevel.Info);
							else
								LogSerialiser.log(".", LogSerialiser.LogLevel.Info);
						}

						action.run(system, state, actionDuration);
						success = true;
						actionCount++;
						LogSerialiser.log("Success!\n", LogSerialiser.LogLevel.Info);
					} catch(ActionFailedException afe){}

					Util.pause(actionDelay);

					if(mode() == Modes.Quit) break;
					state = getState(system);
				}

			}

			cv.release();
			//ois.close();
			stopSystem(system); // by urueda
			if (system != null && system.isRunning()) // by urueda
				system.stop();

		} catch(IOException ioe){
			throw new RuntimeException("Cannot read file.", ioe);
		} catch (ClassNotFoundException cnfe) {
			throw new RuntimeException("Cannot read file.", cnfe);
		} finally {
			if (ois != null){
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (gis != null){
				try { gis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (bis != null){
				try { bis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (fis != null){
				try { fis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if (cv != null)
				cv.release();
			if (system != null)
				system.stop();			
		}

		if(success){
			String msg = "Sequence successfully replayed!\n";
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Info);
			
		} else{
			String msg = "Failed to replay sequence.\n";
			System.out.println(msg);
			LogSerialiser.log(msg, LogSerialiser.LogLevel.Critical);
		}
		// begin by urueda
		LogSerialiser.finish();
		Grapher.GRAPHS_ACTIVATED = graphsActivated;
		Grapher.PROLOG_ACTIVATED = prologActivated;
		// end by urueda		
	}
	
	// by urueda
	protected Widget getWidget(State state, String concreteID){
		for (Widget w : state){
			if (w.get(Tags.ConcreteID).equals(concreteID)){
				return w;
			}
		}
		return null;
	}
	
}
