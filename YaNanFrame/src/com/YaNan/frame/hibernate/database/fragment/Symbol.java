package com.YaNan.frame.hibernate.database.fragment;

public interface Symbol {
	public static enum JAVASCRIPT{
		FULL_EQS("==="),PLUS_EQ("+="),SUB_EQ("-="),MULTI_EQ("*="),DIV_EQ("/="),REMA_EQ("%="),
		EQS("=="),NOT_EQS("!="),AUTO_PLUS("++"),AUTO_SUB("--"),BIG_EQS(">="),SMALL_EQS("<="),
		PLUS("+"),SUB("-"),MULTI("*"),DIV("/"),REMA("%"),
		BIG(">"),SMALL("<"),EQ("="),
		AND("&&"),OR("||"),NOT("!")
		,IN("in"),NOT_IN("not in");
		public String value;
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		JAVASCRIPT(String value){
			this.value = value;
		}
		public static JAVASCRIPT match(String condition) {
			JAVASCRIPT[] jsSymbol = JAVASCRIPT.values();
			for(JAVASCRIPT js : jsSymbol){
				if(condition.contains(js.value))
					return js;
			}
			return null;
		}
	}
}
