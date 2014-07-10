
public class Node {

	//Parameters read from the input file
	private String name;
	private int id;
	private Node left, right;
	private Node father;
	private double time;
	private int nLeaves;
	
	//Parameters computed
	private int y;		//The vertical position of the branch, on the window
	private int nBus;	//The number of bus of this branch
	
	
	public Node(int id, String name, Node left, Node right, double time, int nLeaves) {
		this.id = id;
		this.name = name;
		this.left = left;
		this.right = right;
		this.time = time;
		this.nLeaves = nLeaves;
		this.father = null;
		y = 0;
	}
	
	static public Node createInternNode(int id, Node left, Node right, double time) {
		Node node = new Node(id, "", left, right, time, left.nLeaves+right.nLeaves);
		left.father = node;
		right.father = node;
		return node;
	}
	
	static public Node createLeaf(int id, String name, double time) {
		return new Node(id, name, null, null, time, 1);
	}
	
	static public Node createOut() {
		return new Node(0, "", null, null, 0, 1);
	}
	
	public int getID() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Node getLeft() {
		return left;
	}
	
	public Node getRight() {
		return right;
	}
	
	public Node getFather() {
		return father;
	}
	
	public double getTime() {
		return time;
	}
	
	public void setTime(double time) {
		this.time = time;
	}
	public int getNLeaves() {
		return nLeaves;
	}
	
	public boolean isLeaf() {
		return nLeaves == 1;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getY() {
		return y;
	}
	
	public int getNBus() {
		return nBus;
	}
	
	public void setNBus(int nBus) {
		this.nBus = nBus;
	}
}