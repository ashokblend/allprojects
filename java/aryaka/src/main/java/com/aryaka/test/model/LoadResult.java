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

	public LoadResult(Chunk[] chunks, String dataPath) {
		
		this.chunks = chunks;
		this.dataPath = dataPath;
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
	public String toString() {
		return loadStartIp +Aryaka.IP_RANGE_DELIMITER+loadEndIp +","+dataPath;
	}
	

}
