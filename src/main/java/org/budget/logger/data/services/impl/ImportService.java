/**
 * 
 */
package org.budget.logger.data.services.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Type;
import org.budget.logger.data.services.IImportService;
import org.budget.logger.data.services.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author kanshin
 * 
 */
@Service("importService")
public class ImportService implements IImportService {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IRecordService recordService;
	
	private Set<String> categories = new HashSet<String>();
	
	public static void main(String[] args) throws Exception {
		ImportService is = new ImportService();
		is.importFromCsv(new FileInputStream("/media/media/develop/projects/budget-logger/test-import.csv"));
	}
	
	@Override
	public void importFromCsv(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		//first line is comments
		reader.readLine();
		
		int index = 0;
		String line = reader.readLine();
		while (null != line) {
			try {
				RecordRow rr = parseRow(line);
				saveRow(rr);
			} catch (Exception e) {
				logger.warn("Cannot import line '" + line + "'", e);
			}
			index++;
			line = reader.readLine();
		}
		saveCategories(categories);
		logger.info("Imported " + index + " records from file");
	}
	
	private void saveRow(RecordRow rr) {
		recordService.createRecord(rr.category, rr.description, rr.type, rr.date, rr.amount);
		if (!categories.contains(rr.category)) {
			categories.add(rr.category);
		}
	}
	
	private void saveCategories(Set<String> categories) {
		for (String name : categories) {
			Category cat = recordService.getCateroryByName(name);
			if (null == cat) {
				recordService.createCategory(name, false, true);
			}
		}
	}
	
	private RecordRow parseRow(String line) throws ParseException {
		RecordRow rr = new RecordRow();
		String data[] = line.split(";");
		rr.id = data[0];
		rr.type = Type.valueOf(data[1]);
		rr.date = DATE_FORMAT.parse(data[2]);
		rr.amount = Double.parseDouble(data[3].replace(",", "."));
		rr.category = data[4];
		rr.description = data[5];
		return rr;
	}
	
	private class RecordRow {
		String id;
		Type type;
		Date date;
		Double amount;
		String category;
		String description;
		@Override
		public String toString() {
			return "RecordRow [id=" + id + ", type=" + type + ", date=" + date
					+ ", amount=" + amount + ", category=" + category
					+ ", description=" + description + "]";
		}
		
	}
}
