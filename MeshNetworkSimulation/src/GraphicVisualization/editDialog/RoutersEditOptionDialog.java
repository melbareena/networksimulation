package GraphicVisualization.editDialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import GraphicVisualization.GraphViewer;
import GraphicVisualization.StartOptionsDialog;

public class RoutersEditOptionDialog extends AbstractEditOptionDialog {

	private static final long serialVersionUID = 3287062923315713899L;

	private JPanel centerPanel;
	
	private JPanel tablePanel;
	
	private JPanel randomPanel;
	
	private JPanel filePanel;
	
	private JSpinner nbOfChannelsSpinner;
	
	private JComboBox<String> typeGenerationComboBox;
	
	private JSpinner minDistanceSpinner;
	
	private JSpinner transmissionRateSpinner;
	
	private JSpinner numberOfRoutersSpinner;
	
	private JSpinner seedSpinner;
	
	private JSpinner safetyTestSpinner;
	
	private JTextField pathTextField;

	public RoutersEditOptionDialog(StartOptionsDialog parent) {
		super(parent, "Routers edit");
		
		/*-------------*/
		/* NORTH PANEL */
		/*-------------*/
		
		JPanel northPanel = new JPanel();
		getContentPane().add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new MigLayout("", "[][grow]25[][grow]", "[]10[]"));

		JLabel lblNumberOfRadios = new JLabel("Number of radios:");
		northPanel.add(lblNumberOfRadios, "cell 0 0");

		nbOfChannelsSpinner = new JSpinner();
		nbOfChannelsSpinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		northPanel.add(nbOfChannelsSpinner, "cell 1 0,grow");

		JLabel lblTypeOfGeneration = new JLabel("Type of generation:");
		northPanel.add(lblTypeOfGeneration, "cell 2 0,alignx trailing");

