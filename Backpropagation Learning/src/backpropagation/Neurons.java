package backpropagation;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;




public class Neurons implements NeuralNetInterface {
	private int numInput = 2;
	private int numOutput = 1;
	private int numHidenNeros = 4;
	private boolean binary = false;
	private double learningRate = 0.2;
	private double momentum = 0.2;
	public int epoch = 0;
	private boolean dataSave = true;
	//input
	private double[][] binaryInput = {{1,0},{0,1},{1,1},{0,0}};
	private double[][] bipolarInput = {{1,-1},{-1,1},{1,1},{-1,-1}};
	//output
	private double[][] binaryOutput = {{1},{1},{0},{0}};
	private double[][] bipolarOutput = {{1},{1},{-1},{-1}};
	
	//hidden layer
	private double[] layerSum = new double[numHidenNeros+1];
	private double[] outputSum = new double[numOutput];
    private double[] inputLayer = new double[numInput + 1];
    private double[][] deltaHI = new double[numInput + 1][numHidenNeros];
    private double[][] deltaOH = new double[numHidenNeros + 1][numOutput];
    
	//weight variable
	private double floor = -0.5;
	private double[][] weightIntoHide = new double[numInput+1][numHidenNeros];
	private double[][] weighttHidetoOut = new double[numHidenNeros+1][numOutput];
	//train input
	private double[][] trainInput;
	private double[][] trainOutput;
	//errors
	private double [] errorOH = new double[numOutput];
	private double [] errorHI = new double[numHidenNeros];
	private double[] singleError = new double[numOutput];
	public double [] totalError = new double[numOutput];
	
	
	
	public Neurons(boolean binary, double momentum,boolean dataSave) {
		this.binary = binary;
		this.momentum = momentum;
		this.dataSave = dataSave;
	}
	public void trainSetup() {
		if(binary) {
			trainInput = binaryInput;
			trainOutput = binaryOutput;
			
		}
		else {
			trainInput = bipolarInput;
			trainOutput = bipolarOutput;
			
		}
		
	}
	public void forwardPropagation(double [] input) {
		inputSetup(input);
		//input to hidden
		for(int i = 0; i < numHidenNeros;i++) {
			for(int j = 0; j < numInput+1; j++) {
				layerSum[i] += inputLayer[j]*weightIntoHide[j][i]; 
			}
			if(binary) {
				layerSum[i] = sigmoid(layerSum[i]);
			}else {
				// add bipolar
				layerSum[i] = customSigmoid(layerSum[i]);
			}
		}
		//Hidden to output
		for(int i = 0; i < numOutput;i++) {
			for(int j = 0; j < numHidenNeros+1; j++) {
				outputSum[i] += layerSum[j]*weighttHidetoOut[j][i];
			}
			if(binary) {
				outputSum[i] = sigmoid(outputSum[i]);
			}else {
				//add bipolar
				outputSum[i] = customSigmoid(outputSum[i]);
			}
		}
		  
			
	}
	public void backPropagation() {
		//compute errorOH
		for(int i = 0; i < numOutput; i++) {
			if(binary) {
				errorOH[i] = outputSum[i]*(1-outputSum[i])*singleError[i];
			}else {
				//add bipolar
				errorOH[i] = 0.5*(1-(outputSum[i]*outputSum[i]))*singleError[i];
			}
			
		}
		//update the weightOuttoHide
		for(int i = 0; i < numOutput; i++) {
			for(int j = 0; j < numHidenNeros+1; j++) {
				deltaOH[j][i] = momentum*deltaOH[j][i]+learningRate*errorOH[i]*layerSum[j];
				weighttHidetoOut[j][i] += deltaOH[j][i];
			}
		}
		//compute errorHI
		for(int i = 0; i < numHidenNeros; i++) {
			for(int j = 0; j < numOutput; j++) {
				errorHI[i] += weighttHidetoOut[i][j]*errorOH[j]; 
			}
			if(binary) {
				errorHI[i] = layerSum[i]*(1-layerSum[i])*errorHI[i];
			}else {
				//add bipolar
				errorHI[i] = 0.5*(1-(layerSum[i]*layerSum[i]))*errorHI[i];
			}
		}
		//update the wheightHidetoIn
		for(int i = 0; i < numHidenNeros;i++) {
			for(int j = 0; j < numInput+1; j++) {
				deltaHI[j][i] = momentum*deltaHI[j][i]+learningRate*errorHI[i]*inputLayer[j];
				weightIntoHide[j][i] += deltaHI[j][i]; 
			}
		}
		
		
		
		
		
	}

	@Override
	public double outputFor(double[] X) {
		
		return 0;
	}

	@Override
	public double train() {
		int numInput = 0;
		do {
			totalError[0] = 0;
			numInput = trainInput.length;
			for(int i = 0; i<numInput; i++ ) {
				double[] input = trainInput[i]; 
				forwardPropagation(input);
				for(int j = 0; j<numOutput; j++ ) {
					singleError[j] = trainOutput[i][j] - outputSum[j]; 
					totalError[j] += Math.pow(singleError[j],2);
					
				}
				backPropagation();
			}
			for (int k = 0; k < numOutput; k++) {
                totalError[k] = totalError[k]/2;
                System.out.println(totalError[k]);
            }
			/*if(dataSave){
				save("C:/Users/weskerlyw/Desktop/UBC/1st Term/a.txt");
			}*/
			epoch++;
		}while(totalError[0] > 0.05);
		System.out.println("# epoch " + epoch + "\n");
		if(dataSave){
			save("C:/Users/weskerlyw/Desktop/UBC/1st Term/a.txt");
		}
		epoch = 0;
		return 0;
		
	}
	
	@Override
	public void save(String string) {
		
		try {
			FileOutputStream fos = new FileOutputStream(string,true);
			OutputStreamWriter osw = new OutputStreamWriter(fos);
			 PrintWriter pw = new PrintWriter(osw);
			 pw.println(epoch);
			 pw.close();
			 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
				
		

	}

	@Override
	public void load(String argFileName) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public double sigmoid(double x) {
		
		return (1 / (1 + Math.exp(-x)));
		
	}

	@Override
	public double customSigmoid(double x) {
		return ((-1)+(2/(1+Math.exp(-x))));
	}

	@Override
	public void initializeWeights() {
		double weight;
		double inRand;
		// weight from input to hide
		for(int i = 0; i <numInput+1; i++) {
			for(int j = 0; j < numHidenNeros; j++) {
			    inRand = Math.random();
				weight = floor+inRand;
				weightIntoHide[i][j] = weight;
				}
		}
		
		//weight from hide to output
		for(int i = 0; i < numHidenNeros+1;i++) {
			for(int j = 0; j < numOutput; j++) {
				inRand = Math.random();
				weight = floor+inRand;
				weighttHidetoOut[i][j] = weight;
			}
		}
		
		
	}
	 
	public void inputSetup(double[] input) {
	        for (int i = 0; i < numInput; i++) {
	            inputLayer[i] = input[i];
	        }
	        inputLayer[numInput] = 1;
	        layerSum[numHidenNeros] = 1;
	}

	@Override
	public void zeroWeights() {
		// TODO Auto-generated method stub

	}



}
