import java.io.IOException;
import java.util.List;
import java.util.Map;


abstract public class TreeReader {
	
	//Parameters read from the input file
	protected double scale;
	protected int nLeaves;
	protected int nGenes;
	protected int nEvents;
	
	//Maps from ids to objects
	protected Map<Integer, Node> nodes;
	protected Map<Integer, Event> events;
	
	//Map giving, for each branch, the events leading to a new bus (O, S, Sb, T...)
	protected Map<Node, List<Event>> in;
	protected Node root;
	protected List<Event> originations;
	protected Node out;
	
	
	abstract public void readTree(String filepath) throws IOException;

	
	public Node getRoot() {
		return root;
	}
	
	public List<Event> getOriginations() {
		return originations;
	}
	
	public int getNLeaves() {
		return nLeaves;
	}
	
	public double getScale() {
		return scale;
	}
	
	public List<Event> getIn(Node node) {
		return in.get(node);
	}
	
	public Node getOut() {
		return out;
	}
	
	public int getNumberOfGeneTrees() {
		return originations.size();
	}
	
	public String getGeneTreeName(int i) {
		return originations.get(i).getName();
	}
}