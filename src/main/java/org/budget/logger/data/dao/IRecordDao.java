/**
 * 
 */
package org.budget.logger.data.dao;

import java.util.Collection;
import java.util.Date;

import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
public interface IRecordDao {

    Record create(Record record);

    Record update(Record record);

    Record read(Long id);

    Collection<Record> readAll();

    void delete(Long id);

    Collection<Record> findByParams(Type type, Date from, Date to, Category name);

    void updateCategory(String oldCategory, String newCategory);

}
