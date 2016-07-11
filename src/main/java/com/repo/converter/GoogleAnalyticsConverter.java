/*
 *  Copyright (C) 2016-2017 Tomas Bajus ICO: 04871774.
 */
package com.repo.converter;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.repo.converter.network.UrlBuilder;

public class GoogleAnalyticsConverter {

	private static final Logger LOG = LoggerFactory.getLogger(GoogleAnalyticsConverter.class);

	private final static int ID_ROW = 1;
	private final static int VALUES_ROW = 0;
	private String path;

	public GoogleAnalyticsConverter(String path) {
		this.path = path;
	}

	public List<String> convert() throws Throwable {
		FileInputStream file = new FileInputStream(new File(path));
		List<String> urls = new ArrayList<>();

		HSSFWorkbook workbook = new HSSFWorkbook(file);
		HSSFSheet sheet = workbook.getSheetAt(0);
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			Cell id = row.getCell(ID_ROW);
			String idVal = getId(readVal(id));
			if (idVal == null  || !idVal.contains(".")) {
				LOG.warn("Wrong id found. There is no '.' in id = "+idVal);
			}
			UrlBuilder urlBuilder = constructUrlFromExport(rowIterator, idVal);
			if (!"null".equals(idVal)) {
				String url = urlBuilder.build();
				urls.add(url);
			}
		}
		file.close();
		return urls;
	}

	private UrlBuilder constructUrlFromExport(final Iterator<Row> rowIterator, final String id) {
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setCid(id);
		Row next = null;
		if (rowIterator.hasNext()) {
			next = rowIterator.next();
			 readVal(next.getCell(VALUES_ROW));
		}
		if (rowIterator.hasNext()) {
			next = rowIterator.next();
			urlBuilder.setEl(readVal(next.getCell(VALUES_ROW)));
		}
		if (rowIterator.hasNext()) {
			next = rowIterator.next();
			urlBuilder.setEa(readVal(next.getCell(VALUES_ROW)));
		}
		return urlBuilder;
	}

	private String readVal(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case Cell.CELL_TYPE_NUMERIC:
				return String.valueOf(cell.getNumericCellValue());
			case Cell.CELL_TYPE_STRING:
				return cell.getStringCellValue();
		}
		return cell.toString();
	}

	private String getId(String val) {
		String valToReturn;
		if (val == null || val.isEmpty()) {
			return null;
		}
		if (Character.isDigit(val.charAt(0)) && Character.isDigit(val.charAt(val.length()-1))) {
			valToReturn =  val;
			return removeAllDotsExceptForLastOne(valToReturn);
		}

		Integer begin = null;
		int end = val.length();
		for(int index=0;index < val.length(); index ++) {
			if (begin == null && Character.isDigit(val.charAt(index))) {
				begin=index;
			}
			if (Character.isDigit(val.charAt(index))) {
				end = index;
			}
		}
		if (begin == null && end == val.length()) {
			return val;
		}

		return removeAllDotsExceptForLastOne(val.substring(begin, end));
	}

	private String removeAllDotsExceptForLastOne(String id) {

		/*int lastDotLocation = id.lastIndexOf(".");
		if (lastDotLocation < 0) {
			return id;
		}
		String firstHalf = id.substring(0, lastDotLocation);
		String secondHalf = id.substring(lastDotLocation);
		firstHalf = firstHalf.replaceAll("\\.", "");
		return firstHalf + secondHalf;*/
		return id.substring(4);
	}
}
