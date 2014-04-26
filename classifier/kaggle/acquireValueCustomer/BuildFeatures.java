package classifier.kaggle.acquireValueCustomer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BuildFeatures {
	public static void main(String[] args){
		BuildFeatures buildFeature = new BuildFeatures();
		buildFeature.buildFeatures();
	}	
	private void buildFeatures(){
		String pathTransaction = "data/AcquireValueShopper/transactionReduce.csv";	
		String pathTrainIn = "data/AcquireValueShopper/Merge_of_trainHistory_and_offers.csv";
		String pathTrainOut = "data/AcquireValueShopper/train_new.csv";
		String pathTestIn = "data/AcquireValueShopper/Merge_of_testHistory_and_offers.csv";
		String pathTestOut = "data/AcquireValueShopper/test_new.csv";
			
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		String userId = "";			
		String category = "";
		String company = "";
		String brand = "";
		String month = "";
		
		String repeater = "f";
		String market ="";
		String offerValue ="";
		String purchaseamount = "";
		
		Map<String,List<String>> features = null;
		Map<String,Map<String,List<String>>> userHistory = new HashMap<String,Map<String,List<String>>>();
		List<String> feature = null;
		System.out.println("Loading transaction...");
		try {			
			scanner = new Scanner(new File(pathTransaction));
			//line schema: id,chain,dept,category,company,brand,date,productsize,productmeasure,purchasequantity,purchaseamount
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				userId = partsOfLine[0];			
				category = partsOfLine[3];
				company = partsOfLine[4];
				brand = partsOfLine[5];
				month = partsOfLine[6].split("-")[1];
				purchaseamount = partsOfLine[11];
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
				//purchaseamount
				if(features.containsKey("purchaseamount")){
					feature = features.get("purchaseamount");
				}else{
					feature = new ArrayList<String>();					
				}
				feature.add(purchaseamount);
				features.put("purchaseamount", feature);
				userHistory.put(userId, features);
			}
			scanner.close();					
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		//read train file	
		PrintWriter output;			
		List<String> dataTrain = new ArrayList<String>();	
		String data = "";
		System.out.println("Processing train file...");
		int count = 0;
		try {			
			scanner = new Scanner(new File(pathTrainIn));
			//line schema: offer,id,chain,market,repeattrips,repeater,offerdate,category,quantity,company,offervalue,brand
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				if(partsOfLine.length!=12){
					System.out.println("Number of line: " + count);
					continue;
				}				
				userId = partsOfLine[1];	
				market = partsOfLine[3];
				repeater = partsOfLine[5];
				//System.out.println(partsOfLine[6]);
				month = partsOfLine[6].split("-")[1];
				category = partsOfLine[7];
				company = partsOfLine[9];
				offerValue = partsOfLine[10];
				brand = partsOfLine[11];
				
				if(userHistory.containsKey(userId)){
					features = userHistory.get(userId);
					//category
					if(features.containsKey("category")){
						feature = features.get("category");
						if(feature.contains(category)){
							category = "1";
						}else{
							category = "0";
						}
						
					}					
					//company
					if(features.containsKey("company")){
						feature = features.get("company");
						if(feature.contains(company)){
							company = "1";
						}else{
							company = "0";
						}
					}					
					//brand
					if(features.containsKey("brand")){
						feature = features.get("brand");
						if(feature.contains(brand)){
							brand = "1";
						}else{
							brand = "0";
						}
					}
					//month
					if(features.containsKey("month")){
						feature = features.get("month");
						if(feature.contains(month)){
							month = "1";
						}else{
							month = "0";
						}
					}
					//purchaseamount
					if(features.containsKey("purchaseamount")){
						feature = features.get("purchaseamount");						
						double value = 0.0;
						for(int i =0; i<feature.size();i++){
							value += Double.valueOf(feature.get(i));
						}	
						purchaseamount = String.valueOf(value/feature.size());
					}	
				}else{
					category = "0";
					company = "0";
					brand = "0";
					month = "0";
					purchaseamount = "0";
				}
				//feature:IsMarket,IsMonth,IsCategory,IsCompany,IsBrand,OfferValue,Return
				data = userId + "," + market + "," + month + "," + category 
						+ "," + company + "," + brand + "," + offerValue 
						+ "," + purchaseamount
						+ "," + repeater;
				dataTrain.add(data);
				count++;
				//System.out.println("Number of line: " + count);
			}
			scanner.close();							
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		//write out data train		
		System.out.println("Writing out new train file...");
		try {
			output = new PrintWriter(pathTrainOut);
			output.append("Id,IsMarket,IsMonth,IsCategory,IsCompany,IsBrand,OfferValue,Return" + "\n");			
			for(int i=0;i<dataTrain.size();i++){
				output.append(dataTrain.get(i) + "\n");
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataTrain = new ArrayList<String>();
		
		//read test data
		System.out.println("Processing test file...");
		count =0;		
		try {			
			scanner = new Scanner(new File(pathTestIn));
			//line schema: offer,id,chain,market,offerdate,category,quantity,company,offervalue,brand
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split(",");
				if(partsOfLine.length!=10){
					System.out.println("Number of line: " + count);
					continue;
				}
				userId = partsOfLine[1];	
				market = partsOfLine[3];
				//repeater = partsOfLine[5];
				month = partsOfLine[4].split("-")[1];
				category = partsOfLine[5];
				company = partsOfLine[7];
				offerValue = partsOfLine[8];
				brand = partsOfLine[9];
				
				if(userHistory.containsKey(userId)){
					features = userHistory.get(userId);
					//category
					if(features.containsKey("category")){
						feature = features.get("category");
						if(feature.contains(category)){
							category = "1";
						}else{
							category = "0";
						}
					}					
					//company
					if(features.containsKey("company")){
						feature = features.get("company");
						if(feature.contains(company)){
							company = "1";
						}else{
							company = "0";
						}
					}					
					//brand
					if(features.containsKey("brand")){
						feature = features.get("brand");
						if(feature.contains(brand)){
							brand = "1";
						}else{
							brand = "0";
						}
					}
					//month
					if(features.containsKey("month")){
						feature = features.get("month");
						if(feature.contains(month)){
							month = "1";
						}else{
							month = "0";
						}
					}
				}else{
					category = "0";
					company = "0";
					brand = "0";
					month = "0";
				}
				//feature:IsMarket,IsMonth,IsCategory,IsCompany,IsBrand,OfferValue,Return
				data = userId + "," + market + "," + month + "," + category 
						+ "," + company + "," + brand + "," + offerValue + "," + repeater;
				dataTrain.add(data);
				count++;
			}
			scanner.close();							
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		//write out data test		
		System.out.println("Writing out test file...");
		try {
			output = new PrintWriter(pathTestOut);
			output.append("Id,IsMarket,IsMonth,IsCategory,IsCompany,IsBrand,OfferValue,Return" + "\n");			
			for(int i=0;i<dataTrain.size();i++){
				output.append(dataTrain.get(i) + "\n");
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
