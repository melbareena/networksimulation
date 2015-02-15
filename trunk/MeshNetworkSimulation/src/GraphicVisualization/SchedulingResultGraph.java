package GraphicVisualization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import launcher.Program;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

import setting.ApplicationSettingFacade;
import trafficGenerator.DynamicTrafficGenerator;
import dataStructure.SchedulingResult;

public class SchedulingResultGraph extends JFrame
{

	/**
	 * 
	 */
	
	private SchedulingResult _sResult; 
	private static final long	serialVersionUID	= 1L;
	
	public final Marker endTraffic = new ValueMarker(ApplicationSettingFacade.Traffic.getDuration() / 50);
	public final ValueMarker offerLoad = new ValueMarker(DynamicTrafficGenerator.offerloadTraffic);
	
	public SchedulingResultGraph(SchedulingResult result)
	{
		super("Scheduling Result");
		_sResult = result;
		final XYDataset dataSet = createDataSet();
        final JFreeChart chart = createChart(dataSet);
        final ChartPanel chartPanel = new ChartPanel(chart);
        //chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
        setContentPane(chartPanel);
        showDiagram();
	}


	private void showDiagram()
	{
		
        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
		
	}


	private JFreeChart createChart(XYDataset dataSet)
	{
		String title = "Channels:"+Program.getAvailableChannels() + "," + "Topology: " + ApplicationSettingFacade.Nodes.getNodes().size()
				+ ",Power Control: " + ApplicationSettingFacade.PowerControl.isEnable() + ","				
				+ "TC: " + ApplicationSettingFacade.TranmissionConfiguration.getStertegy().name() + "\n"
				+ "Scheduling: " + _sResult.getSchedulingStrategy()
				;
		
		
		
		 final JFreeChart chart = ChartFactory.createXYLineChart(
				 	title,      // chart title
		                                
		            "Time Slots (s)",   // x axis label
		            "Average Throughput (Mbps)", // y axis label
		            dataSet,                  // data
		            PlotOrientation.VERTICAL,
		            true,                     // include legend
		            true,                     // tooltips
		            false                     // urls
		        );

		        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		        chart.setBackgroundPaint(Color.gray);

//		        final StandardLegend legend = (StandardLegend) chart.getLegend();
		  //      legend.setDisplaySeriesShapes(true);
		        
		        // get a reference to the plot for further customisation...
		        final XYPlot plot = chart.getXYPlot();
		        plot.setBackgroundPaint(Color.lightGray);
		        plot.setDomainGridlinePaint(Color.white);
		        plot.setRangeGridlinePaint(Color.white);
		        plot.setRangeGridlinesVisible(true);
		        
		        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

		        plot.setRenderer(renderer);

		        
		        endTraffic.setPaint(Color.MAGENTA);
		        
		        endTraffic.setStroke(new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		        		10.0F, new float[] {10, 10}, 0.0F));
		        endTraffic.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
		        endTraffic.setLabelTextAnchor(TextAnchor.TOP_LEFT);
		        endTraffic.setOutlinePaint(Color.ORANGE);
		        endTraffic.setLabelFont(endTraffic.getLabelFont().deriveFont(Font.BOLD, 12));
		        plot.addDomainMarker(endTraffic);
		        
		        offerLoad.setLabel("OfferLoad");
		        offerLoad.setPaint(Color.BLACK);
		        offerLoad.setStroke(new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
		        		10.0F, new float[] {10, 10}, 0.0F));
		        plot.addRangeMarker(offerLoad);
		        
		        
		        // change the auto tick unit selection to integer units only...
		        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		        // OPTIONAL CUSTOMISATION COMPLETED.
		                
		        return chart;
	}


	private XYDataset createDataSet()
	{
		//first throughput
		
		int timeIndex = 0;
		final XYSeries throughputSeries = new XYSeries("Average Throughput");
		
		for (Double t : _sResult.getThroughputData())
		{
			throughputSeries.add(timeIndex,t);
			timeIndex++;
		}

		final XYSeries sourceDataSeries = new XYSeries("Source Traffic");
		timeIndex = 0;
		for (Double t : _sResult.getSourceData())
		{
			sourceDataSeries.add(timeIndex,t);
			timeIndex++;
		}
		
		final XYSeries transmitDataSeries = new XYSeries("Transmit Traffic");
		timeIndex = 0;
		for (Double t : _sResult.getTransmitData())
		{
			transmitDataSeries.add(timeIndex,t);
			timeIndex++;
		}
		
		final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(throughputSeries);
        dataset.addSeries(sourceDataSeries);
        dataset.addSeries(transmitDataSeries);
                
        return dataset;
		
		
	}

}
