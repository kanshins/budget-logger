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

    public static final EventType OpenRecordsTab = new EventType();

    public static final EventType OpenMainReportTab = new EventType();
    
    public static final EventType OpenCategoryReportTab = new EventType();
    
    public static final EventType OpenDetailsReportTab = new EventType();

    public static final EventType OpenSettingsTab = new EventType();

    public static final EventType DoSearchRecords = new EventType();

    public static final EventType DoAddRecord = new EventType();

    public static final EventType DoDeleteRecord = new EventType();

    public static final EventType DoEditRecord = new EventType();

    public static final EventType ViewReport = new EventType();

    public static final EventType ShowError = new EventType();
}
