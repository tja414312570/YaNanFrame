package com.YaNan.frame.RTDT.context;

import com.YaNan.frame.RTDT.requestListener;
import com.YaNan.frame.RTDT.actionSupport.RTDTNotification;
import com.YaNan.frame.RTDT.entity.ActionEntity;
import com.YaNan.frame.RTDT.entity.Notification;
import com.YaNan.frame.RTDT.entity.NotifyEntity;
import com.YaNan.frame.RTDT.entity.RequestAction;
import com.YaNan.frame.RTDT.entity.ResponseAction;
import com.YaNan.frame.RTDT.entity.interfacer.ACTION_TYPE;
import com.YaNan.frame.core.reflect.ClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Iterator;

public class ActionDispatcher {
	public void doDipatcher(RequestAction request, requestListener client) throws Exception {
		ResponseAction responseAction = new ResponseAction();
		responseAction.setAUID(request.getAUID());
		responseAction.setType(4280);
		responseAction.setClient(client);
		if (request.getType() == ACTION_TYPE.ACTION) {
			ActionManager manager = ActionManager.getActionManager();
			if (manager.hasAction(request.getAction())) {
				ActionEntity action = manager.getAction(request.getAction());
				request.setActionEntity(action);
				ClassLoader loader = new ClassLoader(action.getCLASS());
				if (loader.hasMethod("doContext", RequestAction.class,ResponseAction.class, ClassLoader.class ))
					loader.invokeMethod("doContext",request,responseAction, loader);
				Iterator<String> parametersIterator = request.getParametersKeyIterator();
				while (parametersIterator.hasNext()) {
					String parameterName = (String) parametersIterator.next();
					valuation(loader, parameterName, request.getParameter(parameterName));
				}
				invoke(action, responseAction, loader, client);
			} else {
				responseAction.setStatus(4282);
				responseAction.setData("RTDT action " + request.getAction() + " is not exists!");
				client.write(responseAction);
			}
		} else {
				NotifyEntity notifyEntity = NotifyManager.getManager().getEntity(request.getAction());
				if(notifyEntity==null){
					responseAction.setStatus(4282);
					responseAction.setData("RTDT notify " + request.getAction() + " is not exists!");
					client.write(responseAction);
					return;
				}
				notifyEntity.setRequestAction(request);
				RTDTNotification notifyImp = (RTDTNotification) NotifyManager.getManager().getEntity(request.getAction()).getCLASS().newInstance();
				ClassLoader loader = new ClassLoader(notifyImp);
				if (loader.hasMethod("doContext", RequestAction.class,ResponseAction.class, ClassLoader.class ))
					loader.invokeMethod("doContext",request,responseAction, loader);
				Notification notify = new Notification(client, request, notifyEntity,notifyImp);
				notify.setAction(notifyEntity);
				notify.setToken(request.getToken());
				request.setNotification(notify);
				notifyImp.onBind(notify);
				if (notify.isBind()) {
					NotifyManager.getManager().bindNotify(notify);
					responseAction.setStatus(4270);
					responseAction.setType(4281);
					responseAction.setData("Notify bind success!");
					responseAction.write();
				} else {
					responseAction.setStatus(4271);
					responseAction.setType(4281);
					responseAction.setData(request.getNotification().getReason());
					responseAction.write();
				}
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

	private void invoke(ActionEntity action, ResponseAction response, ClassLoader loader, requestListener client) {
		try {
			if (loader.hasMethod(action.getMethod().getName())) {
				Object callBack = loader.invokeMethod(action.getMethod().getName());
				response.setStatus(4280);
				response.setData(callBack != null ? callBack.toString() : "");
				response.write();
			} else {
				response.setStatus(4283);
				response.setData("RTDT action " + action.getName() + " method exception");
				response.write();
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			response.setStatus(4290);
			response.setData("RTDT Services Inner Error!");
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
			return new Date(orgin.toString());
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
