import com.YaNan.frame.reflect.ClassLoader;;
public class Main {
	public static void main(String[] args) throws Exception {
		Class<?> cls = Math.class;
		System.out.println(ClassLoader.invokeStaticMethod(cls, "addExact", 1,1));
		
	}
}
