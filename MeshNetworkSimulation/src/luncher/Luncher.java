package luncher;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.UIManager;

import common.FileGenerator;
import common.PrintConsole;
import scheduling.SchedulingFacade;
import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.AppExecMode;
import transConf.TCFacade;
import GraphicVisualization.DelayGraph;
import GraphicVisualization.GraphViewer;
import GraphicVisualization.LoadingDialog;
import GraphicVisualization.SchedulingResultGraph;
import GraphicVisualization.StartOptionsDialog;
import dataStructure.Channel;
import dataStructure.SchedulingResult;
import dataStructure.TCUnit;

public class Luncher {

	public static LoadingDialog loadingDialog;
	
	public static int multiExecIndex = 1;

	private static int numberOfExecution = 1;
	
	/**
	 * @return A string containing the current available channels,
	 * depending on the current instance index of the program.
	 */
	public static String getAvailableChannels()
	{
		if (ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single)
		{
			return ApplicationSettingFacade.Channel.getChannelMode().name();
		}
		if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.AllCombination)
		{
			numberOfExecution = 12;
			if(multiExecIndex != 12)
				return "1.." + multiExecIndex;
			return "1,6,11";
		}
		String str = "";
		List<Channel> channels = ApplicationSettingFacade.Channel.getChannel();
		numberOfExecution = 11;
		for (Channel channel : channels) {
		 	str += channel.getChannel() + ",";
		}
		str = str.substring(0, str.length() - 1 );
		return str;
	}

	/**Ends and restarts the application in the JVM.
	 */
	public static void restartApplication() {
		try {
			StringBuilder cmd = new StringBuilder();
			cmd.append(System.getProperty("java.home") + File.separator + "bin"
					+ File.separator + "java ");
			for (String jvmArg : ManagementFactory.getRuntimeMXBean()
					.getInputArguments()) {
				cmd.append(jvmArg + " ");
			}
			cmd.append("-cp ")
					.append(ManagementFactory.getRuntimeMXBean().getClassPath())
					.append(" ");
			cmd.append(Luncher.class.getName()).append(" ");
			Runtime.getRuntime().exec(cmd.toString());
			System.exit(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**Show the options dialog for the user to select the program parameters.
	 * @param args -
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception 
	{
		/*
		DynamicBP dp = new DynamicBP(0);
		SchedulingResult sr =	dp.dynamicScheduling(ApplicationSettingFacade.Traffic.getDuration());
		FileGenerator.SchedulingResult(sr);
		SchedulingResultGraph g = new SchedulingResultGraph(sr);
		g.createDiagram();
		//TrafficEstimatingFacade.getLinksTraffic(0, 500, new BufferMap(), new BufferMap());
		//TrafficEstimatingFacade.getLinksTraffic(501, 1, new BufferMap(), new BufferMap());
		*/
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			StartOptionsDialog startOptionDialog = new StartOptionsDialog();
			startOptionDialog.setVisible(true);
		}
		catch (Exception e)
		{
			System.err.println("Unable to set the UI look and feel...");
			System.err.println(e.getMessage());
			System.err.println(e.getStackTrace());
			System.exit(0);
		}
	}

	static List<SchedulingResult> _finalResults = new ArrayList<SchedulingResult>();
	/**Launch the program with the selected parameters.
	 */
	public static void launch()
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e)
		{
			System.err.println("Unable to set the UI look and feel...");
		}
		
		getAvailableChannels();
		System.err.println("Power control is: " + ApplicationSettingFacade.PowerControl.isEnable());
		loadingDialog = new LoadingDialog();
		loadingDialog.setVisible(true);
		int nbBars = numberOfExecution;
		
		for(int i = 1; i < nbBars; i++) {
			Luncher.loadingDialog.addBar();
		}
		
		try {
			SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {
				List<Integer> samplesList = new ArrayList<Integer>();
				List<Double> throughputList = new ArrayList<Double>();
				List<Integer> delaysList = new ArrayList<Integer>();
				
				@Override
				protected Object doInBackground() throws Exception {
					if (ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single) {
						singleMode();
					} else {
						multiMode();
					}
					return null;
				}

				private void multiMode() 
				{
					PrintConsole.print("********************** Application is in multi execution mode ************************");
			
					for (multiExecIndex = 1; multiExecIndex <= numberOfExecution; multiExecIndex++) {
						final int index = multiExecIndex;
						PrintConsole.print("Exceute Number : " + index);
						SchedulingResult result = SchedulingFacade.getScheduling(index-1);
						_finalResults.add(result);
						Luncher.loadingDialog.setIndeterminate(index-1, true);
						Luncher.loadingDialog.setLabel(index-1, "Building user interface...");
						int step = result.getThroughputData().size()/100;
						if(step == 0 ) step = 1;
						createDiagrams(result);
						Luncher.loadingDialog.setProgress(index-1, 100, "Done!");
						
						samplesList.add(result.getThroughputData().size());
						delaysList.add((int) Math.round(result.getAverageDelayOfPacket()));
					}
				}

				

				private void singleMode() {
					PrintConsole.print("********************** Application is in single execution mode ************************");
					long startTime = System.currentTimeMillis();
					//SchedulingResult result = SchedulingFacade.getScheduling(0);
					
					SchedulingResult result = null;
					result = SchedulingFacade.getScheduling(0);
					
					
					FileGenerator.SchedulingResult(result);
					
					System.out.println(getAverageTCCapacity());
					
					long stopTime = System.currentTimeMillis();
				    long elapsedTime = stopTime - startTime;
				    System.out.println("---------------------------Execution Time:" + elapsedTime);
					Luncher.loadingDialog.setIndeterminate(0, true);
					Luncher.loadingDialog.setLabel(0, "Building user interface...");
					new GraphViewer(result, getAvailableChannels(), 0);
				}
				
				@Override
				protected void done() {
					super.done();
					Luncher.loadingDialog.dispose();
					Luncher.loadingDialog.setVisible(false);
					if(_finalResults.size() > 0)
					{
						
							FileGenerator.seceduleResult(_finalResults.toArray(new SchedulingResult[0]));
					}
				}

			};
			worker.execute();
			
		}
		catch (Exception e) 
		{
			GraphViewer.showErrorDialog(e.getClass().toString(), e.getClass()
					.toString() + ": " + e.getMessage().toString());
			e.printStackTrace(System.err);
			System.exit(0);
		}

	}
	
	private static double getAverageTCCapacity()
	{
		double sum = 0;
		double counter = 0;
		double average = 0;
		for (List<TCUnit> tcCollection : TCFacade._all)
		{
			sum = 0;
			counter ++;
			for (TCUnit tc : tcCollection)
			{
				sum+= tc.getTCAP();
			}
			average += sum / tcCollection.size();
		}
		return  average/counter;
		
	}
	private static void createDiagrams(SchedulingResult result)
	{
		SchedulingResultGraph g = new SchedulingResultGraph(result);
		g.createDiagram();
		DelayGraph delayG = new DelayGraph(result);
		delayG.createDiagram();
	}

}
