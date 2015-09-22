/**
 * 
 */
package org.budget.logger.ui.controllers;

import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.views.MainView;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class MainController extends Controller {

    private ApplicationContext context;

    private MainView mainView;

    public MainController(ApplicationContext context) {
        this.context = context;
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.StartApp);
        registerEventTypes(AppEvents.Exit);
        registerEventTypes(AppEvents.ShowError);
    }

    @Override
    protected void initialize() {
        mainView = new MainView(this);
    }

    @Override
    public void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            forwardToView(mainView, event);
            return;
        }
        if (AppEvents.StartApp.equals(event.getType())) {
            forwardToView(mainView, event);
            return;
        }
        if (AppEvents.Exit.equals(event.getType())) {
            ((ConfigurableApplicationContext) context).close();
            System.exit(0);
            return;
        }
        if (AppEvents.ShowError.equals(event.getType())) {
            forwardToView(mainView, event);
            return;
        }
    }

}
