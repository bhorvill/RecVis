import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Window extends JFrame implements ItemListener, ChangeListener, ComponentListener, ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	Visualizer visualizer;
	Caption caption;
	JCheckBox bSpeciesTree, bGeneNames, bTaxonNames, bEvents, bLineages;
	List<JCheckBox> bGeneTrees;
	JSlider sThickness, sZoom;
	JScrollPane picturePane, eventsPane;
	Camera camera;


	JPanel boxPanel, slidePanel, leftDock;
	
	JMenuBar menuBar;
	JMenu menuFile, menuView;
	JMenuItem itemOpenFile, itemOpenSzollosiFile, itemSavePicture;
	JMenuItem itemViewOptions;
	
	public Window(TreeReader reader) {
		
		addComponentListener(this);
		
		bSpeciesTree = new JCheckBox("Show species tree", true);
		bSpeciesTree.addItemListener(this);
		bGeneNames = new JCheckBox("Show gene names", true);
		bGeneNames.addItemListener(this);
		bTaxonNames = new JCheckBox("Show taxon names", true);
		bTaxonNames.addItemListener(this);
		bGeneTrees = new ArrayList<JCheckBox>();
		bEvents = new JCheckBox("Show events", true);
		bEvents.addItemListener(this);
		bLineages = new JCheckBox("Show gene lineages", true);
		bLineages.addItemListener(this);
	
		
		sThickness = new JSlider();
		sThickness.setMinimum(5);
		sThickness.setMaximum(200);
		sThickness.setValue(50);
		sThickness.addChangeListener(this);
		sZoom = new JSlider();
		sZoom.setMinimum(10);
		sZoom.setMaximum(1000);
		sZoom.setValue(100);
		sZoom.addChangeListener(this);
		
		setLayout(new BorderLayout());
		boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
		slidePanel = new JPanel();
		slidePanel.setLayout(new BoxLayout(slidePanel, BoxLayout.X_AXIS));
		

		eventsPane = new JScrollPane(boxPanel);
		caption = new Caption();
				
		leftDock = new JPanel();
		leftDock.setLayout(new BoxLayout(leftDock, BoxLayout.Y_AXIS));
		leftDock.add(eventsPane);
		leftDock.add(caption);
		
		camera = new Camera(sZoom);
		init(reader);
		if(reader != null) {
			for(int i=0; i<reader.getNumberOfGeneTrees(); i++) {
				JCheckBox bEvent = new JCheckBox("Show " + reader.getGeneTreeName(i), true);
				bEvent.addItemListener(this);
				bGeneTrees.add(bEvent);
			}
		}
		

		slidePanel.add(sThickness);
		slidePanel.add(sZoom);
		add(slidePanel, BorderLayout.NORTH);
		add(leftDock, BorderLayout.WEST);
		add(picturePane, BorderLayout.CENTER);
		
		
		
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		itemOpenFile = new JMenuItem("Open RecVis file");
		menuFile.add(itemOpenFile);
		itemOpenFile.addActionListener(this);
		itemOpenSzollosiFile = new JMenuItem("Open Ale file");
		menuFile.add(itemOpenSzollosiFile);
		itemOpenSzollosiFile.addActionListener(this);
		menuFile.addSeparator();
		itemSavePicture = new JMenuItem("Save as picture (PNG)");
		itemSavePicture.addActionListener(this);
		menuFile.add(itemSavePicture);
		
		menuView = new JMenu("View");
		menuBar.add(menuView);
		itemViewOptions = new JMenuItem("View options");
		itemViewOptions.addActionListener(this);
		menuView.add(itemViewOptions);
		
		
	}
	
	public void init(TreeReader reader) {

		bGeneTrees.clear();
		boxPanel.removeAll();
		boxPanel.add(bSpeciesTree);
		boxPanel.add(bTaxonNames);
		boxPanel.add(bEvents);
		boxPanel.add(bLineages);
		boxPanel.add(bGeneNames);
		if(reader!=null) {
			for(int i=0; i<reader.getNumberOfGeneTrees(); i++) {
				JCheckBox bEvent = new JCheckBox("Show " + reader.getGeneTreeName(i), true);
				bEvent.addItemListener(this);
				bGeneTrees.add(bEvent);
			}
		}
		for(int i=0; i<bGeneTrees.size(); i++) {
			boxPanel.add(bGeneTrees.get(i));
		}
		
		visualizer = new Visualizer(reader);
		picturePane = new JScrollPane(visualizer);
		add(picturePane, BorderLayout.CENTER);
		camera.setVisualizer(visualizer);
		camera.setScrollPane(picturePane);
		
		sThickness.setValue(visualizer.getThichness());
		sZoom.setValue(visualizer.getZoom());
		
		
		validate();
		
		
		
	}

	@Override
	public void itemStateChanged(ItemEvent arg) {
		if(arg.getSource()==bSpeciesTree) {
			visualizer.setSpeciesTreeVisible(bSpeciesTree.isSelected());
			bTaxonNames.setEnabled(bSpeciesTree.isSelected());
		}
		if(arg.getSource()==bTaxonNames) {
			visualizer.setTaxonNamesVisible(bTaxonNames.isSelected());
		}
		if(arg.getSource()==bEvents) {
			visualizer.setEventsVisible(bEvents.isSelected());
		}
		if(arg.getSource()==bLineages) {
			visualizer.setLineagesVisible(bLineages.isSelected());
		}
		if(arg.getSource()==bGeneNames) {
			visualizer.setGeneNamesVisible(bGeneNames.isSelected());
		}
		for(int i=0; i<bGeneTrees.size(); i++)
			if(arg.getSource()==bGeneTrees.get(i)) {
				visualizer.setGeneTreeVisible(i, bGeneTrees.get(i).isSelected());
			}
		validate();
		
	}

	@Override
	public void stateChanged(ChangeEvent arg) {
		if(arg.getSource()==sThickness) {
			visualizer.setThickness(sThickness.getValue());
		}
		if(arg.getSource()==sZoom) {
			visualizer.setZoom(sZoom.getValue());
		}
		visualizer.revalidate();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		visualizer.updateStandardSize(picturePane.getWidth(), picturePane.getHeight());
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public Visualizer getVisualizer() {
		return visualizer;
	}
	
	
	
	
	public static void main(String arg[]) {
		ParamManager.loadParam();
		TreeReader reader = null;
		String newick;
		
		if(arg.length>0) {
			if(arg[0].equals("-sz") || arg[0].equals("-ale")) {
				reader = new SzollosiReader();
				newick = arg[1];
			}
			else {
				reader = new SerializedTreeReader();
				newick = arg[0];
			}
			
			try {
				reader.readTree(newick);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		JFrame frame = new Window(reader);
		frame.setTitle("RecVis");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		if(arg.getSource()==itemOpenFile || arg.getSource() == itemOpenSzollosiFile) {
			FileDialog fileDialog = new FileDialog(this);
			fileDialog.setVisible(true);
			if(fileDialog.getFile() == null) {
				return;
			}
			String filePath = fileDialog.getDirectory() + fileDialog.getFile();
			TreeReader reader;
			if(arg.getSource()==itemOpenFile) {
				reader = new SerializedTreeReader();
			}
			else {
				reader = new SzollosiReader();
			}
			try {
				reader.readTree(filePath);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, "Error in the input file.");
				e.printStackTrace();
				return;
			}
			init(reader);
		}		
		if(arg.getSource() == itemSavePicture && !visualizer.isNull()) {
			FileDialog fileDialog = new FileDialog(this, "Save picture", FileDialog.SAVE);
			fileDialog.setFilenameFilter(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith("png");
				}
				
			});
			fileDialog.setVisible(true);
			if(fileDialog.getFile() == null) {
				return;
			}
			String filePath = fileDialog.getDirectory() + fileDialog.getFile();
			BufferedImage image = camera.getScreenShot();
			try {
               ImageIO.write(image, "png",new File(filePath));
              } 
			catch(Exception e) {
              e.printStackTrace();
            }
		}
		if(arg.getSource() == itemViewOptions) {
			new ViewDialog(this);
		}
		
	}
	
	
	
}