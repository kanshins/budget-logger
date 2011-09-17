/**
 * 
 */
package org.budget.logger.controllers;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.budget.logger.helpers.DateHelper;
import org.budget.logger.model.Category;
import org.budget.logger.model.Record;
import org.budget.logger.model.Type;
import org.budget.logger.services.IRecordService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
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
@RequestMapping("/reports/category")
public class ReportCategoryController extends ApplicationObjectSupport {

	private static final int DEFAULT_PERIOD = 6;
	
	@Autowired
	private IRecordService recordService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String show(ModelMap modelMap) {
		return period(DEFAULT_PERIOD, modelMap);
	}
	
	@RequestMapping(value = "/period", method = RequestMethod.GET)
	public String period(@RequestParam Integer period, ModelMap modelMap) {
		Collection<Category> categories = recordService.getCategories();
		modelMap.addAttribute("categories", categories);
		modelMap.addAttribute("period", period);
		return "reports/category";
	}
	
	private Double computeAmountForMonth(Category cat, Date from, Date to) {
		Collection<Record> records = recordService.getRecords(null, from, to, cat);
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
	
	@RequestMapping(value = "/chartcat", method = RequestMethod.GET)
	public void chart(@RequestParam String catid, @RequestParam Integer period,
			HttpServletResponse response) {
		Category cat = recordService.getCaterory(catid);
		if (null == cat) {
			logger.warn("Cannot find category with id=" + catid);
			return;
		}
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = period - 1; i >= 0; i--) {
			Date from = DateHelper.getMonthStart(DateHelper.getPreviousMonth(i));
			String label = DateHelper.getShortMonth(from, getMessageSourceAccessor());
			Date to = DateHelper.getNextMonth(from);
			Double sum = computeAmountForMonth(cat, from, to);
	        dataset.addValue(sum, "Series 1", label);
		}

		JFreeChart jfreechart = ChartFactory.createLineChart(cat.getName(),
				null, null, dataset, PlotOrientation.VERTICAL, false, false,
				false);
        TextTitle title = new TextTitle(cat.getName());
        title.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
        jfreechart.setTitle(title);
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);
        NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)categoryplot.getRenderer();
        lineandshaperenderer.setSeriesShapesVisible(0, true);
        //lineandshaperenderer.setSeriesShape(0, ShapeUtilities.createDiamond(4F));
        lineandshaperenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3D, -3D, 6D, 6D));
        lineandshaperenderer.setDrawOutlines(true);
        lineandshaperenderer.setUseFillPaint(true);
        lineandshaperenderer.setBaseFillPaint(new Color(77, 77, 250));
        lineandshaperenderer.setSeriesPaint(0, new Color(77, 77, 250));

        try {
			ChartUtilities.writeChartAsJPEG(response.getOutputStream(),
					jfreechart, getSizeX(period), 200);
			response.getOutputStream().close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/chartoutcome", method = RequestMethod.GET)
	public void chartoutcome(@RequestParam Integer period, HttpServletResponse response) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = period - 1; i >= 0; i--) {
			Date from = DateHelper.getMonthStart(DateHelper.getPreviousMonth(i));
			String label = DateHelper.getShortMonth(from, getMessageSourceAccessor());
			Date to = DateHelper.getNextMonth(from);
			Collection<Record> records = recordService.getRecords(Type.OUTCOME, from, to, null);
			Double sum = 0.0;
			for (Record r : records) {
				if (null == r || null == r.getAmount()) {
					logger.warn("Bad record: " + r);
					continue;
				}
				sum += r.getAmount();
			}
	        dataset.addValue(sum, "Series 1", label);
		}

		JFreeChart jfreechart = ChartFactory.createLineChart("", null, null,
				dataset, PlotOrientation.VERTICAL, false, false, false);
		TextTitle title = new TextTitle(getMessageSourceAccessor().getMessage(
				"jsp.reports.category.outcome"));
        title.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
        jfreechart.setTitle(title);
        CategoryPlot categoryplot = (CategoryPlot)jfreechart.getPlot();
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);
        NumberAxis numberaxis = (NumberAxis)categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer)categoryplot.getRenderer();
        lineandshaperenderer.setSeriesShapesVisible(0, true);
        //lineandshaperenderer.setSeriesShape(0, ShapeUtilities.createDiamond(4F));
        lineandshaperenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3D, -3D, 6D, 6D));
        lineandshaperenderer.setDrawOutlines(true);
        lineandshaperenderer.setUseFillPaint(true);
        lineandshaperenderer.setBaseFillPaint(new Color(77, 77, 250));
        lineandshaperenderer.setSeriesPaint(0, new Color(77, 77, 250));

        try {
			ChartUtilities.writeChartAsJPEG(response.getOutputStream(),
					jfreechart, getSizeX(period), 200);
			response.getOutputStream().close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private int getSizeX(int period) {
		return (period * 50) + 40;
	}
}
