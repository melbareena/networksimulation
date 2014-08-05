package launcher;

import java.io.File;
import java.lang.management.ManagementFactory;

import javax.swing.SwingWorker;
import javax.swing.UIManager;

import scheduling.SchedulingFacade;
import GraphicVisualization.GraphViewer;
import GraphicVisualization.LoadingDialog;
import GraphicVisualization.StartOptionsDialog;
import dataStructure.SchedulingResult;

public class Program {

	public static LoadingDialog loadingDialog = new LoadingDialog(null,
			"simulation", false);

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

	public static void launch() {
		loadingDialog.setVisible(true);
		try {
			SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {
				@Override
				protected Object doInBackground() throws Exception {
					SchedulingResult result = SchedulingFacade.getScheduling();
					Program.loadingDialog.setIndeterminate(true);
					Program.loadingDialog
							.setLabel("Building user interface...");
					new GraphViewer(result);
					return null;
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
