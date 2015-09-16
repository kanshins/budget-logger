/**
 * 
 */
package org.budget.logger.ui.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Dispatcher;
import org.budget.logger.ui.mvc.Registry;
import org.budget.logger.ui.mvc.View;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class MainView extends View {

    private JFrame mainFrame;

    public MainView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            onInit();
            return;
        }
        if (AppEvents.StartApp.equals(event.getType())) {
            mainFrame.setVisible(true);
            return;
        }
    }

    private void onInit() {
        mainFrame = new JFrame();
        mainFrame.setSize(900, 600);
        mainFrame.setResizable(true);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle("Budget Logger 3.0");
        mainFrame.setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainFrame.setJMenuBar(createMenu());
        final JTabbedPane tabs = new JTabbedPane();
        mainFrame.getContentPane().add(tabs);
        JPanel panel = new JPanel();
        tabs.add("Записи", panel);
        Registry.register("recordsPanel", panel);
        panel = new JPanel();
        tabs.add("Отчет основной", panel);
        Registry.register("reportMainPanel", panel);
        panel = new JPanel();
        tabs.add("Отчет детальный", panel);
        Registry.register("reportDetailPanel", panel);
        panel = new JPanel();
        tabs.add("Настройки", panel);
        Registry.register("settingsPanel", panel);
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (0 ==tabs.getSelectedIndex()) {
                    Dispatcher.forwardEvent(AppEvents.OpenRecordsTab);
                }
            }
        });
    }

    private JMenuBar createMenu() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        jMenuBar.add(fileMenu);
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add(exit);
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Dispatcher.forwardEvent(AppEvents.Exit);
            }
        });
        return jMenuBar;
    }

}
