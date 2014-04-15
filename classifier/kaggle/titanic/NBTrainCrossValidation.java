package classifier.kaggle.titanic;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;
 
public class NBTrainCrossValidation {
	public static void main(String[] args) throws Exception {
		//data
		String dataFile = "data\\train.arff".replace("\\", File.separator);
		String modelFile = "data\\titanic.model".replace("\\", File.separator);
		Instances data = new Instances(new BufferedReader(new FileReader(dataFile)));
		data.setClassIndex(0);
		//folds
		int folds =10;
		//cross validation
		Evaluation eval = new Evaluation(data);
		for (int n = 0; n < folds; n++) {
			Instances train = data.trainCV(folds, n);
			Instances test = data.testCV(folds, n);
			// the above code is used by the StratifiedRemoveFolds filter, the
			// code below by the Explorer/Experimenter:
			// Instances train = randData.trainCV(folds, n, rand);

			// build and evaluate classifier
			NaiveBayesUpdateable classifier = new NaiveBayesUpdateable();
			classifier.buildClassifier(train);
			eval.evaluateModel(classifier, test);
			
			// output evaluation
		    System.out.println();
		    System.out.println(eval.toMatrixString("=== Confusion matrix for fold " + (n+1) + "/" + folds + " ===\n"));
		    System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		    

		}
		
		// output evaluation
	    System.out.println();
	   // System.out.println(evalAll.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
	
	}
}
