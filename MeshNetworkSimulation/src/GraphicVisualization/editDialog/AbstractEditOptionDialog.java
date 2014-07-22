package GraphicVisualization.editDialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import GraphicVisualization.GraphViewer;
import GraphicVisualization.StartOptionsDialog;

@SuppressWarnings("unused")
public abstract class AbstractEditOptionDialog extends JDialog {
	
	private static final long serialVersionUID = -6656614289121355978L;
	
	protected HashMap<String,Object> results;
	
	protected Dialog parent;
	
	protected JTable table;
	
	protected ArrayList<Object> options;

	public AbstractEditOptionDialog(StartOptionsDialog parent, String title) {
		super(parent, title, true);
		this.parent = parent;
		this.results = new HashMap<String, Object>();
		this.options = new ArrayList<Object>();

		this.setResizable(false);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		getContentPane().setLayout(new BorderLayout());
				
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(areOptionsValid()) {
					saveValidOptions();
					collectResults();
					parent.updateOnOptionDialogCompleted(getIdentifier());
					dispose();
				}
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDefault();
			}
		});
		buttonPane.add(resetButton);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttonPane.add(cancelButton);
	}
	
	public abstract void showDialog(boolean reset);
	
	protected abstract String getIdentifier();
	
	protected abstract void collectResults();
	
	protected abstract void setDefault();
	
	protected abstract boolean areOptionsValid();
	
	protected abstract void saveValidOptions();
	
	public HashMap<String, Object> getResults() {
		return results;
	}
	
	protected JPanel buildTablePanel(Vector<String> columnHeaders, Vector<Class<?>> columnTypes) {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(0, 0));
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
		            }
		        }
		        if (isSelected) {
		            setFont(getFont().deriveFont(Font.BOLD));
		        }
		        setValue(row+1);
		        return this;
		    }
			
		});
		
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		centerPanel.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JButton btnAddRow = new JButton("Add row");
		btnAddRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((DefaultTableModel)table.getModel()).addRow(new Vector<Object>());
			}
		});
		panel.add(btnAddRow);

		JButton btnRemoveRow = new JButton("Remove row");
		btnRemoveRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if(selectedRow != -1) {
					((DefaultTableModel)table.getModel()).removeRow(selectedRow);
				}
			}
		});
		btnRemoveRow.setEnabled(false);
		panel.add(btnRemoveRow);
		
		((DefaultTableModel)table.getModel()).addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				btnRemoveRow.setEnabled((table.getRowCount() > 0) && !table.getSelectionModel().isSelectionEmpty());
			}
		});
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				btnRemoveRow.setEnabled((table.getRowCount() > 0) && !table.getSelectionModel().isSelectionEmpty());
			}
		});

		return centerPanel;
	}
	
	protected boolean validateTable(boolean showErrorMessage) {
		if(this.table != null) {
			if(this.table.getRowCount() < 1) {
				if(showErrorMessage) {
					GraphViewer.showErrorDialog("No row in table",
							"The table doesn't contain any row.\n"
							+ "Add at least one row.");
					return false;
				}
			} else {
				for(int i = 0; i < this.table.getRowCount(); i++) {
					for(int j = 1; j < this.table.getColumnCount(); j++) {
						if(table.getValueAt(i, j) == null) {
							if(showErrorMessage) {
								GraphViewer.showErrorDialog("Empty cell in table",
										"The cell "+table.getColumnName(j)+" of row #"+(i+1)+" is empty.\n"
										+ "Please fill it or delete the row.");
							}
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
}
