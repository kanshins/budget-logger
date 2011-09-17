/**
 * 
 */
package org.budget.logger.dao.derby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.bson.types.ObjectId;
import org.budget.logger.dao.IRecordDao;
import org.budget.logger.model.Category;
import org.budget.logger.model.Record;
import org.budget.logger.model.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.document.mongodb.MongoTemplate;
import org.springframework.data.document.mongodb.convert.MongoConverter;
import org.springframework.data.document.mongodb.query.Criteria;
import org.springframework.data.document.mongodb.query.Query;
import org.springframework.data.document.mongodb.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author kanshin
 *
 */
@Repository("recordDao")
public class RecordDao implements IRecordDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public Record create(Record r) {
		mongoTemplate.insert(r);
		return r;
	}
	
	@Override
	public Collection<Record> readAll() {
		return mongoTemplate.findAll(Record.class);
	}

	@Override
	public void delete(String id) {
		mongoTemplate.remove(Query.query(Criteria.where("_id").is(new ObjectId(id))), Record.class);
	}
	
	@Override
	public void multipleAmount(Double d) {
		DB db = mongoTemplate.getDb();
		DBCollection coll = db.getCollection("records");
		DBCursor cur = coll.find();
		while (cur.hasNext()) {
			DBObject o = cur.next();
			Double amount = (Double) o.get("amount");
			amount = amount * d;
			o.put("amount", amount);
			coll.save(o);
		}
	}
	
	/*private Criteria getCriteria(Criteria c, String param) {
		if (null == c) {
			c = Criteria.where(param);
		} else {
			c = c.and(param);
		}
		return c;
	}*/
	
	@Override
	public Collection<Record> findByParams(Type type, Date from, Date to, Category category) {
		
		DBObject q = new BasicDBObject();
		if (null != type) {
			q.put("type", type.name());
		}
		if (null != from && null != to) {
			q.put("date", new BasicDBObject("$gte", from).append("$lt", to));
		} else if (null != from) {
			q.put("date", new BasicDBObject("$gte", from));
		} else if (null != to) {
			q.put("date", new BasicDBObject("$lt", to));
		}
		if (null != category) {
			q.put("category", category.getName());
		}
		DBCollection coll = mongoTemplate.getDb().getCollection("records");
		DBCursor cur = coll.find(q).sort(new BasicDBObject("date", -1));
		MongoConverter converter = mongoTemplate.getConverter();
		Collection<Record> records = new ArrayList<Record>();
		while (cur.hasNext()) {
			DBObject o = cur.next();
			records.add(converter.read(Record.class, o));
		}
		return records;
	}
	
/*	@Override
	public Collection<Record> findByParams(Type type, Date from, Date to, Category category) {
		Criteria c = null;
		if (null != type) {
			c = getCriteria(c, "type").is(type.name());
		}
		if (null != from) {
			c = getCriteria(c, "date").gte(from);
		}
		if (null != to) {
			c = getCriteria(c, "date").lt(to);
		}
		if (null != category) {
			c = getCriteria(c, "category").is(category.getName());
		}
		Query q = null;
		if (null != c) {
			q = Query.query(c);
		} else {
			q = Query.query(Criteria.where("_id").exists(true));
		}
		q.sort().on("date", Order.DESCENDING);
		return mongoTemplate.find(q, Record.class);
	}
*/
	@Override
	public Record update(Record r) {
		mongoTemplate.save(r);
		return r;
	}

	@Override
	public Record read(String id) {
		return mongoTemplate.findById(id, Record.class);
	}
	
	@Override
	public void updateCategory(String oldCategory, String newCategory) {
		mongoTemplate.updateMulti(
				Query.query(Criteria.where("category").is(oldCategory)),
				Update.update("category", newCategory), Record.class);
	}
	
}
