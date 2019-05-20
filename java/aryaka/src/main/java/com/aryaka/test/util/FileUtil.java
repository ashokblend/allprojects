package com.aryaka.test.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

	public static void closeStream(Closeable... streams) {
		try {
			for (Closeable stream : streams) {
				if (null != stream) {
					stream.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void writeFile(File tmpFile, List<String> tmpSets) throws Exception {
		BufferedWriter bw = null ;
		try {
			bw = new BufferedWriter(new FileWriter(tmpFile));
			for(String str: tmpSets) {
				bw.write(str+"\n");
			}
		} catch (Exception e) {
			throw new Exception("Failed to write file:"+tmpFile.getAbsolutePath(), e);
		} finally {
			closeStream(bw);
		}
		
	}

	public static void cleanup(List<File> dataFiles) {
		for(File file: dataFiles) {
			if(!file.delete()) {
			  System.out.println("Failed to delete:"+file.getAbsolutePath());
			}
		}
	}

	public static void cleanup(String storePath) {
		File file = new File(storePath);
		File files[] = file.listFiles();
		cleanup(Arrays.asList(files));
	}

}
