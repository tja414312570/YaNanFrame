package com.YaNan.frame.hibernate.database.DBInterface;

import java.util.regex.Pattern;

public class MySql implements mySqlInterface {
	final public static int TYPE_TEXT = 0;
	final public static int TYPE_CHAR = 1;
	final public static int TYPE_ENUM = 2;
	final public static int TYPE_INT = 3;
	final public static int TYPE_FLOAT = 4;
	final public static int TYPE_DATE = 5;
	final public static String INT_TINYINT = "255";
	final public static String INT_TINYINT_SIGN = "127";
	final public static String INT_SMALLINT = "255";
	final public static String INT_SMALLINT_SIGN = "127";
	final public static String INT_MEDIUMINT = "255";
	final public static String INT_MEDIUMINT_SIGN = "127";
	final public static String INT_INT = "255";
	final public static String INT_INT_SIGN = "127";
	final public static String INT_BIGINT = "255";
	final public static String INT_BIGINT_SIGN = "127";
	final public static String DATE_DATE = "YYYY-MM-DD";
	final public static String DATE_DATETIME = "YYYY-MM-DD HH:MM:SS";
	final public static String DATE_TIMESTAMP = "YYYY-MM-DD";
	final public static String DATE_TIME = "YYYY-MM-DD HH:MM:SS";
	final public static String DATE_YEAR = "YYYY-MM-DD";

	private static Pattern textP = Pattern.compile(".*(text|blob|set)+.*",
			Pattern.CASE_INSENSITIVE), charP = Pattern.compile(".*char+.*",
			Pattern.CASE_INSENSITIVE), enmuP = Pattern.compile(".*enmu+.*",
			Pattern.CASE_INSENSITIVE), intP = Pattern.compile(".*int+.*",
			Pattern.CASE_INSENSITIVE), floatP = Pattern.compile(
			".*(float|double|decimal)+.*", Pattern.CASE_INSENSITIVE),
			dateP = Pattern.compile(".*(text|blob|enum|set)+.*",
					Pattern.CASE_INSENSITIVE);

	public static int macherType(String type) {
		if (textP.matcher(type).matches())
			return 0;
		if (charP.matcher(type).matches())
			return 1;
		if (enmuP.matcher(type).matches())
			return 2;
		if (intP.matcher(type).matches())
			return 3;
		if (floatP.matcher(type).matches())
			return 4;
		if (dateP.matcher(type).matches())
			return 5;
		return -1;
	}

	public static String defaultInt(String type, Boolean sign) {
		if (type.equals(COLUMN_TYPE_TINYINT)) {
			return sign ? INT_TINYINT_SIGN : INT_TINYINT;
		} else if (type.equals(COLUMN_TYPE_SMALLINT)) {
			return sign ? INT_SMALLINT_SIGN : INT_SMALLINT;
		} else if (type.equals(COLUMN_TYPE_MEDIUMINT)) {
			return sign ? INT_MEDIUMINT_SIGN : INT_MEDIUMINT;
		} else if (type.equals(COLUMN_TYPE_INT)) {
			return sign ? INT_INT_SIGN : INT_INT;
		} else if (type.equals(COLUMN_TYPE_BIGINT)) {
			return sign ? INT_BIGINT_SIGN : INT_BIGINT;
		}
		return "";
	}

	public static String defaultDate(String type, Boolean sign) {
		if (type.equals(COLUMN_TYPE_TINYINT)) {
			return sign ? INT_TINYINT_SIGN : INT_TINYINT;
		} else if (type.equals(COLUMN_TYPE_SMALLINT)) {
			return sign ? INT_SMALLINT_SIGN : INT_SMALLINT;
		} else if (type.equals(COLUMN_TYPE_MEDIUMINT)) {
			return sign ? INT_MEDIUMINT_SIGN : INT_MEDIUMINT;
		} else if (type.equals(COLUMN_TYPE_INT)) {
			return sign ? INT_INT_SIGN : INT_INT;
		} else if (type.equals(COLUMN_TYPE_BIGINT)) {
			return sign ? INT_BIGINT_SIGN : INT_BIGINT;
		}
		return "";
	}
}
