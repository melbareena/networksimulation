package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import launcher.Program;
import net.miginfocom.swing.MigLayout;
import transConf.TCFacade;

import javax.swing.JTextField;

import java.awt.Font;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings({ "rawtypes", "unchecked" })
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
	private final JLabel lblAlgorithm = new JLabel("Algorithm:");
	private final JLabel lblEnvironment = new JLabel("Environment:");
	private final JPanel environmentPanel = new JPanel();
	private final JSpinner spinner = new JSpinner();
	private final JSpinner spinner_1 = new JSpinner();
	private final JLabel lblX = new JLabel(" X: ");
	private final JLabel lblY = new JLabel(" Y: ");
	private final JLabel lblNewLabel = new JLabel("Other parameters:");
	private final JPanel ratioLabelsPanel = new JPanel();
	private final JLabel lblOverUplinks = new JLabel("over Uplinks:");
	private final JPanel panel = new JPanel();
	private final JLabel lblOutput = new JLabel("Output:");
	private final JButton btnOutput = new JButton("...");
	private final JPanel outputPanel = new JPanel();
	private final JTextField outputFolderPathTextField = new JTextField();
	private final JCheckBox chckbxGenerateFiles = new JCheckBox("Generate files");
	private final JLabel lblTraffic = new JLabel("Traffic:");
	private final JPanel trafficPanel = new JPanel();
	private final JLabel lblGenerator = new JLabel("Generator:");
	private final JComboBox comboBox = new JComboBox();
	private final JTextField upTrafficTextField = new JTextField();
	private final JButton btnUpTraffic = new JButton("...");
	private final JLabel lblU = new JLabel("U:");
	private final JButton btnDownTraffic = new JButton("...");
	private final JTextField downTrafficTextField = new JTextField();
	private final JLabel lblD = new JLabel("D:");
	private final JLabel lblSetDefaults = new JLabel("Set defaults...");

	/**
	 * Create the dialog.
	 */
	public StartOptionsDialog() {
		downTrafficTextField.setText(".../trafficDown.txt");
		downTrafficTextField.setToolTipText("/setting/input/trafficDown.txt");
		downTrafficTextField.setEditable(false);
		downTrafficTextField.setColumns(10);
		upTrafficTextField.setText("...ut/trafficUp.txt");
		upTrafficTextField.setToolTipText("/setting/input/trafficUp.txt");
		upTrafficTextField.setEditable(false);
		upTrafficTextField.setColumns(10);
		outputFolderPathTextField.setFont(new Font("Tahoma", Font.ITALIC, 11));
		outputFolderPathTextField.setText("Output folder...");
		outputFolderPathTextField.setEditable(false);
		outputFolderPathTextField.setColumns(10);	
		setIconImage(Toolkit.getDefaultToolkit().getImage(StartOptionsDialog.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
		setTitle("Parameters");
		setResizable(false);
		setBounds(100, 100, 450, 300);
		BorderLayout borderLayout = new BorderLayout();
		getContentPane().setLayout(borderLayout);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[95px]10[157px,grow]", "[min!]10[min!]20[grow]10[min!]10[min!]"));
		lblEnvironment.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblEnvironment, "cell 0 0,grow");
		
		contentPanel.add(environmentPanel, "cell 1 0,alignx left,aligny center");
		environmentPanel.setLayout(new MigLayout("insets 0 0 0 0", "[min!][150px]", "[25px][25px]"));
		environmentPanel.add(lblX);
		
		spinner.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		environmentPanel.add(spinner, "wrap,grow");
		environmentPanel.add(lblY);
		spinner_1.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		environmentPanel.add(spinner_1, "grow");
		
		contentPanel.add(lblOutput, "cell 0 1,alignx trailing,aligny center");
		
		contentPanel.add(outputPanel, "cell 1 1,grow");
		outputPanel.setLayout(new MigLayout("insets 0 0 0 0", "[5][150px][min!]", "[][23px]"));
		chckbxGenerateFiles.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				btnOutput.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				outputFolderPathTextField.setText("Output folder...");
				outputFolderPathTextField.setToolTipText("");
			}
		});
		
		outputPanel.add(chckbxGenerateFiles, "cell 0 0 3 1,grow");
		
		outputPanel.add(outputFolderPathTextField, "cell 1 1,grow");
		btnOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showFileChooser(outputFolderPathTextField, "output", true);
			}
		});
		btnOutput.setEnabled(false);
		outputPanel.add(btnOutput, "cell 2 1,growx,aligny top");
		lblTraffic.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblTraffic, "cell 0 2,grow");
		
		contentPanel.add(trafficPanel, "cell 1 2,grow");
		trafficPanel.setLayout(new MigLayout("insets 5 3 0 0", "[][grow][min!][grow][min!]", "[grow][grow][grow][][grow]"));
		
		trafficPanel.add(lblGenerator, "cell 0 0 2 1,grow");
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				boolean select = ((String)comboBox.getSelectedItem()).equals("File");
				btnDownTraffic.setEnabled(select);
				btnUpTraffic.setEnabled(select);
				lblSetDefaults.setEnabled(select);
			}
		});
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"File", "Random"}));
		comboBox.setSelectedIndex(0);
		
		trafficPanel.add(comboBox, "cell 3 0 3 1,grow");
		
		trafficPanel.add(lblU, "cell 0 1,alignx trailing");
		
		trafficPanel.add(upTrafficTextField, "cell 1 1 3 1,growx");
		btnUpTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showFileChooser(upTrafficTextField, "uplink traffic", false);
			}
		});
		
		trafficPanel.add(btnUpTraffic, "cell 4 1,growx");
		
		trafficPanel.add(lblD, "cell 0 2,alignx trailing");
		
		trafficPanel.add(downTrafficTextField, "cell 1 2 3 1,growx");
		
		trafficPanel.add(btnDownTraffic, "cell 4 2");
		btnDownTraffic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				showFileChooser(downTrafficTextField, "downlink traffic", false);
			}
		});
		
		lblSetDefaults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(lblSetDefaults.isEnabled()) {
					upTrafficTextField.setText("...ut/trafficUp.txt");
					upTrafficTextField.setToolTipText("/setting/input/trafficUp.txt");
					downTrafficTextField.setText(".../trafficDown.txt");
					downTrafficTextField.setToolTipText("/setting/input/trafficDown.txt");
				}
			}
		});
		lblSetDefaults.setCursor(new Cursor(java.awt.Cursor.HAND_CURSOR));
		lblSetDefaults.setForeground(new Color(0, 0, 255));
		lblSetDefaults.setFont(new Font("Tahoma", Font.ITALIC, 11));
		
		trafficPanel.add(lblSetDefaults, "cell 3 3 2 1,alignx right");
		
		lblAlgorithm.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblAlgorithm, "cell 0 3,grow");

		JPanel rdbtnPanel = new JPanel();
		contentPanel.add(rdbtnPanel, "cell 1 3,alignx left,growy");
		rdbtnPanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		JRadioButton rdbtnOriginal = new JRadioButton("Original algorithm");
		rdbtnPanel.add(rdbtnOriginal);

		rdbtnPanel.add(rdbtnNewAlgorithm);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnNewAlgorithm);
		group.add(rdbtnOriginal);
		rdbtnNewAlgorithm.setSelected(true);
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "New algorithm parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		contentPanel.add(panel, "cell 0 4 2 1,alignx center,aligny center");
		panel.setLayout(new MigLayout("insets 5 0 0 0", "[min!]10[157px]", "[83px][83px]"));
		panel.add(ratioLabelsPanel, "cell 0 0,alignx trailing,aligny center");
		ratioLabelsPanel.setLayout(new MigLayout("insets 0 0 0 0", "[157px]", "[min!][min!]"));
		
		JLabel spinnerLabel = new JLabel("Ratio Downlinks");
		ratioLabelsPanel.add(spinnerLabel, "cell 0 0,alignx right,growy");
		spinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerLabel.setAlignmentX(CENTER_ALIGNMENT);
		lblOverUplinks.setHorizontalAlignment(SwingConstants.CENTER);
		
		ratioLabelsPanel.add(lblOverUplinks, "cell 0 1,alignx right,growy");
		
		spinnerRatio.setModel(new SpinnerNumberModel(1, 1, 4, 1));
		JPanel spinnerPanel = new JPanel();
		panel.add(spinnerPanel, "cell 1 0,growx,aligny center");
		spinnerPanel.setAlignmentX(CENTER_ALIGNMENT);
		spinnerPanel.setLayout(new MigLayout("insets 0 5 0 0", "[157px]", "[27px]"));
		spinnerPanel.add(spinnerRatio, "cell 0 0,grow");
		panel.add(lblNewLabel, "cell 0 1,alignx right,aligny center");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(chckbxPanel, "cell 1 1,grow");
		chckbxPanel.setLayout(new GridLayout(3, 0, 0, 0));

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
	
	private void showFileChooser(JTextField source, String title, boolean folder) {
		final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setFileSelectionMode(((folder) ? JFileChooser.DIRECTORIES_ONLY :JFileChooser.FILES_ONLY));
		fileChooser.setDialogTitle("Select the "+((folder) ? "folder" : "file")+" for the "+title);
		fileChooser.setMultiSelectionEnabled(false);
		int retVal = fileChooser.showDialog(null, "Choose");
		if(retVal == JFileChooser.APPROVE_OPTION) {	
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			source.setText("..."+filePath.substring(filePath.length()-15, filePath.length()));
			source.setToolTipText(filePath);
		} else {
			/*TODO*/
		}
		
	}

}
