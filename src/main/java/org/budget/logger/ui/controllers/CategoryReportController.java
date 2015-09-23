/**
 * 
 */
package org.budget.logger.ui.controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;
import org.budget.logger.data.services.IRecordService;
import org.budget.logger.helpers.DateHelper;
import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Dispatcher;
import org.budget.logger.ui.views.CategoryReportView;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 23 сент. 2015 г.
 */
public class CategoryReportController extends Controller {

    private Logger logger = Logger.getLogger(getClass());

    private CategoryReportView categoryReportView;
    private MessageSourceAccessor messageSource;
    private IRecordService recordService;

    public CategoryReportController(MessageSourceAccessor messageSource, IRecordService recordService) {
        this.messageSource = messageSource;
        this.recordService = recordService;
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.OpenCategoryReportTab);
        registerEventTypes(AppEvents.ViewReport);
    }

    @Override
    protected void initialize() {
        categoryReportView = new CategoryReportView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            forwardToView(categoryReportView, event);
            return;
        }
        if (AppEvents.OpenCategoryReportTab.equals(event.getType())) {
            handleEvent(new AppEvent(AppEvents.ViewReport));
            return;
        }
        if (AppEvents.ViewReport.equals(event.getType())) {
            Integer period = categoryReportView.getPeriod();
            BufferedImage outcomeImage = generateChartOutcomeImage(period);
            if (null != outcomeImage) {
                categoryReportView.setReportOutcomeImage(outcomeImage);
            }
            Map<Category, BufferedImage> imagesMap = generateChartImages(period);
            if (null != imagesMap) {
                categoryReportView.setReportImages(imagesMap);
            }
            return;
        }
    }

    private BufferedImage generateChartOutcomeImage(Integer period) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = period - 1; i >= 0; i--) {
            Date from = DateHelper.getMonthStart(DateHelper.getPreviousMonth(i));
            String label = DateHelper.getShortMonth(from, messageSource);
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

        JFreeChart jfreechart = ChartFactory.createLineChart("", null, null, dataset, PlotOrientation.VERTICAL, false,
                false, false);
        TextTitle title = new TextTitle(messageSource.getMessage("jsp.reports.category.outcome"));
        title.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
        jfreechart.setTitle(title);
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);
        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
        lineandshaperenderer.setSeriesShapesVisible(0, true);
        lineandshaperenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3D, -3D, 6D, 6D));
        lineandshaperenderer.setDrawOutlines(true);
        lineandshaperenderer.setUseFillPaint(true);
        lineandshaperenderer.setBaseFillPaint(new Color(77, 77, 250));
        lineandshaperenderer.setSeriesPaint(0, new Color(77, 77, 250));

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsJPEG(out, jfreechart, getSizeX(period), 250);
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

    private Map<Category, BufferedImage> generateChartImages(Integer period) {
        Map<Category, BufferedImage> imagesMap = new LinkedHashMap<Category, BufferedImage>();

        List<Category> categories = recordService.getCategories();
        for (Category cat : categories) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for (int i = period - 1; i >= 0; i--) {
                Date from = DateHelper.getMonthStart(DateHelper.getPreviousMonth(i));
                String label = DateHelper.getShortMonth(from, messageSource);
                Date to = DateHelper.getNextMonth(from);
                Double sum = computeAmountForMonth(cat, from, to);
                dataset.addValue(sum, "Series 1", label);
            }

            JFreeChart jfreechart = ChartFactory.createLineChart(cat.getName(), null, null, dataset,
                    PlotOrientation.VERTICAL, false, false, false);
            TextTitle title = new TextTitle(cat.getName());
            title.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
            jfreechart.setTitle(title);
            CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
            categoryplot.setBackgroundPaint(Color.white);
            categoryplot.setDomainGridlinesVisible(true);
            categoryplot.setDomainGridlinePaint(Color.gray);
            categoryplot.setRangeGridlinePaint(Color.gray);
            NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
            numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
            lineandshaperenderer.setSeriesShapesVisible(0, true);
            lineandshaperenderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3D, -3D, 6D, 6D));
            lineandshaperenderer.setDrawOutlines(true);
            lineandshaperenderer.setUseFillPaint(true);
            lineandshaperenderer.setBaseFillPaint(new Color(77, 77, 250));
            lineandshaperenderer.setSeriesPaint(0, new Color(77, 77, 250));

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ChartUtilities.writeChartAsJPEG(out, jfreechart, getSizeX(period), 200);
                ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
                BufferedImage image = ImageIO.read(in);
                out.close();
                in.close();
                imagesMap.put(cat, image);
            } catch (IOException e) {
                logger.error(e.getMessage());
                Dispatcher.forwardEvent(AppEvents.ShowError, e.getMessage());
                return null;
            }
        }
        return imagesMap;
    }

    private int getSizeX(int period) {
        return (period * 50) + 40;
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
}
