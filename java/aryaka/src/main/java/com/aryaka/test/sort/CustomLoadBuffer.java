package com.aryaka.test.sort;

import java.util.Iterator;

import com.aryaka.test.model.LoadResult;

public class CustomLoadBuffer implements RecordStack {

	private String cache;

	private Iterator<String> loadReader;

	public CustomLoadBuffer(LoadResult load) throws Exception {
		this.loadReader = new com.aryaka.test.query.LoadReader(load);
		reload();
	}

	@Override
	public void close() throws Exception {
		//no stream

	}

	@Override
	public boolean empty() {
		return this.cache == null;
	}

	@Override
	public String peek() {
		return this.cache;
	}

	@Override
	public String pop() throws Exception {
		String answer = peek().toString();// make a copy
		reload();
		return answer;
	}

	private void reload() throws Exception {
		if (loadReader.hasNext()) {
			this.cache = loadReader.next();
		} else {
			this.cache = null;
		}

	}
}
