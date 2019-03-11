package com.YaNan.frame.RTDT.context;

import com.YaNan.frame.RTDT.WebSocketListener;
import com.YaNan.frame.RTDT.actionSupport.RTDTNotification;
import com.YaNan.frame.RTDT.entity.ActionEntity;
import com.YaNan.frame.RTDT.entity.Notification;
import com.YaNan.frame.RTDT.entity.NotifyEntity;
import com.YaNan.frame.RTDT.entity.RequestAction;
import com.YaNan.frame.RTDT.entity.ResponseAction;
import com.YaNan.frame.RTDT.entity.interfacer.ACTION_TYPE;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.parameter.annotations.RequestParam;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ActionDispatcher {
	public void doDipatcher(RequestAction request, WebSocketListener webSocketListener) throws Exception {
		ResponseAction responseAction = new ResponseAction();
		responseAction.setAUID(request.getAUID());
		responseAction.setType(4280);
		responseAction.setClient(webSocketListener);
		if (request.getType() == ACTION_TYPE.ACTION) {
			ActionManager manager = ActionManager.getActionManager();
			if (manager.hasAction(request.getAction())) {
				ActionEntity action = manager.getAction(request.getAction());
				request.setActionEntity(action);
				ClassLoader loader = new ClassLoader(PlugsFactory.getPlugsInstance(action.getCLASS()));
				if (loader.hasMethod("doContext", RequestAction.class,ResponseAction.class, ClassLoader.class ))
					loader.invokeMethod("doContext",request,responseAction, loader);
				Iterator<String> parametersIterator = request.getParametersKeyIterator();
				while (parametersIterator.hasNext()) {
					String parameterName = (String) parametersIterator.next();
					valuation(loader, parameterName, request.getParameter(parameterName));
				}
				invoke(action,request, responseAction, loader, webSocketListener);
			} else {
				responseAction.setStatus(4282);
				responseAction.setData("Could not found Action " + request.getAction() + "!");
				webSocketListener.write(responseAction);
			}
		} else {
				NotifyEntity notifyEntity = NotifyManager.getManager().getEntity(request.getAction());
				if(notifyEntity==null){
					responseAction.setStatus(4282);
					responseAction.setData("Could not found Notify " + request.getAction() + "!");
					webSocketListener.write(responseAction);
					return;
				}
				notifyEntity.setRequestAction(request);
				RTDTNotification notifyImp = (RTDTNotification) PlugsFactory.getPlugsInstance(NotifyManager.getManager().getEntity(request.getAction()).getCLASS()) ;
				ClassLoader loader = new ClassLoader(notifyImp);
				if (loader.hasMethod("doContext", RequestAction.class,ResponseAction.class, ClassLoader.class ))
					loader.invokeMethod("doContext",request,responseAction, loader);
				Notification notify = new Notification(webSocketListener, request, notifyEntity,notifyImp);
				notify.setAction(notifyEntity);
				notify.setToken(request.getToken());
				request.setNotification(notify);
				notifyImp.onBind(notify);
		}

	}

	private void valuation(ClassLoader loader, String field, Object value) throws Exception {
		if (loader.hasDeclaredField(field)) {
			Field f = loader.getDeclaredField(field);

			if (loader.hasMethod(ClassLoader.createFieldSetMethod(field), new Class[] { f.getType() }))
				loader.set(field, f.getType(), castType(value, f.getType()));
		} else {
			ClassLoader cLoader = new ClassLoader(loader.getLoadedClass().getSuperclass(), false);
			if (cLoader.hasDeclaredField(field)) {
				Field f = cLoader.getDeclaredField(field);

				if (loader.hasMethod(ClassLoader.createFieldSetMethod(field), new Class[] { f.getType() })) {
					loader.set(field, f.getType(), castType(value, f.getType()));
				}
			}
		}
	}
	/**
	 * 调用目标方法
	 * @param action
	 * @param request 
	 * @param response
	 * @param loader
	 * @param client
	 */
	private void invoke(ActionEntity action, RequestAction request, ResponseAction response, ClassLoader loader, WebSocketListener client) throws NoSuchMethodException {
		try {
			//get method
			Method method = action.getMethod();
			//获取方法中参数
			Parameter[] parameters = method.getParameters();
			Object[] object = new Object[parameters.length];
			int i = 0;
			Iterator<String> iterator = request.getParametersKeyIterator();
			//获取参数判断参数是否有注解@RequestParam()
			for(Parameter parameter : parameters){
				if(parameter.getType().equals(RequestAction.class)){
					object[i++] = request;
					continue;
				}
				if(parameter.getType().equals(ResponseAction.class)){
					object[i++] = response;
					continue;
				}
				RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
				if(requestParam!=null){
					object[i++] = ClassLoader.castType(request.getParameter(requestParam.value()) , parameter.getType());
				}else{
					if(iterator.hasNext()){
						object[i++] = ClassLoader.castType(request.getParameter(iterator.next()) , parameter.getType());
						iterator.remove();
					}else{
						object[i++] = ClassLoader.castType(null , parameter.getType());
					}
				}
			}
			method.setAccessible(true);
			Object callBack = action.getMethod().invoke(loader.getLoadedObject() , object);
			method.setAccessible(false);
			response.setStatus(4280);
			response.setData(callBack);
			response.write();
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			response.setStatus(4290);
			response.setData("Services Inner Error!");
			response.write();
		}
	}

	public static Object castType(Object orgin, Class<?> targetType) {
		if (orgin == null) {
			return null;
		}
		if (targetType.equals(Integer.TYPE))
			return Integer.valueOf(Integer.parseInt(orgin.toString().equals("") ? "0" : orgin.toString()));
		if (targetType.equals(Short.TYPE))
			return Short.valueOf(Short.parseShort((String) orgin));
		if (targetType.equals(Long.TYPE))
			return Long.valueOf(Long.parseLong((String) orgin));
		if (targetType.equals(Byte.TYPE)) {
			return Byte.valueOf(Byte.parseByte((String) orgin));
		}
		if (targetType.equals(Float.TYPE))
			return Float.valueOf(Float.parseFloat(orgin.toString()));
		if (targetType.equals(Double.TYPE)) {
			return Double.valueOf(Double.parseDouble((String) orgin));
		}
		if (targetType.equals(Date.class)) {
			try {
				return SimpleDateFormat.getInstance().parse(orgin.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (targetType.equals(Boolean.TYPE)) {
			return Boolean.valueOf(Boolean.parseBoolean((String) orgin));
		}
		if (targetType.equals(Character.TYPE))
			return Character.valueOf(((Character) orgin).charValue());
		if (targetType.equals(String.class)) {
			return orgin.toString();
		}
		return orgin;
	}
}
