package Matrix_AI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Matrix {
	
	
	public static String genGrid() {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		
		int m = rand.nextInt(11) + 5;
		int n = rand.nextInt(11) + 5;
		
		sb = sb.append(m).append(",").append(n).append(";");
		
		HashMap<Integer, String> locations = new HashMap<>();
		
		int key = 0;
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				String location = i + "," + j;
				locations.put(key++, location);
			}
		}
		
		ArrayList<Integer> locationsList = new ArrayList(locations.keySet());
		
		String neo = locations.get(locationsList.get(rand.nextInt(locationsList.size())));
		
		
		return "";
	}
	
	
	public static void main(String[] args) {
//		StringBuilder sb = new StringBuilder();
//		
//		int m = 5;
//		int n = 10;
//		
//		sb = sb.append(m).append(n);
//		
//		System.out.println(sb);
		
		HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
		hm.put(1, 1);hm.put(2, 2);
		List <Integer> l=new ArrayList<>(hm.keySet());
		System.out.println(l);
		
	}
}
