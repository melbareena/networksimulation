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
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 * @author Benjamin
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StartOptionsDialog extends JDialog {

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
	private final JPanel newAlgoPanel = new JPanel();
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
	private final JLabel lblGateways = new JLabel("Gateways:");
	private final JButton gatewaysButton = new JButton("Edit");
	private final JPanel gatewaysPanel = new JPanel();
	private final JTextField gatewaysTextField = new JTextField();
	private final JLabel lblRouters = new JLabel("Routers:");
	private final JPanel routersPanel = new JPanel();
	private final JButton routersButton = new JButton("Edit");
	private final JTextField routersTextField = new JTextField();
	private final JLabel lblChannels = new JLabel("Channels:");
	private final JLabel lblDatarates = new JLabel("Datarates:");
	private final JLabel lblIfactor = new JLabel("IFactor:");
	private final JLabel lblSinr = new JLabel("SINR:");
	private final JPanel IFactorPanel = new JPanel();
	private final JButton IFactorButton = new JButton("Edit");
	private final JTextField IFactorTextField = new JTextField();
	private final JPanel dataratesPanel = new JPanel();
	private final JButton dataratesButton = new JButton("Edit");
	private final JTextField dataratesTextField = new JTextField();
	private final JPanel SINRPanel = new JPanel();
	private final JButton SINRButton = new JButton("Edit");
	private final JTextField SINRTextField = new JTextField();
	private final JPanel channelsPanel = new JPanel();
	private final JLabel lblMode = new JLabel("Mode:");
	private final JComboBox channelModeComboBox = new JComboBox();
	private final JList channelList = new JList();
	private final JLabel lblChannels_1 = new JLabel("Channels:");
	private final JScrollPane channelScrollPane = new JScrollPane();
	private final JLabel lblSelected = new JLabel("Selected");
	private final JLabel lblStrategy = new JLabel("Strategy:");
	private final JComboBox comboBox_1 = new JComboBox();

	/**
	 * Create the dialog.
	 */
	public StartOptionsDialog() {
		SINRTextField.setEditable(false);
		SINRTextField.setColumns(10);
		dataratesTextField.setEditable(false);
		dataratesTextField.setColumns(10);
		routersTextField.setEditable(false);
		routersTextField.setColumns(10);
		gatewaysTextField.setEditable(false);
		gatewaysTextField.setColumns(10);
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
		contentPanel.setLayout(new MigLayout("", "[95px]10[157px,grow]10[95px]10[157px,grow]", "[min!]10[min!,grow]20[grow]10[grow]10[min!,grow]10[min!,grow]"));
		lblEnvironment.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblEnvironment, "cell 0 0,grow");
		
		contentPanel.add(environmentPanel, "cell 1 0,growx,aligny center");
		environmentPanel.setLayout(new MigLayout("insets 0 0 0 0", "[min!][150px]", "[25px][25px]"));
		environmentPanel.add(lblX);
		
		spinner.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		environmentPanel.add(spinner, "wrap,grow");
		environmentPanel.add(lblY);
		spinner_1.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		environmentPanel.add(spinner_1, "grow");
		lblGateways.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblGateways, "cell 2 0,grow");
		
		contentPanel.add(gatewaysPanel, "cell 3 0,growx,aligny center");
		gatewaysPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
		
		gatewaysPanel.add(gatewaysTextField, "cell 0 0,grow");
		gatewaysPanel.add(gatewaysButton, "cell 1 0,alignx center,aligny center");
		
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
		lblRouters.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblRouters, "cell 2 1,grow");
		
		contentPanel.add(routersPanel, "cell 3 1,growx,aligny center");
		routersPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
		
		routersPanel.add(routersTextField, "cell 0 1,grow");
		
		routersPanel.add(routersButton, "cell 1 1");
		lblTraffic.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblTraffic, "cell 0 2 1 2,grow");
		
		contentPanel.add(trafficPanel, "cell 1 2 1 2,grow");
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
		lblIfactor.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblIfactor, "cell 2 2,growx,aligny center");
		IFactorTextField.setEditable(false);
		IFactorTextField.setColumns(10);
		
		contentPanel.add(IFactorPanel, "cell 3 2,growx,aligny center");
		IFactorPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
		
		IFactorPanel.add(IFactorTextField, "cell 0 0,grow");
		
		IFactorPanel.add(IFactorButton, "cell 1 0,growx");
		lblDatarates.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblDatarates, "cell 2 3,growx,aligny center");
		
		contentPanel.add(dataratesPanel, "cell 3 3,growx,aligny center");
		dataratesPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
		
		dataratesPanel.add(dataratesTextField, "cell 0 0,grow");
		
		dataratesPanel.add(dataratesButton, "cell 1 0,growx");
		
		lblAlgorithm.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblAlgorithm, "cell 0 4,grow");

		JPanel rdbtnPanel = new JPanel();
		contentPanel.add(rdbtnPanel, "cell 1 4,alignx left,growy");
		rdbtnPanel.setLayout(new GridLayout(2, 0, 0, 0));
		
		JRadioButton rdbtnOriginal = new JRadioButton("Original algorithm");
		rdbtnPanel.add(rdbtnOriginal);

		rdbtnPanel.add(rdbtnNewAlgorithm);
		
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnNewAlgorithm);
		group.add(rdbtnOriginal);
		rdbtnNewAlgorithm.setSelected(true);
		lblSinr.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblSinr, "cell 2 4,growx,aligny center");
		
		contentPanel.add(SINRPanel, "cell 3 4,growx,aligny center");
		SINRPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
		
		SINRPanel.add(SINRTextField, "cell 0 0,grow");
		
		SINRPanel.add(SINRButton, "cell 1 0,growx");
		newAlgoPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "New algorithm parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		contentPanel.add(newAlgoPanel, "cell 0 5 2 1,alignx center,aligny center");
		newAlgoPanel.setLayout(new MigLayout("insets 5 0 0 0", "[min!]10[157px]", "[83px][83px]"));
		newAlgoPanel.add(ratioLabelsPanel, "cell 0 0,alignx trailing,aligny center");
		ratioLabelsPanel.setLayout(new MigLayout("insets 0 0 0 0", "[157px]", "[min!][min!]"));
		
		JLabel spinnerLabel = new JLabel("Ratio Downlinks");
		ratioLabelsPanel.add(spinnerLabel, "cell 0 0,alignx right,growy");
		spinnerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		spinnerLabel.setAlignmentX(CENTER_ALIGNMENT);
		lblOverUplinks.setHorizontalAlignment(SwingConstants.CENTER);
		
		ratioLabelsPanel.add(lblOverUplinks, "cell 0 1,alignx right,growy");
		
		spinnerRatio.setModel(new SpinnerNumberModel(1, 1, 4, 1));
		JPanel spinnerPanel = new JPanel();
		newAlgoPanel.add(spinnerPanel, "cell 1 0,growx,aligny center");
		spinnerPanel.setAlignmentX(CENTER_ALIGNMENT);
		spinnerPanel.setLayout(new MigLayout("insets 0 5 0 0", "[157px]", "[27px]"));
		spinnerPanel.add(spinnerRatio, "cell 0 0,growx,aligny center");
		newAlgoPanel.add(lblNewLabel, "cell 0 1,alignx right,aligny center");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		newAlgoPanel.add(chckbxPanel, "cell 1 1,grow");
		chckbxPanel.setLayout(new GridLayout(3, 0, 0, 0));

		chckbxPanel.add(chckbxAlternateOrder);

		chckbxPanel.add(chckbxRepeatLinksTo);

		chckbxPanel.add(chckbxEnlargeByGateways);
		lblChannels.setHorizontalAlignment(SwingConstants.TRAILING);
		
		contentPanel.add(lblChannels, "cell 2 5,growx,aligny center");
		
		contentPanel.add(channelsPanel, "cell 3 5,grow");
		channelsPanel.setLayout(new MigLayout("", "[][grow]", "[][grow][]"));
		
		channelsPanel.add(lblMode, "cell 0 0,alignx trailing");
		channelModeComboBox.setModel(new DefaultComboBoxModel(new String[] {"All Channels", "Partially"}));
		channelModeComboBox.setSelectedIndex(0);
		
		channelsPanel.add(channelModeComboBox, "cell 1 0,grow");
		lblSelected.setHorizontalAlignment(SwingConstants.TRAILING);
		
		channelsPanel.add(lblSelected, "flowy,cell 0 1,growx");
		lblChannels_1.setHorizontalAlignment(SwingConstants.TRAILING);
		
		channelsPanel.add(lblChannels_1, "cell 0 1,growx");
		
		channelsPanel.add(channelScrollPane, "cell 1 1,grow");
		channelList.setVisibleRowCount(3);
		channelScrollPane.setViewportView(channelList);
		lblStrategy.setHorizontalAlignment(SwingConstants.TRAILING);
		
		channelsPanel.add(lblStrategy, "cell 0 2,growx");
		
		channelsPanel.add(comboBox_1, "cell 1 2,grow");

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
