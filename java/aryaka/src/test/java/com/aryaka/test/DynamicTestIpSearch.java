package com.aryaka.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.aryaka.test.gen.IPGenerator;
import com.aryaka.test.load.DynamicLoadData;
import com.aryaka.test.load.LoadData;
import com.aryaka.test.model.Aryaka;
import com.aryaka.test.query.QueryData;
import com.aryaka.test.util.FileUtil;

import junit.framework.TestCase;

public class DynamicTestIpSearch extends TestCase {

	@Test
	public void testIpSearch() {
		List<File> dataFiles = null;
		String storePath = "src/test/resources";
		try {
			GenerateData datagen = new GenerateData(storePath);
			Thread datagenThread = new Thread(datagen);
			datagenThread.start();
			Thread.sleep(Aryaka.WORK_TIME);
			dataFiles = datagen.getFiles();
			DataLoader dataload = new DataLoader(storePath, dataFiles);
			Thread dataloadThread = new Thread(dataload);
			dataloadThread.start();
			
			List<String> sampledata = getSampleData(dataFiles);
            
			String record = sampledata.get(0);
			File metaFile = FileUtil.getMetaFile(storePath);
			while(!metaFile.exists()) {
				System.out.println("Waiting for meta file to get created");
			}
			
			QueryData queryData = new QueryData(storePath);
			
			//query range startIp
			String record1 = getRandomData(sampledata);
			String startIp = record1.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0];
			String result1 = queryData.queryCity(startIp);
			assertTrue(record1.split(Aryaka.DELIMITER)[1].equals(result1));
			
			//query range endip
			String record2 = getRandomData(sampledata);
			String endIp = record2.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[1];
			String result2 = queryData.queryCity(endIp);
			assertTrue(record2.split(Aryaka.DELIMITER)[1].equals(result2));
			
			//query mid endip
			String record3 = getRandomData(sampledata);
			String rangeip = incrementIp(record3.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0]);
			String result3 = queryData.queryCity(rangeip);
			assertTrue(record3.split(Aryaka.DELIMITER)[1].equals(result3));
			
			datagenThread.interrupt();
			dataloadThread.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if (null != dataFiles) {
				FileUtil.cleanup(dataFiles);
			}
			FileUtil.cleanup(storePath + File.separator + Aryaka.LOAD_DIR);
		}
	}

	private String incrementIp(String ip) {
		int[] seg = Arrays.stream(ip.split("\\.")).mapToInt(Integer::valueOf).toArray();
		int[] newseg = seg;
		if (seg[3] == 255) {
			newseg[3] = 0;
			if (seg[2] == 255) {
				newseg[2] = 0;
				if (seg[1] == 255) {
					newseg[1] = 0;
					if (seg[0] == 255) {
						throw new RuntimeException("out of range ip:" + ip);
					} else {
						newseg[0] = newseg[0] + 1;
					}
				} else {
					newseg[1] = newseg[1] + 1;
				}
			} else {
				newseg[2] = newseg[2] + 1;
			}
		} else {
			newseg[3] = newseg[3] + 1;
		}
		return newseg[0] + "." + newseg[1] + "." + newseg[2] + "." + newseg[3];
	}

	private String getRandomData(List<String> sampledata) {
		Random random = new Random();
		int index = random.nextInt(sampledata.size() - 1);
		return sampledata.get(index);
	}

	private List<String> getSampleData(List<File> dataFiles) throws Exception {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(dataFiles.get(0));
			br = new BufferedReader(fr);
			String line = null;
			List<String> lines = new ArrayList<String>();
			int counter = 0;
			while ((line = br.readLine()) != null) {
				counter++;
				lines.add(line);
				if (counter > 10) {
					break;
				}
			}
			return lines;

		} finally {
			FileUtil.closeStream(fr, br);
		}
	}
	
	class GenerateData implements Runnable {

		private IPGenerator ipgen;
		GenerateData(String resourceDir) {
			this.ipgen =  new IPGenerator(100, 10, 100, resourceDir, true);
		}
		@Override
		public void run() {
			try {
				ipgen.generate();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		public List<File> getFiles() {
			return ipgen.getOutfiles();
		}
		
		
	}
	
	class DataLoader implements Runnable {

		private String storePath;
		private List<File> dataFiles;
		DataLoader(String storePath, List<File> dataFiles) {
			this.storePath = storePath;
			this.dataFiles = dataFiles;
		}
		@Override
		public void run() {
			try {
				LoadData loadData = new DynamicLoadData(storePath);
				loadData.loadData(dataFiles);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}

}
