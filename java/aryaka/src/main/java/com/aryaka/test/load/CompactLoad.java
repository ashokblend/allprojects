package com.aryaka.test.load;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.model.MergeResult;
import com.aryaka.test.sort.IPComparator;
import com.aryaka.test.sort.LoadResultComparator;
import com.aryaka.test.util.FileUtil;
import com.aryaka.test.util.Utility;

public class CompactLoad extends LoadTask {

	public CompactLoad(String store) {
		super(null, store);
	}

	public List<LoadResult> doMergeLoads(List<LoadResult> metaInfo) throws Exception {
		try {

			List<LoadResult> mergedLoads = new ArrayList<>(metaInfo.size());
			List<LoadResult> tempLoads = new ArrayList<>(metaInfo.size());
			int loadRecCounter = 0;
			for (LoadResult loadResult : metaInfo) {
				if (considerForMerging(loadResult)) {
					tempLoads.add(loadResult);
					System.out.println("Compaction: Adding to mergelist:"+loadResult.getDataPath()+", record count for this load:"+loadResult.getNoOfRecords());
					loadRecCounter += loadResult.getNoOfRecords();
					loadResult.setMarkedForDelete(true);
				} else {
					System.out.println("Compaction: Merging not required:"+loadResult.getDataPath()+", record count for this load:"+loadResult.getNoOfRecords());
					mergedLoads.add(loadResult);
					continue;
				}
				if (loadRecCounter > Aryaka.MAX_REC_LOAD) {
					LoadResult mergedLoad = mergeLoads(tempLoads);
					System.out.println("Compaction:Merged loads,"+tempLoads+", after merging record count:"+mergedLoad.getNoOfRecords());
					mergedLoads.add(mergedLoad);
					loadRecCounter = 0;
					tempLoads.clear();
				}
			}
			if (tempLoads.size() > 1) {
				LoadResult mergedLoad = mergeLoads(tempLoads);
				mergedLoads.add(mergedLoad);
			}
			System.out.println("Compaction: Merged load count:"+mergedLoads.size());
			return mergedLoads;
		} catch (Exception e) {
			throw e;
		}
	}

	private LoadResult mergeLoads(List<LoadResult> tempLoads) throws Exception {
		Collections.sort(tempLoads, new LoadResultComparator());
		/**
		 * int totalRec = 0; String startIp = tempLoads.get(0).getLoadStartIp(); String
		 * endIp = tempLoads.get(tempLoads.size()-1).getLoadEndIp();
		 * 
		 * for(LoadResult load: tempLoads) { totalRec +=load.getNoOfRecords(); }
		 **/
		Comparator<String> comp = new IPComparator();
		File outputfile = new File(storePath + File.separator + Aryaka.SORT_DIR, FileUtil.getUniqueFileName());
		int noOfRecordSorted = Utility.mergeSortedLoads(tempLoads, outputfile, comp);
		MergeResult mergeResult = new MergeResult(outputfile.getAbsolutePath(), noOfRecordSorted);
		return writeToStore(mergeResult);

	}

	private boolean considerForMerging(LoadResult loadResult) {
		int recordCount = loadResult.getNoOfRecords();
		if (recordCount >= Aryaka.MAX_REC_LOAD) {
			return false;
		}
		int toleanceLImit = (int) (Aryaka.MAX_REC_LOAD - Aryaka.TOLERANCE_MERGE_LOAD * Aryaka.MAX_REC_LOAD);
		if (recordCount > toleanceLImit) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		String store = "/Users/ashok.kumar/github/allprojects/java/aryaka/WEB-INF/store";
		CompactLoad compactLoad = new CompactLoad(store);
		try {
			// compactLoad.doMergeLoads();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
