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

package com.gallatinsystems.survey.device.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gallatinsystems.survey.device.R;
import com.gallatinsystems.survey.device.dao.SurveyDbAdapter;
import com.gallatinsystems.survey.device.domain.Survey;
import com.gallatinsystems.survey.device.util.ArrayUtil;
import com.gallatinsystems.survey.device.util.ConstantUtil;

/**
 * 
 * Adapter class to populate the grid view on the survey overview page. 
 * 
 * @author Mark Westra
 * 
 */
public class SurveyOverviewAdapter extends BaseAdapter {

	private static final String TAG = "Home Menu View Adapter";

	/**
	 * statically defined menu options
	 */
	
	// references to our buttons
	private Integer[] buttonImages = new Integer[0];
	// this is of type Object since the values can be Integers or Strings
	private Object[] buttonLabels;
	private ArrayList<Survey> surveys;
	private ArrayList<String> operations;
	private LayoutInflater inflater;

	/**
	 * initializes the view inflater
	 * 
	 * @param c
	 */
	public SurveyOverviewAdapter(Context c) {
		inflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * gets all surveys from the db and uses them to instantiate the views
	 * 
	 * @param c
	 */
	public void loadData(Context c, String projectId) {
		fetchSurveys(c, projectId);
		initializeValues();
	}

	/**
	 * builds internal data structures to facilitate the combination of both
	 * static and dynamic menu options
	 */
	private void initializeValues() {
		operations = new ArrayList<String>();
		
		int arraySize = surveys.size() ;
		
		buttonImages = new Integer[arraySize];
		buttonLabels = new Object[arraySize];
		
		int p = 0;
		for (int i = 0; i < surveys.size(); i++) {
			if (ConstantUtil.SURVEY_TYPE.equalsIgnoreCase(surveys.get(i).getType())) {
				buttonImages[p + i] = R.drawable.checklist;
			} else {
				buttonImages[p + i] = R.drawable.map;
			}
			buttonLabels[p + i] = surveys.get(i).getName()
					+ " v. "
					+ (surveys.get(i).getVersion() != 0.0d ? surveys.get(i)
							.getVersion() : "1.0");
			operations.add(ConstantUtil.SURVEY_OP);
		}
		p += surveys.size();
		notifyDataSetChanged();
	}

	/**
	 * gets surveys from the database and closes the db adaptor
	 * 
	 * @param c
	 * @return
	 */
	private ArrayList<Survey> fetchSurveys(Context c, String projectId) {
		SurveyDbAdapter database = new SurveyDbAdapter(c);
		database.open();
		surveys = database.listSurveysForProject(projectId);
		database.close();
		return surveys;
	}

	public int getCount() {
		return buttonImages.length;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	/**
	 * creates a new ImageView for each item referenced by the Adapter
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		View item = null;
		if (convertView == null) { // if it's not recycled, initialize some
			item = inflater.inflate(R.layout.homebuttonlayout, parent, false);
		} else {
			item = convertView;
		}
		item.setTag(operations.get(position));
		ImageView button = (ImageView) item.findViewById(R.id.homebuttonimg);
		TextView text = (TextView) item.findViewById(R.id.buttonText);

		button.setImageResource(buttonImages[position]);
		if (buttonLabels[position] instanceof String) {
			text.setText((String) buttonLabels[position]);
		} else {
			text.setText((Integer) buttonLabels[position]);
		}
		return item;
	}

	/**
	 * determines the survey that corresponds to the position within the grid
	 * and calls delete on the database for that survey. It then calls the
	 * initialize method to reset internal structures
	 * 
	 * @param position
	 * @param c
	 */
	public void deleteItem(int position, Context c) {
		SurveyDbAdapter database = new SurveyDbAdapter(c);
		database.open();
		Survey itemToDelete = getSelectedSurvey(position);
		if (itemToDelete != null) {
			database.deleteSurvey(itemToDelete.getId(), false);
			database.close();
			surveys.remove(itemToDelete);
			// update the view
			initializeValues();
		}
	}

	/**
	 * returns the survey id of the selected survey
	 * 
	 * @param idx
	 * @return
	 */
	public Survey getSelectedSurvey(int idx) {
		if (idx < surveys.size()) {
			return surveys.get(idx );
		} else {
			Log.e(TAG, "Selected item exceeds survey list size");
			return null;
		}
	}

	/**
	 * returns the operation code for the selected index
	 * 
	 * @param index
	 * @return
	 */
	public String getSelectedOperation(int index) {
		return operations.get(index);
	}
}
