/**
 * 
 */
package org.budget.logger.dao.derby;

import java.util.Collection;

import org.bson.types.ObjectId;
import org.budget.logger.dao.ICategoryDao;
import org.budget.logger.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.query.Criteria;
import org.springframework.data.document.mongodb.query.Order;
import org.springframework.data.document.mongodb.query.Query;
import org.springframework.data.document.mongodb.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author kanshin
 * 
 */
@Repository("categoryDao")
public class CategoryDao implements ICategoryDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void setDefaultForAll(Boolean def) {
		mongoTemplate.updateMulti(
				Query.query(Criteria.where("_id").exists(true)),
				Update.update("def", def), Category.class);
	}

	@Override
	public Category update(Category cat) {
		mongoTemplate.save(cat);
		return cat;
	}

	@Override
	public Category read(String id) {
		return mongoTemplate.findById(id, Category.class);
	}

	@Override
	public Category create(Category cat) {
		mongoTemplate.insert(cat);
		return cat;
	}

	@Override
	public void delete(String id) {
		mongoTemplate.remove(
				Query.query(Criteria.where("_id").is(new ObjectId(id))),
				Category.class);
	}

	@Override
	public Collection<Category> readAll() {
		Query q = Query.query(Criteria.where("_id").exists(true));
		q.sort().on("name", Order.ASCENDING);
		return mongoTemplate.find(q, Category.class);
	}

	@Override
	public Category findByName(String name) {
		return mongoTemplate.findOne(
				Query.query(Criteria.where("name").is(name)), Category.class);
	}

}
