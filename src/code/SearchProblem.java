package code;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class SearchProblem {
	
	Set<Operator> operators;
	String initialState;
	ArrayList<String> stateSpace;
	Predicate<SearchTreeNode> goalTest;
	Function<SearchTreeNode, Integer> pathCostFunction;

	public SearchTreeNode generalSearch(SearchProblem problem, Function<Queue<SearchTreeNode>, Queue<SearchTreeNode>> qingFunction) {
		SearchTreeNode root = new SearchTreeNode(null, null, null, 0, 0);
		
		Queue<SearchTreeNode> nodes = new LinkedList<SearchTreeNode>();
		nodes.add(root);
		
		while(true) {
			if (nodes.size() == 0) return null;
			SearchTreeNode node = nodes.peek();
			if (goalTest.test(node)) return node;
			nodes = qingFunction.apply(nodes);
		}
	}
}
