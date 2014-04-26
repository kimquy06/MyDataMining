package classifier.kaggle.acquireValueCustomer;

import java.io.File;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;

public class PredictOneInstance {
	public static void main(String[] args) {
		// Attribute:
		// Id,IsMarket,IsMonth,IsCategory,IsCompany,IsBrand,OfferValue
		String modelFile = "data\\AcquireValueShopper\\RF.model".replace("\\",File.separator);
		FastVector attributes;
		FastVector attributeReturn;
		Instances inputInstance;
		double[] values;
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

		// 3. fill with data
		String sampledata = "15073302,9,0,1,0,0,5,f";
		String[] s_parts = sampledata.split(",");
		// first instance
		// 2. create Instances object
		String id = s_parts[0];
		inputInstance = new Instances(id, attributes, 0);
		values = new double[inputInstance.numAttributes()];
		
		for (int j = 1; j < s_parts.length; j++) {
			String text = s_parts[j];
			if(j==7){
				values[j-1] = attributeReturn.indexOf(text);
			}else{
				values[j-1] = Double.valueOf(text);
			}			
		}
		System.out.println(values[6]);
		// add
		inputInstance.add(new DenseInstance(1.0, values));
		inputInstance.setClass(inputInstance.attribute(6));
		/*
		 * Now we read in the serialized model from disk
		 */
		try {
			Classifier classifier = (Classifier) SerializationHelper
					.read(modelFile);
			Instance instance = inputInstance.instance(0);
			double classification = classifier.classifyInstance(instance);
			double[] returnProb = classifier.distributionForInstance(instance);
			int index = (int)classification;
			System.out.println(id + " " + classification + " " + 
			attributeReturn.elementAt(index) + " "
			+ returnProb[0] + " " +returnProb[1]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
}
