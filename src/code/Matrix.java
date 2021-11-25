package code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Matrix extends SearchProblem {
	static int up = 0;
	static int down = 0;
	static int left = 0;
	static int right = 0;
	static int carry = 0;
	static int drop = 0;
	static int takePill = 0;
	static int kill = 0;
	static int fly = 0;

	public Matrix(String initialState, Predicate<SearchTreeNode> gaolTest,
			Function<SearchTreeNode, Integer> pathCostFunction) {
		super(initialState, gaolTest, pathCostFunction);
	}

	public static String genGrid() {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();

//		int m = rand.nextInt(11) + 5;
//		int n = rand.nextInt(11) + 5;
		int m = 5;
		int n = 5;

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
		int carry = rand.nextInt(4) + 1;
		sb.append(carry + ";");
		sb.append(neo + ";");
		locationsList = new ArrayList(locations.keySet());
		int TBkey = locationsList.get(rand.nextInt(locationsList.size()));
		String TBlocation = locations.get(TBkey);
		locations.remove(TBkey);
		sb.append(TBlocation + ";");
		int hostages = rand.nextInt(8) + 3;
		int pills = rand.nextInt(hostages + 1);
		int remaining = m * n - pills - hostages - 2;
		int pads = rand.nextInt(remaining - 1) + 2;
		if (pads % 2 == 1) {
			pads -= 1;
		}
		remaining -= pads;
		int agents = rand.nextInt(remaining + 1);
		for (int i = 1; i <= agents; i++) {
			locationsList = new ArrayList(locations.keySet());
			int agentLocationKey = locationsList.get(rand.nextInt(locationsList.size()));
			String agentLocation = locations.get(agentLocationKey);
			sb.append(agentLocation);
			if (i < agents)
				sb.append(",");
			locations.remove(agentLocationKey);
		}
		sb.append(";");
		for (int i = 1; i <= pills; i++) {
			locationsList = new ArrayList<Integer>(locations.keySet());
			int pillLocationKey = locationsList.get(rand.nextInt(locationsList.size()));
			String pillLocation = locations.get(pillLocationKey);
			sb.append(pillLocation);
			if (i < pills)
				sb.append(",");
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
			sb.append(padLocation1);
			if (i < pads / 2)
				sb.append(",");
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
			sb.append(hostageDamage);
			if (i < hostages)
				sb.append(",");
			locations.remove(hostageLocationKey);
		}

		return sb.toString();
	}

	public static String solve(String grid, String strategy, boolean visualize) {
		SearchTreeNode root = MakeRoot(grid);
		String initialState = root.getState();
		Predicate<SearchTreeNode> goalTest = Matrix::isGoalNode;
		Function<SearchTreeNode, Integer> pathCostFunction = Matrix::getPathCost;
		Matrix problem = new Matrix(initialState, goalTest, pathCostFunction);
		SearchTreeNode goal = problem.generalSearch(strategy, root, Matrix::expandNode);
		SearchTreeNode currentNode = goal;
		String actions = "";
		if (goal == null)
			return "no solution";
		while (!currentNode.getParent().equals(null)) {
			// System.out.println(currentNode.getState());
			actions = currentNode.getGenerator() + "," + actions;
			currentNode = currentNode.getParent();

		}
		System.out.println(root.getState());
		String[] initial = root.getState().split(";");
		int hostagesAtInitialState = root.getState().split(";")[7].length() == 0 ? 0
				: root.getState().split(";")[7].split(",").length;
		int hostgesAtGoalState = goal.getState().split(";")[7].length() == 0 ? 0
				: goal.getState().split(";")[7].split(",").length;
		int hostageDifference = hostagesAtInitialState - hostgesAtGoalState;
		actions = actions.substring(0, actions.length() - 1) + ";" + hostageDifference + ";"
				+ goal.getState().split(";")[9] + ";" + goal.getVisitedNodes();

		return actions;
	}

	public static boolean isGoalNode(SearchTreeNode node) {
		String[] state = node.getState().split(";");

		if (state[2].split(",")[2].equals("100")) {
			return true;
		}

		String[] neoInfo = state[2].split(",");
		String[] TBLocation = state[3].split(",");
		String[] hostages = state[7].split(",");

		boolean hostagesSafeOrDead = true;
		for (int i = 0; i < hostages.length - 2; i += 4) {
			if ((Integer.parseInt(hostages[i + 2]) < 100)
					&& !(hostages[i].equals(TBLocation[0]) && hostages[i + 1].equals(TBLocation[1])))
			{
				hostagesSafeOrDead = false;
				break;
			}
		}

		boolean turnedAgentsDead = state[8].length() > 0 ? false : true;
		boolean neoInBooth = (neoInfo[0].equals(TBLocation[0]) && neoInfo[1].equals(TBLocation[1])) ? true : false;

		return hostagesSafeOrDead && turnedAgentsDead && neoInBooth;
	}

	public static int getPathCost(SearchTreeNode node) {
		String parentState = node.getParent().getState();
		String state = node.getState();

		int parentHostages = parentState.split(";")[7].length() == 0 ? 0 : parentState.split(";")[7].split(",").length;
		int childHostages = state.split(";")[7].length() == 0 ? 0 : state.split(";")[7].split(",").length;
		int hostageDifference = parentHostages - childHostages;

		int killDifference = 0;
		if (hostageDifference == 0) {
			int parentKills = Integer.parseInt(parentState.split(";")[9]);
			int childKills = Integer.parseInt(state.split(";")[9]);
			killDifference = childKills - parentKills;
		}

		int result = node.getParent().getPathCost() + hostageDifference + killDifference;
		return result;
	}

	public static boolean isValid(String N1, String M1, String x1, String y1, String move) {
		boolean valid = false;

		int N = Integer.parseInt(N1);
		int M = Integer.parseInt(M1);
		int x = Integer.parseInt(x1);
		int y = Integer.parseInt(y1);

		switch (move) {
		case "u":
			valid = x - 1 > 0 || x - 1 == 0 ? true : false;
			break;
		case "d":
			valid = x + 1 < N ? true : false;
			break;
		case "r":
			valid = y + 1 < M ? true : false;
			break;
		case "l":
			valid = y - 1 > 0 || y - 1 == 0 ? true : false;
			break;
		default:
			System.out.println("ERROR: " + x + y);
		}

		return valid;
	}

	public static SearchTreeNode MakeRoot(String grid) {
		String arr[] = grid.split(";");
		// dimensions
		String[] mn = arr[0].split(",");
		String dims = mn[1] + "," + mn[0];
		// carry info
		String C = arr[1];
		String remC = C;
		// neo location
		String neoLocation = arr[2];
		String neoDamage = "0";
		// telephone booth
		String TBLocation = arr[3];
		// agents
		String agents = arr[4];
		// pills
		String pills = arr[5];
		// pads
		String pads = arr[6];
		// hostages
		String hostages = "";
		String[] allHostages = arr[7].split(",");
		for (int i = 0; i < allHostages.length - 2; i += 3) {
			hostages += allHostages[i] + "," + allHostages[i + 1] + "," + allHostages[i + 2] + ",false,notrescued,";
		}
		hostages = hostages.substring(0, hostages.length() - 1);

		String state = dims + ";" + C + "," + remC + ";" + neoLocation + "," + neoDamage + ";" + TBLocation + ";"
				+ agents + ";" + pills + ";" + pads + ";" + hostages + ";;0";
		SearchTreeNode root = new SearchTreeNode(state, null, null, 0, 0, 0);
		return root;
	}

	public static void printArray(String[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(array[i] + ",");
		}
		System.out.println();
	}
	
	public static String[] actionResult(String[] neo, String[] hostages, Operator action) {
		String remHostages = "";
		String turnedAgents = "";
		String newNeo = "";
		
		for (int i = 2; i < hostages.length - 2; i += 5) {
			int damage = Integer.parseInt(hostages[i]) + 2;
			if (damage >= 100 && hostages[i + 1].equals("false")) {
				turnedAgents += hostages[i - 2] + "," + hostages[i - 1] + ",";
			} else {
				switch (action) {
				case carry:
					if (neo[0].equals(hostages[i - 2]) && neo[1].equals(hostages[i - 1])) {
						hostages[i + 1] = "true";
					}
					break;
				case up:
					if (hostages[i + 1].equals("true")) {
						hostages[i - 2] = neo[0];
						hostages[i - 1] = neo[1];
					}
					break;
				case down:
					if (hostages[i + 1].equals("true")) {
						hostages[i - 2] = neo[0];
						hostages[i - 1] = neo[1];
					}
					break;
				case left:
					if (hostages[i + 1].equals("true")) {
						hostages[i - 2] = neo[0];
						hostages[i - 1] = neo[1];
					}
					break;
				case right:
					if (hostages[i + 1].equals("true")) {
						hostages[i - 2] = neo[0];
						hostages[i - 1] = neo[1];
					}
					break;
				case drop:
					hostages[i + 1] = "false";
					hostages[i + 2] = "rescued";
					break;
				case takePill:
					int damageH = Integer.parseInt(hostages[i]) - 22;
					hostages[i] = (damageH < 0 ? 0 : damageH) + "";
					int neoDamage = Integer.parseInt(neo[2]) - 20;
					newNeo = neo[0] + "," + neo[1] + "," + (neoDamage < 0 ? 0 : neoDamage);
					break;
				case kill:
					int neoDamageKill = Integer.parseInt(neo[2]) + 20;
					newNeo = neo[0] + "," + neo[1] + "," + (neoDamageKill > 100 ? 100 : neoDamageKill);
					break;
				default:

				}
				remHostages += hostages[i - 2] + "," + hostages[i - 1] + "," + hostages[i] + "," + hostages[i + 1]
						+ "," + hostages[i + 2] + ",";
			}
		}
		if (turnedAgents.length() == 0) {
			return new String[] { remHostages.substring(0, remHostages.length() - 1), turnedAgents, newNeo };
		}
		return new String[] { remHostages.substring(0, remHostages.length() - 1),
				turnedAgents.substring(0, turnedAgents.length() - 1), newNeo };
	}

	public static ArrayList<SearchTreeNode> expandNode(SearchTreeNode node) {

		ArrayList<SearchTreeNode> children = new ArrayList<SearchTreeNode>();
		String[] state = node.getState().split(";");
		// dimensions
		String dims = state[0];
		// carry info
		String cInfo = state[1];
		// neo location
		String neoInfo = state[2];
		// telephone booth
		String TBLocation = state[3];
		// agents
		String agents = state[4];
		// pills
		String pills = state[5];
		// pads
		String pads = state[6];
		// hostages
		String hostages = state[7];
		// hostages who became agents
		String agentsH = state[8];
		// kill number
		String killCount = state[9];

		// for kill action
		String foundAgents = "";

		// move up
		String[] rc = dims.split(",");
		String[] neo = neoInfo.split(",");
		String[] agentsSplit = agents.split(",");
		String[] turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "u") && node.getGenerator() != Operator.down) {
			boolean canMove = true;
			for (int i = 0; i < agentsSplit.length - 1; i += 2) {
				if (agentsSplit[i].equals((Integer.parseInt(neo[0]) - 1) + "") && agentsSplit[i + 1].equals(neo[1])) {
					canMove = false;
					foundAgents += agentsSplit[i] + "," + agentsSplit[i + 1] + ",";
					break;
				}
			}
			for (int i = 0; i < turnedAgents.length - 1; i += 2) {
				if (turnedAgents[i].equals((Integer.parseInt(neo[0]) - 1) + "") && turnedAgents[i + 1].equals(neo[1])) {
					canMove = false;
					foundAgents += turnedAgents[i] + "," + turnedAgents[i + 1] + ",";
					break;
				}
			}

			if (canMove) {
				if (agentsH.length() > 0) {
					neo[0] = (Integer.parseInt(neo[0]) - 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.up);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
							+ actionResult[1] + ";" + killCount + ";" + Operator.up;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.up, node.getDepth() + 1,
							node.getPathCost() + up, 0);
					children.add(nextNode);
				} else {
					neo[0] = (Integer.parseInt(neo[0]) - 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.up);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1]
							+ ";" + killCount + ";" + Operator.up;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.up, node.getDepth() + 1,
							node.getPathCost() + up, 0);
					children.add(nextNode);
				}
			}
		}

		// down
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "d") && node.getGenerator() != Operator.up) {
			boolean canMove = true;
			for (int i = 0; i < agentsSplit.length - 1; i += 2) {
				if (agentsSplit[i].equals((Integer.parseInt(neo[0]) + 1) + "") && agentsSplit[i + 1].equals(neo[1])) {
					canMove = false;
					foundAgents += agentsSplit[i] + "," + agentsSplit[i + 1] + ",";
					break;
				}
			}
			for (int i = 0; i < turnedAgents.length - 1; i += 2) {
				if (turnedAgents[i].equals((Integer.parseInt(neo[0]) + 1) + "") && turnedAgents[i + 1].equals(neo[1])) {
					canMove = false;
					foundAgents += turnedAgents[i] + "," + turnedAgents[i + 1] + ",";
					break;
				}
			}

			if (canMove) {
				if (agentsH.length() > 0) {
					neo[0] = (Integer.parseInt(neo[0]) + 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.down);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
							+ actionResult[1] + ";" + killCount + ";" + Operator.down;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.down, node.getDepth() + 1,
							node.getPathCost() + down, 0);
					children.add(nextNode);
				} else {
					neo[0] = (Integer.parseInt(neo[0]) + 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.down);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1]
							+ ";" + killCount + ";" + Operator.down;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.down, node.getDepth() + 1,
							node.getPathCost() + down, 0);
					children.add(nextNode);
				}
			}
		}

		// left
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "l") && node.getGenerator() != Operator.right) {
			boolean canMove = true;
			for (int i = 0; i < agentsSplit.length - 1; i += 2) {
				if (agentsSplit[i].equals(neo[0]) && agentsSplit[i + 1].equals((Integer.parseInt(neo[1]) - 1) + "")) {
					canMove = false;
					foundAgents += agentsSplit[i] + "," + agentsSplit[i + 1] + ",";
					break;
				}
			}
			for (int i = 0; i < turnedAgents.length - 1; i += 2) {
				if (turnedAgents[i].equals(neo[0]) && turnedAgents[i + 1].equals((Integer.parseInt(neo[1]) - 1) + "")) {
					canMove = false;
					foundAgents += turnedAgents[i] + "," + turnedAgents[i + 1] + ",";
					break;
				}
			}

			if (canMove) {
				if (agentsH.length() > 0) {
					neo[1] = (Integer.parseInt(neo[1]) - 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.left);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
							+ actionResult[1] + ";" + killCount + ";" + Operator.left;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.left, node.getDepth() + 1,
							node.getPathCost() + left, 0);
					children.add(nextNode);
				} else {
					neo[1] = (Integer.parseInt(neo[1]) - 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.left);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1]
							+ ";" + killCount + ";" + Operator.left;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.left, node.getDepth() + 1,
							node.getPathCost() + left, 0);
					children.add(nextNode);
				}
			}
		}

		// right
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "r") && node.getGenerator() != Operator.left) {
			boolean canMove = true;
			for (int i = 0; i < agentsSplit.length - 1; i += 2) {
				if (agentsSplit[i].equals(neo[0]) && agentsSplit[i + 1].equals((Integer.parseInt(neo[1]) + 1) + "")) {
					canMove = false;
					foundAgents += agentsSplit[i] + "," + agentsSplit[i + 1] + ",";
					break;
				}
			}
			for (int i = 0; i < turnedAgents.length - 1; i += 2) {
				if (turnedAgents[i].equals(neo[0]) && turnedAgents[i + 1].equals((Integer.parseInt(neo[1]) + 1) + "")) {
					canMove = false;
					foundAgents += turnedAgents[i] + "," + turnedAgents[i + 1] + ",";
					break;
				}
			}

			if (canMove) {
				if (agentsH.length() > 0) {
					neo[1] = (Integer.parseInt(neo[1]) + 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.right);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
							+ actionResult[1] + ";" + killCount + ";" + Operator.right;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.right, node.getDepth() + 1,
							node.getPathCost() + right, 0);
					children.add(nextNode);
				} else {
					neo[1] = (Integer.parseInt(neo[1]) + 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.right);
					String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
							+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1]
							+ ";" + killCount + ";" + Operator.right;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.right, node.getDepth() + 1,
							node.getPathCost() + right, 0);
					children.add(nextNode);
				}
			}
		}

		// carry
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		String[] hostagesSplit = hostages.split(",");
		String[] telephone = TBLocation.split(",");
		for (int i = 0; i < hostagesSplit.length - 1; i += 5) {
			if (hostagesSplit[i].equals(neo[0]) && hostagesSplit[i + 1].equals(neo[1])
					&& (Integer.parseInt(hostagesSplit[i + 2]) + 2 < 100) && cInfo.charAt(2) > '0'
					&& !(hostagesSplit[i].equals(telephone[0]) && hostagesSplit[i + 1].equals(telephone[1]))) {
				if (agentsH.length() > 0) {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.carry);
					String newState = dims + ";" + cInfo.charAt(0) + "," + (Integer.parseInt(cInfo.charAt(2) + "") - 1)
							+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";" + pills
							+ ";" + pads + ";" + actionResult[0] + ";" + agentsH + "," + actionResult[1] + ";"
							+ killCount + ";" + Operator.carry;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.carry, node.getDepth() + 1,
							node.getPathCost() + carry, 0);
					children.add(nextNode);
					break;
				} else {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.carry);
					String newState = dims + ";" + cInfo.charAt(0) + "," + (Integer.parseInt(cInfo.charAt(2) + "") - 1)
							+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";" + pills
							+ ";" + pads + ";" + actionResult[0] + ";" + actionResult[1] + ";" + killCount + ";"
							+ Operator.carry;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.carry, node.getDepth() + 1,
							node.getPathCost() + carry, 0);
					children.add(nextNode);
					break;
				}
			}
		}

		// drop
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (neo[0].equals(telephone[0]) && neo[1].equals(telephone[1])) {
			for (int i = 0; i < hostagesSplit.length - 3; i += 5) {
				if (hostagesSplit[i + 3].equals("true")) {
					if (agentsH.length() > 0) {
						String[] actionResult = actionResult(neo, hostages.split(","), Operator.drop);
						String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";"
								+ TBLocation + ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";"
								+ agentsH + "," + actionResult[1] + ";" + killCount + ";" + Operator.drop;
						SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.drop, node.getDepth() + 1,
								node.getPathCost() + drop, 0);
						children.add(nextNode);
						break;
					} else {
						String[] actionResult = actionResult(neo, hostages.split(","), Operator.drop);
						String newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";"
								+ TBLocation + ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";"
								+ actionResult[1] + ";" + killCount + ";" + Operator.drop;
						SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.drop, node.getDepth() + 1,
								node.getPathCost() + drop, 0);
						children.add(nextNode);
						break;
					}
				}
			}
		}

		// takePill
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		String[] pillsSplit = pills.split(",");
		String newPills = "";
		for (int i = 0; i < pillsSplit.length - 1; i += 2) {
			if (!(pillsSplit[i].equals(neo[0]) && pillsSplit[i + 1].equals(neo[1]))) {
				newPills += pillsSplit[i] + "," + pillsSplit[i + 1] + ",";
			}
		}
		if (newPills.length() > 0) {
			newPills = newPills.substring(0, newPills.length() - 1);
			if (newPills.length() != pills.length()) {
				if (agentsH.length() > 0) {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.takePill);
					String newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + agents + ";"
							+ newPills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + "," + actionResult[1]
							+ ";" + killCount + ";" + Operator.takePill;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.takePill, node.getDepth() + 1,
							node.getPathCost() + takePill, 0);
					children.add(nextNode);
				} else {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.takePill);
					String newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + agents + ";"
							+ newPills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1] + ";" + killCount
							+ ";" + Operator.takePill;
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.takePill, node.getDepth() + 1,
							node.getPathCost() + takePill, 0);
					children.add(nextNode);
				}
			}
		}

		// kill
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		String[] adjacentAgents = foundAgents.split(",");
		String newAgents = "";
		String newTurnedAgents = "";

		int count = 0;
		for (int i = 0; i < agentsSplit.length - 1; i += 2) {
			for (int j = 0; j < adjacentAgents.length - 1; j += 2) {
				if (!adjacentAgents[j].equals(agentsSplit[i]) && !adjacentAgents[j + 1].equals(agentsSplit[i + 1])) {
					newAgents += agentsSplit[i] + "," + agentsSplit[i + 1] + ",";
				} else {
					count++;
				}
			}
		}
		for (int i = 0; i < turnedAgents.length - 1; i += 2) {
			for (int j = 0; j < adjacentAgents.length - 1; j += 2) {
				if (!adjacentAgents[j].equals(turnedAgents[i]) && !adjacentAgents[j + 1].equals(turnedAgents[i + 1])) {
					newTurnedAgents += turnedAgents[i] + "," + turnedAgents[i + 1] + ",";
				} else {
					count++;
				}
			}
		}

		if (newAgents.length() > 0) {
			newAgents = newAgents.substring(0, newAgents.length() - 1);
		}
		if (newTurnedAgents.length() > 0) {
			newTurnedAgents = newTurnedAgents.substring(0, newTurnedAgents.length() - 1);
		}
		if (foundAgents.length() > 0) {
			if (newTurnedAgents.length() > 0) {
				String[] actionResult = actionResult(neo, hostages.split(","), Operator.kill);
				String newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + newAgents + ";"
						+ pills + ";" + pads + ";" + actionResult[0] + ";" + newTurnedAgents + "," + actionResult[1]
						+ ";" + (Integer.parseInt(killCount) + count) + ";" + Operator.kill;
				SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.kill, node.getDepth() + 1,
						node.getPathCost() + kill, 0);
				children.add(nextNode);
			} else {
				String[] actionResult = actionResult(neo, hostages.split(","), Operator.kill);
				String newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + newAgents + ";"
						+ pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1] + ";"
						+ (Integer.parseInt(killCount) + count) + ";" + Operator.kill;
				SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.kill, node.getDepth() + 1,
						node.getPathCost() + kill, 0);
				children.add(nextNode);
			}
		}

		// fly
		if (node.getGenerator() != Operator.fly) {
			rc = dims.split(",");
			neo = neoInfo.split(",");
			agentsSplit = agents.split(",");
			turnedAgents = agentsH.split(",");
			String[] padsSplit = pads.split(",");
			String newNeo = "";
			for (int i = 0; i < padsSplit.length - 3; i += 4) {
				if (padsSplit[i].equals(neo[0]) && padsSplit[i + 1].equals(neo[1])) {
					newNeo += padsSplit[i + 2] + "," + padsSplit[i + 3] + "," + neo[2];
					break;
				}
				if (padsSplit[i + 2].equals(neo[0]) && padsSplit[i + 3].equals(neo[1])) {
					newNeo += padsSplit[i] + "," + padsSplit[i + 1] + "," + neo[2];
					break;
				}
			}
			if (newNeo.length() > 0) {
				if (agentsH.length() > 0) {
					String[] actionResultFly = actionResult(neo, hostages.split(","), Operator.fly);
					String newStateFly = dims + ";" + cInfo + ";" + newNeo + ";" + TBLocation + ";" + agents + ";" + pills
							+ ";" + pads + ";" + actionResultFly[0] + ";" + agentsH + "," + actionResultFly[1] + ";"
							+ killCount + ";" + Operator.fly;
					SearchTreeNode nextNodeFly = new SearchTreeNode(newStateFly, node, Operator.fly, node.getDepth() + 1,
							node.getPathCost() + fly, 0);
					children.add(nextNodeFly);
				} else {
					String[] actionResultFly = actionResult(neo, hostages.split(","), Operator.fly);
					String newStateFly = dims + ";" + cInfo + ";" + newNeo + ";" + TBLocation + ";" + agents + ";" + pills
							+ ";" + pads + ";" + actionResultFly[0] + ";" + actionResultFly[1] + ";" + killCount + ";"
							+ Operator.fly;
					SearchTreeNode nextNodeFly = new SearchTreeNode(newStateFly, node, Operator.fly, node.getDepth() + 1,
							node.getPathCost() + fly, 0);
					children.add(nextNodeFly);
				}

			}
		}

		return children;
	}

	public static void main(String[] args) {
		// String grid = genGrid();
//		SearchTreeNode root = MakeRoot("5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80");
//		ArrayList<SearchTreeNode> children = expandNode(root);
//		for (int i = 0; i < children.size(); i++) {
//			System.out.println(children.get(i));
//			System.out.println(
//					"___________________________________________________________________________________________________________________________");
//			ArrayList<SearchTreeNode> children1 = expandNode(children.get(i));
//			System.out.println("Children of " + i);
//			for (int j = 0; j < children1.size(); j++) {
//				System.out.println(children1.get(j));
//			}
//			System.out.println(
//					"___________________________________________________________________________________________________________________________");
//		}

//		System.out.println(solve("5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80",
//				"BF", false));
		System.out.println(solve("5,5;2;0,0,;0,1;;;;",
				"BF", false));
	}
}