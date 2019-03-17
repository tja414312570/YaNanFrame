package a.test.hibernate;

import com.YaNan.frame.util.PathMatcher.Matcher;
import com.YaNan.frame.util.PathMatcher.Token;

public class Main {
	public static void main(String[] args) {
		Matcher matcher = com.YaNan.frame.util.PathMatcher.match("/ab{1*}c{2??}c{3*}/{4**}/{5?5}/{6**}/ds/**rd**", 
				"/abxdfercdfcx/llll/sejlf/asdkfelrj/skdfds/s/h/asleje/dsdfds/ds/dsee/lsete/rd/ds/rd/rter")		;
		System.out.println(matcher.isMatch());
		for(Token token : matcher.getTokens())
			System.out.println(token);
//		System.out.println(matcher.extractUriTemplateVariables("/{a}/{b}", "/aj/kk"));
	}
}
