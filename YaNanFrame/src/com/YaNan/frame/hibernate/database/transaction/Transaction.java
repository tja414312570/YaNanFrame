package com.YaNan.frame.hibernate.database.transaction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.hibernate.database.Delete;
import com.YaNan.frame.hibernate.database.Insert;
import com.YaNan.frame.hibernate.database.Query;
import com.YaNan.frame.hibernate.database.Update;
import com.YaNan.frame.hibernate.database.DBInterface.OperateImplement;
import com.mysql.jdbc.Connection;

public class Transaction {
	private final int id;//事物ID
	private Map<String,Connection> coonMap = new HashMap<String,Connection>();//事物中所用到的连接
	private transient static Map<Integer,Transaction> transcations = new LinkedHashMap<Integer,Transaction>(); //事物集队列
	private List<OperateImplement> transcationQueue =   new ArrayList<OperateImplement>();//事物队列
	private Map<OperateImplement,Object> transResult = new HashMap<OperateImplement,Object>();//事物处理成功后的结果集
	private List<String> sqls = new ArrayList<String>();
	private Exception exception;
	private OperateImplement exceptionOperate;
	private int successIndex;
	public Map<String, Connection> getCoonMap() {
		return coonMap;
	}
	public void setCoonMap(Map<String, Connection> coonMap) {
		this.coonMap = coonMap;
	}
	public static Map<Integer, Transaction> getTranscations() {
		return transcations;
	}
	public static void setTranscations(Map<Integer, Transaction> transcations) {
		Transaction.transcations = transcations;
	}
	public List<OperateImplement> getTranscationQueue() {
		return transcationQueue;
	}
	public void setTranscationQueue(List<OperateImplement> transcationQueue) {
		this.transcationQueue = transcationQueue;
	}
	public Map<OperateImplement, Object> getTransResult() {
		return transResult;
	}
	public void setTransResult(Map<OperateImplement, Object> transResult) {
		this.transResult = transResult;
	}
	public Exception getException() {
		return exception;
	}
	private Transaction(int id){
		this.id = id;
	}
	public static Transaction getTransaction(){
		int id = (int)(Math.random()*10000000);
		Transaction Transaction=new Transaction(id);
		transcations.put(id, Transaction);
		return Transaction;
	}
	public static Transaction getTranscation(int id){
		return transcations.get(id);
	}
	public void addTranscation(OperateImplement... operators) {
		for(OperateImplement operator : operators)
			this.transcationQueue.add(operator);
	}
	public int getId() {
		return id;
	}
	public boolean start() {
		if(transcationQueue.size()>0){
			//开始事物
			Iterator<OperateImplement> iterator = transcationQueue.iterator();
			while(iterator.hasNext()){
				OperateImplement operate =iterator.next();
				this.sqls.add(operate.create());
				Connection coon = null;
				//获取连接
				if(coonMap.containsKey(operate.getDbTab().getDBName()))
					coon = coonMap.get(operate.getDbTab().getDBName());
				else{
					coon = operate.getDbTab().getDataBase().getConnection();
					coonMap.put(operate.getDbTab().getDBName(), coon);
					try {
						coon.setAutoCommit(false);
					} catch (SQLException e) {
						e.printStackTrace();
						try {
							coon.rollback();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
				//处理事物
				if(operate.getClass().equals(Update.class)){//更新
					Update update = (Update) operate;
					try {
						this.transResult.put(operate, operate.getDbTab().update(update,coon)); 
					} catch (SQLException e) {
						e.printStackTrace();
						if(this.coonMap.size()>0){
							Iterator<String> itera = this.coonMap.keySet().iterator();
							while(itera.hasNext()){
								try {
									String db = itera.next();
									Connection rc = this.coonMap.get(db);
									rc.rollback();
									DBFactory.getDataBase(db).releaseConnection(rc);
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}
						}
						this.exceptionOperate = operate;
						this.exception = e;
						return false;
					}
				}
				if(operate.getClass().equals(Query.class)){//更新
					Query query = (Query) operate;
					try {
						this.transResult.put(operate, operate.getDbTab().query(query,coon)); 
					} catch (SQLException | InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException | IllegalArgumentException e) {
						e.printStackTrace();
						if(this.coonMap.size()>0){
							Iterator<String> itera = this.coonMap.keySet().iterator();
							while(itera.hasNext()){
								try {
									String db = itera.next();
									Connection rc = this.coonMap.get(db);
									rc.rollback();
									DBFactory.getDataBase(db).releaseConnection(rc);
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}
						}
						this.exceptionOperate = operate;
						this.exception = e;
						return false;
					}
				}
				if(operate.getClass().equals(Delete.class)){//更新
					Delete delete = (Delete) operate;
					try {
						this.transResult.put(operate, operate.getDbTab().delete(delete,coon)); 
					} catch (SecurityException | IllegalArgumentException | SQLException e) {
						e.printStackTrace();
						if(this.coonMap.size()>0){
							Iterator<String> itera = this.coonMap.keySet().iterator();
							while(itera.hasNext()){
								try {
									String db = itera.next();
									Connection rc = this.coonMap.get(db);
									rc.rollback();
									DBFactory.getDataBase(db).releaseConnection(rc);
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}
						}
						this.exceptionOperate = operate;
						this.exception = e;
						return false;
					}
				}
				if(operate.getClass().equals(Insert.class)){//更新
					Insert insert = (Insert) operate;
					try {
						this.transResult.put(operate, operate.getDbTab().insert(insert,coon)); 
					} catch (SecurityException | IllegalArgumentException | IllegalAccessException | SQLException e) {
						e.printStackTrace();
						if(this.coonMap.size()>0){
							Iterator<String> itera = this.coonMap.keySet().iterator();
							while(itera.hasNext()){
								try {
									String db = itera.next();
									Connection rc = this.coonMap.get(db);
									rc.rollback();
									DBFactory.getDataBase(db).releaseConnection(rc);
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}
						}
						this.exceptionOperate = operate;
						this.exception = e;
						return false;
					}
				}
				this.successIndex++;
			}
			Iterator<String> itera = this.coonMap.keySet().iterator();
			while(itera.hasNext()){
				try {
					String db = itera.next();
					Connection rc = this.coonMap.get(db);
					rc.commit();
					DBFactory.getDataBase(db).releaseConnection(rc);
					rc.setAutoCommit(true);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return true;
	}
	public OperateImplement getExceptionOperate() {
		return exceptionOperate;
	}
	public int getExceptionIndex() {
		return this.successIndex+1;
	}
	public int getSuccessIndex(){
		return this.successIndex;
	}
	public List<String> getSqls() {
		return sqls;
	}
	
}
