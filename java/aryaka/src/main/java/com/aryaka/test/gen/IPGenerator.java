package com.aryaka.test.gen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.aryaka.test.model.Aryaka;
import com.aryaka.test.util.FileUtil;

/**
 * 
 * @author ashok.kumar
 *
 */
public class IPGenerator {

	int currentip[] = new int[] { 1, 0, 0, 0 };

	//Total no of record
	private int noOfRecord;

	//distinct city count
	private int cityCardinality;

	//records per file
	private int recordsperfile;

	//output directory
	private String outDir;

	//resultant outputfiles. It will get filled up by this generator
	private List<File> outfiles;

	public IPGenerator(int noOfRecord, int cityCardinality, int recordsperfile, String outDir) {
		this.noOfRecord = noOfRecord;
		this.cityCardinality = cityCardinality;
		this.recordsperfile = recordsperfile;
		this.outDir = outDir;
		this.outfiles = new ArrayList<File>();
	}

	public static void main(String[] args) {
		try {
			int noOfRecord = 10000000;
			int cityCardinality = 10000;
			int recordsperfile = 2000000;
			String outDir = "/Users/ashok.kumar/github/allprojects/java/aryaka/data/big/";
			IPGenerator ipgen = new IPGenerator(noOfRecord, cityCardinality, recordsperfile, outDir);
			ipgen.generate();
		} catch (Exception e) {
            e.printStackTrace();
		}

	}

	public List<File> generate() throws Exception {
		// 223.225.198.0 - 223.225.198.255 = Delhi, India
		List<String> lines = new ArrayList<String>();
		// 10,1,0,200-10,1,1,200
		Random cityRand = new Random();
		Random rangeRand = new Random();

		for (int i = 0; i < noOfRecord; i++) {
			String ip = getNextIp(rangeRand.nextInt(255));
			String city = "city" + cityRand.nextInt(cityCardinality);
			String line = ip + Aryaka.DELIMITER + city;
			// System.out.println(line);
			if (lines.size() == recordsperfile) {
				writeRecords(lines);
				lines.clear();
			}
			lines.add(line);
		}
		writeRecords(lines);
		return outfiles;
	}

	private void writeRecords(List<String> lines) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			String filename = System.currentTimeMillis() + Aryaka.FILE_EXT;
			File file = new File(outDir, filename);
			System.out.println("Writting file:" + file.getAbsolutePath());
			outfiles.add(file);
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			for (String line : lines) {
				bw.write(line + "\n");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.closeStream(bw, fw);
		}
	}

	private String getNextIp(int range) {
		StringBuffer buf = new StringBuffer();
		strconvert(buf, currentip);

		for (int i = 0; i < range; i++) {
			if (currentip[3] == 255) {
				if (currentip[2] == 255) {
					if (currentip[1] == 255) {
						if (currentip[0] == 255) {
							throw new RuntimeException("cannot allocate more ip");
						} else {
							currentip[3] = 0;
							currentip[2] = 0;
							currentip[1] = 0;
							currentip[0] += 1;
						}
					} else {
						currentip[3] = 0;
						currentip[2] = 0;
						currentip[1] += 1;
					}
				} else {
					currentip[3] = 0;
					currentip[2] += 1;
				}
			} else {
				currentip[3] += 1;
			}
		}
		buf.append(Aryaka.IP_RANGE_DELIMITER);
		int[] lastbutoneip = new int[] { currentip[0], currentip[1], currentip[2], currentip[3] - 1 };
		strconvert(buf, lastbutoneip);
		return buf.toString();
	}

	private void strconvert(StringBuffer buf, int ip[]) {
		buf.append(ip[0]).append(".").append(ip[1]).append(".").append(ip[2]).append(".").append(ip[3]);
	}

}
