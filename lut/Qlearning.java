package lut;

import java.util.Random;

public class Qlearning {
	private double alpha = 0.1;
	private double gamma = 0.9;
	public double explorationRate = 0;
	private int currentState;
	private int currentAction;
	private boolean isFirstState = true;
	private LUT lut;
	
	public Qlearning(LUT lut) {
		this.lut = lut;
	}
	//On policy method
	public void SARSLearn(int nextState, int nextAction, double reward) {
		double lastQVal;
		double newQVal;
		if(isFirstState) {
			isFirstState = false;			
		}
		else {
			lastQVal = lut.getQvalue(currentState, currentAction);
			newQVal = lastQVal + alpha*(reward + gamma * lut.getQvalue(nextState, nextAction) - lastQVal);
			lut.setQvalue(currentState, currentAction, newQVal);
		}
		
		currentState = nextState;
		currentAction = nextAction;
	}
	//Off policy method
	public void  offPolicy(int nextState, int nextAction, double reward) {
		double lastQ;
		double newQ;
		if(isFirstState) {
			isFirstState = false;
		}else {
			lastQ = lut.getQvalue(currentState, currentAction);
			newQ  = lastQ + alpha*(reward + gamma * lut.getMaxQvalue(nextState)-lastQ);
			lut.setQvalue(currentState, currentAction, newQ);
		}
		currentState =  nextState;
		currentAction = nextAction;
}
	
	public int nextAction(int state) {
		double probability = Math.random();
		int action = 0;
		if(probability < explorationRate) {
			Random rand = new Random();
			action = rand.nextInt(Actions.numActions);
		}else {
			
			action = lut.getBestAction(state);
		}
		return action;
		
	}
	
	
	
	

}
