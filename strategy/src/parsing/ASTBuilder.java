package parsing;

import antlrfour.StrategyBaseVisitor;
import antlrfour.StrategyParser;
import strategynodes.*;
import strategynodes.bool_expr.*;
import strategynodes.action_expr.*;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends StrategyBaseVisitor<BaseStrategyNode>
{
    /////////////////////
    // top-level nodes //
    /////////////////////
    
    @Override
    public Strategy_Node visitStrategy_file(StrategyParser.Strategy_fileContext ctx)
    {return (Strategy_Node) visit(ctx.strategy());}
    @Override
    public Strategy_Node visitStrategy(StrategyParser.StrategyContext ctx)
    {return new Strategy_Node(visit(ctx.ifExpr), visit(ctx.thenExpr), visit(ctx.elseExpr));}
    
    /////////////////////////
    // boolean expressions //
    /////////////////////////
    
    @Override
    public BoolOprNode visitNotExpr(StrategyParser.NotExprContext ctx)
    {return new BoolOprNode(null, BoolOperator.NOT, visit(ctx.expr));}
    
    @Override
    public BoolOprNode visitBoolOprExpr(StrategyParser.BoolOprExprContext ctx)
    {
        BoolOperator opr;
        if(ctx.AND() != null)           opr = BoolOperator.AND;
        else if (ctx.XOR() != null)     opr = BoolOperator.XOR;
        else if (ctx.OR() != null)      opr = BoolOperator.OR;
        else                            return null;
        return new BoolOprNode(visit(ctx.left), opr, visit(ctx.right));
    }
    
    ////////////////////////
    // number expressions //
    ////////////////////////
    
    @Override
    public BaseStrategyNode visitNumber_expr(StrategyParser.Number_exprContext ctx)
    {
        if(ctx.NUMBER() != null) //plain integer
            return visitChildren(ctx);
        else
            return visit(ctx.number_of_actions());
    }
    
    @Override
    public BaseStrategyNode visitNumberOprExpr(StrategyParser.NumberOprExprContext ctx)
    {
        IntOperator opr;
        if(ctx.LT() != null)            opr = IntOperator.LT;
        else if (ctx.LE() != null)      opr = IntOperator.LE;
        else if (ctx.GT() != null)      opr = IntOperator.GT;
        else if (ctx.GE() != null)      opr = IntOperator.GE;
        else if (ctx.EQ() != null)      opr = IntOperator.EQ;
        else if (ctx.NE() != null)      opr = IntOperator.NE;
        else                            return null;
        return new IntOprNode(visit(ctx.left), opr, (BaseStrategyNode) visit(ctx.right));
    }
    
    ///////////////////////
    // number of actions //
    ///////////////////////
    
    @Override
    public NumberOfActions_Node visitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx)
    {
//        VisitedModifier visitedModifier = (ctx.ACTION_VISITED() != null) ? VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()) : null;
        return new NumberOfActions_Node(
                VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.toEnum(ctx.ACTION_TYPE().getText()));
    }
    
    ////////////////////
    // state booleans //
    ////////////////////
    
    @Override
    public StateChanged_Node visitStateChanged(StrategyParser.StateChangedContext ctx)
    {return new StateChanged_Node();}
    @Override
    public AnyActionsExist_Node visitAnyActionsExists(StrategyParser.AnyActionsExistsContext ctx)
    {
        return new AnyActionsExist_Node(
                VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.toEnum(ctx.ACTION_TYPE().getText()));
    }
    @Override
    public Sut_Node visitSutType(StrategyParser.SutTypeContext ctx)
    {
        return new Sut_Node(
                Filter.stringToEnum(ctx.FILTER().getText()),
                SutType.stringToEnum(ctx.SUT_TYPE().getText()));
    }
    @Override
    public RelatedActionExists_Node visitRelatedActionExists(StrategyParser.RelatedActionExistsContext ctx)
    { return new RelatedActionExists_Node(ActionRelation.stringToEnum(ctx.RELATED_ACTION().getText())); }
    
    ////////////////////////
    // action expressions //
    ////////////////////////
    
    @Override public BaseStrategyNode visitSubStrategy(StrategyParser.SubStrategyContext ctx)
    { return visit(ctx.strategy()); }
    
    @Override public ActionList_Node visitActionList(StrategyParser.ActionListContext ctx)
    {
        List<BaseAction_Node> actionNodes = new ArrayList<>();
        for(int i = 0; i < ctx.getChildCount(); i++)
            actionNodes.add((BaseAction_Node) visit(ctx.action(i)));
        return new ActionList_Node(actionNodes);
    }
    
    @Override
    public SelectPreviousAction_Node visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx)
    {
        return new SelectPreviousAction_Node(Integer.valueOf(ctx.NUMBER().getText()));
    }
    @Override
    public SelectRandomAction_Node visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx)
    {
//        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new SelectRandomAction_Node(
                Integer.valueOf(ctx.NUMBER().getText()),
                VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.toEnum(ctx.ACTION_TYPE().getText()));
    }
    @Override public SelectByRelation_Node visitSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx)
    {
        return new SelectByRelation_Node(
                Integer.valueOf(ctx.NUMBER().getText()),
                ActionRelation.stringToEnum(ctx.RELATED_ACTION().getText()));
    }
}