package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import launcher.Program;
import net.miginfocom.swing.MigLayout;
import setting.XMLParser;
import setting.XMLWriter;
import transConf.TCFacade;
import GraphicVisualization.editDialog.DatarateEditOptionDialog;
import GraphicVisualization.editDialog.GatewaysEditOptionDialog;
import GraphicVisualization.editDialog.IFactorEditOptionDialog;
import GraphicVisualization.editDialog.RoutersEditOptionDialog;
import GraphicVisualization.editDialog.SINREditOptionDialog;

/**
 * @author Benjamin
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class StartOptionsDialog extends JDialog {

	private static final long	serialVersionUID	= -8488459792255899186L;

	public static final int MaxChannelNumber = 11;
	
	private final JPanel	contentPanel	= new JPanel();
	private final JPanel chckbxPanel = new JPanel();
	private final JSpinner spinnerRatio = new JSpinner();
	private final JCheckBox chckbxAlternateOrder = new JCheckBox("Alternate order");
	private final JCheckBox chckbxRepeatLinksTo = new JCheckBox("Repeat links to ensure ratio");
	private final JCheckBox chckbxEnlargeByGateways = new JCheckBox("Enlarge by gateways");
	private final JRadioButton rdbtnNewAlgorithm = new JRadioButton("New algorithm");
	private final JLabel lblAlgorithm = new JLabel("TC Algorithms:");
	private final JLabel lblEnvironment = new JLabel("Environment:");
	private final JPanel environmentPanel = new JPanel();
	private final JSpinner envXSpinner = new JSpinner();
	private final JSpinner envYSpinner = new JSpinner();
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
	private JPanel trafficPanel = new JPanel();
	private final JLabel lblDuration = new JLabel("Duration:");
	private final JSpinner durationSpinner = new JSpinner();
	private final JLabel lblRate = new JLabel("Traffic rate:");
	private final JSpinner rateSpinner = new JSpinner();
	private final JLabel lblSeed = new JLabel("Seed:");
	private final JSpinner seedSpinner = new JSpinner();
	private final JLabel lblNbNewNodes = new JLabel("Nb new nodes:");
	private final JSpinner nodesSpinner = new JSpinner();
	private final JLabel lblRatio = new JLabel("Ratio:");
	private final JSpinner ratioSpinner = new JSpinner();
	private final JLabel lblTraffic = new JLabel("Traffic:");
	private final JLabel lblGenerator = new JLabel("Generator:");
	private final JComboBox trafficComboBox = new JComboBox();
	private final JTextField upTrafficTextField = new JTextField();
	private final JButton btnUpTraffic = new JButton("...");
	private final JLabel lblU = new JLabel("U:");
	private final JButton btnDownTraffic = new JButton("...");
	private final JTextField downTrafficTextField = new JTextField();
	private final JLabel lblD = new JLabel("D:");
	private final JLabel lblSetDefaults = new JLabel("Set defaults...");
	private final JLabel lblUpSeed = new JLabel("Up seed:");
	private final JSpinner upseedSpinner = new JSpinner();
	private final JButton btnUpseed = new JButton("R");
	private final JLabel lblDownSeed = new JLabel("Down seed:");
	private final JSpinner downseedSpinner = new JSpinner();
	private final JButton btnDownseed = new JButton("R");
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
	private final JTextField IFactorTextField = new JTextField("11 IFactors set...");
	private final JPanel dataratesPanel = new JPanel();
	private final JButton dataratesButton = new JButton("Edit");
	private final JTextField dataratesTextField = new JTextField("8 datarates set...");
	private final JPanel SINRPanel = new JPanel();
	private final JButton SINRButton = new JButton("Edit");
	private final JTextField SINRTextField = new JTextField("SINR set...");
	private final JPanel channelsPanel = new JPanel();
	private final JLabel lblMode = new JLabel("Mode:");
	private final JComboBox channelModeComboBox = new JComboBox();
	private final JList channelList = new JList();
	private final JLabel lblChannels_1 = new JLabel("Channels:");
	private final JScrollPane channelScrollPane = new JScrollPane();
	private final JLabel lblSelected = new JLabel("Selected");
	private final JLabel lblStrategy = new JLabel("Strategy:");
	private final JComboBox channelStrategyComboBox = new JComboBox();
	private final JLabel lbluseCtrlTo = new JLabel("(use Ctrl to select mutiples)");
	private final JLabel lblSchedulingStrategy = new JLabel("Scheduling");
	private final JPanel schedulingPanel = new JPanel();
	private final JComboBox schedulingComboBox = new JComboBox();
	private final JLabel lblStrategy_1 = new JLabel("Strategy:");
	
	
	private final JButton defaultButton = new JButton("Use Default and Run");
	
	private GatewaysEditOptionDialog gatewaysDialog;
	private RoutersEditOptionDialog routersDialog;
	private IFactorEditOptionDialog ifactorDialog;
	private DatarateEditOptionDialog datarateDialog;
	private SINREditOptionDialog sinrDialog;
	
	private String configFile;
	private final JPanel trafficLabelPanel = new JPanel();
	private final JRadioButton rdbtnStatic = new JRadioButton("Static");
	private final JRadioButton rdbtnDynamic = new JRadioButton("Dynamic");
	private final ButtonGroup groupTraffic = new ButtonGroup();

	/**
	 * Create the dialog.
	 */
	public StartOptionsDialog() {
		/*--------------*/
		/* Init Dialogs */
		/*--------------*/
		{
			gatewaysDialog = new GatewaysEditOptionDialog(this);
			routersDialog = new RoutersEditOptionDialog(this);
			ifactorDialog = new IFactorEditOptionDialog(this);
			datarateDialog = new DatarateEditOptionDialog(this);
			sinrDialog = new SINREditOptionDialog(this);
		}
		
		/*-----------------*/
		/* Init Components */
		/*-----------------*/
		{
			SINRTextField.setEditable(false);
			SINRTextField.setColumns(10);
			dataratesTextField.setEditable(false);
			dataratesTextField.setColumns(10);
			String filePath = "/setting/input/routers.txt";
			routersTextField.setText("File: ..."+filePath.substring(filePath.length()-20, filePath.length()));
			routersTextField.setEditable(false);
			routersTextField.setColumns(10);
			gatewaysTextField.setText("Static: 4 gateways...");
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
		}
		
		/*------------------*/
		/* Frame Parameters */
		/*------------------*/
		{
			setIconImage(Toolkit.getDefaultToolkit().getImage(StartOptionsDialog.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
			setTitle("Parameters");
			setResizable(false);
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(new MigLayout("", "[90px,grow]10[157px,grow]10[95px]10[157px,grow]", "[min!]10[min!]10[min!,grow]10[min!,grow]10[min!,grow]10[min!,grow]10[min!,grow]"));
		}
		
		/*-------------*/
		/* Environment */
		/*-------------*/
		lblEnvironment.setHorizontalAlignment(SwingConstants.TRAILING);
		contentPanel.add(lblEnvironment, "cell 0 0,grow");
		
		contentPanel.add(environmentPanel, "cell 1 0,growx,aligny center");
		environmentPanel.setLayout(new MigLayout("insets 0 0 0 0", "[min!][150px]", "[25px][25px]"));
		environmentPanel.add(lblX);
		
		envXSpinner.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		environmentPanel.add(envXSpinner, "wrap,grow");
		environmentPanel.add(lblY);
		envYSpinner.setModel(new SpinnerNumberModel(new Integer(1000), new Integer(1), null, new Integer(1)));
		environmentPanel.add(envYSpinner, "grow");
		
		contentPanel.add(lblSchedulingStrategy, "flowy,cell 2 0,alignx trailing");
		{
			lblGateways.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		
		/*----------*/
		/* Gateways */
		/*----------*/
		
		contentPanel.add(schedulingPanel, "cell 3 0,grow");
		schedulingPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		schedulingComboBox.setMaximumRowCount(3);
		schedulingComboBox.setModel(new DefaultComboBoxModel(new String[] {"Normal", "Round Robin", "Back Pressure"}));
		schedulingComboBox.setSelectedIndex(1);
		
		schedulingPanel.add(schedulingComboBox, "cell 0 0,growx,aligny center");
		contentPanel.add(lblOutput, "cell 0 1,alignx trailing,aligny center");
		outputFolderPathTextField.setFont(new Font("Tahoma", Font.ITALIC, 11));
		outputFolderPathTextField.setText("Output folder...");
		outputFolderPathTextField.setEditable(false);
		outputFolderPathTextField.setColumns(10);	
		{
			
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
					showFileChooser(outputFolderPathTextField, "output", true, 15);
				}
			});
			btnOutput.setEnabled(false);
			outputPanel.add(btnOutput, "cell 2 1,growx,aligny top");
		}
		{
			contentPanel.add(lblGateways, "cell 2 1,grow");
			
			contentPanel.add(gatewaysPanel, "cell 3 1,growx,aligny center");
			gatewaysPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
			
			gatewaysPanel.add(gatewaysTextField, "cell 0 0,grow");
			gatewaysButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					gatewaysDialog.showDialog(true);
				}
			});
			gatewaysPanel.add(gatewaysButton, "cell 1 0,alignx center,aligny center");
		}
		
		/*--------*/
		/* Output */
		/*--------*/
		
		/*---------*/
		/* Routers */
		/*---------*/
		{
			lblRouters.setHorizontalAlignment(SwingConstants.TRAILING);
			
			contentPanel.add(lblRouters, "cell 2 2,grow");
			
			contentPanel.add(routersPanel, "cell 3 2,growx,aligny center");
			routersPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
			
			routersPanel.add(routersTextField, "cell 0 1,grow");
			routersButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					routersDialog.showDialog(true);
				}
			});
			routersPanel.add(routersButton, "cell 1 1");
		}
		
		/*---------*/
		/* Traffic */
		/*---------*/
		{
			contentPanel.add(trafficLabelPanel, "cell 0 2 1 3,growx,aligny center");
			
			trafficLabelPanel.setLayout(new MigLayout("", "[grow]", "[min!]10[min!][min!]"));
			trafficLabelPanel.add(lblTraffic, "cell 0 0,growx");
			
			lblTraffic.setHorizontalAlignment(SwingConstants.TRAILING);
			
			groupTraffic.add(rdbtnStatic);
			
			rdbtnStatic.setFont(new Font("Tahoma", Font.ITALIC, 10));
			rdbtnStatic.setSelected(true);
			rdbtnStatic.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateTrafficPanel(!rdbtnStatic.isSelected(), trafficComboBox.getSelectedIndex() == 1);
				}
			});
			
			trafficLabelPanel.add(rdbtnStatic, "cell 0 1,alignx right,growy");
			
			groupTraffic.add(rdbtnDynamic);
			
			rdbtnDynamic.setFont(new Font("Tahoma", Font.ITALIC, 10));
			rdbtnDynamic.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateTrafficPanel(rdbtnDynamic.isSelected(), false);
				}
			});
			
			
			trafficLabelPanel.add(rdbtnDynamic, "cell 0 2,alignx right,growy");
			
			trafficComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					boolean select = ((String)trafficComboBox.getSelectedItem()).equals("File");
					btnDownTraffic.setEnabled(select);
					btnUpTraffic.setEnabled(select);
					lblSetDefaults.setEnabled(select);
				}
			});
			trafficComboBox.setModel(new DefaultComboBoxModel(new String[] {"File", "Random"}));
			trafficComboBox.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						updateTrafficPanel(false, trafficComboBox.getSelectedIndex() == 1);
					}
				}
			});
			trafficComboBox.setSelectedIndex(0);
			
			btnUpTraffic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					showFileChooser(upTrafficTextField, "uplink traffic", false, 15);
				}
			});
			
			btnDownTraffic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					showFileChooser(downTrafficTextField, "downlink traffic", false, 15);
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
			
			durationSpinner.setModel(new SpinnerNumberModel(new Long(100000), new Long(1), null, new Long(100)));
			rateSpinner.setModel(new SpinnerNumberModel(new Double(0.5), new Double(0.001), new Double(1.0), new Double(0.01)));
			seedSpinner.setModel(new SpinnerNumberModel(Math.abs(new Random().nextLong()), new Long(1), null, new Long(1)));
			nodesSpinner.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
			ratioSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(1), null, new Integer(1)));
			
			upseedSpinner.setModel(new SpinnerNumberModel(Math.abs(new Random().nextLong()), new Long(1), null, new Long(1)));
			btnUpseed.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					upseedSpinner.setValue(Math.abs(new Random().nextLong()));
				}
			});
			downseedSpinner.setModel(new SpinnerNumberModel(Math.abs(new Random().nextLong()), new Long(1), null, new Long(1)));
			btnDownseed.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					downseedSpinner.setValue(Math.abs(new Random().nextLong()));
				}
			});
			
			updateTrafficPanel(false, false);
		}
			
		/*---------*/
		/* IFactor */
		/*---------*/
		{
			lblIfactor.setHorizontalAlignment(SwingConstants.TRAILING);
			
			contentPanel.add(lblIfactor, "cell 2 3,growx,aligny center");
			IFactorTextField.setEditable(false);
			IFactorTextField.setColumns(10);
			
			contentPanel.add(IFactorPanel, "cell 3 3,growx,aligny center");
			IFactorPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
			
			IFactorPanel.add(IFactorTextField, "cell 0 0,grow");
			IFactorButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ifactorDialog.showDialog(true);
				}
			});
			IFactorPanel.add(IFactorButton, "cell 1 0,growx");
		}
		
		/*-----------*/
		/* Datarates */
		/*-----------*/
		{
			lblDatarates.setHorizontalAlignment(SwingConstants.TRAILING);
			contentPanel.add(lblDatarates, "cell 2 4,growx,aligny center");
			
			contentPanel.add(dataratesPanel, "cell 3 4,growx,aligny center");
			dataratesPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
			
			dataratesPanel.add(dataratesTextField, "cell 0 0,grow");
			dataratesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					datarateDialog.showDialog(true);
				}
			});
			dataratesPanel.add(dataratesButton, "cell 1 0,growx");
		}
		
		/*------------*/
		/* Algorithms */
		/*------------*/
		{
			lblAlgorithm.setHorizontalAlignment(SwingConstants.TRAILING);
			contentPanel.add(lblAlgorithm, "cell 0 5,grow");
	
			JPanel rdbtnPanel = new JPanel();
			contentPanel.add(rdbtnPanel, "cell 1 5,alignx left,growy");
			rdbtnPanel.setLayout(new GridLayout(2, 0, 0, 0));
			
			JRadioButton rdbtnOriginal = new JRadioButton("Original algorithm");
			rdbtnPanel.add(rdbtnOriginal);
	
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
			rdbtnPanel.add(rdbtnNewAlgorithm);
			
			ButtonGroup groupAlgo = new ButtonGroup();
			groupAlgo.add(rdbtnNewAlgorithm);
			groupAlgo.add(rdbtnOriginal);
			rdbtnNewAlgorithm.setSelected(true);
			lblSinr.setHorizontalAlignment(SwingConstants.TRAILING);
			
			newAlgoPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "New algorithm parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
			
			contentPanel.add(newAlgoPanel, "cell 0 6 2 1,alignx center,aligny center");
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
		}
		
		/*------*/
		/* SINR */
		/*------*/
		{
			contentPanel.add(lblSinr, "cell 2 5,growx,aligny center");
			
			contentPanel.add(SINRPanel, "cell 3 5,growx,aligny center");
			SINRPanel.setLayout(new MigLayout("", "[grow][min!]", "[min!]"));
			
			SINRPanel.add(SINRTextField, "cell 0 0,grow");
			SINRButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					sinrDialog.showDialog(true);
				}
			});
			SINRPanel.add(SINRButton, "cell 1 0,growx");
		}
		
		/*----------*/
		/* Channels */
		/*----------*/
		{
			contentPanel.add(lblChannels, "cell 2 6,growx,aligny center");
			
			contentPanel.add(channelsPanel, "cell 3 6,grow");
			channelsPanel.setLayout(new MigLayout("", "[][grow]", "[][grow][]"));
			
			channelsPanel.add(lblMode, "cell 0 0,alignx trailing");
			channelModeComboBox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					channelList.setEnabled(channelModeComboBox.getSelectedIndex() != 0);
					lbluseCtrlTo.setVisible(channelModeComboBox.getSelectedIndex() != 0);
				}
			});
			channelModeComboBox.setModel(new DefaultComboBoxModel(new String[] {"All Channels", "Partially"}));
			channelModeComboBox.setSelectedIndex(0);
			
			channelsPanel.add(channelModeComboBox, "cell 1 0,grow");
			lblSelected.setHorizontalAlignment(SwingConstants.TRAILING);
			
			channelsPanel.add(lblSelected, "flowy,cell 0 1,growx");
			lblChannels_1.setHorizontalAlignment(SwingConstants.TRAILING);
			
			channelsPanel.add(lblChannels_1, "cell 0 1,growx");
			
			channelsPanel.add(channelScrollPane, "cell 1 1,grow");
			channelList.setEnabled(false);
			channelList.setModel(new AbstractListModel() {
				public int getSize() {
					return StartOptionsDialog.MaxChannelNumber;
				}
				public Object getElementAt(int index) {
					return ((index < StartOptionsDialog.MaxChannelNumber) ? index+1 : null);
				}
			});
			channelList.setVisibleRowCount(3);
			channelScrollPane.setViewportView(channelList);
			
			lbluseCtrlTo.setVisible(false);
			lbluseCtrlTo.setFont(new Font("Tahoma", Font.PLAIN, 9));
			lbluseCtrlTo.setHorizontalAlignment(SwingConstants.CENTER);
			
			channelScrollPane.setColumnHeaderView(lbluseCtrlTo);
			lblStrategy.setHorizontalAlignment(SwingConstants.TRAILING);
			
			channelsPanel.add(lblStrategy, "cell 0 2,growx");
			channelStrategyComboBox.setModel(new DefaultComboBoxModel(
					new String[] {"All Balancing", "Orthogonal Balancing", "Non Orthogonal Blancing", "Original",
							"Select Min Randomly", "SINR"}));
			channelStrategyComboBox.setSelectedIndex(3);
			
			channelsPanel.add(channelStrategyComboBox, "cell 1 2,grow");
		}
		
		contentPanel.add(lblStrategy_1, "cell 2 0,alignx trailing");

		/*---------*/
		/* Buttons */
		/*---------*/
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			defaultButton.setMnemonic(KeyEvent.VK_D);
			defaultButton.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Properties24.gif")));
			defaultButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TCFacade.newAlgortihm = rdbtnNewAlgorithm.isSelected();
					TCFacade.downOverUpRatio = (int) spinnerRatio.getValue();
					TCFacade.alternateOrder = chckbxAlternateOrder.isSelected();
					TCFacade.repeatLinksToRespectRatio = chckbxRepeatLinksTo.isSelected();
					TCFacade.enlargeByGateways = chckbxEnlargeByGateways.isSelected();
					//Program.schedulingStrategy = (String) schedulingComboBox.getSelectedItem();
					/* Write config_auto.xml */
					configFile = "/setting/input/config_default.xml";
					JOptionPane.showMessageDialog(null,
		        			"Loading config file: \n"+XMLParser.class.getResource(configFile).getPath(),
		    			    "Loading configuration",            			    
		    			    JOptionPane.INFORMATION_MESSAGE);
					dispose();
					XMLParser.CONFIGFILE = configFile;
					Program.launch();
				}
			});
			buttonPane.add(defaultButton);
			
			JButton loadConfigButton = new JButton("Load and Run");
			loadConfigButton.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Open24.gif")));
			loadConfigButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TCFacade.newAlgortihm = rdbtnNewAlgorithm.isSelected();
					TCFacade.downOverUpRatio = (int) spinnerRatio.getValue();
					TCFacade.alternateOrder = chckbxAlternateOrder.isSelected();
					TCFacade.repeatLinksToRespectRatio = chckbxRepeatLinksTo.isSelected();
					TCFacade.enlargeByGateways = chckbxEnlargeByGateways.isSelected();
					//Program.schedulingStrategy = (String) schedulingComboBox.getSelectedItem();
					/* Write config_auto.xml */
					JTextField blank = new JTextField();
					showFileChooser(blank, "configuration to load", false, 10);
					configFile = blank.getToolTipText();
					if(configFile != null) {
						XMLParser.CONFIGFILE = configFile;
						Program.launch();
						dispose();
					}
				}
			});
			buttonPane.add(loadConfigButton);
	
			JButton saveAndRunButton = new JButton("Save and Run");
			saveAndRunButton.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/media/Play24.gif")));
			saveAndRunButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					TCFacade.newAlgortihm = rdbtnNewAlgorithm.isSelected();
					TCFacade.downOverUpRatio = (int) spinnerRatio.getValue();
					TCFacade.alternateOrder = chckbxAlternateOrder.isSelected();
					TCFacade.repeatLinksToRespectRatio = chckbxRepeatLinksTo.isSelected();
					TCFacade.enlargeByGateways = chckbxEnlargeByGateways.isSelected();
					//Program.schedulingStrategy = (String) schedulingComboBox.getSelectedItem();
					/* Write config_auto.xml */
					writeConfiguration();
					if(configFile != null) {
						XMLParser.CONFIGFILE = configFile;
						dispose();
						Program.launch();
					}
				}
			});
			buttonPane.add(saveAndRunButton);
			getRootPane().setDefaultButton(saveAndRunButton);
	
			JButton cancelButton = new JButton("Exit");
			cancelButton.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Stop24.gif")));
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
			buttonPane.add(cancelButton);
		}
		
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	}
	
	private void updateTrafficPanel(boolean dynamic, boolean random) {
		contentPanel.remove(trafficPanel);
		trafficPanel = new JPanel();
		if(dynamic) {
			trafficPanel.setLayout(new MigLayout("insets 5 3 0 0", "[min!][grow]", "[grow][grow][grow][grow][grow]"));
			trafficPanel.add(lblDuration, "cell 0 0,alignx trailing");
			trafficPanel.add(durationSpinner, "cell 1 0, grow");
			trafficPanel.add(lblRate, "cell 0 1,alignx trailing");
			trafficPanel.add(rateSpinner, "cell 1 1, grow");
			trafficPanel.add(lblSeed, "cell 0 2,alignx trailing");
			seedSpinner.setValue(Math.abs(new Random().nextLong()));
			trafficPanel.add(seedSpinner, "cell 1 2, grow");
			trafficPanel.add(lblNbNewNodes, "cell 0 3,alignx trailing");
			trafficPanel.add(nodesSpinner, "cell 1 3, grow");
			trafficPanel.add(lblRatio, "cell 0 4,alignx trailing");
			trafficPanel.add(ratioSpinner, "cell 1 4, grow");
		} else {
			trafficPanel.setLayout(new MigLayout("insets 5 3 0 0", "[][grow][min!][grow][min!]", "[grow][grow][grow][][grow]"));
			trafficPanel.add(lblGenerator, "cell 0 0 2 1,alignx trailing");
			trafficPanel.add(trafficComboBox, "cell 3 0 3 1,grow");
			if(random) {				
				trafficPanel.add(lblUpSeed, "cell 0 1,alignx trailing");
				trafficPanel.add(upseedSpinner, "cell 1 1 3 1,grow");
				trafficPanel.add(btnUpseed, "cell 4 1,growx");
				trafficPanel.add(lblDownSeed, "cell 0 2,alignx trailing");
				trafficPanel.add(downseedSpinner, "cell 1 2 3 1,growx");
				trafficPanel.add(btnDownseed, "cell 4 2");
			} else {
				trafficPanel.add(lblU, "cell 0 1,alignx trailing");
				trafficPanel.add(upTrafficTextField, "cell 1 1 3 1,growx");
				trafficPanel.add(btnUpTraffic, "cell 4 1,growx");
				trafficPanel.add(lblD, "cell 0 2,alignx trailing");
				trafficPanel.add(downTrafficTextField, "cell 1 2 3 1,growx");
				trafficPanel.add(btnDownTraffic, "cell 4 2");
				trafficPanel.add(lblSetDefaults, "cell 3 3 2 1,alignx right");
			}
		}
		
		contentPanel.add(trafficPanel, "cell 1 2 1 3,grow");
		
		this.revalidate();
		this.repaint();
	}
	
	public static void showFileChooser(JTextField source, String title, boolean folder, int textSize) {
		final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
		fileChooser.setFileSelectionMode(((folder) ? JFileChooser.DIRECTORIES_ONLY :JFileChooser.FILES_ONLY));
		fileChooser.setDialogTitle("Select the "+((folder) ? "folder" : "file")+" for the "+title);
		fileChooser.setMultiSelectionEnabled(false);
		int retVal = fileChooser.showDialog(null, "Choose");
		if(retVal == JFileChooser.APPROVE_OPTION) {	
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			if(filePath.length() > textSize) {
				source.setText("..."+filePath.substring(filePath.length()-textSize, filePath.length()));
			} else {
				source.setText(filePath);
			}
			source.setToolTipText(filePath);
		} else {
			/*TODO*/
		}
		
	}
	
	public void updateOnOptionDialogCompleted(String identifier) {
		switch(identifier) {
		case "gateways" :
			System.out.println(gatewaysDialog.getResults());
			if(gatewaysDialog.getResults().containsKey("nbOfGateways")) {
				gatewaysTextField.setText("Static: "+gatewaysDialog.getResults().get("nbOfGateways")+" gateways...");
			} else {
				String filePath = (String) gatewaysDialog.getResults().get("file");
				gatewaysTextField.setText("File: ..."+filePath.substring(filePath.length()-20, filePath.length()));
			}
			break;
		case "routers" :
			System.out.println(routersDialog.getResults());
			if(routersDialog.getResults().containsKey("nbOfRouters")) {
				String head = "";
				if(routersDialog.getResults().containsKey("seed")) {
					head += "Random: ";
				} else {
					head += "Static: ";
				}
				routersTextField.setText(head+routersDialog.getResults().get("nbOfRouters")+" routers...");
			} else {
				String filePath = (String) routersDialog.getResults().get("file");
				routersTextField.setText("File: ..."+filePath.substring(filePath.length()-20, filePath.length()));
			}
			break;
		case "ifactor" :
			System.out.println(ifactorDialog.getResults());
			break;
		case "datarate" :
			System.out.println(datarateDialog.getResults());
			dataratesTextField.setText(datarateDialog.getResults().get("nbOfDatarates")+" datarates set...");
			break;
		case "sinr" :
			System.out.println(sinrDialog.getResults());
			break;
		}
	}
	
	private void writeConfiguration() {
		XMLWriter.Initialize();
		
		String channelAssignment = "";
		switch(channelStrategyComboBox.getSelectedItem().toString()) {
		case "All Balancing" :
			channelAssignment = "AllBalancingSterategy";
			break;
		case "Orthogonal Balancing" :
			channelAssignment = "OrthogonalBalancingSterategy";
			break;
		case "Non Orthogonal Blancing" :
			channelAssignment = "NonOrthogonalBalancingSterategy";
			break;
		case "Original" :
			channelAssignment = "OriginalSterategy";
			break;
		case "Select Min Randomly" :
			channelAssignment = "SelectMinRandomlySterategy";
			break;
		case "SINR" :
			channelAssignment = "SINRSterategy";
			break;
		}
		XMLWriter.writeChannelAssignment(channelAssignment);
		
		HashSet<Integer> channelSet = new HashSet<Integer>();
		for(int i : channelList.getSelectedIndices()) {
			channelSet.add(i+1);
		}
		XMLWriter.writeChannels(((channelModeComboBox.getSelectedIndex() == 0) ? "All" : "Partially"),
				channelSet);
		
		XMLWriter.writeDatarates((HashMap<Double, Integer>) datarateDialog.getResults().get("datarate"));
		
		XMLWriter.writeEnvironment((int) envXSpinner.getValue(), (int) envYSpinner.getValue());
		
		HashMap<String, Object> gatewaysResult = gatewaysDialog.getResults();
		String gatewayGenerator = gatewaysResult.get("generation").toString();
		if(gatewayGenerator == "File") {
			XMLWriter.writeGateways((int)gatewaysResult.get("radio"), 0,
					gatewayGenerator, gatewaysResult.get("file").toString(), null);
		} else {
			XMLWriter.writeGateways((int) gatewaysResult.get("radio"), (int) gatewaysResult.get("nbOfGateways"),
					gatewayGenerator, "", (int[][]) gatewaysResult.get("gateways"));
		}
		
		XMLWriter.writeIFactor((double[]) ifactorDialog.getResults().get("ifactor"));
		
		XMLWriter.writeOutputFolder(outputFolderPathTextField.getToolTipText()+"", chckbxGenerateFiles.isSelected());
		
		HashMap<String, Object> routersResult = routersDialog.getResults();
		String routersGenerator = routersResult.get("generation").toString();
		if(routersGenerator == "File") {
			XMLWriter.writeRouters((int) routersResult.get("radio"), routersGenerator,
					(int) routersResult.get("minDistance"), (int) routersResult.get("transmissionRate"),
					routersResult.get("file").toString(), null, 0, 0, 0);
		} else if(routersGenerator == "Static") {
			XMLWriter.writeRouters((int) routersResult.get("radio"), routersGenerator,
					(int) routersResult.get("minDistance"), (int) routersResult.get("transmissionRate"),
					"", (int[][]) routersResult.get("routers"), (int) routersResult.get("nbOfRouters"), 0, 0);
		} else if(routersGenerator == "Random") {
			XMLWriter.writeRouters((int) routersResult.get("radio"), routersGenerator,
					(int) routersResult.get("minDistance"), (int) routersResult.get("transmissionRate"),
					"", null, (int) routersResult.get("nbOfRouters"), (int) routersResult.get("safetyTest"),
					(long) routersResult.get("seed"));
		}
		
		XMLWriter.writeSchedulingStrategy(((String) this.schedulingComboBox.getSelectedItem()).replaceAll("\\s+","")
				+ "SchedulingStrategy");
		
		HashMap<String, Object> sinrResult = sinrDialog.getResults();
		XMLWriter.writeSINR((int) sinrResult.get("alpha"), (int) sinrResult.get("w"),
				(int) sinrResult.get("power"), (double) sinrResult.get("beta"),
				(double) sinrResult.get("mu"));
		
		XMLWriter.writeTraffic(rdbtnDynamic.isSelected(),
				trafficComboBox.getSelectedItem().toString(),
				upTrafficTextField.getToolTipText()+"",
				downTrafficTextField.getToolTipText()+"",
				(long) upseedSpinner.getValue(),
				(long) downseedSpinner.getValue(),
				(double) rateSpinner.getValue(),
				(long) seedSpinner.getValue(),
				(int) nodesSpinner.getValue(),
				(int) ratioSpinner.getValue(),
				(long) durationSpinner.getValue());

		File f = saveConfiguration();
		if(f != null) {
			configFile = f.getAbsolutePath();
			XMLWriter.write(f);
		}
	}
	
	public static File saveConfiguration() {
		try {
			System.out.println(StartOptionsDialog.class.getResource("/setting/input").getPath());
			final JFileChooser fileChooser = new JFileChooser(
					StartOptionsDialog.class.getResource("/setting/input").getPath()){
				private static final long serialVersionUID = 5313190094648329448L;
				@Override
			    public void approveSelection(){
			        File f = getSelectedFile();
			        if(f.exists() && getDialogType() == SAVE_DIALOG){
			            int result = JOptionPane.showConfirmDialog(this,
			            		"The file "+f.getName()+" already exists.\n\n"
			            				+ "Do you want to overwrite it?",
			            		"Existing file",
			            		JOptionPane.YES_NO_CANCEL_OPTION);
			            switch(result){
			                case JOptionPane.YES_OPTION:
			                    super.approveSelection();
			                    return;
			                case JOptionPane.NO_OPTION:
			                    return;
			                case JOptionPane.CLOSED_OPTION:
			                    return;
			                case JOptionPane.CANCEL_OPTION:
			                    cancelSelection();
			                    return;
			            }
			        }
			        super.approveSelection();
			    }
			};
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setDialogTitle("Save the configuration file as...");
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setSelectedFile(new File(StartOptionsDialog.class.getResource("/setting/input").getPath()+
					"/config.xml"));
			FileNameExtensionFilter filterXML = new FileNameExtensionFilter(
			        "XML Document", "xml");
			fileChooser.addChoosableFileFilter(filterXML);
			fileChooser.setFileFilter(filterXML);
			int returnVal = fileChooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				File file = null;
				if(!selectedFile.getAbsolutePath().toLowerCase().endsWith(".xml") &&
						!selectedFile.getAbsolutePath().toLowerCase().endsWith(".xml")) {
					file = new File(selectedFile.getAbsolutePath()+".xml");
				} else {
					file = selectedFile;
				}
				return file;
			}
		} catch (Exception e) {
			GraphViewer.showErrorDialog(e.getMessage());
		}
		return null;
	}

}
