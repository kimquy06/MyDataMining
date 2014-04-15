package classifier.com.gender;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

/**
 * @author quyntk
 * 
 */
public class Train {

	public static void main(String[] args) throws Exception {
		/*
		 * First we load the training data from our ARFF file
		 */
		String trainFile = "data\\gender\\train\\5k_7site_082013_cut15-300_balanced.arff".replace("\\", File.separator);
		trainFile = "data\\gender\\test\\Survey_T8\\survey_082013_7site.matrix.arff".replace("\\", File.separator);	
		//trainFile = "data\\gender\\train\\CV\\5k_survey_7site_082013_cut15-300_balanced.arff".replace("\\", File.separator);
		String evaluationFile = "data\\gender\\train\\CV\\cross.csv".replace("\\", File.separator);
		String modelFile = "data\\gender\\gender_survey_t8.model".replace("\\", File.separator);
		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new File(trainFile));
		trainLoader.setRetrieval(Loader.BATCH);
		Instances trainDataSet = trainLoader.getDataSet();

		/*
		 * Now we tell the data set which attribute we want to classify, in our
		 * case, we want to classify the first column: survived
		 */
		Attribute trainAttribute = trainDataSet.attribute(trainDataSet.numAttributes() - 1);
		trainDataSet.setClass(trainAttribute);

		/*
		 * The RandomForest implementation cannot handle columns of type string,
		 * so we remove them for now.
		 */
		trainDataSet.deleteStringAttributes();
		

		
		// Create a new Classifier of type RandomForest and configure it.
		 
		
		 /*
		  * ntree (default = 500) = overall number of trees in the forest 
			The number of trees should be increased as the number of variables or data points increase. 
			A suitably large number of trees will guarantee more stable and robust results. (e.g., having 
			ntree=8000 for a sparse dataset with a large number of predictors is not unheard of.)
		  */
		 RandomForest classifier = new RandomForest();
		 classifier.setNumTrees(1000);
		 classifier.setMaxDepth(0);
		 classifier.setNumFeatures(20);
		 classifier.setSeed(1);
		 classifier.setDebug(false);		
	     Evaluation eval = new Evaluation(trainDataSet);
	     eval.crossValidateModel(classifier, trainDataSet, 5, new Random(1));		
		 PrintWriter output = new PrintWriter(evaluationFile);
		 output.println(classifier);
		 output.println(eval.toSummaryString());
		 output.println(eval.toMatrixString());//("\nResults\n======\n", false));
		 output.println(eval.toClassDetailsString());	
		 output.close();
		 System.out.println(classifier);
		 System.out.println(eval.toSummaryString());
		 System.out.println(eval.toMatrixString());//("\nResults\n======\n", false));
		 System.out.println(eval.toClassDetailsString());	
	/*	 
		NaiveBayes classifier = new NaiveBayes();
		classifier.setDebug(false);
		classifier.setUseKernelEstimator(false);
	    classifier.setDisplayModelInOldFormat(false);
	    classifier.setUseSupervisedDiscretization(false);*/
		 // Now we train the classifier
		 
		classifier.buildClassifier(trainDataSet);

		//fast random forest
		/*FastRandomForest rf = new FastRandomForest();
		rf.setNumTrees(100);
		try {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}*/

		
		/* NaiveBayesUpdateable nb = new NaiveBayesUpdateable();
		 nb.buildClassifier(trainDataSet);*/
		/*
		 * We are done training the classifier, so now we serialize it to disk
		 */
		SerializationHelper.write(modelFile, classifier);
		System.out.println("Saved trained model to titanic.model");

	}
}