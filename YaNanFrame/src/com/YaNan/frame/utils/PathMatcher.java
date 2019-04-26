package com.YaNan.frame.utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.YaNan.frame.servlets.parameter.DefaultParameterHandler;

/**
 * ant风格路径匹配
 * 匹配规则 
 * * 匹配0-n个非"/"的数据
 * ** 匹配任意数据
 * ? 匹配任意一个数据  不包含"/"
 * 变量使用
 * 将变量放在中括号之中，例如{page},默认将变量转为*，若要转为另外两种
 * 匹配方式，在变量后面追加匹配符号，如{page**},如果为?，在?之后，可以
 * 填写数字，以转化为此数字个数的?，例如{index?2}和{index??}等效。
 * 20190109
 * @author yanan
 *
 */
public class PathMatcher {
	private static Map<String,PathMatcher> tokensCache = new HashMap<String,PathMatcher>();
	private List<Token> tokens;
	private String express;
	private String buildExpress;
	private boolean result;
	/**
	 * 创建一个ant path matcher的表达式解析
	 * @param express
	 */
	public PathMatcher(String express){
		this.express = express;
		this.buildExpress = express;
		PathMatcher pathMatcher = tokensCache.get(express);
		if(pathMatcher==null){
			this.buildVariableToken();
			tokensCache.put(express,this);
		}else{
			this.tokens = pathMatcher.getTokens();
		}
		
	}
	/**
	 * 获取一个ant path matcher的表达式解析
	 * @param express
	 * @return
	 */
	public static PathMatcher getPathMatcher(String express) {
		PathMatcher pathMatcher = tokensCache.get(express);
		if(pathMatcher==null){
			pathMatcher = new PathMatcher(express);
			pathMatcher.buildVariableToken();
			tokensCache.put(express,pathMatcher);
		}
		return pathMatcher;
	}
	

	/**
	 * 将表达式生成token
	 * 
	 * @param reg
	 * @return
	 */
	public static List<Token> buildToken(String express,int point) {
		int last = 0;
		int index = 0;
		Token token;
		List<Token> tokens = new ArrayList<Token>();
iterator: while (index < express.length()) {
			char ch = express.charAt(index);
			switch (ch) {
			case '*':
				if(index>0){
					token = new Token();
					token.setToken(express.substring(last, index));
					tokens.add(token);
					token.setIndex(point++);
				}
				if (index == express.length()-1 || express.charAt(index+1) != '*') {
					token = new Token();
					token.setToken("*");
					token.setIndex(point++);
					token.setType(1);
					tokens.add(token);
					index++;
				} else {
					token = new Token();
					token.setToken("**");
					token.setIndex(point++);
					token.setType(2);
					tokens.add(token);
					index=index+2;
				}
				last = index;
				continue iterator;
			case '?':
				if(index>0){
					token = new Token();
					token.setToken(express.substring(last, index));
					tokens.add(token);
					token.setIndex(point++);
				}
				StringBuffer sb = new StringBuffer("?");
				while (index +1 < express.length()){
					if(express.charAt(index+1) == '?'){
						sb.append('?');
						index++;
					}else break;
				}
				token = new Token();
				token.setToken(sb.toString());
				tokens.add(token);
				token.setType(3);
				token.setIndex(point++);
				last = ++index;
				continue iterator;
			}
			index++;
		}
		if (last < express.length()) {
			token = new Token();
			token.setToken(express.substring(last));
			token.setIndex(point++);
			tokens.add(token);
		}
		return tokens;
	}
	
	public void buildVariableToken() {
		this.tokens = new ArrayList<Token>();
		int index = 0;
		// 分段截取
		StringBuffer buffer = new StringBuffer();
		int preIndex = 0;
		int sufIndex = 0;
		if (express.indexOf("{", sufIndex) == -1) {
			buffer.append(express);
			tokens = buildToken(express,index);
		} else
			while ((preIndex = express.indexOf("{", sufIndex)) != -1) {
				String temp = null;
				// 获取变量的前面部分内容
				if (preIndex > 0) {
					temp = express.substring(sufIndex, preIndex);
					List<Token> tokenFragment = buildToken(temp,index);
					tokens.addAll(tokenFragment);
					index = index+tokenFragment.size();
					buffer.append(temp);
				}
				// 截取变量部分
				sufIndex = express.indexOf("}", preIndex);
				if (sufIndex < 0)
					throw new RuntimeException(
							"express " + express + " error,the Variable descriptors are not equal,please check ");
				String exp = "*";
				String variable = express.substring(preIndex + 1, sufIndex);
				// 新建一个Token
				Token token = new Token();
				token.setType(1);
				int i;
				if ((i = variable.indexOf("*")) > -1) {
					if (variable.indexOf("*", i + 1) > -1) {
						token.setType(2);
						exp = "**";
					}
					variable = variable.substring(0, i);
				}
				if ((i = variable.indexOf("?")) > -1) {
					token.setType(3);
					if (variable.length() - 1 == i) {
						exp = "?";
					} else {
						if (variable.charAt(i + 1) == '?') {
							exp = variable.substring(i);
						} else {
							try {
								String num = variable.substring(i + 1);
								if (!num.isEmpty()) {
									exp = "?";
									int bum = (int) DefaultParameterHandler.parseBaseType(int.class, num, null);
									while (--bum > 0)
										exp += "?";
								}
							} catch (ParseException pe) {

							}

						}
					}
					variable = variable.substring(0, i);
				}
				token.setName(variable);
				token.setToken(exp);
				token.setIndex(index++);
				tokens.add(token);
				buffer.append(exp);
				// 末尾部分内容
				if (express.indexOf("{", sufIndex + 1) == -1 && sufIndex < express.length() - 1) {
					temp = express.substring(sufIndex + 1);
					List<Token> tokenFragment = buildToken(temp,index);
					tokens.addAll(tokenFragment);
					index = index+tokenFragment.size();
					buffer.append(temp);
				}
				sufIndex++;
			}
		this.buildExpress = buffer.toString();
	}
	
