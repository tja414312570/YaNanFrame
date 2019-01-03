package com.YaNan.frame.web.security;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import com.YaNan.frame.plugin.ConfigContext;
import com.YaNan.frame.plugin.annotations.Register;
import com.YaNan.frame.servlets.URLSupport;
import com.YaNan.frame.util.StringUtil;
import com.typesafe.config.Config;

@Register
@WebFilter(filterName = "SecurityFilter", urlPatterns = "/*")
public class SecurityFilter extends HttpServlet implements Filter {
	private Map<String,Boolean> xssWrapper = new LinkedHashMap<String,Boolean>();
	private static final long serialVersionUID = 1L;
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		String url =URLSupport.getRelativePath((HttpServletRequest) request);//URLSupport
		String queryParam = ((HttpServletRequest) request).getQueryString();
		if(queryParam!=null)
			url = new StringBuilder(url).append("?").append(queryParam).toString();
		for(Entry<String,Boolean> entry : xssWrapper.entrySet()){
			if(StringUtil.matchURI(url, entry.getKey())){
				chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request),
						response);
				return;
			}
		}
		chain.doFilter(request,
				response);
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		Config config = ConfigContext.getConfig("SecurityFilter");
		if(config!=null){
			config.disableTypeVerify();
			config.allowKeyNull();
			config.allowValueNull();
			//获取xss配置
			String value = config.getString("xss-wrapper");
			if(value!=null){
				if(value.trim().equals("true"))
					this.xssWrapper.put("/**", true);
				else{
					String[] strs =value.split(",");
					for(String str : strs){
						this.xssWrapper.put(str.trim(), true);
					}
				}
			}
			List<? extends Config> confLists = config.getConfigList("xss-wrapper");
			//获取到conf 的 list
			if(confLists!=null&&confLists.size()!=0){
				for(Config c : confLists){
					Iterator<Entry<String, Object>> i = c.simpleObjectEntrySet().iterator();
					while(i.hasNext()){
						Entry<String, Object> entry = i.next();
						this.xssWrapper.put(entry.getKey(),Boolean.parseBoolean(entry.getValue().toString()));
					}
				}
			}
			//对象的config
			Config conf= config.getConfig("xss-wrapper");
			if(conf!=null){
				Iterator<Entry<String, Object>> i = conf.simpleObjectEntrySet().iterator();
				while(i.hasNext()){
					Entry<String, Object> entry = i.next();
					this.xssWrapper.put(entry.getKey(),Boolean.parseBoolean(entry.getValue().toString()));
				}
			}
		}else{
			this.xssWrapper.put("/**", true);
		}
	}
}
