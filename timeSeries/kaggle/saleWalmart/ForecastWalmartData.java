package timeSeries.kaggle.saleWalmart;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdditiveRegression;
import weka.classifiers.timeseries.WekaForecaster;
import weka.classifiers.timeseries.core.TSLagMaker;
import weka.classifiers.timeseries.core.TSLagMaker.Periodicity;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instances;


public class ForecastWalmartData {
	SimpleDateFormat dmyDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private void devidedData(){
		Map<String, List<String>> dataSales =  new LinkedHashMap<String, List<String>>();
		String pathIn = "data/Sale_Walmart/train.csv";		
		PrintWriter output;
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		String store_dept = "";				
		List<String> dataSale = null;
		try {
			scanner = new Scanner(new File(pathIn));
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				store_dept = partsOfLine[0] + "_" + partsOfLine[1];						
				if(dataSales.containsKey(store_dept)){
					dataSale = dataSales.get(store_dept);
				}else{
					dataSale = new ArrayList<String>();
				}
				dataSale.add(line);		
				dataSales.put(store_dept, dataSale);
			}
			scanner.close();					
			Iterator<String> storeDeptIterator = dataSales.keySet().iterator();
			String relation = "";
			String attributes = 
					"@attribute Store numeric" + "\n" +
					"@attribute Dept numeric" + "\n" +
					"@attribute Date date 'yyyy-MM-dd'" + "\n" +
					"@attribute Weekly_Sales numeric" + "\n" +
					"@attribute IsHoliday {FALSE,TRUE}";				
			/*@relation wine2
				@attribute Store numeric
				@attribute Dept numeric
				@attribute Date date 'yyyy-MM-dd'
				@attribute Weekly_Sales numeric
				@attribute IsHoliday {FALSE,TRUE}
			*/
			while (storeDeptIterator.hasNext()) {
				store_dept = storeDeptIterator.next();
				output = new PrintWriter("data/Sale_Walmart/ByStore" + File.separator + store_dept + ".arff");	
				relation = "@relation " + store_dept;
				output.append(relation + "\n" + "\n");
				output.append(attributes + "\n" + "\n" + "@data" + "\n");
				dataSale =  dataSales.get(store_dept);
				for(String data : dataSale){
					output.append(data + "\n");
				}
				output.close();
			}			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}

