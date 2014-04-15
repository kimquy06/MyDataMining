package timeSeries.example;
import java.io.*;

import java.util.Date;
import java.util.List;
import weka.core.Instances;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.lazy.IBk;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker;
import weka.classifiers.timeseries.core.TSLagMaker.Periodicity;
import weka.classifiers.timeseries.eval.TSEvaluation;

/**
 * Example of using the time series forecasting API. To compile and
 * run the CLASSPATH will need to contain:
 *
 * weka.jar (from your weka distribution)
 * pdm-timeseriesforecasting-ce-TRUNK-SNAPSHOT.jar (from the time series package)
 * jcommon-1.0.14.jar (from the time series package lib directory)
 * jfreechart-1.0.13.jar (from the time series package lib directory)
 */
public class TimeSeriesExample {

  public static void main(String[] args) {
    try {
      // path to the Australian wine data included with the time series forecasting
      // package
      String pathToWineData = weka.core.WekaPackageManager.PACKAGES_DIR.toString()
        + File.separator + "timeseriesForecasting" + File.separator + "sample-data"
        + File.separator + "wine.arff";

      // load the wine data
      Instances wine = new Instances(new BufferedReader(new FileReader(pathToWineData)));

      // new forecaster
      WekaForecaster forecaster = new WekaForecaster();

      // set the targets we want to forecast. This method calls
      // setFieldsToLag() on the lag maker object for us
      forecaster.setFieldsToForecast("Fortified,Dry-white");

      // default underlying classifier is SMOreg (SVM) - we'll use
      // gaussian processes for regression instead
      forecaster.setBaseForecaster(new GaussianProcesses());

      forecaster.getTSLagMaker().setTimeStampField("Date"); // date time stamp
      forecaster.getTSLagMaker().setMinLag(1);
      //forecaster.getTSLagMaker().setMaxLag(12); // monthly data

      // add a month of the year indicator field
      //forecaster.getTSLagMaker().setAddMonthOfYear(true);

      // add a quarter of the year indicator field
      //forecaster.getTSLagMaker().setAddQuarterOfYear(true);

      // build the model
      forecaster.buildForecaster(wine, System.out);

      // prime the forecaster with enough recent historical data
      // to cover up to the maximum lag. In our case, we could just supply
      // the 12 most recent historical instances, as this covers our maximum
      // lag period
      forecaster.primeForecaster(wine);

      // forecast for 12 units (months) beyond the end of the
      // training data
      List<List<NumericPrediction>> forecast = forecaster.forecast(12, System.out);

      // output the predictions. Outer list is over the steps; inner list is over
      // the targets
      Date currentDt = getCurrentDateTime(forecaster.getTSLagMaker());    
      for (int i = 0; i < 12; i++) {
        List<NumericPrediction> predsAtStep = forecast.get(i);
        for (int j = 0; j < 2; j++) {
          NumericPrediction predForTarget = predsAtStep.get(j);
          System.out.print(currentDt + ": ");
          System.out.print("" + predForTarget.predicted() + " ");
        }
        System.out.println();
        currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
      }

      // we can continue to use the trained forecaster for further forecasting
      // by priming with the most recent historical data (as it becomes available).
      // At some stage it becomes prudent to re-build the model using current
      // historical data.

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

	private static Date getCurrentDateTime(TSLagMaker lm) throws Exception {		
		return new Date((long) lm.getCurrentTimeStampValue());
	}

	private static Date advanceTime(TSLagMaker lm, Date dt) {
		return new Date((long) lm.advanceSuppliedTimeValue(dt.getTime()));
	}
}
//unused
/*
//load the data
			Instances instance = new Instances(new BufferedReader(new FileReader(
					dataFile)));
			
			// new forecaster
			WekaForecaster forecaster = new WekaForecaster();

			// set the targets we want to forecast. This method calls
			// setFieldsToLag() on the lag maker object for us
			forecaster.setFieldsToForecast("weight");

			// default underlying classifier is SMOreg (SVM) - we'll use
			// gaussian processes for regression instead
			IBk ibk = new IBk();
			ibk.setKNN(1);
			ibk.setMeanSquared(true);
		//	ibk.setWindowSize(0);	
			//forecaster.setBaseForecaster(new GaussianProcesses());
			//forecaster.setBaseForecaster(ibk);
			//-K 1 -W 0 -A "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\""
			/*
			String opt = "-W -P 0 -M 5.0 -norm 1.0 -lnorm 2.0 -lowercase -stoplist -stopwords C:\\Users\\Fernando\\workspace\\GPCommentsAnalyzer\\pt-br_stopwords.dat -tokenizer \"weka.core.tokenizers.NGramTokenizer -delimiters ' \\r\\n\\t.,;:\\\'\\\"()?!\' -max 2 -min 1\" -stemmer weka.core.stemmers.NullStemmer";
	        a.setOptions(Utils.splitOptions(opt));                                            
	       
	        Evaluation eval = new Evaluation(train);                                           
	        eval.evaluateModel(nb, train);
	        System.out.println(eval.toSummaryString());                                        
	        System.out.println(eval.toClassDetailsString());                                   
	        System.out.println(eval.toMatrixString());    

			forecaster.getTSLagMaker().setTimeStampField("Date"); // date time
																	// stamp
			//forecaster.s			
			forecaster.getTSLagMaker().setMinLag(1);
			forecaster.getTSLagMaker().setMaxLag(365); // monthly data

			// add a month of the year indicator field
			forecaster.getTSLagMaker().setAddDayOfMonth(true);
			//forecaster.getTSLagMaker().setAddMonthOfYear(true);

			// add a quarter of the year indicator field
			//forecaster.getTSLagMaker().setAddQuarterOfYear(true);
			
			//forecaster.getTSLagMaker().setPrimaryPeriodicFieldName("Date");
		    forecaster.getTSLagMaker().setPeriodicity(Periodicity.DAILY);
		    //forecaster.getTSLagMaker().createTimeLagCrossProducts(instance);
		    forecaster.getTSLagMaker().setAdjustForTrends(true);
		    
			
			TSEvaluation eval = new TSEvaluation(instance, 12);
			eval.setHorizon(12);
			eval.setEvaluateOnTestData(true);
			eval.setEvaluateOnTrainingData(true);
	 		eval.evaluateForecaster(forecaster, System.out);		
	        System.out.println(eval.toSummaryString());                                        
	       // System.out.println(eval.);                                   
	       // System.out.println(eval.toMatrixString());  
			
			//forecaster.
			// build the model
			forecaster.buildForecaster(instance, System.out);

			// prime the forecaster with enough recent historical data
			// to cover up to the maximum lag. In our case, we could just supply
			// the 12 most recent historical instances, as this covers our
			// maximum
			// lag period
			//forecaster.primeForecaster(instance);

			// forecast for 12 units (months) beyond the end of the
			// training data
			//List<List<NumericPrediction>> forecast = forecaster.forecast(12,System.out);
			List<List<NumericPrediction>> forecast = forecaster.forecast(1,instance, System.out);
			//List<List<NumericPrediction>> forecast  = forecaster.forecast(12, instance, System.out);
			// output the predictions. Outer list is over the steps; inner list
			// is over
			// the targets
			Date currentDt = getCurrentDateTime(forecaster.getTSLagMaker());
			
			for (int i = 0; i < 1; i++) {
				List<NumericPrediction> predsAtStep = forecast.get(i);				
				NumericPrediction predForTarget = predsAtStep.get(0);				
				//System.out.print(predForTarget.predicted() + " ");		
				System.out.print(currentDt + ": ");
			    System.out.print(predForTarget.predicted() + " ");	
			    System.out.println();
			    // Advance the current date to the next prediction date
			    currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
				//System.out.println();
				
			}
*/