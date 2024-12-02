package parsing;

import antlrfour.StrategyBaseVisitor;
import antlrfour.StrategyParser;
import org.antlr.v4.runtime.tree.TerminalNode;
import strategynodes.BaseNode;
import strategynodes.conditions.*;
import strategynodes.data.ActionStatus;
import strategynodes.data.RelationStatus;
import strategynodes.data.VisitStatus;
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
    public IfThenElseNode visitIfThenElse(StrategyParser.IfThenElseContext ctx)
    {
        int weight = ctx.INT() == null ? 1 : Integer.parseInt(ctx.INT().getText());
        return
                (ctx.elseExpr == null) ?
                new IfThenElseNode(weight, (BooleanNode) visit(ctx.ifExpr), (ListNode) visit(ctx.thenExpr)) :
                new IfThenElseNode(weight, (BooleanNode) visit(ctx.ifExpr), (ListNode) visit(ctx.thenExpr), (ListNode) visit(ctx.elseExpr));
    }

    @Override
    public RepeatPreviousNode visitRepeatPreviousAction(StrategyParser.RepeatPreviousActionContext ctx)
    {
        int         weight      =   getWeight(ctx.INT());
        return new RepeatPreviousNode(weight);
    }

    @Override
    public SelectPreviousNode visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx)
    {
        int             weight          =   getWeight(ctx.INT());
        VisitStatus     visitStatus     =   getVisitStatus(ctx.visit_status());
        ActionStatus    actionStatus    =   getActionStatus(ctx.FILTER(), ctx.ACTION_TYPE());
        return new SelectPreviousNode(weight, visitStatus, actionStatus);
    }

    @Override
    public SelectRandomNode visitSelectByRelation(StrategyParser.SelectByRelationContext ctx)
    {
        int             weight          =   getWeight(ctx.INT());
        VisitStatus     visitStatus     =   getVisitStatus(ctx.visit_status());
        RelationStatus  relationStatus  =   getRelationStatus(ctx.FILTER(), ctx.RELATION());
        return new SelectRandomNode(weight, visitStatus, relationStatus);
    }

    @Override
    public SelectRandomNode visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx)
    {
        int             weight          =   getWeight(ctx.INT());
        VisitStatus     visitStatus     =   getVisitStatus(ctx.visit_status());
        ActionStatus    actionStatus    =   getActionStatus(ctx.FILTER(), ctx.ACTION_TYPE());
        return new SelectRandomNode(weight, visitStatus, actionStatus);
    }
    
    //////////////
    // booleans //
    //////////////
    
    @Override
    public BoolOprNode visitNotExpr(StrategyParser.NotExprContext ctx)
    { return new BoolOprNode(null, BooleanOperator.NOT, (BooleanNode) visit(ctx.expr)); }
    
    @Override
    public BoolOprNode visitBoolOprExpr(StrategyParser.BoolOprExprContext ctx)
    {
        BooleanOperator opr;
        if      (ctx.AND()      != null)    opr = BooleanOperator.AND;
        else if (ctx.XOR()      != null)    opr = BooleanOperator.XOR;
        else if (ctx.OR()       != null)    opr = BooleanOperator.OR;
        else if (ctx.EQUALS()   != null)    opr = BooleanOperator.EQUALS;
        else                            return null;
        return new BoolOprNode((BooleanNode) visit(ctx.left), opr, (BooleanNode) visit(ctx.right));
    }
    
    @Override
    public BaseNode visitStateBool(StrategyParser.StateBoolContext ctx)
    { return visit(ctx.state_boolean()); }

    @Override
    public StateChangedNode visitStateChanged(StrategyParser.StateChangedContext ctx)
    { return new StateChangedNode(); }
    
    @Override
    public AnyExistNode visitAnyExist(StrategyParser.AnyExistContext ctx)
    {
        VisitStatus     visitStatus     =   getVisitStatus(ctx.visit_status());
        ActionStatus    actionStatus    =   getActionStatus(ctx.FILTER(), ctx.ACTION_TYPE());
        return new AnyExistNode(visitStatus, actionStatus);
    }
    
    @Override
    public AnyExistNode visitAnyExistByRelation(StrategyParser.AnyExistByRelationContext ctx)
    {
        VisitStatus     visitStatus     =   getVisitStatus(ctx.visit_status());
        RelationStatus  relationStatus    =   getRelationStatus(ctx.FILTER(), ctx.RELATION());
        return new AnyExistNode(visitStatus, relationStatus);
    }
    
    @Override
    public PreviousExistNode visitPreviousExist(StrategyParser.PreviousExistContext ctx)
    {
        VisitStatus     visitStatus     =   getVisitStatus(ctx.visit_status());
        ActionStatus    actionStatus    =   getActionStatus(ctx.FILTER(), ctx.ACTION_TYPE());
        return new PreviousExistNode(visitStatus, actionStatus);
    }

    @Override
    public SutNode visitSutType(StrategyParser.SutTypeContext ctx)
    {
        Filter      filter      =   getFilter(ctx.FILTER());
        SutType     sutType     =   getSutType(ctx.SUT_TYPE());
        return new SutNode(filter, sutType);
    }
    
    @Override
    public PlainBooleanNode visitPlainBool(StrategyParser.PlainBoolContext ctx)
    { return new PlainBooleanNode(Boolean.parseBoolean(ctx.BOOL().getText())); }

    @Override
    public BaseNode visitVisit_status(StrategyParser.Visit_statusContext ctx)
    { return visitChildren(ctx); }

    //////////////
    // integers //
    //////////////
    
    @Override
    public IntOprNode visitIntOprExpr(StrategyParser.IntOprExprContext ctx)
    {
        IntegerOperator opr;
        if      (ctx.LT() != null)      opr = IntegerOperator.LT;
        else if (ctx.LE() != null)      opr = IntegerOperator.LE;
        else if (ctx.GT() != null)      opr = IntegerOperator.GT;
        else if (ctx.GE() != null)      opr = IntegerOperator.GE;
        else if (ctx.EQ() != null)      opr = IntegerOperator.EQ;
        else if (ctx.NE() != null)      opr = IntegerOperator.NE;
        else                            return null;
        return new IntOprNode((IntegerNode) visit(ctx.left), opr, (IntegerNode) visit(ctx.right));
    }
    
    @Override
    public NrOfActionsNode visitNActions(StrategyParser.NActionsContext ctx)
    {
        VisitStatus     visitStatus     =   getVisitStatus(ctx.visit_status());
        ActionStatus    actionStatus    =   getActionStatus(ctx.FILTER(), ctx.ACTION_TYPE());
        return new NrOfActionsNode(visitStatus, actionStatus);
    }

    @Override public NrOfPreviousActionsNode visitNPrevious(StrategyParser.NPreviousContext ctx)
    {
        VisitStatus visitStatus =   getVisitStatus(ctx.visit_status());
        ActionStatus    actionStatus    =   getActionStatus(ctx.FILTER(), ctx.ACTION_TYPE());
        return new NrOfPreviousActionsNode(visitStatus, actionStatus);
    }

    @Override
    public PlainIntegerNode visitPlainInt(StrategyParser.PlainIntContext ctx)
    { return new PlainIntegerNode(Integer.parseInt(ctx.INT().getText())); }

    ////////////////////
    // helper methods //
    ////////////////////

    private int getWeight(TerminalNode WEIGHT)
    { return (WEIGHT == null) ? 1 : Integer.parseInt(WEIGHT.getText()); }

    private VisitStatus getVisitStatus(StrategyParser.Visit_statusContext visit_status)
    {
        if(visit_status == null)
            return null;

        String rawText = visit_status.getText();

        rawText = rawText.replaceAll("[0-9]", ""); // replace all digits

        VisitType visitType =  (rawText.isEmpty()) ? null : VisitType.toEnum(rawText);
        Integer visitInt = (visit_status.INT() == null) ? null : Integer.valueOf(visit_status.INT().getText());

        return (visitType == null) ? null : new VisitStatus(visitType, visitInt);
    }

    private ActionStatus getActionStatus(TerminalNode FILTER, TerminalNode ACTION_TYPE)
    {
        Filter      filter      = getFilter(FILTER);
        ActionType  actionType  = (ACTION_TYPE == null) ? null : ActionType.toEnum(ACTION_TYPE.getText());
        return (actionType == null) ? null : new ActionStatus(filter, actionType);
    }

    private RelationStatus getRelationStatus(TerminalNode FILTER, TerminalNode RELATION_TYPE)
    {
        Filter          filter          = getFilter(FILTER);
        RelationType    relationType    = (RELATION_TYPE == null) ? null : RelationType.toEnum(RELATION_TYPE.getText());
        return (relationType == null) ? null : new RelationStatus(filter, relationType);
    }

    private Filter getFilter(TerminalNode FILTER)
    { return (FILTER == null) ? null : Filter.toEnum(FILTER.getText()); }

    private SutType getSutType(TerminalNode SUT_TYPE)
    { return (SUT_TYPE == null) ? null : SutType.toEnum(SUT_TYPE.getText()); }
}