package com.YaNan.frame.hibernate.json;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.YaNan.frame.service.Log;
import com.mysql.jdbc.PreparedStatement;

public class Sql2Class {
	private Log log = Log.getSystemLog();
	private Object object;

	public static Object makeNewObject(Object object, PreparedStatement ps) {
		Log log = Log.getSystemLog();
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
			log.write(e.toString());
			e.printStackTrace();
		}
		return object;
	}

	public static Object makeNewObject(Object object, ResultSet rs) {
		Log log = Log.getSystemLog();
		try {
			Field[] fields = object.getClass().getFields();
			for (Field field : fields) {
				field.setAccessible(true);
				field.set(object, rs.getString(field.getName()));
			}
		} catch (SQLException | IllegalArgumentException
				| IllegalAccessException e) {
			log.write(e.toString());
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
			log.write(e.toString());
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
			log.write(e.toString());
			e.printStackTrace();
		}
	}

	public void set(String fieldName, Class<?> args) {
		try {
			Field field = this.object.getClass().getField(fieldName);
			field.set(this.object, args);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			log.write(e.toString());
			e.printStackTrace();
		}
	}

	public Object makeNewObject() {
		return this.object;
	}
}
