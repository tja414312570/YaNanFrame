package com.YaNan.frame.servlets.parameter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.servlets.ParameterHandlerCache;
import com.YaNan.frame.servlets.ServletBean;

/**
 * 参数处理接口
 * @author yanan
 *
 */
public interface ParameterHandler {
	/**
	 * 初始化参数处理，用于构建参数集合等基础数据
	 * @param request
	 * @param response
	 * @param servletBean
	 * @param parameterHandlerCache
	 */
	void initHandler(HttpServletRequest request,HttpServletResponse response,ServletBean servletBean, ParameterHandlerCache parameterHandlerCache);
	/**
	 * 获取参数
	 * @param parameter 参数
	 * @param parameterAnnotation 参数的注解
	 * @return
	 */
	Object getParameter(Parameter parameter, Annotation parameterAnnotation);
	/**
	 * 获取参数
	 * @param parameter
	 * @return
	 * @throws IOException
	 */
	Object getParameter(Parameter parameter) throws IOException;
	/**
	 * 获取参数
	 * @param field 字段
	 * @param parameterAnnotation 注解
	 * @return
	 */
	Object getParameter(Field field, Annotation parameterAnnotation);
	/**
	 * 获取参数
	 * @param field
	 * @return
	 * @throws IOException
	 */
	Object getParameter(Field field) throws IOException;
	/**
	 * 此处应该返回initHandler中的ServletBean
	 * @return
	 */
	ServletBean getServletBean();
	/**
	 * 此处应该返回initHandler中的ServletRequest
	 * @return
	 */
	HttpServletRequest getServletRequest();
	/**
	 * 此处应该返回initHandler中的ServletResponse
	 * @return
	 */
	HttpServletResponse getServletResponse();
}
