package classifier.kaggle.multi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

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
 * The Verify class uses the trained model and the predicted data to verify the
 * classification actually worked.
 * 
 * @author jbirchfield
 * 
 */
public class Verify {

	public static void main(String[] args) throws Exception {

		String modelFile;
		modelFile = "data\\gender_new\\gender_multi_rf_ibk__all_select_attribute_rf_cost_sensitive_8tree_knn4.model".replace("\\", File.separator);
		String testFile;		
		testFile = "data\\gender_new\\test\\Survey_T8\\survey_082013_7site.matrix_normalized_select_attribute_cost_sensitive.arff".replace("\\", File.separator);			
		String trainFile;
		trainFile = "data\\gender_new\\train\\5k_7site_082013_cut15-300_id_1_normalized_select_attribute_cost_sensitive.arff".replace("\\", File.separator);
		String evaluationFile;
		evaluationFile = "data\\gender_new\\test\\Survey_T8\\evaluation_multi_rf_ibk_all_select_attribute_rf_cost_sensitive_8tree_knn4.csv".replace("\\", File.separator);
		
		/*
		 * Next we load the training data from our ARFF file
		 */
		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new File(trainFile));
		trainLoader.setRetrieval(Loader.BATCH);
		Instances trainDataSet = trainLoader.getDataSet();
		trainDataSet.deleteStringAttributes();
		
		//test set
		ArffLoader testLoader = new ArffLoader();
		testLoader.setSource(new File(testFile));
		testLoader.setRetrieval(Loader.BATCH);
		Instances testDataSet = testLoader.getDataSet();
		testDataSet.deleteStringAttributes();

		/*
		 * Now we tell the data set which attribute we want to classify, in our
		 * case, we want to classify the first column: survived
		 */
		Attribute trainAttribute = trainDataSet.attribute(trainDataSet.numAttributes()-1);
		trainDataSet.setClass(trainAttribute);
		testDataSet.setClass(trainAttribute);
		/*
		 * The RandomForest implementation cannot handle columns of type string,
		 * so we remove them for now.
		 */
		trainDataSet.deleteStringAttributes();
		testDataSet.deleteStringAttributes();
		/*
		 * Now we read in the serialized model from disk
		 */
		Classifier classifier ;//= (Classifier) SerializationHelper.read(modelFile);
		//Classifier classifier = (Classifier) (new ObjectInputStream(modelFile)).readObject();
		//RandomForest rf = (RandomForest) (new ObjectInputStream(modelFile)).readObject();
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFile));
		classifier = (Classifier) ois.readObject();
		ois.close();
		/*
		 * Next we will use an Evaluation class to evaluate the performance of
		 * our Classifier.
		 */
		Evaluation evaluation = new Evaluation(trainDataSet);		
		evaluation.evaluateModel(classifier, testDataSet);
		
		/*
		 * After we evaluate the Classifier, we write out the summary
		 * information to the screen.
		 */
		System.out.println(classifier);
		System.out.println(evaluation.toSummaryString());
		 
		PrintWriter output = new PrintWriter(evaluationFile);
		output.println(classifier);
		output.println(evaluation.toSummaryString());

		System.out.println(evaluation.toMatrixString());
		System.out.println(evaluation.toClassDetailsString());		
		output.println(evaluation.toMatrixString());
		output.println(evaluation.toClassDetailsString());	
		output.close();

	}
}
