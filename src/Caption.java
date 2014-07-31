import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


public class Caption extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sizeEvent;			//The size of the event icons


	public Caption() {
		
		setPreferredSize(new Dimension(200, 350));
		sizeEvent = 10;
		
	}
	
	
	public void paintComponent(Graphics g) {
		
		g.clearRect(0, 0, getWidth(), getHeight());
		
		int y = 30;
		g.setColor(Color.black);
		g.drawString("Species tree", 20, y);
		
		y+=30;

		g.setColor(Color.black);
		g.drawString("species tree", 50, y);
		g.setColor(ParamManager.cLineagesIn);
		g.fillRect(15, y-15, 30, 20);
		
		y+=30;
		
		g.setColor(Color.black);
		g.drawString("extern lineages", 50, y);
		g.setColor(ParamManager.cLineagesOut);
		g.fillRect(15, y-15, 30, 20);
		
		y+=50;
		
		g.setColor(Color.black);
		g.drawString("Events", 20, y);
		
		y+=30;
		
		g.setColor(Color.black);
		g.drawString("Origination", 50, y);
		g.setColor(ParamManager.cOrigination);
		g.fillOval(25-sizeEvent/2, y-5-sizeEvent/2, sizeEvent, sizeEvent);
		g.setColor(Color.black);
		g.drawOval(25-sizeEvent/2, y-5-sizeEvent/2, sizeEvent, sizeEvent);
		
		y+=30;
		
		if(ParamManager.showDuplications) {
			g.setColor(Color.black);
			g.drawString("Duplication", 50, y);
			g.setColor(ParamManager.cDuplication);
			g.fillRect(25-sizeEvent/2, y-5-sizeEvent, sizeEvent, 2*sizeEvent);
				
			y+=30;
		}
		
		if(ParamManager.showSpecBranchSource || ParamManager.showSpecNode) {
			g.setColor(Color.black);
			g.drawString("Speciation", 50, y);
			g.setColor(ParamManager.cSpeciation);
			g.fillRect(25-sizeEvent/2, y-5-sizeEvent/2, sizeEvent, sizeEvent);
			
			
			y+=30;
			
			g.setColor(Color.black);
			g.drawString("Speciation and loss", 50, y);
			g.setColor(ParamManager.cSpeciation);
			int[] xs = new int[] {(int) (25-Math.sqrt(3)*sizeEvent/2), 25, (int) (25+Math.sqrt(3)*sizeEvent/2)};
			int[] ys = new int[] {y-5+sizeEvent/2, y-5-sizeEvent, y-5+sizeEvent/2};
			g.fillPolygon(xs, ys, 3);
			
			y+=30;

		}
		
		if(ParamManager.showSpecBranchRecept) {
			g.setColor(Color.black);
			g.drawString("Speciation receiving", 50, y);
			g.setColor(ParamManager.cSpeciation);
			int[] xs = new int[] {25-sizeEvent/2, 25+sizeEvent, 25-sizeEvent/2};
			int[] ys = new int[] {(int) (y-5-Math.sqrt(3)*sizeEvent/2), y-5, (int) (y-5+Math.sqrt(3)*sizeEvent/2)};
			g.fillPolygon(xs, ys, 3);
			
			y+=30;
		}
		
		if(ParamManager.showTransferSource) {
			g.setColor(Color.black);
			g.drawString("Transfer back", 50, y);
			g.setColor(ParamManager.cTransfer);
			int[] xs = new int[] {(int) (25-Math.sqrt(3)*sizeEvent/2), 25, (int) (25+Math.sqrt(3)*sizeEvent/2)};
			int[] ys = new int[] {y-5+sizeEvent/2, y-5-sizeEvent, y-5+sizeEvent/2};
			g.fillPolygon(xs, ys, 3);
			
			y+=30;
		}
		
		if(ParamManager.showTransferRecept) {
			g.setColor(Color.black);
			g.drawString("Transfer receiving", 50, y);
			g.setColor(ParamManager.cTransfer);
			int[] xs = new int[] {25-sizeEvent/2, 25+sizeEvent, 25-sizeEvent/2};
			int[] ys = new int[] {(int) (y-5-Math.sqrt(3)*sizeEvent/2), y-5, (int) (y-5+Math.sqrt(3)*sizeEvent/2)};
			g.fillPolygon(xs, ys, 3);
			
			y+=30;
		}
		
		if(ParamManager.showLosses && (!ParamManager.showTransferSource || !ParamManager.showSpecBranchSource)) {
			g.setColor(Color.black);
			g.drawString("Loss", 50, y);
			g.drawLine(25-sizeEvent/2, y-5-sizeEvent/2, 25+sizeEvent/2,y-5+sizeEvent/2);
			g.drawLine(25-sizeEvent/2+1, y-5-sizeEvent/2, 25+sizeEvent/2+1,y-5+sizeEvent/2);
			g.drawLine(25-sizeEvent/2, y-5+sizeEvent/2, 25+sizeEvent/2,y-5-sizeEvent/2);
			g.drawLine(25-sizeEvent/2+1, y-5+sizeEvent/2, 25+sizeEvent/2+1,y-5-sizeEvent/2);
		}


	}
	
	
	
}
