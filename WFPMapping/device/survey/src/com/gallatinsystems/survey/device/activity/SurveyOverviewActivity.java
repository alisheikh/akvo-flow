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

import java.util.Date;
import java.util.List;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Project;
import com.gallatinsystems.survey.device.domain.QuestionResponse;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.domain.SurveyedLocale;
import com.gallatinsystems.survey.device.domain.SurveyedLocaleValue;
import com.gallatinsystems.survey.device.service.BootstrapService;
import com.gallatinsystems.survey.device.util.ConstantUtil;
import com.gallatinsystems.survey.device.util.ViewUtil;
import com.gallatinsystems.survey.device.view.adapter.HomeMenuViewAdapter;
import com.gallatinsystems.survey.device.view.adapter.SurveyOverviewAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * Activity to select survey-specific actions
 * 
 * @author Mark Tiele Westra
 */
public class SurveyOverviewActivity extends Activity implements OnItemClickListener{

	public static final int SURVEY_ACTIVITY = 1;
	public static final int SELECT_LOCALE_ACTIVITY = 2;
	public static final int DOWNLOAD_RECORDS_ACTIVITY = 10;
	private static final String TAG = "Survey Home Activity";

	private TextView surveyField;
	private TextView currentUserField;
	private TextView existingRecordField;
	private TextView projectField;
	private TextView recordsCollectedField;
	private TextView readyForUploadField;
	private TextView savedSurveysField;
	private SurveyDbAdapter databaseAdapter;
	private SurveyOverviewAdapter menuViewAdapter;
	private String userId;
	private String surveyId;
	private String projectId;
	private String selectedLocale;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.surveyoverview);

		//surveyField = (TextView) findViewById(R.id.surveyField);
		currentUserField = (TextView) findViewById(R.id.currentUserField);
		projectField = (TextView) findViewById(R.id.projectField);
		existingRecordField = (TextView) findViewById(R.id.existingRecordField);

