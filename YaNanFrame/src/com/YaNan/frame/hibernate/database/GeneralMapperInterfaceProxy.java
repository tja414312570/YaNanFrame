package com.YaNan.frame.hibernate.database;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.hibernate.database.SqlSession;
import com.YaNan.frame.hibernate.database.annotation.Sql;
import com.YaNan.frame.hibernate.database.entity.BaseMapping;
import com.YaNan.frame.plugin.Plug;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.plugin.interfacer.PlugsListener;

/**
 * 通用Sql映射接口调用实现
 * @author yanan
 *
 */
@Support(Sql.class)
@Register(priority=1)
public class GeneralMapperInterfaceProxy implements PlugsListener,InvokeHandler{
	@Override
	public void excute(PlugsFactory plugsFactory) {
		//从组件工厂获取所有的组件，就不必要重新扫描整个类了
		Map<Class<?>, Plug> plugs = plugsFactory.getAllPlugs();
		for(Plug plug : plugs.values()){
			// 查找具有Sql注解的接口
			if(plug.getDescription().getPlugClass().getAnnotation(Sql.class)!=null){
				//获取一个注册器实例
				RegisterDescription register = new RegisterDescription(GeneralMapperInterfaceProxy.class);
				//设置默认实例为此实例的目标对象的本类实现
				Object t =PlugsFactory.getPlugsInstance(GeneralMapperInterfaceProxy.class);
				//创建一个此注册器的代理容器
				register.createProxyContainer();
				//对接口方法进行代理，代理对象为本身，目的是为了拦截方法的执行
				for(Method method : plug.getDescription().getPlugClass().getMethods()){
					register.addMethodHandler(method, this);
				}
				//生成代理容器中实例的key
				int hash = RegisterDescription.hash(plug.getDescription().getPlugClass());
				//将代理对象保存到代理容器，则在调用接口的实例实际访问到自己类，其实就是为了给接口一个实例，具体有没有实现其接口并不关心
				register.getProxyContainer().put(hash, t);
				//将生成的注册描述添加到接口组件
				plug.addRegister(register);
			}
		}
	}
	/**
	 * sql的接口进行拦截，才能知道具体调用了接口哪个方法
	 * 对接口方法进行分析，调用具体的sql语句，然后执行sql，什么orm之类的返回出去
	 */
	public void before(MethodHandler methodHandler) {
		//获取SqlSession
		SqlSession sqlSession = PlugsFactory.getPlugsInstance(SqlSession.class);
		//获取类名和方法并组装为sqlId
		String clzz = methodHandler.getPlugsProxy().getInterfaceClass().getName();
		String method = methodHandler.getMethod().getName();
		String sqlId=clzz+"."+method;
		//从映射中获取sqlId对应的映射，并通过映射获取SQL的类型，对应增删查改
		BaseMapping mapping = DBFactory.getDBFactory().getWrapMap(sqlId);
		if(mapping==null)
			throw new RuntimeException("could not found sql wrapper id \""+method+"\" at namespace \""+clzz+"\"");
		if(mapping.getNode().trim().toLowerCase().equals("select")){
			if(com.YaNan.frame.reflect.ClassLoader.implementsOf(methodHandler.getMethod().getReturnType(), List.class)){
				methodHandler.interrupt(sqlSession.selectList(sqlId, methodHandler.getParameters()));
			}else{
				methodHandler.interrupt(sqlSession.selectOne(sqlId, methodHandler.getParameters()));
			}
		}else if(mapping.getNode().trim().toLowerCase().equals("insert")){
			methodHandler.interrupt(sqlSession.insert(sqlId, methodHandler.getParameters()));
		}else if(mapping.getNode().trim().toLowerCase().equals("updaete")){
			methodHandler.interrupt(sqlSession.update(sqlId, methodHandler.getParameters()));
		}else if(mapping.getNode().trim().toLowerCase().equals("delete")){
			methodHandler.interrupt(sqlSession.delete(sqlId, methodHandler.getParameters()));
		}
	}

	@Override
	public void after(MethodHandler methodHandler) {
	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
		// TODO Auto-generated method stub
		
	}

}
