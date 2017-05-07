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
package org.fruit.alayer.actions;

import org.fruit.Util;
import org.fruit.alayer.Abstractor;
import org.fruit.alayer.Action;
import org.fruit.alayer.Color;
import org.fruit.alayer.FillPattern;
import org.fruit.alayer.OrthogonalPosition;
import org.fruit.alayer.Pen;
import org.fruit.alayer.Position;
import org.fruit.alayer.Rect;
import org.fruit.alayer.Shape;
import org.fruit.alayer.StrokeCaps;
import org.fruit.alayer.Tags;
import org.fruit.alayer.Widget;
import org.fruit.alayer.devices.KBKeys;
import org.fruit.alayer.visualizers.EllipseVisualizer;
import org.fruit.alayer.visualizers.ShapeVisualizer;
import org.fruit.alayer.visualizers.TextVisualizer;
import org.fruit.alayer.visualizers.TrajectoryVisualizer;

public class AnnotatingActionCompiler extends StdActionCompiler {
	
	// begin by urueda
	private static final Pen MovePen = Pen.newPen().setColor(Color.Aqua)
			.setFillPattern(FillPattern.None).setStrokeWidth(3).build();
	// end by urueda
	private static final Pen LClickPen = Pen.newPen().setColor(Color.Green)
			.setFillPattern(FillPattern.Solid).setStrokeWidth(3).build();
	private static final Pen RClickPen = Pen.newPen().setColor(Color.Yellow)
			.setFillPattern(FillPattern.None).setStrokeWidth(5).build();
	private static final Pen DoubleLClickPen = Pen.newPen().setColor(Color.Green) //Color.Red
			.setFillPattern(FillPattern.None).setStrokeWidth(5).build();
	private static final Pen DragDropPen = Pen.newPen().setColor(Color.CornflowerBlue)
			.setFillPattern(FillPattern.None).setStrokeWidth(2).setStrokeCaps(StrokeCaps._Arrow).build();

	// by mimarmu1
	private static final Pen DropdownPen = Pen.newPen().setColor(Color.Gold).setFillPattern(FillPattern.None).setStrokeWidth(3).setFontSize(26).build();	

	private static final Pen TypePen = Pen.newPen().setColor(Color.Blue)
			//.setFillPattern(FillPattern.None).setStrokeWidth(3).setFontSize(26).build();
			.setFillPattern(FillPattern.None).setStrokeWidth(3).build(); // by urueda (use default font size)
	
	public AnnotatingActionCompiler(){ super(); }
	
	public AnnotatingActionCompiler(Abstractor abstractor){
		super(abstractor);
	}
	
	// begin by urueda
	
	@Override
	public Action mouseMove(Widget w){
		Action ret = super.mouseMove(w);
		ret.set(Tags.Desc, "Mouse move");
		ret.set(Tags.Role, ActionRoles.MouseMove);
		return ret;
	}
	
	@Override
	public Action mouseMove(Widget w, Position position){
		Action ret = super.mouseMove(w,position);
		Shape s = w.get(Tags.Shape);
		Shape moveShape = Rect.from(s.x(), s.y() + s.height(), s.width(), 1); // Rect.from(s.x() + s.width()/4, s.y() + s.height(), s.width()/2, 1);
		ret.set(Tags.Visualizer, new ShapeVisualizer(MovePen, moveShape, "", 0.5, 0.5));		
		return ret;
	}
	
	// end by urueda
	
	@Override // by urueda
	public Action leftClick(){
		Action ret = super.leftClick();
		ret.set(Tags.Desc, "Left Click");
		ret.set(Tags.Role, ActionRoles.LeftClick);
		return ret;
	}

	@Override // by urueda
	public Action rightClick(){
		Action ret = super.rightClick();
		ret.set(Tags.Desc, "Right Click");
		ret.set(Tags.Role, ActionRoles.RightClick);
		return ret;
	}

	@Override // by urueda
	public Action leftDoubleClick(){
		Action ret = super.leftDoubleClick();
		ret.set(Tags.Desc, "Left Double Click");
		ret.set(Tags.Role, ActionRoles.LDoubleClick);
		return ret;
	}

	@Override // by urueda
	public Action leftClickAt(Widget widget, double relX, double relY){
		//Action ret = leftClickAt(new WidgetPosition(wf, Tags.Shape, relX, relY, true));
		Action ret = super.leftClickAt(widget, relX, relY); // by urueda
		ret.set(Tags.Desc, "Left Click at '" + widget.get(Tags.Desc, "<no description>") + "'");
		//ret.set(Tags.Targets, Util.newArrayList(wf));
		return ret;
	}

