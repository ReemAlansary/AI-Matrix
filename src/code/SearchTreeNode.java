package code;

public class SearchTreeNode {
	
	private String state;
	private SearchTreeNode parent;
	private Operator generator;
	private int depth;
	private long pathCost;
	private int heuristic;
	private int visitedNodes;
	private boolean ucsAccessable;
	public String path = "";
	
	public SearchTreeNode(String state, SearchTreeNode parent, Operator generator, int depth, int pathCost, int heuristic) {
		this.state = state;
		this.parent = parent; 
		this.generator = generator;
		this.depth = depth;
		this.pathCost = pathCost;
		this.heuristic = heuristic;
		this.setUcsAccessable(true);
	}
	
	
	public String toString() {
//		return "Node: " + state + "," + generator + "," + depth + "," + pathCost + "," + heuristic;
		return "Node: " + pathCost + "," + depth;
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
	
	public long getPathCost() {
		return pathCost;
	}

	public void setPathCost(long pathCost) {
		this.pathCost = pathCost;
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


	public boolean isUcsAccessable() {
		return ucsAccessable;
	}


	public void setUcsAccessable(boolean ucsAccessable) {
		this.ucsAccessable = ucsAccessable;
	}
}
