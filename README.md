# YaNanFrame 2.0 - PLUGIN

           PLUGINPLUGIN     PLUG         PLUG       PLUG      PLUGINPLUGIN    PLUGINPLUGIN   PLUG         PLUG
          PLUG      PLUG   PLUG         PLUG       PLUG    PLUG                  PLUG       PLUGPLUG     PLUG
         PLUG      PLUG   PLUG         PLUG       PLUG   PLUG                   PLUG       PLUG PLUG    PLUG
        PLUG      PLUG   PLUG         PLUG       PLUG   PLUG                   PLUG       PLUG  PLUG   PLUG
       PLUGPLUGPLUGI    PLUG         PLUG       PLUG   PLUG      PLUGINP      PLUG       PLUG   PLUG  PLUG
      PLUG             PLUG         PLUG       PLUG   PLUG         PLUG      PLUG       PLUG    PLUG PLUG
     PLUG             PLUG         PLUG       PLUG     PLUG       PLUG      PLUG       PLUG     PLUGINPL
    PLUG             PLUGINPLUGI   PLUGINPLUGINP        PLUGINPLUGIN    PLUGINPLUGIN  PLUG         PLUG
## 介绍    
基于AOP的编程模式，自带mvc组件，持久层组件，基于AOP的设计模式，用接口规范您的代码，实现团队解耦。用JAVA Bean来做数据库对象，让您无需知道sql就可以实现数据库的CURD，支持子查询，联表查询。将项目的各种模块声明为Service，通过PluginFactory来管理Service的创建，装配，注入。

更新日志：
	20181218：1.修复Delete传入参数为Class的bug，2.支持无状态Token的获取，支持方法的权限认证（对象由Plugin代理）。3.支持hocon配置，用hocon配置代替prop文件作为配置，新增优化的Config库。实验性功能：支持方法参数加密，修改plugin的异常处理的bug，新增Bean的支持。
	future:完善方法参数加密，完善bean支持。


## [介绍](https://github.com/tja414312570/YaNanFrame/wiki/home)

## 开始使用

