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

public class BuildNewDataset {
	public static void main(String args[]){
		BuildNewDataset build = new BuildNewDataset();
		build.buildNewDataset();
	}
	
	public void buildNewDataset(){
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		
		
		//scan feature file
		List<String> features = new ArrayList<String>();
		
		String pathFeature = "data/AcquireValueShopper/list_features.txt";
		String pathTrain = "data/AcquireValueShopper/train.vw";
		String pathTrainOut = "data/AcquireValueShopper/train_55features_temp.csv";
	
		String header = "userid,";
		try {			
			scanner = new Scanner(new File(pathFeature));			
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				features.add(line);	
				header += line + ","; 
			}
			scanner.close();				
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		header = header.substring(0, header.length()-1);
		//System.out.println(header);
		//scan train file
		String part1 = "";
		String part2 = "";
		String classname = "";
		String userId ="";
		String featureAndValue ="";
		String[] partOfFeature;
		String feature ="";
		String value = "";		
		List<String> trainData = new ArrayList<String>();
		Map<String,String> trainLine = null;
		System.out.println("Scanning train file...");
		try {			
			scanner = new Scanner(new File(pathTrain));			
			while (scanner.hasNext()) {	
				trainLine = new LinkedHashMap<String,String>();
				
				line = scanner.nextLine().trim();		
				//System.out.println(line);
				partsOfLine = line.split("f ");
				part1=partsOfLine[0];
				part2=partsOfLine[1];
				//System.out.println(part1);
				//System.out.println(part2);
				//process part 1, pattern: 0 '4642479230 (classname 'userid)
				partsOfLine = part1.split(" '");
				classname = partsOfLine[0];
				//System.out.println(classname);
				
				userId = partsOfLine[1].split(" ")[0];
				//System.out.println(userId);
				trainLine.put("userid", userId);
				//process part 2, pattern: offer_quantity:1 has_bought_category_a_180:24.13 ... 
				for(String s:features){
					trainLine.put(s, "0");
				}
				partsOfLine = part2.split(" ");
				for(int i=0; i< partsOfLine.length;i++){
					featureAndValue = partsOfLine[i].trim();
					//System.out.println(featureAndValue);
					if(!featureAndValue.contains(":") || i>2){
						continue;
					}
					partOfFeature = featureAndValue.split(":");
					feature = partOfFeature[0];
					value = partOfFeature[1];					
					trainLine.put(feature, value);
					//System.out.println(feature);
				}
				trainLine.put("classname", classname);
				trainData.add(getStringValueFromMap(trainLine));
			}
			scanner.close();				
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintWriter output;
		System.out.println("Writing out...");
		try {
			output = new PrintWriter(pathTrainOut);
			output.append(header + "\n");						
			for(int i=0;i<trainData.size();i++){				
				output.append(trainData.get(i) + "\n");
			}
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private String getStringValueFromMap(Map<String,String> map){
		String result = "";
		Iterator<String> keys = map.keySet().iterator();
		String key = "";
		String value = "";
		while(keys.hasNext()){
			key = keys.next();
			value = map.get(key);
			result +=value + ",";
		}
		result = result.substring(0, result.length()-1);
		return result;
	}
}
