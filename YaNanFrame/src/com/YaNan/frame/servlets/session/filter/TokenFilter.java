package com.YaNan.frame.servlets.session.filter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.ServletDispatcher;
import com.YaNan.frame.servlets.ServletMapping;
import com.YaNan.frame.servlets.URLSupport;
import com.YaNan.frame.servlets.session.Token;
import com.YaNan.frame.servlets.session.TokenManager;
import com.YaNan.frame.servlets.session.annotation.Authentication;
import com.YaNan.frame.servlets.session.annotation.AuthenticationGroups;
import com.YaNan.frame.servlets.session.annotation.Chain;
import com.YaNan.frame.servlets.session.entity.Failed;
import com.YaNan.frame.servlets.session.entity.Result;
import com.YaNan.frame.servlets.session.entity.TokenEntity;
import com.YaNan.frame.servlets.session.interfaceSupport.TokenFilterInterface;
import com.YaNan.frame.servlets.session.interfaceSupport.Token_Command_Type;
import com.YaNan.frame.utils.StringUtil;
/**
 * 优先处理action，然后处理命名空间
 * @author Administrator
 *
 */
@WebFilter(filterName = "tokenFilter", urlPatterns = "/*")
public class TokenFilter extends HttpServlet implements Filter {
	Map<ServletBean, Authentication[]> authPools;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			Token token = Token.getToken((HttpServletRequest) request);
			if(token==null)
				token = Token.addToken(((HttpServletRequest)request),(HttpServletResponse) response);
			token.set(HttpServletRequest.class, request);
			token.set(HttpServletResponse.class,response);
			((HttpServletRequest)request).getSession().setAttribute(TokenManager.getTokenMark(), token.getTokenId());
			this.jstlSupport((HttpServletRequest)request,token);
			
