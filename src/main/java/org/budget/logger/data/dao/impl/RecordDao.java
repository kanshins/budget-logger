/**
 * 
 */
package org.budget.logger.data.dao.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.budget.logger.data.dao.IRecordDao;
import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
@Repository("recordDao")
public class RecordDao extends NamedParameterJdbcDaoSupport implements IRecordDao {

    @Autowired
    public void initDataSource(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public Record create(Record r) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "insert into records (category, type, description, create_date, amount) values (?,?,?,?,?)";
        // logger.debug(sql);
        t.update(sql, r.getCategory(), r.getType().getId(), r.getDesc(), r.getDate(), r.getAmount());
        r.setId(t.queryForObject("select max(id) from records", Long.class));
        return r;

    }

    @Override
    public Collection<Record> readAll() {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "select id, category, type, description, create_date, amount from records order by create_date desc";
        // logger.debug(sql);
        return t.query(sql, Record.ROW_MAPPER);
    }

    @Override
    public void delete(Long id) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "delete from records where id=?";
        // logger.debug(sql);
        t.update(sql, id);
    }

    @Override
    public Collection<Record> findByParams(Type type, Date from, Date to, Category category) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "select id, category, type, description, create_date, amount from records";
        List<Object> params = new ArrayList<Object>();
        String sqlWhere = "";
        if (null != type) {
            sqlWhere += prefix(sqlWhere) + "type=?";
            params.add(type.getId());
        }
        if (null != from) {
            sqlWhere += prefix(sqlWhere) + "create_date>=?";
            params.add(from);
        }
        if (null != to) {
            sqlWhere += prefix(sqlWhere) + "create_date<?";
            params.add(to);
        }
        if (null != category) {
            sqlWhere += prefix(sqlWhere) + "category=?";
            params.add(category.getName());
        }
        sql += sqlWhere + " order by create_date desc";
        // logger.debug(sql);
        return t.query(sql, params.toArray(), Record.ROW_MAPPER);

    }

    private String prefix(String sqlWhere) {
        return sqlWhere.length() > 0 ? " and " : " where ";
    }

    @Override
    public Record update(Record r) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "update records set category=?, type=?, description=?, create_date=?, amount=? where id=?";
        // logger.debug(sql);
        t.update(sql, r.getCategory(), r.getType().getId(), r.getDesc(), r.getDate(), r.getAmount(), r.getId());
        return r;
    }

    @Override
    public Record read(Long id) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "select id, category, type, description, create_date, amount from records where id=? order by create_date desc";
        // logger.debug(sql);
        Collection<Record> list = t.query(sql, new Object[] { id }, Record.ROW_MAPPER);
        return list.size() > 0 ? list.iterator().next() : null;

    }

    @Override
    public void updateCategory(String oldCategory, String newCategory) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "update records set category=? where category=?";
        t.update(sql, new Object[] { newCategory, oldCategory });
    }

}
