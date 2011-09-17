/**
 * 
 */
package org.budget.logger.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.budget.logger.model.Record;
import org.budget.logger.services.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author kanshin
 *
 */
@Controller
@RequestMapping("/export")
public class ExportController {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IRecordService recordService;

	@RequestMapping(method = RequestMethod.GET)
	public void export(HttpServletResponse response) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader(
					"Content-disposition",
					"attachment; filename=budget-log-"
							+ DATE_FORMAT.format(new Date()) + ".csv");
			Collection<Record> records = recordService.getAllRecords();
			pw.println("id;type;date;amount;category;description;");
			for (Record r : records) {
				
				pw.printf("%s;%s;%s;%f;%s;%s;\n", r.getId(), r.getType().name(),
						DATE_FORMAT.format(r.getDate()), r.getAmount().doubleValue(),
						r.getCategory(), r.getDesc());
				pw.flush();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			if (null != pw) {
				pw.close();
			}
		}
	}

}
