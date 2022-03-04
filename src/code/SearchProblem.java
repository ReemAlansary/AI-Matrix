package code;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.tree.TreeNode;

public abstract class SearchProblem {

	String initialState;
	Predicate<SearchTreeNode> goalTest;
	Function<SearchTreeNode, Long> pathCostFunction;

	public SearchProblem(String initialState, Predicate<SearchTreeNode> gaolTest,
			Function<SearchTreeNode, Long> pathCostFunction) {
		this.initialState = initialState;
		this.goalTest = gaolTest;
		this.pathCostFunction = pathCostFunction;
	}

	public SearchTreeNode generalSearch(String strategy, SearchTreeNode root,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode,
			Function<SearchTreeNode, Integer> heuristic) {
		List<SearchTreeNode> nodes = new LinkedList<SearchTreeNode>();
		nodes.add(root);

		switch (strategy) {
		case "BF":
			return bfs(nodes, expandNode);
		case "DF":
			return dfs(nodes, expandNode);
		case "ID":
			return Idfs(nodes, expandNode);
		case "UC":
			return ucs(nodes, expandNode);
		case "GR1":
		case "GR2":
			return grs(nodes, expandNode, heuristic);
		case "AS1":
		case "AS2":
			return as(nodes, expandNode, heuristic);
		default:
			return null;
		}
	}

