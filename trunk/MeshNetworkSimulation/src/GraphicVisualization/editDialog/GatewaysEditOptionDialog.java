package GraphicVisualization.editDialog;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import GraphicVisualization.GraphViewer;
import GraphicVisualization.StartOptionsDialog;
import net.miginfocom.swing.MigLayout;

public class GatewaysEditOptionDialog extends AbstractEditOptionDialog {

	private static final long serialVersionUID = 6703449833417760269L;
	
	private JPanel centerPanel;
	
	private JPanel tablePanel;
	
	private JPanel filePanel;
	
	private JSpinner spinner;
	
	private JComboBox<String> comboBox;
	
	private JTextField pathTextField;

	public GatewaysEditOptionDialog(StartOptionsDialog parent) {
		super(parent, "Gateways edit");
		
		JPanel northPanel = new JPanel();
		getContentPane().add(northPanel, BorderLayout.NORTH);
		northPanel.setLayout(new MigLayout("", "[][grow]25[][grow]", "[]"));

		JLabel lblNumberOfRadios = new JLabel("Number of radios:");
		northPanel.add(lblNumberOfRadios, "cell 0 0");

		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		northPanel.add(spinner, "cell 1 0,grow");

		JLabel lblTypeOfGeneration = new JLabel("Type of generation:");
		northPanel.add(lblTypeOfGeneration, "cell 2 0,alignx trailing");

		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"File", "Static"}));
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					setCenterPanel(comboBox.getSelectedIndex() == 0);
				}
			}
		});
		northPanel.add(comboBox, "cell 3 0,growx");

		filePanel = new JPanel();
		filePanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
		pathTextField = new JTextField();
		pathTextField.setEditable(false);
		filePanel.add(pathTextField, "cell 0 0,grow");
		JButton button = new JButton("...");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StartOptionsDialog.showFileChooser(pathTextField, "gateways", false,45);
			}
		});
		filePanel.add(button, "cell 1 0,alignx center,aligny center");

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
				this.spinner.setValue(options.get(0));
				this.comboBox.setSelectedIndex((int) options.get(1));
				setCenterPanel(this.comboBox.getSelectedIndex() == 0);
			} else {
				setDefault();
			}
		}
		pack();
	}
	
	@Override
	protected void setDefault() {
		setCenterPanel(false);
		if(this.table != null) {
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			while(model.getRowCount() > 0) {
				model.removeRow(0);
			}
			model.addRow(new Object[] {0, 100, 100});
			model.addRow(new Object[] {0, 500, 100});
			model.addRow(new Object[] {0, 100, 500});
			model.addRow(new Object[] {0, 500, 500});
		}
		spinner.setValue(4);
		comboBox.setSelectedIndex(1);
	}

	@Override
	protected String getIdentifier() {
		return "gateways";
	}

	@Override
	protected void collectResults() {
		this.results.clear();
		this.results.put("radio", this.spinner.getValue());
		this.results.put("generation", this.comboBox.getSelectedItem());
		if(comboBox.getSelectedIndex() == 0) {
			this.results.put("file", pathTextField.getToolTipText());
		} else {
			int[][] gateways = new int[this.table.getRowCount()][this.table.getColumnCount()-1];
			int i;
			for(i = 0; i < this.table.getRowCount(); i++) {
				for(int j = 1; j < this.table.getColumnCount(); j++) {
					gateways[i][j-1] = (int) table.getValueAt(i, j);
				}
			}
			this.results.put("nbOfGateways", i);
			this.results.put("gateways", gateways);
		}
	}

	private void setCenterPanel(boolean file) {
		if(centerPanel != null) {
			this.getContentPane().remove(centerPanel);
		}
		centerPanel = (file) ? filePanel : tablePanel;
		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
		this.pack();
	}

	
	@Override
	protected boolean areOptionsValid() {
		if(comboBox.getSelectedIndex() == 0) {
			if((pathTextField != null) && (pathTextField.getToolTipText() != null)) {
				return true;
			} else {
				GraphViewer.showErrorDialog("No file chosen",
						"Choose a file for the gateways.");
			}
		} else {
			return super.validateTable(true);
		}
		return false;
	}

	@Override
	protected void saveValidOptions() {
		options.clear();
		options.add(spinner.getValue());
		options.add(comboBox.getSelectedIndex());
	}
	
}
