package GraphicVisualization.editDialog;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import GraphicVisualization.StartOptionsDialog;

public class SINREditOptionDialog extends AbstractEditOptionDialog {

	private static final long serialVersionUID = 1933222462031328266L;

	private JPanel centerPanel;
	
	private JSpinner alphaSpinner;
	
	private JSpinner WSpinner;
	
	private JSpinner powerSpinner;
	
	private JSpinner betaSpinner;
	
	private JSpinner muSpinner;

	public SINREditOptionDialog(StartOptionsDialog parent) {
		super(parent, "SINR edit");
		
		centerPanel = new JPanel();
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new MigLayout("", "[min!]25[grow]", "[]15[]15[]15[]15[]"));

		JLabel lblAlpha = new JLabel("Alpha:");
		lblAlpha.setHorizontalAlignment(SwingConstants.TRAILING);
		centerPanel.add(lblAlpha, "cell 0 0,grow");

		alphaSpinner = new JSpinner();
		alphaSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(0), null, new Integer(1)));
		centerPanel.add(alphaSpinner, "cell 1 0,grow");

		JLabel lblW = new JLabel("W:");
		lblW.setHorizontalAlignment(SwingConstants.TRAILING);
		centerPanel.add(lblW, "cell 0 1,grow");

		WSpinner = new JSpinner();
		WSpinner.setModel(new SpinnerNumberModel(new Integer(20), new Integer(0), null, new Integer(1)));
		centerPanel.add(WSpinner, "cell 1 1,grow");

		JLabel lblPower = new JLabel("Power:");
		lblPower.setHorizontalAlignment(SwingConstants.TRAILING);
		centerPanel.add(lblPower, "cell 0 2,growx");

		powerSpinner = new JSpinner();
		powerSpinner.setModel(new SpinnerNumberModel(new Integer(25), new Integer(0), null, new Integer(1)));
		centerPanel.add(powerSpinner, "cell 1 2,grow");

		JLabel lblBeta = new JLabel("Beta:");
		lblBeta.setHorizontalAlignment(SwingConstants.TRAILING);
		centerPanel.add(lblBeta, "cell 0 3,growx");

		betaSpinner = new JSpinner();
		betaSpinner.setModel(new SpinnerNumberModel(new Double(8.51), new Double(0), null, new Double(0.01)));
		((JSpinner.NumberEditor) betaSpinner.getEditor()).getFormat().setMinimumFractionDigits(2);
		centerPanel.add(betaSpinner, "cell 1 3,grow");

		JLabel lblMu = new JLabel("Mu:");
		lblMu.setHorizontalAlignment(SwingConstants.TRAILING);
		centerPanel.add(lblMu, "cell 0 4,grow");

		muSpinner = new JSpinner();
		muSpinner.setModel(new SpinnerNumberModel(new Double(0.000000001), new Double(0), null, new Double(0.0000000001)));
		((JSpinner.NumberEditor) muSpinner.getEditor()).getFormat().setMinimumFractionDigits(10);
		centerPanel.add(muSpinner, "cell 1 4,grow");

		
		setDefault();
		
		collectResults();
	
		setSize(180,240);
		
		this.setLocationRelativeTo(null);

	}

	@Override 
	public void showDialog(boolean restore) {
		setVisible(true);
		if(restore) {
			if(this.options.size() > 0) {
				alphaSpinner.setValue(options.get(0));
				WSpinner.setValue(options.get(1));
				powerSpinner.setValue(options.get(2));
				betaSpinner.setValue(options.get(3));
				muSpinner.setValue(options.get(4));
			} else {
				setDefault();
			}
		}
		pack();
	}
	
	@Override
	protected void setDefault() {
		alphaSpinner.setValue(2);
		WSpinner.setValue(20);
		powerSpinner.setValue(25);
		betaSpinner.setValue(8.51);
		muSpinner.setValue(0.000000001);
	}

	@Override
	protected String getIdentifier() {
		return "sinr";
	}

	@Override
	protected void collectResults() {
		this.results.clear();
		this.results.put("alpha", alphaSpinner.getValue());
		this.results.put("w", WSpinner.getValue());
		this.results.put("power", powerSpinner.getValue());
		this.results.put("beta", betaSpinner.getValue());
		this.results.put("mu", muSpinner.getValue());
	}
	
	@Override
	protected boolean areOptionsValid() {
		return true;
	}

	@Override
	protected void saveValidOptions() {
		options.clear();
		options.add(alphaSpinner.getValue());
		options.add(WSpinner.getValue());
		options.add(powerSpinner.getValue());
		options.add(betaSpinner.getValue());
		options.add(muSpinner.getValue());
	}
	
}
