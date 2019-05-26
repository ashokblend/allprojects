package com.aryaka.test.query;

import java.util.Iterator;

import com.aryaka.test.model.Chunk;
import com.aryaka.test.model.LoadResult;

public class LoadReader implements Iterator<String> {
	
	private Iterator<Chunk> chunksReader;
	
	private Iterator<String> chunkReader;
	
	private String dataPath;
	public LoadReader(LoadResult load) {
		this.dataPath = load.getDataPath();
		this.chunksReader = new ChunksReader(load.getChunks());
	}

	@Override
	public boolean hasNext() {
	  try {
		  if(null == chunkReader) {
			  if(chunksReader.hasNext()) {
				  chunkReader = new ChunkReader(chunksReader.next(), dataPath);
				  return chunkReader.hasNext();
			  } else {
				  return false;
			  }
		  } else {
			 return chunkReader.hasNext();
		  }
	  } catch(Exception e) {
		  e.printStackTrace();
		  return false;
	  }
	}

	@Override
	public String next() {
		String nextRec = chunkReader.next();
		if(!chunkReader.hasNext()) {
			chunkReader = null;
		}
		return nextRec;
	}

}
