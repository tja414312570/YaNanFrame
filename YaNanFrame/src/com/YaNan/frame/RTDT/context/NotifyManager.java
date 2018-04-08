package com.YaNan.frame.RTDT.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.RTDT.entity.Notification;
import com.YaNan.frame.RTDT.entity.NotifyEntity;

public class NotifyManager {
	private static NotifyManager notify;

	public static NotifyManager getManager() {
		if (notify == null)
			notify = new NotifyManager();
		return notify;
	}
	//notify 实体存储
	private Map<String,NotifyEntity> notifyEntitys = new HashMap<String,NotifyEntity>();
	//存储方式 name List<entity>
	private volatile Map<String, List<Notification>> nameNotifyPools = new HashMap<String, List<Notification>>();
	//存储方式 Token Map<name,entity>
	private volatile Map<String, Map<String, Notification>> tokenNotifyPools = new HashMap<String, Map<String, Notification>>();
	//存储方式 mark Map<token,entity>
	private volatile Map<Integer,Map<String, Notification>> markNotifyPools = new HashMap<Integer, Map<String, Notification>>();
	//存储方式 AUID entity
	private volatile Map<String,Notification> auidNotifyPools = new HashMap<String,Notification>();
	//存储方式 SessionId Notify 
	private volatile Map<String,Notification> sessionNotifyPools = new HashMap<String,Notification>();
	
	public Map<String, List<Notification>> getAllNotify() {
		return this.nameNotifyPools;
	}

	public void bindNotify(Notification notify) {
		String notifyName = notify.getName();
		//nameNotify存储
		if (this.nameNotifyPools.containsKey(notifyName)){
			this.nameNotifyPools.get(notifyName).add(notify);
		} else {
			List<Notification> list = new ArrayList<Notification>();
			this.nameNotifyPools.put(notifyName, list);
		}
		//tokenNotify存储
		String TokenId = notify.getToken().getTokenId();
		if(this.tokenNotifyPools.containsKey(tokenNotifyPools)){
			this.tokenNotifyPools.get(TokenId).put(notifyName, notify);
		}else{
			Map<String, Notification> map = new HashMap<String, Notification>();
			map.put(notifyName, notify);
			this.tokenNotifyPools.put(TokenId,map);
		}
		//markNotify存储
		int mark = notify.getMark();
		if (this.markNotifyPools.containsKey(mark)) {
			this.markNotifyPools.get(mark).put(notify.getValue(), notify);
		} else {
			Map<String, Notification> map = new HashMap<String, Notification>();
			map.put(notify.getValue(), notify);
			this.markNotifyPools.put(mark,map);
		}
		//AUIDNotify存储
		String AUID = notify.getAction().getRequetAction().getAUID();
		this.auidNotifyPools.put(AUID, notify);
		//sessionNotify存储
		String sessionId = notify.getAction().getRequetAction().getSessionId();
		this.sessionNotifyPools.put(sessionId, notify);
	}
	//通过名称和TUID解绑
	public void unbindNotify(String notifyName, String TokenId) {
		if(this.tokenNotifyPools.containsKey(TokenId) 
				&&this.tokenNotifyPools.get(TokenId).containsKey(notifyName))
				this.unbindNotify(this.tokenNotifyPools.get(TokenId).get(notifyName));
	}
	//通过TUID解绑
	public void unbindNotifyByToken(String TokenId){
			Map<String,Notification> map = this.tokenNotifyPools.get(TokenId);
			if(map!=null)
			for(Notification notify : map.values())
				this.unbindNotify(this.tokenNotifyPools.get(TokenId).get(notify));
	}
	//通过标志解绑
	public void unbindNotifyByMark(int mark, String token) {
		 if(this.markNotifyPools.containsKey(mark))
			 this.unbindNotify(this.markNotifyPools.get(mark).get(token));
	}
	//通过标志解绑
	public void unbindNotifysByMark(int mark) {
		Map<String, Notification> map = this.markNotifyPools.get(mark);
		for(Notification notify : map.values())
			this.unbindNotify(notify);
	}
	//通过session解绑
	public void unbindNotify(String id) {
		if(this.sessionNotifyPools.containsKey(id))
			this.unbindNotify(this.sessionNotifyPools.get(id));
	}
	public void unbindNotify(Notification notify) {
		if(notify==null)
			return;
		String notifyName = notify.getName();
		//nameNotify存储
		if (this.nameNotifyPools.containsKey(notifyName))
			this.nameNotifyPools.get(notifyName).remove(notify);
		//tokenNotify存储
		String TokenId = notify.getToken().getTokenId();
		if(this.tokenNotifyPools.containsKey(tokenNotifyPools))
			this.tokenNotifyPools.get(TokenId).remove(notifyName);
		//markNotify存储
		int mark = notify.getName().hashCode()+notify.getMark();
		if (this.markNotifyPools.containsKey(mark)) 
			this.markNotifyPools.get(mark).remove(notify.getValue());
		//AUIDNotify存储
		String AUID = notify.getAction().getRequetAction().getAUID();
		this.auidNotifyPools.remove(AUID);
		notify.destory();
	}
	public Map<String, Notification> getNotifysByMark(String mark) {
		return (Map<String, Notification>) this.markNotifyPools.get(mark);
	}
	public Notification getNotifyByMark(String mark, String value) {
		if (this.markNotifyPools.containsKey(mark))
			return (Notification) ((Map<?, ?>) this.markNotifyPools.get(mark)).get(value);
		return null;
	}
	public void addEntity(NotifyEntity entity) {
		this.notifyEntitys.put(entity.getName(), entity);
	}
	public boolean hasEntity(String name){
		return this.notifyEntitys.containsKey(name);
	}
	public NotifyEntity getEntity(String name){
		return this.notifyEntitys.get(name);
	}

	public List<Notification> getNotifyByName(String name) {
		return this.nameNotifyPools.get(name);
	}

	public static NotifyManager getNotify() {
		return notify;
	}

	public Map<String, NotifyEntity> getNotifyEntitys() {
		return notifyEntitys;
	}

	public Map<String, List<Notification>> getNameNotifyPools() {
		return nameNotifyPools;
	}

	public Map<String, Map<String, Notification>> getTokenNotifyPools() {
		return tokenNotifyPools;
	}

	public Map<Integer, Map<String, Notification>> getMarkNotifyPools() {
		return markNotifyPools;
	}

	public Map<String, Notification> getAuidNotifyPools() {
		return auidNotifyPools;
	}

	public Map<String, Notification> getSessionNotifyPools() {
		return sessionNotifyPools;
	}
}
