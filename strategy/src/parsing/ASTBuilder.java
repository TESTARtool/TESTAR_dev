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
    {return new BoolOprNode(null, BooleanOperator.NOT, visit(ctx.expr));}
    
    @Override
    public BoolOprNode visitBoolOprExpr(StrategyParser.BoolOprExprContext ctx)
    {
        BooleanOperator opr;
        if(ctx.AND() != null)           opr = BooleanOperator.AND;
        else if (ctx.XOR() != null)     opr = BooleanOperator.XOR;
        else if (ctx.OR() != null)      opr = BooleanOperator.OR;
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
        IntegerOperator opr;
        if(ctx.LT() != null)            opr = IntegerOperator.LT;
        else if (ctx.LE() != null)      opr = IntegerOperator.LE;
        else if (ctx.GT() != null)      opr = IntegerOperator.GT;
        else if (ctx.GE() != null)      opr = IntegerOperator.GE;
        else if (ctx.EQ() != null)      opr = IntegerOperator.EQ;
        else if (ctx.NE() != null)      opr = IntegerOperator.NE;
        else                            return null;
        return new IntOprNode(visit(ctx.left), opr, (BaseStrategyNode) visit(ctx.right));
    }
    
    ///////////////////////
    // number of actions //
    ///////////////////////
    
    @Override
    public NumberOfActionsNode visitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx)
    {
//        VisitedModifier visitedModifier = (ctx.ACTION_VISITED() != null) ? VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()) : null;
        return new NumberOfActionsNode(
                VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.toEnum(ctx.ACTION_TYPE().getText()));
    }
    
    ////////////////////
    // state booleans //
    ////////////////////
    
    @Override
    public StateChangedNode visitStateChanged(StrategyParser.StateChangedContext ctx)
    {return new StateChangedNode();}
    @Override
    public AnyActionsExistNode visitAnyActionsExists(StrategyParser.AnyActionsExistsContext ctx)
    {
        return new AnyActionsExistNode(
                VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.toEnum(ctx.ACTION_TYPE().getText()));
    }
    @Override
    public SutNode visitSutType(StrategyParser.SutTypeContext ctx)
    {
        return new SutNode(
                Filter.stringToEnum(ctx.FILTER().getText()),
                SutType.stringToEnum(ctx.SUT_TYPE().getText()));
    }
    @Override
    public RelatedActionExistsNode visitRelatedActionExists(StrategyParser.RelatedActionExistsContext ctx)
    { return new RelatedActionExistsNode(ActionRelation.stringToEnum(ctx.RELATED_ACTION().getText())); }
    
    ////////////////////////
    // action expressions //
    ////////////////////////
    
    @Override public BaseStrategyNode visitSubStrategy(StrategyParser.SubStrategyContext ctx)
    { return visit(ctx.strategy()); }
    
    @Override public ActionListNode visitActionList(StrategyParser.ActionListContext ctx)
    {
        List<BaseActionNode> actionNodes = new ArrayList<>();
        for(int i = 0; i < ctx.getChildCount(); i++)
            actionNodes.add((BaseActionNode) visit(ctx.action(i)));
        return new ActionListNode(actionNodes);
    }
    
    @Override
    public SelectPreviousActionNode visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx)
    {
        return new SelectPreviousActionNode(Integer.valueOf(ctx.NUMBER().getText()));
    }
    @Override
    public SelectRandomActionNode visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx)
    {
//        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new SelectRandomActionNode(
                Integer.valueOf(ctx.NUMBER().getText()),
                VisitedModifier.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.toEnum(ctx.ACTION_TYPE().getText()));
    }
    @Override public SelectByRelationNode visitSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx)
    {
        return new SelectByRelationNode(
                Integer.valueOf(ctx.NUMBER().getText()),
                ActionRelation.stringToEnum(ctx.RELATED_ACTION().getText()));
    }
}