/**
 * 
 */
package org.budget.logger.controllers;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.budget.logger.helpers.DateHelper;
import org.budget.logger.model.Category;
import org.budget.logger.model.Record;
import org.budget.logger.services.IRecordService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author kanshin
 *
 */
@Controller
@RequestMapping("/reports/detailed")
public class ReportDetailedController extends ApplicationObjectSupport {

	private static final DateFormat FORMAT = new SimpleDateFormat("dd-MM-yyyy");

	private final Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private IRecordService recordService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String show(ModelMap modelMap) {
		return showForDate(FORMAT.format(DateHelper.getPreviousMonth()), FORMAT.format(DateHelper.getNextDay()), modelMap);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String showForDate(@RequestParam String from,
			@RequestParam String to, ModelMap modelMap) {

		Collection<Category> categories = recordService.getCategories();

		modelMap.addAttribute("from", from);
		modelMap.addAttribute("to", to);
		modelMap.addAttribute("categories", categories);
		return "reports/detailed";
	}
	
	@RequestMapping(value = "/chartcat", method = RequestMethod.GET)
	public void chartCategory(@RequestParam String from, @RequestParam String to,
			@RequestParam String catid, HttpServletResponse response) {
		Date dateFrom = null;
		Date dateTo = null;
		try {
			if (null != from && !"".equals(from)) {
				dateFrom = FORMAT.parse(from);
			}
			if (null != to && !"".equals(to)) {
				dateTo = FORMAT.parse(to);
			}
		} catch (Exception e) {
			logger.warn("from=" + from + " to=" + to + "\n" + e.getMessage());
		}
		Category cat = recordService.getCaterory(catid);
		if (null == cat) {
			logger.warn("Cannot find category with id=" + catid);
			return;
		}
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Collection<Record> records = recordService.getRecords(null, dateFrom, dateTo, cat);
		
		Map<String, Double> map = new HashMap<String, Double>();
		Double maxAmount = 0.0;
		for (Record r : records) {
			if (null == r || null == r.getAmount()) {
				logger.warn("Bad record: " + r);
				continue;
			}
			String name = r.getDesc().trim().toLowerCase();
			if (null != map.get(name)) {
				Double amount = map.get(name);
				amount += r.getAmount();
				map.put(name, amount);
			} else {
				map.put(name, r.getAmount());
			}
			if (maxAmount < map.get(name)) {
				maxAmount = map.get(name);
			}
		}
		for (String label : map.keySet()) {
			dataset.addValue( map.get(label), "S1", label);
		}
		
		JFreeChart jfreechart = ChartFactory.createBarChart(cat.getName(),
				null, null, dataset, PlotOrientation.HORIZONTAL, false, false,
				false);
        TextTitle title = new TextTitle(cat.getName());
        title.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
        jfreechart.setTitle(title);
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);
        categoryplot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8F);
        
        NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer barrenderer = (BarRenderer)categoryplot.getRenderer();
        barrenderer.setDrawBarOutline(false);
        barrenderer.setSeriesPaint(0, new Color(77, 77, 250));
        barrenderer.setShadowVisible(false);
        
        try {
			ChartUtilities.writeChartAsJPEG(response.getOutputStream(),
					jfreechart, 500, getSizeY(map.size()));
			response.getOutputStream().close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}
	
	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public void chart(@RequestParam String from, @RequestParam String to,
			HttpServletResponse response) {
		Date dateFrom = null;
		Date dateTo = null;
		try {
			if (null != from && !"".equals(from)) {
				dateFrom = FORMAT.parse(from);
			}
			if (null != to && !"".equals(to)) {
				dateTo = FORMAT.parse(to);
			}
		} catch (Exception e) {
			logger.warn(e.getMessage());
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Collection<Category> categories = recordService.getCategories();
		for (Category cat : categories) {
			if (!cat.getReport()) {
				continue;
			}
			Collection<Record> records = recordService.getRecords(null, dateFrom, dateTo, cat);
			Double amount = 0.0;
			for (Record r : records) {
				if (null == r || null == r.getAmount()) {
					logger.warn("Bad record: " + r);
					continue;
				}
				amount += r.getAmount();
			}
			dataset.addValue(amount, "S1", cat.getName());
		}
		String title = getMessageSourceAccessor().getMessage("jsp.report.detailed.all");
		JFreeChart jfreechart = ChartFactory.createBarChart(title, null, null,
				dataset, PlotOrientation.HORIZONTAL, false, false, false);
        TextTitle textTitle = new TextTitle(title);
        textTitle.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
        jfreechart.setTitle(textTitle);
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);
        categoryplot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8F);
        
        NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer barrenderer = (BarRenderer)categoryplot.getRenderer();
        barrenderer.setDrawBarOutline(false);
        barrenderer.setSeriesPaint(0, new Color(77, 77, 250));
        barrenderer.setShadowVisible(false);
        
        try {
			ChartUtilities.writeChartAsJPEG(response.getOutputStream(),
					jfreechart, 500, getSizeY(categories.size()));
			response.getOutputStream().close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	private int getSizeY(int length) {
		return (length * 25) + 50;
	}
}
