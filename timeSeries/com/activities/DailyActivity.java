package timeSeries.com.activities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;



public class DailyActivity {
	public void getHourlyActivitiesForListUser(){
		Map<String, Map<String,Integer>> dailyActivities =  new HashMap<String, Map<String,Integer>>();
		String pathIn = "data/userid_history.txt";		
		PrintWriter output;
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		String userId = "";
		String date = "";	
		Map<String,Integer> activities = null;
		try {
			//
			scanner = new Scanner(new File(pathIn));
			//output = new PrintWriter(pathOut);
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split("\t");
				userId = partsOfLine[0];
				date = partsOfLine[1].split(" ")[0] + " " + partsOfLine[1].split(" ")[1].substring(0, 2);
				if(dailyActivities.containsKey(userId)){
					activities = dailyActivities.get(userId);
				}else{
					activities = new HashMap<String,Integer>();
				}
				if(activities.containsKey(date)){
					activities.put(date, 1 + activities.get(date));
				}else{
					activities.put(date, 1);
				}		
				dailyActivities.put(userId, activities);
			}
			scanner.close();
			//Map<String, String> treeMap = new TreeMap<String, String>(unsortMap);
			//System.out.println(userids.size());
					
			Iterator<String> userIterator = dailyActivities.keySet().iterator();
			String relation = "";
			String attributes = "@attribute weight numeric" + "\n" +
								"@attribute Date date 'dd/MM/yyyy HH'";
			/*@relation wine2

			@attribute weight numeric
			@attribute Date date 'dd/MM/yyyy'*/
			while (userIterator.hasNext()) {
					userId = userIterator.next();
					activities  = dailyActivities.get(userId);
				//	Map<String, Integer> sortMap = new TreeMap<String, Integer>(activities);
					Map<String, Integer> sortMap = sortHourByComparator(activities);
					output = new PrintWriter("data/hourly" + File.separator + userId + ".arff");		
					relation = "@relation " + userId;
					output.append(relation + "\n" + "\n");
					output.append(attributes + "\n" + "\n" + "@data" + "\n");
					Iterator<String> dates = sortMap.keySet().iterator();
					while(dates.hasNext()){
						date = dates.next();
						output.append(sortMap.get(date) + "," +"\"" +  date + "\"" + "\n");
					}					
					output.close();
				}			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	
	public void getDailyActivitiesForListUser(){
		Map<String, Map<String,Integer>> dailyActivities =  new HashMap<String, Map<String,Integer>>();
		String pathIn = "data/userid_history.txt";		
		PrintWriter output;
		Scanner scanner;
		String line = "";
		String[] partsOfLine= null;
		String userId = "";
		String date = "";	
		Map<String,Integer> activities = null;
		try {
			//
			scanner = new Scanner(new File(pathIn));
			//output = new PrintWriter(pathOut);
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				partsOfLine= line.split("\t");
				userId = partsOfLine[0];
				date = partsOfLine[1].split(" ")[0];
				if(dailyActivities.containsKey(userId)){
					activities = dailyActivities.get(userId);
				}else{
					activities = new HashMap<String,Integer>();
				}
				if(activities.containsKey(date)){
					activities.put(date, 1 + activities.get(date));
				}else{
					activities.put(date, 0);
				}		
				dailyActivities.put(userId, activities);
			}
			scanner.close();
			//Map<String, String> treeMap = new TreeMap<String, String>(unsortMap);
			//System.out.println(userids.size());
					
			Iterator<String> userIterator = dailyActivities.keySet().iterator();
			String relation = "";
			String attributes = "@attribute weight numeric" + "\n" +
								"@attribute Date date 'dd/MM/yyyy'";
			/*@relation wine2

			@attribute weight numeric
			@attribute Date date 'dd/MM/yyyy'*/
			while (userIterator.hasNext()) {
					userId = userIterator.next();
					activities  = dailyActivities.get(userId);
				//	Map<String, Integer> sortMap = new TreeMap<String, Integer>(activities);
					Map<String, Integer> sortMap = sortByComparator(activities);
					output = new PrintWriter("data/daily" + File.separator + userId + ".arff");		
					relation = "@relation " + userId;
					output.append(relation + "\n" + "\n");
					output.append(attributes + "\n" + "\n" + "@data" + "\n");
					Iterator<String> dates = sortMap.keySet().iterator();
					while(dates.hasNext()){
						date = dates.next();
						output.append(sortMap.get(date) + "," + date + "\n");
					}					
					output.close();
				}			
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
	}
	private Map sortByComparator(Map unsortMap) {			
		List list = new LinkedList(unsortMap.entrySet()); 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			DateFormat f = new SimpleDateFormat("dd/MM/yyyy");	
			public int compare(Object o1, Object o2) {
				try {
					return ((Comparable) f.parse(((Map.Entry) (o1)).getKey().toString()))
                            .compareTo(f.parse(((Map.Entry) (o2)).getKey().toString()));
	            } catch (ParseException e) {
	                throw new IllegalArgumentException(e);
	            }
			}
		});		
		// put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}		
		return sortedMap;
	}
	
	private Map sortHourByComparator(Map unsortMap) {			
		List list = new LinkedList(unsortMap.entrySet()); 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH");	
			public int compare(Object o1, Object o2) {
				try {
					return ((Comparable) f.parse(((Map.Entry) (o1)).getKey().toString()))
                            .compareTo(f.parse(((Map.Entry) (o2)).getKey().toString()));
	            } catch (ParseException e) {
	                throw new IllegalArgumentException(e);
	            }
			}
		});		
		// put sorted list into map again
        //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		/*for(int i=list.size()-1;i>=0;i--){
			Map.Entry entry = (Map.Entry)list.get(i);
			sortedMap.put(entry.getKey(), entry.getValue());
		}*/
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}	
		return sortedMap;
	}
	
	public static void main (String[] args){
		DailyActivity daily = new DailyActivity();
		daily.getDailyActivitiesForListUser();
	}
}
