package com.YaNan.Demo.rtdt;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.YaNan.Demo.model.Hibernate;
import com.YaNan.frame.Native.PackageScanner;
import com.YaNan.frame.Native.PackageScanner.ClassInter;
import com.YaNan.frame.RTDT.actionSupport.RTDTNotification;
import com.YaNan.frame.RTDT.actionSupport.TokenAction;
import com.YaNan.frame.RTDT.entity.Notification;
import com.YaNan.frame.RTDT.entity.annotations.RAction;
import com.YaNan.frame.RTDT.entity.annotations.RNotify;
import com.google.gson.Gson;
import com.YaNan.frame.core.reflect.ClassLoader;

@RNotify
public class MyRAction extends TokenAction implements RTDTNotification {
	public final static int DICT = 4280;
	public final static int RULE = 4281;
	public final static int ACTION = 4282;
	private String content="";//得到的内容
	private static Map<String,String> dict = new HashMap<String,String>();//词典库
	private static Map<String,List<String>> dictSet = new HashMap<String,List<String>>();//词典目录
	private static List<String> history = new ArrayList<String>();//询问历史
	private static Map<String,Integer> trouble = new LinkedHashMap<String,Integer>();//问题库
	private static String ask;//当前询问问题
	private static String process;//当前待处理的问题
	private static Map<String,String> rule = new HashMap<String,String>();//规则 匹配形式 规则名称  规则
	private static Map<String,List<String>> action = new HashMap<String,List<String>>();//规则动作 规则 动作
	private static boolean train = false;
	@Override
	public void onBind(Notification Notification) {
		Notification.Notify("your connection is success!");
		Notification.Notify("Your session id is "+this.TokenContext.getTokenId());
	}

