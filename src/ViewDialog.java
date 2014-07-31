import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class ViewDialog extends JDialog implements ActionListener, ItemListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private JLabel lSpecBranch;
	private JCheckBox bSpecBranchSource, bSpecBranchLin, bSpecBranchReceiving;
	private JPanel pSpecBranch, pSpecBranchButtons;
	
	private JLabel lTransfer;
	private JCheckBox bTransferSource, bTransferLin, bTransferReceiving;
	private JPanel pTransfer, pTransferButtons;
	
	private JLabel lOther;
	private JCheckBox bSpecNode, bDuplications, bLosses;
	private JPanel pOther, pOtherButtons;
	
	private JPanel pMain;
	private JButton bClose;
	
	private Visualizer vis;
	private Caption caption;
	
	
	ViewDialog(Window parent) {
		
		vis = parent.getVisualizer();
		caption = parent.getCaption();
		pMain = new JPanel();
		add(pMain);
		pMain.setLayout(new BoxLayout(pMain, BoxLayout.Y_AXIS));
		pMain.setAlignmentY(JPanel.CENTER_ALIGNMENT);
		
		pMain.add(Box.createRigidArea(new Dimension(1, 20)));
		
		
		pSpecBranch = new JPanel();
		pSpecBranch.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		pSpecBranch.setLayout(new BoxLayout(pSpecBranch, BoxLayout.Y_AXIS));
		pMain.add(pSpecBranch);
		lSpecBranch = new JLabel("Show the speciation events leading outside the tree by...");
		lSpecBranch.setHorizontalAlignment(JLabel.LEFT);
		lSpecBranch.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		pSpecBranch.add(lSpecBranch);
		pSpecBranchButtons = new JPanel();
		pSpecBranchButtons.setLayout(new BoxLayout(pSpecBranchButtons, BoxLayout.X_AXIS));
		pSpecBranch.add(pSpecBranchButtons);
		bSpecBranchSource = new JCheckBox("A source event", vis.getSpecBranchSourceVisible());
		bSpecBranchSource.addItemListener(this);
		pSpecBranchButtons.add(bSpecBranchSource);
		pSpecBranchButtons.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		bSpecBranchLin = new JCheckBox("A lineage", vis.getSpecBranchLinVisible());
		bSpecBranchLin.addItemListener(this);
		pSpecBranchButtons.add(bSpecBranchLin);
		bSpecBranchReceiving = new JCheckBox("A receiving event", vis.getSpecBranchReceivingVisible());
		bSpecBranchReceiving.addItemListener(this);
		pSpecBranchButtons.add(bSpecBranchReceiving);
		
		pMain.add(Box.createRigidArea(new Dimension(1, 20)));


		pTransfer = new JPanel();
		pTransfer.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		pTransfer.setLayout(new BoxLayout(pTransfer, BoxLayout.Y_AXIS));
		pMain.add(pTransfer);
		lTransfer = new JLabel("Show the transfers events by...");
		lTransfer.setHorizontalAlignment(JLabel.LEFT);
		lTransfer.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		pTransfer.add(lTransfer);
		pTransferButtons = new JPanel();
		pTransferButtons.setLayout(new BoxLayout(pTransferButtons, BoxLayout.X_AXIS));
		pTransfer.add(pTransferButtons);
		bTransferSource = new JCheckBox("A source event", vis.getTransferSourceVisible());
		bTransferSource.addItemListener(this);
		pTransferButtons.add(bTransferSource);
		pTransferButtons.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		bTransferLin = new JCheckBox("A lineage", vis.getTransferLinVisible());
		bTransferLin.addItemListener(this);
		pTransferButtons.add(bTransferLin);
		bTransferReceiving = new JCheckBox("A receiving event", vis.getTransferReceivingVisible());
		bTransferReceiving.addItemListener(this);
		pTransferButtons.add(bTransferReceiving);
		
		
		pMain.add(Box.createRigidArea(new Dimension(1, 20)));

		pOther = new JPanel();
		pOther.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		pOther.setLayout(new BoxLayout(pOther, BoxLayout.Y_AXIS));
		pMain.add(pOther);
		lOther = new JLabel("Other...");
		lOther.setHorizontalAlignment(JLabel.LEFT);
		lOther.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		pOther.add(lOther);
		pOtherButtons = new JPanel();
		pOtherButtons.setLayout(new BoxLayout(pOtherButtons, BoxLayout.X_AXIS));
		pOther.add(pOtherButtons);
		bSpecNode = new JCheckBox("Show speciations on node", vis.getSpecNodeVisible());
		bSpecNode.addItemListener(this);
		pOtherButtons.add(bSpecNode);
		pOtherButtons.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		bDuplications = new JCheckBox("Show duplications", vis.getDuplicationsVisible());
		bDuplications.addItemListener(this);
		pOtherButtons.add(bDuplications);
		bLosses = new JCheckBox("Show losses", vis.getLossesVisible());
		bLosses.addItemListener(this);
		pOtherButtons.add(bLosses);
		
		
		pMain.add(Box.createRigidArea(new Dimension(1, 20)));

		
		
		bClose = new JButton("Close");
		bClose.addActionListener(this);
		bClose.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		pMain.add(bClose);
		
		pMain.add(Box.createRigidArea(new Dimension(1, 10)));

		
		
		
		setLocationRelativeTo(parent);
		setModal(true);
		setTitle("view");
		pack();
		setVisible(true);
		
		
		
	}


	@Override
	public void actionPerformed(ActionEvent event) {
		if(event.getSource() == bClose) {
			setVisible(false);
		}
		
	}


	@Override
	public void itemStateChanged(ItemEvent event) {
		
		if(event.getSource() == bSpecBranchSource) {
			ParamManager.showSpecBranchSource = bSpecBranchSource.isSelected();
			vis.setSpecBranchSourceVisible(bSpecBranchSource.isSelected());		
		}
		else if(event.getSource() == bSpecBranchLin) {
			ParamManager.showSpecBranchLin = bSpecBranchLin.isSelected();
			vis.setSpecBranchLinVisible(bSpecBranchLin.isSelected());		
		}
		else if(event.getSource() == bSpecBranchReceiving) {
			ParamManager.showSpecBranchReceiving = bSpecBranchReceiving.isSelected();
			vis.setSpecBranchReceivingVisible(bSpecBranchReceiving.isSelected());	
		}
		
		else if(event.getSource() == bTransferSource) {
			ParamManager.showTransferSource = bTransferSource.isSelected();
			vis.setTransferSourceVisible(bTransferSource.isSelected());	
		}
		else if(event.getSource() == bTransferLin) {
			vis.setTransferLinVisible(bTransferLin.isSelected());		
			ParamManager.showTransferLin = bTransferLin.isSelected();
		}
		else if(event.getSource() == bTransferReceiving) {
			ParamManager.showTransferReceiving = bTransferReceiving.isSelected();
			vis.setTransferReceivingVisible(bTransferReceiving.isSelected());
		}
		
		else if(event.getSource() == bSpecNode) {
			ParamManager.showSpecNode = bSpecNode.isSelected();
			vis.setSpecNodeVisible(bSpecNode.isSelected());	
		}
		else if(event.getSource() == bDuplications) {
			ParamManager.showDuplications = bDuplications.isSelected();
			vis.setDuplicationsVisible(bDuplications.isSelected());		
		}
		else if(event.getSource() == bLosses) {
			ParamManager.showLosses = bLosses.isSelected();
			vis.setLossesVisible(bLosses.isSelected());	
		}
		
		caption.repaint();
		ParamManager.saveParam();
	}
	
	
	
	
	
}
