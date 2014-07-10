import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SerializedTreeReader extends TreeReader {

@Override
public void readTree(String filepath) throws IOException {
		
		//Initialisation
		nodes = new HashMap<Integer, Node>();
		events = new HashMap<Integer, Event>();
		in = new HashMap<Node, List<Event>>();
		root = null;
		originations = new ArrayList<Event>();
		out = Node.createOut();
		
		//Open the input file, read it, and fill the maps
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		String line;
		while((line = reader.readLine())!=null) {
			if(line.equals("NUMBER OF LEAVES")) {
				line = reader.readLine();
				nLeaves = Integer.parseInt(line);
			}
			if(line.equals("NUMBER OF GENES")) {
				line = reader.readLine();
				nGenes = Integer.parseInt(line);
			}
			if(line.equals("NUMBER OF EVENTS")) {
				line = reader.readLine();
				nEvents = Integer.parseInt(line);
			}
			if(line.equals("DISCRETIZATION")) {
				line = reader.readLine();
				scale = Double.parseDouble(line);
			}
			if(line.equals("LEAVES")) {
				readLeaves(reader);
			}
			if(line.equals("INTERN NODES")) {
				root = readInterNodes(reader);
			}
			if(line.equals("GENES")) {
				readGenes(reader);
			}
			if(line.equals("EVENTS")) {
				readEvents(reader);
			}
		}
		reader.close();
		
	}

	private void readGenes(BufferedReader reader) throws IOException {
		for(int i=0; i<nGenes; i++) {
			String line = reader.readLine();
			String[] fields = line.split(":");
			int id = Integer.parseInt(fields[0]);
			String name = fields[1];
			int idBranch = Integer.parseInt(fields[2]);
			Event event = Event.createLeaf(id, nodes.get(idBranch), name);
			events.put(id, event);
		}
	}
	
	private void readEvents(BufferedReader reader) throws IOException {
		for(int i=0; i<nEvents; i++) {
			String line = reader.readLine();
			String[] fields = line.split(":");
			int id = Integer.parseInt(fields[0]);
			String type = fields[1];
			if(type.equals("O")) {
				String name = fields[2];
				int idBranch = Integer.parseInt(fields[3]);
				double time = Double.parseDouble(fields[4]);
				int idSon = Integer.parseInt(fields[5]);
				Event origination = Event.createOrigination(id, name, nodes.get(idBranch), time, events.get(idSon));
				originations.add(origination);
				events.put(id, origination);
				in.get(origination.getBranch()).add(origination);				
			}
			if(type.equals("S")) {
				int idBranch = Integer.parseInt(fields[2]);
				int idSon1 = Integer.parseInt(fields[3]);
				int idSon2 = Integer.parseInt(fields[4]);
				Event event = Event.createSpeciationOnNode(id, nodes.get(idBranch), events.get(idSon1), events.get(idSon2));
				events.put(id, event);
				in.get(event.getSon1().getBranch()).add(event.getSon1());
				in.get(event.getSon2().getBranch()).add(event.getSon2());
			}
			if(type.equals("SL")) {
				int idBranch = Integer.parseInt(fields[2]);
				int idSon = Integer.parseInt(fields[3]);
				Event event = Event.createSpeciationLossOnNode(id, nodes.get(idBranch), events.get(idSon));
				events.put(id, event);
				in.get(event.getSon1().getBranch()).add(event.getSon1());
			}
			if(type.equals("D")) {
				int idBranch = Integer.parseInt(fields[2]);
				double time = Double.parseDouble(fields[3]);
				int idSon1 = Integer.parseInt(fields[4]);
				int idSon2 = Integer.parseInt(fields[5]);
				Event event = Event.createDuplication(id, nodes.get(idBranch), time, events.get(idSon1), events.get(idSon2));
				events.put(id, event);
			}
			if(type.equals("Sb")) {
				int idBranch = Integer.parseInt(fields[2]);
				double time = Double.parseDouble(fields[3]);
				int idSon1 = Integer.parseInt(fields[4]);
				int idSon2 = Integer.parseInt(fields[5]);
				Event event = Event.createSpeciationOnBranch(id, nodes.get(idBranch), time, events.get(idSon1), events.get(idSon2));
				events.put(id, event);
				in.get(out).add(event.getSon2());
			}
			if(type.equals("SLb")) {
				int idBranch = Integer.parseInt(fields[2]);
				double time = Double.parseDouble(fields[3]);
				int idSon = Integer.parseInt(fields[4]);
				Event event = Event.createSpeciationLossOnBranch(id, nodes.get(idBranch), time, events.get(idSon));
				events.put(id, event);
				in.get(out).add(event.getSon1());
			}
			if(type.equals("T")) {
				int idBranch = Integer.parseInt(fields[2]);
				double time = Double.parseDouble(fields[3]);
				int idSon1 = Integer.parseInt(fields[4]);
				int idSon2 = Integer.parseInt(fields[5]);
				Event event = Event.createTransfer(id, nodes.get(idBranch), time, events.get(idSon1), events.get(idSon2));
				events.put(id, event);
				in.get(event.getSon2().getBranch()).add(event.getSon2());
			}
			if(type.equals("TL")) {
				int idBranch = Integer.parseInt(fields[2]);
				double time = Double.parseDouble(fields[3]);
				int idSon = Integer.parseInt(fields[4]);
				Event event = Event.createTransferLoss(id, nodes.get(idBranch), time, events.get(idSon));
				events.put(id, event);
				in.get(event.getSon1().getBranch()).add(event.getSon1());
			}
			
			
		}
	}

	private Node readInterNodes(BufferedReader reader) throws IOException {
		Node root = null;
		for(int i=0; i<nLeaves-1; i++) {
			String line = reader.readLine();
			String[] fields = line.split(":");
			int id = Integer.parseInt(fields[0]);
			int idLeft = Integer.parseInt(fields[1]);
			int idRight = Integer.parseInt(fields[2]);
			double time = Double.parseDouble(fields[3]);
			Node node = Node.createInternNode(id, nodes.get(idLeft), nodes.get(idRight), time);
			nodes.put(id, node);
			if(i==nLeaves-2) {
				root = node;
			}
			in.put(node, new ArrayList<Event>());
		}
		nodes.put(0, out);
		in.put(out, new ArrayList<Event>());
		return root;
	}

	private void readLeaves(BufferedReader reader) throws IOException {
		for(int i=0; i<nLeaves; i++) {
			String line = reader.readLine();
			String[] fields = line.split(":");
			int id = Integer.parseInt(fields[0]);
			String name = fields[1];
			double time = Double.parseDouble(fields[2]);
			Node node = Node.createLeaf(id, name, time);
			nodes.put(id, node);
			in.put(node, new ArrayList<Event>());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
}
