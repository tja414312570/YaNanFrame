import com.YaNan.frame.hibernate.database.DBFactory;
import com.YaNan.frame.servlets.annotations.GetMapping;;
public class Main {
	public static void main(String[] args) throws Exception {
		DBFactory.getDBFactory().init();
		
	}
	@GetMapping("hello")
	public int hello(String[] name){
		return name.length;
	}
}
