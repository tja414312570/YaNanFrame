package com.YaNan.frame.servlets.session.filter;

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

import com.YaNan.frame.reflect.ClassLoader;
import com.YaNan.frame.servlets.URLSupport;
import com.YaNan.frame.servlets.session.Token;
import com.YaNan.frame.servlets.session.TokenManager;
import com.YaNan.frame.servlets.session.entity.Failed;
import com.YaNan.frame.servlets.session.entity.Result;
import com.YaNan.frame.servlets.session.entity.TokenEntity;
import com.YaNan.frame.servlets.session.interfaceSupport.TokenFilterInterface;
import com.YaNan.frame.servlets.session.interfaceSupport.Token_Command_Type;
import com.YaNan.frame.util.StringUtil;
/**
 * 优先处理action，然后处理命名空间
 * @author Administrator
 *
 */
@WebFilter(filterName = "tokenFilter", urlPatterns = "/*")
public class TokenFilter extends HttpServlet implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			Token token = Token.getToken((HttpServletRequest) request);
			if(token==null)
				token = Token.addToken(((HttpServletRequest)request),(HttpServletResponse) response);
			String url =URLSupport.getRelativePath((HttpServletRequest) request);//URLSupport
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

}
