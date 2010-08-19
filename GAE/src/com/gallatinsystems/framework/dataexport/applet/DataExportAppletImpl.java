package com.gallatinsystems.framework.dataexport.applet;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.waterforpeople.mapping.dataexport.DataExporterFactory;

/**
 * simple applet to allow us to export data from google app engine
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataExportAppletImpl extends JApplet {

	private static final long serialVersionUID = 944163825066341210L;
	private JLabel statusLabel;

	public void init() {
		statusLabel = new JLabel();
		String type = getParameter("type");
		Map<String, String> criteria = parseCriteria(getParameter("criteria"));
		doExport(type, criteria);
	}

	private Map<String, String> parseCriteria(String source) {
		Map<String, String> crit = new HashMap<String, String>();
		if (source != null) {
			StringTokenizer strTok = new StringTokenizer(source, ";");
			while (strTok.hasMoreTokens()) {
				String[] parts = strTok.nextToken().split("=");
				if (parts.length == 2) {
					crit.put(parts[0], parts[1]);
				}
			}
		}
		return crit;
	}

	public void doExport(String type, Map<String, String> criteriaMap) {
		JFileChooser chooser = new JFileChooser();
		chooser.showSaveDialog(this);
		if (chooser.getSelectedFile() != null) {
			DataExporter exporter = DataExporterFactory.getExporter(type);
			statusLabel.setText("Exporting");
			exporter.export(criteriaMap, chooser.getSelectedFile());
		}

	}

}
