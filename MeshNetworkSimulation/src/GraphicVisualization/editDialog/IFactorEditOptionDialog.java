package GraphicVisualization.editDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import GraphicVisualization.StartOptionsDialog;

public class IFactorEditOptionDialog extends AbstractEditOptionDialog {

	private static final long serialVersionUID = 3287062923315713899L;

	private JPanel centerPanel;

	public IFactorEditOptionDialog(StartOptionsDialog parent) {
		super(parent, "IFactor edit");

		/*-------------*/
		/* TABLE PANEL */
		/*-------------*/
		
		Vector<String> columnHeaders = new Vector<String>();
		columnHeaders.add("Channel distance");
		columnHeaders.add("IFactor");
		
		final Vector<Class<?>> columnTypes = new Vector<Class<?>>();
		columnTypes.add(Integer.class);
		columnTypes.add(Double.class);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(0, 0));
		this.getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		centerPanel.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setModel(new DefaultTableModel(new Vector<Object>(), columnHeaders) {
			private static final long serialVersionUID = 1L;

			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes.get(columnIndex);
			}
			
			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);
			}
		});
		table.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6231645862177354908L;
			public Component getTableCellRendererComponent(JTable table,
		            Object value, boolean isSelected, boolean hasFocus,
		            int row, int column) {
		        if (table != null) {
		            JTableHeader header = table.getTableHeader();
		            if (header != null) {
		                setForeground(header.getForeground());
		                setBackground(header.getBackground());
		                setFont(header.getFont());
		                setHorizontalAlignment(TRAILING);
		            }
		        }
		        if (isSelected) {
		            setFont(getFont().deriveFont(Font.BOLD));
		        }
		        setValue(row+"   ");
		        return this;
		    }
		});
		table.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 6231645862177354908L;
			@Override
			public void setValue(Object value) {
		        this.setText(((value == null) ? "" : value+""));
		    }
		});
		
		scrollPane.setViewportView(table);
		
		setDefault();
		
		collectResults();
	
		setSize(200, 300);
		
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
				for(int i = 0; i < options.size(); i++) {
					model.addRow(new Object[] {0, options.get(i)});
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
		model.addRow(new Object[] {0, 1.0});
		model.addRow(new Object[] {0, 0.7272});
		model.addRow(new Object[] {0, 0.2714});
		model.addRow(new Object[] {0, 0.0054});
		model.addRow(new Object[] {0, 0.0008});
		model.addRow(new Object[] {0, 0.0002});
		model.addRow(new Object[] {0, 0.0});
		model.addRow(new Object[] {0, 0.0});
		model.addRow(new Object[] {0, 0.0});
		model.addRow(new Object[] {0, 0.0});
		model.addRow(new Object[] {0, 0.0});
	}

	@Override
	protected String getIdentifier() {
		return "ifactor";
	}

	@Override
	protected void collectResults() {
		this.results.clear();
		double[] ifactor = new double[this.table.getRowCount()];
		int i;
		for(i = 0; i < this.table.getRowCount(); i++) {
			ifactor[i] = (double) table.getValueAt(i, 1);
		}
		this.results.put("ifactor", ifactor);
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
	}


}
