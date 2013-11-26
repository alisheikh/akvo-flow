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

package org.waterforpeople.mapping.app.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.util.json.JSONObject;
//import org.json.JSONArray;
//import org.json.JSONObject;
import org.waterforpeople.mapping.app.web.dto.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyedLocaleResponse;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

/**
 * JSON service for returning the list of records for a specific surveyId
 * 
 * @author Mark Tiele Westra
 */
public class SurveyedLocaleServlet extends AbstractRestApiServlet {
	private static final long serialVersionUID = 8748650927754433019L;
	private SurveyedLocaleDao surveyedLocaleDao;
	private static final Integer SL_PAGE_SIZE = 300;

	public SurveyedLocaleServlet() {
		setMode(JSON_MODE);
		surveyedLocaleDao = new SurveyedLocaleDao();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyedLocaleRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	/**
	 * calls the surveyedLocaleDao to get the list of surveyedLocales for a
	 * certain surveyGroupId passed in via the request, or the total number of
	 * available surveyedLocales if the checkAvailable flag is set.
	 */
	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		SurveyedLocaleRequest slReq = (SurveyedLocaleRequest) req;
		List<SurveyedLocale> slList = null;
		Boolean hasPermission = false;
		if (slReq.getSurveyGroupId() != null){
			if (slReq.getPhoneNumber() != null || slReq.getImei() != null) {
				DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
				SurveyDAO surveyDao = new SurveyDAO();
			    for (DeviceSurveyJobQueue dsjq : dsjqDAO.get(slReq.getPhoneNumber(), slReq.getImei())) {
					Survey s = surveyDao.getById(dsjq.getSurveyID());
					if (s != null && s.getSurveyGroupId().longValue() == slReq.getSurveyGroupId().longValue()) {
						hasPermission = true;
					}
				}
				if (hasPermission) {
					slList = surveyedLocaleDao.listLocalesBySurveyGroupAndDate(slReq.getSurveyGroupId(),slReq.getLastUpdateTime(),SL_PAGE_SIZE);
				}
			}
		}
		return convertToResponse(slList, slReq.getSurveyGroupId(), slReq.getLastUpdateTime());
	}

	/**
	 * converts the domain objects to dtos and then installs them in a
	 * RecordDataResponse object
	 * @param lastUpdateTime 
	 */
	protected SurveyedLocaleResponse convertToResponse(List<SurveyedLocale> slList, Long surveyGroupId, Date lastUpdateTime) {
		SurveyedLocaleResponse resp = new SurveyedLocaleResponse();
		SurveyedLocaleDao slDao = new SurveyedLocaleDao();
		if (slList != null) {
			// set meta data
			resp.setResultCount(slList.size());
			if (slList.size() > 0) {
				resp.setLastUpdateTime(slList.get(slList.size() - 1).getLastUpdateDateTime().getTime());
			} else {
				resp.setLastUpdateTime(lastUpdateTime.getTime()); // return original query timestamp
			}

			// set Locale data
			List<SurveyedLocaleDto> dtoList = new ArrayList<SurveyedLocaleDto>();

			Integer totalLocales = slList.size();
			Integer localesConsumed = 0;
			Integer localeCount;
			HashMap<Long,List<SurveyalValue>> instanceMap;
			List<Long> slIdList = new ArrayList<Long>();
			HashMap<Long,HashMap<Long,List<SurveyalValue>>> localeMap = new HashMap<Long,HashMap<Long,List<SurveyalValue>>>();
			
			while ((totalLocales - localesConsumed) > 0) {
				// try to consume 30 items at a go, this is the maximum for a GAE IN query
				localeCount = 0;
				slIdList.clear();
				localeMap.clear();
				while (localeCount < 30 && (totalLocales - localesConsumed) > 0){
					slIdList.add(slList.get(localesConsumed).getKey().getId());
					localeCount += 1;
					localesConsumed += 1;
				}
				
				// get surveyalValues for these Locales using an IN query.
				List<SurveyalValue> svList = slDao.listValuesByLocales(slIdList);
				
				// put the results in a map
				if (svList != null && svList.size() > 0){
					for (SurveyalValue sv : svList){
						if (localeMap.containsKey(sv.getSurveyedLocaleId())){
							// add it to an existing locale
							instanceMap = localeMap.get(sv.getSurveyedLocaleId());
							if (instanceMap.containsKey(sv.getSurveyInstanceId())){
								instanceMap.get(sv.getSurveyInstanceId()).add(sv);
							} else {
								instanceMap.put(sv.getSurveyInstanceId(),new ArrayList<SurveyalValue>());
								instanceMap.get(sv.getSurveyInstanceId()).add(sv);
							}
						} else {
							// create a new locale in the map
							localeMap.put(sv.getSurveyedLocaleId(), new HashMap<Long,List<SurveyalValue>>());
							instanceMap = localeMap.get(sv.getSurveyedLocaleId());
							instanceMap.put(sv.getSurveyInstanceId(),new ArrayList<SurveyalValue>());
							instanceMap.get(sv.getSurveyInstanceId()).add(sv);
						}
					}
				}
				
				// Add them to the dto
				for (int i = localesConsumed - localeCount ; i < localesConsumed; i++){
					if (slList.get(i) != null && localeMap.containsKey(slList.get(i).getKey().getId())){
						dtoList.add(marshallToDto(surveyGroupId, slList.get(i),localeMap.get(slList.get(i).getKey().getId())));
					}
				}
			} // end of while loop
			resp.setSurveyedLocaleData(dtoList);
		}
		return resp;
	}

