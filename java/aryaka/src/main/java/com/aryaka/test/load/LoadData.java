package com.aryaka.test.load;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.sort.LoadResultComparator;
import com.aryaka.test.util.FileUtil;
import com.aryaka.test.util.Utility;

/**
 * This will spawn thread as configured @Aryaka.THREAD_POOL
 * Each task will be alloted csv files FILES_PER_TASK
 * 
 * @author ashok.kumar
 *
 */
public class LoadData {

	// nof files alloted to each task
	private static final int FILES_PER_TASK = 2;

	//structured data will be stored and also some folder will be created which will be used for temporary storage
	private String storePath;

	public LoadData(String storePath) {
		this.storePath = storePath;
		init();
	}
	
	private void init() {
		File store = new File(storePath);
		if (!store.exists() && !store.mkdir()) {
			System.out.println("Failed to create directory:" + store.getAbsolutePath());
		}
		File load = new File(storePath + File.separator + Aryaka.LOAD_DIR);
		if (!load.exists() && !load.mkdir()) {
			System.out.println("Failed to create directory:" + load.getAbsolutePath());
		}

		File tmp = new File(storePath + File.separator + Aryaka.TMP_DIR);
		if (!tmp.exists() && !tmp.mkdir()) {
			System.out.println("Failed to create directory:" + tmp.getAbsolutePath());
		}
		File sort = new File(storePath + File.separator + Aryaka.SORT_DIR);
		if (!sort.exists() && !sort.mkdir()) {
			System.out.println("Failed to create directory:" + sort.getAbsolutePath());
		}

	}

	public static void main(String[] args) {
		String dataDir = "/Users/ashok.kumar/github/allprojects/java/aryaka/data/big";
		String storePath = "/Users/ashok.kumar/Applications/apache/tomcat8/webapps/Aryaka/WEB-INF/store";
		File data = new File(dataDir);
		File[] files = data.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(Aryaka.FILE_EXT);
			}
		});
		LoadData loadData = new LoadData(storePath);
		try {
			loadData.loadData(Arrays.asList(files));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * spawn task to created structured files
	 * @param dataFiles : raw files
	 * @throws Exception
	 */
	public void loadData(List<File> dataFiles) throws Exception {
		try {
			List<LoadTask> tasks = createTasks(FILES_PER_TASK, dataFiles);
			ExecutorService executorService = Executors.newFixedThreadPool(Aryaka.THREAD_POOL);
			List<Future<LoadResult>> results = new ArrayList<>();
			System.out.println("Submitting "+tasks.size()+" tasks");
			long start = System.currentTimeMillis();
			for (LoadTask task : tasks) {
				results.add(executorService.submit(task));
			}
			List<LoadResult> loadResults = new ArrayList<>();
			for (Future<LoadResult> result : results) {
				loadResults.add(result.get());
			}
			Collections.sort(loadResults, new LoadResultComparator());
			executorService.shutdown();
			writeloadresult(loadResults);
			long diff = System.currentTimeMillis() - start;
			System.out.println("Finished loading in "+diff + " millisecond");
		} catch (Exception e) {
			throw e;
		}

	}

	private void writeloadresult(List<LoadResult> loadResults) throws Exception {
		File meta = new File(storePath + File.separator + Aryaka.LOAD_DIR, Aryaka.META_FILE);
		if (meta.exists() && !meta.delete()) {
			System.out.println("Meta file already exists and failed to delete it.");
		}
		FileOutputStream out = null;
		ObjectOutputStream oout = null;

		try {
			out = new FileOutputStream(meta);
			oout = new ObjectOutputStream(out);
			oout.writeObject(loadResults);
			;
			oout.flush();
		} catch (Exception e) {
			throw new Exception("Error writting Meta file",e);
		} finally {
			FileUtil.closeStream(out, oout);
		}

	}

	/**
	 * Create task based on no of raw files
	 * @param filesPerTask : As configured FILES_PER_TASK
	 * @param files : raw files
	 * @return no of task to be spawned
	 */
	private List<LoadTask> createTasks(int filesPerTask, List<File> files) {
		List<LoadTask> tasks = new ArrayList<>();
		int noOfTask = Utility.getNofTask(filesPerTask, files.size());
		int fileCount = 0;
		for (int i = 0; i < noOfTask && fileCount < files.size(); i++) {
			List<File> subList = new ArrayList<>();
			for (int j = 0; j < filesPerTask && fileCount < files.size(); j++) {
				subList.add(files.get(fileCount++));
			}
			System.out.println("Task"+(i+1)+" files:"+subList);
			tasks.add(new LoadTask(subList, storePath));
		}
		return tasks;
	}

}
