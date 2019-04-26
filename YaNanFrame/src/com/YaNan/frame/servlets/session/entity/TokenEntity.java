package com.YaNan.frame.servlets.session.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.servlets.session.Token;
import com.YaNan.frame.utils.PathMatcher;

public class TokenEntity {
	private String namespace;
	private String roles;
	private String chain;
	private String[] chains;
	private String CLASS;
	//name result
	private Map<String,Result> resultMap = new HashMap<String,Result>();
	//  url  role
	private Map<String,String> urlMap = new HashMap<String,String>();
	private Result result;
	private String value;
	private Failed failed;
	public Map<String, Result> getResultMap() {
		return resultMap;
	}
	public void setResultMap(Map<String, Result> resultMap) {
		this.resultMap = resultMap;
	}
	public List<String> geturlMap() {
		List<String> list = new ArrayList<String>();
		Iterator<String> iterator = urlMap.keySet().iterator();
		while(iterator.hasNext());
			list.add(iterator.next());
		return list;
	}
	public void seturlMap(Map<String, String> urlMap) {
		this.urlMap = urlMap;
	}
	public Result getResult() {
		return result;
	}
	public void setResult(Result result) {
		this.result = result;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Failed getFailed() {
		return failed;
	}
	public void setFailed(Failed failed) {
		this.failed = failed;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String role) {
		this.roles = role;
	}
	public String getChain() {
		return chain;
	}
	public void setChain(String chain) {
		this.chain = chain;
	}
	public String getCLASS() {
		return CLASS;
	}
	public void setCLASS(String cLASS) {
		CLASS = cLASS;
	}
	public void addResult(Result result){
		if(result.getName()==null)
			this.resultMap.put("default", result);
		this.resultMap.put(result.getName(), result);
		this.urlMap.put(result.getValue(),result.getRole());
	}
	public Result getResult(String name){
		return this.resultMap.get(name);
	}
	public boolean hasResult(String name){
		return this.resultMap.containsKey(name);
	}
	public Result getDefaultResult(){
		if(this.resultMap.containsKey("default"))
			return this.resultMap.get("default");
		return null;
	}
	@Override
	public String toString() {
		return "TokenEntity [namespace=" + namespace + ", roles=" + roles + ", chain=" + chain + ", CLASS=" + CLASS
				+ ", resultMap=" + resultMap + ", urlMap=" + urlMap + ", result=" + result + ", value=" + value
				+ ", failed=" + failed + "]";
	}
	public boolean containURL(String requestURL) {
		if(this.value!=null&&requestURL.contains(this.value))
			return true;
		if(this.failed!=null&&this.failed.getValue()!=null&&requestURL.contains(this.failed.getValue()))
			return true;
		Iterator<String> iterator = this.urlMap.keySet().iterator();
		while(iterator.hasNext()){
			String url = iterator.next();
			if(!url.equals("")&&PathMatcher.match(url, requestURL).isMatch())
				return true;
		}
		return false;
	}
	public boolean chainURL(String url,Token token){
		if(this.value!=null&&PathMatcher.match(this.value, url).isMatch())
			return true;
		if(this.failed!=null&&this.failed.getValue()!=null&&PathMatcher.match(this.failed.getValue(), url).isMatch())
			return true;
		if(this.chain!=null){
			if(this.chains==null){
				this.chains = this.chain.split(",");
				for(int i = 0 ;i<this.chains.length;i++){
					this.chains[i] = this.chains[i].trim();
				}
			}
			for(String str : this.chains){
				if(!str.contains(".do")&&PathMatcher.match(str, url).isMatch())
						return true;
			}
		}
		Iterator<String> iterator = this.urlMap.keySet().iterator();
		while(iterator.hasNext()){
			String u = iterator.next();
			if(!u.equals("")&&PathMatcher.match(u, url).isMatch()){
				if(this.urlMap.get(u)==null||this.urlMap.get(u).equals(""))
					return true;
				String role = this.urlMap.get(u);
				if(token.isRole(role))
					return true;
			}
		}
		return false;
	}
	public boolean hasResult(){
		return this.resultMap.size()==0;
	}
	public boolean hasFailed(){
		return this.failed!=null;
	}
}