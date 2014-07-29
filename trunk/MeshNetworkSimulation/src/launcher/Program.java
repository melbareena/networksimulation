package launcher;

import java.io.File;
import java.lang.management.ManagementFactory;

import javax.swing.SwingWorker;
import javax.swing.UIManager;

import scheduling.RoundRobinSchedulingStrategy;
import scheduling.SchedulingStrategy;
import trafficGenerator.DynamicTrafficGenerator;
import GraphicVisualization.GraphViewer;
import GraphicVisualization.LoadingDialog;
import GraphicVisualization.StartOptionsDialog;

public class Program {
	
	public static LoadingDialog loadingDialog = new LoadingDialog(null, "simulation", false);
	
	public static void restartApplication() {
		try {
			StringBuilder cmd = new StringBuilder();
	        cmd.append(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java ");
	        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
	            cmd.append(jvmArg + " ");
	        }
	        cmd.append("-cp ").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append(" ");
	        cmd.append(Program.class.getName()).append(" ");
	        /*for (String arg : args) {
	            cmd.append(arg).append(" ");
	        }*/
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
	
	public static void launch(boolean dynamic) {
		DynamicTrafficGenerator dtg = new DynamicTrafficGenerator();
		final SchedulingStrategy s = new RoundRobinSchedulingStrategy(dtg);
	
		loadingDialog.setVisible(true);
		try {
			if(dynamic) {
				SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {
					@Override
					protected Object doInBackground() throws Exception {
						s.dynamicScheduling();
						Program.loadingDialog.setIndeterminate(true);
						Program.loadingDialog.setLabel("Building user interface...");
						new GraphViewer(s.getResults());
						return null;
					}
				};
				worker.execute();
			} else {
				SwingWorker<Object, String> worker = new SwingWorker<Object, String>() {
					@Override
					protected Object doInBackground() throws Exception {
						s.scheduling();
						Program.loadingDialog.setIndeterminate(true);
						Program.loadingDialog.setLabel("Building user interface...");
						new GraphViewer(s.getResults());
						return null;
					}
				};
				worker.execute();
			}
		} catch(Exception e) {
			GraphViewer.showErrorDialog(e.getClass().toString(),
					e.getClass().toGenericString()+": "+e.getMessage().toString());
			e.printStackTrace(System.err);
		}

		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to set the UI look and feel...");
		}	

	}

}
