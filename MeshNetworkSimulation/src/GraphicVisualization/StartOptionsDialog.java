package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import launcher.Program;
import transConf.TCFacade;

public class StartOptionsDialog extends JDialog
{

	private static final long	serialVersionUID	= -8488459792255899186L;
	
	private final JPanel	contentPanel	= new JPanel();
	
	private final JPanel chckbxPanel = new JPanel();
	
	private final JSpinner spinnerRatio = new JSpinner();
	
	private final JCheckBox chckbxAlternateOrder = new JCheckBox("Alternate order");
	
	private final JCheckBox chckbxRepeatLinksTo = new JCheckBox("Repeat links to ensure ratio");
	
	private final JCheckBox chckbxEnlargeByGateways = new JCheckBox("Enlarge by gateways");
	
	private final JRadioButton rdbtnNewAlgorithm = new JRadioButton("New algorithm");

	/**
	 * Create the dialog.
	 */
	public StartOptionsDialog()
	{
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JPanel rdbtnPanel = new JPanel();
		contentPanel.add(rdbtnPanel);
		
		JRadioButton rdbtnOriginal = new JRadioButton("Original algorithm");
		rdbtnPanel.add(rdbtnOriginal);

		rdbtnPanel.add(rdbtnNewAlgorithm);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnNewAlgorithm);
		group.add(rdbtnOriginal);
		rdbtnNewAlgorithm.setSelected(true);
		
		JLabel spinnerLabel = new JLabel("Ratio Downlinks over Uplinks:");
		spinnerLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPanel.add(spinnerLabel);
		
		SpinnerNumberModel sModel = new SpinnerNumberModel(1, 1, 4, 1);
		spinnerRatio.setModel(sModel);
		spinnerRatio.setAlignmentX(CENTER_ALIGNMENT);
		JPanel spinnerPanel = new JPanel();
		spinnerPanel.setAlignmentX(CENTER_ALIGNMENT);
		spinnerPanel.setLayout(new BoxLayout(spinnerPanel, BoxLayout.X_AXIS));
		spinnerPanel.add(Box.createHorizontalGlue());
		spinnerPanel.add(spinnerRatio);
		spinnerPanel.add(Box.createHorizontalGlue());
		contentPanel.add(spinnerPanel);

		contentPanel.add(chckbxPanel);

		chckbxPanel.add(chckbxAlternateOrder);

		chckbxPanel.add(chckbxRepeatLinksTo);

		chckbxPanel.add(chckbxEnlargeByGateways);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TCFacade.newAlgortihm = rdbtnNewAlgorithm.isSelected();
				TCFacade.downOverUpRatio = (int) spinnerRatio.getValue();
				TCFacade.alternateOrder = chckbxAlternateOrder.isSelected();
				TCFacade.repeatLinksToRespectRatio = chckbxRepeatLinksTo.isSelected();
				TCFacade.enlargeByGateways = chckbxEnlargeByGateways.isSelected();
				dispose();
				Program.launch();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		buttonPane.add(cancelButton);
		
		rdbtnNewAlgorithm.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chckbxPanel.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				spinnerRatio.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				chckbxAlternateOrder.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				chckbxRepeatLinksTo.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				chckbxEnlargeByGateways.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		
		pack();

	}

}
