package com.aryaka.test.model;

import java.io.Serializable;

public class LoadResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//chhunk list in this load
	private Chunk[] chunks;
	
	//final data or load path which has all this chunks
	private String dataPath;

	//this load startIp
	private String loadStartIp;
	
	//this load endIp
	private String loadEndIp;
	
	//no of records in load
	private int noOfRecords;
	
	//if it is true, this load will be deleted as it has been merged
	private boolean markedForDelete;

	public LoadResult(Chunk[] chunks, String dataPath, int noOfRecords) {
		
		this.chunks = chunks;
		this.dataPath = dataPath;
		this.noOfRecords = noOfRecords;
		if(chunks.length > 0) {
		   this.loadStartIp = chunks[0].getStartIp();
		    this.loadEndIp = chunks[chunks.length -1].getEndIp();
		}
		
	}

	public Chunk[] getChunks() {
		return chunks;
	}
	
	public String getDataPath() {
		return dataPath;
	}

	public String getLoadStartIp() {
		return loadStartIp;
	}

	public String getLoadEndIp() {
		return loadEndIp;
	}
	
	
	public int getNoOfRecords() {
		return noOfRecords;
	}

	public String toString() {
		return loadStartIp +Aryaka.IP_RANGE_DELIMITER+loadEndIp +","+noOfRecords+","+dataPath;
	}

	
	public boolean isMarkedForDelete() {
		return markedForDelete;
	}

	public void setMarkedForDelete(boolean markedForDelete) {
		this.markedForDelete = markedForDelete;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataPath == null) ? 0 : dataPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoadResult other = (LoadResult) obj;
		if (dataPath == null) {
			if (other.dataPath != null)
				return false;
		} else if (!dataPath.equals(other.dataPath))
			return false;
		return true;
	}
	

}
