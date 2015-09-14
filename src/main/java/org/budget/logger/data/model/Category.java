/**
 * 
 */
package org.budget.logger.data.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
public class Category {

    private Long id;

    private String name;

    private Boolean def = false;

    private Boolean report = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDef() {
        return def;
    }

    public void setDef(Boolean def) {
        this.def = def;
    }

    public Boolean getReport() {
        return report;
    }

    public void setReport(Boolean report) {
        this.report = report;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Category other = (Category) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + ", def=" + def + "]";
    }

    public static final CategoryRowMapper ROW_MAPPER = new CategoryRowMapper();

    public static class CategoryRowMapper implements RowMapper<Category> {

        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
            Category cat = new Category();
            cat.setId(rs.getLong("id"));
            cat.setName(rs.getString("name"));
            cat.setDef(0 == rs.getInt("def") ? false : true);
            cat.setReport(0 == rs.getInt("report") ? false : true);
            return cat;
        }

    }

}
