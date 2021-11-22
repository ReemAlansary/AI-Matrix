package code;

public class SearchTreeNode {
	
	private State state;
	private SearchTreeNode parent;
	private Operator generator;
	private int depth;
	private int pathCost;
	
	public SearchTreeNode(State state, SearchTreeNode parent, Operator generator, int depth, int pathCost) {
		this.state = state;
		this.parent = parent;
		this.generator = generator;
		this.depth = depth;
		this.pathCost = pathCost;
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

	public State getState() {
		return state;
	}
}
