import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.plugin.PlugsFactory;
import com.YaNan.frame.plugin.autowired.exception.Error;
import com.YaNan.frame.reflect.ClassLoader;;
public class Main {
	public static void main(String[] args) throws Exception {
		Main main = PlugsFactory.getPlugsInstance(Main.class);
		System.out.println(main.hello(null));
	}
	public String hello(String name){
		return name.toString();
	}
}
