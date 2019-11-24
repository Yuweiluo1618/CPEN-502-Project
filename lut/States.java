package lut;


public class States {
	public static int numEnergy = 3;
	public static int numDistance = 3;
	public static int numHitWall = 2;
	public static int numHitByBullet = 2;
	public static int numStates;
	public static int [][][][] statesMap;
	static {
		statesMap = new int[numEnergy][numDistance][numHitWall][numHitByBullet];
		int count = 0;
		for(int i = 0; i < numEnergy; i++) {
			for(int j = 0; j < numDistance;j++) {
				for(int k = 0; k < numHitWall; k++) {
					for(int l = 0; l < numHitByBullet; l++ ) {
						statesMap[i][j][k][l] = count++;
					    
					}
				}
			}
		}
		numStates = count;
		
		
	}
	
	public static int getDistance(double value) {
		int disState = 0;
		if(value <= 100) {
			disState = 0;
		}
		else if(value > 100 && value <= 500) {
			disState = 1;
		}
		else if(value > 500) {
			disState = 2;
		}
		return disState;
	}
	
	public static int getEnergy(double value) {
		int engState = 0;
		if(value <= 30) {
			engState = 0;
		}
		else if(value > 30 && value <= 60) {
			engState = 1;
		}
		else if(value > 60) {
			engState = 2;
		}
		return engState;
	}
	
	public static int getState(int energy, int distance, int hitWall, int hitByBullet) {
		return statesMap[energy][distance][hitWall][hitByBullet];
	}

}
