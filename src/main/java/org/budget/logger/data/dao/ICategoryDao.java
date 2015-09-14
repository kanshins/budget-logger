/**
 * 
 */
package org.budget.logger.data.dao;

import java.util.Collection;

import org.budget.logger.data.model.Category;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
public interface ICategoryDao {

    Category create(Category cat);

    Category update(Category cat);

    Category read(Long id);

    Collection<Category> readAll();

    Category findByName(String name);

    void delete(Long id);

    void setDefaultForAll(Boolean def);

}
