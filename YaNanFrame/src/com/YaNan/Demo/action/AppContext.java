package com.YaNan.Demo.action;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.YaNan.frame.RTDT.RTDT;
import com.YaNan.frame.RTDT.context.ActionManager;
import com.YaNan.frame.core.servlet.DefaultServletMapping;
import com.YaNan.frame.core.servlet.ServletBean;
import com.YaNan.frame.core.servlet.ServletResult;
import com.YaNan.frame.core.servlet.annotations.Action;
import com.YaNan.frame.core.servlet.annotations.Validate;
import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.TokenManager;
import com.YaNan.frame.hibernate.database.DBColumn;
import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.hibernate.database.DBTab;
import com.YaNan.frame.hibernate.database.DataBase;
import com.YaNan.frame.hibernate.database.DataBaseConfigure;
import com.google.gson.Gson;


public class AppContext{
	@Validate(isNull="{\"code\":4281,\"message\":\"数据库名称为空\"}")
	private String databaseName;
	
	private ServletBeanModel[] ServletBeanModels;
	
	@Action(description="获取所有的Servlet")
	
	public String getAllServlet(){
		return "{\"data\":"+new Gson().toJson(getServletBeanModelList())+"}";
	}
	@Action(description="获取Servlet数量")
	public int getServletNum(){
		if(ServletBeanModels==null)
			ServletBeanModels = getServletBeanModelList();
		return ServletBeanModels.length;
	}
	@Action(description="获取RTDT数量")
	public int getRTDTActionNum(){
		ActionManager actionManager = RTDT.getActionManager();
		return actionManager.getActionPools().size();
	}
	@Action(description="获取Token的数量")
	public int getTokenNum(){
		return Token.getTokens().size();
	}
	@Action(description="获取Token拦截实体的数量")
	public int getTokenFilterNum(){
		return TokenManager.getInstance().getTokenMap().size();
	}
	@Action(description="获取数据库的数量")
	public int getDataBaseNum(){
		return DBFactory.getDBFactory().getDataBases().size();
	}
	@Action(description="获取数据表的数量")
	public int getDataTablesNum(){
		return  DBFactory.getDBFactory().getTabMappingCaches().size();
	}
	@Action(description="查看数据库配置")
	public String getDataBase(){
		List<DataBaseConfigure> dbList = new ArrayList<DataBaseConfigure>();
		Iterator<DataBase> iterator = DBFactory.getDBFactory().getDataBases().values().iterator();
		while(iterator.hasNext())
			dbList.add(iterator.next().getDbConfigure());
		return "{\"data\":"+new Gson().toJson(dbList)+"}";
	}
	@Action(description="用于获取数据库下的数据表",args="databaseName")
	public String getDataTabels(){
		DataBase db = DBFactory.getDataBase(this.databaseName);
		Iterator<DBTab> iterator = db.getTabMapping().values().iterator();
		List<TablesBeanModel> list = new ArrayList<TablesBeanModel>();
		while(iterator.hasNext())
			list.add(new TablesBeanModel(iterator.next()));
		return list.size()==0?"{\"code\":4281,\"message\":\"此数据库下无数据表\"}":"{\"code\":4280,\"data\":"+new Gson().toJson(list)+"}";
	}
	public ServletBeanModel[] getServletBeanModelList(){
		DefaultServletMapping defaultServletMapping = DefaultServletMapping.getInstance();
		Collection<ServletBean> collects = defaultServletMapping.getServletMapping().values();
		ServletBeanModel[] list = new ServletBeanModel[collects.size()];
		Iterator<ServletBean> iterator = collects.iterator();
		int i = 0;
		while(iterator.hasNext()){
			list[i]  = new ServletBeanModel(iterator.next());
			i++;
		}
		return list;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public static class TablesBeanModel{
		private Field AIField;
		private String charset;
		private String cls;
		private String collate;
		private String DBName;
		private boolean isMust;
		private List<DBColumn> columns = new ArrayList<DBColumn>();
		private String name;
		private String value;
		public TablesBeanModel(DBTab tab){
			this.AIField = tab.getAIField();
			this.charset = tab.getCharset();
			this.cls = tab.getCls().getName();
			this.DBName = tab.getDBName();
			this.isMust = tab.isMust();
			this.name = tab.getName();
			this.value = tab.getValue();
			Iterator<DBColumn> iterator = tab.getDBColumns().values().iterator();
			while(iterator.hasNext())
				columns.add(iterator.next());
		}
		public Field getAIField() {
			return AIField;
		}
		public void setAIField(Field aIField) {
			AIField = aIField;
		}
		public String getCharset() {
			return charset;
		}
		public void setCharset(String charset) {
			this.charset = charset;
		}
		public String getCls() {
			return cls;
		}
		public void setCls(String cls) {
			this.cls = cls;
		}
		public String getCollate() {
			return collate;
		}
		public void setCollate(String collate) {
			this.collate = collate;
		}
		public String getDBName() {
			return DBName;
		}
		public void setDBName(String dBName) {
			DBName = dBName;
		}
		public boolean isMust() {
			return isMust;
		}
		public void setMust(boolean isMust) {
			this.isMust = isMust;
		}
		public List<DBColumn> getColumns() {
			return columns;
		}
		public void setColumns(List<DBColumn> columns) {
			this.columns = columns;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	/**
	 * Servlet映射
	 * @author yanan
	 *
	 */
	public static class ServletBeanModel{
		private List<ServletResult> result = new ArrayList<ServletResult>();
		private String className;
		private String namespace;
		private String actionName;
		private String method;
		private int type;
		private boolean output;
		private boolean decode;
		private boolean corssOrgin;
		private String[] args={};
		private String description;
		ServletBeanModel(ServletBean bean){
			this.className = bean.getServletClass().getName();
			this.method = bean.getMethod().getName();
			this.type = bean.getType();
			this.output = bean.hasOutputStream();
			this.decode = bean.decode();
			this.corssOrgin = bean.isCorssOrgin();
			this.args = bean.getArgs();
			this.description = bean.getDescription();
			Iterator<String> iterator = bean.getResultIterator();
			while(iterator.hasNext())
				this.result.add(bean.getResult(iterator.next()));
		}
		public List<ServletResult> getResult() {
			return result;
		}
		public void setResult(List<ServletResult> result) {
			this.result = result;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getNamespace() {
			return namespace;
		}
		public void setNamespace(String namespace) {
			this.namespace = namespace;
		}
		public String getActionName() {
			return actionName;
		}
		public void setActionName(String actionName) {
			this.actionName = actionName;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public boolean isOutput() {
			return output;
		}
		public void setOutput(boolean output) {
			this.output = output;
		}
		public boolean isDecode() {
			return decode;
		}
		public void setDecode(boolean decode) {
			this.decode = decode;
		}
		public boolean isCorssOrgin() {
			return corssOrgin;
		}
		public void setCorssOrgin(boolean corssOrgin) {
			this.corssOrgin = corssOrgin;
		}
		public String[] getArgs() {
			return args;
		}
		public void setArgs(String[] args) {
			this.args = args;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
}
