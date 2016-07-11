/*
 *  Copyright (C) 2016-2017 Tomas Bajus ICO: 04871774.
 */
package com.repo.converter.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

	public static final String OUTPUT_DIR = "output";
	public static final String PATTERN_DIR = "MM-dd'_'HH-mm-ss";

	private String now;

	private String outputDirAbsolutePath;

	public FileUtils() {
		Date now = new Date();
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_DIR);
			this.now = simpleDateFormat.format(now);
		} catch (Exception ex) {
			this.now = "2016";

		}
	}

	public boolean mkOutputDir() {
		try {
			File outputDir = new File(OUTPUT_DIR + File.separator);
			boolean mkdirs = outputDir.mkdirs();
			outputDirAbsolutePath = outputDir.getAbsolutePath();

			System.out.println("MKDIRS = "+outputDirAbsolutePath);
			if (mkdirs) {
				return true;
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	public String getOutPutDir() {
		return OUTPUT_DIR + File.separator + now + File.separator;
	}

	public String getOutputDirAbsolutePath() {
		return this.outputDirAbsolutePath;
	}

	public String getGoogleAnalyticsFileName() {
		return outputDirAbsolutePath + File.separator + now +"_GoogleAnalytics.txt";
	}

	public String getGoogleAnalyticsReportFileName() {
		return outputDirAbsolutePath + File.separator + now +"_GoogleAnalytics_report.txt";
	}
}
