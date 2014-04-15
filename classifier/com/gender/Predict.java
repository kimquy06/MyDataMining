package classifier.com.gender;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
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
public class Predict {

	public static void main(String[] args) throws Exception {

		/*
		 * First we load the test data from our ARFF file
		 */
		String testFile = "data\\gender\\test\\5K_T9\\5k_7site_092013_cut15-300_id.arff".replace("\\", File.separator);
		String modelFile = "data\\gender\\gender.model".replace("\\", File.separator);
		String predictFile = "data\\gender\\test\\5K_T9\\predict.csv".replace("\\", File.separator);
		ArffLoader testLoader = new ArffLoader();
		testLoader.setSource(new File(testFile));
		testLoader.setRetrieval(Loader.BATCH);
		Instances testDataSet = testLoader.getDataSet();

		/*
		 * Now we tell the data set which attribute we want to classify, in our
		 * case, we want to classify the first column: survived
		 */
		Attribute testAttribute = testDataSet.attribute(testDataSet.numAttributes()-1);
		testDataSet.setClass(testAttribute);
		testDataSet.deleteStringAttributes();

		/*
		 * Now we read in the serialized model from disk
		 */
		Classifier classifier = (Classifier) SerializationHelper
				.read(modelFile);

		/*
		 * This part may be a little confusing. We load up the test data again
		 * so we have a prediction data set to populate. As we iterate over the
		 * first data set we also iterate over the second data set. After an
		 * instance is classified, we set the value of the prediction data set
		 * to be the value of the classification
		 */
		ArffLoader test1Loader = new ArffLoader();
		test1Loader.setSource(new File(testFile));
		Instances test1DataSet = test1Loader.getDataSet();
		Attribute test1Attribute = test1DataSet.attribute(test1DataSet.numAttributes()-1);
		test1DataSet.setClass(test1Attribute);

		/*
		 * Now we iterate over the test data and classify each entry and set the
		 * value of the 'survived' column to the result of the classification
		 */
		Enumeration testInstances = testDataSet.enumerateInstances();
		Enumeration test1Instances = test1DataSet.enumerateInstances();
		while (testInstances.hasMoreElements()) {
			Instance instance = (Instance) testInstances.nextElement();
			Instance instance1 = (Instance) test1Instances.nextElement();
			double classification = classifier.classifyInstance(instance);
			instance1.setClassValue(classification);
		}

		/*
		 * Now we want to write out our predictions. The resulting file is in a
		 * format suitable to submit to Kaggle.
		 */
		CSVSaver predictedCsvSaver = new CSVSaver();
		predictedCsvSaver.setFile(new File(predictFile));
		predictedCsvSaver.setInstances(test1DataSet);
		predictedCsvSaver.writeBatch();

		System.out.println("Prediciton saved to predict.csv");

	}
}