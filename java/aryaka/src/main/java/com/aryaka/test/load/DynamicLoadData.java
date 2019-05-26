package com.aryaka.test.load;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.util.FileUtil;

public class DynamicLoadData extends LoadData {

	private DynamicLoadTask dl;
	private boolean stopLoading = false;
	
	private TimerTask compaction;

	public DynamicLoadData(String store) {
		super(store);
	}
	
	protected void init() {
		super.init();
		this.dl = new DynamicLoadTask(this.storePath);
		this.compaction = new LoadCompaction();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(compaction, Aryaka.COMPACTION_INTERVAL, Aryaka.COMPACTION_INTERVAL);
	}

	public static void main(String[] args) {
		String store = "/Users/ashok.kumar/github/allprojects/java/aryaka/WEB-INF/store";
		String dataFile = "/Users/ashok.kumar/github/allprojects/java/aryaka/data/big/1558871820217.csv";
		if (args.length == 1) {
			store = args[0];
		}
		DynamicLoadData dl = new DynamicLoadData(store);
		try {
			List<File> dataFiles = new ArrayList<File>();
			dataFiles.add(new File(dataFile));
			dl.loadData(dataFiles);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void loadData(List<File> dataFiles) throws Exception {
		if (dataFiles.size() > 1) {
			throw new Exception("Does not support more then one file");
		}
		File file = dataFiles.get(0);
		List<LoadResult> loadResults = new ArrayList<>();
		RandomAccessFile br = null;
		try {
			br = new RandomAccessFile(file, "r");
			long fp;
			String line = null;
			while (true) {
				line = br.readLine();
				if (line == null) {
					LoadResult res = dl.flush();
					if (null == res) {
						Thread.sleep(Aryaka.SLEEP_TIME);
						continue;
					}
					loadResults.add(res);
					writeMetaFile(loadResults);
					loadResults.clear();
					if (stopLoading)
						break;
					Thread.sleep(Aryaka.SLEEP_TIME);
					fp = br.getFilePointer();
					br.close();
					br = new RandomAccessFile(file, "r");
					if (br.length() >= fp) {
						br.seek(fp);
					}
					continue;
				}
				dl.addRecord(line);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.closeStream(br);
		}
	}

	public void stopLoading(boolean stopLoading) {
		this.stopLoading = stopLoading;
	}

	class LoadCompaction extends TimerTask {
		public void run() {
			try {
				System.out.println("Running compaction");
				CompactLoad compactLoad = new CompactLoad(storePath);
				List<LoadResult> metaInfo = FileUtil.getMetaData(FileUtil.getMetaFile(storePath));
				List<LoadResult> mergedLoads = compactLoad.doMergeLoads(metaInfo);
				checkifMetaLocked();
				lockMetaFile();
				List<LoadResult> latestMeta = FileUtil.getMetaData(FileUtil.getMetaFile(storePath));
				for (LoadResult loadResult : metaInfo) {
					if (!latestMeta.remove(loadResult)) {
						System.out.println("Merged load is not available in latest meta. its suspicious. please check");
						FileUtil.deleteFile(loadResult.getDataPath());
					}
				}
				latestMeta.addAll(mergedLoads);
				overWriteMetaFile(latestMeta);
				unlockMetaFile();
				//clean up old data file
				for(LoadResult loadResult: metaInfo) {
					if(loadResult.isMarkedForDelete()) {
						FileUtil.deleteFile(loadResult.getDataPath());
					}
				}

			} catch (Exception e) {
				System.out.println("Error during compaction");
				e.printStackTrace();
			}

		}

		private void overWriteMetaFile(List<LoadResult> loadResults) throws Exception {

			FileOutputStream out = null;
			ObjectOutputStream oout = null;
			try {
				File meta = FileUtil.getMetaFile(storePath);
				File tempMeta = FileUtil.getTempMetaFile(storePath);
				Collections.sort(loadResults, loadComp);

				out = new FileOutputStream(tempMeta);
				oout = new ObjectOutputStream(out);
				oout.writeObject(loadResults);
				oout.flush();

				FileUtil.deleteFile(meta);
				FileUtil.renameFile(tempMeta, meta);
			} catch (Exception e) {
                throw e;
			} finally {
				FileUtil.closeStream(out,oout);
			}

		}
	}

}
