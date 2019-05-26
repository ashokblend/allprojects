package com.aryaka.test.query;

import java.util.Iterator;

import com.aryaka.test.model.Chunk;

public class ChunksReader implements Iterator<Chunk> {

	private Chunk[] chunks;
	private int index = 0;

	public ChunksReader(Chunk[] chunks) {
		this.chunks = chunks;
	}

	@Override
	public boolean hasNext() {
		if (index < chunks.length) {
			return true;
		}
		return false;
	}

	@Override
	public Chunk next() {
		return chunks[index++];
	}

}
