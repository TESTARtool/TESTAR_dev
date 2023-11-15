package parsing;

import antlrfour.StrategyBaseVisitor;
import antlrfour.StrategyParser;
import strategynodes.*;
import strategynodes.condition.*;
import strategynodes.filtering.*;
import strategynodes.instruction.*;

import java.util.ArrayList;

public class ASTBuilder extends StrategyBaseVisitor<BaseNode>
{
    /////////////////////
    // top-level nodes //
    /////////////////////
    
    @Override
    public StrategyNode visitStrategy_file(StrategyParser.Strategy_fileContext ctx)
    {
        return (StrategyNode) visit(ctx.strategy());
    }

    @Override
    public StrategyNode visitStrategy(StrategyParser.StrategyContext ctx)
    {
        ArrayList<BaseActionNode> actionNodes = new ArrayList<>();
        for(int i = 0; i < ctx.getChildCount(); i++)
            actionNodes.add((BaseActionNode) visit(ctx.getChild(i)));
        return new StrategyNode(actionNodes);
    }
    
    /////////////
    // actions //
    /////////////
    
    @Override
    public BaseActionNode visitAction(StrategyParser.ActionContext ctx)
    {
        return (BaseActionNode) ((ctx.cond_action() != null) ?
        visit(ctx.cond_action()) : visit(ctx.uncond_action()));
    }
    
    @Override
    public IfThenElseNode visitCond_action(StrategyParser.Cond_actionContext ctx)
    {
        int weight = ctx.INT() == null ? 1 : Integer.parseInt(ctx.INT().getText());
        return
                (ctx.elseExpr == null) ?
                new IfThenElseNode(weight, visit(ctx.ifExpr), (StrategyNode) visit(ctx.thenExpr)) :
                new IfThenElseNode(weight, visit(ctx.ifExpr), (StrategyNode) visit(ctx.thenExpr), (StrategyNode) visit(ctx.elseExpr));
    }
    
    @Override
    public SelectPreviousNode visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx)
    {
        int weight = ctx.INT() == null ? 1 : Integer.parseInt(ctx.INT().getText());
        return new SelectPreviousNode(weight);
    }
    
    @Override
    public SelectRandomRelationNode visitSelectRelation(StrategyParser.SelectRelationContext ctx)
    {
        int      weight   =    (ctx.INT() == null)          ? 1     : Integer.parseInt(ctx.INT().getText());
        Modifier modifier = (ctx.MODIFIER() == null)        ? null  : Modifier.toEnum(ctx.MODIFIER().getText());
        Relation relation = Relation.toEnum(ctx.RELATION().getText());
        return new SelectRandomRelationNode(weight, modifier, relation);
    }
    
    @Override
    public SelectRandomActionNode visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx)
    {
        int         weight =    (ctx.INT() == null)         ? 1     : Integer.parseInt(ctx.INT().getText());
        Modifier   modifier   =  (ctx.MODIFIER() == null)    ? null  : Modifier.toEnum(ctx.MODIFIER().getText());
        Filter     filter     = (ctx.FILTER() == null) ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType actionType = (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new SelectRandomActionNode(weight, modifier, filter, actionType);
    }
    
    //////////////
    // booleans //
    //////////////
    
    @Override
    public BoolOprNode visitNotExpr(StrategyParser.NotExprContext ctx)
    {return new BoolOprNode(null, BooleanOperator.NOT, (BaseBooleanNode) visit(ctx.expr));}
    
    @Override
    public BoolOprNode visitBoolOprExpr(StrategyParser.BoolOprExprContext ctx)
    {
        BooleanOperator opr;
        if(ctx.AND() != null)           opr = BooleanOperator.AND;
        else if (ctx.XOR() != null)     opr = BooleanOperator.XOR;
        else if (ctx.OR() != null)      opr = BooleanOperator.OR;
        else if (ctx.EQUALS() != null)  opr = BooleanOperator.EQUALS;
        else                            return null;
        return new BoolOprNode((BaseBooleanNode) visit(ctx.left), opr, (BaseBooleanNode) visit(ctx.right));
    }
    
    @Override public BaseBooleanNode visitStateBool(StrategyParser.StateBoolContext ctx)
    {return (BaseBooleanNode) visit(ctx.state_boolean());}
    
    @Override
    public StateChangedNode visitStateChanged(StrategyParser.StateChangedContext ctx)
    {return new StateChangedNode();}
    
    @Override
    public AnyExistNode visitAnyExist(StrategyParser.AnyExistContext ctx)
    {
        Modifier    modifier =      (ctx.MODIFIER() == null)    ? null  : Modifier.toEnum(ctx.MODIFIER().getText());
        Filter      filter =        (ctx.FILTER() == null)      ? null  : Filter.toEnum(ctx.FILTER().getText());
        ActionType  actionType =    (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new AnyExistNode(modifier, filter, actionType);
    }
    
    @Override
    public AnyExistRelationNode visitAnyExistRelatedAction(StrategyParser.AnyExistRelatedActionContext ctx)
    {
        Modifier modifier = (ctx.MODIFIER() == null) ? null : Modifier.toEnum(ctx.MODIFIER().getText());
        return new AnyExistRelationNode(modifier, Relation.toEnum(ctx.RELATION().getText()));
    }
    
    @Override
    public PreviousExistNode visitPreviousExist(StrategyParser.PreviousExistContext ctx)
    {
        Filter      filter =        (ctx.FILTER() == null)      ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType  actionType =    (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new PreviousExistNode(filter, actionType);
    }
    @Override
    public SutNode visitSutType(StrategyParser.SutTypeContext ctx)
    {
        Filter  filter =    (ctx.FILTER() == null)  ? null : Filter.toEnum(ctx.FILTER().getText());
        SutType sutType =   (ctx.SUT() == null)     ? null : SutType.toEnum(ctx.SUT().getText());
        return new SutNode(filter, sutType);
    }
    
    @Override
    public PlainBooleanNode visitPlainBool(StrategyParser.PlainBoolContext ctx)
    { return new PlainBooleanNode(Boolean.parseBoolean(ctx.BOOL().getText())); }

    //////////////
    // integers //
    //////////////
    
    @Override
    public IntOprNode visitIntOprExpr(StrategyParser.IntOprExprContext ctx)
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
    
    @Override
    public NumberOfActionsNode visitNActions(StrategyParser.NActionsContext ctx)
    {
        Modifier    modifier =      (ctx.MODIFIER() == null)    ? null : Modifier.toEnum(ctx.MODIFIER().getText());
        Filter      filter     =    (ctx.FILTER() == null)      ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType  actionType =    (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new NumberOfActionsNode(modifier, filter, actionType);
    }
    
    @Override
    public PlainIntegerNode visitPlainInt(StrategyParser.PlainIntContext ctx)
    { return new PlainIntegerNode(Integer.parseInt(ctx.INT().getText())); }
}