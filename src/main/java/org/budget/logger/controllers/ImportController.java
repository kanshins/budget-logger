/**
 * 
 */
package org.budget.logger.controllers;

import org.apache.log4j.Logger;
import org.budget.logger.services.IImportService;
import org.budget.logger.services.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author kanshin
 *
 */
@Controller
@RequestMapping("/import")
public class ImportController {
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IRecordService recordService;
	
	@Autowired
	private IImportService importService;

	@RequestMapping(method = RequestMethod.POST)
	public String importXls(@RequestParam CommonsMultipartFile fileData, ModelMap modelMap) {
		
		if (null != fileData) {
			if (fileData.getOriginalFilename().toLowerCase().endsWith(".csv")) {
				try {
					importService.importFromCsv(fileData.getInputStream());
					modelMap.addAttribute("messageKey", "jsp.setting.message.file.imported.successfuly");
				} catch (Exception e) {
					logger.warn(e.getMessage());
					modelMap.addAttribute("errorMessage", e.getMessage());
				}
			} else {
				modelMap.addAttribute("errorKey", "jsp.settings.error.wrong.file");
			}
			return show(modelMap);
		} else {
			modelMap.addAttribute("errorKey", "jsp.settings.error.filenotset");
		}
		return show(modelMap);
	}

	@RequestMapping(method = RequestMethod.GET)
	public String show(ModelMap modelMap) {
		modelMap.addAttribute("categories", recordService.getCategories());
		return "settings";
	}
}
