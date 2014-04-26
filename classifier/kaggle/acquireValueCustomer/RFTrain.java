package classifier.kaggle.acquireValueCustomer;

import java.io.File;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;
import hr.irb.fastRandomForest.FastRandomForest;

/**
 * @author quyntk
 * 
 */
public class RFTrain {

	public static void main(String[] args) throws Exception {
		/*
		 * First we load the training data from our ARFF file
		 */
		String trainFile = "data\\AcquireValueShopper\\train_brand_company_cate.arff".replace("\\", File.separator);
		String modelFile = "data\\AcquireValueShopper\\decisonTable_4feature.model".replace("\\", File.separator);
		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new File(trainFile));
		trainLoader.setRetrieval(Loader.BATCH);
		Instances trainDataSet = trainLoader.getDataSet();		
		
		Attribute trainAttribute = trainDataSet.attribute(trainDataSet.numAttributes() - 1);
		trainDataSet.setClass(trainAttribute);

		
		trainDataSet.deleteStringAttributes();
		
		// Create a new Classifier of type RandomForest and configure it.			
		RandomForest randomForest = new RandomForest();
		randomForest.setNumTrees(1000);
		randomForest.setMaxDepth(0);
		randomForest.setNumFeatures(3);
		randomForest.setSeed(1);
		randomForest.setDebug(false);		 
		//randomForest.buildClassifier(trainDataSet);	
		//Decision tree
		DecisionTable decisionTable = new DecisionTable();
		decisionTable.buildClassifier(trainDataSet);		
		//Naive Bayes
		NaiveBayes naiveBayes = new NaiveBayes();
		//naiveBayes.buildClassifier(trainDataSet);
		
		//iBk
		IBk ibk = new IBk();
		//ibk.buildClassifier(trainDataSet);
		//write out
		SerializationHelper.write(modelFile, decisionTable);
		System.out.println("Saved trained model");
		

	}
}