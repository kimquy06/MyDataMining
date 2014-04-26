package classifier.kaggle.multi;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.pmml.consumer.NeuralNetwork;
import weka.classifiers.pmml.consumer.PMMLClassifier;
import weka.classifiers.pmml.consumer.SupportVectorMachineModel;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.LMT;
import weka.classifiers.trees.M5P;
import weka.classifiers.trees.REPTree;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

/**
 * The Train class is responsible for loading the training data, instantiating a
 * Classifier, then building the classifier instance with the training data. It
 * then serializes the Classifier to disk for other operations to use.
 * 
 * As seen in the README.md file, we have converted the given CSV formatted
 * traiing and teat data into ARFF formatted files. This allows us to specify
 * the types of each column (nominal, numeric, string).
 * 
 * @author jbirchfield
 * 
 */
public class Train {

	public static void main(String[] args) throws Exception {
		/*
		 * First we load the training data from our ARFF file
		 */
	/*	String trainFile;
		trainFile = "data\\gender_new\\train\\5k_7site_082013_cut15-300_id_1_normalized_select_attribute_cost_sensitive.arff"
				.replace("\\", File.separator);
		String evaluationFile = "data\\gender_new\\train\\eval_multi_rf_ibk_all_select_attribute_cost_sensitive_rf_8tree_knn4.csv".replace(
				"\\", File.separator);
		String modelFile = "data\\gender_new\\gender_multi_rf_ibk__all_select_attribute_rf_cost_sensitive_8tree_knn4.model".replace("\\",
				File.separator);*/
		//String trainFile = "data\\AcquireValueShopper\\train_new_remove_userid.arff".replace("\\", File.separator);
		//train_brand_company_cate.arff
		String trainFile = "data\\AcquireValueShopper\\train_brand_company_cate.arff".replace("\\", File.separator);				
		String modelFile = "data\\AcquireValueShopper\\decisionTable_bayes_trees_4feature.model".replace("\\", File.separator);
		
		
		ArffLoader trainLoader = new ArffLoader();
		trainLoader.setSource(new File(trainFile));
		trainLoader.setRetrieval(Loader.BATCH);
		Instances trainDataSet = trainLoader.getDataSet();
		/*
		 * Now we tell the data set which attribute we want to classify, in our
		 * case, we want to classify the first column: survived
		 */
		Attribute trainAttribute = trainDataSet.attribute(trainDataSet
				.numAttributes() - 1);
		trainDataSet.setClass(trainAttribute);

		/*
		 * The RandomForest implementation cannot handle columns of type string,
		 * so we remove them for now.
		 */
		trainDataSet.deleteStringAttributes();

		/*
		 * The RandomForest implementation cannot handle columns of type string,
		 * so we remove them for now.
		 */
		trainDataSet.deleteStringAttributes();

		/*
		 * Create a new Classifier of type RandomForest and configure it.
		 */
		MultiClassifier multiClassifier = new MultiClassifier();

		RandomForest forest = new RandomForest();
		forest.setNumTrees(1000);
		forest.setMaxDepth(0);
		forest.setNumFeatures(3);
		forest.setSeed(1);
		forest.setDebug(false);

		IBk ibk = new IBk();
		ibk.setKNN(4);
		ibk.setDebug(true);
		
		NaiveBayes nb = new NaiveBayes();
		nb.setDebug(true);
		
		
		
		/*BFTree bfTree = new BFTree();
		bfTree.setNumFoldsPruning(10);
		bfTree.setHeuristic(true);
		bfTree.setUseGini(true);
		bfTree.setDebug(true);

		NBTree nbTree = new NBTree();
		nbTree.setDebug(true);*/
		
		

		J48 j48 = new J48();
		j48.setDebug(true);
		j48.setNumFolds(10);

		LMT lmt = new LMT();
		lmt.setDebug(true);

		/*FT ft = new FT();
		ft.setDebug(true);*/

		REPTree repTree = new REPTree();
		repTree.setDebug(true);
		repTree.setNumFolds(10);
		
		RandomTree randomTree = new RandomTree();
		randomTree.setDebug(true);
		randomTree.setNumFolds(10);

		//rule
		DecisionTable decisionTable = new DecisionTable();
		PART part = new PART();

		//rule
		multiClassifier.addClassifier(decisionTable);
		//multiClassifier.addClassifier(part);
		//bayes
		multiClassifier.addClassifier(nb);
		//tree
		multiClassifier.addClassifier(repTree);
		multiClassifier.addClassifier(randomTree);
		multiClassifier.addClassifier(j48);
		multiClassifier.addClassifier(forest);
		//Neural
		//NeuralNetwork neural = new NeuralNetwork(null, trainDataSet, null);
		//SVM
		//SupportVectorMachineModel svm = new SupportVectorMachineModel(null, trainDataSet, null);
		
		//multiClassifier.addClassifier(bfTree);
		//multiClassifier.addClassifier(nbTree);		
//		multiClassifier.addClassifier(lmt);
//		multiClassifier.addClassifier(ft);
		
		
		//	multiClassifier.addClassifier(ibk);
		/*
		 * Now we train the classifier
		 */
		multiClassifier.buildClassifier(trainDataSet);

		/*
		 * We are done training the classifier, so now we serialize it to disk
		 */
		SerializationHelper.write(modelFile, multiClassifier);
		System.out.println("Saved trained model to customer-multi.model");
		
		/* Evaluation eval = new Evaluation(trainDataSet);
		 eval.crossValidateModel(multiClassifier, trainDataSet, 5, new Random(1), System.out);
	    // eval.crossValidateModel(multiClassifier, trainDataSet, 5, new Random(1));		
		 PrintWriter output = new PrintWriter(evaluationFile);
		 output.println(multiClassifier);
		 output.println(eval.toSummaryString());
		 output.println(eval.toMatrixString());//("\nResults\n======\n", false));
		 output.println(eval.toClassDetailsString());	
		 output.close();
		 System.out.println(multiClassifier);
		 System.out.println(eval.toSummaryString());
		 System.out.println(eval.toMatrixString());//("\nResults\n======\n", false));
		 System.out.println(eval.toClassDetailsString());	*/

	}
}
