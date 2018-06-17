package com.YaNan.frame.servlets.validator;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.List;

import javax.validation.Constraint;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.parameter.ParameterHandler;
/**
 * jsr 303 标准验证拦截
 * @author yanan
 *
 */
@Register(attribute="com.YaNan.frame.servlets.parameter.ParameterHandler.getParameter")
public class ParameterValitationRegister implements InvokeHandler{
	@Override
	public Object before(MethodHandler methodHandler) {
		methodHandler.chain();
		return null;
	}

	@Override
	public Object after(MethodHandler methodHandler) {
		//获取ParamterHandler对象
		ParameterHandler parameterHandler= methodHandler.getPlugsProxy().getProxyObject();
		//获得ServletBean
		ServletBean servletBean = parameterHandler.getServletBean();
		//getParamter获取方法中的参数
		Object[] params = methodHandler.getParameters();
		List<Annotation> jsrAnnoList = null;
		//第一个参数为Field或Parameter类型，获取其含有Constraint注解的集合（验证注解）
		if(params[0].getClass().equals(Field.class))
			jsrAnnoList=PlugsFactory.getAnnotationGroup((Field) params[0], Constraint.class);
		else
			jsrAnnoList=servletBean.getParameterAnnotation((Parameter) params[0], Constraint.class);
		//jsrAnnoList = PlugsFactory.getAnnotationGroup(params[0], Constraint.class);
		//依次判断注解//需要保证jsrAnnoList不为空
		if(jsrAnnoList!=null)
		for(Annotation anno : jsrAnnoList){
			ParameterValidator parameterValidator = PlugsFactory.getPlugsInstanceByAttributeStrict(ParameterValidator.class, anno.annotationType().getName());
			ConstraintViolation<?> valitation = parameterValidator.validate(params[0],anno,methodHandler.getOriginResult(),Default.class);
			if(valitation!=null){
				try {
					//如果出现错误   则直接返回错误信息
					if(!parameterHandler.getServletResponse().isCommitted()){
						parameterHandler.getServletResponse().setContentType("text/html;charset=UTF-8");
						parameterHandler.getServletResponse().setStatus(400);
						parameterHandler.getServletResponse().getWriter().write(valitation.getMessage());
						parameterHandler.getServletResponse().getWriter().close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return methodHandler.getOriginResult();
			}
		}
		methodHandler.chain();
		return null;
	}

	@Override
	public Object error(MethodHandler methodHandler, Exception e) {
		return null;
	}

}