	@Override
	public void unBind(Notification Notification) {
		
	}
	//训练模式
	public void train(){
		this.ResponseContext.write("get input "+this.content);
		this.ResponseContext.write("try to find method to resolve the qusition ");
		PackageScanner scanner = new PackageScanner();
		
		scanner.setClassPath(Hibernate.class.getClassLoader().getResource("").getPath().replace("%20"," "));
		scanner.doScanner(new ClassInter(){

			@Override
			public void find(Class<?> cls) {
				ResponseContext.write(cls.getName());
			
			}
		});
	}
	//学习模式
	public void study(){
		if(trouble.size()==0){//判断程序是否有未解决的问题
			process = this.content;
			if(!this.hasTrouble(this.content))
				this.process(process);
		}else{
			int type = trouble.get(ask);
			if(type == MyRAction.DICT){//问题类型为词典
				dict.put(ask, this.content);//将问题答案添加到词典
				if(dictSet.containsKey(this.content)){//词典集合中是否有该问题集合
					dictSet.get(this.content).add(ask);
				}else{
					List<String> tmp = new ArrayList<String>();
					tmp.add(ask);
					dictSet.put(this.content,tmp);
				}
			}else if(type==MyRAction.RULE){
				rule.put(ask, this.content);
			}else{
				if(!this.content.equals("completed:")){
					if(this.content.equals("continue:")){
						this.ResponseContext.write(" what is next step?");
						return;
					}
					if(action.get(ask)!=null){
						action.get(ask).add(this.content);
						this.ResponseContext.write(" ok,the step is "+this.content);
						this.ResponseContext.write(" what is next step?");
					}else{
						List<String> list = new ArrayList<String>();
						list.add(this.content);
						action.put(ask, list);
						this.ResponseContext.write(" ok,the step is "+this.content);
						this.ResponseContext.write(" what is next step?");
						}
					return;
				}
			}
			this.ResponseContext.write(" ok,"+ask+" is "+this.content);
			trouble.remove(ask);//从问题库移除问题
			ask=null;
			if(trouble.keySet().iterator().hasNext()){//判断是否还有其它待处理符号
				ask = trouble.keySet().iterator().next();//获取下一个不懂得符号
				this.ResponseContext.write("what is "+ask);//向用户提问
			}else this.process(process);
		}
	}
	@RAction
	public void accept(){
		history.add(this.content);//记录对话历史
		if(this.content.indexOf(":")!=-1){
			String cmd = this.content.substring(0,this.content.indexOf(":"));
			String args = this.content.substring(this.content.indexOf(":")+1);
			if(cmd.equals("process")){
				this.ResponseContext.write(process==null?"no any process":"process:"+process);
				this.ResponseContext.write(ask==null?"no any ask":"ask:"+ask);
				this.ResponseContext.write(trouble.size()==0?"no any trouble":"trouble:"+new Gson().toJson(trouble));
				return;
			}
			if(cmd.equals("clean")){
				content="";
				dict.clear();
				dictSet.clear();
				history.clear();
				trouble.clear();
				ask=null;
				process=null;
				rule.clear();
				action.clear();
				return;
			}
			if(cmd.equals("study")){
				train=false;
				this.ResponseContext.write("enable study model ");
				return;
			}
			if(cmd.equals("train")){
				train=true;
				this.ResponseContext.write("enable train model ");
				return;
			}
			if(cmd.equals("env")){
				this.ResponseContext.write(new Gson().toJson(System.getenv()));
				return;
			}
			if(cmd.equals("prop")){
				this.ResponseContext.write(new Gson().toJson(System.getProperties()));
				return;
			}
			if(cmd.equals("continue")){
				if(trouble.get(ask)!=MyRAction.ACTION){
					this.content = process;
					ask=null;
					process=null;
					trouble.clear();
				}
			}
		}
		if(train){
			this.train();
		}else{
			this.study();
		}
		
	}
	public boolean hasTrouble(String content){
		char[] cr = content.toCharArray();
		for(int i =0;i<cr.length;i++){//对内容进行划分
			String word = String.valueOf(cr[i]);//取得单个符号
			if(dict.get(word)!=null){//查看词典中是否有这个符号
				this.ResponseContext.write (word +" is "+dict.get(word));
			}else{
				dict.put(word, null);//将符号压入词典库
				trouble.put(word,MyRAction.DICT);//将符号压入问题库
			}
		}
		if(trouble.keySet().iterator().hasNext()){//如果有问题
			ask = trouble.keySet().iterator().next();//获取第一个不懂得符号
			this.ResponseContext.write("what is "+ask);//像用户提问
			return true;
		}else{
			return false;
		}
	}
	public boolean process(String content){
		this.ResponseContext.write ("match rule with "+content);
		String rul = getRule(content);//取得内容规则
		this.ResponseContext.write ("get the content rule "+rul);
		if(rule.get(rul)==null){
			this.ResponseContext.write ("Can't find rule "+rul+",what it is?");
			trouble.put(rul,MyRAction.RULE);//将符号压入问题库
			ask = trouble.keySet().iterator().next();//获取第一个不懂的问题
		}else{
			this.ResponseContext.write ("rule "+rul+" is "+rule.get(rul));
			this.ResponseContext.write ("so,your type content "+content+" is "+rule.get(rul));
			if(action.get(rul)==null){
				this.ResponseContext.write ("but i can't understand the rule "+content+",what can i do?");
				trouble.put(rul,MyRAction.ACTION);//将符号压入问题库
				ask = trouble.keySet().iterator().next();//获取第一个不懂的问题
			}else{
				this.ResponseContext.write ("get the way of the rule "+rule+"");
				List<String> steps = action.get(rul);
				this.ResponseContext.write ("the way all "+steps.size()+" steps,it's "+new Gson().toJson(steps));
				Iterator<String> iterator = steps.iterator();
				while(iterator.hasNext()){
					String step = iterator.next();
					if(step.equals("echo"))
						this.ResponseContext.write ("step "+step+" ,it's "+new Gson().toJson(content));
					ClassLoader loader = new ClassLoader(this);
					try {
						Object obj = loader.invokeMethod("getResponseContext");
						ClassLoader nLoader = new ClassLoader(obj);
						nLoader.invokeMethod("write", "hello "+this.process);
					} catch (SecurityException | IllegalArgumentException
							| IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
						e.printStackTrace();
					}
					
				}
					
			}
			
		}
		return true;
	}
	public static String getRule(String content){
		String rule="";
		char[] cr = content.toCharArray();
		for(int i =0;i<cr.length;i++){//对内容进行划分
			String word = String.valueOf(cr[i]);//取得单个符号
				String set = dict.get(word);
				if(rule.length()>1){
					//判断类型数量
					if(rule.lastIndexOf(set)!=-1){//判断规则中是否有当前类型
						if(rule.lastIndexOf(set)+set.length()==rule.length())
							rule+="*";
						else if(rule.substring(rule.lastIndexOf(set)+set.length()).equals("*"))
							continue;
						else rule+=","+set;
					}else{
						rule+=","+set;
					}
				}else
					rule+=set;
		}
		return rule;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content){
		this.content = content;
	}
}