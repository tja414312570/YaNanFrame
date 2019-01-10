package a.test.hibernate;


public class Main {
	public static void main(String[] args) {
		PathMatcher matcher = new AntPathMatcher();
		System.out.println(matcher.match("/ab*c*/**/?????/**/ds/**rd**", 
				"/abxdfercdfcx/llll/sejlf/asdkfelrj/skdfds/s/h/asleje/dsdfds/ds/dsee/lsete/ds/rd/rter"));
//		System.out.println(matcher.extractUriTemplateVariables("/{a}/{b}", "/aj/kk"));
	}
}
