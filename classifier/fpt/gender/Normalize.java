package classifier.fpt.gender;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class Normalize {
	private static Map<String, Map<Integer, Double>> m_mapUserTopic;
	private static Map<String, String> m_mapUserGender;
	int numberOfTopic = 91;
	public static void main(String[] args) {
		Normalize nom = new Normalize();
		String pathIn;
		pathIn = "data\\gender_37k\\train\\37k_9site_092013_cut15-300.csv"
				.replace("\\", File.separator);
		String pathOut;
		pathOut = "data\\gender_37k\\train\\37k_9site_092013_cut15-300.normalize.csv"
				.replace("\\", File.separator);
		
		nom.normalizeTopicVectorMatrix(pathIn, pathOut);
	}
	
	private void normalizeTopicVectorMatrix(String pathIn,
			String pathOut) {
		Scanner scanner;
		String line = "";
		String[] s = null;
		String username = "";	
		String gender ="";
		m_mapUserTopic = new HashMap<String, Map<Integer, Double>>();
		m_mapUserGender = new HashMap<String, String>();
		PrintWriter output;
		int count =0;
		try {
			scanner = new Scanner(new File(pathIn));
			while (scanner.hasNext()) {
				line = scanner.nextLine().trim();
				System.out.println("line number: " + count++);
				s = line.split(",");
				username = s[0];
				gender=s[s.length-1];
				Map<Integer, Double> originalVector = new HashMap<Integer, Double>();
				for(int i=1; i< s.length-1; i++){
					originalVector.put(i, Double.parseDouble(s[i]));
				}
				Map<Integer, Double> nomalizeVector = nomalizeVector(originalVector);	
				m_mapUserTopic.put(username, nomalizeVector);
				m_mapUserGender.put(username,gender);
			}
			scanner.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//normalize 0-1
		Iterator<String> userNames = m_mapUserTopic.keySet().iterator();				
		System.out.println("Writing out matrix with user size: " + m_mapUserTopic.size());
		try {
			output = new PrintWriter(pathOut);
			userNames = m_mapUserTopic.keySet().iterator();
			while (userNames.hasNext()) {
				username = userNames.next();
				Map<Integer, Double> topicVector = m_mapUserTopic.get(username);
				output.print(username);
				for (int i = 1; i <= numberOfTopic; i++) {
					if (topicVector.containsKey(i)) {
						output.print("," + topicVector.get(i));
					}else{
						output.print("," + 0.0);
					}
				}
				output.print("," + m_mapUserGender.get(username));
				output.println();
			}
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}	
	}
	

	private Map<Integer, Double> nomalizeVector(Map<Integer, Double> map){
		Map<Integer, Double> result = new HashMap<Integer,Double>();
		Iterator<Integer> keys = map.keySet().iterator();
		double sum = 0.0;
		while(keys.hasNext()){
			int key = keys.next();
			double value = map.get(key)	;		
			sum+=value;
		}
		
		keys = map.keySet().iterator();		
		while(keys.hasNext()){
			int key = keys.next();
			double value = map.get(key)	;		
			result.put(key, value/sum);
		}
		return result;
	}
}
