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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.services.IRecordService;
import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Dispatcher;
import org.budget.logger.ui.views.DetailsReportView;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 23 сент. 2015 г.
 */
public class DetailsReportController extends Controller {

    private Logger logger = Logger.getLogger(getClass());

    private DetailsReportView detailsReportView;
    private MessageSourceAccessor messageSource;
    private IRecordService recordService;

    public DetailsReportController(MessageSourceAccessor messageSource, IRecordService recordService) {
        this.messageSource = messageSource;
        this.recordService = recordService;
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.OpenDetailsReportTab);
        registerEventTypes(AppEvents.ViewReport);
    }

    @Override
    protected void initialize() {
        detailsReportView = new DetailsReportView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            forwardToView(detailsReportView, event);
            return;
        }
        if (AppEvents.OpenDetailsReportTab.equals(event.getType())) {
            handleEvent(new AppEvent(AppEvents.ViewReport));
            return;
        }
        if (AppEvents.ViewReport.equals(event.getType())) {
            Date from = detailsReportView.getFromDate();
            Date to = detailsReportView.getToDate();
            BufferedImage mainChart = generateMainChart(from, to);
            if (null != mainChart) {
                detailsReportView.setMainChart(mainChart);
            }
            Map<Category, BufferedImage> imagesMap = generateChartImages(from, to);
            if (null != imagesMap) {
                detailsReportView.setReportImages(imagesMap);
            }
            return;
        }
    }

    private Map<Category, BufferedImage> generateChartImages(Date dateFrom, Date dateTo) {
        Map<Category, BufferedImage> imagesMap = new LinkedHashMap<Category, BufferedImage>();
        List<Category> categories = recordService.getCategories();
        for (Category cat : categories) {
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
                dataset.addValue(map.get(label), "S1", label);
            }

            JFreeChart jfreechart = ChartFactory.createBarChart(cat.getName(), null, null, dataset,
                    PlotOrientation.HORIZONTAL, false, false, false);
            TextTitle title = new TextTitle(cat.getName());
            title.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
            jfreechart.setTitle(title);
            CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
            categoryplot.setDomainGridlinesVisible(true);
            categoryplot.setBackgroundPaint(Color.white);
            categoryplot.setDomainGridlinePaint(Color.gray);
            categoryplot.setRangeGridlinePaint(Color.gray);
            categoryplot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8F);

            NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
            numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
            barrenderer.setDrawBarOutline(false);
            barrenderer.setSeriesPaint(0, new Color(77, 77, 250));
            barrenderer.setShadowVisible(false);

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ChartUtilities.writeChartAsJPEG(out, jfreechart, 500, getSizeY(map.size()));
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

    private int getSizeY(int length) {
        return (length * 25) + 50;
    }

    private BufferedImage generateMainChart(Date dateFrom, Date dateTo) {
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
        String title = messageSource.getMessage("jsp.report.detailed.all");
        JFreeChart jfreechart = ChartFactory.createBarChart(title, null, null, dataset, PlotOrientation.HORIZONTAL,
                false, false, false);
        TextTitle textTitle = new TextTitle(title);
        textTitle.setFont(new Font("SansSerif", java.awt.Font.PLAIN, 12));
        jfreechart.setTitle(textTitle);
        CategoryPlot categoryplot = (CategoryPlot) jfreechart.getPlot();
        categoryplot.setDomainGridlinesVisible(true);
        categoryplot.setBackgroundPaint(Color.white);
        categoryplot.setDomainGridlinePaint(Color.gray);
        categoryplot.setRangeGridlinePaint(Color.gray);
        categoryplot.getDomainAxis().setMaximumCategoryLabelWidthRatio(0.8F);

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
        numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
        barrenderer.setDrawBarOutline(false);
        barrenderer.setSeriesPaint(0, new Color(77, 77, 250));
        barrenderer.setShadowVisible(false);

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsJPEG(out, jfreechart, 500, getSizeY(categories.size()));
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
}
