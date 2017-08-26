package net.shopxx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Util - 字符串工具类
 * 
 * @author gaoxiang
 * @version 5.0.3
 */
public class StringUtil {
	private static final String SPACE = "";
	private static final char SINGLE_QUOTES = '\'';
	/**
	 * 逗号
	 */
	private static final String COMMA = ",";
	
	public static String trimPage(String str){
		if (null == str || "null".equals(str)){
			return "";
		}
		
		return str.trim();
	}
	
	public static String spacePage(String str){
		if (null == str || "null".equals(str) || "".equals(str)){
			return SPACE;
		}
		
		return str.trim();
	}
	
	public static String spiltArray(String[] str, String delimiter){
		StringBuffer sBuffer = new StringBuffer();
		if (null != str && str.length != 0){
			for (int i = 0; i < str.length - 1; i++) {
				sBuffer.append(str[i]).append(delimiter);
			}
			if (isEmpty(str[str.length - 1])){
				sBuffer.append(SPACE);
			}else {
				sBuffer.append(str[str.length - 1]);
			}
		}
		return sBuffer.toString();
	}
	
	public static String spiltList(List<String> strList, String delimiter){
		StringBuffer sBuffer = new StringBuffer();
		if (null != strList && !strList.isEmpty()){
			for (String string : strList) {
				sBuffer.append(string).append(delimiter);
			}
			
			sBuffer.deleteCharAt(sBuffer.length() - 1);
		}
		return sBuffer.toString();
	}
	
	public static String spiltSet(Set<String> strList, String delimiter){
		StringBuffer sBuffer = new StringBuffer();
		if (null != strList && !strList.isEmpty()){
			for (String string : strList) {
				sBuffer.append(string).append(delimiter);
			}
			
			sBuffer.deleteCharAt(sBuffer.length() - 1);
		}
		return sBuffer.toString();
	}
	
	public static String spiltListForDB(List<String> strList, String delimiter){
		StringBuffer sBuffer = new StringBuffer();
		if (null != strList && !strList.isEmpty()){
			for (String string : strList) {
				sBuffer.append(SINGLE_QUOTES).append(string).append(SINGLE_QUOTES).append(delimiter);
			}
			sBuffer.deleteCharAt(sBuffer.length() - 1);
		}
		return sBuffer.toString();
	}
	
	public static String trim(String str){
		if (null == str){
			return "";
		}
		
		return str.trim();
	}
	
	public static String getEscape(String str){
		if (null == str){
			return "";
		}
		
		str = str.replaceAll("%",	"/%");
		str = str.replaceAll("_",	"/_");
		return " '%" + str.trim() + "%' escape '/' ";
	}
	
	/**
	 * true:非空 false:为空
	 * @param str 校验的字符串
	 * @return true:非空 false:为空
	 */
	public static boolean isNotNull(String str) {
		if (null == str){
			return false;
		}
		return true;
	}
	
	/**
	 * true:非空 false:为空
	 * @param str 校验的字符串
	 * @return true:非空 false:为空
	 */
	public static boolean isNotEmpty(String str) {
		if (null == str || str.trim().isEmpty()){
			return false;
		}
		return true;
	}
	
	/**
	 * true:非空 false:为空
	 * @param str 校验的字符串
	 * @return true:非空 false:为空
	 */
	public static boolean isEmpty(String str) {
		if (null == str || str.trim().isEmpty()){
			return true;
		}
		return false;
	}
	
	/**
	 * true:空 false:非空
	 * @param str 校验的字符串
	 * @return true:空 false:非空
	 */
	public static boolean isEmptyOrNull(String str) {
		if (null == str || str.trim().isEmpty() || "null".equalsIgnoreCase(str)){
			return true;
		}
		return false;
	}
	
	/**
	 * true:非空 false:为空
	 * @param str 校验的字符串
	 * @return true:非空 false:为空
	 */
	public static boolean isNotEmptyAndNotNull(String str) {
		if (null == str || str.trim().isEmpty() || "null".equalsIgnoreCase(str)){
			return false;
		}
		return true;
	}
	
	/**
	 * 手机号中间四位改成*
	 * @param phone 手机号
	 * @return 手机号
	 */
	public static String phoneHelp(String phone) {
		if (phone != null && phone.length() >= 11){
			return phone.substring(0, phone.length()-8) + "****" + phone.substring(phone.length()-4);
		}
		return "";
	}
	
	/**
	 * 银行卡号最后四位前四位改成*
	 * @param phone 手机号
	 * @return 手机号
	 */
	public static String bankAccountHelp(String bankAccount) {
		if (bankAccount != null && bankAccount.length() > 8){
			return bankAccount.substring(0, bankAccount.length()-8) + "****" + bankAccount.substring(bankAccount.length()-4);
		}
		return "";
	}
	
	
	/**
	 * 判断两个字符串是否相等
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isStrEquals(String str1, String str2)
	{
		if (trim(str1).equalsIgnoreCase(trim(str2)))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * true:非空 false:为空
	 * @param str 校验的字符串
	 * @return true:非空 false:为空
	 */
	public static boolean isObjEmpty(Object obj) {
		if (null == obj || obj.equals("")){
			return false;
		}
		return true;
	}
	
	public static List<String> spiltCommaList(String str){
		if (null != str){
			str = str.replaceAll("，", COMMA);
			String[] strs = str.split(COMMA);
			if (null != strs){
				return Arrays.asList(strs);
			}
		}
		return new ArrayList<String>();
	}
	
	
	public static boolean equals(String o1, String o2){
		return ((null == o1 || "".equals(o1.trim()) || "null".equals(o1.trim())) && (null == o2 || "".equals(o2.trim()) || "null".equals(o2.trim()))) 
			|| (null != o1 && null != o2 && o1.trim().equals(o2.trim()));
	}
	
	public static String jquery(String str){
		return str.replaceAll(" ", "");
	}
	
	public static String strSub(String str, int length){
		if (length > 0 && StringUtil.isNotEmptyAndNotNull(str) && str.length() > length) {
			return str.substring(0, length) + "...";
		}
		return str;
	}
	
	public static void main(String[] args) {
		System.out.println(strSub("12345678901", 1));
	}
}
