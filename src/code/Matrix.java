package code;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.sun.management.OperatingSystemMXBean;

public class Matrix extends SearchProblem {

	public Matrix(String initialState, Predicate<SearchTreeNode> gaolTest,
			Function<SearchTreeNode, Long> pathCostFunction) {
		super(initialState, gaolTest, pathCostFunction);
	}

	public static String genGrid() {
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();

		// int m = rand.nextInt(11) + 5;
		// int n = rand.nextInt(11) + 5;
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
		Function<SearchTreeNode, Long> pathCostFunction = Matrix::getPathCost;
		Matrix problem = new Matrix(initialState, goalTest, pathCostFunction);
		SearchTreeNode goal = null;
		if (strategy.equals("GR1") || strategy.equals("AS1")) {
			goal = problem.generalSearch(strategy, root, Matrix::expandNode, Matrix::h1);
		} else if (strategy.equals("GR2") || strategy.equals("AS2")) {
			goal = problem.generalSearch(strategy, root, Matrix::expandNode, Matrix::h2);
		} else {
			goal = problem.generalSearch(strategy, root, Matrix::expandNode, null);
		}

		SearchTreeNode currentNode = goal;
		String actions = "";
		String states = "";
		if (goal == null)
			return "No Solution";
		while (currentNode.getParent() != null) {
			// System.out.println(currentNode.getState());
			actions = currentNode.getGenerator() + "," + actions;
			states = currentNode.getState() + "%" + states;
			currentNode = currentNode.getParent();

		}
		String[] initial = root.getState().split(";");
		int hostagesAtInitialState = root.getState().split(";")[7].length() == 0 ? 0
				: root.getState().split(";")[7].split(",").length / 5;
		int hostgesAtGoalState = goal.getState().split(";")[7].length() == 0 ? 0
				: goal.getState().split(";")[7].split(",").length / 5;
		String[] hostagesG = goal.getState().split(";")[7].split(",");
		int hostageDifference = hostagesAtInitialState - hostgesAtGoalState;
		for (int i = 0; i < hostagesG.length - 1; i += 5) {
			if (Integer.parseInt(hostagesG[i + 2]) >= 100) {
				hostageDifference++;
			}
		}
		actions = actions.substring(0, actions.length() - 1) + ";" + hostageDifference + ";"
				+ goal.getState().split(";")[9] + ";" + goal.getVisitedNodes();
		if (visualize && states.length() > 0) {
			states = states.substring(0, states.length() - 1);
			String[] statesArr = states.split("%");
			String[] actionsArr = actions.split(";")[0].split(",");
			for (int i = 0; i < statesArr.length; i++) {
				System.out.println("Action: " + (i + 1) + " (" + actionsArr[i] + ")");
				showActions(statesArr[i]);
				System.out.println("***************************************************");
			}
		}
		return actions;
	}

