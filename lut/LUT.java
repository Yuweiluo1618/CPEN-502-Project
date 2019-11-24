package lut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;


import robocode.RobocodeFileOutputStream;


public class LUT implements LUTInterface {
	private int bestAction = 0;
	
	//Look up Table
	private double [][] table;
	
	
	public LUT() {
		this.table = new double[States.numStates][Actions.numActions];
		initialiseLUT();
	}

	public double getQvalue(int state, int action) {
		return table[state][action];
	}
	
	public void setQvalue(int state,int action,double value) {
		table[state][action] = value;
	}
	
	public double getMaxQvalue(int state) {
		double maxQvalue = -999999;
		for(int i = 0; i < table[state].length;i++) {
			if(table[state][i] > maxQvalue) {
				maxQvalue = table[state][i];
				bestAction = i;
			}
			
		}
		return maxQvalue;
		
	}
	
	 public int getBestAction(int state) {
		 getMaxQvalue(state);
		 return bestAction;
	}

	@Override
	public double outputFor(double[] X) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double train(double[] X, double argValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void save(File argFile) {
		PrintStream saveFile = null;   
	    try   {   
	    	saveFile = new PrintStream(new RobocodeFileOutputStream(argFile));   
  			for (int i = 0; i < States.numStates; i++)   
  				for (int j = 0; j < Actions.numActions; j++)   
  					saveFile.println(table[i][j]);  
  			
  			if (saveFile.checkError()) {  
  				System.out.println("Could not save the data!");   
  			    }
	    }   
	    catch (IOException e)   {   
	    	System.out.println("IOException trying to write: " + e);   
	    }   
	    finally   {   
	    	try   {   
	    		if (saveFile != null)   
	    			saveFile.close();   
	    	}   
	    	catch (Exception e)   {   
	    		System.out.println("Exception trying to close witer: " + e);   
	    	}   
	    }   

	}

	@Override
	public void load(File argFileName) throws IOException {
		BufferedReader read = null;   
	    try   {   
	    	read = new BufferedReader(new FileReader(argFileName));   
	    	for (int i = 0; i < States.numStates; i++)   
	    		for (int j = 0; j < Actions.numActions; j++)   
	    			table[i][j] = Double.parseDouble(read.readLine());   
	    }   
	    catch (IOException e)   {   
	    	System.out.println("IOException trying to open reader: " + e);   
	    	initialiseLUT();   
	    }   
	    catch (NumberFormatException e)   {   
	    	initialiseLUT();   
	    }  
	    finally {   
	    	try {   
		        if (read != null)   
		        	read.close();   
	        }   
	    	catch (IOException e) {   
	    		System.out.println("IOException trying to close reader: " + e);   
	    	}   
	    }   

	}

	@Override
	public void initialiseLUT() {
		for(int i = 0; i < States.numStates; i++) {
			for(int j = 0; j < Actions.numActions; j++) {
				table[i][j] = Math.random();
			}
		}

	}

	@Override
	public int indexFor(double[] X) {
		// TODO Auto-generated method stub
		return 0;
	}


}
