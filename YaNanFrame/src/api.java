import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.YaNan.frame.logging.Log;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.RegisterDescription;
import com.YaNan.frame.plugin.annotations.Service;
import com.YaNan.frame.plugin.autowired.property.Property;
import com.YaNan.frame.servlets.REQUEST_METHOD;
import com.YaNan.frame.servlets.ServletBean;
import com.YaNan.frame.servlets.ServletMapping;
import com.YaNan.frame.servlets.annotations.GetMapping;
import com.YaNan.frame.servlets.response.annotations.ResponseJson;
import com.sun.jmx.snmp.ThreadContext;

public class api {
	@Service 
	private Log log;
	@Property()
	private String ll;
	@ResponseJson
	@GetMapping
	public Map<String, Object> getToken(){
		Thread t =Thread.currentThread();
		Object s = t.getStackTrace();
		System.out.println(log);
		Map<String,Object> result = new HashMap<String, Object>();
		Map<String, ServletBean> ss= ServletMapping.getInstance().getServletMapping();
		Iterator<ServletBean> it = ss.values().iterator();
		while(it.hasNext()){
			ServletBean bean = it.next();
			result.put(REQUEST_METHOD.getRequest(bean.getUrlmapping()), bean.getMethod().getName());
		}
		return result;
	}
	public static void main(String[] args) throws Exception {
		api a = PlugsFactory.getPlugsInstance(api.class);
		RegisterDescription rd =  PlugsFactory.getPlugsHandler(a).getRegisterDescription();
		rd.setAttribute("token", "000000");
		a.getToken();
		Child c = PlugsFactory.getPlugsInstance(Child.class);
		System.out.println(a.getToken());
	}
}
