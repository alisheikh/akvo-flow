/*
 *  Copyright (C) 2012 Stichting Akvo (Akvo Foundation)
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
package org.waterforpeople.mapping.app.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.ProjectDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.ProjectPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.ProjectDao;
import com.gallatinsystems.survey.domain.Project;

@Controller
@RequestMapping("/projects")
public class ProjectRestService {

	@Inject
	private ProjectDao projectDao;

	// list all projects
	@RequestMapping(method = RequestMethod.GET, value = "")
	@ResponseBody
	public Map<String, List<ProjectDto>> listProjects() {
		final Map<String, List<ProjectDto>> response = new HashMap<String, List<ProjectDto>>();
		List<ProjectDto> results = new ArrayList<ProjectDto>();
		List<Project> projects = projectDao
				.list(Constants.ALL_RESULTS);
		if (projects != null) {
			for (Project p : projects) {
				ProjectDto dto = new ProjectDto();
				DtoMarshaller.copyToDto(p, dto);

				results.add(dto);
			}
		}
		response.put("projects", results);
		return response;
	}

	// find a single project by the projectId
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Map<String, ProjectDto> findProject(
			@PathVariable("id") Long id) {
		final Map<String, ProjectDto> response = new HashMap<String, ProjectDto>();
		Project p = projectDao.getByKey(id);
		ProjectDto dto = null;
		if (p != null) {
			dto = new ProjectDto();
			DtoMarshaller.copyToDto(p, dto);
		}
		response.put("project", dto);
		return response;
	}

	// delete project by id
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseBody
	public Map<String, RestStatusDto> deleteProjectById(
			@PathVariable("id") Long id) {
		final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
		Project p = projectDao.getByKey(id);
		RestStatusDto statusDto = null;
		statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// check if project exists in the datastore
		if (p != null) {
			// delete project group
			projectDao.delete(p);
			statusDto.setStatus("ok");
		}
		response.put("meta", statusDto);
		return response;
	}

	// update existing project
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	@ResponseBody
	public Map<String, Object> saveExistingProject(
			@RequestBody ProjectPayload payLoad) {
		final ProjectDto projectDto = payLoad.getProject();
		final Map<String, Object> response = new HashMap<String, Object>();
		ProjectDto dto = null;

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid projectDto, continue.
		// Otherwise,
		// server will respond with 400 Bad Request
		if (projectDto != null) {
			Long keyId = projectDto.getKeyId();
			Project p;

			// if the projectDto has a key, try to get the project.
			if (keyId != null) {
				p = projectDao.getByKey(keyId);
				// if we find the project, update it's properties
				if (p != null) {
					// copy the properties, except the createdDateTime property,
					// because it is set in the Dao.
					BeanUtils.copyProperties(projectDto, p,
							new String[] { "createdDateTime" });
					p = projectDao.save(p);
					dto = new ProjectDto();
					DtoMarshaller.copyToDto(p, dto);
					statusDto.setStatus("ok");
				}
			}
		}
		response.put("meta", statusDto);
		response.put("project", dto);
		return response;
	}

	// create new project
	@RequestMapping(method = RequestMethod.POST, value = "")
	@ResponseBody
	public Map<String, Object> saveNewProject(
			@RequestBody ProjectPayload payLoad) {
		final ProjectDto projectDto = payLoad.getProject();
		final Map<String, Object> response = new HashMap<String, Object>();
		ProjectDto dto = null;

		RestStatusDto statusDto = new RestStatusDto();
		statusDto.setStatus("failed");

		// if the POST data contains a valid projectDto, continue.
		// Otherwise,
		// server will respond with 400 Bad Request
		if (projectDto != null) {
			Project p = new Project();

			// copy the properties, except the createdDateTime property, because
			// it is set in the Dao.
			BeanUtils.copyProperties(projectDto, p,
					new String[] { "createdDateTime" });
			p = projectDao.save(p);

			dto = new ProjectDto();
			DtoMarshaller.copyToDto(p, dto);
			statusDto.setStatus("ok");
		}

		response.put("meta", statusDto);
		response.put("project", dto);
		return response;
	}
}
