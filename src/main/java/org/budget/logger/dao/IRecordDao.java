/**
 * 
 */
package org.budget.logger.dao;

import java.util.Collection;
import java.util.Date;

import org.budget.logger.model.Category;
import org.budget.logger.model.Record;
import org.budget.logger.model.Type;

/**
 * @author kanshin
 *
 */
public interface IRecordDao {

	Record create(Record record);

	Record update(Record record);
	
	Record read(String id);

	Collection<Record> readAll();
	
	void delete(String id);

	Collection<Record> findByParams(Type type, Date from, Date to, Category name);

	void updateCategory(String oldCategory, String newCategory);

	void multipleAmount(Double d);
	
}
