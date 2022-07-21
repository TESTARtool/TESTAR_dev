package parsing;

import antlr4.StrategyBaseVisitor;
import antlr4.StrategyParser;
import strategy_nodes.*;
import strategy_nodes.actions.*;
import strategy_nodes.number_of_actions.*;
import strategy_nodes.operators.*;
import strategy_nodes.state_bools.*;
import strategy_nodes.terminals.*;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends StrategyBaseVisitor<BaseStrategyNode>
{
    //top-level nodes
    @Override
    public StrategyNode visitStrategy_file(StrategyParser.Strategy_fileContext ctx)
    {
        return (StrategyNode) visit(ctx.strategy());
    }
    @Override
    public StrategyNode visitStrategy(StrategyParser.StrategyContext ctx)
    {
        return new StrategyNode(visit(ctx.ifExpr), visit(ctx.thenExpr), visit(ctx.elseExpr));
    }
    //boolean expressions
    @Override
    public NotOprNode visitNotExpr(StrategyParser.NotExprContext ctx)
    {
        return new NotOprNode(visit(ctx.expr));
    }
    @Override
    public AndOprNode visitAndExpr(StrategyParser.AndExprContext ctx)
    {
        return new AndOprNode(visit(ctx.left), visit(ctx.right));
    }
    @Override
    public XorOprNode visitXorExpr(StrategyParser.XorExprContext ctx)
    {
        return new XorOprNode(visit(ctx.left), visit(ctx.right));
    }
    @Override
    public OrOprNode visitOrExpr(StrategyParser.OrExprContext ctx)
    {
        return new OrOprNode(visit(ctx.left), visit(ctx.right));
    }
    public TerminalBoolNode visitBaseBool(StrategyParser.BaseBoolContext ctx)
    {
        return new TerminalBoolNode(Boolean.valueOf(ctx.BOOLEAN().getText()));
    }
    // number expressions
    @Override
    public LessThanOprNode visitLessThanExpr(StrategyParser.LessThanExprContext ctx)
    {
        return new LessThanOprNode(visit(ctx.left), visit(ctx.right));
    }
    @Override
    public LessEqualThanOprNode visitLessEqualThanExpr(StrategyParser.LessEqualThanExprContext ctx)
    {
        return new LessEqualThanOprNode(visit(ctx.left), visit(ctx.right));
    }
    @Override
    public GreaterThanOprNode visitGreaterThanExpr(StrategyParser.GreaterThanExprContext ctx)
    {
        return new GreaterThanOprNode(visit(ctx.left), visit(ctx.right));
    }
    @Override
    public GreaterEqualThanOprNode visitGreaterEqualThanExpr(StrategyParser.GreaterEqualThanExprContext ctx)
    {
        return new GreaterEqualThanOprNode(visit(ctx.left), visit(ctx.right));
    }
    
    @Override
    public EqualOprNode visitEqualExpr(StrategyParser.EqualExprContext ctx)
    {
        return new EqualOprNode(visit(ctx.left), visit(ctx.right));
    }
    @Override
    public NotEqualOprNode visitNotEqualExpr(StrategyParser.NotEqualExprContext ctx)
    {
        return new NotEqualOprNode(visit(ctx.left), visit(ctx.right));
    }
    @Override
    public BaseStrategyNode visitNumber_expr(StrategyParser.Number_exprContext ctx)
    {
        if(ctx.NUMBER() != null)
            return new TerminalNumNode(Integer.valueOf(ctx.NUMBER().getText()));
        else
            return visit(ctx.number_of_actions());
    }
    @Override
    public BaseStrategyNode visitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx)
    {
        String stringValue = "";
        if(ctx.NUM_ACTIONS() != null) //normal action
            stringValue = ctx.NUM_ACTIONS().getText();
        else if(ctx.COMPOUND_NUM_ACTIONS().getText() != null) //compound action
            stringValue = ctx.COMPOUND_NUM_ACTIONS().getText();
        
        switch(stringValue)
        {
            case "total-number-of-actions":
                return new TotalNumberOfActions();
            case "total-number-of-unexecuted-actions":
                return new TotalNumberOfUnexecutedActions();
            case "total-number-of-previous-executed-actions":
                return new TotalNumberOfPreviousExecutedActions();
            case "number-of-previous-executed-actions-of-type":
                return new NumberOfPreviousExecutedActionsOfType(ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
            case "number-of-actions-of-type":
                return new NumberOfActionsOfType(ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
            case "number-of-unexecuted-actions-of-type":
                return new NumberOfUnexecutedActionsOfType(ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
            default://default is null
                return null;
        }
    }
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
    public StateUnchangedNode visitStateUnchanged(StrategyParser.StateUnchangedContext ctx)
    {
        return new StateUnchangedNode();
    }
    //action expressions
    @Override
    public BaseStrategyNode visitAction_expr(StrategyParser.Action_exprContext ctx)
    {
        if(ctx.strategy() == null)
        {
            List<BaseActionNode> actionNodes = new ArrayList<>();
            for(int i = 0; i < ctx.getChildCount(); i++)
                actionNodes.add(visitAction(ctx.action(i)));
            return new ActionListNode(actionNodes);
        }
        else return visit(ctx.strategy());
    }
    
    @Override
    public BaseActionNode visitAction(StrategyParser.ActionContext ctx)
    {
        int weight = (ctx.NUMBER() == null) ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        
        String stringValue = "";
        if(ctx.ACTION() != null) //normal action
            stringValue = ctx.ACTION().getText();
        else if(ctx.COMPOUND_ACTION().getText() != null) //compound action
            stringValue = ctx.COMPOUND_ACTION().getText();
        
        switch(stringValue)
        {
            case "previous-action":
                return new PreviousAction(weight);
            case "random-action":
                return new RandomAction(weight);
            case "random-action-of-type":
                return new RandomActionOfType(weight, ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
            case "random-unexecuted-action":
                return new RandomUnexecutedAction(weight);
            case "random-unexecuted-action-of-type":
                return new RandomUnexecutedActionOfType(weight, ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
            case "random-action-other-than":
                return new RandomActionOtherThan(weight, ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
            case "random-unexecuted-action-other-than":
                return new RandomUnexecutedActionOtherThan(weight, ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
            case "random-least-executed-action":
                return new RandomLeastExecutedAction(weight);
            case "random-most-executed-action":
                return new RandomMostExecutedAction(weight);
            default://default is null
                return null;
        }
    }
}