/**
 * 
 */
package org.budget.logger.ui.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.budget.logger.data.model.Category;
import org.budget.logger.helpers.DateHelper;
import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Registry;
import org.budget.logger.ui.mvc.View;
import org.jdesktop.swingx.JXDatePicker;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 23 сент. 2015 г.
 */
public class DetailsReportView extends View {

    private JXDatePicker datePickerFrom;
    private JXDatePicker datePickerTo;
    private JPanel chartPanel;
    private JScrollPane chartScroll;
    private JPanel wrapper;

    public DetailsReportView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            onInit();
            return;
        }
    }

    private void onInit() {
        JPanel main = Registry.get("reportDetailPanel");
        main.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 1));
        topPanel.add(createPeriodPanel());
        main.add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createPeriodPanel() {
        JPanel periodPanel = new JPanel();
        periodPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        periodPanel.add(new JLabel("С:"));
        datePickerFrom = new JXDatePicker(DateHelper.getPreviousMonth());
        periodPanel.add(datePickerFrom);
        periodPanel.add(new JLabel("По:"));
        datePickerTo = new JXDatePicker(new Date());
        periodPanel.add(datePickerTo);

        JButton actionBtn = new JButton("Показать");
        periodPanel.add(actionBtn);
        actionBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEvent(AppEvents.ViewReport);
            }
        });
        return periodPanel;
    }

    public Date getFromDate() {
        return datePickerFrom.getDate();
    }

    public Date getToDate() {
        return datePickerTo.getDate();
    }

    private JPanel createChart() {
        chartPanel = new JPanel();
        chartPanel.setLayout(new BoxLayout(chartPanel, BoxLayout.Y_AXIS));
        return chartPanel;
    }

    public void setMainChart(BufferedImage image) {
        JPanel main = Registry.get("reportDetailPanel");
        if (null != chartScroll) {
            main.remove(chartScroll);
        }
        wrapper = new JPanel();
        wrapper.setLayout(new FlowLayout());
        wrapper.add(createChart());
        chartScroll = new JScrollPane(wrapper);
        main.add(chartScroll, BorderLayout.CENTER);
        JLabel picLabel = new JLabel(new ImageIcon(image));
        chartPanel.add(picLabel);
    }

    public void setReportImages(Map<Category, BufferedImage> imagesMap) {
        for (BufferedImage image : imagesMap.values()) {
            JLabel picLabel = new JLabel(new ImageIcon(image));
            chartPanel.add(picLabel);
        }
        JPanel main = Registry.get("reportDetailPanel");
        main.doLayout();
    }
}
