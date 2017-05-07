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

import org.fruit.alayer.Role;

public final class ActionRoles {
	private ActionRoles(){}

	public static final Role

	Action = Role.from("Action"),
	MouseAction = Role.from("MouseAction", Action),
	KeyboardAction = Role.from("KeyboardAction", Action),
	MouseMove = Role.from("MouseMove", MouseAction),
	MouseDown = Role.from("MouseDown", MouseAction),
	KeyDown = Role.from("KeyDown", KeyboardAction),
	MouseUp = Role.from("MouseUp", MouseAction),
	KeyUp = Role.from("KeyUp", KeyboardAction),
	HitKey = Role.from("HitKey", KeyDown, KeyUp),
	Click = Role.from("Click", MouseDown, MouseUp),
	LeftClick = Role.from("LeftClick", Click),
	RightClick = Role.from("RightClick", Click),
	DoubleClick = Role.from("DoubleClick", Click),
	LDoubleClick = Role.from("LDoubleClick", LeftClick, DoubleClick),
	RDoubleClick = Role.from("RDoubleClick", RightClick, DoubleClick),
	ClickAt = Role.from("ClickAt", Click, MouseMove),
	LeftClickAt = Role.from("LeftClickAt", ClickAt, LeftClick),
	RightClickAt = Role.from("RightClickAt", ClickAt, RightClick),
	DoubleClickAt = Role.from("DoubleClickAt", ClickAt, DoubleClick),
	LDoubleClickAt = Role.from("LDoubleClickAt", DoubleClickAt, LeftClick),
	RDoubleClickAt = Role.from("RDoubleClickAt", DoubleClickAt, RightClick),
	Type = Role.from("Type", HitKey),
	ClickTypeInto = Role.from("ClickTypeInto", ClickAt, Type),
	DropDown = Role.from("DropDown", Click, KeyDown), // /by mimarmu1
	Drag = Role.from("Drag", MouseDown, MouseUp, MouseMove),
	LeftDrag = Role.from("LeftDrag", Drag);
}