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
    public StrategyNode visitStrategy_file(StrategyParser.Strategy_fileContext ctx)
    {return (StrategyNode) visit(ctx.strategy());}

    @Override
    public StrategyNode visitStrategy(StrategyParser.StrategyContext ctx)
    {
        if(ctx.if_else_then() != null)
            return new StrategyNode((IfThenElseNode) visit(ctx.if_else_then()));
        else return new StrategyNode((ActionListNode) visit(ctx.action_list()));
    }
    
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
        else if (ctx.IS() != null)      opr = BooleanOperator.IS;
        else                            return null;
        return new BoolOprNode(visit(ctx.left), opr, visit(ctx.right));
    }
    @Override
    public PlainBooleanNode visitPlainBool(StrategyParser.PlainBoolContext ctx)
    { return new PlainBooleanNode(Boolean.parseBoolean(ctx.BOOLEAN().getText())); }
    
    ////////////////////////
    // number expressions //
    ////////////////////////

    @Override
    public IntOprNode visitNumberOprExpr(StrategyParser.NumberOprExprContext ctx)
    {
        IntegerOperator opr;
        if(ctx.LT() != null)            opr = IntegerOperator.LT;
        else if (ctx.LE() != null)      opr = IntegerOperator.LE;
        else if (ctx.GT() != null)      opr = IntegerOperator.GT;
        else if (ctx.GE() != null)      opr = IntegerOperator.GE;
        else if (ctx.EQ() != null)      opr = IntegerOperator.EQ;
        else if (ctx.NE() != null)      opr = IntegerOperator.NE;
        else                            return null;
        return new IntOprNode(visit(ctx.left), opr, visit(ctx.right));
    }
    
    ///////////////////////
    // number of actions //
    ///////////////////////
    
    @Override
    public NumberOfActionsNode visitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx)
    {
        VisitedModifier visitModifier = (ctx.VISIT_MODIFIER() == null) ? null : VisitedModifier.toEnum(ctx.VISIT_MODIFIER().getText());
        Filter filter = (ctx.FILTER() == null) ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType actionType = (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        
        return new NumberOfActionsNode(visitModifier,filter, actionType);
    }

    @Override
    public BaseStrategyNode visitNumber_expr(StrategyParser.Number_exprContext ctx)
    {
        if(ctx.number_of_actions() != null)
            return visit(ctx.number_of_actions());
        else
            return new PlainIntegerNode(Integer.parseInt(ctx.NUMBER().getText()));
    }
    
    ////////////////////
    // state booleans //
    ////////////////////
    
    @Override
    public StateChangedNode visitStateChanged(StrategyParser.StateChangedContext ctx)
    {return new StateChangedNode();}
    @Override
    public AnyExistNode visitAnyExist(StrategyParser.AnyExistContext ctx)
    {
        VisitedModifier visitModifier = (ctx.VISIT_MODIFIER() == null) ? null :  VisitedModifier.toEnum(ctx.VISIT_MODIFIER().getText());
        Filter filter = (ctx.FILTER() == null) ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType actionType = (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        
        return new AnyExistNode(visitModifier, filter, actionType);
    }
    @Override
    public SutNode visitSutType(StrategyParser.SutTypeContext ctx)
    {
        Filter filter = (ctx.FILTER() == null) ? null : Filter.toEnum(ctx.FILTER().getText());
        SutType sutType = (ctx.SUT_TYPE() == null) ? null : SutType.toEnum(ctx.SUT_TYPE().getText());
        
        return new SutNode(filter, sutType);
    }
    @Override
    public AnyExistRelatedActionNode visitAnyExistRelatedAction(StrategyParser.AnyExistRelatedActionContext ctx)
    { return new AnyExistRelatedActionNode(RelatedAction.toEnum(ctx.RELATED_ACTION().getText())); }
    
    ////////////////////////
    // action expressions //
    ////////////////////////

    @Override
    public ActionListNode visitAction_list(StrategyParser.Action_listContext ctx)
    {
        List<BaseActionNode> actionNodes = new ArrayList<>();
        for(int i = 0; i < ctx.getChildCount(); i++)
            actionNodes.add((BaseActionNode) visit(ctx.action(i)));
        return new ActionListNode(actionNodes);
    }
    
    @Override
    public SelectPreviousNode visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.parseInt(ctx.NUMBER().getText());
        
        return new SelectPreviousNode(weight);
    }
    @Override
    public SelectRandomActionNode visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.parseInt(ctx.NUMBER().getText());
        VisitedModifier visitModifier = (ctx.VISIT_MODIFIER() == null) ? null:  VisitedModifier.toEnum(ctx.VISIT_MODIFIER().getText());
        Filter filter = (ctx.FILTER() == null) ? null: Filter.toEnum(ctx.FILTER().getText());
        ActionType actionType = (ctx.ACTION_TYPE() == null) ? null: ActionType.toEnum(ctx.ACTION_TYPE().getText());
        
        return new SelectRandomActionNode(weight, visitModifier, filter, actionType);
    }
    @Override public SelectRelationNode visitSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.parseInt(ctx.NUMBER().getText());
        RelatedAction relatedAction = (ctx.RELATED_ACTION() == null) ? null : RelatedAction.toEnum(ctx.RELATED_ACTION().getText());
        
        return new SelectRelationNode(weight, relatedAction);
    }
}