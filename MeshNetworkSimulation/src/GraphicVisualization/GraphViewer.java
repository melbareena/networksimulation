package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import launcher.Program;
import setting.ApplicationSettingFacade;
import trafficEstimating.TrafficEstimatingFacade;
import transConf.TCFacade;
import cAssignment.ChannelAssignmentFacade;

import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;

import dataStructure.Path;
import dataStructure.TCUnit;
import dataStructure.Vertex;
 
/**
 * @author Benjamin
 *
 */
public class GraphViewer extends JFrame {
 
	/** serialVersionUID */
	private static final long serialVersionUID = -8123406571694511514L;
		
	/** Label style constant */
	private static final String FontSizeDefaultStyle = 
			"fontSize=8;";
	
	/** Label style constant */
	private static final String DownlinkDefaultColorStyle = 
			"strokeColor=red;fontColor=red;labelBackgroundColor=#ffeaea;";
	
	/** Label style constant */
	private static final String UplinkDefaultColorStyle = 
			"strokeColor=green;fontColor=green;labelBackgroundColor=#eafff1;";
	
	/** Label style constant */
	private static final String GatewayDefaultStyle = 
			"fillColor=lightcoral;shape=rectangle;";
	
	/** Label style constant */
	private static final String RouterDefaultStyle = 
			"fillColor=powderblue;shape=ellipse;";
	
	/** The number of channels in the network */
	protected static final int MaxChannelNumber = 11;
	
	/** The main graph object */
	private mxGraph graph;
	
	/** The visual component showing the graph */
	private mxGraphComponent graphComponent;
	
	/** The parent cell of all the others in graph */
	private mxCell parent;
	
	/** Map containing the vertices of the graph
	 * <code>key: VertexID (integer) -> value: Vertex (mxCell)</code>*/
	private static HashMap<Integer, mxCell> mapVertices;
	
	/** Map containing the edges of the graph
	 * <code>key: EdgeID (integer) -> value: Link</code>
	 * @see Link*/
	private static HashMap<Integer, Link> mapEdges;
	
	/** Map containing the Transmission Configurations
	 * <code>key: configID (integer) -> value: linkIDs (List)</code>*/
	private static HashMap<Integer, ArrayList<Integer>> mapConfigurations;
	
	/** Map containing the Datarates for each Transmission Configuration
	 * <code>key: configID (integer) -> value: datarate (List)</code>*/
	private static HashMap<Integer, ArrayList<Integer>> mapDatarates;
	
	/* Swing stuff */
	private final JCheckBox chckbxDownlinks = new JCheckBox("Downlinks");
	private final JCheckBoxMenuItem menuDownlinks = new JCheckBoxMenuItem("Downlinks");
	private final JCheckBox chckbxUplinks = new JCheckBox("Uplinks");
	private final JCheckBoxMenuItem menuUplinks = new JCheckBoxMenuItem("Uplinks");
	private final JCheckBox chckbxID = new JCheckBox("Link ID");
	private final JCheckBoxMenuItem menuID = new JCheckBoxMenuItem("Link ID");
	private final JCheckBox chckbxChannel = new JCheckBox("Channel");
	private final JCheckBoxMenuItem menuChannel = new JCheckBoxMenuItem("Channel");
	private final JCheckBox chckbxDatarate = new JCheckBox("Datarate");
	private final JCheckBoxMenuItem menuDatarate = new JCheckBoxMenuItem("Datarate");
	private final JRadioButton rdbtnColorByLink = new JRadioButton("Color by Link");
	private final JRadioButtonMenuItem menuColorByLink = new JRadioButtonMenuItem("Color by Link");
	private final JRadioButton rdbtnColorByChannel = new JRadioButton("Color by Channel");
	private final JRadioButtonMenuItem menuColorByChannel = new JRadioButtonMenuItem("Color by Channel");
	private final JCheckBox chckbxThickness = new JCheckBox("Thickness (Datarate)");
	private final JCheckBoxMenuItem menuThickness = new JCheckBoxMenuItem("Thickness (Datarate)");
	private final JRadioButton rdbtnAllLinks = new JRadioButton("All Links");
	private final JRadioButtonMenuItem menuAllLinks = new JRadioButtonMenuItem("All Links");
	private final JRadioButton rdbtnConfiguration = new JRadioButton("Configuration:");
	private final JRadioButtonMenuItem menuConfiguration = new JRadioButtonMenuItem("Configuration");
	private JComboBox<Integer> configList;
	private final JMenu menuListConfig = new JMenu("  (list)");
	private final JSlider sliderLabels = new JSlider();
	private final JSlider sliderThickness = new JSlider();
	private final JMenuItem mntmSaveAll = new JMenuItem("Save all configurations...");
	
	/* ColorViewer dialog */
	private final ColorViewer colorViewerDialog = new ColorViewer(this, ColorViewer.ColorType.Links);
	
	/* HistogramViewer frame */
	private final HistogramViewer histogramViewerFrame;
	