		typeGenerationComboBox = new JComboBox<String>();
		typeGenerationComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"File", "Static", "Random"}));
		typeGenerationComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					setCenterPanel();
				}
			}
		});
		northPanel.add(typeGenerationComboBox, "cell 3 0,growx");
		
		JLabel lblMinDistance = new JLabel("Min distance:");
		lblMinDistance.setHorizontalAlignment(SwingConstants.TRAILING);
		northPanel.add(lblMinDistance, "cell 0 1,grow");

		minDistanceSpinner = new JSpinner();
		minDistanceSpinner.setModel(new SpinnerNumberModel(new Integer(50), null, null, new Integer(1)));
		northPanel.add(minDistanceSpinner, "cell 1 1,grow");
		
		JLabel lblTransmissionRate = new JLabel("Transmission rate:");
		lblTransmissionRate.setHorizontalAlignment(SwingConstants.TRAILING);
		northPanel.add(lblTransmissionRate, "cell 2 1,alignx trailing");

		transmissionRateSpinner = new JSpinner();
		transmissionRateSpinner.setModel(new SpinnerNumberModel(new Integer(200), null, null, new Integer(1)));
		northPanel.add(transmissionRateSpinner, "cell 3 1,grow");
		
		/*--------------*/
		/* RANDOM PANEL */
		/*--------------*/
		
		randomPanel = new JPanel();
		randomPanel.setLayout(new MigLayout("", "[min!]25[grow]", "[]15[]5[]15[]"));

		JLabel lblNumberOfRouters = new JLabel("Number of routers to generate:");
		lblNumberOfRouters.setHorizontalAlignment(SwingConstants.TRAILING);
		randomPanel.add(lblNumberOfRouters, "cell 0 0,grow");

		numberOfRoutersSpinner = new JSpinner();
		numberOfRoutersSpinner.setModel(new SpinnerNumberModel(new Integer(45), new Integer(1), null, new Integer(1)));
		randomPanel.add(numberOfRoutersSpinner, "cell 1 0,growx");

		JLabel lblSeedforRandom = new JLabel("Seed (for random generator):");
		lblSeedforRandom.setHorizontalAlignment(SwingConstants.TRAILING);
		randomPanel.add(lblSeedforRandom, "cell 0 1,growx");

		seedSpinner = new JSpinner();
		seedSpinner.setModel(new SpinnerNumberModel(Math.abs(new Random().nextLong()), null, null, new Long(1)));
		randomPanel.add(seedSpinner, "cell 1 1,grow");

		JButton btnGenerateRandomSeed = new JButton("Generate random seed");
		btnGenerateRandomSeed.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				seedSpinner.setValue(Math.abs(new Random().nextLong()));
				pack();
			}
		});
		randomPanel.add(btnGenerateRandomSeed, "cell 1 2,alignx trailing");

		JLabel lblSafetyTest = new JLabel("Safety test:");
		lblSafetyTest.setHorizontalAlignment(SwingConstants.TRAILING);
		randomPanel.add(lblSafetyTest, "cell 0 3,growx,aligny center");
		
		safetyTestSpinner = new JSpinner();
		safetyTestSpinner.setModel(new SpinnerNumberModel(new Integer(500), null, null, new Integer(1)));
		randomPanel.add(safetyTestSpinner, "cell 1 3,grow");		
		
		/*------------*/
		/* FILE PANEL */
		/*------------*/
		
		filePanel = new JPanel();
		filePanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		filePanel.add(pathTextField, "cell 0 0,grow");
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StartOptionsDialog.showFileChooser(pathTextField, "routers", false, 45);
			}
		});
		filePanel.add(button, "cell 1 0,alignx center,aligny center");

		/*-------------*/
		/* TABLE PANEL */
		/*-------------*/
		
		Vector<String> columnHeaders = new Vector<String>();
		columnHeaders.add("#");
		columnHeaders.add("X");
		columnHeaders.add("Y");
		
		Vector<Class<?>> columnTypes = new Vector<Class<?>>();
		columnTypes.add(Integer.class);
		columnTypes.add(Integer.class);
		columnTypes.add(Integer.class);
		
		tablePanel = super.buildTablePanel(columnHeaders, columnTypes);
		
		setDefault();
		
		collectResults();
	
		pack();
		
		this.setLocationRelativeTo(null);

	}

	@Override 
	public void showDialog(boolean restore) {
		setVisible(true);
		if(restore) {
			if(this.options.size() > 0) {
				this.nbOfChannelsSpinner.setValue(options.get(0));
				this.typeGenerationComboBox.setSelectedIndex((int) options.get(1));
				this.minDistanceSpinner.setValue(options.get(2));
				this.transmissionRateSpinner.setValue(options.get(3));
				setCenterPanel();
			} else {
				setDefault();
			}
		}
		pack();
	}
	
	@Override
	protected void setDefault() {
		nbOfChannelsSpinner.setValue(2);
		typeGenerationComboBox.setSelectedIndex(0);
		minDistanceSpinner.setValue(50);
		transmissionRateSpinner.setValue(200);
		pathTextField.setText("/setting/input/routers.txt");
		pathTextField.setToolTipText("/setting/input/routers.txt");
		setCenterPanel();
	}

	@Override
	protected String getIdentifier() {
		return "routers";
	}

	@Override
	protected void collectResults() {
		this.results.clear();
		this.results.put("radio", this.nbOfChannelsSpinner.getValue());
		this.results.put("generation", this.typeGenerationComboBox.getSelectedItem());
		this.results.put("minDistance", this.minDistanceSpinner.getValue());
		this.results.put("transmissionRate", this.transmissionRateSpinner.getValue());
		switch(typeGenerationComboBox.getSelectedIndex()) {
		case 0:
			this.results.put("file", pathTextField.getToolTipText());
			break;
		case 1:
			int[][] routers = new int[this.table.getRowCount()][this.table.getColumnCount()-1];
			int i;
			for(i = 0; i < this.table.getRowCount(); i++) {
				for(int j = 1; j < this.table.getColumnCount(); j++) {
					routers[i][j-1] = (int) table.getValueAt(i, j);
				}
			}
			this.results.put("nbOfRouters", i);
			this.results.put("routers", routers);
			break;
		case 2:
			this.results.put("nbOfRouters", numberOfRoutersSpinner.getValue());
			this.results.put("seed", seedSpinner.getValue());
			this.results.put("safetyTest", safetyTestSpinner.getValue());
			break;
		}
	}

	private void setCenterPanel() {
		if(centerPanel != null) {
			this.getContentPane().remove(centerPanel);
		}
		switch(this.typeGenerationComboBox.getSelectedIndex()) {
		case 0:
			centerPanel = filePanel;
			break;
		case 1:
			centerPanel = tablePanel;
			break;
		case 2:
			centerPanel = randomPanel;
			break;
		}
		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
		this.pack();
	}
	
	@Override
	protected boolean areOptionsValid() {
		switch(typeGenerationComboBox.getSelectedIndex()) {
		case 0:
			if((pathTextField != null) && (pathTextField.getToolTipText() != null)) {
				return true;
			} else {
				GraphViewer.showErrorDialog("No file chosen",
						"Choose a file for the routers.");
			}
			break;
		case 1:
			return super.validateTable(true);
		case 2:
			return true;
		}
		return false;
	}

	@Override
	protected void saveValidOptions() {
		options.clear();
		options.add(nbOfChannelsSpinner.getValue());
		options.add(typeGenerationComboBox.getSelectedIndex());
		options.add(minDistanceSpinner.getValue());
		options.add(transmissionRateSpinner.getValue());
	}

}