//		recordsCollectedField = (TextView) findViewById(R.id.recordsCollectedField);
//		readyForUploadField = (TextView) findViewById(R.id.readyForUploadField);
//		savedSurveysField = (TextView) findViewById(R.id.savedSurveysField);

		databaseAdapter = new SurveyDbAdapter(this);
		databaseAdapter.open();

		menuViewAdapter = new SurveyOverviewAdapter(this);
		GridView grid = (GridView) findViewById(R.id.gridview);
		grid.setAdapter(menuViewAdapter);
		grid.setOnItemClickListener(this);

		Bundle extras = getIntent().getExtras();
		userId = extras != null ? extras.getString(ConstantUtil.USER_ID_KEY)
				: null;
		if (userId == null) {
			userId = savedInstanceState != null ? savedInstanceState
					.getString(ConstantUtil.USER_ID_KEY) : null;
		}

		projectId = extras != null ? extras
				.getString(ConstantUtil.PROJECT_ID_KEY) : null;
		if (projectId == null) {
			projectId = savedInstanceState != null ? savedInstanceState
					.getString(ConstantUtil.PROJECT_ID_KEY) : "1";
		}

		populateFields();
		selectedLocale = null;
	}

	/**
	 * put loaded data into the views for display
	 */
	private void populateFields() {

//		Survey surveyFromDb = databaseAdapter.findSurvey(surveyId);
		Project projectFromDb = databaseAdapter.findProject(projectId);
		if (projectFromDb != null) {
			projectField.setText(projectFromDb.getName());
		}

		Cursor user = databaseAdapter.findUser(Long.parseLong(userId));
		startManagingCursor(user);
		if (user.getCount() > 0) {
			currentUserField.setText(user.getString(user
				.getColumnIndexOrThrow(SurveyDbAdapter.DISP_NAME_COL)));
		}

		if (selectedLocale != null) {
			SurveyedLocale localeFromDb = databaseAdapter.findSurveyedLocale(selectedLocale);
			if (localeFromDb != null){
				existingRecordField.setText(localeFromDb.toString());
			}
		}
//		surveyField.setText(surveyFromDb.getName());
//		savedSurveysField.setText(Integer.toString(databaseAdapter
//				.countSurveyRespondents(ConstantUtil.SAVED_STATUS)));
//		recordsCollectedField.setText(Integer.toString(databaseAdapter
//				.countSurveyRespondents(ConstantUtil.SUBMITTED_STATUS)));
//		readyForUploadField.setText(Integer.toString(databaseAdapter
//				.countUnsentSurveyRespondents(surveyId)));
	}


	public void findExistingRecord(View view) {
		Intent i = new Intent(view.getContext(), FilterLocaleActivity.class);
		i.putExtra(ConstantUtil.USER_ID_KEY, userId);
		i.putExtra(ConstantUtil.PROJECT_ID_KEY, projectId);
		startActivityForResult(i, SELECT_LOCALE_ACTIVITY);
	}


	public void showSurveyList(View view) {
		Survey survey = databaseAdapter.findSurvey(surveyId);
		Intent i = new Intent(view.getContext(), SurveyOverviewListActivity.class);
		i.putExtra(ConstantUtil.SURVEY_ID_KEY, survey.getId());
		startActivity(i);
	}

	public void downloadData(View view) {
		 Intent i = new Intent(view.getContext(), DownloadRecordsActivity.class);
		 i.putExtra(ConstantUtil.USER_ID_KEY, userId);
		 i.putExtra(ConstantUtil.PROJECT_ID_KEY, projectId);
		 startActivityForResult(i, DOWNLOAD_RECORDS_ACTIVITY);
	}

	// handle completed survey
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SURVEY_ACTIVITY) {
			// Make sure the request was successful
			if (resultCode == RESULT_OK) {
				ViewUtil.showConfirmDialog(R.string.submitcompletetitle,
						R.string.submitcompletetext, this);
				// refresh record update time
				if (selectedLocale != null){
					SurveyedLocale localeFromDb = databaseAdapter.findSurveyedLocale(selectedLocale);
					Date now =new Date();
					localeFromDb.setLastSubmittedDate(now.getTime());
					existingRecordField.setText(localeFromDb.toString());
				}

			}
		} else if (requestCode == SELECT_LOCALE_ACTIVITY) {
			if (resultCode == ConstantUtil.LOCALE_SELECTED){
				String slId = data.getStringExtra(ConstantUtil.SL_KEY);
				selectedLocale = slId;
				SurveyedLocale localeFromDb = databaseAdapter.findSurveyedLocale(selectedLocale);
				if (localeFromDb != null){
					existingRecordField.setText(localeFromDb.toString());
				}
			}
		}
	}

	public void onResume() {
		super.onResume();
		databaseAdapter.open();
		menuViewAdapter.loadData(this, projectId);

		// Update the survey counts
//		savedSurveysField.setText(Integer.toString(databaseAdapter
//				.countSurveyRespondents(ConstantUtil.SAVED_STATUS)));
//		recordsCollectedField.setText(Integer.toString(databaseAdapter
//				.countSurveyRespondents(ConstantUtil.SUBMITTED_STATUS)));
//		readyForUploadField.setText(Integer.toString(databaseAdapter
//				.countUnsentSurveyRespondents(surveyId)));
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onDestroy() {
		if (databaseAdapter != null) {
			databaseAdapter.close();
		}
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Survey survey = menuViewAdapter.getSelectedSurvey(position);
		if (survey != null ) {
			if (selectedLocale != null){
				SurveyedLocale localeFromDb = databaseAdapter.findSurveyedLocale(selectedLocale);

				// create new survey respondent
				Long respId = databaseAdapter.createSurveyRespondent(surveyId, userId);

				// add answers to the appropriate values
				List<SurveyedLocaleValue> slvList = databaseAdapter.listSurveyedLocaleValuesByLocaleId(selectedLocale);
				QuestionResponse resp = new QuestionResponse(localeFromDb.getLocaleUniqueId(),"IDENT", "0");
				resp.setRespondentId(respId);
				databaseAdapter.createOrUpdateSurveyResponse(resp);

				for (SurveyedLocaleValue slv : slvList){
					resp = new QuestionResponse(slv.getAnswerValue(),ConstantUtil.VALUE_RESPONSE_TYPE, slv.getQuestionId());
					resp.setRespondentId(respId);
					databaseAdapter.createOrUpdateSurveyResponse(resp);
				}

				// initiate the survey view activity
				Intent i = new Intent(v.getContext(), SurveyViewActivity.class);
				i.putExtra(ConstantUtil.USER_ID_KEY, userId);
				i.putExtra(ConstantUtil.SURVEY_ID_KEY, survey.getId());
				i.putExtra(ConstantUtil.RESPONDENT_ID_KEY, respId);
				startActivityForResult(i,SURVEY_ACTIVITY);
			} else {
				// start a new one, or complete an existing one that we left
				Intent i = new Intent(v.getContext(), SurveyViewActivity.class);
				i.putExtra(ConstantUtil.USER_ID_KEY, userId);
				i.putExtra(ConstantUtil.SURVEY_ID_KEY, survey.getId());
				startActivityForResult(i, SURVEY_ACTIVITY);
			}
		} else {
			Log.e(TAG, "Survey for selection is null");
		}
	}
}