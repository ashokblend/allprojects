package com.aryaka.test.query;

import java.util.concurrent.Callable;

import com.aryaka.test.model.Chunk;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.util.Utility;

/**
 * This task will scan each load to check if it has to be scanned or not. If given ip falls in its range then it will read chunk from filesystem
 * @author ashok.kumar
 *
 */
public class SearchLoad implements Callable<String> {
	private LoadResult load;
	private String ip2Search;
	public SearchLoad(LoadResult load, String ip2Search) {
		this.load = load;
		this.ip2Search = ip2Search;
	}

	@Override
	public String call() throws Exception {
		Chunk chunk = queryChunk(load, ip2Search);
		if(null == chunk) {
			System.out.println("no chunk found in:"+load.getDataPath());
			return null;
		}
		ChunkReader reader = new ChunkReader(chunk, load.getDataPath());
		return reader.searchIp(ip2Search);
	}
	
	private Chunk queryChunk(LoadResult loadResult, String ip) throws Exception {
		return Utility.binarySearch(loadResult.getChunks(), 0, loadResult.getChunks().length-1, ip);
	}


}
