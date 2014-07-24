package GraphicVisualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author Benjamin
 *
 */
public class HistogramViewer extends JFrame {

	private static final long	serialVersionUID	= 14167619475087338L;
	
	private JPanel	contentPane;
	
	private ChartPanel cPanel;
	
	private JFreeChart chart;
	
	private int samplesNumber;
	
	public static String[] colors = new String[] {"Blue","Red","Green", "Orange", "Pink", "Black"};

	/**
	 * Create the frame.
	 * @param throughputData 
	 */
	public HistogramViewer(Vector<Double> throughputData, Vector<Double> sourceData,
			Vector<Double> transmitData, int step) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		drawGraph(throughputData, sourceData, transmitData, step);
		
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
		
		JMenu mnColor = new JMenu("Color");
		mnDisplay.add(mnColor);
		for(int i = 0; i < colors.length; i++) {
			JMenuItem color = new JMenuItem(colors[i]);
			color.setActionCommand(colors[i]);
			color.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					XYPlot plot = chart.getXYPlot();		
					XYItemRenderer renderer = plot.getRenderer();
					Field field;
					try {
						field = Color.class.getField(e.getActionCommand().toUpperCase());
						Color color = (Color)field.get(null);
						renderer.setSeriesPaint(0, color);
						plot.setRenderer(renderer);
					} catch (Exception e1) {}
				}
			});
			mnColor.add(color);
		}
		
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
	
	private void drawGraph(Vector<Double> throughputData, Vector<Double> sourceData,
			Vector<Double> transmitData, int step) {
		System.out.println("Collecting data...");
		
		XYSeriesCollection c = new XYSeriesCollection();
		/*TODO convertir en dataset pour afficher avec axes differents
		 * http://www.java2s.com/Code/Java/Chart/JFreeChartDualAxisDemo.htm
		 * */
		XYSeries throughputDataset = new XYSeries("Throughput");
		int index = 0;
		for(int i = 0; i < throughputData.size(); i += step) {
			throughputDataset.add(i, throughputData.get(i));
			index++;
		}
		c.addSeries(throughputDataset);
		
		if(sourceData != null) {
			XYSeries sourceDataset = new XYSeries("Source Buffer Traffic");
			index = 0;
			for(int i = 0; i < sourceData.size(); i += step) {
				sourceDataset.add(i, sourceData.get(i));
				index++;
			}
			c.addSeries(sourceDataset);
		}
		
		if(transmitData != null) {
			XYSeries transmitDataset = new XYSeries("Transmit Buffer Traffic");
			index = 0;
			for(int i = 0; i < transmitData.size(); i += step) {
				transmitDataset.add(i, transmitData.get(i));
				index++;
			}
			c.addSeries(transmitDataset);
		}

		samplesNumber = throughputData.size();
		
		System.out.println("Finished (max index "+index+"), displaying...");
		 
		chart = ChartFactory.createXYLineChart("Throughput "+GraphViewer.optionsTitle, "",
		"Throughput", c, PlotOrientation.VERTICAL, false, true, false);
		
		XYPlot plot = chart.getXYPlot();
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.DARK_GRAY);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.DARK_GRAY);
		plot.setRangeAxis(1, new NumberAxis("Source"));

		plot.mapDatasetToRangeAxis(1, 1);
		
		XYItemRenderer renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, Color.BLUE);
		
		plot.setRenderer(renderer);
		
		cPanel = new ChartPanel(chart);
		cPanel.setMouseWheelEnabled(true);
		cPanel.setMouseZoomable(true);
		cPanel.setZoomInFactor(0.80);
		cPanel.setZoomOutFactor(1.25);
		contentPane.add(cPanel, BorderLayout.CENTER);		
	}

}
