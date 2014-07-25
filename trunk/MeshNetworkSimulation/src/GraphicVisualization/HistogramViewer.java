package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.util.ShapeUtilities;

/**
 * @author Benjamin
 *
 */
public class HistogramViewer extends JFrame {

	private static final long	serialVersionUID	= 14167619475087338L;
	
	private HistogramViewer self;
	
	private JPanel contentPane;
	
	public Vector<Double> dataThroughput;
	
	public Vector<Double> dataSource;
	
	public Vector<Double> dataTransmit;
	
	private ChartPanel cPanel;
	
	private JFreeChart chart;
	
	public DefaultXYDataset datasetThroughput;
	
	public DefaultXYDataset datasetSource;
	
	public DefaultXYDataset datasetTransmit;
	
	private int samplesNumber;
	
	public int stepThroughput;
	
	public int stepSource;
	
	public int stepTransmit;
	
	public final static String[] colors = new String[] {"Blue","Red","Green", "Orange", "Pink", "Black"};
	
	public final static String[] shapes = new String[] {"Regular Cross", "Diagonal Cross", "Diamond", "Down Triangle", "Up Triangle"};

	/**
	 * Create the frame.
	 * @param throughputData 
	 */
	public HistogramViewer(Vector<Double> throughputData, Vector<Double> sourceData,
			Vector<Double> transmitData, int step) {
		this.self = this;		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		this.dataThroughput = throughputData;
		this.dataSource = sourceData;
		this.dataTransmit = transmitData;
		
		this.stepThroughput = step;
		this.stepSource = step;
		this.stepTransmit = step;
		
		drawGraph(throughputData, step, sourceData, step, transmitData, step);
		
		buildMenu();
		
		buildToolBar();
	}
	
