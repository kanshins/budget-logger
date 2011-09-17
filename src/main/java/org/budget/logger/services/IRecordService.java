/**
 * 
 */
package org.budget.logger.services;

import java.util.Collection;
import java.util.Date;

import org.budget.logger.model.Category;
import org.budget.logger.model.Record;
import org.budget.logger.model.Type;

/**
 * @author kanshin
 *
 */
public interface IRecordService {

	Collection<Record> getAllRecords();
	
	Collection<Record> getRecords(Type type, Date from, Date to, Category cat);

	Record createRecord(String category, String desc, Type type, Date date, Double amount);

	Record updateRecord(Record record);

	Category getCateroryByName(String name);

	Category getCaterory(String id);

	Category updateCaterory(Category category);

	Collection<Category> getCategories();

	void deleteRecord(String id);

	Record getRecord(String id);

	void deleteCaterory(String id);

	Category createCategory(String name, Boolean isDefault, Boolean report);

	void multipleAmount(Double d);

}
