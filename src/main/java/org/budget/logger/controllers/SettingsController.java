/**
 * 
 */
package org.budget.logger.controllers;

import org.budget.logger.model.Category;
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
@RequestMapping("/settings")
public class SettingsController {

	@Autowired
	private IRecordService recordService;

	@RequestMapping(value = "/error", method = RequestMethod.GET)
	public String error(ModelMap modelMap) {
		throw new RuntimeException("Test exception");
	}

	@RequestMapping(method = RequestMethod.GET)
	public String show(ModelMap modelMap) {
		modelMap.addAttribute("categories", recordService.getCategories());
		return "settings";
	}

	@RequestMapping(value = "/category/save", method = RequestMethod.POST)
	public String saveCategory(@RequestParam String categoryId,
			@RequestParam String category, @RequestParam Boolean report,
			@RequestParam Boolean isDefault, ModelMap modelMap) {
		if (null == category || null == categoryId) {
			modelMap.addAttribute("errorKey", "jsp.settings.error.name.empty");
			return show(modelMap);
		}
		if (!"".equals(categoryId)) {
			Category cat = recordService.getCaterory(categoryId);
			cat.setDef(null != isDefault ? isDefault : false);
			cat.setName(category);
			cat.setReport(null != report ? report : true);
			recordService.updateCaterory(cat);
		} else {
			Category cat = recordService.getCateroryByName(category);
			if (null == cat) {
				recordService.createCategory(category, isDefault, report);
			} else {
				modelMap.addAttribute("errorKey", "jsp.settings.error.category.already.exists");
			}
		}
		return show(modelMap);
	}
	
	@RequestMapping(value = "/category/{id}/delete", method = RequestMethod.GET)
	public String deleteCategory(@PathVariable String id, ModelMap modelMap) {
		recordService.deleteCaterory(id);
		return show(modelMap);
	}
	
	@RequestMapping(value = "/multiple", method = RequestMethod.POST)
	public String multiple(@RequestParam Double amount, ModelMap modelMap) {
		recordService.multipleAmount(amount);
		return show(modelMap);
	}
}
