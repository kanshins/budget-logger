/**
 * 
 */
package org.budget.logger.data.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author <sergey.kanshin@gmail.com>
 * @date 14 сент. 2015 г.
 */
public class Record {

    private Long id;

    private String category;

    private String desc;

    private Date date;

    private Type type;

    private Double amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String cat) {
        this.category = cat;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Record other = (Record) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Record [date=" + date + ", desc=" + desc + ", id=" + id + ", category=" + category + ", type=" + type
                + "]";
    }

    public static final RecordRowMapper ROW_MAPPER = new RecordRowMapper();

    public static class RecordRowMapper implements RowMapper<Record> {

        public Record mapRow(ResultSet rs, int rowNum) throws SQLException {
            Record r = new Record();
            r.setId(rs.getLong("id"));
            r.setCategory(rs.getString("category"));
            r.setType(Type.valueOf(rs.getInt("type")));
            r.setDesc(rs.getString("description"));
            r.setDate(rs.getDate("create_date"));
            r.setAmount(rs.getDouble("amount"));
            return r;
        }

    }
}
