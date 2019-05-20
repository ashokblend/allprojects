package com.aryaka.test.sort;

import java.util.Comparator;

import javax.management.RuntimeErrorException;

import com.aryaka.test.model.LoadResult;
import com.aryaka.test.util.Utility;

public class LoadResultComparator implements Comparator<LoadResult> {

	@Override
	public int compare(LoadResult o1, LoadResult o2) {
		try {
			long ip1 = Utility.convert2Long(o1.getLoadStartIp());
			long ip2 =  Utility.convert2Long(o2.getLoadStartIp());
		    return Long.compare(ip1, ip2);
		} catch(Exception e) {
			e.printStackTrace();
			
			throw new RuntimeErrorException(new Error("Error in comparator"));
		}
	}

}
