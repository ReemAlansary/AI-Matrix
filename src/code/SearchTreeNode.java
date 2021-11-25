package code;

public class SearchTreeNode {
	
	private String state;
	private SearchTreeNode parent;
	private Operator generator;
	private int depth;
	private int pathCost;
	private int heuristic;
	private int visitedNodes;
	
	public SearchTreeNode(String state, SearchTreeNode parent, Operator generator, int depth, int pathCost, int heuristic) {
		this.state = state;
		this.parent = parent;
		this.generator = generator;
		this.depth = depth;
		this.pathCost = pathCost;
		this.heuristic = heuristic;
		
	}
	
	
	public String toString() {
		return state + "," + generator + "," + depth + "," + pathCost + "," + heuristic;
	}
	
	public SearchTreeNode getParent() {
		return parent;
	}

	public Operator getGenerator() {
		return generator;
	}

	public int getDepth() {
		return depth;
	}
	
	public int getPathCost() {
		return pathCost;
	}

	public String getState() {
		return state;
	}


	public int getVisitedNodes() {
		return visitedNodes;
	}


	public void setVisitedNodes(int visitedNodes) {
		this.visitedNodes = visitedNodes;
	}
}
