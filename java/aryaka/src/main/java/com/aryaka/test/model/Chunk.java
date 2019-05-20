package com.aryaka.test.model;

import java.io.Serializable;

public class Chunk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//startIp of this chunk
	private String startIp;
	//endip of this chunk
	private String endIp;
	//offset of this chunk in file
	private int offset;
	//length of each record in chunk
	private byte[] recLen;
	//total no of bytes in this chunk
	private int length;

	public Chunk() {
		
	}

	public void setStartIp(String startIp) {
		this.startIp = startIp;
	}

	public void setEndIp(String endIp) {
		this.endIp = endIp;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setRecordLen(byte[] recLen) {
		this.recLen = recLen;
	}

	public String getStartIp() {
		return startIp;
	}

	public String getEndIp() {
		return endIp;
	}

	public int getOffset() {
		return offset;
	}

	public byte[] getRecordLen() {
		return recLen;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String toString() {
		return startIp+Aryaka.IP_RANGE_DELIMITER+endIp+","+offset+","+length;
	}
}
