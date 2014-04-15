package classifier.kaggle.titanic;

import java.io.File;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;



public class PredictOneInstance {
	public static void main(String[] args){
		String modelFile = "data\\sample\\titanic.model".replace("\\", File.separator);
		FastVector      atts;
	     FastVector      attsRel;
	     FastVector      attValsSurvied;
	     FastVector      attValsGender;
	     FastVector      attValsEmbarked;
	     FastVector      attValsRel;
	     Instances       data;
	     Instances       dataRel;
	     double[]        vals;
	     double[]        valsRel;
	     int             i;
	     /*@relation test

	     1-@attribute survived {0,1}
	     2-@attribute pclass numeric
	     3-@attribute name string
	     4-@attribute sex {male,female}
	     5-@attribute age numeric
	     6-@attribute sibsp numeric
	     7-@attribute parch numeric
	     8-@attribute ticket string
	     9-@attribute fare numeric
	     10-@attribute cabin string
	     11-@attribute embarked {Q,S,C}*/
	     // 1. set up attributes
	     atts = new FastVector();
	     // - nominal: 1
	     attValsSurvied = new FastVector();
	     attValsSurvied.addElement("0");
	     attValsSurvied.addElement("1");
	     atts.addElement(new Attribute("att1", attValsSurvied));
	     // - numeric: 2,5,6,7,9
	     atts.addElement(new Attribute("att2"));
	     // - string 3
	     atts.addElement(new Attribute("att3", (FastVector) null));
	     // - nominal: 4
	     attValsGender = new FastVector();
	     attValsGender.addElement("male");
	     attValsGender.addElement("female");
	     atts.addElement(new Attribute("att4", attValsGender));
	     // - numeric: 5,6,7
	     atts.addElement(new Attribute("att5"));
	     atts.addElement(new Attribute("att6"));
	     atts.addElement(new Attribute("att7"));
	     // - string 8
	     atts.addElement(new Attribute("att8", (FastVector) null));
	     // - numeric: 9
	     atts.addElement(new Attribute("att9"));
	     // - string 10
	     atts.addElement(new Attribute("att10", (FastVector) null));
	     // - nominal: 11
	     attValsEmbarked = new FastVector();
	     attValsEmbarked.addElement("Q");
	     attValsEmbarked.addElement("S");
	     attValsEmbarked.addElement("C");
	     atts.addElement(new Attribute("att11", attValsEmbarked));	    
	     // 2. create Instances object
	     data = new Instances("MyRelation", atts, 0);
	     // 3. fill with data
		 String temp ="1,2,'Myles Mr. Thomas Francis',male,62,0,0,240276.0,9.6875,?,Q";		 		
		 String[] s_parts = temp.split(",");	
	     // first instance
	     vals = new double[data.numAttributes()];
	     
	     for(int j=0;j<s_parts.length;j++){
	    	 String text = s_parts[j];
	    	 System.out.println(j + " " + text);
	    	 if(j==0){
	    		 vals[0] = attValsSurvied.indexOf(text);
	    	 }else{
	    		 if(j==3){
		    		 vals[3] = attValsGender.indexOf(text);
		    	 }else{
		    		 if(j==10){
			    		 vals[10] = attValsEmbarked.indexOf(text);
			    	 }else{
			    		 if(j==1|| j==4|j==5||j==6||j==8){
				    		 vals[j] = Double.valueOf(text);
				    	 }else{
				    		 vals[j] = data.attribute(j).addStringValue(text);
				    	 }				    	
			    	 }
			    	 
		    	 }
		    	 
	    	 }	    	 
	     }
	     
	     // add
	     data.add(new DenseInstance(1.0, vals));
	     data.setClass(data.attribute(0));
	     // 4. output data
	     System.out.println(data);
	     
		/*
		 * Now we read in the serialized model from disk
		 */
		try {
			Classifier classifier = (Classifier) SerializationHelper.read(modelFile);
			Instance instance = data.instance(0);
			double classification = classifier.classifyInstance(instance);
			System.out.println(classification);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}
}
