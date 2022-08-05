package parsing;

import antlr4.StrategyBaseVisitor;
import antlr4.StrategyParser;
import strategy_nodes.*;
import strategy_nodes.actions.*;
import strategy_nodes.base_nodes.BaseActionNode;
import strategy_nodes.base_nodes.BaseBooleanNode;
import strategy_nodes.base_nodes.BaseIntegerNode;
import strategy_nodes.base_nodes.BaseStrategyNode;
import strategy_nodes.bool_operators.*;
import strategy_nodes.hierarchy.S_ChildNode;
import strategy_nodes.hierarchy.S_ChildOrSiblingNode;
import strategy_nodes.hierarchy.S_SiblingNode;
import strategy_nodes.number_of_actions.Total_N_ActionsNode;
import strategy_nodes.number_of_actions.Total_N_PrevExActionsNode;
import strategy_nodes.number_of_actions.Total_N_UnexActionsNode;
import strategy_nodes.number_operators.*;
import strategy_nodes.state_bools.*;
import strategy_nodes.terminals.*;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends StrategyBaseVisitor<BaseStrategyNode>
{
    //top-level nodes
    @Override
    public StrategyNode visitStrategy_file(StrategyParser.Strategy_fileContext ctx)
    {return (StrategyNode) visit(ctx.strategy());}
    @Override
    public StrategyNode visitStrategy(StrategyParser.StrategyContext ctx)
    {return new StrategyNode((BaseBooleanNode) visit(ctx.ifExpr), visit(ctx.thenExpr), visit(ctx.elseExpr));}
    
    //boolean expressions
    @Override
    public NotOprNode visitNotExpr(StrategyParser.NotExprContext ctx)
    {return new NotOprNode((BaseBooleanNode) visit(ctx.expr));}
    @Override
    public AndOprNode visitAndExpr(StrategyParser.AndExprContext ctx)
    {return new AndOprNode((BaseBooleanNode) visit(ctx.left), (BaseBooleanNode) visit(ctx.right));}
    @Override
    public XorOprNode visitXorExpr(StrategyParser.XorExprContext ctx)
    {return new XorOprNode((BaseBooleanNode) visit(ctx.left), (BaseBooleanNode) visit(ctx.right));}
    @Override
    public OrOprNode visitOrExpr(StrategyParser.OrExprContext ctx)
    {return new OrOprNode(visit(ctx.left), visit(ctx.right));}
    
    public BooleanNode visitBaseBool(StrategyParser.BaseBoolContext ctx)
    {return new BooleanNode(Boolean.valueOf(ctx.BOOLEAN().getText()));}
    
    // number expressions
    @Override
    public LessThanOprNode visitLessThanExpr(StrategyParser.LessThanExprContext ctx)
    {return new LessThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));}
    
    @Override
    public LessEqualThanOprNode visitLessEqualThanExpr(StrategyParser.LessEqualThanExprContext ctx)
    {return new LessEqualThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));}
    
    @Override
    public GreaterThanOprNode visitGreaterThanExpr(StrategyParser.GreaterThanExprContext ctx)
    {return new GreaterThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));}
    
    @Override
    public GreaterEqualThanOprNode visitGreaterEqualThanExpr(StrategyParser.GreaterEqualThanExprContext ctx)
    {return new GreaterEqualThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));}
    
    
    @Override
    public EqualOprNode visitEqualExpr(StrategyParser.EqualExprContext ctx)
    {return new EqualOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));}

    @Override
    public NotEqualOprNode visitNotEqualExpr(StrategyParser.NotEqualExprContext ctx)
    {return new NotEqualOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));}
    
    @Override
    public BaseStrategyNode visitNumber_expr(StrategyParser.Number_exprContext ctx)
    {
        if(ctx.NUMBER() != null) //plain integer
            return new IntegerNode(Integer.valueOf(ctx.NUMBER().getText()));
        else
            return visit(ctx.number_of_actions());
    }
    
