package com.lcz.file.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 日期工具类
 */
public class DateUtil {

    /**
     * excel导入校验
     */
    public static Date parseDate(String parseDate) throws RuntimeException {
        if (StringUtils.isNotBlank(parseDate)) {
            Pattern p1 = Pattern.compile("^([0-9]{4}-[0-9]{2}-[0-9]{2})$");
            Pattern p2 = Pattern.compile("^([0-9]{4}/[0-9]{2}/[0-9]{2})$");
            Pattern p3 = Pattern.compile("^([0-9]{4}-[1-9]-[1-9])$");
            Pattern p4 = Pattern.compile("^([0-9]{4}/[1-9]/[1-9])$");
            Pattern p5 = Pattern.compile("^([0-9]{4}-[1-9]-[0-9]{2})$");
            Pattern p6 = Pattern.compile("^([0-9]{4}/[1-9]/[0-9]{2})$");
            Pattern p7 = Pattern.compile("^([0-9]{4}-[0-9]{2}-[1-9])$");
            Pattern p8 = Pattern.compile("^([0-9]{4}/[0-9]{2}/[1-9])$");
            Pattern p9 = Pattern.compile("^([0-9]{4}年[0-9]{2}月[1-9]日)$");
            Pattern p10 = Pattern.compile("^([0-9]{4}年[1-9]月[0-9]{2}日)$");
            Pattern p11 = Pattern.compile("^([0-9]{4}年[0-9]{2}月[0-9]{2}日)$");
            Pattern p12 = Pattern.compile("^([0-9]{4}年[1-9]月[1-9]日)$");
            Pattern p13 = Pattern.compile("^([0-9]{8})$");
            if (p1.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy-MM-dd");
            } else if (p2.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy/MM/dd");
            } else if (p3.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy-M-d");
            } else if (p4.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy/M/d");
            } else if (p5.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy-M-dd");
            } else if (p6.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy/M/dd");
            } else if (p7.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy-MM-d");
            } else if (p8.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy/MM/d");
            } else if (p9.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy年MM月d日");
            } else if (p10.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy年M月dd日");
            } else if (p11.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy年MM月dd日");
            } else if (p12.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy年M月d日");
            } else if (p13.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyyMMdd");
            } else {
                throw new RuntimeException("日期格式不对");
            }
        }
        return null;
    }

    /**
     * excel导入校验
     */
    public static Date parseDateMonth(String parseDate) throws Exception {
        if (StringUtils.isNotBlank(parseDate)) {
            Pattern p1 = Pattern.compile("^([0-9]{4}-[0-9]{2})$");
            Pattern p2 = Pattern.compile("^([0-9]{4}/[0-9]{2})$");
            Pattern p3 = Pattern.compile("^([0-9]{4}-[1-9])$");
            Pattern p4 = Pattern.compile("^([0-9]{4}/[1-9])$");
            Pattern p5 = Pattern.compile("^([0-9]{4}年[0-9]{2}月)$");
            Pattern p6 = Pattern.compile("^([0-9]{4}年[1-9]月)$");
            Pattern p7 = Pattern.compile("^([0-9]{6})$");
            if (p1.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy-MM");
            } else if (p2.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy/MM");
            } else if (p3.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy-M");
            } else if (p4.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy/M");
            } else if (p5.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy年MM月");
            } else if (p6.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyy年M月");
            } else if (p7.matcher(parseDate).matches()) {
                return DateFormatUtil.parse(parseDate, "yyyyMM");
            } else {
                throw new RuntimeException("日期格式不对");
            }
        }
        return null;
    }

    /**
     * 将字符串转换为时间，包含时分，秒可有可无
     */
    public static Date parseDateMinute(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        Pattern p = Pattern.compile("(\\d{1,4})[-|\\\\/](\\d{1,2})[-|\\\\/](\\d{1,2}) (\\d{1,2}):(\\d{1,2}):(\\d{1,2})", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern p2 = Pattern.compile("(\\d{1,4})[-|\\\\/](\\d{1,2})[-|\\\\/](\\d{1,2}) (\\d{1,2}):(\\d{1,2})", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern p3 = Pattern.compile("(\\d{1,4})年(\\d{1,2})月(\\d{1,2})日 (\\d{1,2})时(\\d{1,2})分(\\d{1,2})秒", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Pattern p4 = Pattern.compile("(\\d{1,4})年(\\d{1,2})月(\\d{1,2})日 (\\d{1,2})时(\\d{1,2})分", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        if (p.matcher(text).matches()) {
            return getDateTimeDetail(p, text);
        } else if (p2.matcher(text).matches()) {
            return getDateTimeDetail(p2, text);
        } else if (p3.matcher(text).matches()) {
            return getDateTimeDetail(p3, text);
        } else if (p4.matcher(text).matches()) {
            return getDateTimeDetail(p4, text);
        } else {
            throw new RuntimeException("日期格式不正确");
        }
    }

    private static Date getDateTimeDetail(Pattern p, String date) {
        Matcher matcher = p.matcher(date);
        if (!matcher.find()) {
            throw new RuntimeException("日期格式错误");
        }

        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int day = Integer.parseInt(matcher.group(3));
        int hour = Integer.parseInt(matcher.group(4));
        int minute = Integer.parseInt(matcher.group(5));
        int second = 0;
        if (matcher.groupCount() == 6) {
            second = Integer.parseInt(matcher.group(6));
        }

        if (year < 100) {
            year = year + 2000;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);  //设置日期
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

}
