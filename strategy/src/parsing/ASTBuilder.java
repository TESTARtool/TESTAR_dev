package parsing;

import antlrfour.StrategyBaseVisitor;
import antlrfour.StrategyParser;
import org.testar.monkey.alayer.Action;
import strategynodes.BaseNode;
import strategynodes.conditions.*;
import strategynodes.enums.*;
import strategynodes.instructions.*;

import java.util.ArrayList;

public class ASTBuilder extends StrategyBaseVisitor<BaseNode>
{
    /////////////////////
    // top-level nodes //
    /////////////////////
    
    @Override
    public ListNode visitStrategy_file(StrategyParser.Strategy_fileContext ctx)
    {
        return (ListNode) visit(ctx.strategy());
    }

    @Override
    public ListNode visitStrategy(StrategyParser.StrategyContext ctx)
    {
        ArrayList<ActionNode> actionNodes = new ArrayList<>();
        for(int i = 0; i < ctx.getChildCount(); i++)
            actionNodes.add((ActionNode) visit(ctx.getChild(i)));
        return new ListNode(actionNodes);
    }
    
    //////////////////
    // instructions //
    //////////////////

    @Override
    public BaseNode<Action> visitAction(StrategyParser.ActionContext ctx)
    {
        return (ctx.cond_action() != null) ?
        visit(ctx.cond_action()) : visit(ctx.uncond_action());
    }
    
    @Override
    public BaseNode<Action> visitCond_action(StrategyParser.Cond_actionContext ctx)
    {
        int weight = ctx.INT() == null ? 1 : Integer.parseInt(ctx.INT().getText());
        return
                (ctx.elseExpr == null) ?
                new IfThenElseNode(weight, (BaseNode<Boolean>) visit(ctx.ifExpr), (ListNode) visit(ctx.thenExpr)) :
                new IfThenElseNode(weight, (BaseNode<Boolean>) visit(ctx.ifExpr), (ListNode) visit(ctx.thenExpr), (ListNode) visit(ctx.elseExpr));
    }

    @Override
    public RepeatPreviousNode visitRepeatPreviousAction(StrategyParser.RepeatPreviousActionContext ctx)
    {
        int weight = ctx.INT() == null ? 1 : Integer.parseInt(ctx.INT().getText());
        return new RepeatPreviousNode(weight);
    }

    @Override
    public SelectPreviousNode visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx)
    {
        int weight = ctx.INT() == null ? 1 : Integer.parseInt(ctx.INT().getText());
        VisitStatus visitStatus = (ctx.VISIT_STATUS() == null)  ? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter filter           = (ctx.FILTER() == null)        ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType actionType   = (ctx.ACTION_TYPE() == null)   ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new SelectPreviousNode(weight, visitStatus, filter, actionType);
    }

    @Override
    public SelectRandomNode visitSelectByRelation(StrategyParser.SelectByRelationContext ctx)
    {
        int weight = ctx.INT() == null ? 1 : Integer.parseInt(ctx.INT().getText());
        VisitStatus visitStatus = (ctx.VISIT_STATUS() == null)? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter filter     = Filter.toEnum(ctx.FILTER().getText());
        Relation relation = Relation.toEnum(ctx.RELATION().getText());
        return new SelectRandomNode(weight, visitStatus, filter, relation);
    }

    @Override
    public SelectRandomNode visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx)
    {
        int         weight =    (ctx.INT() == null)         ? 1     : Integer.parseInt(ctx.INT().getText());
        VisitStatus visitStatus = (ctx.VISIT_STATUS() == null)? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter filter     = (ctx.FILTER() == null) ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType actionType = (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new SelectRandomNode(weight, visitStatus, filter, actionType);
    }
    
    //////////////
    // booleans //
    //////////////
    
    @Override
    public BoolOprNode visitNotExpr(StrategyParser.NotExprContext ctx)
    { return new BoolOprNode(null, BooleanOperator.NOT, (BaseNode<Boolean>) visit(ctx.expr)); }
    
    @Override
    public BoolOprNode visitBoolOprExpr(StrategyParser.BoolOprExprContext ctx)
    {
        BooleanOperator opr;
        if(ctx.AND() != null)           opr = BooleanOperator.AND;
        else if (ctx.XOR() != null)     opr = BooleanOperator.XOR;
        else if (ctx.OR() != null)      opr = BooleanOperator.OR;
        else if (ctx.EQUALS() != null)  opr = BooleanOperator.EQUALS;
        else                            return null;
        return new BoolOprNode((BaseNode<Boolean>) visit(ctx.left), opr, (BaseNode<Boolean>) visit(ctx.right));
    }
    
    @Override public BaseNode<Boolean> visitStateBool(StrategyParser.StateBoolContext ctx)
    { return (BaseNode<Boolean>) visit(ctx.state_boolean()); }
    
    @Override
    public BaseNode<Boolean> visitStateChanged(StrategyParser.StateChangedContext ctx)
    { return new StateChangedNode(); }
    
    @Override
    public AnyExistNode visitAnyExist(StrategyParser.AnyExistContext ctx)
    {
        VisitStatus visitStatus = (ctx.VISIT_STATUS() == null)? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter filter           = (ctx.FILTER() == null) ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType actionType   = (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new AnyExistNode(visitStatus, filter, actionType);
    }
    
    @Override
    public AnyExistNode visitAnyExistByRelation(StrategyParser.AnyExistByRelationContext ctx)
    {
        VisitStatus visitStatus = (ctx.VISIT_STATUS() == null)? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter filter           = Filter.toEnum(ctx.FILTER().getText());
        Relation relation       = Relation.toEnum(ctx.RELATION().getText());
        return new AnyExistNode(visitStatus, filter, relation);
    }
    
    @Override
    public PreviousExistNode visitPreviousExist(StrategyParser.PreviousExistContext ctx)
    {
        VisitStatus visitStatus =   (ctx.VISIT_STATUS() == null)? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter      filter =        (ctx.FILTER() == null)      ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType  actionType =    (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new PreviousExistNode(visitStatus, filter, actionType);
    }

    @Override
    public SutNode visitSutType(StrategyParser.SutTypeContext ctx)
    {
        Filter  filter = Filter.toEnum(ctx.FILTER().getText());
        SutType sutType = SutType.toEnum(ctx.SUT().getText());
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
        return new IntOprNode((BaseNode<Integer>) visit(ctx.left), opr, (BaseNode<Integer>) visit(ctx.right));
    }
    
    @Override
    public NrOfActionsNode visitNActions(StrategyParser.NActionsContext ctx)
    {
        VisitStatus visitStatus =   (ctx.VISIT_STATUS() == null)? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter      filter =        (ctx.FILTER() == null)      ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType  actionType =    (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new NrOfActionsNode(visitStatus, filter, actionType);
    }

    @Override public NrOfPreviousActionsNode visitNPrevious(StrategyParser.NPreviousContext ctx)
    {
        VisitStatus visitStatus =   (ctx.VISIT_STATUS() == null)? null : VisitStatus.toEnum(ctx.VISIT_STATUS().getText());
        Filter      filter =        (ctx.FILTER() == null)      ? null : Filter.toEnum(ctx.FILTER().getText());
        ActionType  actionType =    (ctx.ACTION_TYPE() == null) ? null : ActionType.toEnum(ctx.ACTION_TYPE().getText());
        return new NrOfPreviousActionsNode(visitStatus, filter, actionType);
    }

    @Override
    public PlainIntegerNode visitPlainInt(StrategyParser.PlainIntContext ctx)
    { return new PlainIntegerNode(Integer.parseInt(ctx.INT().getText())); }
}