* [1.0 安装](https://github.com/tja414312570/YaNanFrame/wiki/)
* [2.0 HelloWorld](https://github.com/tja414312570/YaNanFrame/wiki/)

## API

* [MVC组件](https://github.com/tja414312570/YaNanFrame/wiki/servlets)
    * [路径映射](https://github.com/tja414312570/YaNanFrame/wiki/servletsMapping)
    * [参数获取](https://github.com/tja414312570/YaNanFrame/wiki/servletsParameter)
    * [数据响应](https://github.com/tja414312570/YaNanFrame/wiki/servletsResponse)
    * [参数验证](https://github.com/tja414312570/YaNanFrame/wiki/servletsVailed)
    * [扩展](https://github.com/tja414312570/YaNanFrame/wiki/servletsExpand)
* [Token令牌](https://github.com/tja414312570/YaNanFrame/wiki/token)
    * [路径拦截](https://github.com/tja414312570/YaNanFrame/wiki/tokenUrl)
    * [接口拦截](https://github.com/tja414312570/YaNanFrame/wiki/tokenServlets)
    * [无状态Token](https://github.com/tja414312570/YaNanFrame/wiki/noStatusToken)
    * [持久化接口](https://github.com/tja414312570/YaNanFrame/wiki/tokenHibernate)
    * [扩展](https://github.com/tja414312570/YaNanFrame/wiki/tokenExpand)
* [数据库支持Hibernate](https://github.com/tja414312570/YaNanFrame/wiki/hibernate)
    * [初始化持久层](https://github.com/tja414312570/YaNanFrame/wiki/hibernateInit)
    * [数据源配置](https://github.com/tja414312570/YaNanFrame/wiki/dataSource)
    * [对象编程](https://github.com/tja414312570/YaNanFrame/wiki/objectHibernate)
    * [对象编程可用注解](https://github.com/tja414312570/YaNanFrame/wiki/hibernateAnnotations)
    * [Mapper文件模式](https://github.com/tja414312570/YaNanFrame/wiki/hibernaterMapper)
* [组件化编程Plugin](https://github.com/tja414312570/YaNanFrame/wiki/plugin)
    * [初始化组件上下文](https://github.com/tja414312570/YaNanFrame/wiki/plug_init)
    * [定义服务组件](https://github.com/tja414312570/YaNanFrame/wiki/plug_service)
    * [定义服务实体](https://github.com/tja414312570/YaNanFrame/wiki/plug_register)
    * [Plugin.conf文件配置](https://github.com/tja414312570/YaNanFrame/wiki/plug_conf)
    * [注入bean的方式](https://github.com/tja414312570/YaNanFrame/wiki/plug_wired)
* [RTDT即时数据传输](https://github.com/tja414312570/YaNanFrame/wiki/RTDT)
    * [初始化组件上下文](https://github.com/tja414312570/YaNanFrame/wiki/RTDT_init)
    * [创建一个Action](https://github.com/tja414312570/YaNanFrame/wiki/RTDT_Action)
    * [发起一个通知](https://github.com/tja414312570/YaNanFrame/wiki/RTDT_Notify)
* [切面编程](https://github.com/tja414312570/YaNanFrame/wiki/aop)
    * [构造器拦截器InstanceHandler](https://github.com/tja414312570/YaNanFrame/wiki/InstanceHandler)
    * [Field初始化FieldHandler](https://github.com/tja414312570/YaNanFrame/wiki/FieldHandler)
    * [方法拦截器InvokeHandler](https://github.com/tja414312570/YaNanFrame/wiki/InvokeHandler)
* [Hocon配置文件](https://github.com/tja414312570/YaNanFrame/wiki/)
## 工具类
* [config](https://github.com/tja414312570/YaNanFrame/wiki/config)
* [XMLHelper](https://github.com/tja414312570/YaNanFrame/wiki/xmlHelper)
    * [更多使用](https://github.com/tja414312570/YaNanFrame/wiki/xmlHelperMore)
    * [默认支持的注解](https://github.com/tja414312570/YaNanFrame/wiki/xmlHelperDefault)
    * [扩展支持的类型、注解](https://github.com/tja414312570/YaNanFrame/wiki/xmlHelperExpand)
* [ant路径匹配](https://github.com/tja414312570/YaNanFrame/wiki/antPath)
* [ClassHelper](https://github.com/tja414312570/YaNanFrame/wiki/ClassHelper)
* [ClassLoader](https://github.com/tja414312570/YaNanFrame/wiki/ClassLoader)
## 其它组件
* [Corn表达式组件](https://github.com/tja414312570/YaNanFrame/wiki/corn)
## Information

* [Change Log](https://github.com/tja414312570/YaNanFrame/wiki/)

实例
1、配置文件配置bean，框架调用
配置文件：
```
###################### plugin list
plugins:[
		com.YaNan.frame.plugin.autowired.plugin.PluginWiredHandler, ##服务注入提供
		com.YaNan.frame.plugin.autowired.resource.ResourceWiredHandler, ##资源类注入
		com.YaNan.frame.plugin.autowired.property.PropertyWiredHandler, ##属性注入
		com.YaNan.frame.plugin.autowired.exception.ErrorPlugsHandler, ##错误记录
		##	 com.YaNan.frame.util.quartz.QuartzManager,##Quartz corn注解服务
		com.YaNan.frame.servlets.validator.ParameterValitationRegister, ##参数验证拦截器
		com.YaNan.frame.servlets.validator.DefaultParameterValidator, ##默认jsr 303 参数验证器
		com.YaNan.frame.servlets.session.plugin.TokenHandler, ##无状态Token拦截器
		com.YaNan.frame.servlets.session.parameter.TokenParameterHandler, ##Token参数注入 @TokenAttribute
		com.YaNan.frame.RTDT.context.init.RTDTContextInit, ##RTDT功能 @RAction @RNotify
		com.YaNan.frame.plugin.autowired.exception.ErrorPlugsHandler, ##异常捕获 @Error
		com.YaNan.frame.plugin.autowired.plugin.PluginWiredHandler, ##组件注入服务 @Service
		com.YaNan.frame.plugin.autowired.property.PropertyWiredHandler, ##属性注入服务 @Property
		com.YaNan.frame.plugin.autowired.resource.ResourceWiredHandler, ##资源注入服务 @Resource
		{ ##Restful调配器 @RequestMapping @GetMapping @PostMapping @DeleteMapping @RequestParam @RequestPath
			class:com.YaNan.frame.servlets.RestfulDispatcher,
			priority:0,
			signlton:true,
			attribute=RESTFUL_STYLE,
			register="javax.servlet.Servlet,*"
		},
		{ ## 持久化框架
			class:com.YaNan.frame.hibernate.database.HibernateContextInit,##组件类
			priority:0,##优先级
			signlton:true,##是否启用单例模式
			attribute:"DB",
			service:"javax.servlet.Servlet,*",##注册服务类
			field:{location:{Resource:"classpath:hibernate.xml"}}
		},
		{ ## jedis
			id:jedis,
			class:redis.clients.jedis.Jedis,##组件类
			##signlton:true,##bean配置强制单例模式
			init:connect,##实例化后调用connect方法
			args:[localhost,6379]##使用String,int的构造器
		},
		com.YaNan.frame.logging.Log,##日志接口
		com.YaNan.frame.logging.DefaultLog,##日志接口和默认实现
	]
####################### global configure
conf:{
	Plugin:{
		ScanPackage:[com.YaNan], ##扫描包位置，多个包用逗号分开或使用数组ScanPackage:[com.YaNan.DZCT,com.YaNan.debug]
													  ##可以写为ScanPackage:"com.YaNan.DZCT,com.YaNan.debug"
	},
	SecurityFilter:{
		x-frame-options:true,
		xss-wrapper:{"/**":true}
		},
	Token:{
		Timeout:4000,
		Secure:false,
		HttpOnly:true,
	}
}
```
代码
```java
	public class JedisTest {
		@Service(id="jedis")
		private Jedis jediswired;
		public static void jedisTest(){
			Jedis jedis = PlugsFactory.getBean("jedis");
			jedis.append("key1", "value1");
			System.out.println(jedis.get("key1"));

			JedisTest test = PlugsFactory.getPlugsInstance(JedisTest.class);
			System.out.println(test.jediswired.get("key1"));

			System.out.println(test.jediswired == jedis);
		}
	}
	
```
结果
```
value1
value1
true
```
2、方法调用权限认证与无状态Token
java:
```java
public static void main(String[] args) {
		Token token = Token.getToken();
		token.addRole("root");
		Crypt c = PlugsFactory.getPlugsInstance(Crypt.class);
		c.out("输出权限有了");
		
	}
	@Authentication(roles="root")
	public static class Crypt{
		@Authentication(roles="out")
		public void out(String out){
			System.out.println(out.toString());
		}
	}
```
结果:
```
初始化后调用方法
testFacotory
2018-12-28 11:15:19 Error: com.YaNan.frame.plugin.handler.PlugsHandler:
java.lang.RuntimeException: No permission to invoke method:public void com.a.encrypt.testCrypt$Crypt.out(java.lang.String)
	at com.YaNan.frame.servlets.session.plugin.TokenHandler.before(TokenHandler.java:63)
	at com.YaNan.frame.plugin.handler.PlugsHandler.intercept(PlugsHandler.java:226)
	at com.a.encrypt.testCrypt$Crypt$$EnhancerByCGLIB$$857033ca.out(<generated>)
	at com.a.encrypt.testCrypt.main(testCrypt.java:14)
Exception in thread "main" java.lang.RuntimeException: No permission to invoke method:public void com.a.encrypt.testCrypt$Crypt.out(java.lang.String)
	at com.YaNan.frame.servlets.session.plugin.TokenHandler.before(TokenHandler.java:63)
	at com.YaNan.frame.plugin.handler.PlugsHandler.intercept(PlugsHandler.java:226)
	at com.a.encrypt.testCrypt$Crypt$$EnhancerByCGLIB$$857033ca.out(<generated>)
	at com.a.encrypt.testCrypt.main(testCrypt.java:14)

```
项目结构：通用父级命名空间<br>
  1、hibernate：持久层，用于数据库的CRUD以及事物<br>
  2、logging:日志,框架的日志记录<br>
  3、plugin：项目核心组件，其它组件的依赖，实现aop，di，扩展了自动注入，支持方法拦截，自动管理接口，自动装配服务，上下文初始化<br>
  4、reflect：反射，用于提供框架所需的反射部分的实现<br>
  5、servlet：servlet组件，提供action、restful等接口的实现，支持可扩展的验证，参数，回调处理器<br>
  6、servlet.session:web开发的回话管理，权限验证，数据存储等<br>
  7、util：基础工具类，提供一些框架常用的工具<br>
  8，web.sercurity：提供XSS请求的包裹，防止JS，SQL等注入< BR >

＃核心组件  plugin
各组件之间的依赖可以通过最简单的组件实现绑定，也可以通过简单的Property文件进行注册

使用案例:
<br>
（一）、Plugin
1、接口：OSS服务接口，提供服务的抽象方法
```java
package com.BaTu.service.oss;

import java.util.Map;

public interface OssService {
	 public Map<String, String> createUploadEndpoint(String path) throws Exception;

	public String getContextHost();
}

```
2、实现：接口的具体实现，注册到组件容器，提供OSS接口服务的具体功能
```java
package com.BaTu.service.oss;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.autowired.property.Property;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;

@Register
public class DefaultOssService implements OssService{
	@Property("oss.api.endpoint")
	private String endpoint;
	@Property("oss.api.accessId")
    private String accessId;
	@Property("oss.api.accessKey")
    private String accessKey;
	@Property("oss.api.bucket")
    private String bucket;
	@Property("oss.api.user_dir")
    private String user_dir;
	@Property("oss.api.conf_dir")
    private String roolsDir;
	@Property("oss.api.scheme")
	private String scheme;
	@Property("oss.api.expire_time")
	private long expireTime;
	@Override
    public Map<String, String> createUploadEndpoint(String path) throws UnsupportedEncodingException{
    	 String host = getContextHost();
         OSSClient client = new OSSClient(endpoint, accessId, accessKey);
     	long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
         Date expiration = new Date(expireEndTime);
         PolicyConditions policyConds = new PolicyConditions();
         policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
         policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, path);
         String postPolicy = client.generatePostPolicy(expiration, policyConds);
         byte[] binaryData = postPolicy.getBytes("utf-8");
         String encodedPolicy = BinaryUtil.toBase64String(binaryData);
         String postSignature = client.calculatePostSignature(postPolicy);
         Map<String, String> respMap = new LinkedHashMap<String, String>();
         respMap.put("accessid", accessId);
         respMap.put("policy", encodedPolicy);
         respMap.put("signature", postSignature);
         respMap.put("dir",path);
         respMap.put("host", host);
         respMap.put("endpoint",endpoint);
         respMap.put("bucket",bucket);
         respMap.put("expire", String.valueOf(expireEndTime / 1000));
         return respMap;
    }
	public String UploadStream(final String path,final String fileName,InputStream is){
		OSSClient ossClient = new OSSClient(endpoint, accessId, accessKey);
		ossClient.putObject(bucket,path, new ByteArrayInputStream(new byte[0]));
		ossClient.putObject(bucket, path+fileName, is);
		ossClient.shutdown();
		return getContextHost()+path+fileName;
	}
	@Override
	public String getContextHost() {
		return scheme + bucket + "." + endpoint;
	}
}

```
3、消费：使用OSS服务

```java
package com.BaTu.control.source;

import javax.validation.constraints.NotNull;

import com.BaTu.model.user.WXUserModel;
import com.BaTu.plugs.servlet.response.AppResponse;
import com.BaTu.service.oss.OssService;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.autowired.exception.Error;
import com.YaNan.frame.plugin.autowired.property.Property;
import com.YaNan.frame.servlets.annotations.GetMapping;
import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.session.parameter.TokenAttribute;
import com.YaNan.frame.util.StringUtil;
@Error("{\"ret\":5,\"msg\":\"服务器异常\"}")
@RequestMapping("/app/oss")
public class AppOSSControler {
	@Service
	private OssService ossService;
	@Property("oss.dir.image.auth")
	private String authOssDir;
	@GetMapping("/auth")
	public String useAuthOssToken(@NotNull(message="{\"ret\":5,\"msg\":\"请先登录\"}")@TokenAttribute WXUserModel user) throws Exception{
		return AppResponse.success(ossService.createUploadEndpoint(StringUtil.decodeBaseVar(authOssDir, user.getUserID())));
	}
}

```
（二）、高度可扩展的Restful Servlet组件
```java
package com.BaTu.control.Instrument;

import javax.validation.groups.Default;

import com.BaTu.model.Instrument.InstrumentTypeModel;
import com.BaTu.plugs.servlet.response.DataTables;
import com.YaNan.frame.hibernate.database.Insert;
import com.YaNan.frame.hibernate.database.Query;
import com.YaNan.frame.hibernate.database.Update;
import com.YaNan.frame.hibernate.database.DBInterface.LOGIC_STATUS;
import com.YaNan.frame.servlets.annotations.DeleteMapping;
import com.YaNan.frame.servlets.annotations.GetMapping;
import com.YaNan.frame.servlets.annotations.Groups;
import com.YaNan.frame.servlets.annotations.PostMapping;
import com.YaNan.frame.servlets.annotations.PutMapping;
import com.YaNan.frame.servlets.annotations.RequestMapping;
import com.YaNan.frame.servlets.response.annotations.ResponseJson;
import com.YaNan.frame.servlets.session.annotation.Authentication;

@Authentication(roles="root",message="{\"error\":\"登录超时，请刷新页面\"}")
@RequestMapping("/Instrument/Type")
public class InstrumentTypeControler {
	@GetMapping
	@ResponseJson
	public DataTables getInstrumentType(){
		Query query = new Query(InstrumentTypeModel.class);
		return DataTables.success(query.query());
	}
	@Groups(Default.class)
	@PostMapping
	@ResponseJson
	public DataTables createInstrumentType(InstrumentTypeModel ins){
		ins.setStatus(LOGIC_STATUS.NORMAL);
		Insert insert =  new Insert(ins);
		if(insert.insert()){
			Query q = new Query(InstrumentTypeModel.class);
			q.addCondition("name", ins.getName());
			ins = q.queryOne();
			if(ins!=null)
				return DataTables.success(ins);
		}
		return DataTables.error("分类添加失败，可能是因为分类名已经存在");
	}
	@ResponseJson
	@PutMapping
	public DataTables updateInstrumentType(InstrumentTypeModel ins){
		Update update = new Update(ins);
		update.addColumnCondition("id", ins.getId());
		update.removeColumn("id");
		if (update.update() > 0)
			return DataTables.success(ins);
		return DataTables.error("编辑失败");
	}
	@ResponseJson
	@DeleteMapping
	public DataTables deleteInstrumentType(int ID){
		Update update = new Update(InstrumentTypeModel.class,"status");
		update.setColumn("status", LOGIC_STATUS.REMOVE);
		update.addCondition("id", ID);
		return update.update()>0?DataTables.success(null):DataTables.error("编辑失败");
	}
}

```
（三）、方法级别的加密服务
```java
package com.BaTu.control.account;


import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.autowired.exception.Error;
import com.YaNan.frame.plugin.security.Encrypt;
import com.YaNan.frame.plugin.security.EncryptService;
import com.YaNan.frame.servlets.session.annotation.Authentication;
import com.a.encrypt.EncryptUtil;
@Register
public class Main implements Say{
	@Service
	private Say main;
	public static void main(String[] args) throws Exception {
		//测试方法级别加密
		Say say = PlugsFactory.getPlugsInstance(Say.class);
		EncryptService service = PlugsFactory.getPlugsInstance(EncryptService.class);
		String message = "hello java";
		String cryptmsg = service.encrypt(message).toString();
		System.out.println(cryptmsg);
		
		String result = say.say(cryptmsg);
		long c = 10000000;
		long t = System.currentTimeMillis();
		System.out.println("执行"+c+"次耗时："+(System.currentTimeMillis()-t)/1000+"s"+",平均tps:"+(c/((System.currentTimeMillis()-t)/1000)));
		System.out.println("原始返回数据："+result);
		System.out.println(service.descrypt(result));
			
	}
	/**
	 * 调用某个接口用于加密或解密  实现方法级别数据加密传输
	 */
	@Encrypt(interfacer = EncryptUtil.class)
	public String say(String content){
		return "获得内容:"+content.toString();
	}
}

interface Say{
	@Error("出现错误")
	String say(String content);
}

```
```java
aGVsbG8gamF2YQ==
执行10000000次耗时：15s,平均tps:666666
原始返回数据：6I635b6X5YaF5a65OmhlbGxvIGphdmE=
获得内容:hello java

```
