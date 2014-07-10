
public class Event {

	private int id;
	private String type;
	private String name;
	private Node branch;
	private double time;
	private Event son1, son2;
	
	private int bus;
	
	public Event(int id, String type, String name, Node branch, double time, Event son1, Event son2) {
		this.id = id;
		this.type = type;
		this.name = name;
		this.branch = branch;
		this.time = time;
		this.son1 = son1;
		this.son2 = son2;
	}
	
	public static Event createLeaf(int id, Node branch, String name) {
		return new Event(id, "Leaf", name, branch, 0, null, null);
	}
	
	public static Event createOrigination(int id, String name, Node branch, double time, Event son) {
		return new Event(id, "O", name, branch, time, son, null);
	}
	
	public static Event createDuplication(int id, Node branch, double time, Event son1, Event son2) {
		return new Event(id, "D", "", branch, time, son1, son2);
	}
	
	public static Event createSpeciationOnNode(int id, Node branch, Event son1, Event son2) {
		return new Event(id, "S", "", branch, branch.getTime(), son1, son2);
	}
	
	public static Event createSpeciationLossOnNode(int id, Node branch, Event son1) {
		return new Event(id, "SL", "", branch, branch.getTime(), son1, null);
	}
	
	public static Event createSpeciationLossOnBranch(int id, Node branch, double time, Event son1) {
		return new Event(id, "SLb", "", branch, time, son1, null);
	}
	
	public static Event createSpeciationOnBranch(int id, Node branch, double time, Event son1, Event son2) {
		return new Event(id, "Sb", "", branch, time, son1, son2);
	}
	
	public static Event createTransferLoss(int id, Node out, double time, Event son1) {
		return new Event(id, "TLb", "", out, time, son1, null);
	}
	
	public static Event createTransfer(int id, Node out, double time, Event son1, Event son2) {
		return new Event(id, "Tb", "", out, time, son1, son2);
	}
	

	public int getID() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getTime() {
		return time;
	}
	
	public void setTime(double time) {
		this.time = time;
	}
	
	public Node getBranch() {
		return branch;
	}
	
	public Event getSon1() {
		return son1;
	}
	
	public Event getSon2() {
		return son2;
	}
	
	
	public int getBus() {
		return bus;
	}
	
	public void setBus(int bus) {
		this.bus = bus;
	}
}