package com.YaNan.frame.stringSupport;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.YaNan.frame.core.reflect.ClassLoader;

public class StringSupport {
	public static int maxTimes = 10;

	public static String decodeVar(String str, Object obj) {
		Pattern var = Pattern.compile("\\$\\{(\\w|_)+\\}");
		Pattern reVar = Pattern.compile("\\$\\{|\\}");
		Matcher m = var.matcher(str);
		ClassLoader loader = new ClassLoader(obj);
		int i = 0;
		while (m.find()) {
			i++;
			String result = m.group();
			Matcher mA = reVar.matcher(result);
			String field = mA.replaceAll("");
			if (loader.hasMethod(ClassLoader.createFieldGetMethod(field))) {
				try {
					result = (String) loader.get(field);
				} catch (SecurityException
						| IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}
			str = str.substring(0, m.start()) + result + str.substring(m.end());
			m = var.matcher(str);
			if (i > maxTimes)
				break;
		}
		return str;
	}

	public static int getMaxTimes() {
		return maxTimes;
	}

	public static void setMaxTimes(int maxTimes) {
		StringSupport.maxTimes = maxTimes;
	}

	public static String multi(int size, String string) {
		String temp = "";
		for(int i=0;i<size;i++){
			temp+=string;
		}
		return temp;
	}
	//问号匹配
    private static boolean QTMark(String src,String regex)
    {
        if(src.length()!=regex.length())return false;
        for(int i=0;i<regex.length();i++)
            if(src.charAt(i)!=regex.charAt(i) && regex.charAt(i)!='?')
            return false;        
        return true;
    }
   
	//任意字符匹配
	public static  boolean match(String src, String regex) {
		//对*? 或 *? 进行处理，默认替换为*
		//使用while循环处理 ***???或???***格式等错误格式  该格式导致程序异常
		while(regex.contains("*?"))
			regex=regex.replace("*?", "?");
		while(regex.contains("?*"))
			regex=regex.replace("?*", "*");
    	int SMI = regex.indexOf("*");
    	int QMI = regex.indexOf("?");
    	//如果没有星号和问号 则为精确匹配
    	if(SMI==-1&&QMI==-1){
    		if(regex.length()!=src.length())return false;
    		return regex.equals(src);
    	}
    	//如果有问号没有星号  交给星号处理部分
    	if(SMI==-1&&QMI!=-1)
    		return QTMark(src,regex);
    	//如果只有星号没有问号 交给问号处理部分
    	if(SMI!=-1&&QMI==-1)
    		return STMark(src,regex);
    	//特殊位置处理 格式 *.*.?.?格式
    	 //如果?在前面
    	if(SMI<QMI)
    	{
    		//星号特殊位置处理
    		//星号的位置不为00
    		if(SMI!=0){
    			String REGTemp = regex.substring(0,SMI);
    			String SRCTemp = src.substring(0,SMI);
    			if(!REGTemp.equals(SRCTemp))
    				return false;
    		}
    		//连着多个星号*.*x*.?格式
    		String stmp = regex.substring(0,QMI);
    		if(stmp.indexOf("*", SMI+1)!=-1){
    			int lastSMI = stmp.lastIndexOf("*");
    			String stmpREG = stmp.substring(0,lastSMI+1);
    			String spmpREG = stmp.substring(lastSMI+1,QMI);
    			if(!src.contains(spmpREG))return false;
    			int srcSpIndex = src.indexOf(spmpREG);
    			int regSpIndex = regex.indexOf(spmpREG);
    			String stmpSRC = src.substring(0,srcSpIndex);
    			if(!STMark(stmpSRC,stmpREG))return false;
    			String rstREG = regex.substring(regSpIndex+spmpREG.length());
    			String rstSRC = src.substring(srcSpIndex+spmpREG.length());
    			return match(rstSRC,rstREG);
    		}
    		String temp = regex.substring(SMI+1, QMI);
    		int RTIndex = regex.indexOf(temp);
    		int STIndex = src.indexOf(temp);
    		if(src.contains(temp)){
    			return match(src.substring(STIndex+temp.length(),src.length()),regex.substring(RTIndex+temp.length(),regex.length()));
    		}
    		return false;
    	}else{
    		String qtmp = regex.substring(0,SMI);
    		//连着的问号处理
    		if(qtmp.indexOf("?", QMI+1)!=-1){
    			int lastQMI = qtmp.lastIndexOf("?");
    			String qtmpREG = qtmp.substring(0,lastQMI+1);
    			String qpmpREG = qtmp.substring(lastQMI+1,SMI);
    			if(!src.contains(qpmpREG))return false;
    			int srcSpIndex = src.indexOf(qpmpREG);
    			int regSpIndex = regex.indexOf(qpmpREG);
    			String qtmpSRC = src.substring(0,srcSpIndex);
    			if(!QTMark(qtmpSRC,qtmpREG))return false;
    			String rstREG = regex.substring(regSpIndex+qpmpREG.length());
    			String rstSRC = src.substring(srcSpIndex+qpmpREG.length());
    			return match(rstSRC,rstREG);
    		}
    		String temp = regex.substring(QMI+1, SMI);
    		int RTIndex = regex.indexOf(temp);
    		int STIndex = src.indexOf(temp);
    		//问号特殊处理,需要判断索引位置是否相同
    		if(RTIndex!=STIndex)return false;
    		if(src.contains(temp)){
    			return match(src.substring(STIndex+temp.length(),src.length()),regex.substring(RTIndex+temp.length(),regex.length()));
    		}
    		return false;
    	}
	}
	//星号匹配
		private static boolean STMark(String Src,String regex)
	    { // *号匹配
	        int Lstr=Src.length();
	        int Lreg=regex.length();
	        //第一次星号的位置
	        int SIndex=regex.indexOf("*");
	        //第二次星号的位置
	        int SNIndex = regex.indexOf("*",SIndex+1);
	        //如果第一个星号的位置=-1，则说明没有星号，则为精确匹配
	        switch(SIndex)
	        {
	        case -1:{
	        	//如果没有星号，则为精确匹配
	           return Src.equals(regex);
	        }
	        case 0:
	        {//SIndex=0 regex 中 * 号在首位
	            if(Lreg==1)return true;//只有一个星号，自然是匹配的，如 regex="*"
	            //如果只有一个星号，且表达式的长度不为1 如 *abc 截取abc
	            if(SNIndex==-1)
	            {
	            	String rtemp = regex.substring(1,regex.length()) ;
	            	//如果字符串的长度与截取表达式片段长度小，匹配失败
	            	if(Src.length()<rtemp.length())return false;
	            	//字符串中从右往左截取表达式片段长度的字符片段
	            	if(!Src.substring(Src.length()-rtemp.length(), Src.length()).equals(rtemp))
	            		return false;
	            	else
	            		return true;
	            }
	            //多个星号，则按片段截取
	            String temp = regex.substring(SIndex+1, SNIndex);
	            //如果源字符串不包含该片段，匹配失败
	            if(!Src.contains(temp))return false;
	            //如果第二星号的位置在最后以为 匹配成功
	            if(SNIndex==regex.length()-1)return true;
	            //截取匹配以后剩下的片段
	            int STIndex = Src.indexOf(temp)+temp.length();
	            int RTIndex = regex.indexOf(temp)+temp.length();
	            String subSRC = Src.substring(STIndex, Src.length());
	            String subREG = regex.substring(RTIndex, regex.length());
	            return STMark(subSRC,subREG);
	        }
	        default:
	        {    //SIndex>0
	        	String temp = regex.substring(0,SIndex);
	        	//如果元字符串总长度小于片段
	        	if(Src.length()<temp.length())return false;
	            for(int i=0;i<SIndex;i++)
	                if(Src.charAt(i)!=regex.charAt(i))return false;
	            return STMark(Src.substring(SIndex,Lstr),regex.substring(SIndex,Lreg));
	        }
	        }
	    }
}
