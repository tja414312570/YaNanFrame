package com.YaNan.frame.plugin.autowired.plugin;


import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import com.YaNan.frame.plugin.FieldDesc;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.handler.MethodHandler;
/**
 * 注入栈信息记录
 * @author yanan
 *
 */
public class PluginWiredStack {
	/**
	 * 此服务的注入类型，
	 * METHOD_WRIDE：通过方法参数注入
	 * CONSTRUCTOR_WRIDE：通过构造器注入
	 * FIELD_WRIDE：通过属性注入
	 * @author yanan
	 */
	public static enum WiredType{
		METHOD_WRIDE,CONSTRUCTOR_WRIDE,FIELD_WRIDE
	}
	private static ThreadLocal<PluginWiredStack> stackLocal = new ThreadLocal<PluginWiredStack>();
	private Object target;
	private Object proxy;
	private FieldDesc fieldDesc;
	private Parameter parameter;
	private Class<?> plugClass;
	private Constructor<?> constructor;
	private RegisterDescription registerDescription;
	private MethodHandler methodHandler;
	private WiredType wrideType;
	public static PluginWiredStack getStack(){
		PluginWiredStack stack = stackLocal.get();
		if(stack==null){
			stack = new PluginWiredStack();
			stackLocal.set(stack);
		}
		return stack;
	}
	static void setWrideType(WiredType type){
		getStack().wrideType = type;
	}
	public Class<?> getPlugClass() {
		return plugClass;
	}
	public void setPlugClass(Class<?> plugClass) {
		this.plugClass = plugClass;
	}
	public Object getTarget() {
		return target;
	}
	public Object getProxy() {
		return proxy;
	}
	public FieldDesc getFieldDesc() {
		return fieldDesc;
	}
	public Parameter getParameter() {
		return parameter;
	}
	public Constructor<?> getConstructor() {
		return constructor;
	}
	public RegisterDescription getRegisterDescription() {
		return registerDescription;
	}
	public MethodHandler getMethodHandler() {
		return methodHandler;
	}
	public static void setMethodHandler(MethodHandler methodHandler){
		getStack().methodHandler = methodHandler;
		getStack().registerDescription = methodHandler.getPlugsProxy().getRegisterDescription();
		getStack().plugClass = methodHandler.getPlugsProxy().getInterfaceClass();
		getStack().proxy = methodHandler.getPlugsProxy().getProxyObject();
	}
	public static void setRegisterDescription(RegisterDescription registerDescription, Class<?> plugClass,
			Constructor<?> constructor) {
		getStack().registerDescription=registerDescription;
		getStack().plugClass=plugClass;
		getStack().constructor=constructor;
	}
	public static void setParameter(Parameter parameter) {
		getStack().parameter=parameter;
	}
	public static void setRegisterDescription(RegisterDescription registerDescription, Object proxy, Object target,
			FieldDesc fieldDesc) {
		getStack().registerDescription=registerDescription;
		getStack().proxy=proxy;
		getStack().target=target;
		getStack().fieldDesc=fieldDesc;
		
	}
	public WiredType getWrideType() {
		return wrideType;
	}
}
