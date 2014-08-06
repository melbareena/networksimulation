package launcher;

import java.io.File;
import java.lang.management.ManagementFactory;

import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.multi.MultiInternalFrameUI;

import common.PrintConsole;
import scheduling.SchedulingFacade;
import setting.ApplicationSettingFacade;
import setting.BaseConfiguration.AppExecMode;
import GraphicVisualization.GraphViewer;
import GraphicVisualization.LoadingDialog;
import GraphicVisualization.StartOptionsDialog;
import dataStructure.SchedulingResult;

public class Program {

	public static LoadingDialog loadingDialog = new LoadingDialog(null,
			"simulation", false);
	
	
	private static String getAvailableChannels()
	{
		if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single)
		{
			return ApplicationSettingFacade.Channel.getChannelMode().name();
		}
		
		return "1.." + multiExecIndex;
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
			/*
			 * for (String arg : args) { cmd.append(arg).append(" "); }
			 */
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
	public static int multiExecIndex = 1;
	public static void launch() {
		loadingDialog.setVisible(true);
		try {
			SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {
				@Override
				protected Object doInBackground() throws Exception {
					
					if(ApplicationSettingFacade.getApplicationExecutionMode() == AppExecMode.Single)
						singleMode();
					else
						multiMode();
					
					return null;
				}

				private void multiMode()
				{
					PrintConsole.print("********************** Application is in multi execution mode ************************");
					for(multiExecIndex = 1 ; multiExecIndex < 12; multiExecIndex++ )
					{
						PrintConsole.print("Exceute Number : " + multiExecIndex);
						SchedulingResult result = SchedulingFacade.getScheduling();
						Program.loadingDialog.setIndeterminate(true);
						Program.loadingDialog
							.setLabel("Building user interface...");
						new GraphViewer(result, getAvailableChannels());
					}
					
				}

				

				private void singleMode()
				{
					PrintConsole.print("********************** Application is in single execution mode ************************");
					SchedulingResult result = SchedulingFacade.getScheduling();
					Program.loadingDialog.setIndeterminate(true);
					Program.loadingDialog
							.setLabel("Building user interface...");
					new GraphViewer(result , getAvailableChannels());
				}
			};
			worker.execute();
		} catch (Exception e) {
			GraphViewer.showErrorDialog(e.getClass().toString(), e.getClass()
					.toString() + ": " + e.getMessage().toString());
			e.printStackTrace(System.err);
		}

		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set the UI look and feel...");
		}

	}

}