	private void buildMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmSave = new JMenuItem("Save the plot as...");
		mntmSave.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Save16.gif")));
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphViewer.saveImage(cPanel, "Plot");
			}
		});
		mnFile.add(mntmSave);
		
		JMenuItem mntmCopy = (JMenuItem) cPanel.getPopupMenu().getComponent(2);
		mntmCopy.setText("Copy to clipboard");
		mntmCopy.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Copy16.gif")));
		mntmCopy.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		mnFile.add(mntmCopy);
		
		mnFile.addSeparator();
		
		mnFile.add(cPanel.getPopupMenu().getComponent(0));
		
		mnFile.addSeparator();
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		mnFile.add(mntmExit);
		
		JMenu mnDisplay = new JMenu("Display");
		menuBar.add(mnDisplay);

		mnDisplay.add(buildMenuDisplay("Throughput", 0));

		mnDisplay.add(buildMenuDisplay("Source Traffic", 1));

		mnDisplay.add(buildMenuDisplay("Transmit Traffic", 2));
		
		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);
		
		JMenu mnZoomIn = (JMenu) cPanel.getPopupMenu().getComponent(5);
		mnZoomIn.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomIn16.gif")));
		mnView.add(mnZoomIn);
		mnZoomIn.getItem(0).setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_ADD, 0));
		
		JMenu mnZoomOut = (JMenu) cPanel.getPopupMenu().getComponent(5);
		mnZoomOut.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/ZoomOut16.gif")));
		mnView.add(mnZoomOut);
		mnZoomOut.getItem(0).setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_SUBTRACT, 0));
		
		JLabel use = new JLabel(" (or use mouse wheel)");
		//use.setHorizontalAlignment(JLabel.RIGHT);
		use.setFont(use.getFont().deriveFont(Font.ITALIC, 10.0F));
		mnView.add(use);
		
		mnView.addSeparator();
		
		JMenu mnAutoRange = (JMenu) cPanel.getPopupMenu().getComponent(6);
		mnAutoRange.setIcon(new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Zoom16.gif")));
		mnView.add(mnAutoRange);
		mnAutoRange.getItem(0).setAccelerator(KeyStroke.getKeyStroke(
		        KeyEvent.VK_F, 0));
		
		cPanel.setPopupMenu(null);
	}
	
	private JMenu buildMenuDisplay(String target, int index) {
		JMenu menu = new JMenu(target);
		
		JMenuItem mnVisible= new JMenuItem("Set invisible");
		menu.add(mnVisible);
		mnVisible.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String t = ((JMenuItem) e.getSource()).getText();
				boolean toggle = (t.equals("Set visible") ? true : false);
				((JMenuItem) e.getSource()).setText((toggle ? "Set invisible" : "Set visible"));
				XYPlot plot = ((XYPlot) chart.getPlot());
				XYItemRenderer r = plot.getRenderer(index);
				r.setSeriesVisible(0, toggle);
				plot.setRenderer(index, r);
			}
		});
		
		JMenuItem mnStep = new JMenuItem("Set average interval");
		menu.add(mnStep);
		mnStep.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				String s = JOptionPane.showInputDialog(null,
						"Enter an integer for the average\n"+
						"interval of "+target,
						"Set average interval for "+ target,
						JOptionPane.QUESTION_MESSAGE);
				if(s == null) {
					return;
				}
				try {
					int result = Integer.parseInt(s);
					Field stepField;
					stepField = HistogramViewer.class.getField("step"+target.split(" ")[0]);
					Field dataField = HistogramViewer.class.getField("data"+target.split(" ")[0]);
					Field datasetField = HistogramViewer.class.getField("dataset"+target.split(" ")[0]);
					if(stepField.getInt(self) != result) {
						stepField.setInt(self, result);
						updateDataset((DefaultXYDataset)datasetField.get(self),
								target,
								(Vector<Double>) dataField.get(self),
								stepField.getInt(self));
					}
				} catch (NumberFormatException ex) {
					GraphViewer.showErrorDialog("Error "+ex.getClass(), ex.getClass()+": "+s+
							" isn't a valid integer");	
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
					GraphViewer.showErrorDialog("Error "+ex.getClass(), ex.getClass()+": "+ex.getMessage());	
				}
			}
		});
		
		JMenu mnShape = new JMenu("Shape");
		XYPlot plot = chart.getXYPlot();		
		XYSplineRenderer renderer = (XYSplineRenderer) plot.getRenderer(index);
		JMenuItem nullShape = new JMenuItem("No Shape");
		nullShape.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renderer.setSeriesShape(0, null);
				renderer.setSeriesShapesVisible(0, false);
				plot.setRenderer(index, renderer);
			}
		});
		mnShape.add(nullShape);
		for(int i = 0; i < shapes.length; i++) {
			JMenuItem shape = new JMenuItem(shapes[i]);
			shape.setActionCommand(shapes[i]);
			shape.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Shape s = null;
					switch (e.getActionCommand()) {
					case "Regular Cross":
						s = ShapeUtilities.createRegularCross(3.0F, 0.5F);
						break;
					case "Diagonal Cross" :
						s = ShapeUtilities.createDiagonalCross(3.0F, 0.5F);
						break;
					case "Diamond" :
						s = ShapeUtilities.createDiamond(3.0F);
						break;
					case "Down Triangle" :
						s = ShapeUtilities.createDownTriangle(3.0F);
						break;
					case "Up Triangle":
						s = ShapeUtilities.createUpTriangle(3.0F);
						break;
					}
					renderer.setSeriesShape(0, s);
					renderer.setSeriesShapesVisible(0, true);
					plot.setRenderer(index, renderer);
				}
			});
			mnShape.add(shape);
		}
		menu.add(mnShape);
		
		JMenu mnColor = new JMenu("Color");
		for(int i = 0; i < colors.length; i++) {
			JMenuItem color = new JMenuItem(colors[i]);
			color.setActionCommand(colors[i]);
			try {
				Field field = Color.class.getField(colors[i].toUpperCase());
				Color c = (Color)field.get(null);
				color.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						changeColor(index, c);
					}
				});
				color.setForeground(c);
			} catch (Exception e1) {}
			mnColor.add(color);
		}
		menu.add(mnColor);
		return menu;
	}
	
	private void buildToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		contentPane.add(toolBar, BorderLayout.SOUTH);
		
		JButton saveButton = new JButton(
				new ImageIcon(getClass().getResource("/toolbarButtonGraphics/general/Save24.gif")));
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphViewer.saveImage(cPanel, "Plot");
			}
		});
		toolBar.add(saveButton);
		
		toolBar.add(Box.createHorizontalGlue());
		
		JLabel lblSamples = new JLabel(samplesNumber+" samples.");
		toolBar.add(lblSamples);
	}
	
	private void drawGraph(Vector<Double> throughputData, int stepThroughput,
			Vector<Double> sourceData, int stepSource,
			Vector<Double> transmitData, int stepTransmit) {
		System.out.println("Collecting data...");
		
		samplesNumber = throughputData.size();

		/* Throughput data */
		datasetThroughput = new DefaultXYDataset();
		updateDataset(datasetThroughput, "Throughput", throughputData, stepThroughput);
		
		/* Source buffers data */
		datasetSource = new DefaultXYDataset();
		if(sourceData != null) {
			updateDataset(datasetSource, "Source Traffic", sourceData, stepSource);
		}
		
		/* Transmit buffers data */
		datasetTransmit = new DefaultXYDataset();
		if(transmitData != null) {
			updateDataset(datasetTransmit, "Transmit Traffic", transmitData, stepTransmit);
		}
		
		System.out.println("Finished, displaying...");
		
		/* Creating chart */
		chart = ChartFactory.createXYLineChart("Throughput "+GraphViewer.optionsTitle, "",
		"Throughput", datasetThroughput, PlotOrientation.VERTICAL, true, true, false);
		
		/* Plot properties */
		XYPlot plot = chart.getXYPlot();
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.DARK_GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.DARK_GRAY);
		
		/* Throughput renderer */
		XYSplineRenderer renderer0 = new XYSplineRenderer();
		renderer0.setSeriesShape(0, ShapeUtilities.createDiagonalCross(3.0F, 0.5F));
		plot.setRenderer(0, renderer0);
		changeColor(0, Color.BLUE);
		
		/* Source traffic plot and renderer */
		if(sourceData != null) {
			plot.setDataset(1, datasetSource);
			plot.mapDatasetToRangeAxis(1, 1);
			plot.setRangeAxis(1, new NumberAxis("Source Traffic"));
			
			XYSplineRenderer renderer1 = new XYSplineRenderer();
			renderer1.setSeriesShape(0, ShapeUtilities.createUpTriangle(3.0F));
			plot.setRenderer(1, renderer1);
			changeColor(1, Color.GREEN);
		}
		
		/* Transmit traffic plot and renderer */
		if(transmitData != null) {
			plot.setDataset(2, datasetTransmit);
			plot.mapDatasetToRangeAxis(2, 2);
			plot.setRangeAxis(2, new NumberAxis("Transmit Traffic"));
			
			XYSplineRenderer renderer2 = new XYSplineRenderer();
			renderer2.setSeriesShape(0, ShapeUtilities.createDownTriangle(3.0F));
			plot.setRenderer(2, renderer2);
			changeColor(2, Color.RED);
		}
		
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		
		cPanel = new ChartPanel(chart);
		cPanel.setMouseWheelEnabled(true);
		cPanel.setMouseZoomable(true);
		cPanel.setZoomInFactor(0.80);
		cPanel.setZoomOutFactor(1.25);
		contentPane.add(cPanel, BorderLayout.CENTER);
		this.revalidate();
		this.repaint();
	}
	
	private double[][] collectData(Vector<Double> data, int step) {
		int size = (int) Math.floor((double)data.size() / (double)step);
		double[][] dataArray = new double[2][size];
		int index = 0;
		double meanSum = 0;
		for(int i = 0; i < data.size(); i++) {
			meanSum += data.get(i);
			if(((i % step) == 0) && (i > 1)) {
				dataArray[0][index] = i - step/2;
				dataArray[1][index] = meanSum / step;
				meanSum = 0;
				index++;
			}
		}
		return dataArray;
	}
	
	private void updateDataset(DefaultXYDataset ds, String serieKey,
			Vector<Double> data, int step) {
		ds.removeSeries(serieKey);
		ds.addSeries(serieKey, collectData(data, step));
	}
	
	private void changeColor(int index, Color color) {
		XYPlot plot = chart.getXYPlot();
		XYSplineRenderer renderer = (XYSplineRenderer) plot.getRenderer(index);
		renderer.setSeriesPaint(0, color);
		plot.setRenderer(index,renderer);
		ValueAxis axis0 = plot.getRangeAxis(index);
		axis0.setAxisLinePaint(color);
		axis0.setLabelPaint(color);
		axis0.setTickLabelPaint(color);
		axis0.setTickMarkPaint(color);
		plot.setRangeAxis(index, axis0);
	}

}
