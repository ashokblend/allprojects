package com.aryaka.test.load;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.model.MergeResult;
import com.aryaka.test.sort.IPComparator;
import com.aryaka.test.util.FileUtil;
import com.aryaka.test.util.Utility;

public class DynamicLoadTask extends LoadTask {

	private List<String> data = new ArrayList<String>(Aryaka.CHUNK_SIZE);
	private IPComparator comp = new IPComparator();
	private String tmpPath;
	private String sortPath;
	private List<File> tmpFiles = new ArrayList<File>();
	private int noOfRecords=0;
	public DynamicLoadTask(String store) {
		super(null, store);
		this.tmpPath = store + File.separator + Aryaka.TMP_DIR;
		this.sortPath = store + File.separator + Aryaka.SORT_DIR;
	}
	
	public void addRecord(String record) throws Exception {
	    data.add(record+Aryaka.DELIMITER+System.nanoTime());
	    noOfRecords++;
	    if(data.size() == Aryaka.CHUNK_SIZE) {
	    	sortAndSaveToTempLocation();
	    }
	}
	private void sortAndSaveToTempLocation() throws Exception {
		Collections.sort(data, comp);
		String fileName = UUID.randomUUID().toString();
		File tmpFile = new File(tmpPath, fileName);
		FileUtil.writeFile(tmpFile, data);
		tmpFiles.add(tmpFile);
		data.clear();
		if(tmpFiles.size() >= Aryaka.TEMP_FILES) {
			mergeTempFiles();
		}
	}

	/**
	 * Merge temporary files and create another temp file
	 * @throws Exception
	 */
	private void mergeTempFiles() throws Exception {
		String fileName = UUID.randomUUID().toString();
		File tmpFile = new File(tmpPath, fileName);
		int noOfRecordSorted = Utility.mergeSortedFiles(tmpFiles, tmpFile, comp);
		System.out.println("No of records sorted while merging temporary files:"+noOfRecordSorted);
		tmpFiles.clear();
		tmpFiles.add(tmpFile);
	}

	public LoadResult flush() throws Exception {
		File sortedFile = null;
		if(data.size() == 0 && tmpFiles.size() == 0) {
			System.out.println("no data to write to store");
			return null;
		}
		if(tmpFiles.size() == 0) {
			Collections.sort(data, comp);
			String fileName = UUID.randomUUID().toString();
			sortedFile = new File(sortPath, fileName);
			FileUtil.writeFile(sortedFile, data);
		} else {
			sortAndSaveToTempLocation();
			String fileName = UUID.randomUUID().toString();
		    sortedFile = new File(sortPath,fileName);
			Utility.mergeSortedFiles(tmpFiles, sortedFile, comp);
		}
		MergeResult mergeResult =new MergeResult(sortedFile.getCanonicalPath(), noOfRecords);
		LoadResult loadResult = writeToStore(mergeResult);
		System.out.println("Load path:"+loadResult.getDataPath()+",no of record written to load:"+noOfRecords);
		
		clearAll();
		return loadResult;
	}

	private void clearAll() {
		data.clear();
		noOfRecords = 0;
		tmpFiles.clear();
	}
}
