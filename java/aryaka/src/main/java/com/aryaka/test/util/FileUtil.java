package com.aryaka.test.util;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.LoadResult;

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
			deleteFile(file);
		}
	}

	public static void deleteFile(File file) {
		if(!file.delete()) {
		  System.out.println("Failed to delete:"+file.getAbsolutePath());
		}
	}
	
	public static void deleteFile(String filePath) {
		deleteFile(new File(filePath));
	}

	public static void cleanup(String storePath) {
		File file = new File(storePath);
		File files[] = file.listFiles();
		cleanup(Arrays.asList(files));
	}
	
	public static void createDir(String... paths) {
		for(String path: paths) {
			File pathDir = new File(path);
			if (!pathDir.exists() && !pathDir.mkdir()) {
				System.out.println("Failed to create directory:" + pathDir.getAbsolutePath());
			}
		}
	}
	
	public static List<LoadResult> getMetaData(File meta) throws Exception {
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

	public static void renameFile(File tempMeta, File meta) {
		if(!tempMeta.renameTo(meta)) {
			System.out.println("Failed to rename temp meta to meta file");
		}
		
	}

	public static String getUniqueFileName() {
		return UUID.randomUUID().toString();
	}
	
	public static File getTempMetaFile(String storePath) {
		File tempMeta = new File(storePath + File.separator + Aryaka.LOAD_DIR, Aryaka.TEMP_META_FILE);
		return tempMeta;
	}

	public static File getMetaFile(String storePath) {
		File meta = new File(storePath + File.separator + Aryaka.LOAD_DIR, Aryaka.META_FILE);
		return meta;
	}

	public static File getMetaLock(String storePath) {
		File metaLock = new File(storePath + File.separator + Aryaka.LOAD_DIR, Aryaka.META_LOCK);
		return metaLock;
	}
	
	public static void lockMetaFile(File metaLock) throws IOException {
		metaLock.createNewFile();
	}
	
	public static void unlockMetaFile(File metaLock) {
		FileUtil.deleteFile(metaLock);
	}
}
