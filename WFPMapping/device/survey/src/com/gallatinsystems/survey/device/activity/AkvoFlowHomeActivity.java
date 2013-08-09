/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.survey.device.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Project;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.domain.SurveyedLocale;
import com.gallatinsystems.survey.device.exception.PersistentUncaughtExceptionHandler;
import com.gallatinsystems.survey.device.service.ApkUpdateService;
import com.gallatinsystems.survey.device.service.BootstrapService;
import com.gallatinsystems.survey.device.service.DataSyncService;
import com.gallatinsystems.survey.device.service.ExceptionReportingService;
import com.gallatinsystems.survey.device.service.LocationService;
import com.gallatinsystems.survey.device.service.PrecacheService;
import com.gallatinsystems.survey.device.service.SurveyDownloadService;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.PropertyUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;
import com.gallatinsystems.survey.device.view.adapter.HomeMenuViewAdapter;

/**
 * Activity to render the survey home screen. It will list all available
 * sub-activities and start them as needed.
 * 
 * @author M.T. Westra
 * 
 */
public class AkvoFlowHomeActivity extends ListActivity implements OnItemClickListener {
	private SurveyDbAdapter databaseAdapter;
	private static final String TAG = "Survey Home Activity";
	public static final int SURVEY_ACTIVITY = 1;
	public static final int LIST_USER_ACTIVITY = 2;
	public static final int SETTINGS_ACTIVITY = 3;
	public static final int SURVEY_OVERVIEW_ACTIVITY = 4;
	
	public static final int SURVEY_STS_HOME_ACTIVITY = 9;
	private String currentUserId;
	private String currentName;
	private TextView userField;
	private PropertyUtil props;
	private ArrayList<Project> projectArray;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		props = new PropertyUtil(getResources());
		Thread.setDefaultUncaughtExceptionHandler(PersistentUncaughtExceptionHandler
				.getInstance());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.akvoflowhome);

		databaseAdapter = new SurveyDbAdapter(this);
		
		userField = (TextView) findViewById(R.id.currentUserField);
		currentUserId = savedInstanceState != null ? savedInstanceState
				.getString(ConstantUtil.ID_KEY) : null;
		currentName = savedInstanceState != null ? savedInstanceState
				.getString(ConstantUtil.DISPLAY_NAME_KEY) : null;

		if (currentUserId == null) {
			loadLastUser();
		}

		startSyncService();
		startService(SurveyDownloadService.class);
		startService(LocationService.class);
		//startService(PrecacheService.class);
		startService(BootstrapService.class);
		//startService(ApkUpdateService.class);
		startService(ExceptionReportingService.class);
	}

	/**
	 * checks if the user preference to persist logged-in users is set and, if
	 * so, loads the last logged-in user from the DB
	 * 
	 * @return
	 */
	private void loadLastUser() {

		SurveyDbAdapter database = new SurveyDbAdapter(this);
		database.open();
		// first check if they want to keep users logged in
		String val = database
				.findPreference(ConstantUtil.USER_SAVE_SETTING_KEY);
		if (val != null && Boolean.parseBoolean(val)) {
			val = database.findPreference(ConstantUtil.LAST_USER_SETTING_KEY);
			if (val != null && val.trim().length() > 0) {
				currentUserId = val;
				Cursor cur = database.findUser(Long.valueOf(val));
				if (cur != null) {
					currentName = cur
							.getString(cur
									.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL));
					cur.close();
				}
			}
		}
		database.close();
	}

	
	public void showSettings(View view) {
		Intent i = new Intent(view.getContext(), SettingsActivity.class);
		startActivityForResult(i, SETTINGS_ACTIVITY);
	}
	
	public void manageUsers(View view) {
		Intent i = new Intent(view.getContext(), ListUserActivity.class);
		startActivityForResult(i, LIST_USER_ACTIVITY);
	}
	
	
	/**
	 * starts up the data sync service
	 */
	private void startSyncService() {
		Intent i = new Intent(this, DataSyncService.class);
		i.putExtra(ConstantUtil.OP_TYPE_KEY, ConstantUtil.SEND);
		getApplicationContext().startService(i);
	}

	/**
	 * starts up a service that takes no input
	 */
	private <T extends Service> void startService(Class<T> serviceClass) {
		getApplicationContext().startService(new Intent(this, serviceClass));
	}

	
	/**
	 * handles the callbacks from the completed activities. If it was the
	 * user-select activity, we need to get the selected user from the bundle
	 * data and set it in the appropriate member variables.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case LIST_USER_ACTIVITY:
			if (resultCode == RESULT_OK) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					currentUserId = bundle.getString(ConstantUtil.ID_KEY);
					currentName = bundle
							.getString(ConstantUtil.DISPLAY_NAME_KEY);
					populateFields();
				}
			} else if (resultCode == RESULT_CANCELED && intent != null) {
				Bundle bundle = intent.getExtras();
				if (bundle != null
						&& bundle.getBoolean(ConstantUtil.DELETED_SAVED_USER)) {
					currentUserId = null;
					currentName = null;
					populateFields();
				}
			}
			break;
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (outState != null) {
			if (currentUserId != null) {
				outState.putString(ConstantUtil.ID_KEY, currentUserId);
			}
			if (currentName != null) {
				outState.putString(ConstantUtil.DISPLAY_NAME_KEY, currentName);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		databaseAdapter.open();
		populateFields();
		
		projectArray = databaseAdapter.listProjects();
		if(projectArray != null){
			ArrayAdapter<Project> projectAdapter = new ArrayAdapter<Project>(this, R.layout.itemlistrow, projectArray);
			setListAdapter(projectAdapter);
		}
	}
	
	private void populateFields() {
		userField.setText(currentName);
		//menuViewAdapter.loadData(this);
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		
		if (currentUserId != null) {
			Intent i = new Intent(this, SurveyOverviewActivity.class);
			i.putExtra(ConstantUtil.PROJECT_ID_KEY, projectArray.get(position).getProjectId());
			i.putExtra(ConstantUtil.USER_ID_KEY, currentUserId);
			startActivityForResult(i, SURVEY_OVERVIEW_ACTIVITY);
		} else {
			// if the current user is null, we can't enter survey overview mode
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.mustselectuser)
				.setCancelable(true)
				.setPositiveButton(R.string.okbutton,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
							int id) {
								dialog.cancel();
						}
					});
			builder.show();
		}
	}
	
	private void saveState() {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}