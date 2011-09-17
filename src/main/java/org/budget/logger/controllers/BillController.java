/**
 * 
 */
package org.budget.logger.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.budget.logger.model.Type;
import org.budget.logger.services.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author kanshin
 *
 */
@Controller
@RequestMapping("/bill")
public class BillController {

	private static final DateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IRecordService recordService;

	@RequestMapping(method = RequestMethod.GET)
	public String show(ModelMap modelMap) {
		modelMap.addAttribute("today", FORMAT.format(new Date()));
		modelMap.addAttribute("categories", recordService.getCategories());
		return "bill";
	}
	
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(HttpServletRequest request, ModelMap modelMap) {
		Date createDate = null;
		try {
			createDate = FORMAT.parse(request.getParameter("date"));
		} catch (ParseException e) {
			logger.warn(e.getMessage());
			modelMap.addAttribute("errorCode", "jsp.records.error.dateformat");
			return show(modelMap);
		}
		String category = request.getParameter("category");
		Integer rowCount = Integer.parseInt(request.getParameter("rowCount"));
		Map<Double, String> records = new HashMap<Double, String>();
		try {
			for (int i = 1; i <= rowCount; i++) {
				if (null != request.getParameter("sum" + i) && null != request.getParameter("desc" + i) 
						&& !"".equals(request.getParameter("sum" + i)) && !"".equals(request.getParameter("desc" + i))) {
					Double sum = Double.parseDouble(request.getParameter("sum" + i));
					String desc = request.getParameter("desc" + i);
					records.put(sum, desc);
				}
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			modelMap.addAttribute("errorCode", "jsp.bill.error.sum.format");
			Map<String, String> rows = new LinkedHashMap<String, String>();
			for (int i = 1; i <= rowCount; i++) {
				if (null != request.getParameter("sum" + i) && null != request.getParameter("desc" + i) 
						&& !"".equals(request.getParameter("sum" + i)) && !"".equals(request.getParameter("desc" + i))) {
					rows.put(request.getParameter("sum" + i), request.getParameter("desc" + i));
				}
			}
			modelMap.addAttribute("rows", rows);
			return show(modelMap);
		}
		try {
			for (Double sum : records.keySet()) {
				String desc = records.get(sum);
				sum = Math.abs(sum);
				recordService.createRecord(category, desc, Type.OUTCOME, createDate, sum);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
			modelMap.addAttribute("errorCode", "jsp.error.happens");
			return show(modelMap);
		}
		modelMap.addAttribute("messageCode", "jsp.bill.message.bill-created");
		return show(modelMap);
	}
	
}
