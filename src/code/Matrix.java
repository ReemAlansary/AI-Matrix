package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Matrix extends SearchProblem {
	static String matrix[][];
	static int C;
	// static genGrid
	// static solve
	// probably there will be a queuing function for each strategy
	// ...
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

		int neoLocationKey = locationsList.get(rand.nextInt(locationsList.size()));
		String neo = locations.get(neoLocationKey);
		locations.remove(neoLocationKey);
		int carry = rand.nextInt(5);
		sb.append(carry + ";");
		sb.append(neo + ";");
		locationsList = new ArrayList(locations.keySet());
		int TBkey = locationsList.get(rand.nextInt(locationsList.size()));
		String TBlocation = locations.get(TBkey);
		locationsList.remove(TBkey);
		sb.append(TBlocation + ";" );
		int hostages = rand.nextInt(8) + 3;
		int pills = rand.nextInt(hostages + 1);
		int remaining = m * n - pills - hostages;
		int pads = rand.nextInt(remaining - 1) + 2;
		if (pads % 2 == 1) {
			pads -= 1;
		}
		remaining -= pads;
		int agents = rand.nextInt(remaining + 1);
		//System.out.println(hostages + " " + pills + " " + pads + " " + agents);
		for (int i = 1; i <= agents; i++) {
			locationsList = new ArrayList(locations.keySet());
			int agentLocationKey = locationsList.get(rand.nextInt(locationsList.size()));
			String agentLocation = locations.get(agentLocationKey);
			if(i<agents)
			sb.append(agentLocation + ",");
			locations.remove(agentLocationKey);
		}
		sb.append(";");
		for (int i = 1; i <= pills; i++) {
			locationsList = new ArrayList<Integer>(locations.keySet());
			int pillLocationKey = locationsList.get(rand.nextInt(locationsList.size()));
			String pillLocation = locations.get(pillLocationKey);
			if(i<pills)
			sb.append(pillLocation + ",");
			locations.remove(pillLocationKey);
		}
		sb.append(";");
		for (int i = 1; i <= pads / 2; i++) {
			locationsList = new ArrayList<Integer>(locations.keySet());
			int padLocationKey1 = locationsList.get(rand.nextInt(locationsList.size()));
			String padLocation1 = locations.get(padLocationKey1);
			int padLocationKey2 = locationsList.get(rand.nextInt(locationsList.size()));
			String padLocation2 = locations.get(padLocationKey2);
			sb.append(padLocation1 + ",");
			sb.append(padLocation2 + ",");
			sb.append(padLocation2 + ",");
			if(i<pads)
			sb.append(padLocation1 + ",");
			locations.remove(padLocationKey1);
			locations.remove(padLocationKey2);
		}
		sb.append(";");
		for (int i = 1; i <= hostages; i++) {
			locationsList = new ArrayList<Integer>(locations.keySet());
			int hostageLocationKey = locationsList.get(rand.nextInt(locationsList.size()));
			String hostageLocation = locations.get(hostageLocationKey);
			sb.append(hostageLocation + ",");
			int hostageDamage = rand.nextInt(99) + 1;
			if(i<hostages)
			sb.append(hostageDamage + ",");
			locations.remove(hostageLocationKey);
		}

		

		return sb.toString();
	}
	
	public static ArrayList<SearchTreeNode> expandNode(SearchTreeNode currentNode) {
		
		State currentState = currentNode.getState();
		int rows = currentState.rows;
		int columns = currentState.columns;
		int neoX = currentState.neoX;
		int neoY = currentState.neoY;
		int remainingHostages = currentState.remainingHostages;
		int remainingC = currentState.remainingC;
		int neoHealth = currentState.neoHealth;
		//hostages
		if(matrix[neoX][neoY].contains("Hostage")) {
			for(int i=0;i+7<matrix[neoX][neoY].length();i++) {
				if(matrix[neoX][neoY].substring(i, i+8).equals("Hostage")) {
					if(i+11<matrix[neoX][neoY].length()&&matrix[neoX][neoY].substring(i+9, i+12).equals("100") && currentState.remainingC!=0) {
						State state=new State();
						state.neoX=neoX;
						state.neoY=neoY;
						state.rows=rows;
						state.columns=columns;
						state.remainingHostages=remainingHostages;
						state.remainingC=remainingC-1;
						state.neoHealth=neoHealth;
					}
				}
			}
		}
		
		
		
		
		return new ArrayList<>();
		
	}
	
	public static void mapMatrix(String grid){
		String arr[] = grid.split(";");
		String [] dim = arr[0].split(",");
		int columns = Integer.parseInt(dim[0]);
		int rows = Integer.parseInt(dim[1]);
		matrix = new String[rows][columns];
		C = Integer.parseInt(arr[1]);
		String neoLocation[] = arr[2].split(",");
		matrix[Integer.parseInt(neoLocation[0])][Integer.parseInt(neoLocation[1])] = "Neo";
		String TBLocation[] = arr[3].split(",");
		matrix[Integer.parseInt(TBLocation[0])][Integer.parseInt(TBLocation[1])] = "TB";
		String agents[] = arr[4].split(",");
		for(int i=0; i<agents.length; i+=2) {
			matrix[Integer.parseInt(agents[i])][Integer.parseInt(agents[i+1])] = "AgentF";
		}
		String pills[] = arr[5].split(",");
		for(int i=0; i<pills.length; i+=2) {
			matrix[Integer.parseInt(pills[i])][Integer.parseInt(pills[i+1])] = "Pill";
		}
		String pads[] = arr[6].split(",");
		for(int i=0; i<pads.length; i+=4) {
			matrix[Integer.parseInt(pads[i])][Integer.parseInt(pads[i+1])] = "Pad&"+pads[i+2]+"&"+pads[i+3];
			matrix[Integer.parseInt(pads[i+2])][Integer.parseInt(pads[i+3])] = "Pad&"+pads[i]+"&"+pads[i+1];
		}
		String hostages[] = arr[7].split(",");
		for(int i=0; i<hostages.length; i+=3) {
			matrix[Integer.parseInt(hostages[i])][Integer.parseInt(hostages[i+1])] = "Hostage&"+hostages[i+2];
		}
		
	}

	public static void main(String[] args) {
		System.out.println(genGrid());

	}
}
