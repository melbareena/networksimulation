package GraphicVisualization;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
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

public class SchedulingResultGraph extends JFrame
{

	/**
	 * 
	 */
	
	private SchedulingResult _sResult; 
	private static final long	serialVersionUID	= 1L;
	public   Paint white = Color.black;
	public  Marker endTraffic = new ValueMarker(ApplicationSettingFacade.Traffic.getDuration() / 50);
	public  ValueMarker offerLoad = new ValueMarker(DTGFacade.offeredLoad);
	private  ChartPanel chartPanel;
	private  JPanel contentPane;
	private JFreeChart chart;
	public SchedulingResultGraph(SchedulingResult result)
	{
		super("Scheduling Result");
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
        chartPanel.setPreferredSize(new java.awt.Dimension(1670	, 980));
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
		String title = "Channels:" +Luncher.getAvailableChannels() +  
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
		        chart.setBackgroundPaint(Color.white);

		        final XYPlot plot = chart.getXYPlot();
		        
		        plot.setDataset(1,createSourceDataSet());
		        plot.setDataset(2,createTransmitDataSet());
		       // plot.setDataset(3, createDelayAverage());
		       
		        
		        
		        final NumberAxis transmitAxis = new NumberAxis("Mega bits");  
		        final NumberAxis sourceAxis = new NumberAxis("Mega bits");
		       // final NumberAxis delayAxis = new NumberAxis("ms/ts");
		        
		       	
		       
		        
		       	
		       	
		       	ValueAxis thAxis = plot.getRangeAxis(0);
		        thAxis.setRange(0, getMaxRange());
		        
		        
		        sourceAxis.setRange(0, getMaxRange());
		        plot.setRangeAxis(1, sourceAxis);
		        
		        transmitAxis.setRange(0, getMaxRange());
		        plot.setRangeAxis(2, transmitAxis);
		        
		      //  delayAxis.setRange(0, getMaxRange());
		       // plot.setRangeAxis(3,delayAxis);
		        
		        
	              
		        
		       // plot.setRangeAxis(2, bufferAxis);
		        plot.mapDatasetToRangeAxis(1, 1);
		        plot.setBackgroundPaint(Color.lightGray);
		        plot.setDomainGridlinePaint(Color.white);
		        plot.setRangeGridlinePaint(Color.white);
		        plot.setRangeGridlinesVisible(true);
		        
		        XYLineAndShapeRenderer rendererTh = new XYLineAndShapeRenderer();		
		        rendererTh.setSeriesShape(0, ShapeUtilities.createDiagonalCross(3.0F, 0.5F));
				plot.setRenderer(0, rendererTh);
		        
				XYLineAndShapeRenderer rendererSou = new XYLineAndShapeRenderer();		
		        rendererSou.setSeriesShape(1, ShapeUtilities.createUpTriangle(3.0F));
				plot.setRenderer(1, rendererSou);
				
				XYLineAndShapeRenderer rendererTrans = new XYLineAndShapeRenderer();		
				 rendererTrans.setSeriesShape(2, ShapeUtilities.createDownTriangle(3.0F));
			     plot.setRenderer(2, rendererTrans);
			     
			  //   XYSplineRenderer dotRender = new XYSplineRenderer();
			  //   dotRender.setSeriesShape(3, ShapeUtilities.createUpTriangle(5F));
			 //    dotRender.setSeriesLinesVisible(3, false);
			 //    plot.setRenderer(3,dotRender);
		        
		        changeColor(0, Color. BLUE);
		        changeColor(1, Color.GREEN);
		        changeColor(2, Color.RED);
		    //    changeColor(3, Color.BLACK);
		        
		        endTraffic.setPaint(Color.MAGENTA);
		        endTraffic.setLabel("End Traffic");
		        endTraffic.setStroke(new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		        		10.0F, new float[] {10, 10}, 0.0F));
		        endTraffic.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
		        endTraffic.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
		        endTraffic.setLabelFont(endTraffic.getLabelFont().deriveFont(Font.BOLD, 12));
		        endTraffic.setPaint(Color.MAGENTA);
		        plot.addDomainMarker(endTraffic);
		        

		        offerLoad.setPaint(Color.BLACK);
		        offerLoad.setStroke(new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		        		10.0F, new float[] {10, 10}, 0.0F));
		        offerLoad.setLabel("Offered Load:" + DTGFacade.offeredLoad + " Mbps");
		        offerLoad.setLabelAnchor(RectangleAnchor.BOTTOM_LEFT);
		        offerLoad.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		        offerLoad.setLabelFont(offerLoad.getLabelFont().deriveFont(Font.BOLD, 12));
		        plot.addRangeMarker(offerLoad);
		        
		        
		       
		                
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

	
	private double maxThroughput  = 0;
	private XYDataset createDataSet()
	{
		//first throughput
		
		int timeIndex = 0;
		final XYSeries throughputSeries = new XYSeries("Average Throughput");
		
		for (Double t : _sResult.getThroughputData())
		{
			if(t> maxThroughput)
				maxThroughput = t;
			throughputSeries.add(timeIndex,t);
			timeIndex++;
		}	
		
	/*	*/
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(throughputSeries);
       // dataset.addSeries(sourceDataSeries);
        //dataset.addSeries(transmitDataSeries);
                
        return dataset;
		
		
	}
	private double maxSource = 0;
	private XYDataset createSourceDataSet()
	{
		final XYSeries sourceDataSeries = new XYSeries("Source Traffic");
		int timeIndex = 0;
		for (Double t : _sResult.getSourceData())
		{
			if(t > maxSource) maxSource = t;
			sourceDataSeries.add(timeIndex,t);
			timeIndex++;
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(sourceDataSeries);
                
        return dataset;
	
	
	}
	
	private double maxTransmit  = 0 ;
	private XYDataset createTransmitDataSet()
	{

		final XYSeries transmitDataSeries = new XYSeries("Transmit Traffic");
		int timeIndex = 0;
		for (Double t : _sResult.getTransmitData())
		{
			if(t> maxTransmit) maxTransmit = t;
			transmitDataSeries.add(timeIndex,t);
			timeIndex++;
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(transmitDataSeries);       
        return dataset;
	
	
	}
	
	/*private double maxDelay  = 0;
	private XYDataset createDelayAverage()
	{
		final XYSeries delaySeries = new XYSeries("Delay");
		int timeIndex = 0;
		for (Double d : _sResult.delayPerSecond())
		{
			if(d > maxDelay) maxDelay = d;
			delaySeries.add(timeIndex++, d);
		}
		final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(delaySeries);       
        return dataset;
	}*/
	private double getMaxRange()
	{
		double maxSeries =  getMax() + (getMax() / 10);
		System.err.println("Max: " + maxSeries);
		return maxSeries;
		
		/*return 57.70;*/
	}
	private double getMax()
	{
		List<Double> list = new ArrayList<Double>();
		//list.add(maxDelay);
		list.add(maxSource);
		list.add(maxThroughput);
		list.add(maxTransmit);
		return Collections.max(list );
		
	}

}
