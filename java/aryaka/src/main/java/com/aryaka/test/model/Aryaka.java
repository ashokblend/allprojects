package com.aryaka.test.model;

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
	
	//size of each chunk
	public static final int CHUNK_SIZE = 10000;
	
	//file extension
	public static final String FILE_EXT = ".csv";
	
	//no of task to be executed in parallel
	public static final int THREAD_POOL=3;

	//no of temporary files to emitt while sorting
	public static final long TEMP_FILES = 3;
}
