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

package com.gallatinsystems.survey.dao;

import com.gallatinsystems.survey.domain.Project;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * 
 * Dao for manipulating projects
 * 
 */
public class ProjectDao extends BaseDAO<Project> {

	public ProjectDao() {
		super(Project.class);
	}

	/**
	 * finds a single device group by code
	 * 
	 * @param name
	 * @return
	 */
	public Project findByProjectName(String name) {
		return super.findByProperty("name", name, "String");
	}

	public Project getById(Long key) {
		return super.getByKey(key);
	}
}
