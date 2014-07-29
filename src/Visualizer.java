import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class Visualizer extends JPanel {

	
	/**
	 * 
	 */
		
	private static final long serialVersionUID = 1L;
	//Parameters, from the input file
	private double scale;
	private int nLeaves;
	private Node spRoot;
	private List<Event> originations;

	
	private int width, height;	//The real size of the picture
	private int zoom;			//The zoom chosen by the user
	private int standardHeight, standardWidth;	//The part of the picture show on the screen
	private int widthBranch, heightBranch;	//The thickness of the species tree
	private int widthGeneLabels;
	private int widthSpeciesLabels;
	private int sizeEvent;					//The size of the event icons
	private int yTopBranch;					//The top bounding
	private int xRoot;						//The left bounding
	
	
	private TreeReader reader;
	private Scheduler scheduler;
	
	private boolean showSpeciesTree;	//Controlled by the user
	private boolean showTaxonNames;
	private boolean showGeneNames;
	private boolean showBranchLengths;
	private boolean showEvents;
	private boolean showLineages;
	private boolean showSpecBranchSource, showSpecBranchLin, showSpecBranchRecept;
	private boolean showTransferSource, showTransferLin, showTransferRecept;
	private boolean showSpecNode, showDuplications, showLosses;
	private List<Boolean> showGeneTree;
	
	public Visualizer(TreeReader reader) {
		//Initialization
		zoom = 100;
		standardWidth = 800;
		standardHeight = 600;
		
		yTopBranch = 90;
		widthBranch = 60;
		heightBranch = 60;
		sizeEvent = 10;
		xRoot = 40;
		
		setPreferredSize(new Dimension(standardWidth, standardHeight));
		
		
		this.reader = reader;
		scheduler = new Scheduler();
		//Compute the number of bus for each branch
		if(reader != null) {
			spRoot = reader.getRoot();
			scale = reader.getScale();
			originations = reader.getOriginations();
			nLeaves = spRoot.getNLeaves();
			computeNBus(spRoot);
			computeNBus(reader.getOut());
			
			showSpeciesTree = true;
			showTaxonNames = true;
			showBranchLengths = false;
			showGeneNames = true;
			showEvents = true;
			showLineages = true;
			showGeneTree = new ArrayList<Boolean>();
			widthGeneLabels = 0;
			for(int i=0; i<originations.size(); i++) {
				showGeneTree.add(true);
				int x = computeMaxGeneLabelNumberOfLetters(originations.get(i));
				widthGeneLabels = x > widthGeneLabels ? x : widthGeneLabels;
			}
			widthGeneLabels*=9;
			widthSpeciesLabels = 9*computeMaxSpeciesLabelNumberOfLetters(spRoot);
			
			widthBranch = standardHeight / 2 / reader.getNLeaves();
	
			
		}
		showSpecBranchSource = ParamManager.showSpecBranchSource;
		showSpecBranchLin = ParamManager.showSpecBranchLin;
		showSpecBranchRecept = ParamManager.showSpecBranchRecept;
		showTransferSource = ParamManager.showTransferSource;
		showTransferLin = ParamManager.showTransferLin;
		showTransferRecept = ParamManager.showTransferRecept;
		showSpecNode = ParamManager.showSpecNode;
		showDuplications = ParamManager.showDuplications;
		showLosses = ParamManager.showLosses;


		
	}

	public void computeNBus(Node node) {	//Recursive function computing the number of busses
		node.setNBus(scheduler.computeBus(reader.getIn(node)));
		if(!node.isLeaf()) {
			computeNBus(node.getLeft());
			computeNBus(node.getRight());
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		
		//Setting the size of the picture
		width = standardWidth*zoom/100;
		height = standardHeight*zoom/100;
		setPreferredSize(new Dimension(width, height));

		
		if(reader != null) {
			
			//Clearing the picture
			g.setColor(ParamManager.cBackground);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			//Paint the species tree
			if(showSpeciesTree) {
				g.setColor(ParamManager.cLineagesIn);
				paintNode(g, spRoot, scale, 0);
				
				g.setColor(ParamManager.cLineagesOut);
				paintNode(g, reader.getOut(), scale, nLeaves);
			}
			//Paint the events
			for(int i=0; i<originations.size(); i++) {
				if(showGeneTree.get(i)) {
					paintEvent(g, originations.get(i), 0);
				}
			}
		}
	}
	
	//Return the vectical position of the branch
	public int paintNode(Graphics g, Node node, double timeBegin, int nLeaveAbove) {
		int y=0;
		if(node.isLeaf()) {
			y = (int) ((height-2*yTopBranch)*((double)(nLeaveAbove))/nLeaves)+yTopBranch;
			if(node.getTime()==0) {
				g.fillRect(getX(node.getTime()), y-heightBranch/2, widthGeneLabels, heightBranch);	//The branch
				if(showTaxonNames) {
					g.drawString(node.getName(), getX(node.getTime())+widthGeneLabels+5, y+5);				//The name
				}

			}
			else {
				if(showTaxonNames) {
					g.drawString(node.getName(), getX(node.getTime())+20, y+5);				//The name
				}
			}
		}
		else {
			int y1 = paintNode(g, node.getLeft(), node.getTime(), nLeaveAbove);
			int y2 = paintNode(g, node.getRight(), node.getTime(), nLeaveAbove+node.getLeft().getNLeaves());
			y = (y1+y2)/2;
			g.fillRect(getX(node.getTime())-widthBranch/2, y1-heightBranch/2, widthBranch, (y2-y1)+heightBranch);
		}
		g.fillRect(getX(timeBegin), y-heightBranch/2, getX(node.getTime())-getX(timeBegin), heightBranch);
		if(showBranchLengths && node != reader.getOut()) {
			int n;
			if(node == reader.getRoot()) {
				n = (int) (100*(reader.getScale() - node.getTime()));
			}
			else {
				n = (int) (100*(node.getFather().getTime() - node.getTime()));
			}
			g.drawString(""+n, (getX(timeBegin)+getX(node.getTime()))/2, y-heightBranch/2-5);
		}
		node.setY(y);
		return y;
	}
	
	public int paintEvent(Graphics g, Event event, int x1) {
		
		String type = event.getType();
		
		//Computing the position of the branch
		Node branch = event.getBranch();
		int nBus = branch.getNBus();
		int bus = event.getBus();
		int shift = (int) ((-0.5+(bus+0.5)/nBus)*heightBranch);	//The position of the event from the center of the branch
		
		int y = branch.getY() + shift;
		if(type.equals("D")) {	//Duplication particularity : the mother branch has to be drawn between the two sister branches
			y += heightBranch/(2*nBus);
		}
		int x2 = getX(event.getTime());
		if(type.equals("S")||type.equals("SL")) {	//Speciation particularity : the event points has to be horizontally decayed, to avoid superposition
			x2+=shift/4;
		}
		
		
		
		//Drawing the daughter events, and getting their vertical position
		int y1=0, y2=0;		
		if(event.getSon1()!=null) {
			y1 = paintEvent(g, event.getSon1(), x2);
		}
		if(event.getSon2()!=null) {
			y2 = paintEvent(g, event.getSon2(), x2);
		}
		
		if(event.getSon2()!=null && event.getSon2().getBranch() == event.getBranch()) {
			y = (y1+y2)/2;
		}
		else if(event.getSon1()!=null && event.getSon1().getBranch() == event.getBranch()) {
			y = y1;
		}

		
		//Drawing the event
		g.setColor(Color.black);
		if(type.equals("Leaf") && showGeneNames) {
			g.drawString(event.getName(), x2+2, y+5);
		}
		
		if(type.equals("O")) {
			if(showEvents) {
				g.setColor(ParamManager.cOrigination);
				g.fillOval(x2-sizeEvent/2, y-sizeEvent/2, sizeEvent, sizeEvent);
				g.setColor(Color.black);
				g.drawOval(x2-sizeEvent/2, y-sizeEvent/2, sizeEvent, sizeEvent);
			}
		}
		else if(showLineages){
			g.drawLine(x1, y, x2, y);	//Drawing the branch leading to the event
		}
		if(type.equals("S")) {
			if(showLineages) {
				g.drawLine(x2, y, x2, y1);
				g.drawLine(x2, y, x2, y2);
			}
			if(showEvents && showSpecNode) {
				fillSpeciationSource(g, x2, y);
			}
		}
		else if(type.equals("SL")) {
			if(showLineages) {
				g.drawLine(x2, y, x2, y1);
			}
			if(showEvents && (showSpecNode || showLosses)) {
				if(y1<y) {
					fillSpeciationUp(g, x2, y);
				}
				else {
					fillSpeciationDown(g, x2, y);
				}
			}
		}
		else if(type.equals("Sb")) {
			if(showLineages && showSpecBranchLin) {
				g.drawLine(x2, y1, x2, y2);
			}
			if(showEvents && showSpecBranchSource) {
				fillSpeciationSource(g, x2, y);
			}
			if(showEvents && showSpecBranchRecept) {
				fillSpeciationRecept(g, x2, y2);
			}
		}
		else if(type.equals("SLb")) {
			if(showLineages && showSpecBranchLin) {
				g.drawLine(x2, y, x2, y1);
			}
			if(showEvents && showSpecBranchSource) {
				fillSpeciationDown(g, x2, y);
			}
			else if (showLosses){
				drawLoss(g, x2, y);
			}
			if(showEvents && showSpecBranchRecept) {
				fillSpeciationRecept(g, x2, y1);
			}
		}
		else if(type.equals("Tb")) {
			if(showLineages && showTransferLin) {
				g.drawLine(x2, y1, x2, y2);
			}
			if(showEvents && showTransferSource) {
				fillTransferSource(g, x2, y);
			}
			if(showEvents && showTransferRecept) {
				fillTransferRecept(g, x2, y2);
			}
		}
		else if(type.equals("TLb")) {
			if(showLineages && showTransferLin) {
				g.drawLine(x2, y, x2, y1);
			}
			if(showEvents && showTransferSource) {
				if(y>y1)
					fillTransferUp(g, x2, y);
				else
					fillTransferDown(g, x2, y);
			}
			else if(showLosses) {
				drawLoss(g, x2, y);
			}
			if(showEvents && showTransferRecept) {
				fillTransferRecept(g, x2, y1);
			}
		}
		else if(type.equals("D")) {
			if(showEvents && showDuplications) {
				g.setColor(ParamManager.cDuplication);
				g.fillRect(x2-sizeEvent/2, y1-sizeEvent/4, sizeEvent, heightBranch/nBus+sizeEvent/2);
			}
		}
		
		return y;
		
	
	}
	
	
	public void fillSpeciationSource(Graphics g, int x, int y) {
		g.setColor(ParamManager.cSpeciation);
		g.fillRect(x-sizeEvent/2, y-sizeEvent/2, sizeEvent, sizeEvent);
	}
	
	public void fillSpeciationRecept(Graphics g, int x, int y) {
		g.setColor(ParamManager.cSpeciation);
		int[] xs = new int[] {x-sizeEvent/2, x+sizeEvent, x-sizeEvent/2};
		int[] ys = new int[] {(int) (y-Math.sqrt(3)*sizeEvent/2), y, (int) (y+Math.sqrt(3)*sizeEvent/2)};
		g.fillPolygon(xs, ys, 3);	
	}
	
	public void fillSpeciationUp(Graphics g, int x, int y) {
		g.setColor(ParamManager.cSpeciation);
		int[] xs = new int[] {(int) (x-Math.sqrt(3)*sizeEvent/2), x, (int) (x+Math.sqrt(3)*sizeEvent/2)};
		int[] ys = new int[] {y+sizeEvent/2, y-sizeEvent, y+sizeEvent/2};
		g.fillPolygon(xs, ys, 3);
	}
	
	public void fillSpeciationDown(Graphics g, int x, int y) {
		g.setColor(ParamManager.cSpeciation);
		int[] xs = new int[] {(int) (x-Math.sqrt(3)*sizeEvent/2), x, (int) (x+Math.sqrt(3)*sizeEvent/2)};
		int[] ys = new int[] {y-sizeEvent/2, y+sizeEvent, y-sizeEvent/2};
		g.fillPolygon(xs, ys, 3);
	}
	
	public void fillTransferSource(Graphics g, int x, int y) {
		g.setColor(ParamManager.cTransfer);
		g.fillRect(x-sizeEvent/2, y-sizeEvent/2, sizeEvent, sizeEvent);
	}
	
	public void fillTransferRecept(Graphics g, int x, int y) {
		g.setColor(ParamManager.cTransfer);
		int[] xs = new int[] {x-sizeEvent/2, x+sizeEvent, x-sizeEvent/2};
		int[] ys = new int[] {(int) (y-Math.sqrt(3)*sizeEvent/2), y, (int) (y+Math.sqrt(3)*sizeEvent/2)};
		g.fillPolygon(xs, ys, 3);	
	}
	
	public void fillTransferUp(Graphics g, int x, int y) {
		g.setColor(ParamManager.cTransfer);
		int[] xs = new int[] {(int) (x-Math.sqrt(3)*sizeEvent/2), x, (int) (x+Math.sqrt(3)*sizeEvent/2)};
		int[] ys = new int[] {y+sizeEvent/2, y-sizeEvent, y+sizeEvent/2};
		g.fillPolygon(xs, ys, 3);
	}
	
	public void fillTransferDown(Graphics g, int x, int y) {
		g.setColor(ParamManager.cTransfer);
		int[] xs = new int[] {(int) (x-Math.sqrt(3)*sizeEvent/2), x, (int) (x+Math.sqrt(3)*sizeEvent/2)};
		int[] ys = new int[] {y-sizeEvent/2, y+sizeEvent, y-sizeEvent/2};
		g.fillPolygon(xs, ys, 3);
	}
	
	public void drawLoss(Graphics g, int x, int y) {
		g.setColor(Color.black);
		g.drawLine(x-sizeEvent/2, y-sizeEvent/2, x+sizeEvent/2,y+sizeEvent/2);
		g.drawLine(x-sizeEvent/2+1, y-sizeEvent/2, x+sizeEvent/2+1,y+sizeEvent/2);
		g.drawLine(x-sizeEvent/2, y+sizeEvent/2, x+sizeEvent/2,y-sizeEvent/2);
		g.drawLine(x-sizeEvent/2+1, y+sizeEvent/2, x+sizeEvent/2+1,y-sizeEvent/2);

	}
	
	
	
		
	public int getX(double time) {
		return xRoot + (int) ((width-2*xRoot-widthSpeciesLabels-widthGeneLabels)*(1-(double)(time)/scale));
	}
	
	public int getCladeHeight(int nCladeLeaves) {
		return (int) ((height-2*yTopBranch)*((double)nCladeLeaves)/nLeaves);
	}
	
	
	public void setSpeciesTreeVisible(boolean val) {
		showSpeciesTree = val;
		repaint();
	}
	
	public void setGeneNamesVisible(boolean val) {
		showGeneNames = val;
		repaint();
	}
	
	public void setTaxonNamesVisible(boolean val) {
		showTaxonNames = val;
		repaint();
	}
	
	public void setBranchLengthsVisible(boolean val) {
		showBranchLengths = val;
		repaint();
	}
	
	public void setGeneTreeVisible(int i, boolean val) {
		showGeneTree.set(i, val);
		repaint();
	}
	
	public void setEventsVisible(boolean val) {
		showEvents = val;
		System.out.println("events" + val);
		repaint();
	}
	
	public void setLineagesVisible(boolean val) {
		showLineages = val;
		System.out.println("val" + val);
		repaint();
	}
	
	public void setThickness(int thickness) {
		widthBranch = thickness;
		heightBranch = thickness;
		repaint();
	}
	
	public int getThickness() {
		return widthBranch;
	}
	
	public void setSpecBranchSourceVisible(boolean specBranchSourceVisible) {
		this.showSpecBranchSource = specBranchSourceVisible;
		repaint(); 
	}
	
	public boolean getSpecBranchSourceVisible() {
		return showSpecBranchSource;
	}
	
	public void setSpecBranchLinVisible(boolean specBranchLinVisible) {
		this.showSpecBranchLin = specBranchLinVisible;
		repaint();
	}
	
	public boolean getSpecBranchLinVisible() {
		return showSpecBranchLin;
	}
	
	public void setSpecBranchReceptVisible(boolean specBranchReceptVisible) {
		this.showSpecBranchRecept = specBranchReceptVisible;
		repaint();
	}
	
	public boolean getSpecBranchReceptVisible() {
		return showSpecBranchRecept;
	}
	
	public void setTransferSourceVisible(boolean transferSourceVisible) {
		this.showTransferSource = transferSourceVisible;
		repaint(); 
	}
	
	public boolean getTransferSourceVisible() {
		return showTransferSource;
	}
	
	public void setTransferLinVisible(boolean transferLinVisible) {
		this.showTransferLin = transferLinVisible;
		repaint();
	}
	
	public boolean getTransferLinVisible() {
		return showTransferLin;
	}
	
	public void setTransferReceptVisible(boolean transferReceptVisible) {
		this.showTransferRecept = transferReceptVisible;
		repaint();
	}
	
	public boolean getTransferReceptVisible() {
		return showTransferRecept;
	}
	
	public void setSpecNodeVisible(boolean specNodeVisible) {
		this.showSpecNode = specNodeVisible;
		repaint(); 
	}
	
	public boolean getSpecNodeVisible() {
		return showSpecNode;
	}
	
	public void setDuplicationsVisible(boolean duplicationsVisible) {
		this.showDuplications = duplicationsVisible;
		repaint();
	}
	
	public boolean getDuplicationsVisible() {
		return showDuplications;
	}
	
	public void setLossesVisible(boolean lossesVisible) {
		this.showLosses = lossesVisible;
		repaint();
	}
	
	public boolean getLossesVisible() {
		return showLosses;
	}
	
	public void setZoom(int zoom) {
		this.zoom = zoom;
		repaint();
	}
	
	public int getZoom() {
		return zoom;
	}


	public void updateStandardSize(int newStandardWidth, int newStandardHeight) {
		standardWidth = newStandardWidth;
		standardHeight = newStandardHeight;
		repaint();
	}
	
	public int computeMaxGeneLabelNumberOfLetters(Event e) {
		if(e.getType().equals("Leaf")) {
			return e.getName().length();
		}
		else {
			int x = 0;
			if(e.getSon1()!=null) {
				int y = computeMaxGeneLabelNumberOfLetters(e.getSon1());
				x = y>x ? y : x;
			}
			if(e.getSon2()!=null) {
				int y = computeMaxGeneLabelNumberOfLetters(e.getSon2());
				x = y>x ? y : x;
			}
			return x;
		}
	}
	
	public int computeMaxSpeciesLabelNumberOfLetters(Node node) {
		if(node.isLeaf()) {
			return node.getName().length();
		}
		else {
			int x = computeMaxSpeciesLabelNumberOfLetters(node.getLeft());
			int y = computeMaxSpeciesLabelNumberOfLetters(node.getRight());
			return x < y ? y : x;
		}
	}
	
	
	public int getRealWidth() {
		return standardWidth*zoom/100;
	}
	
	public int getRealHeight() {
		return standardHeight*zoom/100;
	}
	
	public boolean isNull() {
		return (reader == null);
	}


}