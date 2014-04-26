package classifier.kaggle.multi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;

public class MultiClassifier implements Classifier, Serializable {

	/**
	 * 
	 */
	//private static final long serialVersionUID = -8996052328536347834L;

	List<Classifier> classifiers = new ArrayList<Classifier>();

	@Override
	public void buildClassifier(Instances data) throws Exception {
		for (Classifier classifier : classifiers) {
			classifier.buildClassifier(data);
		}
	}

	@Override
	public double classifyInstance(Instance instance) throws Exception {
		double sum = 0.0;
		for (Classifier classifier : classifiers) {
			double classification = classifier.classifyInstance(instance);
			sum += classification;
		}
		if(sum >= classifiers.size()/2) {
			return 1.0;
		}
		return 0.0;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void addClassifier(Classifier classifier) {
		classifiers.add(classifier);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MultiClassifier\n");
		buffer.append("-----------------\n");
		for (Classifier classifier : classifiers) {
			buffer.append(classifier.toString());
			buffer.append("\n-----------------\n");
		}

		return buffer.toString();
	}

	@Override
	public double[] distributionForInstance(Instance instance) throws Exception {
		// TODO Auto-generated method stub
		double[] prob = new double[2];
		double[] sum = new double[2];
		for (Classifier classifier : classifiers) {
			prob = classifier.distributionForInstance(instance);
			sum[0] += prob[0];
			sum[1] += prob[1];
		}
		sum[0] = sum[0]/classifiers.size();
		sum[1] = sum[1]/classifiers.size();

		return sum;
	}

	@Override
	public Capabilities getCapabilities() {
		// TODO Auto-generated method stub
		return null;
	}


}
