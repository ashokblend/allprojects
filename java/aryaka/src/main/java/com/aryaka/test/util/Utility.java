package com.aryaka.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.Chunk;
import com.aryaka.test.model.LoadResult;
import com.aryaka.test.sort.CustomFileBuffer;
import com.aryaka.test.sort.CustomLoadBuffer;
import com.aryaka.test.sort.RecordStack;

public class Utility {

	public static Chunk binarySearch(Chunk chunks[], int first, int last, String ip) throws Exception {
		int mid = (first + last) / 2;
		long searchip = convert2Long(ip);
		while (first <= last) {
			long startIp = convert2Long(chunks[mid].getStartIp());
			long endIp = convert2Long(chunks[mid].getEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				return chunks[mid];
			} else if (startIp < searchip) {
				first = mid + 1;
			} else {
				last = mid - 1;
			}
			mid = (first + last) / 2;
		}
		if (first > last) {
			System.out.println("Element is not found!");
		}
		return null;
	}

	public static List<LoadResult> binarySearch(List<LoadResult> loadResults, int first, int last, String ip)
			throws Exception {
		int mid = (first + last) / 2;
		long searchip = convert2Long(ip);
		List<LoadResult> results = new ArrayList<LoadResult>();
		while (first <= last) {
			long startIp = convert2Long(loadResults.get(mid).getLoadStartIp());
			long endIp = convert2Long(loadResults.get(mid).getLoadEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				results.add(loadResults.get(mid));
				searchForward(results, mid, loadResults, searchip);
				searchBackward(results, mid, loadResults, searchip);
				return results;
			} else if (startIp < searchip) {
				first = mid + 1;

			} else {
				last = mid - 1;
			}
			mid = (first + last) / 2;
		}
		if (first > last) {
			System.out.println("Element is not found!");
		}
		return null;
	}

	public static List<LoadResult> search(List<LoadResult> loads, String ip) throws Exception {
		List<LoadResult> results = new ArrayList<LoadResult>();
		long searchip = convert2Long(ip);
		for (LoadResult load : loads) {
			long startIp = convert2Long(load.getLoadStartIp());
			long endIp = convert2Long(load.getLoadEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				results.add(load);
			}
		}
		return results;
	}

	private static void searchBackward(List<LoadResult> results, int mid, List<LoadResult> loadResults, long searchip)
			throws Exception {
		for (int i = mid - 1; i > 0; i--) {
			long startIp = convert2Long(loadResults.get(i).getLoadStartIp());
			long endIp = convert2Long(loadResults.get(i).getLoadEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				results.add(loadResults.get(i));
			} else {
				break;
			}
		}

	}

	private static void searchForward(List<LoadResult> results, int mid, List<LoadResult> loads, long searchip)
			throws Exception {
		for (int i = mid + 1; i < loads.size(); i++) {
			long startIp = convert2Long(loads.get(i).getLoadStartIp());
			long endIp = convert2Long(loads.get(i).getLoadEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				results.add(loads.get(i));
			} else {
				break;
			}
		}
	}

	public static long convert2Long(String ip) throws Exception {
		long result = -1l;
		try {
			byte[] octets = InetAddress.getByName(ip).getAddress();
			for (byte octet : octets) {
				result <<= 8;
				result |= octet & 0xff;
			}
		} catch (Exception e) {
			throw new Exception("Error converting ip to long", e);
		}
		return result;
	}

	public static String binarySearch(byte[][] data, int first, int last, String ip) throws Exception {
		int mid = (first + last) / 2;
		long searchip = convert2Long(ip);
		while (first <= last) {
			String line = new String(data[mid]);
			long startIp = convert2Long(line.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0]);
			long endIp = convert2Long(line.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[1]);
			if (searchip >= startIp && searchip <= endIp) {
				return line;
			} else if (startIp < searchip) {
				first = mid + 1;
			} else {
				last = mid - 1;
			}
			mid = (first + last) / 2;
		}
		if (first > last) {
			System.out.println("Element is not found!");
		}
		return null;
	}

	public static int getNofTask(int filesPerTask, int noOfFiles) {

		if (noOfFiles % filesPerTask == 0) {
			return noOfFiles / filesPerTask;
		} else {
			return noOfFiles / filesPerTask + 1;
		}

	}

	/**
	 * merge sorted files
	 * 
	 * @param files
	 * @param outputfile
	 * @param cmp
	 * @return
	 * @throws IOException
	 */
	public static int mergeSortedFiles(List<File> files, File outputfile, final Comparator<String> cmp)
			throws Exception {
		ArrayList<RecordStack> bfbs = new ArrayList<RecordStack>();
		for (File f : files) {
			InputStream in = new FileInputStream(f);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));

			RecordStack bfb = new CustomFileBuffer(br);
			bfbs.add(bfb);
		}
		BufferedWriter fbw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outputfile, true), Charset.defaultCharset()));
		int rowcounter = mergeSortedFiles(fbw, cmp, bfbs);
		for (File f : files) {
			f.delete();
		}
		return rowcounter;
	}
	
	public static int mergeSortedLoads(List<LoadResult> loads, File outputfile, final Comparator<String> cmp)
			throws Exception {
		ArrayList<RecordStack> bfbs = new ArrayList<RecordStack>();
		for (LoadResult load : loads) {
			RecordStack bfb = new CustomLoadBuffer(load);
			bfbs.add(bfb);
		}
		BufferedWriter fbw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outputfile, true), Charset.defaultCharset()));
		return mergeSortedFiles(fbw, cmp, bfbs);
		
	}

	/**
	 * This will merge sorted files
	 * 
	 * @param fbw
	 * @param cmp
	 * @param buffers
	 * @return no of row sorted while merging
	 * @throws IOException
	 */
	public static int mergeSortedFiles(BufferedWriter fbw, final Comparator<String> cmp, List<RecordStack> buffers)
			throws Exception {
		PriorityQueue<RecordStack> pq = new PriorityQueue<RecordStack>(11,
				new Comparator<RecordStack>() {
					public int compare(RecordStack i, RecordStack j) {
						return cmp.compare(i.peek(), j.peek());
					}
				});
		for (RecordStack bfb : buffers) {
			if (!bfb.empty()) {
				pq.add(bfb);
			}
		}
		int rowcounter = 0;
		try {

			String lastLine = null;
			if (pq.size() > 0) {
				RecordStack bfb = pq.poll();
				lastLine = bfb.pop();
				fbw.write(lastLine);
				fbw.newLine();
				++rowcounter;
				if (bfb.empty()) {
					bfb.close();
				} else {
					pq.add(bfb);
				}
			}
			while (pq.size() > 0) {
				RecordStack bfb = pq.poll();
				String r = bfb.pop();

				fbw.write(r);
				fbw.newLine();
				lastLine = r;
				++rowcounter;
				if (bfb.empty()) {
					bfb.close();
				} else {
					pq.add(bfb);
				}
			}

		} finally {
			fbw.close();
			for (RecordStack bfb : pq) {
				bfb.close();
			}
		}
		return rowcounter;

	}

}
