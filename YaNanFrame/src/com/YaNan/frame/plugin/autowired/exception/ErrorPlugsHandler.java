package com.YaNan.frame.plugin.autowired.exception;

import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.reflect.ClassLoader;

@Register(attribute="*",priority=Integer.MAX_VALUE)
public class ErrorPlugsHandler implements InvokeHandler{
	private static Log log = new DefaultLog(ErrorPlugsHandler.class);
	@Override
	public Object before(MethodHandler methodHandler) {
		methodHandler.chain();
		return null;
	}

	@Override
	public Object after(MethodHandler methodHandler) {
		methodHandler.chain();
		return null;
	}

	@Override
	public Object error(MethodHandler methodHandler, Exception e) {
		Error error = methodHandler.getMethod().getAnnotation(Error.class);
		if(error!=null&&(ClassLoader.implementOf(e.getClass(), error.exception())
				||ClassLoader.extendsOf(e.getClass(), error.exception()))){
			if(error.recorder()){
				StringBuilder sb = new StringBuilder();
				if(methodHandler.getParameters()!=null&&methodHandler.getParameters().length>0){
					for(Object par: methodHandler.getParameters()){
						sb.append("[").append(par).append("]  ");
					}
				}else sb.append("Void");
				log.error("An error occurred  \r\nat method :"
			+methodHandler.getMethod()+"\r\nparameter :"
						+sb.toString(), e);;
			}
			return error.value();
		}
		methodHandler.chain();
		return null;
	}

}
