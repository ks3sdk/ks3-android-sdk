package com.ksyun.ks3.util;

public class ExceptionUtil {
	public static String getStackMsg(Exception e) {  
		  
        StringBuffer sb = new StringBuffer();  
        sb.append(e.toString()).append("\n");
        StackTraceElement[] stackArray = e.getStackTrace();  
        for (int i = 0; i < stackArray.length; i++) {  
            StackTraceElement element = stackArray[i];  
            sb.append(element.toString() + "\n");  
        }  
        return sb.toString();  
    }  
  
    public static String getStackMsg(Throwable e) {  
  
        StringBuffer sb = new StringBuffer();  
        sb.append(e.toString()).append("\n");
        StackTraceElement[] stackArray = e.getStackTrace();  
        for (int i = 0; i < stackArray.length; i++) {  
            StackTraceElement element = stackArray[i];  
            sb.append(element.toString() + "\n");  
        }  
        return sb.toString();  
    } 
}
