package com.aryaka.test.sort;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.MergeResult;
import com.aryaka.test.util.FileUtil;
import com.aryaka.test.util.Utility;

/**
 * This will sort and merge files for each task. It will use tmp folder to emit
 * temporary files.
 * 
 * @author ashok.kumar
 *
 */
public class ExternalMergeSort {

	// files to sort
	private List<File> files;
	// tmp folder where temporary files will be emitted
	private String tmpPath;
	// folder where sorted file will be kept
	private String sortPath;
	// total record for this task
	private int recordCounter = 0;

	public ExternalMergeSort(List<File> files, String storePath) {
		this.files = files;
		this.tmpPath = storePath + File.separator + Aryaka.TMP_DIR;
		this.sortPath = storePath + File.separator + Aryaka.SORT_DIR;

	}

	public MergeResult doMergeSort() throws Exception {
		Comparator<String> comp = new IPComparator();
		List<File> sortedFiles = new ArrayList<File>();
		File outFile = new File(sortPath, UUID.randomUUID().toString());
		try {

			for (File file : files) {
				List<File> tempFiles = sortFile(file, comp);
				sortedFiles.addAll(tempFiles);
			}
			Utility.mergeSortedFiles(sortedFiles, outFile, comp);
		} catch (Exception e) {
			throw new Exception("Error sorting data for files:" + files, e);
		}
		return new MergeResult(outFile.getAbsolutePath(), recordCounter);
	}

	/**
	 * This will read each file alloted to task , emits temporary file. Sort each
	 * temp file
	 * 
	 * @param file
	 *            : file to be sorted
	 * @param comp
	 *            : comparator
	 * @return list of emitted temporary files which are sorted
	 * @throws Exception
	 */
	private List<File> sortFile(File file, Comparator<String> comp) throws Exception {
		long fileSize = file.length();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		List<String> tmpLists = new ArrayList<String>();
		String line = null;
		long readSize = 0l;
		List<File> tmpFiles = new ArrayList<File>();
		long blockSize = fileSize / Aryaka.TEMP_FILES;
		while ((line = br.readLine()) != null) {
			// TO-DO move this from here
			line = line+Aryaka.DELIMITER+System.nanoTime();
			readSize += line.getBytes().length;
			tmpLists.add(line);
			if (readSize > blockSize) {
				sortAndSave(comp, tmpLists, tmpFiles);
				readSize = 0;
			}
			++recordCounter;
		}
		sortAndSave(comp, tmpLists, tmpFiles);
		FileUtil.closeStream(br);
		return tmpFiles;
	}

	private void sortAndSave(Comparator<String> comp, List<String> tmpLists, List<File> tmpFiles) throws Exception {
		String fileName = UUID.randomUUID().toString();
		File tmpFile = new File(tmpPath, fileName);
		tmpFiles.add(tmpFile);
		Collections.sort(tmpLists, comp);
		FileUtil.writeFile(tmpFile, tmpLists);
		tmpLists.clear();
	}
}
