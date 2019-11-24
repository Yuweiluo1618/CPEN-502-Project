package sample;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintStream;

import lut.Actions;
import lut.LUT;
import lut.Qlearning;
import lut.States;
import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.DeathEvent;
import robocode.HitByBulletEvent;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.RobocodeFileOutputStream;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.WinEvent;

public class LUTRobot extends AdvancedRobot {
	private static final double PI = Math.PI;
	private LUT lut;
	private Qlearning qLearning;
	private double reward = 0.0;
	private int isHitWall = 0;
    private int isHitBullet = 0;
    private double distance;
    private double bearing;
    private int state;
    private int action;
    private double rewardForWin = 200;
    private double rewardForDeath = -50;
    private double accumuReward = 0.0;
    private boolean interRewards = true;
    private boolean isSARSA = false;
    private boolean isFound = false;
    
    public void run() {
		lut = new LUT();
		loadData();
		qLearning = new Qlearning(lut);
		
		setAllColors(Color.red);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		execute();
		
		while(true) {
			turnRadarRightRadians(2*PI);
			//Get Last State
			state = getState();
			System.out.print(state);
			action = qLearning.nextAction(state);
			
			switch(action) {
			case Actions.RobotAhead:
				setAhead(Actions.RobotMoveDistance);
				break;
			case Actions.RobotBack:
				setBack(Actions.RobotMoveDistance);
				break;
			case Actions.RobotAheadTurnLeft:
				setAhead(Actions.RobotMoveDistance);
				setTurnLeft(Actions.RobotTurnDegree);
				break;
			case Actions.RobotAheadTurnRight:
				setAhead(Actions.RobotMoveDistance);
				setTurnRight(Actions.RobotTurnDegree);
				break;
			case Actions.RobotBackTurnLeft:
				setBack(Actions.RobotMoveDistance);
				setTurnLeft(Actions.RobotTurnDegree);
				break;
			case Actions.RobotBackTurnRight:
				setBack(Actions.RobotMoveDistance);
				setTurnRight(Actions.RobotTurnDegree);
				break;
			case Actions.RobotFire:
				ahead(0);
				turnLeft(0);
				smartFire();
				break;
			case Actions.Gogogo:
				setTurnRight(100);
				setMaxVelocity(4);
				ahead(100);
				smartFire();
				break;
			default:
				System.out.println("Action Not Found");
				break;
			
			}
			
			execute();
			
			turnRadarRightRadians(2*PI);
			//Update states
			state = getState();
			//Choose on policy method or off policy method
			if(isSARSA) {
				qLearning.SARSLearn(state, action, reward);
			}
			else {
			
				qLearning.offPolicy(state, action, reward);
			}
			
			accumuReward += reward;
			
			//Reset Values
			reward = 0.0;
			isHitWall = 0;
			isHitBullet = 0;
		}
		
	}

	private void smartFire() {
		isFound = false;
		while(!isFound) {
			setTurnRadarLeft(360);
			execute();
		}
		double turnGunAmt = normalRelativeAngleDegrees(bearing + getHeading() - getGunHeading());
        turnGunRight(turnGunAmt);
		double currentTargetDist = distance;
		if(currentTargetDist < 101) fire(3); 
		else if(currentTargetDist < 201) fire(2);
		else if(currentTargetDist < 301) fire(1);
		else fire(1); 
	}

	private int getState() {
		double energy = getEnergy();
		int disState = States.getDistance(distance);
		int engState = States.getEnergy(energy);
	    int state = States.getState(engState, disState, isHitWall, isHitBullet);
		return state;
	}
	
	public void onScannedRobot(ScannedRobotEvent e) {
		isFound = true;
		distance = e.getDistance();
		bearing = e.getBearing();
	}
    
	public void onBulletMissed(BulletMissedEvent e)   
    {   
		double change = -e.getBullet().getPower() * 7.5;   
		System.out.println("Bullet Missed: " + change);   
		if (interRewards) reward += change;   
    }
	
	public void onBulletHit(BulletHitEvent e)   
    {  
		    double change = e.getBullet().getPower() * 9;   
		    System.out.println("Bullet Hit: " + change);   
		    if (interRewards) reward += change;   
    }  
	
	public void onHitByBullet(HitByBulletEvent e) {
		     
			double power = e.getBullet().getPower();   
			double change = -6 * power;
			System.out.println("Hit By Bullet: " + change);   
			if (interRewards) reward += change;  
	        isHitBullet = 1;  
	}
	
	public void onHitRobot(HitRobotEvent e) {
		    bearing = e.getBearing();
		    double change = -6.0;
			System.out.println("Hit Robot: " + change);   
			if (interRewards) reward += change;   
		  
    }
	
	public void onHitWall(HitWallEvent e) {
		double change = -10.0;   
		System.out.println("Hit Wall: " + change);   
		if (interRewards) reward += change;   
        isHitWall = 1;
      
	}
	
	public void onRobotDeath(RobotDeathEvent e) {   
		if (interRewards) reward += 20;
    
	}
	
	public void onWin(WinEvent event)   
    {   
		reward+=rewardForWin;
		if(isSARSA) {
			qLearning.SARSLearn(state, action, reward);
		}
		else {
		
			qLearning.offPolicy(state, action, reward);
		}
		saveData();   
  		int winningTag=1;

  		PrintStream w = null; 
  		try { 
  			w = new PrintStream(new RobocodeFileOutputStream(getDataFile("battle_history.dat").getAbsolutePath(), true)); 
  			w.println(accumuReward+" \t"+getRoundNum()+" \t"+winningTag+" \t"+qLearning.explorationRate); 
  			if (w.checkError()) 
  				System.out.println("Could not save the data!");  
  				w.close(); 
  		} 
	    catch (IOException e) { 
	    	System.out.println("IOException trying to write: " + e); 
	    } 
	    finally { 
	    	try { 
	    		if (w != null) 
	    			w.close(); 
	    	} 
	    	catch (Exception e) { 
	    		System.out.println("Exception trying to close witer: " + e); 
	    	}
	    } 
    }
	
    public void onDeath(DeathEvent event)   
    {   
    	reward+=rewardForDeath;
    	if(isSARSA) {
			qLearning.SARSLearn(state, action, reward);
		}
		else {
		
			qLearning.offPolicy(state, action, reward);
		}
		saveData();  
       
		int losingTag = 0;
		PrintStream w = null; 
		try { 
			w = new PrintStream(new RobocodeFileOutputStream(getDataFile("battle_history.dat").getAbsolutePath(), true)); 
			w.println(accumuReward+" \t"+getRoundNum()+" \t"+losingTag+" \t"+qLearning.explorationRate); 
			if (w.checkError()) 
				System.out.println("Could not save the data!"); 
			w.close(); 
		} 
		catch (IOException e) { 
			System.out.println("IOException trying to write: " + e); 
		} 
		finally { 
			try { 
				if (w != null) 
					w.close(); 
			} 
			catch (Exception e) { 
				System.out.println("Exception trying to close witer: " + e); 
			} 
		} 
    }	

	private void saveData() {
		  try   {   
		        lut.save(getDataFile("LUT.dat"));   
		      }   
		      catch (Exception e)   {   
		        out.println("Exception trying to write: " + e);   
		      }   
		
	}   
	
	private void loadData() {
		try   {   
	        lut.load(getDataFile("LUT.dat"));  
	      }   
	      catch (Exception e)   {
	      	out.println("Exception trying to write: " + e); 
	      }   
		
	}

}
