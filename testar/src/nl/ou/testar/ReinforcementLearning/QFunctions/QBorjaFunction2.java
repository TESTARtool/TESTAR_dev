package nl.ou.testar.ReinforcementLearning.QFunctions;

import java.util.Set;

import org.fruit.alayer.Action;
import org.fruit.alayer.Tag;

import nl.ou.testar.StateModel.AbstractAction;
import nl.ou.testar.StateModel.AbstractState;

public class QBorjaFunction2 implements QFunction {
	
	// TODO Borja: Think how to move enfoque 2 QFunctions implementation here

	@Override
	public float getQValue(Tag<Float> rl_tag, AbstractAction previousActionUnderExecution,
			AbstractAction actionUnderExecution, float reward, AbstractState currentAbstractState,
			Set<Action> actions) {
		// TODO Auto-generated method stub
		return 0;
	}

}
