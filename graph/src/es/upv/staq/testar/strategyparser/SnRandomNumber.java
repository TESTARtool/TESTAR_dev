package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;
import java.util.Random;

import es.upv.staq.testar.algorithms.StateManager;

public class SnRandomNumber extends StrategyNodeNumber {

	public SnRandomNumber(ArrayList<StrategyNode> children) {
		super(children);
	}

	@Override
	public int getValue(StateManager state) {
		return new Random().nextInt(100);
	}

}
