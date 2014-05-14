package classifier.kaggle.acquireValueCustomer;

import java.io.File;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.meta.ClassificationViaRegression;
import weka.classifiers.meta.LogitBoost;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;


/**
 * @author quyntk
 * 
 */
public class RFTrain {

	public static void main(String[] args) throws Exception {
		/*
		 * First we load the training data from our ARFF file
		 */
		String trainFile = "data\\AcquireValueShopper\\train_55features.arff".replace("\\", File.separator);
		String modelFile = "data\\AcquireValueShopper\\cr_55features.model".replace("\\", File.separator);
		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new File(trainFile));
		trainLoader.setRetrieval(Loader.BATCH);
		Instances trainDataSet = trainLoader.getDataSet();		
		
		Attribute trainAttribute = trainDataSet.attribute(trainDataSet.numAttributes() - 1);
		trainDataSet.setClass(trainAttribute);

		
		trainDataSet.deleteStringAttributes();
		
		// Create a new Classifier of type RandomForest and configure it.			
		RandomForest randomForest = new RandomForest();
		//randomForest.setNumTrees(1000);
		//randomForest.setMaxDepth(0);
		//randomForest.setNumFeatures(3);
		//randomForest.setSeed(1);
		//randomForest.setDebug(false);		 
		//randomForest.buildClassifier(trainDataSet);	
		//regression
		AdditiveRegression aRegression = new AdditiveRegression();
		//aRegression.buildClassifier(trainDataSet);
		LogitBoost logiBoost = new LogitBoost();
		//logiBoost.buildClassifier(trainDataSet);
		ClassificationViaRegression cr = new ClassificationViaRegression();
		cr.buildClassifier(trainDataSet);
		//Decision tree
		DecisionTable decisionTable = new DecisionTable();
		//decisionTable.buildClassifier(trainDataSet);		
		//Naive Bayes
		NaiveBayes naiveBayes = new NaiveBayes();
		//naiveBayes.buildClassifier(trainDataSet);
		
		//iBk
		IBk ibk = new IBk();
		//ibk.buildClassifier(trainDataSet);
		
		//j48
		J48 j48 = new J48();
		j48.setDebug(true);
		j48.setNumFolds(10);
		//j48.buildClassifier(trainDataSet);
		//lmt
		LMT lmt = new LMT();
		lmt.setDebug(true);
		//lmt.buildClassifier(trainDataSet);
		//rep tree
		REPTree repTree = new REPTree();
		repTree.setDebug(true);
		repTree.setNumFolds(10);
		//repTree.buildClassifier(trainDataSet);
		
		//random tree
		RandomTree randomTree = new RandomTree();
		randomTree.setDebug(true);
		randomTree.setNumFolds(10);
		//randomTree.buildClassifier(trainDataSet);
		
		//write out
		SerializationHelper.write(modelFile, cr);
		System.out.println("Saved trained model");	

	}
}