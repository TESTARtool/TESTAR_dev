package parsing;

import antlrfour.StrategyBaseVisitor;
import antlrfour.StrategyParser;
import strategynodes.*;
import strategynodes.actions.*;
import strategynodes.basenodes.*;
import strategynodes.booloperators.*;
import strategynodes.hierarchy.*;
import strategynodes.numberofactions.*;
import strategynodes.integeroperators.*;
import strategynodes.operators.booloperators.AndOprNode;
import strategynodes.operators.booloperators.NotOprNode;
import strategynodes.operators.booloperators.OrOprNode;
import strategynodes.operators.booloperators.XorOprNode;
import strategynodes.operators.integeroperators.*;
import strategynodes.statebools.*;
import strategynodes.terminals.*;

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
    
    
    //number of actions
    @Override
    public N_ActionsOfTypeNode visitNActionsOfType(StrategyParser.NActionsOfTypeContext ctx)
    {return new N_ActionsOfTypeNode(ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));}
    @Override
    public N_ExActionsOfTypeNode visitNExecActions(StrategyParser.NExecActionsContext ctx)
    {return new N_ExActionsOfTypeNode(ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));}
    @Override
    public N_UnexActionsOfTypeNode visitNUnexActionsOfType(StrategyParser.NUnexActionsOfTypeContext ctx)
    {return new N_UnexActionsOfTypeNode(ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));}
    @Override
    public Total_N_ActionsNode visitTnActions(StrategyParser.TnActionsContext ctx)
    {return new Total_N_ActionsNode(ctx.getText());}
    @Override
    public Total_N_UnexActionsNode visitTnUnexActions(StrategyParser.TnUnexActionsContext ctx)
    {return new Total_N_UnexActionsNode(ctx.getText());}
    @Override
    public Total_N_ExActionsNode visitTnExActions(StrategyParser.TnExActionsContext ctx)
    {return new Total_N_ExActionsNode(ctx.getText());}
    
    
    //state booleans
    @Override
    public AvailableActionOfTypeNode visitAvailableActionsOftype(StrategyParser.AvailableActionsOftypeContext ctx)
    {return new AvailableActionOfTypeNode(ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));}
    @Override
    public SutTypeIsNode visitSutType(StrategyParser.SutTypeContext ctx)
    {return new SutTypeIsNode(ctx.getText(), SutType.valueOfLabel(ctx.SUT_TYPE().getText()));}
    @Override
    public StateChangedNode visitStateChanged(StrategyParser.StateChangedContext ctx)
    {return new StateChangedNode(ctx.getText());}
    @Override
    public SiblingActionExistsNode visitSiblingActionExists(StrategyParser.SiblingActionExistsContext ctx)
    {return new SiblingActionExistsNode(ctx.getText());}
    @Override
    public ChildActionExistsNode visitChildActionExists(StrategyParser.ChildActionExistsContext ctx)
    {return new ChildActionExistsNode(ctx.getText());}
    @Override
    public ChildOrSiblingActionExistsNode visitChildOrSiblingActionExists(StrategyParser.ChildOrSiblingActionExistsContext ctx)
    {return new ChildOrSiblingActionExistsNode(ctx.getText());}
    
    
    
    
    
    //action expressions
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
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new RandomActionNode(weight, ctx.getText());
    }
    @Override
    public PreviousActionNode visitPrevAction(StrategyParser.PrevActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new PreviousActionNode(weight, ctx.getText());
    }
    @Override
    public R_ActionOfType visitRActionOfType(StrategyParser.RActionOfTypeContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new R_ActionOfType(weight, ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));}
    @Override
    public R_ActionNotTypeNode visitRActionNotType(StrategyParser.RActionNotTypeContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new R_ActionNotTypeNode(weight, ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
    }
    @Override
    public R_LeastExActionNode visitRLeastExAction(StrategyParser.RLeastExActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new R_LeastExActionNode(weight, ctx.getText());
    }
    @Override
    public R_MostExActionNode visitRMostExAction(StrategyParser.RMostExActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new R_MostExActionNode(weight, ctx.getText());
    }
    @Override
    public R_UnexActionNode visitRUnexAction(StrategyParser.RUnexActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new R_UnexActionNode(weight, ctx.getText());}
    @Override
    public R_UnexActionOfTypeNode visitRUnexActionOfType(StrategyParser.RUnexActionOfTypeContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new R_UnexActionOfTypeNode(weight, ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));
    }
    @Override
    public R_UnexActionNotTypeNode visitRUnexActionNotType(StrategyParser.RUnexActionNotTypeContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new R_UnexActionNotTypeNode(weight, ctx.getText(), ActionType.valueOfLabel(ctx.ACTION_TYPE().getText()));}
    
    //hierarchy
    @Override
    public S_ChildNode visitSChildAction(StrategyParser.SChildActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new S_ChildNode(weight, ctx.getText());
    }
    @Override
    public S_SiblingNode visitSSiblingAction(StrategyParser.SSiblingActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new S_SiblingNode(weight, ctx.getText());
    }
    @Override
    public S_ChildOrSiblingNode visitSChildOrSiblingAction(StrategyParser.SChildOrSiblingActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new S_ChildOrSiblingNode(weight, ctx.getText());
    }
    @Override
    public S_SubmitAction visitSSubmitAction(StrategyParser.SSubmitActionContext ctx)
    {
        int weight = ctx.NUMBER() == null ? 1 : Integer.valueOf(ctx.NUMBER().getText());
        return new S_SubmitAction(weight, ctx.getText());
    }
}