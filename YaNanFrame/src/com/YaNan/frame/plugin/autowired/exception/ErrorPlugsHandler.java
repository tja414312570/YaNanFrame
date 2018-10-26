package com.YaNan.frame.plugin.autowired.exception;


import com.YaNan.frame.logging.DefaultLog;
import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.plugin.annotations.Support;
import com.YaNan.frame.plugin.handler.InvokeHandler;
import com.YaNan.frame.plugin.handler.MethodHandler;
import com.YaNan.frame.reflect.ClassLoader;

@Support(Error.class)
@Register(attribute="*",priority=Integer.MAX_VALUE)
public class ErrorPlugsHandler implements InvokeHandler{
	private static Log log = new DefaultLog(ErrorPlugsHandler.class);
	@Override
	public void before(MethodHandler methodHandler) {
	}

	@Override
	public void after(MethodHandler methodHandler) {
	}

	@Override
	public void error(MethodHandler methodHandler, Throwable e) {
		Error error = methodHandler.getInvokeHandlerSet().getAnnotation(Error.class);
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
			if(error.value()!="")
				methodHandler.interrupt(error.value());
		}
		e.printStackTrace();
	}
}
