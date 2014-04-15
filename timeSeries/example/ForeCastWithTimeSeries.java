package timeSeries.example;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.functions.GaussianProcesses;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.timeseries.AbstractForecaster;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker;
import weka.classifiers.timeseries.core.TSLagMaker.Periodicity;
import weka.classifiers.timeseries.eval.TSEvaluation;
import weka.classifiers.timeseries.eval.graph.GraphDriver;
import weka.core.Instances;

public class ForeCastWithTimeSeries {
	SimpleDateFormat dmyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat dmyhDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
	public void foreCastSale(String dataFile, int step) {
		try {
			Instances instance = new Instances(new BufferedReader(new FileReader(
					dataFile)));
			String relation = instance.relationName();
			System.out.println("Instance summary: " + instance.toSummaryString());			
			WekaForecaster forecaster = new WekaForecaster();
			forecaster.setFieldsToForecast("Weekly_Sales");
			forecaster.getTSLagMaker().setTimeStampField("Date");
			//		
			IBk ibk = new IBk();
			ibk.setKNN(1);
			ibk.setMeanSquared(true);
			ibk.setWindowSize(0);	
			ibk.setCrossValidate(true);
			forecaster.setBaseForecaster(ibk);//weka.classifiers.meta.AdditiveRegression -S 1.0 -I 10 -W weka.classifiers.trees.DecisionStump
			//
			forecaster.getTSLagMaker().setMinLag(1);
			forecaster.getTSLagMaker().setMaxLag(52);
			forecaster.getTSLagMaker().setAddDayOfMonth(true);
			forecaster.getTSLagMaker().setAddMonthOfYear(true);
			forecaster.getTSLagMaker().setAddQuarterOfYear(true);
			forecaster.getTSLagMaker().setPeriodicity(Periodicity.WEEKLY);	
			//forecaster.getTSLagMaker().setPeriodicity(Periodicity.UNKNOWN);	
			
			//
			
			/*TSEvaluation eval = new TSEvaluation(instance,step);
		    System.out.println("Train summary: " + eval.getTrainingData().toSummaryString());	
		    System.out.println("Test summary: " + eval.getTestData().toSummaryString());	
			eval.setHorizon(step);
			//eval.setEvaluateOnTestData(true);
			//eval.setEvaluateOnTrainingData(true);
	 		eval.evaluateForecaster(forecaster);			
	 		JPanel result = eval.graphFutureForecastOnTesting(
	 	            GraphDriver.getDefaultDriver(), forecaster,
	 	            AbstractForecaster.stringToList(forecaster.getFieldsToForecast()));
	 	
	 		 JFrame frame = new JFrame();
	         frame.setBackground( Color.white );
	         frame.add( result );
	       //  frame.add( new JLabel("North"), BorderLayout.NORTH);
	       //  frame.add( new JLabel("South"), BorderLayout.SOUTH);

	         frame.pack();
	         frame.setVisible( true );
	        System.out.println(eval.toSummaryString());  
	   */
	        //
	        
			forecaster.buildForecaster(instance);
			forecaster.primeForecaster(instance);
			Date currentDt = getCurrentDateTime(forecaster.getTSLagMaker());
			currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			List<List<NumericPrediction>> forecast = forecaster.forecast(step,System.out);	
			
			for (int i = 1; i <= step; i++) {						
				List<NumericPrediction> predsAtStep = forecast.get(i-1);				
				NumericPrediction predForTarget = predsAtStep.get(0);	
				System.out.print(dmyDateFormat.format(currentDt) + ",");
			    System.out.print(predForTarget.predicted() + " ");	
			    System.out.println();
			    currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void foreCastDaily(String dataFile, int step) {
		try {
			Instances instance = new Instances(new BufferedReader(new FileReader(
					dataFile)));
			System.out.println("Instance summary: " + instance.toSummaryString());			
			WekaForecaster forecaster = new WekaForecaster();
			forecaster.setFieldsToForecast("weight");
			forecaster.getTSLagMaker().setTimeStampField("Date");
			//		
			IBk ibk = new IBk();
			ibk.setKNN(7);
			ibk.setMeanSquared(true);
			ibk.setWindowSize(0);	
			ibk.setCrossValidate(true);
			//forecaster.setBaseForecaster(ibk);//weka.classifiers.meta.AdditiveRegression -S 1.0 -I 10 -W weka.classifiers.trees.DecisionStump
			forecaster.setBaseForecaster(new AdditiveRegression());
			//
			forecaster.getTSLagMaker().setMinLag(1);
			forecaster.getTSLagMaker().setMaxLag(7);
			forecaster.getTSLagMaker().setAddDayOfMonth(true);
			forecaster.getTSLagMaker().setAddMonthOfYear(true);
			forecaster.getTSLagMaker().setAddQuarterOfYear(true);
			//forecaster.getTSLagMaker().setPeriodicity(Periodicity.DAILY);	
			//forecaster.getTSLagMaker().setPeriodicity(Periodicity.UNKNOWN);	
			
			//
			
			/*TSEvaluation eval = new TSEvaluation(instance,step);
		    System.out.println("Train summary: " + eval.getTrainingData().toSummaryString());	
		    System.out.println("Test summary: " + eval.getTestData().toSummaryString());	
			eval.setHorizon(step);
			//eval.setEvaluateOnTestData(true);
			//eval.setEvaluateOnTrainingData(true);
	 		eval.evaluateForecaster(forecaster);			
	 		JPanel result = eval.graphFutureForecastOnTesting(
	 	            GraphDriver.getDefaultDriver(), forecaster,
	 	            AbstractForecaster.stringToList(forecaster.getFieldsToForecast()));
	 	
	 		 JFrame frame = new JFrame();
	         frame.setBackground( Color.white );
	         frame.add( result );
	       //  frame.add( new JLabel("North"), BorderLayout.NORTH);
	       //  frame.add( new JLabel("South"), BorderLayout.SOUTH);

	         frame.pack();
	         frame.setVisible( true );
	        System.out.println(eval.toSummaryString());  
	   */
	        //
	        
			forecaster.buildForecaster(instance);
			forecaster.primeForecaster(instance);
			Date currentDt = getCurrentDateTime(forecaster.getTSLagMaker());
			currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			List<List<NumericPrediction>> forecast = forecaster.forecast(step,System.out);	
			
			for (int i = 1; i <= step; i++) {						
				List<NumericPrediction> predsAtStep = forecast.get(i-1);				
				NumericPrediction predForTarget = predsAtStep.get(0);	
				System.out.print(dmyDateFormat.format(currentDt) + ",");
			    System.out.print(predForTarget.predicted() + " ");	
			    System.out.println();
			    currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void foreCastHourly(String dataFile, int step) {
		try {
			Instances instance = new Instances(new BufferedReader(new FileReader(
					dataFile)));
			System.out.println("Instance summary: " + instance.toSummaryString());			
			WekaForecaster forecaster = new WekaForecaster();
			forecaster.setFieldsToForecast("weight");
			forecaster.getTSLagMaker().setTimeStampField("Date");
			//		
			IBk ibk = new IBk();
			ibk.setKNN(7);
			ibk.setMeanSquared(true);
			ibk.setWindowSize(0);	
			ibk.setCrossValidate(true);
			//forecaster.setBaseForecaster(ibk);//weka.classifiers.meta.AdditiveRegression -S 1.0 -I 10 -W weka.classifiers.trees.DecisionStump
			forecaster.setBaseForecaster(new AdditiveRegression());
			//
			forecaster.getTSLagMaker().setMinLag(1);
			forecaster.getTSLagMaker().setMaxLag(7);
			forecaster.getTSLagMaker().setAddDayOfMonth(true);
			forecaster.getTSLagMaker().setAddMonthOfYear(true);
			forecaster.getTSLagMaker().setAddQuarterOfYear(true);
			//forecaster.getTSLagMaker().setPeriodicity(Periodicity.HOURLY);	
			//forecaster.getTSLagMaker().setPeriodicity(Periodicity.UNKNOWN);	
			
			//
			
			/*TSEvaluation eval = new TSEvaluation(instance,step);
		    System.out.println("Train summary: " + eval.getTrainingData().toSummaryString());	
		    System.out.println("Test summary: " + eval.getTestData().toSummaryString());	
			eval.setHorizon(step);
			//eval.setEvaluateOnTestData(true);
			//eval.setEvaluateOnTrainingData(true);
	 		eval.evaluateForecaster(forecaster);			
	 		JPanel result = eval.graphFutureForecastOnTesting(
	 	            GraphDriver.getDefaultDriver(), forecaster,
	 	            AbstractForecaster.stringToList(forecaster.getFieldsToForecast()));
	 	
	 		 JFrame frame = new JFrame();
	         frame.setBackground( Color.white );
	         frame.add( result );
	       //  frame.add( new JLabel("North"), BorderLayout.NORTH);
	       //  frame.add( new JLabel("South"), BorderLayout.SOUTH);

	         frame.pack();
	         frame.setVisible( true );
	        System.out.println(eval.toSummaryString());  
	   */
	        //
	        
			forecaster.buildForecaster(instance);
			forecaster.primeForecaster(instance);
			Date currentDt = getCurrentDateTime(forecaster.getTSLagMaker());
			currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			List<List<NumericPrediction>> forecast = forecaster.forecast(step,System.out);	
			
			for (int i = 1; i <= step; i++) {						
				List<NumericPrediction> predsAtStep = forecast.get(i-1);				
				NumericPrediction predForTarget = predsAtStep.get(0);	
				System.out.print(dmyhDateFormat.format(currentDt) + ",");
			    System.out.print(predForTarget.predicted() + " ");	
			    System.out.println();
			    currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		ForeCastWithTimeSeries timeSeries = new ForeCastWithTimeSeries();
		//String dataFile = "data/Sale_Walmart/ByStore/Store1Merged.arff";
		String dataFile = "data/Sale_Walmart/ByStore/5_80.arff";
		timeSeries.foreCastSale(dataFile, 104);
		//String daily = "data/daily/5c6939431da90b6e.arff";
		//timeSeries.foreCastDaily(daily,7);
		//String hourly = "data/hourly/5c6939431da90b6e.arff";
		//timeSeries.foreCastHourly(hourly,12);
	}
	
	private Date getCurrentDateTime(TSLagMaker lm) throws Exception {
		    return new Date((long)lm.getCurrentTimeStampValue());
		  }

	private Date advanceTime(TSLagMaker lm, Date dt) {
		    return new Date((long)lm.advanceSuppliedTimeValue(dt.getTime()));
	}
}
