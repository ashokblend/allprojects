package com.aryaka.test.model;

import java.util.Date;

public interface Aryaka {

	//records column separator
	public static final String DELIMITER=",";
	
	//ip range separator
	public static final String IP_RANGE_DELIMITER="-";
	
	//tmp director where external sort will emit files
	public static final String TMP_DIR = "tmp";
	
	//directory where structured data will be stored
	public static final String LOAD_DIR = "load";
	
	//director where sorted data will be stored before structuring
	public static final String SORT_DIR = "sort";
	
	//meta data which will hold metadata of structured data
	public static final String META_FILE="meta";
	
	//Tempoarary meta file. It is used while updating meta file
	public static final String TEMP_META_FILE="meta.tmp";
	
	//size of each chunk
	public static final int CHUNK_SIZE = 50000;
	
	//file extension
	public static final String FILE_EXT = ".csv";
	
	//no of task to be executed in parallel
	public static final int THREAD_POOL=1;

	//no of temporary files to emitt while sorting
	public static final long TEMP_FILES = 3;
	
	//ip generator working time(ms) i.e it will generate data for this much time and then sleep
	public static final int WORK_TIME=100;
	
	//ip generator sleep time(ms)
	public static final int SLEEP_TIME=1*60*1000;
	
	//Maximum no of record in a file
	public static final int MAX_REC_LOAD=2000000;
	
	//This is considered while merging load. Load having record count under given toleance will not be considered for merging
	public static final float TOLERANCE_MERGE_LOAD=0.1f;
	
	// in a load file how many chunk should be available
	public static final int MAX_CHUNK_IN_LOAD=(MAX_REC_LOAD/CHUNK_SIZE);
	
	//lock file to make sure meta file is not accessed concurrently
	public static final String META_LOCK="meta.lock";

	public static final long COMPACTION_INTERVAL = 5*60*1000;
}
