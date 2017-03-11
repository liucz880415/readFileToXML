package com.lcz.file.utils;

import com.google.common.base.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtil {

    public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_YMDHM = "yyyy-MM-dd HH:mm";

    public static final String FORMAT_YMD = "yyyy-MM-dd";

    public static final String FORMAT_MDHM = "MM-dd HH:mm";

    public static final String FORMAT_MD = "MM-dd";

    public static final String FORMATMD = "MMdd";

    public static final String FORMATYMD = "yyyyMMdd";

    public static final String FORMATYM = "yyyyMM";

    public static final String FORMAT_YM = "yyyy-MM";

    public static final String FORMAT_Y = "yyyy";

    public static final String FORMAT_M = "MM";

    public static final String FORMAT_D = "dd";

    public static final String FORMAT_HM = "HH:mm";

    public static final String FORMAT_HM_NUMBER = "HHmm";

    public static final String FORMAT_HMS = "HH:mm:ss";

    public static final String FORMATYMDHM = "yyyyMMddHHmm";

    public static final String FORMATYMDHMS = "yyyyMMddHHmmss";

    public static SimpleDateFormat formatYMD = new SimpleDateFormat(FORMATYMD);

    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat simpleDate = new SimpleDateFormat(format);
        return simpleDate.format(date);
    }

    public static String format(Calendar calendar, String format) {
        return format(calendar.getTime(), format);
    }

    public static String format(Long time, String format) {
        return format(new Date(time), format);
    }

    public static Date parse(String date, String format) {
        SimpleDateFormat simpleDate = new SimpleDateFormat(format);
        try {
            if (Strings.isNullOrEmpty(date)) {
                return null;
            }
            return simpleDate.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
