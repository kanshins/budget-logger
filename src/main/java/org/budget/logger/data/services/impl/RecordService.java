/**
 * 
 */
package org.budget.logger.data.services.impl;

import java.util.Date;
import java.util.List;

import org.budget.logger.data.dao.ICategoryDao;
import org.budget.logger.data.dao.IRecordDao;
import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;
import org.budget.logger.data.services.IRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
@Service("recordService")
public class RecordService implements IRecordService {

    @Autowired
    private IRecordDao recordDao;

    @Autowired
    private ICategoryDao categoryDao;

    @Override
    public Record createRecord(String name, String desc, Type type, Date date, Double amount) {
        Record r = new Record();
        r.setCategory(name);
        r.setDate(date);
        r.setType(type);
        r.setDesc(desc);
        r.setAmount(amount);
        return recordDao.create(r);
    }

    @Override
    public Record updateRecord(Record record) {
        return recordDao.update(record);
    }

    @Override
    public Record getRecord(Long id) {
        return recordDao.read(id);
    }

    @Override
    public List<Record> getAllRecords() {
        return recordDao.readAll();
    }

    @Override
    public void deleteRecord(Long id) {
        recordDao.delete(id);
    }

    @Override
    public Category createCategory(String name, Boolean isDefault, Boolean report) {
        if (isDefault) {
            categoryDao.setDefaultForAll(false);
        }
        Category n = new Category();
        n.setName(name);
        n.setDef(isDefault);
        n.setReport(report);
        return categoryDao.create(n);
    }

    @Override
    public Category getCaterory(Long id) {
        return categoryDao.read(id);
    }

    @Override
    public void deleteCaterory(Long id) {
        categoryDao.delete(id);
    }

    @Override
    public Category updateCaterory(Category category) {
        Category old = categoryDao.read(category.getId());
        if (category.getDef()) {
            categoryDao.setDefaultForAll(false);
        }
        recordDao.updateCategory(old.getName(), category.getName());
        return categoryDao.update(category);
    }

    @Override
    public List<Category> getCategories() {
        return categoryDao.readAll();
    }

    @Override
    public List<Record> getRecords(Type type, Date from, Date to, Category name) {
        return recordDao.findByParams(type, from, to, name);
    }

    @Override
    public Category getCateroryByName(String name) {
        return categoryDao.findByName(name);
    }

}
