package org.waterforpeople.mapping.app.web.service;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetDefinition;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SpreadsheetMappingAttributeServiceAsync {

	void listSpreadsheets(AsyncCallback<ArrayList<String>> callback);

	void listSpreadsheetColumns(String spreadsheetName,
			AsyncCallback<ArrayList<String>> callback);

	void listObjectAttributes(String objectNames,
			AsyncCallback<ArrayList<String>> callback);

	void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef,
			AsyncCallback<Void> callback);

	void processSpreadsheet(MappingSpreadsheetDefinition mapDef,
			AsyncCallback<Void> callback);

}
