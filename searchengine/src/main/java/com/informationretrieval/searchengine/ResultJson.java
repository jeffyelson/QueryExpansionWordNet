package com.informationretrieval.searchengine;
import java.util.List;

public class ResultJson {

	private List<String> queryList;
	private List<String> synonymList;
	private List<DocumentDetails> resultList;
	private int count;

	public ResultJson(List<String> queryList, List<String> synonymList, List<DocumentDetails> resultList) {
		this.queryList = queryList;
		this.synonymList = synonymList;
		this.resultList = resultList;
		this.count = resultList.size();
	}

	public List<String> getQueryList() {
		return queryList;
	}

	public List<String> getSynonymList() {
		return synonymList;
	}

	public List<DocumentDetails> getResultList() {
		return resultList;
	}

	public int getCount() {
		return count;
	}

	public void setQueryList(List<String> queryList) {
		this.queryList = queryList;
	}

	public void setSynonymList(List<String> synonymList) {
		this.synonymList = synonymList;
	}

	public void setResultList(List<DocumentDetails> resultList) {
		this.resultList = resultList;
		this.setCount();
	}

	private void setCount() {
		this.count = resultList.size();
	}

	@Override
	public String toString() {
		return "ResultJson [queryList=" + queryList + ", synonymList=" + synonymList + ", resultList=" + resultList
				+ ", count=" + count + "]";
	}

}