	public SearchTreeNode bfs(List<SearchTreeNode> nodes,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode) {
		HashSet<String> visitedStates = new HashSet<>();
		int counter = 0;
		while (!nodes.isEmpty()) {
			SearchTreeNode currentNode = nodes.remove(0);
			String state = currentNode.getState();
			String newState = newStateString(state);
			if (!visitedStates.contains(newState)) {
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

	public SearchTreeNode dfs(List<SearchTreeNode> nodes,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode) {
		SearchTreeNode node = nodes.remove(0);
		Stack<SearchTreeNode> nodesStack = new Stack<>();
		nodesStack.add(node);
		HashSet<String> visitedStates = new HashSet<>();
		int counter = 0;
		while (!nodesStack.isEmpty()) {
			SearchTreeNode currentNode = nodesStack.pop();
			String state = currentNode.getState();
			String newState = newStateString(state);
			if (!visitedStates.contains(newState)) {
				counter++;
				visitedStates.add(newState);
				if (goalTest.test(currentNode)) {
					currentNode.setVisitedNodes(counter);
					return currentNode;
				}

				ArrayList<SearchTreeNode> children = expandNode.apply(currentNode);
				for (int i = children.size() - 1; i >= 0; i--) {
					nodesStack.add(children.get(i));
				}
			}
		}
		return null;
	}

	public SearchTreeNode Idfs(List<SearchTreeNode> nodes,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode) {
		SearchTreeNode node = nodes.remove(0);
		Stack<SearchTreeNode> nodesStack = new Stack<>();
		// nodesStack.add(node);

		int counter = 0;
		int currentLevel = 0;
		int lastLevel = 1;

		int lastLevel1 = 0;
		while (true) {
			nodesStack.add(node);
			HashSet<String> visitedStates = new HashSet<>();
			while (!nodesStack.isEmpty()) {
				SearchTreeNode currentNode = nodesStack.pop();
				String state = currentNode.getState();
				String newState = newStateString(state);
				if (!visitedStates.contains(newState)) {
					counter++;
					visitedStates.add(newState);
					if (goalTest.test(currentNode)) {
						currentNode.setVisitedNodes(counter);
						return currentNode;
					}

					ArrayList<SearchTreeNode> children = expandNode.apply(currentNode);
					for (int i = children.size() - 1; i >= 0; i--) {
						if (children.get(i).getDepth() <= currentLevel) {
							nodesStack.add(children.get(i));
							lastLevel1 = Math.max(lastLevel1, children.get(i).getDepth());
						}
					}
				}
			}
			//
			// System.out.println("currentLevel: "+currentLevel+" last: "+lastLevel+"last1:
			// "+lastLevel1);
			if (lastLevel1 == lastLevel)
				return null;
			lastLevel = lastLevel1;
			currentLevel += 10;
		}
	}

	public SearchTreeNode ucs(List<SearchTreeNode> nodes,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode) {
		PriorityQueue<SearchTreeNode> pnodes = new PriorityQueue<>(new Comparator<SearchTreeNode>() {
			@Override
			public int compare(SearchTreeNode o1, SearchTreeNode o2) {
				return (int) (o1.getPathCost() - o2.getPathCost());
			}
		});
		pnodes.add(nodes.remove(0));
		HashSet<String> visitedStates = new HashSet<>();
		int counter = 0;
		while (!pnodes.isEmpty()) {
			SearchTreeNode currentNode = pnodes.poll();
			String state = currentNode.getState();
			String newState = newStateString(state);
			if (!visitedStates.contains(newState)) {
				counter++;
				visitedStates.add(newState);
				if (goalTest.test(currentNode)) {
					currentNode.setVisitedNodes(counter);
					return currentNode;
				}
				pnodes.addAll(expandNode.apply(currentNode));
			}
		}
		return null;
	}

	public SearchTreeNode grs(List<SearchTreeNode> nodes,
			Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode,
			Function<SearchTreeNode, Integer> heuristic) {

		PriorityQueue<SearchTreeNode> pnodes = new PriorityQueue<>(new Comparator<SearchTreeNode>() {
			@Override
			public int compare(SearchTreeNode o1, SearchTreeNode o2) {
				return heuristic.apply(o1) - heuristic.apply(o2);
			}
		});
		pnodes.add(nodes.remove(0));
		HashSet<String> visitedStates = new HashSet<>();
		int counter = 0;
		while (!pnodes.isEmpty()) {
			SearchTreeNode currentNode = pnodes.poll();
			String state = currentNode.getState();
			String newState = newStateString(state);
			if (!visitedStates.contains(newState)) {
				counter++;
				visitedStates.add(newState);
				if (goalTest.test(currentNode)) {
					currentNode.setVisitedNodes(counter);
					return currentNode;
				}
				pnodes.addAll(expandNode.apply(currentNode));
			}
		}
		return null;
	}

	public SearchTreeNode as(List<SearchTreeNode> nodes, Function<SearchTreeNode, ArrayList<SearchTreeNode>> expandNode,
			Function<SearchTreeNode, Integer> heuristic) {
		PriorityQueue<SearchTreeNode> pnodes = new PriorityQueue<>(new Comparator<SearchTreeNode>() {
			@Override
			public int compare(SearchTreeNode o1, SearchTreeNode o2) {
				return heuristic.apply(o1) - heuristic.apply(o2) + (int) (o1.getPathCost() - o2.getPathCost());
			}
		});
		pnodes.add(nodes.remove(0));
		HashSet<String> visitedStates = new HashSet<>();
		int counter = 0;
		while (!pnodes.isEmpty()) {
			SearchTreeNode currentNode = pnodes.poll();
			String state = currentNode.getState();
			String newState = newStateString(state);
			if (!visitedStates.contains(newState)) {
				counter++;
				visitedStates.add(newState);
				if (goalTest.test(currentNode)) {
					currentNode.setVisitedNodes(counter);
					return currentNode;
				}
				pnodes.addAll(expandNode.apply(currentNode));
			}
		}
		return null;
	}

	public String newStateString(String oldState) {
		String newState = "";
		String[] oldStateArr = oldState.split(";");
		// dimensions
		// String dims = oldStateArr[0];
		// carry info
		String[] cInfo = oldStateArr[1].split(",");
		// newState += cInfo[1] + ";";
		// neo location
		String[] neoInfo = oldStateArr[2].split(",");
		newState += neoInfo[0] + "," + neoInfo[1] + ";";
		// telephone booth
		String[] TBLocation = oldStateArr[3].split(",");
		// agents
		String agents = oldStateArr[4];
		newState += agents + ";";
		// pills
		String pills = oldStateArr[5];
		newState += pills + ";";
		// pads
		// String pads = oldStateArr[6];
		// hostages
		String[] hostages = oldStateArr[7].split(",");
		String newHostages = "";
		int rescuedHostages = 0;
		for (int i = 0; i < hostages.length - 4; i += 5) {
			newHostages += hostages[i] + "," + hostages[i + 1] + "," + hostages[i + 3] + /* ","+hostages[i+4]+ */",";
			if (hostages[i].equals(TBLocation[0]) && hostages[i + 1].equals(TBLocation[1]))
				rescuedHostages++;
		}
		if (newHostages.length() > 0) {
			newState += newHostages.substring(0, newHostages.length() - 1) + ";";
		} else {
			newState += newHostages + ";";
		}
		// newState += oldStateArr[7] +";" ;
		// hostages who became agents
		String agentsH = oldStateArr[8];
		newState += agentsH + ";";
		// kill number
		String killCount = oldStateArr[9];
		newState += killCount + ";";
		newState += rescuedHostages;

		// newState += oldStateArr[10];
		return newState;
	}
}