	public static final String optionsTitle = "(" + ((TCFacade.newAlgortihm) ? "new algo." : "origin. algo.")
			+ " ; ratio " + TCFacade.downOverUpRatio
			+ ((TCFacade.alternateOrder) ? " ; alt. order" : "")
			+ ((TCFacade.repeatLinksToRespectRatio) ? " ; repeat links" : "")
			+ ((TCFacade.enlargeByGateways) ? " ; enlarge by gw" : "")
			+ ")";
 
	/** Initiate and show the Frame.
	 * @param throughputData The data for the throughput plot.
	 */
	public GraphViewer(Vector<Double> throughputData, Vector<Double> sourceData,
			Vector<Double> transmitData) {
		super();
		
		String title = "Mesh Network Simulation " + GraphViewer.optionsTitle;
		
		this.setTitle(title);
		
		int step = throughputData.size()/100;
		histogramViewerFrame = new HistogramViewer(throughputData, sourceData, transmitData, step);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				histogramViewerFrame.showGraph();
			}
		});
		t.run();
		

		this.graph = new mxGraph() {
			// Tooltips for edges
			public String getToolTipForCell(Object cell) {
				if (model.isEdge(cell)) {
					String s = "";
					try {
						mxCell c = (mxCell) cell;
						if(mapEdges != null) {
							int id = Integer.parseInt(c.getId())/100;
							Link e = mapEdges.get(id);
							s += "<br/>ID#"+id+"<br/>Channel "+e.getChannel();
							int TCindex = mapConfigurations.get(configList.getSelectedIndex()).indexOf(id);
							if(TCindex != -1) {
								s += "<br/>Datarate "+mapDatarates.get(configList.getSelectedIndex()).get(TCindex);
							}
						}
					} catch(Exception e) {}
					return "<html><center>Link "+convertValueToString(model.getTerminal(cell, true)) + " -> " +
						convertValueToString(model.getTerminal(cell, false)) + s+"</center></html>";
				}
				return super.getToolTipForCell(cell);
			}
		};
		this.parent = (mxCell) this.graph.getDefaultParent();
		
		GraphViewer.mapVertices = new HashMap<Integer, mxCell>();
		GraphViewer.mapEdges = new HashMap<Integer, Link>();
		GraphViewer.mapConfigurations = new HashMap<Integer, ArrayList<Integer>>();
		GraphViewer.mapDatarates = new HashMap<Integer, ArrayList<Integer>>();

		getDataAndFillGraph();
		
		//this.graph.setCellsMovable(false);
		this.graph.setCellsResizable(false);
		this.graph.setCellsEditable(false);
		this.graph.setCellsSelectable(false);
		this.graph.setCellsDisconnectable(false);
		this.graph.setCellsDeletable(false);
		this.graph.setCellsCloneable(false);
		this.graph.setCellsBendable(false);
		for(int i : GraphViewer.mapVertices.keySet()) {
			mxCell vertex = GraphViewer.mapVertices.get(i);
			vertex.setConnectable(false);
		}
 
		graphComponent = new mxGraphComponent(this.graph);
		graphComponent.setToolTips(true);
		graphComponent.getGraphControl().setCursor(new Cursor(Cursor.HAND_CURSOR));
		new mxParallelEdgeLayout(this.graph).execute(this.parent);
		
		graphComponent.getGraphControl().addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if(e.getWheelRotation() < 0) {
					graphComponent.zoomIn();
				} else {
					graphComponent.zoomOut();
				}
				e.consume();
			}
		});
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(graphComponent, BorderLayout.CENTER);
		
		buildBottomPanel();
		
		buildMenu();
		
		System.out.println("Window Size is H:"+(int)Math.ceil(this.graph.getGraphBounds().getHeight()+270)+
				" - W:"+(int)Math.ceil(this.graph.getGraphBounds().getWidth()+35));
		
		this.setSize((int)Math.ceil(this.graph.getGraphBounds().getWidth()+35),
				(int)Math.ceil(this.graph.getGraphBounds().getHeight()+270));
		
		updateShowingLinks();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Object[] options = {"Reset", "Exit", "Cancel"};
				int confirmed = JOptionPane.showOptionDialog(null,
						"Do you want to exit or reset the program?",
						"Exit or reset ?",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null, options,
						options[1]);
				switch(confirmed) {
					case JOptionPane.YES_OPTION:
						colorViewerDialog.dispose();
						histogramViewerFrame.dispose();
						dispose();
						Program.restartApplication();
						break;
					case JOptionPane.NO_OPTION:
						System.exit(0);
						break;
					case JOptionPane.CANCEL_OPTION:
						break;
				}
			}
		});
		
		setVisible(true);
		Program.loadingDialog.addProgress(100 - Program.loadingDialog.getProgress(),
				"Done!");
    }
	
	/** Build the bottom panel of the frame 
	 */
	protected void buildBottomPanel() {
		JScrollPane scrollPaneBottom = new JScrollPane();
		getContentPane().add(scrollPaneBottom, BorderLayout.SOUTH);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		scrollPaneBottom.setViewportView(bottomPanel);
		
		bottomPanel.add(Box.createHorizontalGlue());
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(2, 1));
		leftPanel.setAlignmentY(TOP_ALIGNMENT);
		bottomPanel.add(leftPanel);
		
		/* showLinksPanel */
		JPanel showLinksPanel = new JPanel();
		showLinksPanel.setAlignmentX(CENTER_ALIGNMENT);
		showLinksPanel.setLayout(new BoxLayout(showLinksPanel, BoxLayout.Y_AXIS));
		showLinksPanel.setBorder(new TitledBorder(null, "Type of links:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		leftPanel.add(showLinksPanel);
		
		showLinksPanel.add(Box.createVerticalGlue());
		
		chckbxDownlinks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuDownlinks.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				updateShowingLinks();
			}
		});
		chckbxDownlinks.setSelected(true);
		showLinksPanel.add(chckbxDownlinks);
		
		showLinksPanel.add(Box.createVerticalGlue());
		
		chckbxUplinks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuUplinks.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				updateShowingLinks();
			}
		});
		chckbxUplinks.setSelected(true);
		showLinksPanel.add(chckbxUplinks);
		
		showLinksPanel.add(Box.createVerticalGlue());
		
		/* labelPanel */
		JPanel labelPanel = new JPanel();
		labelPanel.setAlignmentX(CENTER_ALIGNMENT);
		labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
		labelPanel.setBorder(new TitledBorder(null, "Labels:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		leftPanel.add(labelPanel);
		
		labelPanel.add(Box.createVerticalGlue());
		
		chckbxID.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuID.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				updateLabels();
			}
		});
		chckbxID.setSelected(true);
		labelPanel.add(chckbxID);
		
		labelPanel.add(Box.createVerticalGlue());
		
		chckbxChannel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuChannel.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				updateLabels();
			}
		});
		labelPanel.add(chckbxChannel);
		
		labelPanel.add(Box.createVerticalGlue());
		
		chckbxDatarate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuDatarate.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				updateLabels();
			}
		});
		chckbxDatarate.setEnabled(false);
		labelPanel.add(chckbxDatarate);
		
		labelPanel.add(Box.createVerticalGlue());
		
		bottomPanel.add(Box.createHorizontalGlue());
		
		/* displayPanel */
		JPanel displayPanel = new JPanel();
		displayPanel.setAlignmentY(TOP_ALIGNMENT);
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		displayPanel.setBorder(new TitledBorder(null, "Display:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		bottomPanel.add(displayPanel);
		
		displayPanel.add(Box.createVerticalGlue());
		
		
		JPanel colorPanel = new JPanel();
		colorPanel.setAlignmentY(TOP_ALIGNMENT);
		colorPanel.setLayout(new GridLayout(2, 2, 5, 5));
		displayPanel.add(colorPanel);
		
		rdbtnColorByLink.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuColorByLink.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				colorViewerDialog.updateContent(ColorViewer.ColorType.Links);
				updateStyle();
			}
		});
		rdbtnColorByLink.setSelected(true);
		colorPanel.add(rdbtnColorByLink);
		
		JButton buttonColorViewer = new JButton("Color viewer");
		buttonColorViewer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				colorViewerDialog.setVisible(true);
			}
		});
		colorPanel.add(buttonColorViewer);
		
		rdbtnColorByChannel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuColorByChannel.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				colorViewerDialog.updateContent(ColorViewer.ColorType.Channels);
				updateStyle();
			}
		});
		rdbtnColorByChannel.setSelected(false);
		colorPanel.add(rdbtnColorByChannel);	
		
		ButtonGroup groupColor = new ButtonGroup();
		groupColor.add(rdbtnColorByLink);
		groupColor.add(rdbtnColorByChannel);
		
		chckbxThickness.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuThickness.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				sliderThickness.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
				updateStyle();
			}
		});
		chckbxThickness.setEnabled(false);
		colorPanel.add(chckbxThickness);
		
		displayPanel.add(Box.createVerticalGlue());
		
		sliderLabels.setBorder(new TitledBorder(null, "Label size:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sliderLabels.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateStyle();
			}
		});
		sliderLabels.setMaximum(20);
		sliderLabels.setMinimum(5);
		sliderLabels.setValue(8);
		sliderLabels.setMajorTickSpacing(72);

		//Create the label table
		Hashtable<Integer, JLabel> labelTable1 = new Hashtable<Integer, JLabel>();
		JLabel smaller = new JLabel("Smaller");
		smaller.setFont(smaller.getFont().deriveFont(8.0F));
		labelTable1.put(sliderLabels.getMinimum(), smaller);
		JLabel bigger = new JLabel("Bigger");
		bigger.setFont(bigger.getFont().deriveFont(8.0F));
		labelTable1.put(sliderLabels.getMaximum(), bigger);
		sliderLabels.setLabelTable( labelTable1 );
		sliderLabels.setPaintLabels(true);
		
		displayPanel.add(sliderLabels);
		
		displayPanel.add(Box.createVerticalGlue());
		
		sliderThickness.setBorder(new TitledBorder(null, "Thickness factor:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sliderThickness.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateStyle();
			}
		});
		sliderThickness.setMaximum(100);
		sliderThickness.setMinimum(28);
		sliderThickness.setValue(60);
		sliderThickness.setEnabled(false);
		sliderThickness.setMajorTickSpacing(72);

		//Create the label table
		Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
		JLabel thiner = new JLabel("Thiner");
		thiner.setFont(thiner.getFont().deriveFont(8.0F));
		labelTable2.put(sliderThickness.getMinimum(), thiner);
		JLabel thicker = new JLabel("Thicker");
		thicker.setFont(thicker.getFont().deriveFont(8.0F));
		labelTable2.put(sliderThickness.getMaximum(), thicker);
		sliderThickness.setLabelTable( labelTable2 );
		sliderThickness.setPaintLabels(true);
		
		displayPanel.add(sliderThickness);
		
		displayPanel.add(Box.createVerticalGlue());
		
		bottomPanel.add(Box.createHorizontalGlue());
		
		/* configurationsPanel */
		JPanel configurationsPanel = new JPanel();
		configurationsPanel.setAlignmentY(TOP_ALIGNMENT);
		configurationsPanel.setLayout(new BoxLayout(configurationsPanel, BoxLayout.Y_AXIS));
		configurationsPanel.setBorder(new TitledBorder(null, "Show links:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		bottomPanel.add(configurationsPanel);
		
		Integer[] intList = mapConfigurations.keySet().toArray(new Integer[mapConfigurations.size()]);
		configList = new JComboBox<Integer>(intList);
		configList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateShowingLinks();
				updateLabels();
				updateStyle();
			}
		});
		configList.setEnabled(false);

		configurationsPanel.add(Box.createVerticalGlue());
		
		rdbtnAllLinks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuAllLinks.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				configList.setEnabled(false);
				menuListConfig.setEnabled(false);
				chckbxDatarate.setEnabled(false);
				menuDatarate.setEnabled(false);
				chckbxDatarate.setSelected(false);
				menuDatarate.setSelected(false);
				chckbxThickness.setEnabled(false);
				menuThickness.setEnabled(false);
				chckbxThickness.setSelected(false);
				menuThickness.setSelected(false);
				sliderThickness.setEnabled(false);
				mntmSaveAll.setEnabled(false);
				updateShowingLinks();
				updateLabels();
			}
		});
		rdbtnAllLinks.setSelected(true);
		rdbtnAllLinks.setAlignmentX(LEFT_ALIGNMENT);
		configurationsPanel.add(rdbtnAllLinks);
		
		configurationsPanel.add(Box.createVerticalGlue());

		rdbtnConfiguration.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				menuConfiguration.setSelected(e.getStateChange() == ItemEvent.SELECTED);
				configList.setEnabled(true);
				menuListConfig.setEnabled(true);
				chckbxDatarate.setEnabled(true);
				menuDatarate.setEnabled(true);
				chckbxDatarate.setSelected(false);
				menuDatarate.setSelected(false);
				chckbxThickness.setEnabled(true);
				menuThickness.setEnabled(true);
				chckbxThickness.setSelected(false);
				menuThickness.setSelected(false);
				mntmSaveAll.setEnabled(true);
				updateShowingLinks();
				updateLabels();
			}
		});
		rdbtnConfiguration.setSelected(false);
		configurationsPanel.add(rdbtnConfiguration);
		
		configurationsPanel.add(Box.createVerticalGlue());
		
		configList.setAlignmentX(LEFT_ALIGNMENT);
		configurationsPanel.add(configList);
		
		ButtonGroup groupConfig = new ButtonGroup();
		groupConfig.add(rdbtnAllLinks);
		groupConfig.add(rdbtnConfiguration);
		
		configurationsPanel.add(Box.createVerticalGlue());
		
		bottomPanel.add(Box.createHorizontalGlue());
	}

	/** Build the menu of the frame 
	 */
	protected void buildMenu() {
		
		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		
		/* File */
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('F');
		menuBar.add(mnFile);
		
		JMenuItem colorViewer = new JMenuItem("Color viewer");
		colorViewer.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_V, 0));
		colorViewer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				colorViewerDialog.setVisible(true);
			}
		});
		mnFile.add(colorViewer);
		
		JMenuItem throughputViewer = new JMenuItem("Throughput...");
		throughputViewer.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_T, 0));
		throughputViewer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				histogramViewerFrame.setVisible(true);
			}
		});
		mnFile.add(throughputViewer);
		
		mnFile.add(new JSeparator());
		
		JMenuItem mntmSave = new JMenuItem("Save the Graph as...");
		mntmSave.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Save16.gif")));
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveImage(graphComponent, "Graph");
			}
		});
		mnFile.add(mntmSave);
		
		mntmSaveAll.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/SaveAll16.gif")));
		mntmSaveAll.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.ALT_MASK));
		mntmSaveAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAllConfigurations();
			}
		});
		mntmSaveAll.setEnabled(false);
		mnFile.add(mntmSaveAll);
		
		mnFile.add(new JSeparator());
		
		JMenuItem mntmReset = new JMenuItem("Reset");
		mntmReset.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		mntmReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				colorViewerDialog.dispose();
				histogramViewerFrame.dispose();
				dispose();
				Program.restartApplication();
			}
		});
		mnFile.add(mntmReset);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnFile.add(mntmExit);

		/* Display */
		JMenu mnDisplay = new JMenu("Display");
		mnDisplay.setMnemonic('D');
		menuBar.add(mnDisplay);
		
		mnDisplay.add(new JLabel(" Type of Links:"));
		
		menuDownlinks.setSelected(this.chckbxDownlinks.isSelected());
		menuDownlinks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chckbxDownlinks.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuDownlinks);
		
		menuUplinks.setSelected(this.chckbxUplinks.isSelected());
		menuUplinks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chckbxUplinks.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuUplinks);
		
		mnDisplay.add(new JSeparator());
		
		mnDisplay.add(new JLabel(" Labels:"));
		
		menuID.setSelected(this.chckbxID.isSelected());
		menuID.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chckbxID.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuID);
		
		menuChannel.setSelected(this.chckbxChannel.isSelected());
		menuChannel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chckbxChannel.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuChannel);
		
		menuDatarate.setSelected(this.chckbxDatarate.isSelected());
		menuDatarate.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chckbxDatarate.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		menuDatarate.setEnabled(false);
		mnDisplay.add(menuDatarate);
		
		mnDisplay.add(new JSeparator());
		
		mnDisplay.add(new JLabel(" Display:"));
		
		menuColorByLink.setSelected(this.rdbtnColorByLink.isSelected());
		menuColorByLink.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				rdbtnColorByLink.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuColorByLink);
		
		menuColorByChannel.setSelected(this.rdbtnColorByChannel.isSelected());
		menuColorByChannel.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				rdbtnColorByChannel.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuColorByChannel);
		
		menuThickness.setSelected(this.chckbxThickness.isSelected());
		menuThickness.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				chckbxThickness.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuThickness);
		
		mnDisplay.add(new JSeparator());
		
		mnDisplay.add(new JLabel(" Show Links:"));
		
		menuAllLinks.setSelected(this.rdbtnAllLinks.isSelected());
		menuAllLinks.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				rdbtnAllLinks.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuAllLinks);
		
		menuConfiguration.setSelected(this.rdbtnConfiguration.isSelected());
		menuConfiguration.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				rdbtnConfiguration.setSelected(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
		mnDisplay.add(menuConfiguration);
		
		mnDisplay.add(menuListConfig);
		menuListConfig.setEnabled(this.configList.isEnabled());
		for(int i : GraphViewer.mapConfigurations.keySet()) {
			JMenuItem item = new JMenuItem(i+"");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int index = Integer.parseInt(((JMenuItem)e.getSource()).getText());
					configList.setSelectedIndex(index);
				}
			});
			menuListConfig.add(item);
		}
		
		/* View */
		JMenu mnView = new JMenu("View");
		mnView.setMnemonic('V');
		menuBar.add(mnView);
		
		JMenuItem zoomIn = new JMenuItem("Zoom in");
		zoomIn.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomIn16.gif")));
		zoomIn.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_ADD, 0));
		zoomIn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graphComponent.zoomIn();
			}
		});
		mnView.add(zoomIn);
		
		JMenuItem zoomOut = new JMenuItem("Zoom out");
		zoomOut.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomOut16.gif")));
		zoomOut.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_SUBTRACT, 0));
		zoomOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graphComponent.zoomOut();
			}
		});
		mnView.add(zoomOut);
		
		JLabel use = new JLabel(" (or use mouse wheel)");
		//use.setHorizontalAlignment(JLabel.RIGHT);
		use.setFont(use.getFont().deriveFont(Font.ITALIC, 10.0F));
		mnView.add(use);
		
		mnView.addSeparator();
		
		JMenuItem zoomFit = new JMenuItem("Fit in");
		zoomFit.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Zoom16.gif")));
		zoomFit.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F, 0));
		zoomFit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mxGraphView view = graphComponent.getGraph().getView();
				int compLen = graphComponent.getHeight()-17;
				int viewLen = (int)view.getGraphBounds().getHeight();
				view.setScale((double)compLen/viewLen * view.getScale());
				
				/*mxRectangle bounds = graph.getGraphBounds();
				double translateX = -bounds.getX()-(bounds.getWidth() - graphComponent.getWidth())/2;
				double translateY = -bounds.getY()-(bounds.getHeight() - graphComponent.getHeight()) /2;
				graph.getView().setTranslate(new mxPoint(translateX, translateY));*/
			}
		});
		mnView.add(zoomFit);
		
		JMenuItem zoomRst = new JMenuItem("Reset zoom");
		zoomRst.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Undo16.gif")));
		zoomRst.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_R, 0));
		zoomRst.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				graphComponent.zoomActual();
			}
		});
		mnView.add(zoomRst);
		
	}
	
	/** Get the data computed by the program and fill the graph object
	 */
	protected void getDataAndFillGraph() {
		this.graph.getModel().beginUpdate();
		try {
			fillMapConfigurations();
			fillMapNodes();
			fillMapEdges();
			System.out.println("#"+GraphViewer.mapVertices.size()+" vertices");
			System.out.println("#"+GraphViewer.mapConfigurations.size()+" transmission configurations");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.graph.getModel().endUpdate();
		}
	}
	
	/** Hide all the links showing
	 */
	protected void hideAllLinks() {
		for(int id : GraphViewer.mapEdges.keySet()) {
			this.graph.getModel().setVisible(GraphViewer.mapEdges.get(id).getEdge(), false);
		}
	}
	
	/** Show the links supposed to be shown and hide the others.
	 * (Based on the Checkboxes checked by the user)
	 */
	protected void updateShowingLinks() {
		hideAllLinks();
		for(int id : GraphViewer.mapEdges.keySet()) {
			Link l = GraphViewer.mapEdges.get(id);
			if((l.isDownlink() && this.chckbxDownlinks.isSelected()) ||
					(l.isUplink() && this.chckbxUplinks.isSelected())) {
				if(this.rdbtnAllLinks.isSelected()) {
					this.graph.getModel().setVisible(l.getEdge(), true);
				} else if(this.configList != null) {
					ArrayList<Integer> list = GraphViewer.mapConfigurations.get(this.configList.getSelectedIndex());
					if(list.contains(id)) {
						this.graph.getModel().setVisible(l.getEdge(), true);
					}
				}
			}
		}
	}

	/** Update the label of each showing link on the graph.
	 * (Based on the Checkboxes checked by the user)
	 */
	protected void updateLabels() {
		for(int id : GraphViewer.mapEdges.keySet()) {
			Link link = GraphViewer.mapEdges.get(id);
			mxCell edge = link.getEdge();
			boolean before = false;
			String label = "";
			if(chckbxID.isSelected()) {
				label += "#"+id;
				before = true;
			}
			if(chckbxChannel.isSelected()) {
				label += (before) ? "\nCh" : "Ch";
				label += link.getChannel();
				before = true;
			}
			if(chckbxDatarate.isSelected()) {
				label += (before) ? "\nR" : "R";
				// If the current Link is in the currently showing TC
				int TCindex = GraphViewer.mapConfigurations.get(this.configList.getSelectedIndex()).indexOf(id);
				if(TCindex != -1) {
					label += GraphViewer.mapDatarates.get(this.configList.getSelectedIndex()).get(TCindex);
				}
			}
			this.graph.getModel().setValue(edge, label);
		}
	}
	
	/** Update the style of the link and their label on the graph.
	 * (Based on the Checkboxes checked by the user)
	 */
	protected void updateStyle() {
		for(int id : GraphViewer.mapEdges.keySet()) {
			Link link = GraphViewer.mapEdges.get(id);
			mxCell edge = link.getEdge();
			String style = "fontSize="+this.sliderLabels.getValue()+";";
			if(this.rdbtnColorByLink.isSelected()) {
				if(link.isDownlink()) {
					style += DownlinkDefaultColorStyle;
				} else {
					style += UplinkDefaultColorStyle;
				}
			} else { // Color by Channel
				float hue = link.getChannel() * (1.0F / MaxChannelNumber);
				//Make the yellow colors darkers:
				float value = ((hue > 55F/360F) && (hue < 75F/360F)) ? 0.8F : 1.0F;
				Color color = Color.getHSBColor(hue, 1.0F, value);
				Color bColor = Color.getHSBColor(hue, 0.1F, 1.0F);
				String hexColorCode = String.format("#%02x%02x%02x",
						color.getRed(), color.getGreen(), color.getBlue());
				String hexbColorCode = String.format("#%02x%02x%02x",
						bColor.getRed(), bColor.getGreen(), bColor.getBlue());
				style += "strokeColor="+hexColorCode+
						";fontColor="+hexColorCode+
						";labelBackgroundColor="+hexbColorCode+";";
			}
			/* Epaisseur des liens en fonction du datarate */
			if(configList != null && this.chckbxThickness.isSelected()) {
				int TCindex = GraphViewer.mapConfigurations.get(this.configList.getSelectedIndex()).indexOf(id);
				float factor = (128.0F-this.sliderThickness.getValue())/10.0F;
				if(TCindex != -1) {
					style += "strokeWidth="+
							GraphViewer.mapDatarates.get(this.configList.getSelectedIndex()).get(TCindex)/factor+";";
				}
			}
			
			this.graph.getModel().setStyle(edge, style);
		}
	}
	
	/** Fill the map containing the edges, and add them to the graph
	 * @see GraphViewer#mapVertices
	 */
	protected void fillMapNodes() {
		for(int index : ApplicationSettingFacade.Router.getRouters().keySet()) {
			Point p = ApplicationSettingFacade.Router.getRouters().get(index).location;
			mxCell cell = (mxCell) this.graph.insertVertex(this.parent, ""+index, index,
					10+p.x, 10+p.y, 30, 30,	RouterDefaultStyle);
			GraphViewer.mapVertices.put(index, cell);
		}
		for(int index : ApplicationSettingFacade.Gateway.getGateway().keySet()) {
			Point p = ApplicationSettingFacade.Gateway.getGateway().get(index).location;
			mxCell cell = (mxCell) this.graph.insertVertex(this.parent, ""+index, index,
					10+p.x, 10+p.y, 30, 30,	GatewayDefaultStyle);
			GraphViewer.mapVertices.put(index, cell);
		}
	}	
	
	/** Fill the map containing the transmission configurations and
	 * the map containing the datarates
	 * @see GraphViewer#mapConfigurations
	 * @see GraphViewer#mapDatarates
	 */
	protected void fillMapConfigurations() {
		int tcuIndex = 0;
		for(TCUnit tcu : TCFacade.getConfigurations()) {
			ArrayList<Integer> tabConfig = new ArrayList<Integer>();
			ArrayList<Integer> tabRates = new ArrayList<Integer>();
			for(dataStructure.Link l : tcu.getLinks()) {
				tabConfig.add(l.getId());
				tabRates.add(tcu.getRate(l));
			}
			GraphViewer.mapConfigurations.put(tcuIndex, tabConfig);
			GraphViewer.mapDatarates.put(tcuIndex++, tabRates);
		}
	}
	
	/** Fill the map containing the edges, and add them to the graph
	 * @see GraphViewer#mapEdges
	 */
	protected void fillMapEdges() {
		HashSet<Integer> downlinkSet = new HashSet<Integer>();
		for(Entry<Vertex,List<Path>> allPath : TrafficEstimatingFacade.getDownlinkPath().entrySet()) {
			for(Path p : allPath.getValue()) {
				for (dataStructure.Link edge : p.getEdgePath()) {
					downlinkSet.add(edge.getId());
				}
			}			
		}
		for(dataStructure.Link l : TrafficEstimatingFacade.getOptimalLinks()) {
			int edgeIndex = l.getId();
			int sourceIndex = l.getSource().getId();
			int destinationIndex = l.getDestination().getId();
			boolean downlink = downlinkSet.contains(edgeIndex);
			String style = FontSizeDefaultStyle;
			if(downlink) {
				style += DownlinkDefaultColorStyle;
			} else {
				style += UplinkDefaultColorStyle;
			}
			mxCell cell = (mxCell) this.graph.insertEdge(this.parent, ""+(100*edgeIndex), "#"+edgeIndex,
					GraphViewer.mapVertices.get(sourceIndex), GraphViewer.mapVertices.get(destinationIndex),
					style);
			Link.Type type = (downlink) ? Link.Type.DOWNLINK : Link.Type.UPLINK;
			int channel = ChannelAssignmentFacade.getChannels().get(l).getChannel();
			GraphViewer.mapEdges.put(edgeIndex, new Link(cell, type, channel));
		}
	}	
	
	/** Save the <code>component</code> as an Image : JPEG, PNG or BMP.
	 * Show a save dialog, and handle the user request.
	 * @param component the component to save
	 * @param title the title for the save window (Save the ... as)
	 */
	public static void saveImage(Component component, String title) {
		BufferedImage im = new BufferedImage(component.getWidth(), component.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);
		component.paint(im.getGraphics());
		try {
			final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir")){
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
			fileChooser.setDialogTitle("Save the "+title+" as...");
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setAcceptAllFileFilterUsed(false);
			FileNameExtensionFilter filterJPG = new FileNameExtensionFilter(
			        "JPG Image", "jpg", "jpeg");
			FileNameExtensionFilter filterPNG = new FileNameExtensionFilter(
			        "PNG Image", "png");
			FileNameExtensionFilter filterBMP = new FileNameExtensionFilter(
			        "BMP Image", "bmp");
			fileChooser.addChoosableFileFilter(filterJPG);
			fileChooser.addChoosableFileFilter(filterPNG);
			fileChooser.addChoosableFileFilter(filterBMP);
			fileChooser.setFileFilter(filterPNG);
			int returnVal = fileChooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				String ext = null;
				File selectedFile = fileChooser.getSelectedFile();
				File file = null;
				switch(fileChooser.getFileFilter().getDescription()) {
				case "JPG Image":
					ext = "jpg";
					if(!selectedFile.getAbsolutePath().toLowerCase().endsWith(".jpg") &&
							!selectedFile.getAbsolutePath().toLowerCase().endsWith(".jpeg")) {
						file = new File(selectedFile.getAbsolutePath()+".jpg");
					} else {
						file = selectedFile;
					}
					break;
				case "PNG Image":
					ext = "png";
					if(!selectedFile.getAbsolutePath().toLowerCase().endsWith(".png")) {
						file = new File(selectedFile.getAbsolutePath()+".png");
					} else {
						file = selectedFile;
					}
					break;
				case "BMP Image":
					ext = "bmp";
					if(!selectedFile.getAbsolutePath().toLowerCase().endsWith(".bmp")) {
						file = new File(selectedFile.getAbsolutePath()+".bmp");
					} else {
						file = selectedFile;
					}
					break;
				}
                boolean imageWritten = ImageIO.write(im, ext, file);
                if(imageWritten) {
                	JOptionPane.showMessageDialog(null,
                			title+" has been saved to: \n"+file.getAbsolutePath(),
            			    "Image successfully saved",            			    
            			    JOptionPane.INFORMATION_MESSAGE);
                } else {
                	showErrorDialog("Error while saving image",
                			"Failed to write \n"+file.getAbsolutePath());
                }
			}
		} catch (Exception e) {
			showErrorDialog(e.getMessage());
		}
	}
	
	/** Save the graphComponent for all the configurations as an Image.
	 * Show a save dialog, and handle the user request. 
	 */
	protected void saveAllConfigurations() {
		int result = JOptionPane.showConfirmDialog(this,
        		"All the configurations will be saved\n"
        				+ "with the current display parameters.\n\n"
        				+ "Do you want to continue?",
        		"Saving all configurations",
        		JOptionPane.YES_NO_OPTION);
        switch(result){
            case JOptionPane.YES_OPTION:
            	try {
        			final JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        			fileChooser.setDialogTitle("Save the configurations as...");
        			fileChooser.setMultiSelectionEnabled(false);
        			fileChooser.setAcceptAllFileFilterUsed(false);
        			FileNameExtensionFilter filterJPG = new FileNameExtensionFilter(
        			        "JPG Image", "jpg", "jpeg");
        			FileNameExtensionFilter filterPNG = new FileNameExtensionFilter(
        			        "PNG Image", "png");
        			FileNameExtensionFilter filterBMP = new FileNameExtensionFilter(
        			        "BMP Image", "bmp");
        			fileChooser.addChoosableFileFilter(filterJPG);
        			fileChooser.addChoosableFileFilter(filterPNG);
        			fileChooser.addChoosableFileFilter(filterBMP);
        			fileChooser.setFileFilter(filterPNG);
        			int returnVal = fileChooser.showSaveDialog(this);
        			if (returnVal == JFileChooser.APPROVE_OPTION) {
        				boolean imageWritten = false;
        				int selectedIndex = this.configList.getSelectedIndex();
        				String ext = null;
        				for(int i = 0; i < GraphViewer.mapConfigurations.size(); i++) {
        					this.configList.setSelectedIndex(i);
	        				BufferedImage im = new BufferedImage(graphComponent.getWidth(), graphComponent.getHeight(),
	        						BufferedImage.TYPE_3BYTE_BGR);
	        				graphComponent.paint(im.getGraphics());
	        				File selectedFile = fileChooser.getSelectedFile();
	        				File file = null;
	        				switch(fileChooser.getFileFilter().getDescription()) {
	        				case "JPG Image":
	        					ext = "jpg";
	        					file = new File(selectedFile.getAbsolutePath()+i+".jpg");
	        					break;
	        				case "PNG Image":
	        					ext = "png";
	        					file = new File(selectedFile.getAbsolutePath()+i+".png");
	        					break;
	        				case "BMP Image":
	        					ext = "bmp";
	        					file = new File(selectedFile.getAbsolutePath()+i+".bmp");
	        					break;
	        				}
	                        imageWritten = ImageIO.write(im, ext, file);
        				}
        				this.configList.setSelectedIndex(selectedIndex);
                        if(imageWritten) {
                        	JOptionPane.showMessageDialog(null,
                    			    "All the configurations have been saved to: \n"+
                    			    fileChooser.getSelectedFile().getAbsolutePath()+
                    			    "[0-"+(GraphViewer.mapConfigurations.size()-1)+"]"+
                    			    "."+ext,
                    			    "Images successfully saved",            			    
                    			    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                        	showErrorDialog("Error while saving images",
                        			"Failed to write files\n"+
                        			fileChooser.getSelectedFile().getAbsolutePath()+
                        			"[0-"+(GraphViewer.mapConfigurations.size()-1)+"]"+
                    			    "."+ext);
                        }
        			}
        		} catch (Exception e) {
        			showErrorDialog(e.getMessage());
        		}
                break;
            case JOptionPane.NO_OPTION:
            case JOptionPane.CLOSED_OPTION:
                return;
        }
	}
	
	/** Display all the childs from the cell parent (console)
	 * @param parent the parent cell
	 * @param indent a String to indent from
	 */
	protected void displayModel(mxCell parent, String indent) {
		System.out.println(indent+parent.getValue()+"("+parent.getClass().getName()+")");
		int nbChilds = parent.getChildCount();
		indent = indent + "  ";
		for (int i=0; i<nbChilds ; i++) {
			displayModel((mxCell) parent.getChildAt(i), indent);
		}
	}
 
	/** Show an error panel with a default title and a given message
	 * @param message the error message
	 */
	public static void showErrorDialog(String message) {
		showErrorDialog("Error", message);
	}
	
	/** Show an error panel with the given title and message
	 * @param title the title of the error panel
	 * @param message the error message
	 */
	public static void showErrorDialog(String title, String message) {
		JOptionPane.showMessageDialog(null,
			    message,
			    title,
			    JOptionPane.ERROR_MESSAGE);
	}


}
