package com.YaNan.frame.hibernate.database;

import java.util.ArrayList;
import java.util.List;

public class Case {
	private String column;
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public List<String> getCases() {
		return cases;
	}
	public void setCases(List<String> cases) {
		this.cases = cases;
	}
	private List<String> cases = new ArrayList<String>();
	public Case(String column){
		this.column = column;
	}
	public void addCase(String condition,String express){
		this.cases.add(condition+" THEN "+express);
	}
	public void addCaseAnd(String express,String... conditions){
		String cr = "";
		for(int i = 0 ;i<conditions.length;i++){
			cr += conditions[i]+(i<conditions.length-1?" AND ":" ");
		}
		this.cases.add(cr+" THEN "+express);
	}
	public void addCaseOr(String express,String... conditions){
		String cr = "";
		for(int i = 0 ;i<conditions.length;i++){
			cr += conditions[i]+(i<conditions.length-1?" OR ":" ");
		}
		this.cases.add(cr+" THEN "+express);
	}
	public void addCaseNot(String express,String... conditions){
		String cr = "";
		for(int i = 0 ;i<conditions.length;i++){
			cr += conditions[i]+(i<conditions.length-1?" NOT ":" ");
		}
		this.cases.add(cr+" THEN "+express);
	}
	public String create(){
		String cs = " CASE ";
		for(String wh : this.cases)
			cs+="WHEN "+wh+" ";
		cs+="ELSE "+this.column+" END";
		return cs;
	}
}