	public Matcher match(String path){
		List<Token> tokenList = new ArrayList<Token>();
		for(Token token : tokens)
			try {
				tokenList.add(token.clone());
			} catch (CloneNotSupportedException e) {
			}
		Matcher matcher = new Matcher(this.buildExpress, path, tokens, matchURI(path, tokens));
		return matcher;
	}
	/**
	 * ant路径匹配
	 * @param express 表达式
	 * @param path 路径
	 * @return Matcher 匹配结果
	 */
	public static Matcher match(String express, String path) {
		PathMatcher pathMatcher = PathMatcher.getPathMatcher(express);
		return pathMatcher.match(path);
	}
	
	public static boolean matchURI(String resource, List<Token> tokens) {
		if(resource==null)
			throw new RuntimeException("path is null");
		if(tokens==null)
			throw new RuntimeException("token set is null");
		Iterator<Token> iterator = tokens.iterator();
		Token nT;
		int p;
		while (iterator.hasNext()) {
			Token token = iterator.next();
			if (token.getType() == 0) {
				if (!resource.startsWith(token.getToken())) {
					return false;
				} else if(iterator.hasNext()){
					resource = resource.substring(token.getToken().length());
				}else{
					return token.getToken().length() == resource.length();
				}
			} else {
				switch (token.getType()) {
				case 1:// *
						// 获取下一个token
					if (iterator.hasNext()) {
						nT = iterator.next();
						p = resource.indexOf(nT.getToken());
						if (p < 0)
							return false;
						String ma = resource.substring(0, p);
						if (ma.indexOf("/") != -1) {
							return false;
						}
						token.setValue(ma);
						resource = resource.substring(p + nT.getToken().length());
					} else {// 最后一个位置
						String ma = resource;
						if (ma.indexOf("/") != -1) {
							return false;
						}
							token.setValue(ma);
					}
					break;
				case 2:// **
					String var;
					if (iterator.hasNext()) {
						nT = iterator.next();
						int n = 0;
						String temp;
						while ((p = resource.indexOf(nT.getToken(), n + 1)) > -1) {
							var = resource.substring(0, p);
							temp = resource.substring(p + nT.getToken().length());
							if (matchURI(temp, tokens.subList(tokens.indexOf(nT) + 1, tokens.size()))) {
									token.setValue(var);
								return true;
							}
							n = p;
						}
						return false;
					} else {
						var = resource;
					}
						token.setValue(var);
					break;
				case 3:// ?
					if (token.getToken().length() > resource.length()) {
						return false;
					}
					int i = 0;
					int len = token.getToken().length();
					char[] chars = new char[len];
					while (i < len) {
						char ch = resource.charAt(i);
						if (ch == '/') {
							return false;
						}
						chars[i++] = ch;
					}
						token.setValue(new String(chars));
						resource = resource.substring(token.getToken().length());
				}
			}
		}
		return true;
	}

	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public List<Token> getTokens() {
		return tokens;
	}
	
	
	public String getBuildExpress() {
		return buildExpress;
	}


	/**
	 * 
	 * @author yanan
	 *
	 */
	public static class Token implements Cloneable {
		private int index;
		private String token;
		private String name;
		private String value;
		private int type;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "Token [index=" + index + ", token=" + token + ", name=" + name + ", value=" + value + ", type="
					+ type + "]";
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public Token clone() throws CloneNotSupportedException {
			Token token = (Token) super.clone();
			return token;
		}
	}
	
	public static class Matcher {
		private List<Token> tokens;
		private Map<String,String> variables;
		private String regex;
		private String resource;
		private boolean result;
		
		public List<Token> getTokens() {
			return tokens;
		}

		public String getRegex() {
			return regex;
		}

		public String getResource() {
			return resource;
		}

		public boolean isMatch() {
			return result;
		}
		/**
		 * 将token转换成变量
		 */
		private void initVariable(){
			if(!isMatch())
				throw new RuntimeException("express \""+regex+"\" is not match path \""+resource+"\"");
			variables = new HashMap<String,String>();
			Iterator<Token> iterator = this.tokens.iterator();
			while(iterator.hasNext()){
				Token token = iterator.next();
				if(token.getType()>0 && StringUtil.isNotEmpty(token.getName()))
					this.variables.put(token.getName(), token.getValue());
			}
		}
		/**
		 * 获取变量的集合
		 * @return
		 */
		public  Set<Entry<String, String>> variableSet(){
			if(this.variables==null)
				this.initVariable();
			return this.variables.entrySet();
		}
		public Map<String, String> variableMap() {
			if(this.variables==null)
				this.initVariable();
			return this.variables;
		}
		/**
		 * 获取匹配到的变量
		 * @param varName
		 * @return
		 */
		public String getVariable(String varName){
			if(this.variables==null)
				this.initVariable();
			return this.variables.get(varName);
		}
		Matcher(String regex, String resource, List<Token> tokens, boolean result) {
			super();
			this.tokens = tokens;
			this.regex = regex;
			this.resource = resource;
			this.result = result;
		}
	}

	

	

}
