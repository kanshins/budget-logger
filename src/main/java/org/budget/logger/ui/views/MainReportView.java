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

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Registry;
import org.budget.logger.ui.mvc.View;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 22 сент. 2015 г.
 */
public class MainReportView extends View {

    private JComboBox<Integer> periodCombo;
    private JPanel chartPanel;
    private JScrollPane chartScroll;
    
    public MainReportView(Controller controller) {
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
        JPanel main = Registry.get("reportMainPanel");
        main.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 1));
        topPanel.add(createPeriodPanel());
        main.add(topPanel, BorderLayout.NORTH);

        chartScroll = new JScrollPane(createChart());
        main.add(chartScroll, BorderLayout.CENTER);
    }

    private JPanel createPeriodPanel() {
        JPanel periodPanel = new JPanel();
        periodPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        periodPanel.add(new JLabel("Период:"));
        periodCombo = new JComboBox<Integer>();
        Integer[] items = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        periodCombo.setModel(new DefaultComboBoxModel<Integer>(items));
        periodCombo.setSelectedIndex(0);
        periodPanel.add(periodCombo);

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

    public Integer getPeriod() {
        return (Integer) periodCombo.getSelectedItem();
    }

    private JPanel createChart() {
        chartPanel = new JPanel();
        chartPanel.setLayout(new FlowLayout());
        return chartPanel;
    }
    
    public void setReportImage(BufferedImage image) {
        JLabel picLabel = new JLabel(new ImageIcon(image));
        chartPanel.removeAll();
        chartPanel.add(picLabel);
        chartScroll.doLayout();
    }
}
