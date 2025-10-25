package com.example.textview.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private static final SimpleDateFormat DAY_NAME_FORMAT = new SimpleDateFormat("EEEE", new Locale("pt", "BR"));

    public static String getCurrentDate() {
        return DATE_FORMAT.format(new Date());
    }

    public static String formatToDisplay(String dateString) {
        try {
            Date date = DATE_FORMAT.parse(dateString);
            return DISPLAY_DATE_FORMAT.format(date);
        } catch (ParseException e) {
            return dateString;
        }
    }

    public static String getDayName(String dateString) {
        try {
            Date date = DATE_FORMAT.parse(dateString);
            return DAY_NAME_FORMAT.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    public static boolean isToday(String dateString) {
        return dateString.equals(getCurrentDate());
    }

    public static boolean isTomorrow(String dateString) {
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrowStr = DATE_FORMAT.format(tomorrow.getTime());
        return dateString.equals(tomorrowStr);
    }

    public static int getDaysUntil(String dateString) {
        try {
            Date targetDate = DATE_FORMAT.parse(dateString);
            Date today = DATE_FORMAT.parse(getCurrentDate());
            long diff = targetDate.getTime() - today.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24));
        } catch (ParseException e) {
            return 0;
        }
    }

    public static boolean isThisWeek(String dateString) {
        try {
            Date date = DATE_FORMAT.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int week = cal.get(Calendar.WEEK_OF_YEAR);
            int year = cal.get(Calendar.YEAR);

            Calendar now = Calendar.getInstance();
            int currentWeek = now.get(Calendar.WEEK_OF_YEAR);
            int currentYear = now.get(Calendar.YEAR);

            return week == currentWeek && year == currentYear;
        } catch (ParseException e) {
            return false;
        }
    }

    public static String formatTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