			//如果为servlet 访问其属性
			String url =URLSupport.getRelativePath((HttpServletRequest) request);//URLSupport
			ServletBean servletBean = ServletMapping.getInstance().getAsServlet(url);
			if (servletBean != null) {
				String resourceType = servletBean.getStyle();
				ServletDispatcher servletDispatcher = PlugsFactory.getPlugsInstanceByAttribute(ServletDispatcher.class,
						resourceType);
				servletBean = servletDispatcher.getServletBean((HttpServletRequest) request);
				if (servletBean != null)
					if(!dispatcherServlet(request, response, chain, servletBean, token))
						return;
			}
			String queryParam = ((HttpServletRequest) request).getQueryString();
			if(queryParam!=null)
				url = new StringBuilder(url).append("?").append(queryParam).toString();
					//.getRequestPath((HttpServletRequest) request);
			 if(TokenManager.match(url)) {
				List<TokenEntity> tokenList = TokenManager.getTokenEntitys(url);
				Iterator<TokenEntity> iterator = tokenList.iterator();
				boolean RPIC = false;
				while(iterator.hasNext()){
					if(response.isCommitted())return;
					RPIC = this.dispatcherNamespace(token,iterator.next(), request, response, chain,url);
					if(RPIC)break;
				}
				if(!RPIC)chain.doFilter(request, response);
			}else{
				chain.doFilter(request, response);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void jstlSupport(HttpServletRequest request, Token token) {
		Iterator<Entry<String, Object>> iterator = token.attributeEntry().iterator();
		while(iterator.hasNext()){
			Entry<String, Object> entry = iterator.next();
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	private void Redirect(String value, ServletRequest request, ServletResponse response) {
		String contextURL = ((HttpServletRequest)request).getContextPath();
		try {
			((HttpServletResponse) response)
			.sendRedirect(getURL(contextURL,value));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void Redirect(TokenFilterInterface ti, String url, ServletRequest request, ServletResponse response) throws IOException {
		String contextURL = ((HttpServletRequest)request).getContextPath();
		if(!((HttpServletResponse) response).isCommitted())
			((HttpServletResponse) response).sendRedirect(getURL(contextURL,url,ti));
	}
	private void forward(String url, ServletRequest request, ServletResponse response) {
		try {
			((HttpServletRequest)request).getRequestDispatcher(url).forward(request, response);
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private void forward(TokenFilterInterface ti, String url, ServletRequest request, ServletResponse response){
		this.forward(getURL(null,url, ti), request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
	public boolean dispatcherNamespace(Token token,TokenEntity tokenEntity, ServletRequest request, ServletResponse response, FilterChain chain,String url) throws Exception{
		if (tokenEntity.chainURL(url,token)){
			return false;
		}
		if(token!=null&&token.containerRole(tokenEntity.getRoles().split(","))){
			return false;
		}
		if (tokenEntity.getChain() != null) {
			String[] str =tokenEntity.getChain().split(",");
			for (String s : str) {
				if (StringUtil.match(URLSupport.getRequestFile(request).toString(), s)) {
					return false;
				}
			}
		}
		if(tokenEntity.getCLASS()!=null){
			Class<?> cls  = Class.forName(tokenEntity.getCLASS());
			if(ClassLoader.implementOf(cls,TokenFilterInterface.class)){
				TokenFilterInterface ti = (TokenFilterInterface) cls.newInstance();
				String ro = ti.excute(request,response, token);
				if(ro==null){
					Result result = tokenEntity.getDefaultResult();
					if(result==null){
						response.getWriter().flush();
						return true;
					}
					String command = result.getCommand();
					if(command.equals(Token_Command_Type.COMMAND_REDIRECT)){
						this.Redirect(ti,result.getValue(), request, response);
						return true;
					}
				}
				if(ro!=null&&tokenEntity.hasResult(ro.toString())){
					Result result = tokenEntity.getResult(ro.toString());
					if(result==null){
						response.getWriter().write(ro.toString());
						response.getWriter().flush();
						return true;
					}
					String command = result.getCommand();
					if(command.equals(Token_Command_Type.COMMAND_CHAIN)){
						chain.doFilter(request, response);
						return true;
					}
					if(command.equals(Token_Command_Type.COMMAND_REDIRECT)){
						this.Redirect(ti,result.getValue(), request, response);
						return true;
					}
					if(command.equals(Token_Command_Type.COMMAND_OUTPUT)){
						response.getWriter().write(result.getValue());
						response.getWriter().flush();
						return true;
					}
					if(command.equals(Token_Command_Type.COMMAND_FORWARD)){
						this.forward(ti,result.getValue(), request, response);
						return true;
					}
						
				}
				response.getWriter().write(ro);
				response.getWriter().flush();
				return true;
			}
		}
		//if request url container the url's in Token Entity do chain
			//判断token存在
		
			if (Token.getToken((HttpServletRequest) request) == null) {
				//获取默认failed 或 默认值
				if(tokenEntity.getValue()!=null){
					this.Redirect(tokenEntity.getValue(), request, response);
					return true;
				}
				if(tokenEntity.hasFailed()){
					Failed failed = tokenEntity.getFailed();
					if (failed.getValue()!=null&&!failed.getValue().equals("")) {
						String command = failed.getCommand()==null?"redirect":failed.getCommand();
						if(command.equals(Token_Command_Type.COMMAND_REDIRECT)){
							this.Redirect(failed.getValue(), request, response);
							return true;
						}
						if(command.equals(Token_Command_Type.COMMAND_OUTPUT)){
							response.getWriter().write(failed.getValue());
							response.getWriter().flush();
							return true;
						}
						if(command.equals(Token_Command_Type.COMMAND_FORWARD)){
							this.forward(failed.getValue(), request, response);
							return true;
						}
						if(command.equals(Token_Command_Type.COMMAND_CHAIN)){
							chain.doFilter(request, response);
							return true;
						}
					}
					return true;
				}
				if(tokenEntity.getDefaultResult()!=null){
					Result result = tokenEntity.getDefaultResult();
					if(result.getValue()!=null){
						
						if(result.getCommand().equals(Token_Command_Type.COMMAND_REDIRECT)){
							this.Redirect(result.getValue(), request, response);
						}
						if(result.getCommand().equals(Token_Command_Type.COMMAND_OUTPUT)){
							response.getWriter().write(result.getValue());
							response.getWriter().flush();
						}
						if(result.getCommand().equals(Token_Command_Type.COMMAND_FORWARD)){
							this.forward(result.getValue(), request, response);
						}
						if(result.getCommand().equals(Token_Command_Type.COMMAND_CHAIN)){
							chain.doFilter(request, response);
							return true;
						}
					}
				
				}
				return true;
			} else {
				if (tokenEntity.getRoles() == null || tokenEntity.getRoles().equals("")) {
					return false;
				} else {
					if (token.isRole(tokenEntity.getRoles())) {
						return false;
					} else {
						return this.OnFailed(tokenEntity, token, request, response, chain);
					}
				}
			}
	}

	private boolean OnFailed(TokenEntity tokenEntity,Token token,ServletRequest request,ServletResponse response,FilterChain chain) throws IOException {
		if(tokenEntity.getValue()!=null){
			this.Redirect(tokenEntity.getValue(), request, response);
			return true;
		}
		if(tokenEntity.hasFailed()){
			Failed failed = tokenEntity.getFailed();
			if (failed.getValue()!=null&&!failed.getValue().equals("")) {
				String command = failed.getCommand()==null?"redirect":failed.getCommand();
				if(command.equals("redirect")){
					this.Redirect(failed.getValue(), request, response);
					return true;
				}
				if(command.equals("output")){
					response.setContentType("text/html;charset=UTF-8");
					response.getWriter().write(failed.getValue());
					response.getWriter().flush();
					return true;
				}
			}
			return true;
		}
		if(tokenEntity.getDefaultResult()!=null){
			Result result = tokenEntity.getDefaultResult();
			if(result.getValue()!=null){
				if(result.getCommand().equals(Token_Command_Type.COMMAND_REDIRECT)){
					this.Redirect(result.getValue(), request, response);
				}
				if(result.getCommand().equals(Token_Command_Type.COMMAND_OUTPUT)){
					response.setContentType("text/html;charset=UTF-8");
					response.getWriter().write(result.getValue());
					response.getWriter().flush();
				}
				if(result.getCommand().equals(Token_Command_Type.COMMAND_FORWARD)){
					this.forward(result.getValue(), request, response);
				}
			}
		}
		return true;
	}

	public static String getURL(String contextUrl,String oUrl,Object ti){
		if(contextUrl!=null)
			contextUrl = StringUtil.decodeVar(contextUrl, ti);
		oUrl = StringUtil.decodeVar(oUrl, ti);
		return getURL(contextUrl,oUrl);
	}
	public static String getURL(String contextUrl,String oUrl){
		if(contextUrl==null)
			return oUrl;
		Pattern pattern = Pattern  
	            .compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
		if(pattern.matcher(oUrl).matches())
			return oUrl;
		return contextUrl
				+ oUrl;
	}
	
	/**
	 * 如果有注解。则返回true，否则返回false；
	 * @param request
	 * @param response
	 * @param chain
	 * @param servletBean
	 * @param token
	 * @return
	 */
	private boolean dispatcherServlet(ServletRequest request, ServletResponse response, FilterChain chain,
			ServletBean servletBean, Token token) {
		if (servletBean != null) {
			try {
				Class<?> cls = servletBean.getServletClass();
				Authentication[] authGroups = getAuthGroups(servletBean);
				if(authGroups.length==0) 
					return true;
				//遍历每个认证注解
				for (Authentication auth : authGroups) {
					// 要求token验证
					if (auth != null) {
						// 0获取Token注解中的chain
						if (auth.chain().length != 0) {
							for (String action : auth.chain()) {
								if (servletBean.getMethod().getName().equals(action)) {// 如果找到，就直接放行
									chain.doFilter(request, response);
								}
							}
						}
						// 1获取类中的chain注解
						Chain c = cls.getAnnotation(Chain.class);
						if (c != null) {
							for (String action : auth.chain()) {
								if (servletBean.getMethod().getName().equals(action)) {// 如果找到，就直接放行
									chain.doFilter(request, response);
								}
							}
						}
						// 2获取token注解中的roles
						if (auth.roles().length != 0) {
							if (token.containerRole(auth.roles())) {
								chain.doFilter(request, response);
							} else {
								this.onFailed(request, response, auth);
							}
						}
						// 3 获取Token注解中的exroles
						if (auth.exroles().length != 0) {
							if (token.containerRole(auth.exroles())) {
								this.onFailed(request, response, auth);
							} else {
								chain.doFilter(request, response);
							}
						}
						chain.doFilter(request, response);
					}
				}
				chain.doFilter(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void onFailed(ServletRequest request, ServletResponse response, Authentication auth)
			throws IOException, ServletException {
		int status = auth.status() == -1 ? HttpServletResponse.SC_UNAUTHORIZED : auth.status();
		if (auth.message().length() != 0) {
			((HttpServletResponse) response).setStatus(status);
			response.setContentType(auth.contextType());
			Writer writer = response.getWriter();
			writer.write(auth.message());
			writer.flush();
			writer.close();
		} else if (auth.forward().length() != 0) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(auth.forward());
			dispatcher.forward(request, response);
		} else if (auth.redirect().length() != 0) {
			((HttpServletResponse) response).setStatus(status);
			((HttpServletResponse) response).sendRedirect(auth.redirect());
		} else {
			((HttpServletResponse) response).sendError(status, "You do not have permission to access this content");
		}

	}

	private Authentication[] getAuthGroups(ServletBean servletBean) {
		Authentication[] authGroups;
		if (authPools == null)
			synchronized (this) {
				if (authPools == null)
					authPools = new HashMap<ServletBean, Authentication[]>();
			}
		authGroups = authPools.get(servletBean);
		if (authGroups == null) {
			authGroups = servletBean.getMethod().getAnnotationsByType(Authentication.class);
			if (authGroups.length == 0) {
				AuthenticationGroups authGroup = servletBean.getMethod().getAnnotation(AuthenticationGroups.class);
				if (authGroup != null)
					authGroups = authGroup.value();
				else {
					authGroups = servletBean.getServletClass().getAnnotationsByType(Authentication.class);
					if (authGroups.length == 0)
						authGroup = servletBean.getServletClass().getAnnotation(AuthenticationGroups.class);
					if (authGroup != null)
						authGroups = authGroup.value();
				}
			}
			authPools.put(servletBean, authGroups);
		}
		return authGroups;
	}


}
