package com.chatapp.threadripper.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    public static String formatDateTime(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(date);
    }

    public static String formatTime(Date date) {
        return new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(date);
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(date);
    }

    public static String formatBestDateTime(Date date) {
        if (differentInDays(date, new Date()) > 1) {
            return formatDateTime(date);
        }
        return formatTime(date);
    }

    public static String formatBestShortDateTime(Date date) {
        if (differentInDays(date, new Date()) > 1) {
            return formatDate(date);
        }
        return formatTime(date);
    }

    public static int differentInDays(Date sm, Date lg) {
        return (int) ((lg.getTime() - sm.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static int differentInHours(Date sm, Date lg) {
        return (int) ((lg.getTime() - sm.getTime()) / (1000 * 60 * 60));
    }

    public static int differentInMinutes(Date sm, Date lg) {
        return (int) ((lg.getTime() - sm.getTime()) / (1000 * 60));
    }

    public static int differentInSeconds(Date sm, Date lg) {
        return (int) ((lg.getTime() - sm.getTime()) / (1000));
    }

    public static Date parseDateTime(String format, String dateTimeText) {
        Date dateTime = null;
        try {
            dateTime = (new SimpleDateFormat(format, Locale.ENGLISH)).parse(dateTimeText);
        } catch (ParseException e) {
            e.printStackTrace();
            dateTime = new Date();
        }
        return dateTime;
    }
}
