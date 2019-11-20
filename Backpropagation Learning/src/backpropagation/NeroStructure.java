package backpropagation;

import java.util.Scanner;

public class NeroStructure {
	

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Do you want to use Binary reprsentation?(true/false)");
	    boolean isBinary = sc.nextBoolean();
	    System.out.println("What mommentum do you want?(0.0 ~ 0.9)");
	    double momemtum = sc.nextDouble();
	    System.out.println("How many epoch trail you want?");
	    int numEpoch = sc.nextInt();
	    System.out.println("Do you want to save the data?(true/false)");
	    boolean dataSave = sc.nextBoolean();
	    Neurons prototypeNeurons = new Neurons(isBinary,momemtum,dataSave);
	    prototypeNeurons.trainSetup();
		for (int i = 0; i < numEpoch; i++) {
			prototypeNeurons.initializeWeights();
			prototypeNeurons.train();
		}
		int avgEpoch = (prototypeNeurons.epoch)/numEpoch;
		System.out.println("the average epoch : "+avgEpoch);
		
		
		sc.close();
		

	}

}
