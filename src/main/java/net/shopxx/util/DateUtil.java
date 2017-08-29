package net.shopxx.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换工具
 * @author gaoxaing
 *
 */
public class DateUtil 
{
	private final static String defaultDateTimeExpression = "yyyy-MM-dd HH:mm:ss";
	private final static String defaultDateExpression = "yyyy-MM-dd";
	private final static String defaultTimeStampExpression = "yyyy-MM-dd HH:mm:ss.SSS";
	private static Calendar cal = null;
	/**
	 * 将对象转化成时间对象
	 * @param o
	 * @return
	 */
	public static Date parseObject(Object o)
	{
		try {
			return (Date)o;
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}
	
	/**
	 * 自定义时间转字符串
	 * @param d
	 * @param expression
	 * @return
	 */
	public static String format(Date d , String expression)
	{
		DateFormat df =new SimpleDateFormat(expression);
		return df.format(d);
	}
	
	/**
	 * 自定义字符串转时间
	 * @param dateString
	 * @param expression
	 * @return
	 * @throws ParseException
	 */
	public static Date parse(String dateString , String expression) throws ParseException
	{
		DateFormat df =new SimpleDateFormat(expression);
		return df.parse(dateString);
	}
	
	/**
	 * 将时间转成yyyy-MM-dd HH:mm:ss格式的字符串
	 * @param d
	 * @return
	 */
	public static String dateTimeFormat(Date d)
	{
		DateFormat df =new SimpleDateFormat(defaultDateTimeExpression);
		return df.format(d);
	}
	
	/**
	 * 将yyyy-MM-dd HH:mm:ss格式的字符串转化为时间
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date dateTimeParse(String dateString) throws ParseException
	{
		DateFormat df =new SimpleDateFormat(defaultDateTimeExpression);
		return df.parse(dateString);
	}
	
	/**
	 * 将时间转化成yyyy-MM-dd格式的字符串
	 * @param d
	 * @return
	 */
	public static String dateFormat(Date d)
	{
		DateFormat df =new SimpleDateFormat(defaultDateExpression);
		return df.format(d);
	}
	
	/**
	 * 将yyyy-MM-dd格式的字符串转换成时间
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date dateParse(String dateString) throws ParseException
	{
		DateFormat df =new SimpleDateFormat(defaultDateExpression);
		return df.parse(dateString);
	}
	
	/**
	 * 将时间转成yyyy-MM-dd HH:mm:ss.SSS格式的字符串
	 * @param d
	 * @return
	 */
	public static String timeStampFormat(Date d)
	{
		DateFormat df =new SimpleDateFormat(defaultTimeStampExpression);
		return df.format(d);
	}
	
	/**
	 * 将yyyy-MM-dd HH:mm:ss.SSS格式的字符串转化为时间
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date timeStampParse(String dateString) throws ParseException
	{
		DateFormat df =new SimpleDateFormat(defaultTimeStampExpression);
		return df.parse(dateString);
	}
	
	/**
	 * 创建时间（年,月,日）
	 * @param yyyy
	 * @param MM
	 * @param dd
	 * @return
	 */
	public static Date create(int yyyy, int MM, int dd)
	{
		return create(yyyy, MM, dd, 0, 0, 0, 0);
	}
	
	/**
	 * 创建时间（年,月,日,时,分,秒）
	 * @param yyyy
	 * @param MM
	 * @param dd
	 * @param HH
	 * @param mm
	 * @param ss
	 * @return
	 */
	public static Date create(int yyyy, int MM, int dd, int HH, int mm, int ss)
	{
		return create(yyyy, MM, dd, HH, mm, ss, 0);
	}
	
	/**
	 * 创建时间（年,月,日,时,分,秒,毫秒）
	 * @param yyyy
	 * @param MM
	 * @param dd
	 * @param HH
	 * @param mm
	 * @param ss
	 * @param SSS
	 * @return
	 */
	public static Date create(int yyyy, int MM, int dd, int HH, int mm, int ss, int SSS)
	{
		cal = Calendar.getInstance();
		try 
		{
			cal.set(Calendar.YEAR, yyyy);
			cal.set(Calendar.MONTH, MM-1);
			cal.set(Calendar.DATE, dd);
			cal.set(Calendar.HOUR_OF_DAY, HH);
			cal.set(Calendar.MINUTE, mm);
			cal.set(Calendar.SECOND, ss);
			cal.set(Calendar.MILLISECOND, SSS);
		} 
		catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return cal.getTime();
	}
	
	/**
	 * 增加或者减少小时
	 * @param d
	 * @param num
	 * @return
	 */
	public static Date addHours(Date d , Integer num)
	{
		return add(d, Calendar.HOUR_OF_DAY, num);
	}
	
	/**
	 * 增加或者减少分钟
	 * @param d
	 * @param num
	 * @return
	 */
	public static Date addMinutes(Date d , Integer num)
	{
		return add(d, Calendar.MINUTE, num);
	}
	
	/**
	 * 增加或者减少秒
	 * @param d
	 * @param num
	 * @return
	 */
	public static Date addSeconds(Date d, Integer num)
	{
		return add(d, Calendar.SECOND, num);
	}
	
	/**
	 * 增加或者减少年份
	 * @param d
	 * @param num
	 * @return
	 */
	public static Date addYears(Date d, Integer num)
	{
		return add(d, Calendar.YEAR, num);
	}
	
	/**
	 * 增加或者减少月份
	 * @param d
	 * @param num
	 * @return
	 */
	public static Date addMonths(Date d, Integer num)
	{
		return add(d, Calendar.MONTH, num);
	}
	
	/**
	 * 增加或者减少日期
	 * @param d
	 * @param num
	 * @return
	 */
	public static Date addDays(Date d, Integer num)
	{
		return add(d, Calendar.DATE, num);
	}
	
	/**
	 * 得到年份
	 * @param d
	 * @return
	 */
	public static Integer getYear(Date d)
	{
		return get(d, Calendar.YEAR);
	}
	
	/**
	 * 得到月份
	 * @param d
	 * @return
	 */
	public static Integer getMonth(Date d)
	{
		return get(d, Calendar.MONTH) + 1;
	}
	
	/**
	 * 得到日期（月中的几号）
	 * @param d
	 * @return
	 */
	public static Integer getDay(Date d)
	{
		return get(d, Calendar.DATE);
	}
	
	/**
	 * 得到小时（24时制）
	 * @param d
	 * @return
	 */
	public static Integer getHour(Date d)
	{
		return get(d, Calendar.HOUR_OF_DAY);
	}
	
	/**
	 * 得到分钟
	 * @param d
	 * @return
	 */
	public static Integer getMinute(Date d)
	{
		return get(d, Calendar.MINUTE);
	}
	
	/**
	 * 得到秒
	 * @param d
	 * @return
	 */
	public static Integer getSecend(Date d)
	{
		return get(d, Calendar.SECOND);
	}
	
	/**
	 * 改变时间的某个数值
	 * @param d
	 * @param type
	 * @param num
	 * @return
	 */
	public static Date add(Date d , Integer type,  Integer num)
	{
		try
		{
			cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(type, num);
			return cal.getTime();
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
		
		return d;
	}
	
	/**
	 * 得到时间的某个数值
	 * @param d
	 * @param type
	 * @return
	 */
	public static Integer get(Date d, Integer type)
	{
		try
		{
			cal = Calendar.getInstance();
			cal.setTime(d);
			return cal.get(type);
		}
		catch(Exception err)
		{
			err.printStackTrace();
		}
		
		return null;
	}
}
