package classifier.kaggle.titanic;

import java.io.File;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
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
public class NBTrain {

	public static void main(String[] args) throws Exception {
		/*
		 * First we load the training data from our ARFF file
		 */
		String trainFile = "data\\sample\\train.arff".replace("\\", File.separator);
		String modelFile = "data\\NB\\titanic.model".replace("\\", File.separator);
		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new File(trainFile));
		trainLoader.setRetrieval(Loader.BATCH);
		Instances trainDataSet = trainLoader.getDataSet();

		/*
		 * Now we tell the data set which attribute we want to classify, in our
		 * case, we want to classify the first column: survived
		 */
		Attribute trainAttribute = trainDataSet.attribute(0);
		trainDataSet.setClass(trainAttribute);

		/*
		 * The RandomForest implementation cannot handle columns of type string,
		 * so we remove them for now.
		 */
		trainDataSet.deleteStringAttributes();
		
		// Create a new Classifier of type NB and configure it.		 
		NaiveBayes classifier = new NaiveBayes();
		classifier.setDebug(false);
		classifier.setUseKernelEstimator(false);
	    classifier.setDisplayModelInOldFormat(false);
	    classifier.setUseSupervisedDiscretization(false);
		
		 // Now we train the classifier		 
		classifier.buildClassifier(trainDataSet);		
		SerializationHelper.write(modelFile, classifier);
		System.out.println("Saved trained model to titanic.model");

	}
}