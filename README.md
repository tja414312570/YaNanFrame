# YaNanFrame 2.0 
# 全新PLUGIN组件，带给您高效的开发效率

           PLUGINPLUGIN     PLUG         PLUG       PLUG      PLUGINPLUGIN    PLUGINPLUGIN   PLUG         PLUG
          PLUG      PLUG   PLUG         PLUG       PLUG    PLUG                  PLUG       PLUGPLUG     PLUG
         PLUG      PLUG   PLUG         PLUG       PLUG   PLUG                   PLUG       PLUG PLUG    PLUG
        PLUG      PLUG   PLUG         PLUG       PLUG   PLUG                   PLUG       PLUG  PLUG   PLUG
       PLUGPLUGPLUGI    PLUG         PLUG       PLUG   PLUG      PLUGINP      PLUG       PLUG   PLUG  PLUG
      PLUG             PLUG         PLUG       PLUG   PLUG         PLUG      PLUG       PLUG    PLUG PLUG
     PLUG             PLUG         PLUG       PLUG     PLUG       PLUG      PLUG       PLUG     PLUGINPL
    PLUG             PLUGINPLUGI   PLUGINPLUGINP        PLUGINPLUGIN    PLUGINPLUGIN  PLUG         PLUG
    
基于AOP的编程模式，自带mvc组件，持久层组件，基于AOP的设计模式，用接口规范您的代码，实现团队解耦。用JAVA Bean来做数据库对象，让您无需知道sql就可以实现数据库的CURD，支持子查询，联表查询。将项目的各种模块声明为Service，通过PluginFactory来管理Service的创建，装配，注入。

项目结构：通用父级命名空间,com.YaNan.frame<br>
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
