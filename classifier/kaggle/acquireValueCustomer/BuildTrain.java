package classifier.kaggle.acquireValueCustomer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class BuildTrain {
	
	public void buildTrain(){
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		String userId = "";	
		
		//scan feature file
		List<String> features = new ArrayList<String>();
		
		String pathFeature = "";
		String pathTrain = "";
		int count =0;
		try {			
			scanner = new Scanner(new File(pathFeature));			
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				features.add(line);	
			}
			scanner.close();				
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//scan train file
		String part1 = "";
		String part2 = "";
		String classname = "";
		String featureAndValue ="";
		String[] partOfFeature;
		String feature ="";
		String value = "";	
		try {			
			scanner = new Scanner(new File(pathTrain));			
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();		
				partsOfLine = line.split("|");
				part1=partsOfLine[0];
				part2=partsOfLine[1];
				//process part 1, pattern: 0 '4642479230
				partsOfLine = part1.split(" '");
				classname = partsOfLine[0];
				//process part 2, pattern: f offer_quantity:1 has_bought_category_a_180:24.13 ... 
				partsOfLine = part2.split(" ");
				for(int i=0; i< partsOfLine.length;i++){
					featureAndValue = partsOfLine[i];
					if(featureAndValue.contains(":")){
						continue;
					}
					partOfFeature = featureAndValue.split(":");
					feature = partOfFeature[0];
					value = partOfFeature[1];
				}
			}
			scanner.close();				
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
