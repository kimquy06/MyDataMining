package classifier.com.gender;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.AttributeStats;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AddClassification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;
 
public class RFTrainCrossValidation {
	public static void main(String[] args) throws Exception {
		//data
		String dataFile = "data\\gender\\train\\CV\\5k_survey_7site_082013_cut15-300.arff".replace("\\", File.separator);
		dataFile = "data\\gender\\train\\5k_7site_082013_cut15-300_balanced.arff".replace("\\", File.separator);
		//String modelFile = "data\\titanic.model".replace("\\", File.separator);
		Instances data = new Instances(new BufferedReader(new FileReader(dataFile)));
		data.setClassIndex(data.numAttributes()-1);
		AttributeStats stats = data.attributeStats(data.classIndex());
		int countClass[] = stats.nominalCounts;
		System.out.println("All data female: " + countClass[0]);
		System.out.println("All data male: " + countClass[1]);
		//folds
		int folds =5;
		//seed
		int seed =0;
		// randomize data
	    Random rand = new Random(seed);
	    Instances randData = new Instances(data);
	    randData.randomize(rand);
	    if (randData.classAttribute().isNominal())
	      randData.stratify(folds);
		//cross validation		
	    Evaluation eval = new Evaluation(randData);
		Instances predictedData = null;
		for (int n = 0; n < folds; n++) {
			System.out.println(("=== Fold " + (n+1) + "/" + folds + " ===\n"));
			Instances train = data.trainCV(folds, n);			
			//Instances train = randData.trainCV(folds, n, rand);
			System.out.println("Number instance in train set: " + train.numInstances());
			stats = train.attributeStats(train.classIndex());
			countClass = stats.nominalCounts;			
			System.out.println("Train female: " + countClass[0]);
			System.out.println("Train male: " + countClass[1]);
			train.numDistinctValues(0);
			Instances test = data.testCV(folds, n);
			System.out.println("Number instance in test set " + test.numInstances());
			stats = test.attributeStats(train.classIndex());
			countClass = stats.nominalCounts;			
			System.out.println("Test female: " + countClass[0]);
			System.out.println("Test male: " + countClass[1]);
			//Instances test = randData.trainCV(folds, n, rand);
			train.deleteStringAttributes();
			test.deleteStringAttributes();
			// the above code is used by the StratifiedRemoveFolds filter, the
			// code below by the Explorer/Experimenter:
			// Instances train = randData.trainCV(folds, n, rand);

			// build and evaluate classifier
			RandomForest classifier = new RandomForest();
			classifier.setNumTrees(1000);
			classifier.setMaxDepth(0);
			classifier.setNumFeatures(20);
			classifier.setSeed(1);
			classifier.setDebug(false);
			classifier.buildClassifier(train);
			eval = new Evaluation(train);		
			eval.evaluateModel(classifier, test);
			
			// output evaluation
		    System.out.println();
		    System.out.println(eval.toMatrixString("=== Confusion matrix for fold " + (n+1) + "/" + folds + " ===\n"));
		    System.out.println(eval.toSummaryString("\nResults\n======\n", false));
		    System.out.println(eval.toClassDetailsString());
		    
		    //class female
		    System.out.println("Female");
		    System.out.println("TP: " + eval.numTruePositives(0));
		    System.out.println("TN: " + eval.numTrueNegatives(0));
		    System.out.println("FP: " + eval.numFalsePositives(0));
		    System.out.println("FN: " + eval.numFalseNegatives(0));
		    //class male
		    System.out.println("Male");
		    System.out.println("TP: " + eval.numTruePositives(1));
		    System.out.println("TN: " + eval.numTrueNegatives(1));
		    System.out.println("FP: " + eval.numFalsePositives(1));
		    System.out.println("FN: " + eval.numFalseNegatives(1));
		}
		
		// output evaluation
	    System.out.println();	    
	   // System.out.println(evalAll.toSummaryString("=== " + folds + "-fold Cross-validation ===", false));
	
	}
}