	public static void showActions(String state) {
		String result = "";
		// String state = dims + ";" + C + "," + remC + ";" + neoLocation + "," +
		// neoDamage + ";" + TBLocation + ";"
		// + agents + ";" + pills + ";" + pads + ";" + hostages + ";;0" + ";null";
		// hostages += allHostages[i] + "," + allHostages[i + 1] + "," + allHostages[i +
		// 2] + ",false,notrescued,";
		String[] stateArr = state.split(";");
		int rows = Integer.parseInt(stateArr[0].split(",")[0]);
		int cols = Integer.parseInt(stateArr[0].split(",")[1]);
		String[][] arr = new String[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				arr[i][j] = ".";
			}
		}
		String[] cInfo = stateArr[1].split(",");
		// System.out.println(state);
		System.out.println("neo now can carry up to " + cInfo[1] + " hostages");
		String killCount = stateArr[9];
		System.out.println("noe Killed " + killCount + " so far");
		// telephone booth
		String[] TBLocation = stateArr[3].split(",");
		arr[Integer.parseInt(TBLocation[0])][Integer.parseInt(TBLocation[1])] += "TB" + "+";
		String[] neoInfo = stateArr[2].split(",");
		arr[Integer.parseInt(neoInfo[0])][Integer.parseInt(neoInfo[1])] += "Neo(" + neoInfo[2] + ")" + "+";
		// agents
		String[] agents = stateArr[4].split(",");
		int j = 1;
		for (int i = 0; i < agents.length - 1; i += 2) {
			arr[Integer.parseInt(agents[i])][Integer.parseInt(agents[i + 1])] += "Agent" + j + "+";
			j++;
		}
		// pills
		String[] pills = stateArr[5].split(",");
		j = 1;
		for (int i = 0; i < pills.length - 1; i += 2) {
			arr[Integer.parseInt(pills[i])][Integer.parseInt(pills[i + 1])] += "Pill" + j + "+";
			j++;
		}
		// pads
		String[] pads = stateArr[6].split(",");
		j = 1;
		for (int i = 0; i < pads.length - 1; i += 8) {
			arr[Integer.parseInt(pads[i])][Integer.parseInt(pads[i + 1])] += "Pad" + j + "+";
			arr[Integer.parseInt(pads[i + 2])][Integer.parseInt(pads[i + 3])] += "Pad" + j + "+";
			j++;
		}
		// hostages
		String[] hostages = stateArr[7].split(",");
		j = 1;
		for (int i = 0; i < hostages.length - 1; i += 5) {
			arr[Integer.parseInt(hostages[i])][Integer.parseInt(hostages[i + 1])] += "H" + j + "(" + hostages[i + 2]
					+ ")" + "+";
			j++;
		}
		// hostages who became agents
		String[] agentsH = stateArr[8].split(",");
		j = 1;
		for (int i = 0; i < agentsH.length - 1; i += 2) {
			arr[Integer.parseInt(agentsH[i])][Integer.parseInt(agentsH[i + 1])] += "TurnedH" + j + "+";
			j++;
		}
		// kill number
		// System.out.println(Arrays.deepToString(arr));

		System.out
				.println(Arrays.deepToString(arr).replace("],", "\n").replace(",", "\t| ").replaceAll("[\\[\\]]", " "));

		// System.out.println(Arrays.deepToString(arr).replace("], ",
		// "]\n").replace("[[", "[").replace("]]", "]"));
		// for(int i=0; i<rows; i++) {
		// for(j=0; j<cols; j++) {
		// System.out.printf("%5f", arr[i][j]);
		// }
		// System.out.println();
		// }

