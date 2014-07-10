import java.util.ArrayList;
import java.util.List;



public class Scheduler {

	//The types of events which free the bus
	private List<String> outTypes;
	
	public Scheduler() {
		outTypes = new ArrayList<String>();
		outTypes.add("S");
		outTypes.add("SL");
		outTypes.add("Leaf");
		outTypes.add("SLb");
		outTypes.add("TLb");
	}
	
	public int computeBus(List<Event> in) {
		int nBus = 0;
		for(Event e : in) {
			nBus+=computeBus(e, nBus);
			nBus++;
		}
		return nBus;
	}
	
	public int computeBus(Event e, int bus) {
		e.setBus(bus);
		int n = 0;
		if(e.getType().equals("D")) {
			n=1 + computeBus(e.getSon1(), bus) + computeBus(e.getSon2(), bus+1);
		}
		else if(!outTypes.contains(e.getType())) {
			return computeBus(e.getSon1(), bus);
		}
		return n;
	}
	
	
	
}
