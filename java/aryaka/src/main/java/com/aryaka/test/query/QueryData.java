package com.aryaka.test.query;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.util.FileUtil;
import com.aryaka.test.util.Utility;

/**
 * This will query data from store
 * @author ashok.kumar
 *
 */
public class QueryData {

	//directory where structured data is stored
	private String storeDir;

	//Each task is loadresult
	private List<LoadResult> loadResults;

	public QueryData(String storeDir) throws Exception {
		this.storeDir = storeDir;
		initialise();
	}

	//read meta file and keep in memory
	private void initialise() throws Exception {
		File meta = new File(storeDir + File.separator + Aryaka.LOAD_DIR, Aryaka.META_FILE);
		try {
			loadResults = getMetaData(meta);
		} catch (Exception e) {
			throw new Exception("Error reading meta file", e);
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
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public String queryCity(String ip) throws Exception {
		try {
			long st = System.currentTimeMillis();
			//Query list of loads which has to be scanned
			List<LoadResult> loads = queryLoadResult(loadResults, ip);
			if(loads.size() == 0) {
				System.out.println("No loadresult found");
				return "Ip not found";
			}
			//spawn task to scan selected LoadResult
			ExecutorService executorService = Executors.newFixedThreadPool(Aryaka.THREAD_POOL);
			List<Future<String>> futres = new ArrayList<>();
			for(LoadResult load: loads) {
				futres.add(executorService.submit(new SearchLoad(load, ip)));
			}
			StringBuffer sb = new StringBuffer();
			for(Future<String> fut: futres) {
				if(null != fut.get()) {
					sb.append(fut.get());
				}
			}
			long dif = System.currentTimeMillis() - st;
			System.out.println("TimeTaken in millisec:" + dif);
			System.out.println("result:" + sb.toString());
			executorService.shutdown();
			if(sb.toString().length()==0) {
				return "Ip not found";
			} else {
				return sb.toString().split(Aryaka.DELIMITER)[1];
			}
		} catch (Exception e) {
			throw new Exception("Error querying ip", e);
		}

	}

	private List<LoadResult> queryLoadResult(List<LoadResult> loadResults, String ip) throws Exception {
		//return Utility.binarySearch(loadResults, 0, loadResults.size()-1, ip);
		return Utility.search(loadResults, ip);
	}

	
	private List<LoadResult> getMetaData(File meta) throws Exception {
		FileInputStream is = null;
		ObjectInputStream ois = null;
		try {
			is = new FileInputStream(meta);
			ois = new ObjectInputStream(is);
			List<LoadResult> loadResults = (List<LoadResult>) ois.readObject();
			return loadResults;
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.closeStream(is, ois);
		}
	}

}
