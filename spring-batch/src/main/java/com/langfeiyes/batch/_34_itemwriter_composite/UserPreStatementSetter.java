package com.langfeiyes.batch._34_itemwriter_composite;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

//用于设置sql中占位符的参数
public class UserPreStatementSetter implements ItemPreparedStatementSetter<User> {
    @Override
    public void setValues(User item, PreparedStatement ps) throws SQLException {

        ps.setLong(1, item.getId());
        ps.setString(2, item.getName());
        ps.setInt(3, item.getAge());
    }
}
