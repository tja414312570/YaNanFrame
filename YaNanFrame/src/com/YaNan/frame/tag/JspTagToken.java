package com.YaNan.frame.tag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.session.Token;

public class JspTagToken extends BodyTagSupport {
	private String method;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int doAfterBody() throws JspException {
		String s = this.bodyContent.getString().trim();
		if (s != null) {
			HttpServletRequest request = (HttpServletRequest) this.pageContext
					.getRequest();
			HttpServletResponse response = (HttpServletResponse) this.pageContext
					.getResponse();
			Token token = Token.getToken(request);
			JspWriter out = bodyContent.getEnclosingWriter();
			if (token != null) {
				String[] str = s.split("\\.");
				try {
					if (str.length == 1) {
						Object put = token.get(s);
						out.print(put);
					} else {
						ClassLoader loader = new ClassLoader(s.substring(0,
								s.lastIndexOf(".")));
						if (loader.hasMethod("setServletContext",
								RequestFacade.class, ResponseFacade.class)) {
							loader.invokeMethod("setServletContext", request,
									response);
						}
						Field field = loader
								.getDeclaredField(str[str.length - 1]);
						field.setAccessible(true);
						loader.invokeMethod(method == null ? "execute" : method);
						Object obj = token.get(loader.getLoadedClass());
						out.print(field.get(obj));
					}
				} catch (ClassNotFoundException | NoSuchFieldException
						| SecurityException | IllegalAccessException
						| IOException | NoSuchMethodException
						| IllegalArgumentException | InvocationTargetException
						| InstantiationException e) {
					e.printStackTrace();
				}
			}

		}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		// TODO Auto-generated method stub
		return super.doEndTag();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
