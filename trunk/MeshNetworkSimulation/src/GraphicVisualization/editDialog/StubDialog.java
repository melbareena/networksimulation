package GraphicVisualization.editDialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Window.Type;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

public class StubDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			StubDialog dialog = new StubDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public StubDialog() {
		setResizable(false);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel northPanel = new JPanel();
			contentPanel.add(northPanel, BorderLayout.NORTH);
			northPanel.setLayout(new MigLayout("", "[][grow]25[][grow]", "[]"));
			{
				JLabel lblNumberOfRadios = new JLabel("Number of radios:");
				northPanel.add(lblNumberOfRadios, "cell 0 0");
			}
			{
				JSpinner spinner = new JSpinner();
				spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				northPanel.add(spinner, "cell 1 0,grow");
			}
			{
				JLabel lblTypeOfGeneration = new JLabel("Type of generation:");
				northPanel.add(lblTypeOfGeneration, "cell 2 0,alignx trailing");
			}
			{
				JComboBox comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(new String[] {"File", "Static"}));
				comboBox.setSelectedIndex(0);
				northPanel.add(comboBox, "cell 3 0,growx");
			}
		}
		{
			JPanel centerPanel = new JPanel();
			contentPanel.add(centerPanel, BorderLayout.CENTER);
			centerPanel.setLayout(new BorderLayout(0, 0));
			{
				JScrollPane scrollPane = new JScrollPane();
				centerPanel.add(scrollPane, BorderLayout.CENTER);

				table = new JTable();
				table.setModel(new DefaultTableModel(
					new Object[][] {
						{null, null, null},
					},
					new String[] {
						"#", "X", "Y"
					}
				) {
					Class[] columnTypes = new Class[] {
						Integer.class, Integer.class, Integer.class
					};
					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});
				scrollPane.setViewportView(table);
				{
					JLabel label = new JLabel("New label");
					scrollPane.setRowHeaderView(label);
				}
			}
			{
				JPanel panel = new JPanel();
				centerPanel.add(panel, BorderLayout.SOUTH);
				panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
				{
					JButton btnAddRow = new JButton("Add row");
					panel.add(btnAddRow);
				}
				{
					JButton btnRemoveRow = new JButton("Remove row");
					panel.add(btnRemoveRow);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
