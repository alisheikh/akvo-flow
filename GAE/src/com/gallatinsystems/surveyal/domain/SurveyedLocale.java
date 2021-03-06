/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.surveyal.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Domain structure to represent a location about which there is data gathered through one or more
 * surveys. The sublevel1-6 fields are geographical subdivisions (state/province/sector/cell, etc)
 * where the exact definition of what each level corresponds to depends on the country in which the
 * point is located. Details about SurveyedLocales are stored using SurveyalValue which has a loose
 * association with this object.
 *
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class SurveyedLocale extends BaseDomain {
    private static final long serialVersionUID = -7908506708459480822L;

    /**
     * The pattern of the base32 surveyed locale id generated by the function generateBase32Uuid()
     */
    public static final String IDENTIFIER_PATTERN = "^[a-z0-9]{4}-[a-z0-9]{4}-[a-z0-9]{4}$";

    private String organization;
    private String systemIdentifier;
    private String identifier;
    private String displayName;
    private String countryCode;
    private String sublevel1;
    private String sublevel2;
    private String sublevel3;
    private String sublevel4;
    private String sublevel5;
    private String sublevel6;
    private Set<Long> surveyInstanceContrib;
    private List<String> geocells;
    private String localeType;
    private Double latitude;
    private Double longitude;
    private boolean ambiguous;
    private String currentStatus;
    private Long surveyGroupId;
    private Date lastSurveyedDate;
    private Long lastSurveyalInstanceId;
    private Long creationSurveyId;
    @NotPersistent
    private List<SurveyalValue> surveyalValues;

    public Long getLastSurveyalInstanceId() {
        return lastSurveyalInstanceId;
    }

    public void setLastSurveyalInstanceId(Long lastSurveyalInstanceId) {
        this.lastSurveyalInstanceId = lastSurveyalInstanceId;
    }

    public SurveyedLocale() {
        ambiguous = false;
    }

    public Date getLastSurveyedDate() {
        return lastSurveyedDate;
    }

    public void setLastSurveyedDate(Date lastSurveyedDate) {
        this.lastSurveyedDate = lastSurveyedDate;
    }

    public boolean isAmbiguous() {
        return ambiguous;
    }

    public void setAmbiguous(boolean ambiguous) {
        this.ambiguous = ambiguous;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getSublevel1() {
        return sublevel1;
    }

    public void setSublevel1(String sublevel1) {
        this.sublevel1 = sublevel1;
    }

    public String getSublevel2() {
        return sublevel2;
    }

    public void setSublevel2(String sublevel2) {
        this.sublevel2 = sublevel2;
    }

    public String getSublevel3() {
        return sublevel3;
    }

    public void setSublevel3(String sublevel3) {
        this.sublevel3 = sublevel3;
    }

    public String getSublevel4() {
        return sublevel4;
    }

    public void setSublevel4(String sublevel4) {
        this.sublevel4 = sublevel4;
    }

    public String getSublevel5() {
        return sublevel5;
    }

    public void setSublevel5(String sublevel5) {
        this.sublevel5 = sublevel5;
    }

    public String getSublevel6() {
        return sublevel6;
    }

    public void setSublevel6(String sublevel6) {
        this.sublevel6 = sublevel6;
    }

    public String getLocaleType() {
        return localeType;
    }

    public void setLocaleType(String localeType) {
        this.localeType = localeType;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getSystemIdentifier() {
        return systemIdentifier;
    }

    public void setSystemIdentifier(String systemIdentifier) {
        this.systemIdentifier = systemIdentifier;
    }

    public List<SurveyalValue> getSurveyalValues() {
        return surveyalValues;
    }

    public void setSurveyalValues(List<SurveyalValue> surveyalValues) {
        this.surveyalValues = surveyalValues;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Long getSurveyGroupId() {
        return surveyGroupId;
    }

    public void setSurveyGroupId(Long surveyGroupId) {
        this.surveyGroupId = surveyGroupId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<Long> getSurveyInstanceContrib() {
        return surveyInstanceContrib;
    }

    public void addContributingSurveyInstance(Long surveyInstanceId) {
        if (surveyInstanceContrib == null) {
            surveyInstanceContrib = new HashSet<Long>();
        }
        surveyInstanceContrib.add(surveyInstanceId);
    }

    public void setSurveyInstanceContrib(Set<Long> surveyInstanceIdList) {
        this.surveyInstanceContrib.addAll(surveyInstanceIdList);
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public List<String> getGeocells() {
        return geocells;
    }

    public void setGeocells(List<String> geocells) {
        this.geocells = geocells;
    }

    public Long getCreationSurveyId() {
        return creationSurveyId;
    }

    public void setCreationSurveyId(Long creationSurveyId) {
        this.creationSurveyId = creationSurveyId;
    }

    /**
     * Creates a base32 version of a UUID. In the output, it replaces the following letters: l, o, i
     * are replace by w, x, y, to avoid confusion with 1 and 0 we don't use the z as it can easily
     * be confused with 2, especially in handwriting. If we can't form the base32 version, we return
     * an empty string. The same code is used in the FLOW Mobile app:
     * https://github.com/akvo/akvo-flow-mobile/blob/feature/pointupdates/survey/
     * src/com/gallatinsystems/survey/device/util/Base32.java The resulting identifier is a string
     * in the format xxxx-xxxx-xxxx
     *
     * @return
     */
    public static String generateBase32Uuid() {
        final String uuid = UUID.randomUUID().toString();
        String strippedUUID = (uuid.substring(0, 13) + uuid.substring(24, 27)).replace("-", "");
        String result = null;
        try {
            Long id = Long.parseLong(strippedUUID, 16);
            result = Long.toString(id, 32).replace("l", "w").replace("o", "x").replace("i", "y");
        } catch (NumberFormatException e) {
            // if we can't create the base32 UUID string, return the original uuid.
            result = uuid;
        }

        // insert dashes for readability
        return String.format("%s-%s-%s", result.substring(0, 4), result.substring(4, 8),
                result.substring(8));
    }

    /**
     * Creates a base32 version of a UUID, starting with the old style identifiers for
     * surveyedLocales as a seed. The resulting identifier is a string in the format xxxx-xxxx-xxxx
     *
     * @param oldStyleIdentifier
     * @return
     */
    public static String generateBase32Uuid(String oldStyleIdentifier) {
        String base32Uuid = SurveyedLocale.generateBase32Uuid();

        if (oldStyleIdentifier.length() < 8) {
            return base32Uuid;
        }

        return String.format("%s-%s-%s", oldStyleIdentifier.substring(0, 4),
                oldStyleIdentifier.substring(4, 8),
                base32Uuid.substring(10));
    }
}