	private void batchForecast(){
		String pathTrain = "data/Sale_Walmart/train.csv";		
		//String pathTrain = "data/Sale_Walmart/Demo/15_99.csv";
		String pathTest = "data/Sale_Walmart/test.csv";	
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		String store_dept = "store_dept";	
		//read test file to determine number of steps for each store
		//Map<String,Integer> steps = new LinkedHashMap<String,Integer>();
		Map<String,List<String>> allPrediction = new LinkedHashMap<String,List<String>>();
		List<String> predictionForStore = null;
		String date ="";
		try {			
			scanner = new Scanner(new File(pathTest));
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				store_dept = partsOfLine[0] + "_" + partsOfLine[1];			
				date = partsOfLine[2];
				/*if(steps.containsKey(store_dept)){
					steps.put(store_dept, steps.get(store_dept)+1);
				}else{
					steps.put(store_dept, 1);
				}*/	
				if(allPrediction.containsKey(store_dept)){
					predictionForStore = allPrediction.get(store_dept);
				}else{
					predictionForStore = new ArrayList<String>();
				}
				predictionForStore.add(date);
				allPrediction.put(store_dept, predictionForStore);
			}
			scanner.close();					
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		//read train file
		Map<String, List<String>> dataSales =  new LinkedHashMap<String, List<String>>();
		PrintWriter output;			
		List<String> dataSale = null;			
		try {			
			scanner = new Scanner(new File(pathTrain));
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				store_dept = partsOfLine[0] + "_" + partsOfLine[1];					
				if(dataSales.containsKey(store_dept)){
					dataSale = dataSales.get(store_dept);
				}else{
					dataSale = new ArrayList<String>();
				}
				dataSale.add(line);		
				dataSales.put(store_dept, dataSale);
			}
			scanner.close();							
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		//Build instance for forecasting
		try {
			output = new PrintWriter("data/Sale_Walmart/submission_SMO.csv");
			output.append("Id,Weekly_Sales" + "\n");			
			Iterator<String> allPredictionIterator = allPrediction.keySet().iterator();
			//int step =0;		
			double value = 0.0;
			int i =0;
			Instances instances = null;
			Map<String,Double> forecastValues = null;
			while(allPredictionIterator.hasNext()){				
				store_dept = allPredictionIterator.next();
				//System.out.println("Predicting for: " + store_dept);
				predictionForStore = allPrediction.get(store_dept);
				//step = predictionForStore.size();				
				//
				if(dataSales.containsKey(store_dept)){//have train data
					dataSale = dataSales.get(store_dept);
					instances = getInstanceFromList(dataSale);
					if(instances!=null){//error didn't happen					
						forecastValues = foreCastSale(instances,52);	
						Iterator<String> temp = forecastValues.keySet().iterator();
						while(temp.hasNext()){
							System.out.println(temp.next());
						}
						System.out.println("forecast");
						for(i=0;i<predictionForStore.size();i++){
							date = predictionForStore.get(i);								
							if(!forecastValues.containsKey(date)){
								value = 0.0;	
								System.out.println(date);
							}else{
								value = forecastValues.get(date);
								if(value<0){
									value = 0.0;
								}								
							}
							output.append( store_dept + "_" + date + "," + value + "\n");
						}						
					}else{
						System.out.println("Error in creating instance for: " + store_dept);
					}
				}else{
					//Don't have train data, so output is 0.0. 
					//Could not predict based on history 
					//Have to find another way
					for(i=0;i<predictionForStore.size();i++){
						date = predictionForStore.get(i);
						value = 0.0;
						output.append( store_dept + "_" + date + "," + value + "\n");
					}				
				}					
			}			
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private Instances getInstanceFromList(List<String> data){
		// setup
		FastVector attributes = new FastVector();
		Attribute weightAtt = new Attribute("Weekly_Sales");
		Attribute dateAtt = new Attribute("Date", "yyyy-MM-dd");		
		attributes.addElement(weightAtt);
		attributes.addElement(dateAtt);
		Instances instances = new Instances("timeseries", attributes, 0);
		double[] values;
		String[] partsOfLine = null;
		try {
			for(String line: data){
				partsOfLine = line.split(",");
				String date = partsOfLine[2];				
				double weight = Double.parseDouble(partsOfLine[3]);
				values = new double[instances.numAttributes()];
				values[0]= weight;
				values[1] = instances.attribute(1).parseDate(date);
				instances.add(new DenseInstance(1.0, values));
			}			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instances;
	}
	
	private Map<String,Double> foreCastSale(Instances instance, int step) {
		Map<String,Double> result = new LinkedHashMap<String,Double>();
		try {		
			WekaForecaster forecaster = new WekaForecaster();
			forecaster.setFieldsToForecast("Weekly_Sales");
			forecaster.getTSLagMaker().setTimeStampField("Date");
			//		
			IBk ibk = new IBk();
			ibk.setKNN(1);
			ibk.setMeanSquared(true);
			ibk.setWindowSize(0);	
			ibk.setCrossValidate(true);
			//forecaster.setBaseForecaster(ibk);
			//forecaster.setBaseForecaster(new AdditiveRegression());
			//forecaster.setBaseForecaster(new Hmm());
			//
			forecaster.getTSLagMaker().setMinLag(1);
			forecaster.getTSLagMaker().setMaxLag(52);
			forecaster.getTSLagMaker().setAddDayOfMonth(true);
			forecaster.getTSLagMaker().setAddMonthOfYear(true);
			forecaster.getTSLagMaker().setAddQuarterOfYear(true);	
			forecaster.getTSLagMaker().setPeriodicity(Periodicity.WEEKLY);	
			
			forecaster.buildForecaster(instance);
			forecaster.primeForecaster(instance);
			Date currentDt = getCurrentDateTime(forecaster.getTSLagMaker());
			currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			List<List<NumericPrediction>> forecast = forecaster.forecast(step,System.out);				
			for (int i = 1; i <= step; i++) {						
				List<NumericPrediction> predsAtStep = forecast.get(i-1);				
				NumericPrediction predForTarget = predsAtStep.get(0);	
				result.put(dmyDateFormat.format(currentDt), predForTarget.predicted());
				//System.out.print(dmyDateFormat.format(currentDt) + ",");
			    //System.out.print(predForTarget.predicted() + " ");	
			    //System.out.println();
			    currentDt = advanceTime(forecaster.getTSLagMaker(), currentDt);
			}			
		} catch (Exception ex) {			
			ex.printStackTrace();			
		}
		return result;
	}
	
	private Date getCurrentDateTime(TSLagMaker lm) throws Exception {
	    return new Date((long)lm.getCurrentTimeStampValue());
	}

	private Date advanceTime(TSLagMaker lm, Date dt) {
		    return new Date((long)lm.advanceSuppliedTimeValue(dt.getTime()));
	}
	
	public static void main (String[] args){
		ForecastWalmartData devideData = new ForecastWalmartData();
		String folderName = "data/Sale_Walmart/ByStore/";
		devideData.batchForecast();
	}
}
