package es.upv.staq.testar.strategyparser;

import java.util.ArrayList;


public abstract class StrategyNode {
	ArrayList<StrategyNode> children;
	
	public StrategyNode(ArrayList<StrategyNode> children){
		this.children = children;
	}
	
	public void print(int level){
		for (int i = 0; i < level; i++){
			System.out.print("-");
		}
		System.out.println(this.getClass().getSimpleName());
		for (StrategyNode c : children){
			c.print(level + 1);
		}
	}
	
}
