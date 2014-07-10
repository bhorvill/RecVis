import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SzollosiReader extends TreeReader {

	private String geneSpeciesSeparator;		//In the gene name, the separator between the species part and the rest of it
	private int speciesPart;					//The number of geneSpeciesSeparator the species name contains. If -1, the speciesPart is supposed to be a prefix of the gene name.
	private int nextLeaf;
	private int nextEvent;
	private int cursor;
	
	private List<Double> timeSlice;
	private List<List<Event> > eventsInTimeSlices;
//	private double timeReturn;
	
	@Override
	public void readTree(String filepath) throws IOException {
		//Initialisation
		nodes = new HashMap<Integer, Node>();
		events = new HashMap<Integer, Event>();
		in = new HashMap<Node, List<Event>>();
		root = null;
		originations = new ArrayList<Event>();
		out = Node.createOut();
		in.put(out, new ArrayList<Event>());
		
		geneSpeciesSeparator = "";
		speciesPart = 0;
		
		
		nextLeaf = 0;
		nextEvent = 1;
		timeSlice = new ArrayList<Double>();
		
		//reading
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line;
		while((line = reader.readLine())!=null) {
			if(line.startsWith("separator string:")) {
				geneSpeciesSeparator = line.substring(line.indexOf('\t')+1);
			}
			if(line.startsWith("separator number:")) {
				speciesPart = Integer.parseInt(line.substring(line.indexOf('\t')+1));
			}
			if(line.startsWith("S:")) {
				String newick = line.substring(line.indexOf('('));
				SetNumberOfLeaves(newick);
				cursor = 0;
				root = readSpeciesTree(newick, ';');
				retime(root, getHeight(root));
				scale = 1.5*root.getTime();
				timeSlice.add(scale);
				sortTimeSlice();
				eventsInTimeSlices = new ArrayList<List<Event>>();
				for(int i=0; i<timeSlice.size(); i++) {
					eventsInTimeSlices.add(new ArrayList<Event>());
				}
			}
			if(line.startsWith("reconciled ")) {
				String name = line.substring(11, line.indexOf(":"));
				String newick = line.substring(line.indexOf('('));
				cursor = 0;
				Event event = readGeneTree(newick, ';');
				event.setName(name);
				originations.add(event);
			}
		}
		setBranchEventTimes();
		reader.close();
		
	}
	
	
	private void SetNumberOfLeaves(String newick) {
		int s = Integer.parseInt(newick.substring(newick.lastIndexOf(')')+1, newick.indexOf(';')));
		nLeaves = s+1;
		nextLeaf = nLeaves;
	}


	private Node readSpeciesTree(String newick, char endChar) {
				
		if(newick.charAt(cursor) == '(') {
			cursor++;
			Node left = readSpeciesTree(newick, ',');
			if(newick.charAt(cursor) != ',') {
				System.out.println(cursor);
			}
			cursor++;	//Coma
			Node right = readSpeciesTree(newick, ')');
			if(newick.charAt(cursor) != ')') {
				System.out.println(cursor);
			}
			cursor++;	//Closing parenthesis
			
			if(endChar == ';') {
				int sepTime = newick.indexOf(';', cursor);
				int id = Integer.parseInt(newick.substring(cursor, sepTime));
				Node node = Node.createInternNode(id, left, right, 0);
				nodes.put(id, node);
				in.put(node, new ArrayList<Event>());
				return node;
			}
			else {			
				int sepTime = newick.indexOf(':', cursor);
				int id = Integer.parseInt(newick.substring(cursor, sepTime));
				
				cursor = sepTime+1;
				
				sepTime = newick.indexOf(endChar, cursor);
				
				double time = Double.parseDouble(newick.substring(cursor, sepTime));
				cursor = sepTime;
				Node node = Node.createInternNode(id, left, right, time);
				nodes.put(id, node);
				in.put(node, new ArrayList<Event>());
				return node;
			}
		}
		else {
			
			int id = nextLeaf;
			nextLeaf++;
			
			int sepTime = newick.indexOf(':', cursor);
			String name = newick.substring(cursor, sepTime);
			cursor = sepTime+1;
			sepTime = newick.indexOf(endChar, cursor);
		
			double time = Double.parseDouble(newick.substring(cursor, sepTime));
			cursor = sepTime;
			Node node = Node.createLeaf(id, name, time);
			nodes.put(id, node);
			in.put(node, new ArrayList<Event>());
			return node;
		}
	}
	
	public double getHeight(Node node) {
		if(node.isLeaf()) {
			return 0;
		}
		else {
			double h1 = getHeight(node.getLeft())+node.getLeft().getTime();
			double h2 = getHeight(node.getRight())+node.getRight().getTime();
			return h1 > h2 ? h1 : h1;
		}
	}
	
	public void retime(Node node, double time) {
		time-=node.getTime();
		node.setTime(time);
		if(!node.isLeaf()) {
			retime(node.getLeft(), time);
			retime(node.getRight(), time);
			timeSlice.add(time);
		}
	}
	
	public String findSpeciesInName(String geneName) {
		if(speciesPart == 0) {
			return geneName;
		}
		else if(speciesPart>0 ){
			int index = -1;
			for(int i = 0; i<speciesPart; i++) {
				index = geneName.indexOf(geneSpeciesSeparator, index+1);
				if(index == -1) {
					System.out.println("Warning : in findSpeciesInName : not enough separators in the chain");
					return "";
				}
			}
			return geneName.substring(0, index);
		}
		else {
			int index = geneName.length();
			for(int i = 0; i<speciesPart; i++) {
				index = geneName.lastIndexOf(geneSpeciesSeparator, index);
				if(index == -1) {
					System.out.println("Warning : in findSpeciesInName : not enough separators in the chain");
					return "";
				}
			}
			return geneName.substring(index+geneSpeciesSeparator.length());
		}
	}
	
	
	public Event readGeneTree(String newick, char endChar) {
		
		if(newick.charAt(cursor) == '(') {
			Event event = null;
			cursor++;
			Event left = readGeneTree(newick, ',');
			cursor++;
			Event right = readGeneTree(newick, ')');
			cursor++;
			int sepTime = newick.indexOf(':', cursor);
			String label = newick.substring(cursor, sepTime);
			
			event = createBranchEvents(label, 0, left, right);
			
			cursor = sepTime+1;
			sepTime = newick.indexOf(endChar, cursor);
//			double length = Double.parseDouble(newick.substring(cursor, sepTime));
//			timeReturn = length;
			cursor = sepTime;
			

			
			if(endChar == ';') {
				event = Event.createOrigination(nextEvent, "", event.getBranch(), event.getTime(), event);
				in.get(event.getBranch()).add(event);				
				events.put(nextEvent, event);
				nextEvent++;
			}
			
			
			return event;
			
		}
		else {
			int sepTime = newick.indexOf(':', cursor);
			String fullName = newick.substring(cursor, sepTime);
			int posEndName = fullName.indexOf('@');
			if(posEndName == -1) {
				posEndName = fullName.length();
			}
			int posEndNameTemp = fullName.substring(0, posEndName).indexOf('.');
			if(posEndNameTemp != -1) {
				posEndName = posEndNameTemp;
			}
			String geneName = fullName.substring(0, posEndName);
			String speciesName = findSpeciesInName(geneName);
			Event event = Event.createLeaf(nextEvent, getSpecies(speciesName), geneName);
			events.put(nextEvent, event);
			nextEvent++;
			
			event = createBranchEvents(fullName, posEndName, event, null);			
			cursor = sepTime+1;
			sepTime = newick.indexOf(endChar, cursor);
			cursor = sepTime;
			return event;
		}
		
	}
	
	private int findPosEnd(String label, int cursor) {
		int posEnd = label.indexOf('@', cursor);
		int posEndPrime = label.indexOf('.', cursor);
		if(posEnd == -1 || (posEndPrime < posEnd && posEndPrime != -1)) {
			posEnd = posEndPrime;
		}
		if(posEnd == -1) {
			posEnd = label.length();
		}
		else if(label.substring(posEnd-2).startsWith(".T") || label.substring(posEnd-2).startsWith("Tb")) {
			posEnd -= 2;
		}
		else if(label.charAt(posEnd-1)=='T' || label.charAt(posEnd-1)=='D') {
			posEnd -= 1;
		}
		return posEnd;
	}
	
	
	
	
	private Event createBranchEvents(String label, int cursor, Event event1, Event event2) {
		if (cursor == label.length()) {		//A leaf
			return event1;
		}
		
		if(label.substring(cursor).startsWith(".T@")) {	//Speciation on branch and loss
			cursor+=3;
			int posEnd = findPosEnd(label, cursor);
			int posPipe = label.indexOf('|', cursor);
			int rank = Integer.parseInt(label.substring(cursor, posPipe));
			String branchName = label.substring(posPipe+1, posEnd);
			Node branch = getSpecies(branchName);
			if(branch == null) {
				if(branchName.equals("-1")) {
					branch = out;
				}
				else {
					branch = nodes.get(Integer.parseInt(branchName));
				}
			}
			Event event = Event.createSpeciationLossOnBranch(nextEvent, branch, 0,  createBranchEvents(label, posEnd, event1, event2));
			eventsInTimeSlices.get(rank).add(event);
			in.get(out).add(event.getSon1());
			events.put(nextEvent, event);
			nextEvent++;
			return event;
		}
	/*	else if(label.charAt(cursor) == '.') {
			int posEnd = findPosEnd(label, cursor+1);


			if(posEnd != label.length()) {											//Speciation on node and loss
				Node branch = nodes.get(Integer.parseInt(label.substring(cursor+1, posEnd)));
				Event event = createBranchEvents(label, posEnd, event1, event2);
				return linkTo(event, branch);
			}
			else if(event2 == null) {
				Node branch = nodes.get(Integer.parseInt(label.substring(cursor+1)));

				return linkTo(event1, branch);
			}
			else {																	//Speciation on node
				Node branch = nodes.get(Integer.parseInt(label.substring(cursor+1)));
				

				event1 = linkTo(event1, branch);
				event2 = linkTo(event2, branch);
				
				Event event = Event.createSpeciationOnNode(nextEvent, branch, event1, event2);

				in.get(event1.getBranch()).add(event1);
				in.get(event2.getBranch()).add(event2);
				events.put(nextEvent, event);
				nextEvent++;
				return event;
			}
		}*/
		else if(label.charAt(cursor) == '.') {
			int posEnd = findPosEnd(label, cursor+1);
			if(posEnd != label.length()) {											//Speciation on node and loss
				Node branch = nodes.get(Integer.parseInt(label.substring(cursor+1, posEnd)));
				Event event = createBranchEvents(label, posEnd, event1, event2);
				Node nextBranch = event.getBranch();
				
				if(nextBranch.getFather() != branch) {
					System.out.println("Error while creating Speciation Loss Node : " + branch.getID() + " " + nextBranch.getID());
				}
				
				event = Event.createSpeciationLossOnNode(nextEvent, branch, event);
				in.get(nextBranch).add(event);
				events.put(nextEvent, event);
				nextEvent++;
				return event;
			}
			else if(event2 == null) {		//Speciation on node and loss before a leaf
				Node branch = nodes.get(Integer.parseInt(label.substring(cursor+1)));
				Node nextBranch = event1.getBranch();
				
				if(nextBranch.getFather() != branch) {
					System.out.println("Error while creating Speciation Loss Leaf : " + branch.getID() + " " + nextBranch.getID());
				}
				
				Event event = Event.createSpeciationLossOnNode(nextEvent, branch, event1);
				in.get(nextBranch).add(event);
				events.put(nextEvent, event);
				nextEvent++;
				return event;
			}
			else {																	//Speciation on node
				Node branch = nodes.get(Integer.parseInt(label.substring(cursor+1)));
				
				Node nextBranch1 = event1.getBranch(), nextBranch2 = event2.getBranch();
				
				if(nextBranch1.getFather() != branch || nextBranch2.getFather() != branch) {
					System.out.println("Error while creating Speciation Node : " + branch.getID() + " " + nextBranch1.getID() + nextBranch2.getID());
				}
								
				Event event = Event.createSpeciationOnNode(nextEvent, branch, event1, event2);

				in.get(nextBranch1).add(event1);
				in.get(nextBranch2).add(event2);
				events.put(nextEvent, event);
				nextEvent++;
				return event;
			}
		}
		
		else if(label.charAt(cursor) == '@') {			//Transfer and loss
			int posEnd = findPosEnd(label, cursor+1);
			int posPipe = label.indexOf('|', cursor);
			int rank = Integer.parseInt(label.substring(cursor+1, posPipe));
			String branchName = label.substring(posPipe+1, posEnd);
			Node branch = getSpecies(branchName);
			if(branch == null) {
				if(branchName.equals("-1")) {
					branch = out;
				}
				else {
					branch = nodes.get(Integer.parseInt(branchName));
				}
			}
			Event event = Event.createTransferLoss(nextEvent, out, 0, createBranchEvents(label, posEnd, event1, event2));
			eventsInTimeSlices.get(rank).add(event);
			in.get(event.getSon1().getBranch()).add(event.getSon1());
			events.put(nextEvent, event);
			nextEvent++;
			return event;
		}
		
		else if(label.substring(cursor).startsWith("Tb@")) {	//Speciation on unrepresented space, transfer, and loss
			cursor +=3;
			int posEnd = findPosEnd(label, cursor);
			int posPipe = label.indexOf('|', cursor);
			int rank = Integer.parseInt(label.substring(cursor, posPipe));
			String branchName = label.substring(posPipe+1, posEnd);
			Node branch = getSpecies(branchName);
			if(branch == null) {
				if(branchName.equals("-1")) {
					branch = out;
				}
				else {
					branch = nodes.get(Integer.parseInt(branchName));
				}
			}
			if(event1.getBranch()!=out) {
				Event temp = event2;
				event2 = event1;
				event1 = temp;
			}
			Event event = Event.createTransfer(nextEvent, out, 0, event1, event2);
			eventsInTimeSlices.get(rank).add(event);
			in.get(branch).add(event.getSon2());
			events.put(nextEvent, event);
			nextEvent++;
			return event;
		}
	
		else if(label.substring(cursor).startsWith("D@")) {				//Duplication
			int posPipe = label.indexOf('|', cursor);
			int rank = Integer.parseInt(label.substring(cursor+2, posPipe));
			String branchName = label.substring(posPipe+1);
			Node branch = getSpecies(branchName);
			if(branch == null) {
				if(branchName.equals("-1")) {
					branch = out;
				}
				else {
					branch = nodes.get(Integer.parseInt(branchName));
				}
			}
			if(event2.getBranch()!=out) {
				Event temp = event2;
				event2 = event1;
				event1 = temp;
			}
			Event event = Event.createDuplication(nextEvent, branch, 0, event1, event2);
			eventsInTimeSlices.get(rank).add(event);
			events.put(nextEvent, event);
			nextEvent++;
			return event;
		}
		else if(label.substring(cursor).startsWith("T@")) {					//Speciation on branch
			int posPipe = label.indexOf('|', cursor+2);
			int rank = Integer.parseInt(label.substring(cursor+2, posPipe));
			String branchName = label.substring(posPipe+1);
			Node branch = getSpecies(branchName);
			if(branch == null) {
				if(branchName.equals("-1")) {
					branch = out;
				}
				else {
					branch = nodes.get(Integer.parseInt(branchName));
				}
			}

			
			if(event2.getBranch()!=out) {
				Event temp = event2;
				event2 = event1;
				event1 = temp;
			}
				
			Event event = Event.createSpeciationOnBranch(nextEvent, branch, 0, linkTo(event1, branch), event2);
			eventsInTimeSlices.get(rank).add(event);
			in.get(out).add(event2);
			events.put(nextEvent, event);
			nextEvent++;
			return event;
		}
		else {
			System.out.println("error : unknown event on branch");
			System.out.println(label.substring(cursor));
			return null;
		}
	}
	
	
	private Event linkTo(Event event, Node branch) {
		if(event.getBranch() == branch) {
			return event;
		}
		else {
			Event newEvent = Event.createSpeciationLossOnNode(nextEvent, event.getBranch().getFather(), event);
			in.get(event.getBranch()).add(event);
			events.put(nextEvent, newEvent);
			nextEvent++;
			return linkTo(newEvent, branch);
		}
	}


	public Node getSpecies(String name) {
		for(int i=nLeaves; i<2*nLeaves; i++) {
			if(nodes.get(i).getName().equals(name)) {
				return nodes.get(i);
			}
		}
		return null;
	}
	
	public void sortTimeSlice() {
		timeSlice.add(0.);
		for(int i=0; i<timeSlice.size(); i++) {
			for(int j=timeSlice.size()-1; j>i; j--) {
				if(timeSlice.get(i)>timeSlice.get(j)) {
					double d = timeSlice.get(j);
					timeSlice.set(j, timeSlice.get(i));
					timeSlice.set(i, d);
				}
			}
		}
	}
	
	public void setBranchEventTimes() {
				
		for(int i=0; i<eventsInTimeSlices.size()-1; i++) {
			double timeBegin = timeSlice.get(i);
			double timeEnd = timeSlice.get(i+1);
			
			int n = eventsInTimeSlices.get(i).size();
			for(int j = 0; j<n; j++) {
				eventsInTimeSlices.get(i).get(j).setTime(timeBegin + (j+1.0)*(timeEnd-timeBegin)/(n+1));
			}
			
			
		}
		
	}
	
	

}