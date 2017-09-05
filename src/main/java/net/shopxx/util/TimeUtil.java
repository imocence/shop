package net.shopxx.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil { 
	/**
	 * 传递的时间戳与当前时间的差
	 * @param TimeStamp
	 * @return
	 * @throws Long
	 */
	public static Long validateTimeStamp(Long TimeStamp){
		Long difference = (System.currentTimeMillis() - TimeStamp*1000) / (1000 * 60);
		return difference;
	}
	/**
	 * 获取当前时间
	 * @return
	 */
	public static String getNowTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar
				.getInstance().getTime());
	}
	
	public static String getFormatNowTime(String format) {
		return new SimpleDateFormat(format).format(Calendar
				.getInstance().getTime());
	}
	
	public static String getNowTime(String format) {
		format = isNullOrUndefined(format)?"yyyy-MM-dd HH:mm:ss":format;
		return new SimpleDateFormat(format).format(Calendar.getInstance()
				.getTime());
	}
	public static boolean isZero(String v){
		return (v == "1000000000");    	
	}
	public static boolean isNullOrUndefined(String v){
		return (v == null || v.trim().length() == 0 || v.trim().equalsIgnoreCase("undefined") || v.trim().equalsIgnoreCase("NULL"));    	
	}
	public static boolean isBlank(String v) {
		return v == null || "".equals(v.trim()) ;
	}
	public static boolean isEmpty(String v){
		return (v == null || v.trim().isEmpty());    	
	}
	/**
	 * 判断string数组是否包含指定string
	 */
	public static boolean stringArrayContains(String[] arr, String str){
		if(str == null)return false;
		for(String s : arr){
	        if(str.equals(s))
	            return true;
	    }
		return false;
	}
	public static boolean intArrayContains(int[] arr, int str){
		for(int s : arr){
	        if(str == s)
	            return true;
	    }
		return false;
	}
	public static int[] intArrayAdd(int[] arr, int str){
		if(intArrayContains(arr, str))return arr;
		arr = Arrays.copyOf(arr, arr.length+1);
		arr[arr.length - 1] = str;
		return arr;
	}
	
	//生成指定位数为5的数字
	public static String getLineNumber(String lastNo){
		if(lastNo == null || lastNo.isEmpty()){
			return "00001";
		}else{
			int mNo = Integer.parseInt("1"+lastNo);
			mNo++;
			String no = mNo+"";
			return no.substring(1,no.length());
		} 
	}
	
	//判断手机号
	public static boolean isMobile(String mobile){  
		Pattern p = Pattern.compile("^1\\d{10}$");  
		Matcher m = p.matcher(mobile);  
		return m.matches();  
	} 
	
	//判断一个字符串是否为数字
	public static boolean isIsNum(String str) {
		Pattern p = Pattern.compile("^[0-9]+$");  
		Matcher m = p.matcher(str);  
		return m.matches(); 
	}

	//判断登录名(只能英文或数字,6-10位)
	public static boolean isLoginName(String name){  
		Pattern p = Pattern.compile("^[a-zA-Z0-9]{6,10}$");  
		Matcher m = p.matcher(name);  
		return m.matches();  
	}

	//判断一个字符串是否为数字
	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	//判断是否是车牌号
	public static boolean isTruckNo(String truckNo){
		Pattern p = Pattern.compile("^([\u4E00-\u9FFF][A-Za-z]-[A-Za-z0-9]{5})$");  
		Matcher m = p.matcher(truckNo);  
		return m.matches();   
    }
	
	//判断是否是数字,最多两位小数
	public static boolean isPrice(String price){
		Pattern p = Pattern.compile("^\\d+(\\.\\d{1,2})?$");  
		Matcher m = p.matcher(price);  
		return m.matches();   
    }
	public static boolean isDecimal(String price,int precision){
		Pattern p = Pattern.compile("^\\d+(\\.\\d{1,"+precision+"})?$");  
		Matcher m = p.matcher(price);  
		return m.matches();   
    }

	
	
	//生成指定位数的随机数
	public static String gerRandomIntNos(int times){
    	String ran = "";
		while(ran.length() < times){  //随机
			ran+=(int)(Math.random()*10);
		}	
		return ran;
    }
	
	
	@SuppressWarnings("resource")
	public static String readXML(String filePath){
		String str = null;
		StringBuffer strs = new StringBuffer();
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(filePath), "utf-8");
			BufferedReader br = new BufferedReader(read);
			while((str = br.readLine()) != null) {
				strs.append(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strs.toString();
	}
	  
	/**
	 * 计算两个时间之间的秒数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getTimeDelta(Date date1,Date date2){  
		long timeDelta=(date1.getTime()-date2.getTime())/1000;//单位是秒
		int minutesDelta=timeDelta>0?(int)timeDelta:(int)Math.abs(timeDelta);  
		return minutesDelta;  
	} 
}
