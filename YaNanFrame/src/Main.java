import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.YaNan.frame.reflect.cache.ClassInfoCache;
import com.YaNan.frame.servlets.annotations.GetMapping;
import com.google.gson.Gson;
import com.YaNan.frame.reflect.ClassLoader;
public class Main {
	public static void main(String[] args) throws Exception {
		Field[] fields = ClassInfoCache.getClassHelper(Child.class).getDeclaredFields();
		for(Field field : fields){
			System.out.println(field);
		}
		System.out.println("====");
		fields = ClassInfoCache.getClassHelper(Child.class).getFields();
		for(Field field : fields){
			System.out.println(field);
		}
		System.out.println("====");
		fields = ClassInfoCache.getClassHelper(Child.class).getAllFields();
		ClassLoader classLoader = new ClassLoader(Child.class);
		int i = 0;
		for(Field field : fields){
			System.out.println(field);
			System.out.println(field.getModifiers());
			classLoader.set(field, (++i)+"");
		}
		System.out.println(new Gson().toJson(classLoader.getLoadedObject()));
	}
	static List<Field> getAllFields(Class<?> target,Class<?> endClass){
		if(endClass==null)
			endClass = Object.class;
		List<Field> fieldList = new ArrayList<Field>();
		boolean find=target.equals(endClass)?true:false;
		while(!find){
			Field[] fields = ClassInfoCache.getClassHelper(target).getDeclaredFields();
			for(Field field : fields)
				fieldList.add(field);
			if((target = target.getSuperclass()).equals(endClass))
					find = true;
		}
		return fieldList;
	}
	@GetMapping("hello")
	public int hello(String[] name){
		return name.length;
	}
}
