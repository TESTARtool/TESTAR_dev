package nl.ou.testar.StateModel;

public class PredictedTransition extends AbstractStateTransition {

	public PredictedTransition(AbstractState sourceState, AbstractState targetState, PredictedAction action) {
		super(sourceState, targetState, action);
	}

}
