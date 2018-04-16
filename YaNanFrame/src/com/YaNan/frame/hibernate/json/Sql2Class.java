package com.YaNan.frame.hibernate.json;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugs.PlugsFactory;
import com.mysql.jdbc.PreparedStatement;

public class Sql2Class {
	private final Log log = PlugsFactory.getPlugsInstance(Log.class, Sql2Class.class);
	private Object object;

	public static Object makeNewObject(Object object, PreparedStatement ps) {
		try {
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Field[] fields = object.getClass().getFields();
				for (Field field : fields) {
					field.setAccessible(true);
					field.set(object, rs.getString(field.getName()));
				}
			}
		} catch (SQLException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}

	public static Object makeNewObject(Object object, ResultSet rs) {
		try {
			Field[] fields = object.getClass().getFields();
			for (Field field : fields) {
				field.setAccessible(true);
				field.set(object, rs.getString(field.getName()));
			}
		} catch (SQLException | IllegalArgumentException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		return object;
	}

	public Sql2Class(Object object, PreparedStatement ps) {
		this.object = object;
		try {
			ResultSet rs = ps.executeQuery();
			Field[] fields = this.object.getClass().getFields();
			for (Field field : fields) {
				field.setAccessible(true);
				field.set(this.object, rs.getString(field.getName()));
			}
		} catch (SQLException | IllegalArgumentException
				| IllegalAccessException e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	public Sql2Class(String className, PreparedStatement ps) {
		try {
			this.object = Class.forName(className);
			ResultSet rs = ps.executeQuery();
			Field[] fields = this.object.getClass().getFields();
			for (Field field : fields) {
				field.setAccessible(true);
				field.set(this.object, rs.getString(field.getName()));
			}
		} catch (SQLException | IllegalArgumentException
				| IllegalAccessException | ClassNotFoundException e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	public void set(String fieldName, Class<?> args) {
		try {
			Field field = this.object.getClass().getField(fieldName);
			field.set(this.object, args);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	public Object makeNewObject() {
		return this.object;
	}
}
