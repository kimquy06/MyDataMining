package classifier.fpt.gender;

 import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import weka.core.*;
import weka.classifiers.*;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.*;
import weka.classifiers.trees.RandomForest;
import weka.gui.visualize.*;
 
 /**
   * Generates and displays a ROC curve from a dataset. Uses a default 
   * NaiveBayes to generate the ROC data.
   *
   * @author FracPete
   */
 public class GenerateROC {
 
   /**
    * takes one argument: dataset in ARFF format (expects class to 
    * be last attribute)
    */
   public static void main(String[] args) throws Exception {
	 String trainFile = "data\\gender\\train\\CV\\5k_survey_7site_082013_cut15-300.arff".replace("\\", File.separator);
	 //String modelFile = "data\\gender\\gender_rf_10tree.model".replace("\\", File.separator);
     // load data
     Instances data = new Instances(
                           new BufferedReader(
                             new FileReader(trainFile)));
     data.setClassIndex(data.numAttributes() - 1);
     data.deleteStringAttributes();
 
     // train classifier
     //Classifier classifier = new NaiveBayes();     
     RandomForest classifier = new RandomForest();
	 classifier.setNumTrees(1000);
	 classifier.setMaxDepth(0);
	 classifier.setNumFeatures(20);
	 classifier.setSeed(1);
	 classifier.setDebug(false);	
     Evaluation eval = new Evaluation(data);
     eval.crossValidateModel(classifier, data, 5, new Random(1));
     
 
     // generate curve
     ThresholdCurve tc = new ThresholdCurve();
     int classIndex = 1;
     Instances result = tc.getCurve(eval.predictions(), classIndex);
 
     // plot curve
     ThresholdVisualizePanel vmc = new ThresholdVisualizePanel();
     vmc.setROCString("(Area under ROC = " + 
         Utils.doubleToString(tc.getROCArea(result), 4) + ")");
     vmc.setName(result.relationName());
     PlotData2D tempd = new PlotData2D(result);
     tempd.setPlotName(result.relationName());
     tempd.addInstanceNumberAttribute();
     // specify which points are connected
     boolean[] cp = new boolean[result.numInstances()];
     for (int n = 1; n < cp.length; n++)
       cp[n] = true;
     tempd.setConnectPoints(cp);
     // add plot
     vmc.addPlot(tempd);
 
     // display curve
     String plotName = vmc.getName(); 
     final javax.swing.JFrame jf = 
       new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
     jf.setSize(500,400);
     jf.getContentPane().setLayout(new BorderLayout());
     jf.getContentPane().add(vmc, BorderLayout.CENTER);
     jf.addWindowListener(new java.awt.event.WindowAdapter() {
       public void windowClosing(java.awt.event.WindowEvent e) {
       jf.dispose();
       }
     });
     jf.setVisible(true);
   }
 }