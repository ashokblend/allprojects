package com.aryaka.test.sort;

import java.util.Comparator;

import javax.management.RuntimeErrorException;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.util.Utility;

class IPComparator implements Comparator<String> {

	public int compare(String line1, String line2) {
		try {
			long ip1 = Utility.convert2Long(line1.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0]);
			long ip2 =  Utility.convert2Long(line2.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0]);
		    return Long.compare(ip1, ip2);
		} catch(Exception e) {
			e.printStackTrace();
			
			throw new RuntimeErrorException(new Error("Error in comparator"));
		}
		
	}

}