	@Override // by urueda
	public Action leftClickAt(Position position){
		Action ret = super.leftClickAt(position);
		ret.set(Tags.Desc, "Left Click at '" + position.toString() + "'");
		ret.set(Tags.Visualizer, new EllipseVisualizer(position, LClickPen, 10, 10));
		ret.set(Tags.Role, ActionRoles.LeftClickAt);
		return ret;
	}

	@Override // by urueda
	public Action rightClickAt(Position position){
		Action ret = super.rightClickAt(position);
		ret.set(Tags.Desc, "Right Click at '" + position.toString() + "'");
		ret.set(Tags.Visualizer, new EllipseVisualizer(position, RClickPen, 20, 20));
		ret.set(Tags.Role, ActionRoles.RightClickAt);
		return ret;
	}

	@Override // by urueda
	public Action leftDoubleClickAt(Position position){
		Action ret = super.leftDoubleClickAt(position);
		ret.set(Tags.Desc, "Double-Click at '" + position.toString() + "'");
		ret.set(Tags.Visualizer, new EllipseVisualizer(position, DoubleLClickPen, 30, 30));
		ret.set(Tags.Role, ActionRoles.LDoubleClickAt);
		return ret;
	}

	private final int DISPLAY_TEXT_MAX_LENGTH = 16;
	
	@Override // by urueda
	public Action clickTypeInto(final Widget widget, double relX, double relY, final String text){
		//Action ret = clickTypeInto(new WidgetPosition(abstractor.apply(widget), Tags.Shape, relX, relY, true), text);
		Action ret = super.clickTypeInto(widget, relX, relY, text); // by urueda
		//ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, 5, "...") + "' into '" + widget.get(Tags.Desc, "<no description>" + "'"));
		ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "...") + "' into '" + widget.get(Tags.Desc, "<no description>" + "'")); // by urueda
		return ret;
	}

	@Override // by urueda
	public Action clickTypeInto(final Position position, final String text){
		Action ret = super.clickTypeInto(position, text);
		//ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate(text, 5, "..."), TypePen));
		ret.set(Tags.Visualizer, new TextVisualizer(position, Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "..."), TypePen));
		//ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, 5, "...") + "' into '" + position.toString() + "'");
		ret.set(Tags.Desc, "Type '" + Util.abbreviate(text, DISPLAY_TEXT_MAX_LENGTH, "...") + "' into '" + position.toString() + "'"); // by urueda
		ret.set(Tags.Role, ActionRoles.ClickTypeInto);		
		return ret;
	}
	
	//by mimarmu1
	@Override // by urueda
	public Action dropDownAt(Position position){
	  Action ret = super.dropDownAt(position);
	  ret.set(Tags.Desc, "Dropdown at '" + position.toString() + "'");
	  ret.set(Tags.Visualizer, new EllipseVisualizer(position, DropdownPen, 30, 30));
	  ret.set(Tags.Role, ActionRoles.DropDown);
	  return ret;
	}	
	
	@Override // by urueda
	public Action dragFromTo(Position from, Position to){
		Action ret = super.dragFromTo(from, to);
		ret.set(Tags.Visualizer, new TrajectoryVisualizer(DragDropPen, from, new OrthogonalPosition(from, to, 0.2, 0), to));
		ret.set(Tags.Desc, "Drag " + from.toString() + " To " + to.toString());
		ret.set(Tags.Role, ActionRoles.LeftDrag);		
		return ret;
	}
	
	@Override // by urueda
	public Action hitKey(KBKeys key){
		Action ret = super.hitKey(key);
		ret.set(Tags.Desc, "Hit Key " + key);
		ret.set(Tags.Role, ActionRoles.HitKey);		
		return ret;
	}	
	
	@Override // by urueda
	public Action killProcessByPID(long pid, double timeToWaitBeforeKilling){
		Action ret = super.killProcessByPID(pid, timeToWaitBeforeKilling);
		ret.set(Tags.Desc, "Kill Process with pid: " + pid + ".");
		return ret;
	}
	
	@Override // by urueda
	public Action killProcessByName(String name, double timeToWaitBeforeKilling){
		Action ret = super.killProcessByName(name, timeToWaitBeforeKilling);
		ret.set(Tags.Desc, "Kill Process with name '" + name + "'");
		return ret;
	}
	
	@Override // by urueda
	public Action activateSystem(){	
		Action ret = super.activateSystem();
		ret.set(Tags.Desc, "Bring the system to the foreground.");
		return ret;
	}
}