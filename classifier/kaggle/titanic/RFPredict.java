package classifier.kaggle.titanic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;

/**
 * The Predict class uses the trained Classifier and the test data to create a
 * prediction CSV file. As noted inthe README.md, we modified the original
 * test.csv file to contain the 'survived' column. We do not actually use the
 * values of this column, weka simply requires the train and test data to match.
 * 
 * @author jbirchfield
 * 
 */
public class RFPredict {

	public static void main(String[] args) throws Exception {
		String testFile = "data\\test.arff".replace("\\", File.separator);
		String modelFile = "data\\titanic.model".replace("\\", File.separator);
		String predictFile = "data\\predict.csv".replace("\\", File.separator);		
		// load unlabeled data
		System.out.println("load unlabeled data");
		Instances unlabeled = new Instances(new BufferedReader(new FileReader(testFile)));
		// set class attribute
		unlabeled.setClassIndex(0);
		// create copy
		Instances labeled = new Instances(unlabeled);
		//model
		Classifier classifier = (Classifier) SerializationHelper.read(modelFile);
		// label instances
		for (int i = 0; i < unlabeled.numInstances(); i++) {
			double clsLabel = classifier.classifyInstance(unlabeled.instance(i));
			labeled.instance(i).setClassValue(clsLabel);
		}		 
		// save labeled data
		BufferedWriter writer = new BufferedWriter(new FileWriter(predictFile));
		writer.write(labeled.toString());
		writer.newLine();
		writer.flush();
		writer.close();
	}
}