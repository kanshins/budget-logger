/**
 * 
 */
package org.budget.logger.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.budget.logger.helpers.DateHelper;
import org.budget.logger.model.Category;
import org.budget.logger.model.Record;
import org.budget.logger.model.Type;
import org.budget.logger.services.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kanshin
 *
 */
@Controller
@RequestMapping("/records")
public class RecordsController {
	
	private static final DateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	
	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IRecordService recordService;

	@RequestMapping(method = RequestMethod.GET)
	public String show(ModelMap modelMap) {
		return show(null, FORMAT.format(DateHelper.getPreviousMonth()), null, modelMap);
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String show(@RequestParam String cat, @RequestParam String from, @RequestParam String to, ModelMap modelMap) {
		Date dateFrom = null;
		Date dateTo = null;
		Category category = null;
		if (null != cat) {
			category = recordService.getCateroryByName(cat);
		}
		try {
			if (null != from && !"".equals(from)) {
				dateFrom = FORMAT.parse(from);
			}
			if (null != to && !"".equals(to)) {
				dateTo = FORMAT.parse(to);
			}
		} catch (ParseException e) {
			logger.warn(e.getMessage());
		}
		Collection<Record> records = recordService.getRecords(null, dateFrom, dateTo, category);
		Double totalIncome = 0.0;
		Double totalOutcome = 0.0;
		Double totalStoring = 0.0;
		for (Record r : records) {
			if (null == r || null == r.getAmount()) {
				logger.warn("Bad record: " + r);
				continue;
			}
			if (r.getType().equals(Type.INCOME)) {
				totalIncome += r.getAmount();
			} else if (r.getType().equals(Type.OUTCOME)) {
				totalOutcome += r.getAmount();
			} else if (r.getType().equals(Type.STORING)) {
				totalStoring += r.getAmount();
			}
		}
		modelMap.addAttribute("records", records);
		modelMap.addAttribute("totalIncome", totalIncome);
		modelMap.addAttribute("totalOutcome", totalOutcome);
		modelMap.addAttribute("totalStoring", totalStoring);
		modelMap.addAttribute("from", from);
		modelMap.addAttribute("to", to);
		modelMap.addAttribute("cat", cat);
		if (!modelMap.containsAttribute("today")) {
			modelMap.addAttribute("today", FORMAT.format(new Date()));
		}
		modelMap.addAttribute("categories", recordService.getCategories());
		return "records";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(@RequestParam String recordId, @RequestParam String date,
			@RequestParam Integer type, @RequestParam Double amount,
			@RequestParam String category, @RequestParam String desc,
			@RequestParam String from, @RequestParam String to, ModelMap modelMap) {
		Date createDate = null;
		try {
			createDate = FORMAT.parse(date);
		} catch (ParseException e) {
			logger.warn(e.getMessage());
			modelMap.addAttribute("errorCode", "jsp.records.error.dateformat");
		}
//		validate(recordId, createDate, type, amount, name, desc, errors);
//		if (errors.hasErrors()) {
//			return show(modelMap);
//		}
		if (null == recordId || "".equals(recordId.trim())) {
			recordService.createRecord(category, desc, Type.valueOf(type), createDate, amount);
		} else {
			Record r = recordService.getRecord(recordId);
			if (null == r) {
				logger.warn("Object with id '" + recordId + "' was not found");
				modelMap.addAttribute("errorCode", "jsp.records.error.objectnotfound");
				return show(modelMap);
			}
			r.setAmount(amount);
			r.setDate(createDate);
			r.setDesc(desc);
			r.setCategory(category);
			r.setType(Type.valueOf(type));
			recordService.updateRecord(r);
		}
		modelMap.addAttribute("today", date);
		return show(null, from, to, modelMap);
	}
	
//	private void validate(Long recordId, Date date, Integer type,
//			Double amount, String name, String desc, Errors errors) {
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "jsp.records.error.nameisempty");
//	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable String id, @RequestParam String from,
			@RequestParam String to, ModelMap modelMap) {
		recordService.deleteRecord(id);
		return show(null, from, to, modelMap);
	}
}
