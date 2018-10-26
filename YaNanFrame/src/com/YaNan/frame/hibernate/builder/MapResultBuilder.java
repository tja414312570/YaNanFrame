package com.YaNan.frame.hibernate.builder;

import java.sql.ResultSet;

import com.YaNan.frame.hibernate.database.entity.PreparedSql;
import com.YaNan.frame.plugin.annotations.Register;

@Register
public class MapResultBuilder implements ResultBuilder{
	@Override
	public <T> T build(PreparedSql sql, ResultSet set) {
		
		return null;
	}

}
