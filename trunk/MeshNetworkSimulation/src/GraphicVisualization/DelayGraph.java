package GraphicVisualization;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import luncher.Luncher;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;
import org.jfree.ui.VerticalAlignment;
import org.jfree.util.ShapeUtilities;

import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.AlgorithmMode;
import trafficGenerator.DTGFacade;
import dataStructure.SchedulingResult;

public class DelayGraph extends JFrame
{

	/**
	 * 
	 */
	
	private SchedulingResult _sResult; 
	private static final long	serialVersionUID	= 1L;
	public   Paint white = Color.black;
	public  Marker endTraffic = new ValueMarker(ApplicationSettingFacade.Traffic.getDuration() / 50);
	private  ChartPanel chartPanel;
	private  JPanel contentPane;
	private JFreeChart chart;
	public DelayGraph(SchedulingResult result)
	{
		super("Delay");
		_sResult = result;
	}

	public void createDiagram()
	{
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
		
		
		final XYDataset dataSet = createDataSet();
       chart = createChart(dataSet);
       chartPanel = new ChartPanel(chart);
       chartPanel.setPreferredSize(new java.awt.Dimension(1670	, 1045));
       contentPane.add(chartPanel, BorderLayout.CENTER);
       
       
       buildToolBar();
       showDiagram();
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
				GraphViewer.saveImage(contentPane, "Plot");
			}
		});
		toolBar.add(saveButton);
		
		toolBar.add(Box.createHorizontalGlue());
		
		JLabel lblInfos = new JLabel("<html><u>Total Traffic: </u>" + DTGFacade.getTotalTraffic() + "Mb"
				+ ",    <u>Offered Load: </u>" + DTGFacade.offeredLoad
				+ ",	<u>Average Throughpu: </u>"+ _sResult.getAverageThorughput() + " Mbps"
				+ ",	<u>Network Delay: </u> "+ _sResult.getNetworkDelay() +"s"
				+ ",	<u>Average Packet Delay: </u>" +_sResult.getAverageDelayOfPacket() + "ts</html>");
		lblInfos.setFont( new Font("Tahoma", 3 , 16));
		lblInfos.setHorizontalAlignment(JLabel.TRAILING);
		toolBar.add(lblInfos);
	}
	
	private void showDiagram()
	{ 
		
       this.pack();
       RefineryUtilities.centerFrameOnScreen(this);
       this.setVisible(true);
		
	}


	private JFreeChart createChart(XYDataset dataSet)
	{
		String title = "Channels:" + Luncher.getAvailableChannels() +  
						",Routers: " + ApplicationSettingFacade.Router.getSize() +
						",Gateways:" + ApplicationSettingFacade.Gateway.getSize() + 
						",Power Control: " + ApplicationSettingFacade.PowerControl.isEnable() +  		
						",TC:" + ApplicationSettingFacade.TranmissionConfiguration.getStertegy().name() + 
						",Scheduling: " + _sResult.getSchedulingStrategy() + 
						",Lambda(Min): " + ApplicationSettingFacade.Traffic.getLambdaMin() + 
						",Lambda(Max): " + ApplicationSettingFacade.Traffic.getLambdaMax()
				;
		if(ApplicationSettingFacade.getAlgorithmMode() == AlgorithmMode.Dynamic)
			title += ", Interval: " + ApplicationSettingFacade.getInterval() + "ts";
		
		
		  chart = ChartFactory.createXYLineChart(
				 	"",      // chart title
		                                
		            "Seconds (s)",   // x axis label
		            "Average Throughput (Mbps)", // y axis label
		            dataSet,                  // data
		            PlotOrientation.VERTICAL,
		            true,                     // include legend
		            true,                     // tooltips
		            false                     // urls
		        );

		  TextTitle titleT = new TextTitle(title, new Font("Tahoma", 0, 14),Color.BLACK,RectangleEdge.TOP,HorizontalAlignment.CENTER,VerticalAlignment.CENTER, new RectangleInsets(15, .45, .25, .45));
		 
		  chart.setTitle(titleT);
		        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		        chart.setBackgroundPaint(Color.GRAY);

		        final XYPlot plot = chart.getXYPlot();
		        

		       
		        
		        

		        final NumberAxis delayAxis = new NumberAxis("ms/ts");
		        
		       	
		       

		        
		        delayAxis.setRange(0, maxDelay + (maxDelay / 100)*10 );
		        System.out.println("Delay Max Range:" + maxDelay + (maxDelay / 100)*10);
		        plot.setRangeAxis(0,delayAxis);
		              

		        plot.mapDatasetToRangeAxis(1, 1);
		        plot.setBackgroundPaint(Color.LIGHT_GRAY);
		        plot.setDomainGridlinePaint(Color.WHITE);
		        plot.setRangeGridlinePaint(Color.WHITE);
		        plot.setRangeGridlinesVisible(true);
		        

			     
			    XYLineAndShapeRenderer render = new XYLineAndShapeRenderer();
			    render.setSeriesShape(0, ShapeUtilities.createRegularCross(5F, 1F));
			    render.setSeriesShapesVisible(0, false);
			    plot.setRenderer(0,render);
		        
		        changeColor(0, Color.BLACK);
		      
		        
		        endTraffic.setPaint(Color.MAGENTA);
		        endTraffic.setLabel("End Traffic");
		        endTraffic.setStroke(new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		        		10.0F, new float[] {10, 10}, 0.0F));
		        endTraffic.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
		        endTraffic.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		        endTraffic.setLabelFont(endTraffic.getLabelFont().deriveFont(Font.BOLD, 12));
		        endTraffic.setPaint(Color.MAGENTA);
		        plot.addDomainMarker(endTraffic);
		        

		      
		        
		        
		       
		                
		        return chart;
	}


	private void changeColor(int index, Color c)
	{	
			XYPlot plot = chart.getXYPlot();
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(index);
			renderer.setSeriesPaint(0, c);
			plot.setRenderer(index,renderer);
			ValueAxis axis0 = plot.getRangeAxis(index);
			axis0.setAxisLinePaint(c);
			axis0.setLabelPaint(c);
			axis0.setTickLabelPaint(c);
			axis0.setTickMarkPaint(c);
			plot.setRangeAxis(index, axis0);	
	}

	
	private double maxDelay  = 0;
	private XYDataset createDataSet()
	{
		final XYSeries delaySeries = new XYSeries("Delay");
		int timeIndex = 0;
		for (Double d : _sResult.averageDelayPerSecond())
		{
			if(d > maxDelay) maxDelay = d;
			delaySeries.add(timeIndex++, d);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
       dataset.addSeries(delaySeries);       
       return dataset;	
		
	}
}
