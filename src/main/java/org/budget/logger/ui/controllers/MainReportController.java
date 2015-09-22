/**
 * 
 */
package org.budget.logger.ui.controllers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;
import org.budget.logger.data.services.IRecordService;
import org.budget.logger.helpers.DateHelper;
import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Dispatcher;
import org.budget.logger.ui.views.MainReportView;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 22 сент. 2015 г.
 */
public class MainReportController extends Controller {

    private Logger logger = Logger.getLogger(getClass());

    private MainReportView mainReportView;
    private MessageSourceAccessor messageSource;
    private IRecordService recordService;

    public MainReportController(MessageSourceAccessor messageSource, IRecordService recordService) {
        this.messageSource = messageSource;
        this.recordService = recordService;
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.OpenMainReportTab);
        registerEventTypes(AppEvents.ViewReport);
    }

    @Override
    protected void initialize() {
        mainReportView = new MainReportView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            forwardToView(mainReportView, event);
            return;
        }
        if (AppEvents.OpenMainReportTab.equals(event.getType())) {
            handleEvent(new AppEvent(AppEvents.ViewReport));
            return;
        }
        if (AppEvents.ViewReport.equals(event.getType())) {
            Integer period = mainReportView.getPeriod();
            BufferedImage image = generateChartImage(period);
            if (null != image) {
                mainReportView.setReportImage(image);
            }
            return;
        }
    }

    public BufferedImage generateChartImage(Integer period) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = period - 1; i >= 0; i--) {
            Date from = DateHelper.getMonthStart(DateHelper.getPreviousMonth(i));
            String label = DateHelper.getShortMonth(from, messageSource);
            Date to = DateHelper.getNextMonth(from);

            Double incomeSum = computeAmountForMonth(Type.INCOME, from, to);
            dataset.addValue(incomeSum, messageSource.getMessage("jsp.reports.type." + Type.INCOME.name()), label);
            Double outcomeSum = computeAmountForMonth(Type.OUTCOME, from, to);
            dataset.addValue(outcomeSum, messageSource.getMessage("jsp.reports.type." + Type.OUTCOME.name()), label);
            Double storingSum = computeAmountForMonth(Type.STORING, from, to);
            dataset.addValue(storingSum, messageSource.getMessage("jsp.reports.type." + Type.STORING.name()), label);
        }

        JFreeChart jfreechart = ChartFactory.createBarChart(null, null, null, dataset, PlotOrientation.VERTICAL, true,
                false, false);

        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        // categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setRangeCrosshairVisible(true);
        categoryplot.setRangeCrosshairPaint(Color.blue);
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
        barrenderer.setDrawBarOutline(false);
        barrenderer.setSeriesPaint(0, new Color(77, 77, 250));
        barrenderer.setSeriesPaint(1, new Color(250, 77, 77));
        barrenderer.setSeriesPaint(2, new Color(77, 250, 77));
        barrenderer.setShadowVisible(false);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsJPEG(out, jfreechart, getXSize(period), 250);
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            BufferedImage image = ImageIO.read(in);
            out.close();
            in.close();
            return image;
        } catch (IOException e) {
            logger.error(e.getMessage());
            Dispatcher.forwardEvent(AppEvents.ShowError, e.getMessage());
            return null;
        }
    }

    private Double computeAmountForMonth(Type type, Date from, Date to) {
        List<Record> records = recordService.getRecords(type, from, to, null);
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

    private int getXSize(int period) {
        return (period * 70) + 100;
    }
}
