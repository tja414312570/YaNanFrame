package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Field;
import java.sql.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.YaNan.frame.hibernate.database.DBInterface.MySql;
import com.YaNan.frame.hibernate.database.DBInterface.mySqlInterface;
import com.YaNan.frame.hibernate.database.annotation.Column;

/**
 * java字段与数据库的列的映射实体类</br>
 * @author yanan
 *
 */
public class DBColumn implements mySqlInterface {
	private Field field;
	private String name;
	private Object value;
	private String type;
	private int Length;
	private int Size;
	private String Format;
	private boolean Not_Null;
	private boolean Point;
	private boolean Auto_Increment;
	private boolean Primary_Key;
	private boolean Not_Sign;
	private boolean Auto_Fill;
	private boolean Unique;
	private String Annotations;
	private String charset="";
	private String collate="";
	private final transient Logger log = LoggerFactory.getLogger(DBColumn.class);

	public DBColumn(Field field, Column column) {
		setField(field);
		setAuto_Fill(column.Auto_Fill());
		setAuto_Increment(column.Auto_Increment());
		setLength(column.length() > 0 ? column.length() : 255);
		setSize(column.size());
		setFormat(column.format());
		setName(column.name().equals("") ? field.getName() : column.name());
		setNot_Null(column.Not_Null());
		setNot_Sign(column.Not_Sign());
		setPoint(column.point());
		setPrimary_Key(column.Primary_Key());
		setUnique(column.unique());
		setType(column.type().equals("") ? this.getType(field) : column.type());
		setValue(column.value());
		setAnnotations(column.Annotations());
		setCharset(column.charset());
		setCollate(column.collate());
		log.debug(columnDesc());
	}

	public DBColumn(Field field) {
		setField(field);
		log.debug("this Field [" + field.getName() + "] is not annotion,all attribute set default");
		setAuto_Fill(false);
		setAuto_Increment(false);
		setLength(255);
		setSize(11);
		setFormat("");
		setName(field.getName());
		setNot_Null(false);
		setNot_Sign(true);
		setPoint(false);
		setPrimary_Key(false);
		setUnique(false);
		setType(this.getType(field));
		setValue("");
		setAnnotations("");
		log.debug(columnDesc());
	}

	public String columnDesc() {
		return "Column INFO:\r\n 		Name:" + name + "\r\n 		Value:" + value
				+ "\r\n 		Type:" + type + "\r\n 		Length:" + Length
				+ "\r\n 		Size:" + Size + "\r\n 		Format:" + Format
				+ "\r\n 		Not_Null:" + Not_Null + "\r\n 		Point:" + Point
				+ "\r\n 		Auto_Increment:" + Auto_Increment
				+ "\r\n 		Primary_Key:" + Primary_Key + "\r\n 		Not_Sign:"
				+ Not_Sign + "\r\n 		Auto_Fill:" + Auto_Fill + "\r\n 		Unique:"
				+ Unique + "\r\n 		Annotations:" + Annotations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return Length;
	}

	public void setLength(int length) {
		Length = length;
	}

	public boolean isNot_Null() {
		return Not_Null;
	}

	public void setNot_Null(boolean not_Null) {
		Not_Null = not_Null;
	}

	public boolean isPoint() {
		return Point;
	}

	public void setPoint(boolean point) {
		Point = point;
	}

	public boolean isAuto_Increment() {
		return Auto_Increment;
	}

	public void setAuto_Increment(boolean auto_Increment) {
		Auto_Increment = auto_Increment;
	}

	public boolean isPrimary_Key() {
		return Primary_Key;
	}

	public void setPrimary_Key(boolean primary_Key) {
		Primary_Key = primary_Key;
	}

	public boolean isNot_Sign() {
		return Not_Sign;
	}

	public void setNot_Sign(boolean not_Sign) {
		Not_Sign = not_Sign;
	}

	public boolean isAuto_Fill() {
		return Auto_Fill;
	}

	public void setAuto_Fill(boolean auto_Fill) {
		Auto_Fill = auto_Fill;
	}

	public String getAnnotations() {
		return Annotations;
	}

	public void setAnnotations(String annotations) {
		Annotations = annotations;
	}

	private String getTypeConstraints() {
		int t = MySql.macherType(this.type);
		switch (t) {
		case MySql.TYPE_CHAR:
			return (type == null ? "" : type)
					+ (Length <= 0 || Length > 255 ? "(255)" : "(" + Length
							+ ")");
		case MySql.TYPE_TEXT:
			return type;
		case MySql.TYPE_INT:
			return (type == null ? "" : type)
					+ (Length < 0 ? "" : "(" + MySql.defaultInt(type, Not_Sign)
							+ ")");
		case MySql.TYPE_ENUM:
			break;
		case MySql.TYPE_FLOAT:
			return (type == null ? "" : type)
					+ (Format.equals("") ? "" : "(" + Format + ")");
		case MySql.TYPE_DATE:
			return (type == null ? "" : type)
					+ (Format.equals("") ? MySql.defaultDate(type, Not_Sign)
							: "(" + Format + ")");
		}
		return type;
	}

	public String Constraints() {

		return this.getTypeConstraints()
				+(charset.equals("")?"":" CHARACTER SET "+charset)
				+(collate.equals("")?"":" COLLATE "+collate)
				+ (Not_Null == true ? " NOT NULL" : "")
				+ (Auto_Increment == true ? " AUTO_INCREMENT" : "")
				// +(Not_Sign==true?" NOT SIGN ":"")
				+ (Auto_Fill == true ? " AUTO FILL" : "")
				+ (Annotations != null && !Annotations.equals("") ? " COMMENT '"
						+ Annotations + "'"
						: "");
	}

	public int getSize() {
		return Size;
	}

	public void setSize(int size) {
		Size = size;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	private String getType(Field field) {
		Class<?> cls = field.getType();
		if (cls.equals(int.class)) 
			return COLUMN_TYPE_INT;
		if (cls.equals(Integer.class))
			return COLUMN_TYPE_INTEGER;
		if (cls.equals(byte.class)) 
			return COLUMN_TYPE_TINYINT;
		if (cls.equals(short.class))
			return COLUMN_TYPE_SMALLINT;
		if (cls.equals(long.class))
			return COLUMN_TYPE_BIGINT;
		if (cls.equals(float.class))
			return COLUMN_TYPE_FLOAT;
		if (cls.equals(double.class))
			return COLUMN_TYPE_DOUBLE;
		if (cls.equals(boolean.class))
			return COLUMN_TYPE_TEXT;
		if (cls.equals(String.class))
			return COLUMN_TYPE_LONGTEXT;
		if (cls.equals(char.class))
			return COLUMN_TYPE_TEXT;
		if (cls.equals(Date.class))
			return COLUMN_TYPE_DATE;
		return COLUMN_TYPE_TEXT;
	}

	public boolean isUnique() {
		return Unique;
	}

	public void setUnique(boolean unique) {
		Unique = unique;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getCollate() {
		return collate;
	}

	public void setCollate(String collate) {
		this.collate = collate;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}
}