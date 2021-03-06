/**
 * 
 */
package org.budget.logger.data.services;

import java.util.Date;
import java.util.List;

import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;

/**
 * @author kanshin
 *
 */
public interface IRecordService {

    List<Record> getAllRecords();

    List<Record> getRecords(Type type, Date from, Date to, Category cat);

    Record createRecord(String category, String desc, Type type, Date date, Double amount);

    Record createRecord(Record record);

    Record updateRecord(Record record);

    Category getCateroryByName(String name);

    Category getCaterory(Long id);

    Category updateCaterory(Category category);

    List<Category> getCategories();

    void deleteRecord(Long id);

    Record getRecord(Long id);

    void deleteCaterory(Long id);

    Category createCategory(String name, Boolean isDefault, Boolean report);

}
