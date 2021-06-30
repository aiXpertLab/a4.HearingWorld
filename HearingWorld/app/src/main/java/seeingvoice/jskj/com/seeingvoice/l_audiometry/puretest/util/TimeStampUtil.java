package seeingvoice.jskj.com.seeingvoice.l_audiometry.puretest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 在Java中如何获取时间戳

 Date类提供了getTime方法:Date().getTime()可以获取时间戳
 Calendar.getInstance().getTimeInMillis();
 System.currentTimeMillis(),效率更佳。

 在不同的开发语言中，获取到的时间戳的长度是不同的，例如C++中的时间戳是精确到秒的，
 但是Java中的时间戳是精确到毫秒的，这样在涉及到不同语言的开发过程中，
 如果不进行统一则会出现一些时间不准确的问题。
 */
public class TimeStampUtil {
    /**
     * 获取精确到秒的时间戳   方法一：通过String.substring()方法将最后的三位去掉
     * @return
     */
    public static int getSecondTimestamp(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime());
        int length = timestamp.length();
        if (length > 3) {
            return Integer.valueOf(timestamp.substring(0,length-3));
        } else {
            return 0;
        }
    }

    /**
     * 获取精确到秒的时间戳   方法二：通过整除将最后的三位去掉
     * @param date
     * @return
     */
    public static int getSecondTimestampTwo(Date date){
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime()/1000);
        return Integer.valueOf(timestamp);
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

}
