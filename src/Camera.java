import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;


public class Camera implements MouseListener, MouseMotionListener, MouseWheelListener {

	private JViewport viewPort;
	private Visualizer visualizer;
	private boolean rightPressed;
	
	int x_mouse, y_mouse;
	int x_corner, y_corner;
	JSlider sZoom;
	
	Camera(JSlider sZoom) {
		this.sZoom = sZoom;
		visualizer = null;
		rightPressed = false;
	}
	
	void setScrollPane(JScrollPane scrollPane) {
		this.viewPort = scrollPane.getViewport();
	}
	
	void setVisualizer(Visualizer vis) {
		visualizer = vis;
		vis.addMouseListener(this);
		vis.addMouseMotionListener(this);
		vis.addMouseWheelListener(this);
	}

	@Override
	public void mouseDragged(MouseEvent arg) {
		if(rightPressed) {
			int x = arg.getX(), y = arg.getY();
			int diffX = (x - x_mouse), diffY = (y-y_mouse);
			if(x_corner + diffX < 0) {
				diffX = -x_corner;
			}
			if(x_corner + diffX + viewPort.getWidth() > visualizer.getRealWidth()) {
				diffX = visualizer.getRealWidth()-viewPort.getWidth()-x_corner;
			}
			if(y_corner + diffY < 0) {
				diffY = -y_corner;
			}
			if(y_corner + diffY + viewPort.getHeight() > visualizer.getRealHeight()) {
				diffY = visualizer.getRealHeight()-viewPort.getHeight()-y_corner;
			}
			x_corner+=diffX;
			y_corner+=diffY;
			viewPort.setViewPosition(new Point(x_corner, y_corner));
			x_mouse = x+diffX;
			y_mouse = y+diffY;
			
			viewPort.repaint();
		}

		
	}

	@Override
	public void mouseMoved(MouseEvent arg) {
	
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg) {
		if(arg.getButton()==MouseEvent.BUTTON3) {
		}
		if(arg.getButton()==MouseEvent.BUTTON1) {
			rightPressed = true;
			x_corner = viewPort.getViewPosition().x;
			y_corner = viewPort.getViewPosition().y;
			x_mouse = arg.getX();
			y_mouse = arg.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg) {
		rightPressed = false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg) {
		
		int x = arg.getX(), y = arg.getY();
		int x_corner = viewPort.getViewPosition().x;
		int y_corner = viewPort.getViewPosition().y;
		
		int oldzoom = visualizer.getZoom();
		int newzoom = oldzoom - 10*arg.getWheelRotation();
		sZoom.setValue(newzoom);
		visualizer.setZoom(newzoom);
		//visualizer.revalidate();
				
		int newx_corner = x - (int) ((x-x_corner)* ((double) oldzoom)/newzoom) ;
		if(newx_corner < 0)
			newx_corner = 0;
		if(newx_corner + viewPort.getWidth() > visualizer.getRealWidth())
			newx_corner = viewPort.getWidth() - visualizer.getRealWidth();
		int newy_corner = y - (int) ((y-y_corner)* ((double) oldzoom)/newzoom) ;
		if(newy_corner < 0)
			newy_corner = 0;
		if(newy_corner + viewPort.getHeight() > visualizer.getRealHeight())
			newy_corner = viewPort.getHeight() - visualizer.getRealHeight();

		viewPort.setViewPosition(new Point(newx_corner, newy_corner));
		viewPort.repaint();

	}
	
	
	public BufferedImage getScreenShot() {

	    BufferedImage image = new BufferedImage(
	      visualizer.getWidth(),
	      visualizer.getHeight(),
	      BufferedImage.TYPE_INT_RGB
	      );
	    // call the Component's paint method, using
	    // the Graphics object of the image.
	    visualizer.paint( image.getGraphics() ); // alternately use .printAll(..)
	    return image;
	  }



}