	/**
	 * puts the result in dto
	 * @param surveyGroupId 
	 * @param surveyInstanceMap 
	 * @param surveyedLocale 
	 */
	private SurveyedLocaleDto marshallToDto(Long surveyGroupId, SurveyedLocale sl, HashMap<Long, List<SurveyalValue>> instanceMap) {
		SurveyedLocaleDto dto = new SurveyedLocaleDto();
		dto.setId(sl.getIdentifier());
		dto.setSurveyGroupId(surveyGroupId);
		dto.setDisplayName(sl.getDisplayName());
		dto.setLat(sl.getLatitude());
		dto.setLon(sl.getLongitude());
		SurveyInstanceDAO sDao = new SurveyInstanceDAO();
		QuestionDao qDao = new QuestionDao();
		HashMap <Long, String> questionTypeMap = new HashMap<Long, String>();
		
		for (Long instanceId : instanceMap.keySet()){
			SurveyInstanceDto siDto = new SurveyInstanceDto();
			SurveyInstance si = sDao.getByKey(instanceId);
			if (si != null){
				siDto.setUuid(si.getUuid());
			}
			siDto.setCollectionDate(instanceMap.get(instanceId).get(0).getCollectionDate().getTime());
			siDto.setSurveyId(instanceMap.get(instanceId).get(0).getSurveyId());
			for (SurveyalValue sv : instanceMap.get(instanceId)){
				String svQuestionType = sv.getQuestionType();
				String deviceQuestionType = "VALUE";
				if (svQuestionType.equals("DATE")) {
					deviceQuestionType = "DATE";
				} else if (svQuestionType.equals("GEO")) {
					deviceQuestionType = "GEO";
				} else if (svQuestionType.equals("PHOTO")) {
					deviceQuestionType = "IMAGE";
				} else if (svQuestionType.equals("VIDEO")) {
					deviceQuestionType = "VIDEO";
				} else if (svQuestionType.equals("SCAN")) {
					deviceQuestionType = "SCAN";
				} else if (svQuestionType.equals("OPTION")) {
					// first see if we have the question in the map already
					if (questionTypeMap.containsKey(sv.getSurveyQuestionId())){
						deviceQuestionType = questionTypeMap.get(sv.getSurveyQuestionId()); 
					} else {
						// find question by id
						Question q = qDao.getByKey(sv.getSurveyQuestionId());
						if (q!= null){
							// if the question has the allowOtherFlag set,
							// use OTHER as the device question type
							if (q.getAllowOtherFlag()) {
								deviceQuestionType = "OTHER";
							}
							questionTypeMap.put(sv.getSurveyQuestionId(), deviceQuestionType);
						}
					}
				}
				if (!sv.getQuestionType().equals("IMAGE")){
					// add question type
					siDto.addProperty(sv.getSurveyQuestionId(), sv.getStringValue() != null ? sv.getStringValue() : "", deviceQuestionType);
				}
			}
			dto.getSurveyInstances().add(siDto);
		}
		return dto;
	}
	
	
	
	/**
	 * writes response as a JSON string
	 */
	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		SurveyedLocaleResponse slResp = (SurveyedLocaleResponse) resp;
		JSONObject result = new JSONObject(slResp);

		getResponse().getWriter().println(result.toString());
	}
}