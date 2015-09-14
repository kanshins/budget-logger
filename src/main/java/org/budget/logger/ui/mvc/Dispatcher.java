/**
 * 
 */
package org.budget.logger.ui.mvc;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class Dispatcher {

    private static Dispatcher instance;

    private List<Controller> controllers;

    private Dispatcher() {
        controllers = new ArrayList<Controller>();
    }

    /**
     * Returns the singleton instance.
     * 
     * @return the dispatcher
     */
    public static Dispatcher get() {
        if (instance == null) {
            instance = new Dispatcher();
        }
        return instance;
    }

    /**
     * Adds a controller.
     * 
     * @param controller
     *            the controller to be added
     */
    public void addController(Controller controller) {
        if (!controllers.contains(controller)) {
            controllers.add(controller);
        }
    }

    public void dispatch(AppEvent event) {
        MvcEvent e = new MvcEvent(this, event);
        e.setAppEvent(event);
        List<Controller> copy = new ArrayList<Controller>(controllers);
        for (Controller controller : copy) {
            if (controller.canHandle(event)) {
                if (!controller.initialized) {
                    controller.initialized = true;
                    controller.initialize();
                }
                controller.handleEvent(event);
            }
        }
    }

    /**
     * The dispatcher will query its controllers and pass the application event
     * to controllers that can handle the particular event type.
     * 
     * @param type
     *            the event type
     */
    public void dispatch(EventType type) {
        dispatch(new AppEvent(type));
    }

    /**
     * The dispatcher will query its controllers and pass the application event
     * to controllers that can handle the particular event type.
     * 
     * @param type
     *            the event type
     * @param data
     *            the app event data
     */
    public void dispatch(EventType type, Object data) {
        dispatch(new AppEvent(type, data));
    }

    /**
     * Returns all controllers.
     * 
     * @return the list of controllers
     */
    public List<Controller> getControllers() {
        return controllers;
    }

    /**
     * Removes a controller.
     * 
     * @param controller
     *            the controller to be removed
     */
    public void removeController(Controller controller) {
        controllers.remove(controller);
    }

    /**
     * Forwards an application event to the dispatcher.
     * 
     * @param event
     *            the application event
     */
    public static void forwardEvent(AppEvent event) {
        get().dispatch(event);
    }

    /**
     * Creates and forwards an application event to the dispatcher.
     * 
     * @param eventType
     *            the application event type
     */
    public static void forwardEvent(EventType eventType) {
        get().dispatch(eventType);
    }

    /**
     * Creates and forwards an application event to the dispatcher.
     * 
     * @param eventType
     *            the application event type
     * @param data
     *            the event data
     */
    public static void forwardEvent(EventType eventType, Object data) {
        get().dispatch(new AppEvent(eventType, data));
    }
}
