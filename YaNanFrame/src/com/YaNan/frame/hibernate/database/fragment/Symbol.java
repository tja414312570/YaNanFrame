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
					if(js.value.equals("in") || js.value.equals("not in")){
						int index=condition.indexOf(js.value);
						while(index >-1){
							if(index==condition.length())
								return js;
							else{
								String after = condition.substring(index+js.value.length(),index+1+js.value.length());
								if(index==0){
									if(after.equals("(")||after.equals(" "))
										return js;
								}else{
									String before = condition.substring(index-1,index);
									if((after.equals("(")||after.equals(" "))
											&&(before.equals(")")||before.equals(" ")))
										return js;
								}
							}
							index=condition.indexOf(js.value,index+1);
						}
					}else{
						if(condition.indexOf(js.value)>0)
							return js;
					}
			}
			return null;
		}
	}
}
