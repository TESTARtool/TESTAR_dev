package parsing;

import antlrfour.StrategyBaseVisitor;
import antlrfour.StrategyParser;
import parsing.treenodes.*;
import parsing.treenodes.ActionType;
import parsing.treenodes.Filter;
import parsing.treenodes.SutType;
import parsing.treenodes.operators.booloperators.AndOprNode;
import parsing.treenodes.operators.booloperators.NotOprNode;
import parsing.treenodes.operators.booloperators.OrOprNode;
import parsing.treenodes.operators.booloperators.XorOprNode;
import parsing.treenodes.operators.integeroperators.*;
import strategynodes.basenodes.*;
import strategynodes.terminals.BooleanNode;
import strategynodes.terminals.IntegerNode;

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
    {return new Strategy_Node((BaseBooleanNode) visit(ctx.ifExpr), visit(ctx.thenExpr), visit(ctx.elseExpr));}
    
    /////////////////////////
    // boolean expressions //
    /////////////////////////
    
    @Override
    public NotOprNode visitNotExpr(StrategyParser.NotExprContext ctx)
    {return new NotOprNode((BaseBooleanNode) visit(ctx.expr));}
    
    @Override
    public BaseStrategyNode visitBoolOprExpr(StrategyParser.BoolOprExprContext ctx)
    {
        switch(ctx.getText().toUpperCase())
        {
            case "AND":
                return new AndOprNode((BaseBooleanNode) visit(ctx.left), (BaseBooleanNode) visit(ctx.right));
            case "XOR":
                return new XorOprNode((BaseBooleanNode) visit(ctx.left), (BaseBooleanNode) visit(ctx.right));
            case "OR":
                return new OrOprNode((BaseBooleanNode) visit(ctx.left), (BaseBooleanNode) visit(ctx.right));
        }
        return null;
    }
    
    @Override
    public BooleanNode visitBaseBool(StrategyParser.BaseBoolContext ctx)
    {return new BooleanNode(Boolean.valueOf(ctx.BOOLEAN().getText()));}
    
    
    ////////////////////////
    // number expressions //
    ////////////////////////
    
    @Override
    public BaseStrategyNode visitNumber_expr(StrategyParser.Number_exprContext ctx)
    {
        if(ctx.NUMBER() != null) //plain integer
            return new IntegerNode(Integer.valueOf(ctx.NUMBER().getText()));
        else
            return visit(ctx.number_of_actions());
    }
    
    @Override
    public BaseStrategyNode visitNumberOprExpr(StrategyParser.NumberOprExprContext ctx)
    {
        switch(ctx.getText())
        {
            case ">":
                return new GreaterThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));
            case ">=":
                return new GreaterEqualThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));
            case "<":
                return new LessThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));
            case "<=":
                return new LessEqualThanOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));
            case "=":
            case "==":
                return new EqualOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));
            case "!=":
                return new NotEqualOprNode((BaseIntegerNode) visit(ctx.left), (BaseIntegerNode) visit(ctx.right));
        }
        return null;
    }
    
    ///////////////////////
    // number of actions //
    ///////////////////////
    
    @Override
    public NumberOfActions_Node visitNumber_of_actions(StrategyParser.Number_of_actionsContext ctx)
    {
        Visited visited = (ctx.ACTION_VISITED() != null) ? Visited.stringToEnum(ctx.ACTION_VISITED().getText()) : null;
        return new NumberOfActions_Node(
                Visited.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.stringToEnum(ctx.action_type().getText()));
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
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.stringToEnum(ctx.action_type().getText()));
    }
    @Override
    public Sut_Node visitSutType(StrategyParser.SutTypeContext ctx)
    {
        return new Sut_Node(
                Filter.stringToEnum(ctx.FILTER().getText()),
                SutType.stringToEnum(ctx.sut_type().getText()));
    }
    @Override
    public RelatedActionExists_Node visitRelatedActionExists(StrategyParser.RelatedActionExistsContext ctx)
    { return new RelatedActionExists_Node(Relation.stringToEnum(ctx.related_action().getText())); }
    
    ////////////////////////
    // action expressions //
    ////////////////////////
    
//    @Override
//    public BaseStrategyNode visitAction_expr(StrategyParser.Action_exprContext ctx)
//    {
//        if(ctx.strategy() == null) //not a strategy node
//        {
//            List<BaseAction_Node> actionNodes = new ArrayList<>();
//            for(int i = 0; i < ctx.getChildCount(); i++)
//                actionNodes.add((BaseAction_Node) visit(ctx.action(i)));
//            return new ActionList_Node(actionNodes);
//        }
//        else return visit(ctx.strategy());
//    }
    
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
    public PreviousAction_Node visitSelectPreviousAction(StrategyParser.SelectPreviousActionContext ctx)
    {
        return new PreviousAction_Node(Integer.valueOf(ctx.NUMBER().getText()));
    }
    @Override
    public SelectRandomAction_Node visitSelectRandomAction(StrategyParser.SelectRandomActionContext ctx)
    {
//        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new SelectRandomAction_Node(
                Integer.valueOf(ctx.NUMBER().getText()),
                Visited.stringToEnum(ctx.ACTION_VISITED().getText()),
                Filter.stringToEnum(ctx.FILTER().getText()),
                ActionType.stringToEnum(ctx.action_type().getText()));
    }
    @Override public SelectByRelation_Node visitSelectRelatedAction(StrategyParser.SelectRelatedActionContext ctx)
    {
        return new SelectByRelation_Node(
                Integer.valueOf(ctx.NUMBER().getText()),
                Relation.stringToEnum(ctx.related_action().getText()));
    }
}