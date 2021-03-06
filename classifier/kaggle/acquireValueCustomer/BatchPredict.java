package classifier.kaggle.acquireValueCustomer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class BatchPredict {
	FastVector attributes;
	FastVector attributeReturn;

	public static void main(String[] args){
		BatchPredict batchPredict = new BatchPredict();
		batchPredict.init();
		batchPredict.batchPredict();
	}
	
	public void batchPredict() {
		// load all test set
		String modelFile = "data\\AcquireValueShopper\\decisionTable_bayes_trees.model".replace("\\",
				File.separator);
		String pathTest = "data/AcquireValueShopper/test_new.csv";		
		String pathPredict = "data/AcquireValueShopper/submission.csv";

		Scanner scanner;
		String line = "";
		String[] partsOfLine = null;
		String id = "";
		PrintWriter output;
		Map<String, String> testSet = new HashMap<String, String>();
		try {
			scanner = new Scanner(new File(pathTest));
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine = line.split(",");
				id = partsOfLine[0];
				testSet.put(id, line);
			}
			scanner.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		double[] returnProb;
		double prob = 0.0;
		// predict
		try {
			// load model
			Classifier classifier = (Classifier) SerializationHelper
					.read(modelFile);

			output = new PrintWriter(pathPredict);
			output.append("id,repeatProbability" + "\n");
			Iterator<String> idIterator = testSet.keySet().iterator();
			while (idIterator.hasNext()) {
				id = idIterator.next();
				line = testSet.get(id);
				Instances instances =  buildInstance(line);
				Instance instance = instances.instance(0);
				returnProb = classifier.distributionForInstance(instance);
				prob = returnProb[1];
				//prob = classifier.classifyInstance(instance);
				output.append(id + "," + prob + "\n");
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init() {
		// 1. set up attributes
		attributes = new FastVector();
		// - numeric: 1
		attributes.addElement(new Attribute("IsMarket"));
		// - numeric: 2
		attributes.addElement(new Attribute("IsMonth"));
		// - numeric: 3
		attributes.addElement(new Attribute("IsCategory"));
		// - numeric: 4
		attributes.addElement(new Attribute("IsCompany"));
		// - numeric: 5
		attributes.addElement(new Attribute("IsBrand"));
		// - numeric: 5
		attributes.addElement(new Attribute("OfferValue"));
		// - nominal: 6
		attributeReturn = new FastVector();
		attributeReturn.addElement("f");
		attributeReturn.addElement("t");
		attributes.addElement(new Attribute("Return", attributeReturn));
	}

	private Instances buildInstance(String data) {		
		Instances instance;
		double[] values;
		// 3. fill with data
		//data = "15073302,9,0,1,0,0,5,f";
		String[] s_parts = data.split(",");
		// first instance
		// 2. create Instances object
		String id = s_parts[0];
		instance = new Instances(id, attributes, 0);
		values = new double[instance.numAttributes()];

		for (int j = 1; j < s_parts.length; j++) {
			String text = s_parts[j];
			if (j == s_parts.length-1) {
				values[j - 1] = attributeReturn.indexOf(text);
			} else {
				values[j - 1] = Double.valueOf(text);
			}
		}
		//System.out.println(values[6]);
		// add
		instance.add(new DenseInstance(1.0, values));
		instance.setClass(instance.attribute(s_parts.length-2));
		return instance;
	}
}
