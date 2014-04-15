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
public class NBVerify {

	public static void main(String[] args) throws Exception {

		String modelFile = "data\\NB\\titanic.model".replace("\\", File.separator);
		String testFile = "data\\sample\\test.arff".replace("\\", File.separator);
		String trainFile = "data\\sample\\train.arff".replace("\\", File.separator);
		
		Instances train = new Instances(new BufferedReader(new FileReader(trainFile)));
		train.setClassIndex(0);
		Instances test = new Instances(new BufferedReader(new FileReader(testFile)));
		test.setClassIndex(0);
		//model
		Classifier classifier = (Classifier) SerializationHelper.read(modelFile);		
		// evaluate classifier and print some statistics
		Evaluation evaluation = new Evaluation(train);
		evaluation.evaluateModel(classifier, test);
		//eval.evaluateModel(classifier, test, new Object[] {});
		System.out.println(evaluation.toSummaryString("\nResults\n======\n", false));
		System.out.println("F-measure 1: " + evaluation.fMeasure(1) );
		System.out.println("F-measure 0: " + evaluation.fMeasure(0) );

	}
}