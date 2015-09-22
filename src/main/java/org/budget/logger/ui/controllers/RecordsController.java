/**
 * 
 */
package org.budget.logger.ui.controllers;

import java.util.Date;
import java.util.List;

import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.services.IRecordService;
import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Dispatcher;
import org.budget.logger.ui.views.RecordsTabView;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class RecordsController extends Controller {
    
    private IRecordService recordService;
    
    private RecordsTabView recordsTabView;

    public RecordsController(IRecordService recordService) {
        this.recordService = recordService;
        registerEventTypes(AppEvents.Init);
        registerEventTypes(AppEvents.OpenRecordsTab);
        registerEventTypes(AppEvents.DoSearchRecords);
        registerEventTypes(AppEvents.DoAddRecord);
        registerEventTypes(AppEvents.DoDeleteRecord);
        registerEventTypes(AppEvents.DoEditRecord);
    }
    
    @Override
    protected void initialize() {
        recordsTabView = new RecordsTabView(this);
    }
    
    @Override
    public void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            forwardToView(recordsTabView, event);
            return;
        }
        if (AppEvents.OpenRecordsTab.equals(event.getType())) {
            List<Category> categories = recordService.getCategories();
            recordsTabView.setCategories(categories);
            Dispatcher.forwardEvent(AppEvents.DoSearchRecords);
            return;
        }
        if (AppEvents.DoSearchRecords.equals(event.getType())) {
            Category cat = recordsTabView.getSearchCategory();
            Date from = recordsTabView.getSearchDateFrom();
            Date to = recordsTabView.getSearchDateTo();
            List<Record> list = recordService.getRecords(null, from, to, cat);
            recordsTabView.setData(list);
            return;
        }
        if (AppEvents.DoAddRecord.equals(event.getType())) {
            Record r = event.getData();
            recordService.createRecord(r);
            Dispatcher.forwardEvent(AppEvents.DoSearchRecords);
            return;
        }
        if (AppEvents.DoDeleteRecord.equals(event.getType())) {
            Record r = event.getData();
            recordService.deleteRecord(r.getId());
            recordsTabView.deleteFromStore(r);
            return;
        }
        if (AppEvents.DoEditRecord.equals(event.getType())) {

            return;
        }
    }

}
