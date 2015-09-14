/**
 * AppEvents.java
 */
package org.budget.logger.ui;

import org.budget.logger.ui.mvc.EventType;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public abstract class AppEvents {

    public static final EventType Init = new EventType();

    public static final EventType StartApp = new EventType();

    public static final EventType Exit = new EventType();

    public static final EventType AppendLog = new EventType();

    public static final EventType DoGetRequest = new EventType();

    public static final EventType DoPostRequest = new EventType();

    public static final EventType ShowContent = new EventType();

    public static final EventType ShowSrc = new EventType();

    public static final EventType UpdateLoadProgress = new EventType();
}