		// return result;
	}

	public static boolean isGoalNode(SearchTreeNode node) {
		String[] state = node.getState().split(";");

		String[] neoInfo = state[2].split(",");
		String[] TBLocation = state[3].split(",");
		String[] hostages = state[7].split(",");

		boolean hostagesSafeOrDead = true;

		for (int i = 0; i < hostages.length - 4; i += 5) {
			if (!hostages[i + 4].equals("rescued")) {
				hostagesSafeOrDead = false;
				break;
			}
		}

		boolean turnedAgentsDead = state[8].length() > 0 ? false : true;
		boolean neoInBooth = (neoInfo[0].equals(TBLocation[0]) && neoInfo[1].equals(TBLocation[1])) ? true : false;
		return hostagesSafeOrDead && turnedAgentsDead && neoInBooth;
	}

	public static long getPathCost(SearchTreeNode node) {
		String parentState = node.getParent().getState();
		String state = node.getState();

		int hostagesAtInitialState = node.getParent().getState().split(";")[7].length() == 0 ? 0
				: node.getParent().getState().split(";")[7].split(",").length / 5;
		int hostgesAtGoalState = node.getState().split(";")[7].length() == 0 ? 0
				: node.getState().split(";")[7].split(",").length / 5;
		String[] hostagesG = node.getState().split(";")[7].split(",");
		int hostageDifference = hostagesAtInitialState - hostgesAtGoalState;
		for (int i = 0; i < hostagesG.length - 1; i += 5) {
			if (Integer.parseInt(hostagesG[i + 2]) >= 100) {
				hostageDifference++;
			}
		}

		int killDifference = 0;
		int parentKills = Integer.parseInt(parentState.split(";")[9]);
		int childKills = Integer.parseInt(state.split(";")[9]);
		killDifference = childKills - parentKills;

		long result = node.getParent().getPathCost() + 1000000 * hostageDifference + 1000 * killDifference
				+ node.getDepth();
		return result;
	}

	public static int h1(SearchTreeNode node) {
		String[] neo = node.getState().split(";")[2].split(",");
		String[] hostages = node.getState().split(";")[7].split(",");
		String[] pills = node.getState().split(";")[5].split(",");
		int pillCount = pills.length / 2;
		// int C = Integer.parseInt(node.getState().split(";")[1].split(",")[0]);
		// int remC = Integer.parseInt(node.getState().split(";")[1].split(",")[1]);
		int pillsDistance = 0;
		for (int i = 0; i < pills.length - 1; i += 2) {
			pillsDistance += Math.abs(Integer.parseInt(pills[i]) - Integer.parseInt(neo[0]))
					+ Math.abs(Integer.parseInt(pills[i + 1]) - Integer.parseInt(neo[1]));
		}
		int notRescued = 0;
		for (int i = 0; i < hostages.length - 1; i += 5) {
			int manhattanDistance = Math.abs(Integer.parseInt(hostages[i]) - Integer.parseInt(neo[0]))
					+ Math.abs(Integer.parseInt(hostages[i + 1]) - Integer.parseInt(neo[1]));

			if (pillCount > 0) {
				if ((Integer.parseInt(hostages[i + 2]) + manhattanDistance * 2 - 20) >= 100) {
					notRescued++;
				}
			} else {
				if ((Integer.parseInt(hostages[i + 2]) + manhattanDistance * 2) >= 100) {
					notRescued++;
				}
			}
		}
		return notRescued;
	}
	

	public static int h2(SearchTreeNode node) {
		String[] turnedAgents = node.getState().split(";")[8].split(",");
		String[] agents = node.getState().split(";")[4].split(",");

		if (turnedAgents.length > 0) {
			return (turnedAgents.length + agents.length) / 2;
		} else {
			return turnedAgents.length / 2;
		}
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
				+ agents + ";" + pills + ";" + pads + ";" + hostages + ";;0" + ";null";
		SearchTreeNode root = new SearchTreeNode(state, null, null, 0, 0, 0);
		return root;
	}

	public static String[] actionResult(String[] neo, String[] hostages, Operator action) {
		String remHostages = "";
		String turnedAgents = "";
		String newNeo = neo[0] + "," + neo[1] + "," + neo[2];

		switch (action) {
		case takePill:
			int neoDamage = Integer.parseInt(neo[2]) - 20;
			newNeo = neo[0] + "," + neo[1] + "," + (neoDamage < 0 ? 0 : neoDamage);
			break;
		case kill:
			int neoDamageKill = Integer.parseInt(neo[2]) + 20;
			newNeo = neo[0] + "," + neo[1] + "," + (neoDamageKill > 100 ? 100 : neoDamageKill);
			break;
		default:
		}

		for (int i = 2; i < hostages.length - 2; i += 5) {
			int damage = Integer.parseInt(hostages[i]);
			if (hostages[i + 2].equals("notrescued"))
				damage = Integer.parseInt(hostages[i]) + 2;
			if (Integer.parseInt(hostages[i]) >= 100 && hostages[i + 1].equals("false")
					&& hostages[i + 2].equals("notrescued")) {
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
					if (hostages[i + 1].equals("true")) {
						hostages[i + 2] = "rescued";
						hostages[i + 1] = "false";
						if (Integer.parseInt(hostages[i]) >= 98 && Integer.parseInt(hostages[i]) < 100) {
							damage = Integer.parseInt(hostages[i]) - 2;
						}
					}

					break;
				case takePill:
					if (hostages[i + 2].equals("notrescued") && Integer.parseInt(hostages[i]) < 100)
						damage = Integer.parseInt(hostages[i]) - 20;
					break;
				default:

				}
				hostages[i] = (damage < 0 ? 0 : damage) + "";
				remHostages += hostages[i - 2] + "," + hostages[i - 1] + "," + hostages[i] + "," + hostages[i + 1] + ","
						+ hostages[i + 2] + ",";
			}
		}

		if (turnedAgents.length() == 0 && remHostages.length() == 0) {
			return new String[] { remHostages, turnedAgents, newNeo };
		}
		if (turnedAgents.length() == 0) {
			return new String[] { remHostages.substring(0, remHostages.length() - 1), turnedAgents, newNeo };
		}
		if (remHostages.length() == 0) {
			return new String[] { remHostages, turnedAgents.substring(0, turnedAgents.length() - 1), newNeo };
		}

		return new String[] { remHostages.substring(0, remHostages.length() - 1),
				turnedAgents.substring(0, turnedAgents.length() - 1), newNeo };
	}

	public static boolean checkHostageDamage(String[] hostages, String[] neo) {
		for (int i = 0; i < hostages.length - 4; i += 5) {
			if (neo[0].equals(hostages[i]) && neo[1].equals(hostages[i + 1])
					&& (Integer.parseInt(hostages[i + 2]) >= 98) && hostages[i + 3].equals("false")
					&& hostages[i + 4].equals("notrescued")) {
				return true;
			}
		}
		return false;
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

		String[] rc = dims.split(",");
		String[] neo = neoInfo.split(",");
		String[] agentsSplit = agents.split(",");
		String[] turnedAgents = agentsH.split(",");

		String[] hostagesSplit = hostages.split(",");
		String[] telephone = TBLocation.split(",");

		// drop
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		String[] cinformation = cInfo.split(",");
		if (neo[0].equals(telephone[0]) && neo[1].equals(telephone[1])) {
			for (int i = 0; i < hostagesSplit.length - 3; i += 5) {
				if (hostagesSplit[i + 3].equals("true")) {
					if (agentsH.length() > 0) {
						String[] actionResult = actionResult(neo, hostages.split(","), Operator.drop);
						String newState = "";
						if (actionResult[1].length() > 0) {
							newState = dims + ";" + cinformation[0] + "," + (Integer.parseInt(cinformation[1]) + 1)
									+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
									+ pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + "," + actionResult[1]
									+ ";" + killCount + ";" + Operator.drop;
						} else {
							newState = dims + ";" + cinformation[0] + "," + (Integer.parseInt(cinformation[1]) + 1)
									+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
									+ pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ";" + killCount + ";"
									+ Operator.drop;
						}
						SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.drop, node.getDepth() + 1,
								0, 0);
						nextNode.setPathCost(getPathCost(nextNode));
						nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
						children.add(nextNode);
						break;
					} else {
						String[] actionResult = actionResult(neo, hostages.split(","), Operator.drop);
						String newState = "";
						if (actionResult[1].length() > 0) {
							newState = dims + ";" + cinformation[0] + "," + (Integer.parseInt(cinformation[1]) + 1)
									+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
									+ pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1] + ";"
									+ killCount + ";" + Operator.drop;
						} else {
							newState = dims + ";" + cinformation[0] + "," + (Integer.parseInt(cinformation[1]) + 1)
									+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
									+ pills + ";" + pads + ";" + actionResult[0] + ";" + ";" + killCount + ";"
									+ Operator.drop;
						}

						SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.drop, node.getDepth() + 1,
								0, 0);
						nextNode.setPathCost(getPathCost(nextNode));
						nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
						children.add(nextNode);
						break;
					}

				}
			}
		}

		// carry
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		for (int i = 0; i < hostagesSplit.length - 1; i += 5) {
			if (hostagesSplit[i].equals(neo[0]) && hostagesSplit[i + 1].equals(neo[1])
					&& (Integer.parseInt(hostagesSplit[i + 2]) < 100) && cInfo.charAt(2) > '0'
					&& !(hostagesSplit[i].equals(telephone[0]) && hostagesSplit[i + 1].equals(telephone[1]))
					&& hostagesSplit[i + 3].equals("false")) {
				if (agentsH.length() > 0) {
					String newState = "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.carry);
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo.charAt(0) + "," + (Integer.parseInt(cInfo.charAt(2) + "") - 1)
								+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + "," + actionResult[1]
								+ ";" + killCount + ";" + Operator.carry;
					} else {
						newState = dims + ";" + cInfo.charAt(0) + "," + (Integer.parseInt(cInfo.charAt(2) + "") - 1)
								+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ";" + killCount + ";"
								+ Operator.carry;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.carry, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
					break;
				} else {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.carry);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo.charAt(0) + "," + (Integer.parseInt(cInfo.charAt(2) + "") - 1)
								+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1] + ";" + killCount
								+ ";" + Operator.carry;
					} else {
						newState = dims + ";" + cInfo.charAt(0) + "," + (Integer.parseInt(cInfo.charAt(2) + "") - 1)
								+ ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation + ";" + agents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + ";" + killCount + ";"
								+ Operator.carry;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.carry, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
					break;
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
		if (!checkHostageDamage(hostages.split(","), neo)) {
			for (int i = 0; i < pillsSplit.length - 1; i += 2) {
				if (!(pillsSplit[i].equals(neo[0]) && pillsSplit[i + 1].equals(neo[1]))) {
					newPills += pillsSplit[i] + "," + pillsSplit[i + 1] + ",";
				}
			}
			if (newPills.length() > 0) {
				newPills = newPills.substring(0, newPills.length() - 1);
			}
			if (newPills.length() != pills.length()) {
				if (agentsH.length() > 0) {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.takePill);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + agents + ";"
								+ newPills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + "," + actionResult[1]
								+ ";" + killCount + ";" + Operator.takePill;
					} else {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + agents + ";"
								+ newPills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ";" + killCount + ";"
								+ Operator.takePill;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.takePill, node.getDepth() + 1,
							0, 0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				} else {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.takePill);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + agents + ";"
								+ newPills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1] + ";"
								+ killCount + ";" + Operator.takePill;
					} else {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + agents + ";"
								+ newPills + ";" + pads + ";" + actionResult[0] + ";" + ";" + killCount + ";"
								+ Operator.takePill;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.takePill, node.getDepth() + 1,
							0, 0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();

					children.add(nextNode);
				}
			}
		}

		// move up
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "u") && !checkHostageDamage(hostages.split(","),
				new String[] { (Integer.parseInt(neo[0]) - 1) + "", neo[1] })) {
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
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
								+ actionResult[1] + ";" + killCount + ";" + Operator.up;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ";"
								+ killCount + ";" + Operator.up;
					}

					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.up, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				} else {
					neo[0] = (Integer.parseInt(neo[0]) - 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.up);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";"
								+ actionResult[1] + ";" + killCount + ";" + Operator.up;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + ";"
								+ killCount + ";" + Operator.up;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.up, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				}
			}
		}

		// down
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "d") && !checkHostageDamage(hostages.split(","),
				new String[] { (Integer.parseInt(neo[0]) + 1) + "", neo[1] })) {
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
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
								+ actionResult[1] + ";" + killCount + ";" + Operator.down;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ";"
								+ killCount + ";" + Operator.down;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.down, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				} else {
					neo[0] = (Integer.parseInt(neo[0]) + 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.down);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";"
								+ actionResult[1] + ";" + killCount + ";" + Operator.down;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + ";"
								+ killCount + ";" + Operator.down;
					}

					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.down, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				}
			}
		}

		// left
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "l") && !checkHostageDamage(hostages.split(","),
				new String[] { neo[0], (Integer.parseInt(neo[1]) - 1) + "" })) {
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
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
								+ actionResult[1] + ";" + killCount + ";" + Operator.left;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ";"
								+ killCount + ";" + Operator.left;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.left, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				} else {
					neo[1] = (Integer.parseInt(neo[1]) - 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.left);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";"
								+ actionResult[1] + ";" + killCount + ";" + Operator.left;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + ";"
								+ killCount + ";" + Operator.left;
					}

					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.left, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				}
			}
		}

		// right
		rc = dims.split(",");
		neo = neoInfo.split(",");
		agentsSplit = agents.split(",");
		turnedAgents = agentsH.split(",");
		if (isValid(rc[0], rc[1], neo[0], neo[1], "r") && !checkHostageDamage(hostages.split(","),
				new String[] { neo[0], (Integer.parseInt(neo[1]) + 1) + "" })) {
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
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ","
								+ actionResult[1] + ";" + killCount + ";" + Operator.right;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + agentsH + ";"
								+ killCount + ";" + Operator.right;
					}
					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.right, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				} else {
					neo[1] = (Integer.parseInt(neo[1]) + 1) + "";
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.right);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";"
								+ actionResult[1] + ";" + killCount + ";" + Operator.right;
					} else {
						newState = dims + ";" + cInfo + ";" + neo[0] + "," + neo[1] + "," + neo[2] + ";" + TBLocation
								+ ";" + agents + ";" + pills + ";" + pads + ";" + actionResult[0] + ";" + ";"
								+ killCount + ";" + Operator.right;
					}

					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.right, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);
				}
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
					String newStateFly = "";
					if (actionResultFly[1].length() > 0) {
						newStateFly = dims + ";" + cInfo + ";" + newNeo + ";" + TBLocation + ";" + agents + ";" + pills
								+ ";" + pads + ";" + actionResultFly[0] + ";" + agentsH + "," + actionResultFly[1] + ";"
								+ killCount + ";" + Operator.fly;
					} else {
						newStateFly = dims + ";" + cInfo + ";" + newNeo + ";" + TBLocation + ";" + agents + ";" + pills
								+ ";" + pads + ";" + actionResultFly[0] + ";" + agentsH + ";" + killCount + ";"
								+ Operator.fly;
					}

					SearchTreeNode nextNodeFly = new SearchTreeNode(newStateFly, node, Operator.fly,
							node.getDepth() + 1, 0, 0);
					nextNodeFly.setPathCost(getPathCost(nextNodeFly));
					nextNodeFly.path = nextNodeFly.getParent().path + "," + nextNodeFly.getGenerator();
					children.add(nextNodeFly);
				} else {
					String[] actionResultFly = actionResult(neo, hostages.split(","), Operator.fly);
					String newStateFly = "";
					if (actionResultFly[1].length() > 0) {
						newStateFly = dims + ";" + cInfo + ";" + newNeo + ";" + TBLocation + ";" + agents + ";" + pills
								+ ";" + pads + ";" + actionResultFly[0] + ";" + actionResultFly[1] + ";" + killCount
								+ ";" + Operator.fly;
					} else {
						newStateFly = dims + ";" + cInfo + ";" + newNeo + ";" + TBLocation + ";" + agents + ";" + pills
								+ ";" + pads + ";" + actionResultFly[0] + ";" + ";" + killCount + ";" + Operator.fly;
					}

					SearchTreeNode nextNodeFly = new SearchTreeNode(newStateFly, node, Operator.fly,
							node.getDepth() + 1, 0, 0);
					nextNodeFly.setPathCost(getPathCost(nextNodeFly));
					nextNodeFly.path = nextNodeFly.getParent().path + "," + nextNodeFly.getGenerator();
					children.add(nextNodeFly);
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

		if (Integer.parseInt(neo[2]) < 80 && !checkHostageDamage(hostages.split(","), neo)) {
			for (int i = 0; i < agentsSplit.length - 1; i += 2) {
				for (int j = 0; j < adjacentAgents.length - 1; j += 2) {
					if (adjacentAgents[j].equals(agentsSplit[i]) && adjacentAgents[j + 1].equals(agentsSplit[i + 1])) {
						count++;
						agentsSplit[i] = "-1";
						agentsSplit[i + 1] = "-1";
					}
				}
			}
			for (int i = 0; i < agentsSplit.length - 1; i += 2) {
				if (!agentsSplit[i].equals("-1")) {
					newAgents += agentsSplit[i] + "," + agentsSplit[i + 1] + ",";
				}
			}

			for (int i = 0; i < turnedAgents.length - 1; i += 2) {
				for (int j = 0; j < adjacentAgents.length - 1; j += 2) {
					if (adjacentAgents[j].equals(turnedAgents[i])
							&& adjacentAgents[j + 1].equals(turnedAgents[i + 1])) {
						count++;
						turnedAgents[i] = "-1";
						turnedAgents[i + 1] = "-1";
					}
				}
			}
			for (int i = 0; i < turnedAgents.length - 1; i += 2) {
				if (!turnedAgents[i].equals("-1")) {
					newTurnedAgents += turnedAgents[i] + "," + turnedAgents[i + 1] + ",";
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
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + newAgents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + newTurnedAgents + ","
								+ actionResult[1] + ";" + (Integer.parseInt(killCount) + count) + ";" + Operator.kill;
					} else {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + newAgents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + newTurnedAgents + ";"
								+ (Integer.parseInt(killCount) + count) + ";" + Operator.kill;
					}

					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.kill, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);

				} else {
					String[] actionResult = actionResult(neo, hostages.split(","), Operator.kill);
					String newState = "";
					if (actionResult[1].length() > 0) {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + newAgents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + actionResult[1] + ";"
								+ (Integer.parseInt(killCount) + count) + ";" + Operator.kill;
					} else {
						newState = dims + ";" + cInfo + ";" + actionResult[2] + ";" + TBLocation + ";" + newAgents + ";"
								+ pills + ";" + pads + ";" + actionResult[0] + ";" + ";"
								+ (Integer.parseInt(killCount) + count) + ";" + Operator.kill;
					}

					SearchTreeNode nextNode = new SearchTreeNode(newState, node, Operator.kill, node.getDepth() + 1, 0,
							0);
					nextNode.setPathCost(getPathCost(nextNode));
					nextNode.path = nextNode.getParent().path + "," + nextNode.getGenerator();
					children.add(nextNode);

				}
			}
		}

		return children;
	}

	public static void main(String[] args) {
//		String[] grids = new String[] { "5,5;2;3,4;1,2;0,3,1,4;2,3;4,4,0,2,0,2,4,4;2,2,91,2,4,62",
//				"5,5;1;1,4;1,0;0,4;0,0,2,2;3,4,4,2,4,2,3,4;0,2,32,0,1,38",
//				"5,5;2;3,2;0,1;4,1;0,3;1,2,4,2,4,2,1,2,0,4,3,0,3,0,0,4;1,1,77,3,4,34",
//				"5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,1",
//				"5,5;1;0,4;4,4;0,3,1,4,2,1,3,0,4,1;4,0;2,4,3,4,3,4,2,4;0,2,98,1,2,98,2,2,98,3,2,98,4,2,98,2,0,98,1,0,98",
//				"5,5;2;0,4;3,4;3,1,1,1;2,3;3,0,0,1,0,1,3,0;4,2,54,4,0,85,1,0,43",
//				"5,5;2;3,0;4,3;2,1,2,2,3,1,0,0,1,1,4,2,3,3,1,3,0,1;2,4,3,2,3,4,0,4;4,4,4,0,4,0,4,4;1,4,57,2,0,46",
//				"5,5;3;1,3;4,0;0,1,3,2,4,3,2,4,0,4;3,4,3,0,4,2;1,4,1,2,1,2,1,4,0,3,1,0,1,0,0,3;4,4,45,3,3,12,0,2,88",
//				"5,5;2;4,3;2,1;2,0,0,4,0,3,0,1;3,1,3,2;4,4,3,3,3,3,4,4;4,0,17,1,2,54,0,0,46,4,1,22",
//				"5,5;2;0,4;1,4;0,1,1,1,2,1,3,1,3,3,3,4;1,0,2,4;0,3,4,3,4,3,0,3;0,0,30,3,0,80,4,4,80",
//				"5,5;4;1,1;4,1;2,4,0,4,3,2,3,0,4,2,0,1,1,3,2,1;4,0,4,4,1,0;2,0,0,2,0,2,2,0;0,0,62,4,3,45,3,3,39,2,3,40" };
//		for (int i = 0; i < grids.length; i++) {
//			System.out.println(solve(grids[i], "UC", false));
//			System.out.println(solve(grids[i], "AS1", false));
//			System.out.println(solve(grids[i], "AS2", false));
//		}
		
//		com.sun.management.OperatingSystemMXBean oss = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
//
//		long totalBefore = (oss.getTotalPhysicalMemorySize());
//		        long freeBefore = oss.getFreePhysicalMemorySize();
//		        solve("5,5;2;3,4;1,2;0,3,1,4;2,3;4,4,0,2,0,2,4,4;2,2,91,2,4,62", "DF", false);
//		        //solve("5,5;2;0,4;3,4;3,1,1,1;2,3;3,0,0,1,0,1,3,0;4,2,54,4,0,85,1,0,43", "DF", false);
//		        double cpuLoad = oss.getProcessCpuLoad() * 100;
//		        long totalAfter = (oss.getTotalPhysicalMemorySize());
//		        long freeAfter = oss.getFreePhysicalMemorySize();
//		        float z = (totalAfter-freeAfter)-(totalBefore-freeBefore);
//
//		System.out.println("CPU Utilization: " + String.format("%.02f", cpuLoad) + "%");
//		System.out.println("Memory Utilization: " + String.format("%.02f", (float) z / totalAfter * 100) + "%");
		
		OperatingSystemMXBean mbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
		
		System.out.println(solve("5,5;2;0,4;3,4;3,1,1,1;2,3;3,0,0,1,0,1,3,0;4,2,54,4,0,85,1,0,43", "AS2", false));
		double usageCPU = ((com.sun.management.OperatingSystemMXBean) mbean).getProcessCpuLoad() * 100;

        while (usageCPU == -1.0) {
            usageCPU = ((com.sun.management.OperatingSystemMXBean) mbean).getProcessCpuLoad() * 100;
            System.out.println("CPU utilization: " + usageCPU);
        }

        System.out.println("CPU utilization: " + usageCPU);

        java.lang.management.ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        System.out.println();
        double physicalMEMORY = 100
                * ((double) Runtime.getRuntime().totalMemory() - (double) Runtime.getRuntime().freeMemory())
                / Runtime.getRuntime().totalMemory();
        System.out.println("RAM USAGE: " + physicalMEMORY);
	}
}