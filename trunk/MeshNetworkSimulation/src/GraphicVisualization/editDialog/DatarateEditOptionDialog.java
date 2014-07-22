package GraphicVisualization.editDialog;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import GraphicVisualization.StartOptionsDialog;

public class DatarateEditOptionDialog extends AbstractEditOptionDialog {

	private static final long serialVersionUID = -8312031086549427054L;
	
	private JPanel centerPanel;

	public DatarateEditOptionDialog(StartOptionsDialog parent) {
		super(parent, "Datarate edit");

		/*-------------*/
		/* TABLE PANEL */
		/*-------------*/
		
		Vector<String> columnHeaders = new Vector<String>();
		columnHeaders.add("#");
		columnHeaders.add("SINR");
		columnHeaders.add("Datarate");
		
		Vector<Class<?>> columnTypes = new Vector<Class<?>>();
		columnTypes.add(Integer.class);
		columnTypes.add(Double.class);
		columnTypes.add(Integer.class);
		
		centerPanel = super.buildTablePanel(columnHeaders, columnTypes);
		this.getContentPane().add(centerPanel, BorderLayout.CENTER);

		setDefault();
		
		collectResults();
	
		pack();
		
		this.setLocationRelativeTo(null);

	}

	@Override 
	public void showDialog(boolean restore) {
		setVisible(true);
		if(restore) {
			if(this.options.size() == 0) {
				setDefault();
			} else {
				DefaultTableModel model = (DefaultTableModel)table.getModel();
				while(model.getRowCount() > 0) {
					model.removeRow(0);
				}
				for(int i = 0; i < (options.size()/2); i++) {
					model.addRow(new Object[] {0, options.get(i), options.get(i+(options.size()/2))});
				}
			}
		}
	}
	
	@Override
	protected void setDefault() {
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		while(model.getRowCount() > 0) {
			model.removeRow(0);
		}
		model.addRow(new Object[] {0, 8.15, 6});
		model.addRow(new Object[] {0, 10.71, 9});
		model.addRow(new Object[] {0, 13.48, 12});
		model.addRow(new Object[] {0, 21.37, 18});
		model.addRow(new Object[] {0, 50.70, 24});
		model.addRow(new Object[] {0, 134.89, 36});
		model.addRow(new Object[] {0, 269.15, 48});
		model.addRow(new Object[] {0, 429.57, 54});
	}

	@Override
	protected String getIdentifier() {
		return "datarate";
	}

	@Override
	protected void collectResults() {
		this.results.clear();
		HashMap<Double, Integer> datarates = new HashMap<Double, Integer>();
		int i;
		for(i = 0; i < this.table.getRowCount(); i++) {
			datarates.put((double)table.getValueAt(i, 1), (int)table.getValueAt(i, 2)) ;
		}
		this.results.put("nbOfDatarates", i);
		this.results.put("datarate", datarates);
	}
	
	@Override
	protected boolean areOptionsValid() {
		return super.validateTable(true);
	}

	@Override
	protected void saveValidOptions() {
		options.clear();
		for(int i = 0; i < this.table.getRowCount(); i++) {
			options.add((double) table.getValueAt(i, 1));
		}
		for(int i = 0; i < this.table.getRowCount(); i++) {
			options.add((int) table.getValueAt(i, 2));
		}
		
	}

}
