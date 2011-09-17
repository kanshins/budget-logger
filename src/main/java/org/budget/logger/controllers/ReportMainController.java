/**
 * 
 */
package org.budget.logger.controllers;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.budget.logger.helpers.DateHelper;
import org.budget.logger.model.Record;
import org.budget.logger.model.Type;
import org.budget.logger.services.IRecordService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
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
@RequestMapping("/reports/main")
public class ReportMainController extends ApplicationObjectSupport {
	
	private static final int DEFAULT_PERIOD = 6;
	
	@Autowired
	private IRecordService recordService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String main(ModelMap modelMap) {
		return period(DEFAULT_PERIOD, modelMap);
	}
	
	@RequestMapping(value = "/period", method = RequestMethod.GET)
	public String period(@RequestParam Integer period, ModelMap modelMap) {
		modelMap.addAttribute("period", period);
		return "reports/main";
	}
	
	private Double computeAmountForMonth(Type type, Date from, Date to) {
		Collection<Record> records = recordService.getRecords(type, from, to, null);
		Double sum = 0.0;
		for (Record r : records) {
			if (null == r || null == r.getAmount()) {
				logger.warn("Bad record: " + r);
				continue;
			}
			sum += r.getAmount();
		}
		return sum;
	}
	
	@RequestMapping(value = "/chart", method = RequestMethod.GET)
	public void chart(@RequestParam Integer period, HttpServletResponse response) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = period - 1; i >= 0; i--) {
			Date from = DateHelper.getMonthStart(DateHelper.getPreviousMonth(i));
			String label = DateHelper.getShortMonth(from, getMessageSourceAccessor());
			Date to = DateHelper.getNextMonth(from);
			
			Double incomeSum = computeAmountForMonth(Type.INCOME, from, to);
	        dataset.addValue(incomeSum, getMessageSourceAccessor().getMessage("jsp.reports.type." + Type.INCOME.name()), label);
			Double outcomeSum = computeAmountForMonth(Type.OUTCOME, from, to);
	        dataset.addValue(outcomeSum, getMessageSourceAccessor().getMessage("jsp.reports.type." + Type.OUTCOME.name()), label);
			Double storingSum = computeAmountForMonth(Type.STORING, from, to);
	        dataset.addValue(storingSum, getMessageSourceAccessor().getMessage("jsp.reports.type." + Type.STORING.name()), label);
		}

		JFreeChart jfreechart = ChartFactory.createBarChart(null, null, null,
				dataset, PlotOrientation.VERTICAL, true, false, false);

        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        //categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setRangeCrosshairVisible(true);
        categoryplot.setRangeCrosshairPaint(Color.blue);
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);
        
        NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer barrenderer = (BarRenderer)categoryplot.getRenderer();
        barrenderer.setDrawBarOutline(false);
        barrenderer.setSeriesPaint(0, new Color(77, 77, 250));
        barrenderer.setSeriesPaint(1, new Color(250, 77, 77));
        barrenderer.setSeriesPaint(2, new Color(77, 250, 77));
        barrenderer.setShadowVisible(false);

        try {
			ChartUtilities.writeChartAsJPEG(response.getOutputStream(),
					jfreechart, getXSize(period), 250);
			response.getOutputStream().close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	private int getXSize(int period) {
		return (period * 70) + 100;
	}
}
