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

	// Total no of record
	private int noOfRecord;

	// distinct city count
	private int cityCardinality;

	// records per file
	private int recordsperfile;

	// output directory
	private String outDir;

	// resultant outputfiles. It will get filled up by this generator
	private List<File> outfiles;

	// continuosly updating file
	private boolean continuosUpdate;

	public IPGenerator(int noOfRecord, int cityCardinality, int recordsperfile, String outDir,
			boolean continuosUpdate) {
		this.noOfRecord = noOfRecord;
		this.cityCardinality = cityCardinality;
		this.recordsperfile = recordsperfile;
		this.outDir = outDir;
		this.continuosUpdate = continuosUpdate;
		this.outfiles = new ArrayList<File>();
	}

	public static void main(String[] args) {
		try {
			int noOfRecord = 10000000;
			int cityCardinality = 10000;
			int recordsperfile = 2000000;
			String outDir = "/Users/ashok.kumar/github/allprojects/java/aryaka/data/big/";
			IPGenerator ipgen = new IPGenerator(noOfRecord, cityCardinality, recordsperfile, outDir, true);
			ipgen.generateData();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void generateData() throws Exception {
		if (continuosUpdate) {
			appendContinuosly();
		} else {
			generate();
		}

	}

	private void appendContinuosly() throws Exception {

		// 223.225.198.0 - 223.225.198.255 = Delhi, India
		// 10,1,0,200-10,1,1,200
		Random cityRand = new Random();
		Random rangeRand = new Random();
		FileWriter fw = null;
        BufferedWriter bw = null;
		try {
			long startTime = System.currentTimeMillis();
			fw = new FileWriter(getOutFile());
			bw = new BufferedWriter(fw);
			for (int i = 0; i < noOfRecord; i++) {
				if((System.currentTimeMillis()-startTime)>Aryaka.WORK_TIME) {
					bw.flush();
					System.out.println("Generated "+i+" records. Sleeping for "+Aryaka.SLEEP_TIME + " ms");
					Thread.sleep(Aryaka.SLEEP_TIME);
					startTime = System.currentTimeMillis();
				}
				String ip = getNextIp(rangeRand.nextInt(255));
				String city = "city" + cityRand.nextInt(cityCardinality);
				String line = ip + Aryaka.DELIMITER + city;
				bw.write(line + "\n");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			FileUtil.closeStream(bw, fw);
		}
	}

	public void generate() throws Exception {
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
	}

	private void writeRecords(List<String> lines) throws Exception {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = getOutFile();
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

	private File getOutFile() {
		String filename = System.currentTimeMillis() + Aryaka.FILE_EXT;
		File file = new File(outDir, filename);
		System.out.println("Writting file:" + file.getAbsolutePath());
		outfiles.add(file);
		return file;
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
		int[] lastbutoneip = getLastButOneIp();//new int[] { currentip[0], currentip[1], currentip[2], currentip[3] - 1 };
		strconvert(buf, lastbutoneip);
		return buf.toString();
	}

	private int[] getLastButOneIp() {
		int first=currentip[0];
		int second = currentip[1];
		int third = currentip[2];
		int fourth = currentip[3];
		if((fourth-1) < 0) {
			if((third -1) < 0) {
				if((second-1)<0) {
					if((first -1)<0) {
						throw new RuntimeException("underbound");
					} else {
						first = first-1;
					}
				} else {
					second = second -1;
				}
			} else {
				third = third -1;
			}
		} else {
			fourth = fourth -1;
		}
		
		return new int[] {first,second,third,fourth};
	}

	private void strconvert(StringBuffer buf, int ip[]) {
		buf.append(ip[0]).append(".").append(ip[1]).append(".").append(ip[2]).append(".").append(ip[3]);
	}

	public List<File> getOutfiles() {
		return outfiles;
	}
}
