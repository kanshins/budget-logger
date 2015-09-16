/**
 * 
 */
package org.budget.logger.data.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.budget.logger.data.dao.ICategoryDao;
import org.budget.logger.data.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
@Repository("categoryDao")
public class CategoryDao extends NamedParameterJdbcDaoSupport implements ICategoryDao {

    @Autowired
    public void initDataSource(DataSource dataSource) {
        setDataSource(dataSource);
    }

    @Override
    public void setDefaultForAll(Boolean def) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "update categories set def=?";
        // logger.debug(sql);
        t.update(sql, def);
    }

    @Override
    public Category update(Category cat) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "update categories set name=?, def=?, report=? where id=?";
        // logger.debug(sql);
        t.update(sql, cat.getName(), cat.getDef(), cat.getReport(), cat.getId());
        return cat;

    }

    @Override
    public Category read(Long id) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "select id, name, def, report from categories where id=?";
        // logger.debug(sql);
        List<Category> list = t.query(sql, new Object[] { id }, Category.ROW_MAPPER);
        return list.size() > 0 ? list.iterator().next() : null;

    }

    @Override
    public Category create(Category cat) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "insert into categories (name,def,report) values (?,?,?)";
        t.update(sql, cat.getName(), cat.getDef() ? 1 : 0, cat.getReport() ? 1 : 0);
        // logger.debug(sql);
        cat.setId(t.queryForObject("select max(id) from categories", Long.class));
        return cat;
    }

    @Override
    public void delete(Long id) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "delete from categories where id=?";
        // logger.debug(sql);
        t.update(sql, id);
    }

    @Override
    public List<Category> readAll() {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "select id, name, def, report from categories order by name asc";
        // logger.debug(sql);
        return t.query(sql, Category.ROW_MAPPER);
    }

    @Override
    public Category findByName(String name) {
        JdbcTemplate t = getJdbcTemplate();
        String sql = "select id, name, def, report from categories where name=?";
        // logger.debug(sql);
        List<Category> list = t.query(sql, new Object[] { name }, Category.ROW_MAPPER);
        return list.size() > 0 ? list.get(0) : null;

    }

}
