package classifier.fpt.gender;



import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;
import weka.core.Instance;
import weka.core.FastVector;
import weka.classifiers.trees.RandomForest;
/**
 * @author quyntk
 * 
 */
public class Verify {

	public static void main(String[] args) throws Exception {

		String modelFile = "data\\gender\\gender_5k_balanced.model".replace("\\", File.separator);
		modelFile = "data\\gender_new\\model\\5k_rf_not_cv.model".replace("\\", File.separator);
		//modelFile = "data\\gender\\gender_5k_balanced.model".replace("\\", File.separator);		
		//modelFile = "data\\gender\\gender_5k_survey__balanced.model".replace("\\", File.separator);
		//modelFile = "data\\gender\\gender_5k_survey_balanced.model".replace("\\", File.separator);
		 //modelFile = "data\\gender\\gender_survey_t8.model".replace("\\", File.separator);
		String testFile;// = "data\\gender\\test\\5K_T9\\5k_7site_092013_cut15-300_id.arff".replace("\\", File.separator);			
		//testFile = "data\\gender\\test\\5K_T9\\5k_7site_092013_cut15-300_id.arff".replace("\\", File.separator);
		//testFile = "data\\gender\\train\\CV\\5k_survey_7site_082013_cut15-300_balanced.arff".replace("\\", File.separator);
		testFile = "data\\gender\\test\\Survey_T8\\survey_082013_7site.matrix.arff".replace("\\", File.separator);	
		//testFile = "data\\gender\\test\\Survey_T9\\survey_092013_7site.matrix.arff".replace("\\", File.separator);
		String trainFile = "data\\gender\\train\\CV\\5k_survey_7site_082013_cut15-300.arff".replace("\\", File.separator);
		//trainFile = "data\\gender\\train\\5k_7site_082013_cut15-300_balanced.arff".replace("\\", File.separator);
		//trainFile = "data\\gender\\train\\CV\\5k_survey_7site_082013_cut15-300_balanced.arff".replace("\\", File.separator);
		trainFile = "data\\gender\\train\\5k_7site_082013_cut15-300_balanced.arff".replace("\\", File.separator);
		String evaluationFile;// = "data\\gender\\test\\5K_T9\\evaluation_5000tree_20feature.csv".replace("\\", File.separator);
		evaluationFile = "data\\gender\\test\\Survey_T9\\evaluation.csv".replace("\\", File.separator);
		//CSVLoader predictCsvLoader = new CSVLoader();
		//predictCsvLoader.setSource(new File(predictFile));

		/*
		 * Since we are not using the ARFF format here, we have to give the
		 * loader a little bit of information about the data types. Columns
		 * 3,8,10 need to be of type string and columns 1,4,11 are nominal
		 * types.
		 */
		//predictCsvLoader.setStringAttributes("3,8,10");
		//predictCsvLoader.setNominalAttributes("1,4,11");
		//Instances predictDataSet = predictCsvLoader.getDataSet();

		/*
		 * Here we set the attribute we want to test the predicitons with
		 */
		//Attribute testAttribute = predictDataSet.attribute(predictDataSet.numAttributes()-1);
		//predictDataSet.setClass(testAttribute);

		/*
		 * We still have to remove all string attributes before we can test
		 */
		//predictDataSet.deleteStringAttributes();

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
		/*for (int i = 0; i < testDataSet.numInstances(); i++) {
			   double pred = classifier.classifyInstance(testDataSet.instance(i));
			   //System.out.print("ID: " + testDataSet.instance(i).value(0));
			   System.out.print(i+1 + " actual: " + testDataSet.classAttribute().value((int) testDataSet.instance(i).classValue()));
			   output.print(i+1 + " actual: " + testDataSet.classAttribute().value((int) testDataSet.instance(i).classValue()));
			   String act =testDataSet.classAttribute().value((int) testDataSet.instance(i).classValue());
			   String pre = testDataSet.classAttribute().value((int) pred);
			   boolean test = false;
			   if(act.equals(pre)){
				   test=true;
			   }
			   System.out.println(", predicted: " + testDataSet.classAttribute().value((int) pred) + ", " + test);
			   output.println(", predicted: " + testDataSet.classAttribute().value((int) pred) + ", " + test);
			 }*/
		System.out.println(evaluation.toMatrixString());//("\nResults\n======\n", false));
		System.out.println(evaluation.toClassDetailsString());		
		output.println(evaluation.toMatrixString());//("\nResults\n======\n", false));
		output.println(evaluation.toClassDetailsString());	
		output.close();
	}
}