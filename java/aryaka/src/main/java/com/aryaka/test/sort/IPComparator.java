package com.aryaka.test.sort;

import java.util.Comparator;

import javax.management.RuntimeErrorException;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.util.Utility;

public class IPComparator implements Comparator<String> {

	public int compare(String line1, String line2) {
		try {
			long ip1 = Utility.convert2Long(line1.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0]);
			long ip2 =  Utility.convert2Long(line2.split(Aryaka.DELIMITER)[0].split(Aryaka.IP_RANGE_DELIMITER)[0]);
			int ipcompres = Long.compare(ip1, ip2);
			if(ipcompres == 0) {
			   long loadTime1 = Long.parseLong(line1.split(Aryaka.DELIMITER)[2]);
			   long loadTime2 = Long.parseLong(line2.split(Aryaka.DELIMITER)[2]);
			   return Long.compare(loadTime1, loadTime2);
			}
		    return ipcompres;
		} catch(Exception e) {
			e.printStackTrace();
			
			throw new RuntimeErrorException(new Error("Error in comparator"));
		}
		
	}

}