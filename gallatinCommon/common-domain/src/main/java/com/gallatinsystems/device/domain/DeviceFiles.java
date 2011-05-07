package com.gallatinsystems.device.domain;

import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import com.gallatinsystems.device.domain.Status.StatusCode;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DeviceFiles extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Persistent
	private String URI = null;
	@Persistent
	private Date uploadDateTime = null;
	@Persistent
	private Status.StatusCode processedStatus = null;
	@Persistent
	private Status status = null;
	@Persistent
	private String processDate = null;
	private Text processingMessageText = null;
	private Long surveyInstanceId = null;

	private String phoneNumber;
	private String checksum;

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getURI() {
		return URI;
	}

	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}

	public void setURI(String uri) {
		URI = uri;
	}

	public Date getUploadDateTime() {
		return uploadDateTime;
	}

	public void setUploadDateTime(Date uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
	}

	public Status.StatusCode getProcessedStatus() {
		return processedStatus;
	}

	public void setProcessedStatus(StatusCode statusCode) {
		this.processedStatus = statusCode;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("DeviceFiles: ");
		sb.append("\n   Key: " + key.toString());
		sb.append("\n   URI: " + URI);
		sb.append("\n   ProcessDate: " + this.processDate);
		sb.append("\n   Status: " + status);
		sb.append("\n   ProcessedStatus: " + this.processedStatus);
		return sb.toString();
	}

	public void addProcessingMessage(String message) {
		if (processingMessageText != null)
			processingMessageText = new Text(processingMessageText.getValue()
					+ "\n" + message);
		else
			processingMessageText = new Text(message);
	}

	public void setProcessingMessageText(Text processingMessage) {
		this.processingMessageText = processingMessage;
	}

	

	public String getProcessingStringMessage() {
		return processingMessageText.getValue();
	}

	public void setSurveyInstanceId(Long surveyInstanceId) {
		this.surveyInstanceId = surveyInstanceId;
	}

	public Long getSurveyInstanceId() {
		return surveyInstanceId;
	}

	public Text getProcessingMessageText() {
		// TODO Auto-generated method stub
		return processingMessageText;
	}
}