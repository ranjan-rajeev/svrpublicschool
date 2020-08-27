package com.svrpublicschool.Util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtility {

    public int getDaysOld(String expiryDate) {
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
        try {
            Date date = sourceFormat.parse(expiryDate);
            Calendar calendarExpiry = Calendar.getInstance();
            calendarExpiry.setTime(date);
            calendarExpiry.set(Calendar.HOUR_OF_DAY, 0);
            calendarExpiry.set(Calendar.MINUTE, 0);
            calendarExpiry.set(Calendar.SECOND, 0);
            calendarExpiry.set(Calendar.MILLISECOND, 0);

            Calendar todayCalendar = Calendar.getInstance();
            todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
            todayCalendar.set(Calendar.MINUTE, 0);
            todayCalendar.set(Calendar.SECOND, 0);
            todayCalendar.set(Calendar.MILLISECOND, 0);

            long daysOld = (calendarExpiry.getTimeInMillis() - todayCalendar.getTimeInMillis());
            long diffInDays = TimeUnit.MILLISECONDS.toDays(daysOld);
            return (int) diffInDays;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getFormattedEstimatedDeliveryDate(int dayOfMonth) {
        String result = "";
        if (dayOfMonth >= 11 && dayOfMonth <= 13) {
            result = "th";
        } else {
            switch (dayOfMonth % 10) {
                case 1:
                    result = "st";
                    break;
                case 2:
                    result = "nd";
                    break;
                case 3:
                    result = "rd";
                    break;
                default:
                    result = "th";
                    break;
            }
        }
        return result;
    }

    public static String getFormattedPromoCliqCashExpiryDate(String date, String format, boolean withTime, boolean isExceptionCame) {
        String result = "";
        if (!TextUtils.isEmpty(date)) {
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat(!TextUtils.isEmpty(format) ? format : "yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                SimpleDateFormat desiredFormat;
                Date date1 = sourceFormat.parse(date);
                Calendar estimateDate = Calendar.getInstance();
                estimateDate.setTime(date1);
                desiredFormat = new SimpleDateFormat("d\'" + getFormattedEstimatedDeliveryDate(estimateDate.get(Calendar.DAY_OF_MONTH)) + "\' MMM, yyyy" + (withTime ? " h:mm a" : ""), Locale.ENGLISH);
                result = desiredFormat.format(date1);
            } catch (Exception e) {
                if (!isExceptionCame) {
                    return getFormattedCliqCashExpiryDate(date, "yyyy-MM-dd'T'HH:mm:ss", withTime, true);
                }
            }
        }
        return result;
    }

    public static String getFormattedTime(long time) {
        try {
            Calendar estimateDate = Calendar.getInstance();
            estimateDate.setTime(new Date(time));
            //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d\'" + getFormattedEstimatedDeliveryDate(estimateDate.get(Calendar.DAY_OF_MONTH)) + "\' MMM" + " h:mm a", Locale.ENGLISH);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" h:mm a", Locale.ENGLISH);

            return simpleDateFormat.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFormattedCliqCashExpiryDate(String date, String format, boolean withTime, boolean isExceptionCame) {
        String result = "";
        if (!TextUtils.isEmpty(date)) {
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat(!TextUtils.isEmpty(format) ? format : "yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                SimpleDateFormat desiredFormat;
                Date date1 = sourceFormat.parse(date);
                Calendar estimateDate = Calendar.getInstance();
                estimateDate.setTime(date1);
                desiredFormat = new SimpleDateFormat("d\'" + getFormattedEstimatedDeliveryDate(estimateDate.get(Calendar.DAY_OF_MONTH)) + "\' MMMM yyyy" + (withTime ? " h:mm a" : ""), Locale.ENGLISH);
//                if (withTodayTomorrow) {
//                    if (isSameDay(today, estimateDate)) {
//                        result = "Today, " + desiredFormat.format(date1);
//                    } else {
//                        today.add(Calendar.DAY_OF_MONTH, 1);
//                        if (isSameDay(today, estimateDate)) {
//                            result = "Tomorrow, " + desiredFormat.format(date1);
//                        } else {
//                            result = desiredFormat.format(date1);
//                        }
//                    }
//                } else {
                result = desiredFormat.format(date1);
//                }
            } catch (Exception e) {
                if (!isExceptionCame) {
                    return getFormattedCliqCashExpiryDate(date, "yyyy-MM-dd'T'HH:mm:ss", withTime, true);
                }
            }
        }
        return result;
    }
}
