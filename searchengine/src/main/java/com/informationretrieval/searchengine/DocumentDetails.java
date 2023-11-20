package com.informationretrieval.searchengine;

public class DocumentDetails {

	private int topicID;
	private String topicName;
	private String topicContent;
	private String topicDocument;

	public DocumentDetails(int topicID, String topicName, String topicContent, String topicDocument) {
		this.topicID = topicID;
		this.topicName = topicName;
		this.topicContent = topicContent;
		this.topicDocument = topicDocument;
	}

	public int getTopicID() {
		return topicID;
	}

	public String getTopicName() {
		return topicName;
	}

	public String getTopicContent() {
		return topicContent;
	}

	public String getTopicDocument() {
		return topicDocument;
	}

	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public void setTopicContent(String topicContent) {
		this.topicContent = topicContent;
	}

	public void setTopicDocument(String topicDocument) {
		this.topicDocument = topicDocument;
	}

}
