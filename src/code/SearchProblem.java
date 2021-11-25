package code;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SearchProblem {

	String initialState;
	Predicate<SearchTreeNode> goalTest;
	Function<SearchTreeNode, Integer> pathCostFunction;

	public SearchProblem(String initialState, Predicate<SearchTreeNode> gaolTest,
			Function<SearchTreeNode, Integer> pathCostFunction) {
		this.initialState = initialState;
		this.goalTest = gaolTest;
		this.pathCostFunction = pathCostFunction;
	}

	public SearchTreeNode generalSearch(String strategy, SearchTreeNode root,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode) {
		Queue<SearchTreeNode> nodes = new LinkedList<SearchTreeNode>();
		nodes.add(root);

		switch (strategy.substring(0, 2)) {
		case "BF":
			return bfs(nodes, expandNode);
		default:
			return null;
		}
	}

	public SearchTreeNode bfs(Queue<SearchTreeNode> nodes,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode) {
		HashSet<String> visitedStates = new HashSet<>();
		int counter = 0;
		while (!nodes.isEmpty()) {
			SearchTreeNode currentNode = nodes.poll();
			String state = currentNode.getState();
			String newState = newStateString(state);
			if (!visitedStates.contains(newState)) {
//				if (currentNode != null && currentNode.getParent() != null) {
//					System.out.println(state + ":" + currentNode.getParent().getGenerator());
//				}
				counter++;
				visitedStates.add(newState);
				if (goalTest.test(currentNode)) {
					currentNode.setVisitedNodes(counter);
					return currentNode;
				}
				nodes.addAll(expandNode.apply(currentNode));
			}
		}
		return null;
	}

	public String newStateString(String oldState) {
		String newState = "";
		String[] oldStateArr = oldState.split(";");
		// dimensions
//		String dims = oldStateArr[0];
		// carry info
		String[] cInfo = oldStateArr[1].split(",");
		newState += cInfo[1] + ";";
		// neo location
		String neoInfo = oldStateArr[2];
		newState += neoInfo + ";";
		// telephone booth
		String[] TBLocation = oldStateArr[3].split(",");
		// agents
		String agents = oldStateArr[4];
		newState += agents + ";";
		// pills
		String pills = oldStateArr[5];
		newState += pills + ";";
		// pads
//		String pads = oldStateArr[6];
		// hostages
		String[] hostages = oldStateArr[7].split(",");
		String newHostages = "";
		int rescuedHostages = 0;
		for (int i = 0; i < hostages.length - 4; i += 5) {
			newHostages += hostages[i] + "," + hostages[i + 1] + "," + hostages[i + 3]+","+hostages[i+4]+",";
			if (hostages[i].equals(TBLocation[0]) && hostages[i+1].equals(TBLocation[1])) rescuedHostages++;
		}
		newState += newHostages.substring(0, newHostages.length() - 1) + ";";
		// hostages who became agents
		String agentsH = oldStateArr[8];
		newState += agentsH + ";";
//		System.out.println(agentsH);
		// kill number
		String killCount = oldStateArr[9];
		newState += killCount ;
//		newState += rescuedHostages;
		
//		newState += oldStateArr[10];
		return newState;
	}
}
