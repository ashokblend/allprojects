package com.aryaka.test.query;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.sort.IPComparator;
import com.aryaka.test.util.FileUtil;
import com.aryaka.test.util.Utility;

/**
 * This will query data from store
 * 
 * @author ashok.kumar
 *
 */
public class QueryData {

	// Each task is loadresult
	private List<LoadResult> loadResults;

	private long lastModified;

	private File meta;

	private IPComparator comp;

	private File metaLock;

	private TimerTask refreshMeta;

	public QueryData(String storeDir) throws Exception {
		this.metaLock = new File(storeDir + File.separator + Aryaka.LOAD_DIR, Aryaka.META_LOCK);
		this.meta = new File(storeDir + File.separator + Aryaka.LOAD_DIR, Aryaka.META_FILE);
		this.comp = new IPComparator();
		refreshMeta = new RefreshMeta();
		Timer timer = new Timer();
		refreshMeta.run();
		timer.scheduleAtFixedRate(refreshMeta, Aryaka.COMPACTION_INTERVAL, Aryaka.COMPACTION_INTERVAL);
	}

	// read meta file and keep in memory
	private void readMeta() throws Exception {
		try {
			loadResults = FileUtil.getMetaData(meta);
			lastModified = meta.lastModified();
		} catch (Exception e) {
			throw new Exception("Error reading meta file", e);
		}
	}

	private void refreshMeta() throws Exception {
		if (meta.lastModified() > this.lastModified) {
			readMeta();
		}
	}

	public static void main(String[] args) {
		try {
			// String storeDir = args[0];
			String storeDir = "/Users/ashok.kumar/Applications/apache/tomcat8/webapps/Aryaka/WEB-INF/store/";
			QueryData queryData = new QueryData(storeDir);
			String ip = "46.108.91.198";
			queryData.queryCity(ip);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Search given ip
	 * 
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public String queryCity(String ip) throws Exception {
		try {
			long st = System.currentTimeMillis();
			// Query list of loads which has to be scanned
			List<LoadResult> loads = queryLoadResult(loadResults, ip);
			if (loads.size() == 0) {
				System.out.println("No loadresult found");
				return "Ip not found";
			}
			// spawn task to scan selected LoadResult
			ExecutorService executorService = Executors.newFixedThreadPool(Aryaka.THREAD_POOL);
			List<Future<String>> futres = new ArrayList<>();

			for (LoadResult load : loads) {
				futres.add(executorService.submit(new SearchLoad(load, ip)));
			}
			List<String> results = new ArrayList<String>();
			for (Future<String> fut : futres) {
				String res = fut.get();
				if (null != res) {
					results.add(res);
				}
			}
			long dif = System.currentTimeMillis() - st;
			System.out.println("TimeTaken in millisec:" + dif);
			System.out.println("result:" + results);
			executorService.shutdown();
			if (results.size() == 0) {
				return "Ip not found";
			} else {
				Collections.sort(results, comp);
				return results.get(results.size() - 1).split(Aryaka.DELIMITER)[1];
			}
		} catch (Exception e) {
			throw new Exception("Error querying ip", e);
		}

	}

	private List<LoadResult> queryLoadResult(List<LoadResult> loadResults, String ip) throws Exception {
		// return Utility.binarySearch(loadResults, 0, loadResults.size()-1, ip);
		return Utility.search(loadResults, ip);
	}

	/**
	 * This will refresh meta cache in every given interval
	 * @author ashok.kumar
	 *
	 */
	class RefreshMeta extends TimerTask {
		public void run() {
			try {
				while (metaLock.exists()) {
					System.out.println("Sleeping for," + Aryaka.SLEEP_TIME + ", as meta file is locked");
				}
				FileUtil.lockMetaFile(metaLock);
				refreshMeta();
				FileUtil.unlockMetaFile(metaLock);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
