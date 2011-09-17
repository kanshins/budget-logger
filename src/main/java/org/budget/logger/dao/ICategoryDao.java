/**
 * 
 */
package org.budget.logger.dao;

import java.util.Collection;

import org.budget.logger.model.Category;

/**
 * @author kanshin
 *
 */
public interface ICategoryDao {

	Category create(Category cat);

	Category update(Category cat);

	Category read(String id);

	Collection<Category> readAll();
	
	Category findByName(String name);
	
	void delete(String id);

	void setDefaultForAll(Boolean def);
	
}
