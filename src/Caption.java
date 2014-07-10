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
		
		g.drawString("Species tree", 20, 30);
		g.drawString("species tree", 50, 60);
		g.drawString("extern lineages", 50, 90);
		g.drawString("Events", 20, 140);
		g.drawString("Origination", 50, 170);
		g.drawString("Duplication", 50, 200);
		g.drawString("Speciation", 50, 230);
		g.drawString("Speciation and loss", 50, 260);
		g.drawString("Transfer", 50, 290);
		g.drawString("Transfer and loss", 50, 320);
		
		g.setColor(ParamManager.cLineagesIn);
		g.fillRect(15, 45, 30, 20);
		
		g.setColor(ParamManager.cLineagesOut);
		g.fillRect(15, 75, 30, 20);
		
		g.setColor(ParamManager.cOrigination);
		g.fillOval(25-sizeEvent/2, 165-sizeEvent/2, sizeEvent, sizeEvent);
		g.setColor(Color.black);
		g.drawOval(25-sizeEvent/2, 165-sizeEvent/2, sizeEvent, sizeEvent);
		
		g.setColor(ParamManager.cDuplication);
		g.fillRect(25-sizeEvent/2, 195-sizeEvent, sizeEvent, 2*sizeEvent);
		
		g.setColor(ParamManager.cSpeciation);
		g.fillRect(25-sizeEvent/2, 225-sizeEvent/2, sizeEvent, sizeEvent);
		
		int[] xs = new int[] {(int) (25-Math.sqrt(3)*sizeEvent/2), 25, (int) (25+Math.sqrt(3)*sizeEvent/2)};
		int[] ys = new int[] {255+sizeEvent/2, 255-sizeEvent, 255+sizeEvent/2};
		g.fillPolygon(xs, ys, 3);
		
		g.setColor(ParamManager.cTransfer);
		g.fillRect(25-sizeEvent/2, 285-sizeEvent/2, sizeEvent, sizeEvent);
		
		xs = new int[] {(int) (25-Math.sqrt(3)*sizeEvent/2), 25, (int) (25+Math.sqrt(3)*sizeEvent/2)};
		ys = new int[] {315+sizeEvent/2, 315-sizeEvent, 315+sizeEvent/2};
		g.fillPolygon(xs, ys, 3);


	}
	
	
	
}
