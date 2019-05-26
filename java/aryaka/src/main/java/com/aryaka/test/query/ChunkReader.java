package com.aryaka.test.query;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;

import org.xerial.snappy.Snappy;

import com.aryaka.test.model.Chunk;
import com.aryaka.test.util.FileUtil;
import com.aryaka.test.util.Utility;

/**
 * During query, this will read store structured data to search for given ip
 * 
 * @author ashok.kumar
 *
 */
public class ChunkReader implements Iterator<String> {

	private Chunk chunk;
	private String dataPath;
	private byte[][] data;
	private int index = 0;

	public ChunkReader(Chunk chunk, String dataPath) throws Exception {
		this.chunk = chunk;
		this.dataPath = dataPath;
		readChunk();
	}

	private void readChunk() throws Exception {
		RandomAccessFile is = null;
		try {
			is = new RandomAccessFile(new File(dataPath), "r");
			FileChannel ch = is.getChannel().position(chunk.getOffset());

			ByteBuffer cb = ByteBuffer.allocate(chunk.getLength());
			ch.read(cb);
			byte[] compressed = cb.array();
			byte[] uncompressed = Snappy.uncompress(compressed);
			int[] recLen = Snappy.uncompressIntArray(chunk.getRecordLen());
			data = new byte[recLen.length][];
			int srcPos = 0;
			for (int i = 0; i < recLen.length; i++) {
				data[i] = new byte[recLen[i]];
				System.arraycopy(uncompressed, srcPos, data[i], 0, recLen[i]);
				srcPos += recLen[i];
			}

		} catch (Exception e) {
			throw new Exception("Error reading data file,"+dataPath, e);

		} finally {
			FileUtil.closeStream(is);
		}
	}

	public String searchIp(String ip) throws Exception {
		return Utility.binarySearch(data, 0, data.length - 1, ip);
	}

	@Override
	public boolean hasNext() {
		if (index < chunk.getNoOfRecords()) {
			return true;
		}
		return false;
	}

	@Override
	public String next() {
		return new String(data[index++]);
	}

}
