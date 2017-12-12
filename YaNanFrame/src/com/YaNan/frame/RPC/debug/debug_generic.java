package com.YaNan.frame.RPC.debug;
import com.YaNan.frame.core.reflect.ClassLoader;
public class debug_generic {
	public static void main(String[] args) {
		exc<g> e = new exc<g>();
		e.bind(new g2(){
			@Override
			public String method2(String s) {
				return s;
			}
		});
		e.sayHello();
	}
	static interface g{}
	static interface g1 extends g{
		public int method1(int i);
	}
	static interface g2 extends g{
		public String method2(String s);
	}
	static class exc<K extends g>{
		private K k;

		public void bind(K k){
			this.k= k;
		}
		public void sayHello(){
			if(ClassLoader.implementOf(k.getClass(), g1.class)){
				g1 g = (g1) k;
				System.out.println("hello "+g.method1(125));
			}
			if(ClassLoader.implementOf(k.getClass(), g2.class)){
				g2 g = (g2) k;
				System.out.println("hello "+g.method2("amy"));
			}
		}
	}
}
