package com.aryaka.test.model;

/**
 * After sorting, result will be this file
 * it has noOfRecords and path where sortedfiles exists
 * @author ashok.kumar
 *
 */
public class MergeResult {

	private String sortedFilePath;
	private int noOfRecords;
	public MergeResult(String sortedFilePath, int noOfRecords) {
		super();
		this.sortedFilePath = sortedFilePath;
		this.noOfRecords = noOfRecords;
	}
	public String getSortedFilePath() {
		return sortedFilePath;
	}
	public int getNoOfRecords() {
		return noOfRecords;
	}
	
	
}
