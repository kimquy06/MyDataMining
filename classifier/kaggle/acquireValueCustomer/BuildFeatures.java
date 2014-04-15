package classifier.kaggle.acquireValueCustomer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import weka.core.Instances;

public class BuildFeatures {
	public void loadTransaction(){
		
	}	
	private void buildFeatures(){
		String pathTransaction = "data/AcquireValueShopper/transactionReduce.csv";	
		String pathTrainIn = "data/AcquireValueShopper/Merge_of_trainHistory_and_offers.csv";
		String pathTestIn = "data/AcquireValueShopper/Merge_of_testHistory_and_offers.csv";
			
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		String userId = "";			
		String category = "";
		String company = "";
		String brand = "";
		String month = "";
		
		
		
		Map<String,List<String>> features = null;
		Map<String,Map<String,List<String>>> userHistory = new HashMap<String,Map<String,List<String>>>();
		List<String> feature = null;
		
		try {			
			scanner = new Scanner(new File(pathTestIn));
			//line schema: id,chain,dept,category,company,brand,date,productsize,productmeasure,purchasequantity,purchaseamount
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				userId = partsOfLine[0];			
				category = partsOfLine[3];
				company = partsOfLine[4];
				brand = partsOfLine[5];
				month = partsOfLine[6].split("-")[1];
				
				if(userHistory.containsKey(userId)){
					features = userHistory.get(userId);
				}else{
					features = new HashMap<String,List<String>>();
				}
				
				//category
				if(features.containsKey("category")){
					feature = features.get("category");
				}else{
					feature = new ArrayList<String>();					
				}
				feature.add(category);
				features.put("category", feature);
				
				//company
				if(features.containsKey("company")){
					feature = features.get("company");
				}else{
					feature = new ArrayList<String>();					
				}
				feature.add(company);
				features.put("company", feature);
				
				//brand
				if(features.containsKey("brand")){
					feature = features.get("brand");
				}else{
					feature = new ArrayList<String>();					
				}
				feature.add(brand);
				features.put("brand", feature);
				
				//month
				if(features.containsKey("month")){
					feature = features.get("month");
				}else{
					feature = new ArrayList<String>();					
				}
				feature.add(month);
				features.put("month", feature);
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
			scanner = new Scanner(new File(pathTrainIn));
			//line schema: offer,id,chain,market,repeattrips,repeater,offerdate,category,quantity,company,offervalue,brand
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				userId = partsOfLine[1];					
				if(dataSales.containsKey(userId)){
					dataSale = dataSales.get(userId);
				}else{
					dataSale = new ArrayList<String>();
				}
				dataSale.add(line);		
				dataSales.put(userId, dataSale);
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
			Iterator<String> allPredictionIterator = features.keySet().iterator();
	
			while(allPredictionIterator.hasNext()){				
				userId = allPredictionIterator.next();
				//System.out.println("Predicting for: " + store_dept);
				feature = features.get(userId);
				//step = predictionForStore.size();				
				//
									
			}			
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
