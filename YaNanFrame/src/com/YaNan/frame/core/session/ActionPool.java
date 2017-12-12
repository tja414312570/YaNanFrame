package com.YaNan.frame.core.session;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.YaNan.frame.core.servlet.ServletBean;
import com.YaNan.frame.core.servlet.defaultServletMapping;
import com.YaNan.frame.core.session.annotation.iToken;

public class ActionPool {
	private static ActionPool actionPool;
	private Map<ServletBean, iToken> actionMap = new HashMap<ServletBean, iToken>();

	private ActionPool() {
	}

	public void init() {
		defaultServletMapping dsm = defaultServletMapping.getInstance();
		Iterator<String> aIterator = dsm.getActionKSIterator();
		while (aIterator.hasNext()) {
			Iterator<Entry<String, ServletBean>> nIterator = dsm
					.getServletEIterator(aIterator.next());
			while (nIterator.hasNext()) {
			//	ServletBean bean = nIterator.next().getValue();
				// if(bean)
			}
		}
	}

	public static ActionPool getActionPool() {
		if (actionPool == null)
			actionPool = new ActionPool();
		return actionPool;
	}

	public static boolean hasAction(ServletBean servletBean) {
		return actionPool.actionMap.containsKey(servletBean);
	}

}
