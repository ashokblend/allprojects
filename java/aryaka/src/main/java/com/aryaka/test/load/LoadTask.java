package com.aryaka.test.load;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.xerial.snappy.Snappy;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.Chunk;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.model.MergeResult;
import com.aryaka.test.sort.ExternalMergeSort;
import com.aryaka.test.util.FileUtil;

/**
 * Each task is alloted files. It has below job
 * 1. Sort files using ExternalSort
 * 2. Write sorted file under store/sort folder
 * 3. Write sorted files in compressed format
 * 4. Return @LoadResult to caller
 * @author ashok.kumar
 *
 */
public class LoadTask implements Callable<LoadResult> {
	/**
	 * 
	 */
	private List<File> files;
	private int[] chunksRecCount;
	private Chunk[] chunks;
	private int offset = 0;
	private File loadFile;

	private String storePath;

	private String loadPath;

	public LoadTask(List<File> files, String storePath) {
		this.files = files;
		this.storePath = storePath;
		this.loadPath = storePath + File.separator + Aryaka.LOAD_DIR;
	}

	@Override
	public LoadResult call() throws Exception {
		ExternalMergeSort mergeSort = new ExternalMergeSort(files, storePath);
		MergeResult mergeResult = mergeSort.doMergeSort();
		int noOfRecord = mergeResult.getNoOfRecords();

		FileReader fr = null;
		BufferedReader br = null;
		FileOutputStream out = null;
		try {
			File sortFile = new File(mergeResult.getSortedFilePath());
			fr = new FileReader(sortFile);
			br = new BufferedReader(fr);
			String outFileName = UUID.randomUUID().toString();
			loadFile = new File(loadPath, outFileName);
			out = new FileOutputStream(loadFile, true);
			initChunRecCount(noOfRecord);
			int noOfChunk = chunksRecCount.length;
			String line = null;
			for (int chunkIndx = 0; chunkIndx < noOfChunk; chunkIndx++) {
				// [recordIndx][data]
				byte[][] chunkData = new byte[chunksRecCount[chunkIndx]][];
				// [recordindx]
				int[] recLen = new int[chunksRecCount[chunkIndx]];
				int recCounter = 0;
				chunks[chunkIndx] = new Chunk();
				while (recCounter < chunksRecCount[chunkIndx] && (line = br.readLine()) != null) {
					if (recCounter == 0) {
						chunks[chunkIndx].setStartIp(line.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0]);
					} 
					if (recCounter == chunksRecCount[chunkIndx] - 1) {
						chunks[chunkIndx].setEndIp(line.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[1]);
					}
					chunkData[recCounter] = line.getBytes();
					recLen[recCounter] = chunkData[recCounter].length;
					recCounter++;
				}

				flushData(chunkData, out, chunkIndx, recLen);

			}
			if (!sortFile.delete()) {
				System.out.println("Failed to delete sort file:" + sortFile.getAbsolutePath());
			}

		} catch (Exception e) {
			throw new Exception("Error writting result data", e);
		} finally {
			FileUtil.closeStream(fr,br,out);
		}

		return new LoadResult(chunks, loadFile.getAbsolutePath());
	}

	// write data to file. this is final file
	private void flushData(byte[][] chunkData, OutputStream out, int chunkIndx, int[] recLen) throws IOException {
		int totalLen = Arrays.stream(recLen).sum();
		byte[] completeChunkData = new byte[totalLen];
		int destPos = 0;
		for (int i = 0; i < chunkData.length; i++) {
			System.arraycopy(chunkData[i], 0, completeChunkData, destPos, recLen[i]);
			destPos += recLen[i];
		}
		try {
			byte[] compressed = Snappy.compress(completeChunkData);
			chunks[chunkIndx].setOffset(offset);
			out.write(compressed);
			offset += compressed.length;
			chunks[chunkIndx].setLength(compressed.length);
			chunks[chunkIndx].setRecordLen(Snappy.compress(recLen));

		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * This calculate how many chunk should be there and each chunk should contain how many records
	 * @param noOfRecord : total record count for this load
	 */
	private void initChunRecCount(int noOfRecord) {
		if (noOfRecord % Aryaka.CHUNK_SIZE == 0) {
			chunksRecCount = new int[noOfRecord / Aryaka.CHUNK_SIZE];
			for (int i = 0; i < chunksRecCount.length; i++) {
				if (i == chunksRecCount.length - 1) {

				}
				chunksRecCount[i] = Aryaka.CHUNK_SIZE;
			}
		} else {
			chunksRecCount = new int[noOfRecord / Aryaka.CHUNK_SIZE + 1];
			for (int i = 0; i < chunksRecCount.length; i++) {
				if (i == chunksRecCount.length - 1) {
					chunksRecCount[i] = noOfRecord % Aryaka.CHUNK_SIZE;
					break;
				}
				chunksRecCount[i] = Aryaka.CHUNK_SIZE;
			}
		}
		chunks = new Chunk[chunksRecCount.length];

	}

}