//    @Override
//    public BaseStrategyNode visitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx)
//    {
//        String stringValue = "";
//        if(ctx.NUM_ACTIONS() != null) //normal action
//            stringValue = ctx.NUM_ACTIONS().getText();
//        else if(ctx.COMPOUND_NUM_ACTIONS().getText() != null) //compound action
//            stringValue = ctx.COMPOUND_NUM_ACTIONS().getText();
//
//        switch(stringValue)
//        {
//            case "total-number-of-actions":
//                return new TotalNumberOfActions();
//            case "total-number-of-unexecuted-actions":
//                return new TotalNumberOfUnexecutedActions();
//            case "total-number-of-previous-executed-actions":
//                return new TotalNumberOfPreviousExecutedActions();
//            case "number-of-previous-executed-actions-of-type":
//                return new NumberOfPreviousExecutedActionsOfType(ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
//            case "number-of-actions-of-type":
//                return new NumberOfActionsOfType(ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
//            case "number-of-unexecuted-actions-of-type":
//                return new NumberOfUnexecutedActionsOfType(ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
//            default://default is null
//                return null;
//        }
//    }
    //number of actions
    @Override
    public Total_N_ActionsNode visitTnActions(StrategyParser.TnActionsContext ctx)
    {return new Total_N_ActionsNode(ctx.getText());}
    @Override
    public Total_N_UnexActionsNode visitTnUnexActions(StrategyParser.TnUnexActionsContext ctx)
    {return new Total_N_UnexActionsNode(ctx.getText());}
    @Override
    public Total_N_PrevExActionsNode visitTnPrevUnexActions(StrategyParser.TnPrevUnexActionsContext ctx)
    {return new Total_N_PrevExActionsNode(ctx.getText());}
    
    
    //state booleans
    @Override
    public AvailableActionOfTypeNode visitAvailableActionsOftype(StrategyParser.AvailableActionsOftypeContext ctx)
    {
        return new AvailableActionOfTypeNode(ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
    }
    @Override
    public SutTypeIsNode visitSutType(StrategyParser.SutTypeContext ctx)
    {
        return new SutTypeIsNode(SutType.valueOfLabel(ctx.SUT_TYPE().getText()));
    }
    @Override
    public StateChangedNode visitStateChanged(StrategyParser.StateChangedContext ctx)
    {
        return new StateChangedNode();
    }
    //action expressions
//    @Override
//    public BaseStrategyNode visitAction_expr(StrategyParser.Action_exprContext ctx)
//    {
//        if(ctx.strategy() == null)
//        {
//            List<BaseActionNode> actionNodes = new ArrayList<>();
//            for(int i = 0; i < ctx.getChildCount(); i++)
//                actionNodes.add(visitAction(ctx.action(i)));
//            return new ActionListNode(actionNodes);
//        }
//        else return visit(ctx.strategy());
//    }
    
    @Override
    public BaseStrategyNode visitAction_expr(StrategyParser.Action_exprContext ctx)
    {
        if(ctx.strategy() == null) //not a strategy node
        {
            List<BaseActionNode> actionNodes = new ArrayList<>();
            for(int i = 0; i < ctx.getChildCount(); i++)
                actionNodes.add((BaseActionNode) visit(ctx.action(i)));
            return new ActionListNode(actionNodes);
        }
        else return visit(ctx.strategy());
    }
    
    @Override
    public RandomActionNode visitRAction(StrategyParser.RActionContext ctx)
    {return new RandomActionNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
    @Override
    public PreviousActionNode visitPrevAction(StrategyParser.PrevActionContext ctx)
    {return new PreviousActionNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
    @Override
    public R_ActionOfType visitRActionOfType(StrategyParser.RActionOfTypeContext ctx)
    {return new R_ActionOfType(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText(), ActionType.valueOf(ctx.ACTION_TYPE().getText()));}
    @Override
    public R_ActionNotTypeNode visitRActionNotType(StrategyParser.RActionNotTypeContext ctx)
    {return new R_ActionNotTypeNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText(), ActionType.valueOf(ctx.ACTION_TYPE().getText()));}
    @Override
    public R_LeastExActionNode visitRLeastExAction(StrategyParser.RLeastExActionContext ctx)
    {return new R_LeastExActionNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
    @Override
    public R_MostExActionNode visitRMostExAction(StrategyParser.RMostExActionContext ctx)
    {return new R_MostExActionNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
    @Override
    public R_UnexActionNode visitRUnexAction(StrategyParser.RUnexActionContext ctx)
    {return new R_UnexActionNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
    @Override
    public R_UnexActionOfTypeNode visitRUnexActionOfType(StrategyParser.RUnexActionOfTypeContext ctx)
    {return new R_UnexActionOfTypeNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText(), ActionType.valueOf(ctx.ACTION_TYPE().getText()));}
    @Override
    public R_UnexActionNotTypeNode visitRUnexActionNotType(StrategyParser.RUnexActionNotTypeContext ctx)
    {return new R_UnexActionNotTypeNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText(), ActionType.valueOf(ctx.ACTION_TYPE().getText()));}
    
    //hierarchy
    @Override
    public S_ChildNode visitSChildAction(StrategyParser.SChildActionContext ctx)
    {return new S_ChildNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
    @Override
    public S_SiblingNode visitSSiblingAction(StrategyParser.SSiblingActionContext ctx)
    {return new S_SiblingNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
    @Override
    public S_ChildOrSiblingNode visitSChildOrSiblingAction(StrategyParser.SChildOrSiblingActionContext ctx)
    {return new S_ChildOrSiblingNode(Integer.valueOf(ctx.NUMBER().getText()), ctx.getText());}
}