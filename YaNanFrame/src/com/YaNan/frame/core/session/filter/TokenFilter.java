package com.YaNan.frame.core.session.filter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.YaNan.frame.core.reflect.ClassLoader;
import com.YaNan.frame.core.servlet.ServletBean;
import com.YaNan.frame.core.servlet.URLSupport;
import com.YaNan.frame.core.servlet.defaultServletMapping;
import com.YaNan.frame.core.session.Token;
import com.YaNan.frame.core.session.TokenManager;
import com.YaNan.frame.core.session.annotation.Chain;
import com.YaNan.frame.core.session.annotation.NoToken;
import com.YaNan.frame.core.session.annotation.iToken;
import com.YaNan.frame.core.session.entity.Failed;
import com.YaNan.frame.core.session.entity.Result;
import com.YaNan.frame.core.session.entity.TokenEntity;
import com.YaNan.frame.core.session.interfaceSupport.TokenFilterInterface;
import com.YaNan.frame.core.session.interfaceSupport.TokenListener;
import com.YaNan.frame.core.session.interfaceSupport.Token_Command_Type;
import com.YaNan.frame.stringSupport.StringSupport;
/**
 * 优先处理action，然后处理命名空间
 * @author Administrator
 *
 */
@WebFilter(filterName = "tokenFilter", urlPatterns = "/*")
public class TokenFilter extends HttpServlet implements Filter {
	private TokenListener tokenListener = new TokenListener() {
		@Override
		public void onSuccess(ServletRequest request, ServletResponse response,
				FilterChain chain) throws IOException, ServletException {
			//chain.doFilter(request, response);
		}

		@Override
		public void onFailed(Token token, ServletRequest request, ServletResponse response, iToken itoken)
				throws Exception {
			response.getWriter().write(itoken.onFailed());
			response.getWriter().close();
		}
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestURL;
	private Token token;
	private String namespace;
	private String servletName;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			token = Token.getToken((HttpServletRequest) request);
			if(token==null)
				token = Token.addToken(((HttpServletRequest)request),(HttpServletResponse) response);
			String url = ((HttpServletRequest) request).getRequestURL().toString();
			response.setContentType("text/html;charset=UTF-8");
			servletName = url.substring(url.lastIndexOf("/") + 1);
			boolean isServlet=servletName.contains(".do");
			namespace = URLSupport
					.getNameSpace((HttpServletRequest) request);
			requestURL = URLSupport
					.getRequestPath((HttpServletRequest) request);
			// 获取servletMap实例
			//if is CSS、JS、or image file 
			if((url.contains(".css") || url.contains(".js") || url.contains(".png")|| url.contains(".jpg")||url.contains("fonts"))&&!url.contains(".jsp")){
	               chain.doFilter(request, response);
	               return;
	            }
			if(isServlet){
				if(dispatcherServlet( request, response, chain))return;
			}
			//================================================处理命名空间拦截
			 if(TokenManager.hasNameSpace(namespace)) {
				List<TokenEntity> tokenList = TokenManager.getTokenEntitys(namespace);
				Iterator<TokenEntity> iterator = tokenList.iterator();
				boolean RPIC = false;
				while(iterator.hasNext()){
					if(response.isCommitted())return;
					RPIC = this.dispatcherNamespace(token,iterator.next(), request, response, chain);
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

	private boolean dispatcherServlet(ServletRequest request, ServletResponse response, FilterChain chain) {
		servletName =servletName.replace(".do", "");
		defaultServletMapping servletMap = defaultServletMapping.getInstance();
		if (servletMap.isExist(namespace,servletName)) {
			try {
				ServletBean servletBean = servletMap.getServlet(namespace,servletName);
				Class<?> cls = servletBean.getClassName();
				iToken itoken =servletBean.getMethod().getAnnotation(iToken.class);
				if (itoken == null)
					itoken = cls.getAnnotation(iToken.class);
				// 要求token验证
				if (itoken != null) {
					NoToken n = cls.getAnnotation(NoToken.class);
					// token是否存在
					if (token != null) {
						// token存在-----角色判断
						// 1判断自定义处理类
						Chain c = cls.getAnnotation(Chain.class);
						if (c != null) {
							// 如果自定义处理类存在，则解析，解析后与role对应
							String[] cStr = c.chain();
							for (int i = 0; i < cStr.length; i++) {
								String[] cPstr = cStr[i].split(",");
								for (String str : cPstr) {
									if (StringSupport.match(servletName, str)
											&& token.isRole(c.role()[i]
													.split(","))) {
										chain.doFilter(request, response);
										return true;
									}
								}
							}
						}
						// 处理拦截的角色
						if (itoken.exRole().length != 0) {
							if (itoken.exChain().length == 0) {
								if (token.isRole(itoken.exRole())) {
									tokenListener.onFailed(token,request, response,
											itoken);
									return true;
								} else {
									String[] cStr = itoken.exChain();
									for (int i = 0; i < cStr.length; i++) {
										String[] cPstr = cStr[i].split(",");
										for (String str : cPstr) {
											if (servletName.equals(str)
													&& token.isRole(itoken
															.exRole()[i]
															.split(","))) {
												chain.doFilter(request, response);
												return true;
											}
										}
									}
								}
							}
						}
						// 处理放行的角色
						if (itoken.role().length != 0) {
							if (itoken.chain().length == 0) {
								if (token.isRole(itoken.role())) {
									chain.doFilter(request, response);
									return true;
								} else {
									tokenListener.onFailed(token,request, response,itoken);
									return true;
								}
							}else{
								String[] cStr = itoken.chain();
								for (int i = 0; i < cStr.length; i++) {
									String[] cPstr = cStr[i].split(",");
									for (String str : cPstr) {
										if (servletName.equals(str)) {
											chain.doFilter(request, response);
											return true;
										}
									}
								}
							}
							tokenListener.onFailed(token,request, response,itoken);
							return true;
						}
						// 无需角色认证，拥有token自动放行
						chain.doFilter(request, response);
						return true;
					} else {
						// 没有token
						// 1查看是否有自定义NoToken处理类
						if (n != null) {
							for (String str : n.chain()) {
								if (servletName.equals(str)) {
									chain.doFilter(request, response);
									return true;
								}
							}
						}
						// 2没有NoToken处理类
						if (itoken.noToken().length != 0) {
							for (String str : itoken.noToken()) {
								if (servletName.equals(str)) {
									chain.doFilter(request, response);
									return true;
								}
							}
						}

					}
					// 3没有任何处理方法----默认判断
					// (1判断是否为排除的action
					if (itoken.ex().length != 0) {
						for (String str : itoken.ex()) {
							if (servletName.equals(str)) {
								chain.doFilter(request, response);
								return true;
							}
						}
					}
					// 不在角色认证范围，也不在过滤条件的，拦截
					tokenListener.onFailed(token,request, response,itoken);
					return true;
				}else{
					chain.doFilter(request, response);
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	
	private void Redirect(String value, ServletRequest request, ServletResponse response) {
		String contextURL = URLSupport.getContextURL(request);
		try {
			((HttpServletResponse) response)
			.sendRedirect(getURL(contextURL,value));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void Redirect(TokenFilterInterface ti, String url, ServletRequest request, ServletResponse response) throws IOException {
		String contextURL = URLSupport.getContextURL(request);
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

	private void forward(TokenFilterInterface ti, String url, ServletRequest request, ServletResponse response){
		this.forward(getURL(null,url, ti), request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
	public boolean dispatcherNamespace(Token token,TokenEntity tokenEntity, ServletRequest request, ServletResponse response, FilterChain chain) throws Exception{
		if (tokenEntity.chainURL(requestURL,token)){
			return false;
		}
		if(token!=null&&token.isRole(tokenEntity.getRoles().split(","))){
			return false;
		}
		if (tokenEntity.getRoles() == null || tokenEntity.getRoles().equals("")) {
			tokenListener.onSuccess(request, response, chain);
			return false;
		} 
		
		if (tokenEntity.getChain() != null) {
			String[] str =tokenEntity.getChain().split(",");
			for (String s : str) {
				if (StringSupport.match(URLSupport.getRequestFile(request).toString(), s)) {
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
			if(ClassLoader.implementOf(cls, TokenListener.class)){
				TokenListener tokenListen = (TokenListener) cls.newInstance();
				this.tokenListener = tokenListen;
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
					tokenListener.onSuccess(request, response, chain);
					return false;
				} else {
					if (token.isRole(tokenEntity.getRoles())) {
						tokenListener.onSuccess(request, response, chain);
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

	public TokenListener getTokenListener() {
		return tokenListener;
	}

	public void setTokenListener(TokenListener tokenListener) {
		this.tokenListener = tokenListener;
	}
	public static String getURL(String contextUrl,String oUrl,Object ti){
		if(contextUrl!=null)
			contextUrl = StringSupport.decodeVar(contextUrl, ti);
		oUrl = StringSupport.decodeVar(oUrl, ti);
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

}
