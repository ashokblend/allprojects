package com.aryaka.test.util;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.model.Chunk;
import com.aryaka.test.model.LoadResult;

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
	
	public static List<LoadResult> binarySearch(List<LoadResult> loadResults, int first, int last, String ip) throws Exception {
		int mid = (first + last) / 2;
		long searchip = convert2Long(ip);
		List<LoadResult> results = new ArrayList<LoadResult>();
		while (first <= last) {
			long startIp = convert2Long(loadResults.get(mid).getLoadStartIp());
			long endIp = convert2Long(loadResults.get(mid).getLoadEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				results.add(loadResults.get(mid));
				searchForward(results,mid, loadResults,searchip);
				searchBackward(results,mid, loadResults,searchip);
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
		for(LoadResult load: loads) {
			long startIp = convert2Long(load.getLoadStartIp());
			long endIp = convert2Long(load.getLoadEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				results.add(load);
			}
		}
		return results;
	}
	
	private static void searchBackward(List<LoadResult> results,
			int mid,
			List<LoadResult> loadResults,
			long searchip) throws Exception {
		for(int i=mid-1;i>0;i--) {
			long startIp = convert2Long(loadResults.get(i).getLoadStartIp());
			long endIp = convert2Long(loadResults.get(i).getLoadEndIp());
			if (searchip >= startIp && searchip <= endIp) {
				results.add(loadResults.get(i));
			} else {
				break;
			}
		}
		
	}

	private static void searchForward(List<LoadResult> results,
			int mid,
			List<LoadResult> loads,
			long searchip) throws Exception {
		for(int i=mid+1;i<loads.size();i++) {
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
		long result= -1l;
		try {
			byte[] octets = InetAddress.getByName(ip).getAddress();
	        for (byte octet : octets) {
	            result <<= 8;
	            result |= octet & 0xff;
	        }
		} catch (Exception e) {
          throw new Exception("Error converting ip to long",e);
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

}
