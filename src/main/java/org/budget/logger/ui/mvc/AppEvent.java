/**
 * AppEvent.java
 */
package org.budget.logger.ui.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class AppEvent extends BaseEvent {

    private Object data;
    private Map<String, Object> dataMap;

    /**
     * Creates a new application event.
     * 
     * @param type
     *            the event type
     */
    public AppEvent(EventType type) {
        super(type);
    }

    /**
     * Creates a new application event.
     * 
     * @param type
     *            the event type
     * @param data
     *            the data
     */
    public AppEvent(EventType type, Object data) {
        super(type);
        this.data = data;
    }

    /**
     * Returns the application specified data.
     * 
     * @param <X>
     *            the data type
     * @return the data
     */
    @SuppressWarnings("unchecked")
    public <X> X getData() {
        return (X) data;
    }

    /**
     * Returns the application defined property for the given name, or
     * <code>null</code> if it has not been set.
     * 
     * @param key
     *            the name of the property
     * @return the value or <code>null</code> if it has not been set
     */
    @SuppressWarnings("unchecked")
    public <X> X getData(String key) {
        if (dataMap == null)
            return null;
        return (X) dataMap.get(key);
    }

    /**
     * Sets the application defined data.
     * 
     * @param data
     *            the data
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Sets the application defined property with the given name.
     * 
     * @param key
     *            the name of the property
     * @param data
     *            the new value for the property
     */
    public void setData(String key, Object data) {
        if (dataMap == null)
            dataMap = new HashMap<String, Object>();
        dataMap.put(key, data);
    }

    public String toString() {
        return "Event Type: " + getType();
    }
}
