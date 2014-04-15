package classifier.kaggle.titanic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.EvaluationUtils;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.core.converters.CSVSaver;
import weka.core.converters.Loader;

/**
 * @author quyntk
 * 
 */
public class RFVerify {

	public static void main(String[] args) throws Exception {

		String modelFile = "data\\RF\\titanic.model".replace("\\", File.separator);
		String testFile = "data\\RF\\test.arff".replace("\\", File.separator);
		String trainFile = "data\\RF\\train.arff".replace("\\", File.separator);
		
		Instances train = new Instances(new BufferedReader(new FileReader(trainFile)));
		train.setClassIndex(0);
		Instances test = new Instances(new BufferedReader(new FileReader(testFile)));
		test.setClassIndex(0);
		//model
		RandomForest classifier = (RandomForest) SerializationHelper.read(modelFile);
		//Classifier classifier = (Classifier) SerializationHelper.read(modelFile);
		/*RandomForest classifier = new RandomForest();
		classifier.setNumTrees(500);
		classifier.setDebug(true);		 		 
		classifier.buildClassifier(train);	*/	
		// evaluate classifier and print some statistics
		Evaluation evaluation = new Evaluation(train);
		evaluation.evaluateModel(classifier, test);
		System.out.println(evaluation.toSummaryString("\nResults\n======\n", false));
		
		for (int i = 0; i < test.numInstances(); i++) {
			   double pred = classifier.classifyInstance(test.instance(i));
			   System.out.print("ID: " + test.instance(i).value(0));
			   System.out.print(", actual: " + test.classAttribute().value((int) test.instance(i).classValue()));		   
			   System.out.println(", predicted: " + test.classAttribute().value((int) pred));
			 }
		System.out.println("F-measure 1: " + evaluation.fMeasure(1) );
		System.out.println("F-measure 0: " + evaluation.fMeasure(0) );

	}
}