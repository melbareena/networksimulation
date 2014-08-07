package launcher;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.UIManager;

import common.PrintConsole;
import scheduling.SchedulingFacade;
import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.AppExecMode;
import GraphicVisualization.GraphViewer;
import GraphicVisualization.HistogramViewer;
import GraphicVisualization.LoadingDialog;
import GraphicVisualization.StartOptionsDialog;
import dataStructure.Channel;
import dataStructure.SchedulingResult;

public class Program {

	public static LoadingDialog loadingDialog;
	
	public static int multiExecIndex = 1;

	private static int numberOfExecution = 1;
	
	public static String getAvailableChannels() {
		if (ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single) {
			return ApplicationSettingFacade.Channel.getChannelMode().name();
		}
		if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.AllCombination) {
			numberOfExecution = 12;
			if(multiExecIndex != 12)
				return "1.." + multiExecIndex;
			return "1,6,11";
		}
		String str = "";
		List<Channel> channels = ApplicationSettingFacade.Channel.getChannel();
		numberOfExecution = 1;
		for (Channel channel : channels) {
		 	str += channel.getChannel() + ",";
		}
		str = str.substring(0, str.length() - 1 );
		return str;
	}

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
			cmd.append(Program.class.getName()).append(" ");
			Runtime.getRuntime().exec(cmd.toString());
			System.exit(0);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set the UI look and feel...");
		}
		StartOptionsDialog startOptionDialog = new StartOptionsDialog();
		startOptionDialog.setVisible(true);
	}

	public static void launch() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set the UI look and feel...");
		}
		
		getAvailableChannels();
		
		loadingDialog = new LoadingDialog();
		loadingDialog.setVisible(true);
		int nbBars = 1;
		if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.AllCombination) {
			nbBars = 12;
		} else if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.ApartCombination) {
			nbBars = 11;
		}
		for(int i = 1; i < nbBars; i++) {
			Program.loadingDialog.addBar();
		}
		
		try {
			SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {
				@Override
				protected Object doInBackground() throws Exception {
					if (ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single) {
						singleMode();
					} else {
						multiMode();
					}
					return null;
				}

				private void multiMode() {
					PrintConsole.print("********************** Application is in multi execution mode ************************");
					for (multiExecIndex = 1; multiExecIndex <= numberOfExecution; multiExecIndex++) {
						final int index = multiExecIndex;
						PrintConsole.print("Exceute Number : " + index);
						SchedulingResult result = SchedulingFacade.getScheduling(index-1);
						Program.loadingDialog.setIndeterminate(index-1, true);
						Program.loadingDialog.setLabel(index-1, "Building user interface...");
						int step = result.getThroughputData().size()/100;
						HistogramViewer histogramViewerFrame = new HistogramViewer(result, step);
						histogramViewerFrame.showGraph();
						histogramViewerFrame.setVisible(true);
						Program.loadingDialog.addProgress(index, 100 - Program.loadingDialog.getProgress(index),
								"Done!");
					}

				}

				private void singleMode() {
					PrintConsole.print("********************** Application is in single execution mode ************************");
					SchedulingResult result = SchedulingFacade.getScheduling(0);
					Program.loadingDialog.setIndeterminate(0, true);
					Program.loadingDialog.setLabel(0, "Building user interface...");
					new GraphViewer(result, getAvailableChannels(), 0);
				}

			};
			worker.execute();
		} catch (Exception e) {
			GraphViewer.showErrorDialog(e.getClass().toString(), e.getClass()
					.toString() + ": " + e.getMessage().toString());
			e.printStackTrace(System.err);
		}

	}

}
