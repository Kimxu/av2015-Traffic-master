package com.hinsty.traffic.debug;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author dz
 * @version 2015/6/20.
 */
public class Calendar{

    private Calendar(){}

    public void add(int i, int i1) {
        mCalendar.add(i, i1);
    }

    
//    protected void computeFields() {
//        try {
//            methodComputeFields.invoke(mCalendar,new Object[]{});
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    protected void computeTime() {
//        try {
//            methodComputeTime.invoke(mCalendar,new Object[]{});
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//    public boolean after(Object calendar) {
//        return mCalendar.after(calendar);
//    }
//
//
//    public boolean before(Object calendar) {
//        return mCalendar.before(calendar);
//    }

    
    public Object clone() {
        return mCalendar.clone();
    }

    
//    protected void complete() {
//        try {
//            methodCompile.invoke(mCalendar,new Object[]{});
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }

    
    public boolean equals(Object object) {
        return mCalendar.equals(object);
    }

    
    public int get(int field) {
        return mCalendar.get(field);
    }

    
//    public int getActualMaximum(int field) {
//        return mCalendar.getActualMaximum(field);
//    }
//
//
//    public int getActualMinimum(int field) {
//        return mCalendar.getActualMinimum(field);
//    }

    
    public int getFirstDayOfWeek() {
        return mCalendar.getFirstDayOfWeek();
    }

    
    public int getMinimalDaysInFirstWeek() {
        return mCalendar.getMinimalDaysInFirstWeek();
    }

    
    public long getTimeInMillis() {
        return mCalendar.getTimeInMillis();
    }

    
    public TimeZone getTimeZone() {
        return mCalendar.getTimeZone();
    }

    
    public int hashCode() {
        return mCalendar.hashCode();
    }

    
    public boolean isLenient() {
        return mCalendar.isLenient();
    }

    
    public void roll(int field, int value) {
        mCalendar.roll(field, value);
    }

    
    public void set(int field, int value) {
        mCalendar.set(field, value);
    }

    
    public void setFirstDayOfWeek(int value) {
        mCalendar.setFirstDayOfWeek(value);
    }

    
    public void setLenient(boolean value) {
        mCalendar.setLenient(value);
    }

    
    public void setMinimalDaysInFirstWeek(int value) {
        mCalendar.setMinimalDaysInFirstWeek(value);
    }

    
    public void setTimeInMillis(long milliseconds) {
        mCalendar.setTimeInMillis(milliseconds);
    }

    
    public void setTimeZone(TimeZone timezone) {
        mCalendar.setTimeZone(timezone);
    }

    
    public String toString() {
        return mCalendar.toString();
    }

    
    public int compareTo(java.util.Calendar anotherCalendar) {
        return mCalendar.compareTo(anotherCalendar);
    }

    
    public String getDisplayName(int field, int style, Locale locale) {
        return mCalendar.getDisplayName(field, style, locale);
    }

    
    public Map<String, Integer> getDisplayNames(int field, int style, Locale locale) {
        return mCalendar.getDisplayNames(field, style, locale);
    }

    
    public int getGreatestMinimum(int i) {
        return mCalendar.getGreatestMinimum(i);
    }

    
    public int getLeastMaximum(int i) {
        return mCalendar.getLeastMaximum(i);
    }

    
    public int getMaximum(int i) {
        return mCalendar.getMaximum(i);
    }

    
    public int getMinimum(int i) {
        return mCalendar.getMinimum(i);
    }

    
    public void roll(int i, boolean b) {
        mCalendar.roll(i, b);
    }
    static Class mClass = java.util.Calendar.class;
    static Method methodComputeTime,methodComputeFields,methodCompile;

    static{
        try {
            methodComputeTime = mClass.getDeclaredMethod("computeTime");
            methodComputeFields = mClass.getDeclaredMethod("computeFields");
            methodCompile = mClass.getDeclaredMethod("compile");
            methodComputeTime.setAccessible(true);
            methodComputeFields.setAccessible(true);
            methodCompile.setAccessible(true);
        } catch (NoSuchMethodException e) {
        }
    }
    public  java.util.Calendar mCalendar;
    public static int year,month,day,hour,M;

    public static void setTime(int hour,int M){
        Calendar.hour = hour ;
        Calendar.M = M ;
    }
    public static void now(){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        year = calendar.get(java.util.Calendar.YEAR);
        month = calendar.get(java.util.Calendar.MONTH);
        day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        M = calendar.get(java.util.Calendar.MINUTE);
    }

    public static Calendar getInstance(){
        Calendar ret = new Calendar();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.YEAR,year);
        calendar.set(java.util.Calendar.MONTH,month);
        calendar.set(java.util.Calendar.DAY_OF_MONTH,day);
        calendar.set(java.util.Calendar.HOUR_OF_DAY,hour);
        calendar.set(java.util.Calendar.MINUTE,M);
        ret.mCalendar = calendar;
        return ret;
    }


    /**
     * Value of the {@code MONTH} field indicating the first month of the
     * year.
     */
    public static final int JANUARY = 0;

    /**
     * Value of the {@code MONTH} field indicating the second month of
     * the year.
     */
    public static final int FEBRUARY = 1;

    /**
     * Value of the {@code MONTH} field indicating the third month of the
     * year.
     */
    public static final int MARCH = 2;

    /**
     * Value of the {@code MONTH} field indicating the fourth month of
     * the year.
     */
    public static final int APRIL = 3;

    /**
     * Value of the {@code MONTH} field indicating the fifth month of the
     * year.
     */
    public static final int MAY = 4;

    /**
     * Value of the {@code MONTH} field indicating the sixth month of the
     * year.
     */
    public static final int JUNE = 5;

    /**
     * Value of the {@code MONTH} field indicating the seventh month of
     * the year.
     */
    public static final int JULY = 6;

    /**
     * Value of the {@code MONTH} field indicating the eighth month of
     * the year.
     */
    public static final int AUGUST = 7;

    /**
     * Value of the {@code MONTH} field indicating the ninth month of the
     * year.
     */
    public static final int SEPTEMBER = 8;

    /**
     * Value of the {@code MONTH} field indicating the tenth month of the
     * year.
     */
    public static final int OCTOBER = 9;

    /**
     * Value of the {@code MONTH} field indicating the eleventh month of
     * the year.
     */
    public static final int NOVEMBER = 10;

    /**
     * Value of the {@code MONTH} field indicating the twelfth month of
     * the year.
     */
    public static final int DECEMBER = 11;

    /**
     * Value of the {@code MONTH} field indicating the thirteenth month
     * of the year. Although {@code GregorianCalendar} does not use this
     * value, lunar calendars do.
     */
    public static final int UNDECIMBER = 12;

    /**
     * Value of the {@code DAY_OF_WEEK} field indicating Sunday.
     */
    public static final int SUNDAY = 1;

    /**
     * Value of the {@code DAY_OF_WEEK} field indicating Monday.
     */
    public static final int MONDAY = 2;

    /**
     * Value of the {@code DAY_OF_WEEK} field indicating Tuesday.
     */
    public static final int TUESDAY = 3;

    /**
     * Value of the {@code DAY_OF_WEEK} field indicating Wednesday.
     */
    public static final int WEDNESDAY = 4;

    /**
     * Value of the {@code DAY_OF_WEEK} field indicating Thursday.
     */
    public static final int THURSDAY = 5;

    /**
     * Value of the {@code DAY_OF_WEEK} field indicating Friday.
     */
    public static final int FRIDAY = 6;

    /**
     * Value of the {@code DAY_OF_WEEK} field indicating Saturday.
     */
    public static final int SATURDAY = 7;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * era, e.g., AD or BC in the Julian calendar. This is a calendar-specific
     * value; see subclass documentation.
     *
     * @see java.util.GregorianCalendar#AD
     * @see java.util.GregorianCalendar#BC
     */
    public static final int ERA = 0;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * year. This is a calendar-specific value; see subclass documentation.
     */
    public static final int YEAR = 1;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * month. This is a calendar-specific value. The first month of the year is
     * {@code JANUARY}; the last depends on the number of months in a
     * year.
     *
     * @see #JANUARY
     * @see #FEBRUARY
     * @see #MARCH
     * @see #APRIL
     * @see #MAY
     * @see #JUNE
     * @see #JULY
     * @see #AUGUST
     * @see #SEPTEMBER
     * @see #OCTOBER
     * @see #NOVEMBER
     * @see #DECEMBER
     * @see #UNDECIMBER
     */
    public static final int MONTH = 2;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * week number within the current year. The first week of the year, as
     * defined by {@code getFirstDayOfWeek()} and
     * {@code getMinimalDaysInFirstWeek()}, has value 1. Subclasses
     * define the value of {@code WEEK_OF_YEAR} for days before the first
     * week of the year.
     *
     * @see #getFirstDayOfWeek
     * @see #getMinimalDaysInFirstWeek
     */
    public static final int WEEK_OF_YEAR = 3;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * week number within the current month. The first week of the month, as
     * defined by {@code getFirstDayOfWeek()} and
     * {@code getMinimalDaysInFirstWeek()}, has value 1. Subclasses
     * define the value of {@code WEEK_OF_MONTH} for days before the
     * first week of the month.
     *
     * @see #getFirstDayOfWeek
     * @see #getMinimalDaysInFirstWeek
     */
    public static final int WEEK_OF_MONTH = 4;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * day of the month. This is a synonym for {@code DAY_OF_MONTH}. The
     * first day of the month has value 1.
     *
     * @see #DAY_OF_MONTH
     */
    public static final int DATE = 5;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * day of the month. This is a synonym for {@code DATE}. The first
     * day of the month has value 1.
     *
     * @see #DATE
     */
    public static final int DAY_OF_MONTH = 5;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * day number within the current year. The first day of the year has value
     * 1.
     */
    public static final int DAY_OF_YEAR = 6;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * day of the week. This field takes values {@code SUNDAY},
     * {@code MONDAY}, {@code TUESDAY}, {@code WEDNESDAY},
     * {@code THURSDAY}, {@code FRIDAY}, and
     * {@code SATURDAY}.
     *
     * @see #SUNDAY
     * @see #MONDAY
     * @see #TUESDAY
     * @see #WEDNESDAY
     * @see #THURSDAY
     * @see #FRIDAY
     * @see #SATURDAY
     */
    public static final int DAY_OF_WEEK = 7;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * ordinal number of the day of the week within the current month. Together
     * with the {@code DAY_OF_WEEK} field, this uniquely specifies a day
     * within a month. Unlike {@code WEEK_OF_MONTH} and
     * {@code WEEK_OF_YEAR}, this field's value does <em>not</em>
     * depend on {@code getFirstDayOfWeek()} or
     * {@code getMinimalDaysInFirstWeek()}. {@code DAY_OF_MONTH 1}
     * through {@code 7} always correspond to <code>DAY_OF_WEEK_IN_MONTH
     * 1</code>;
     * {@code 8} through {@code 15} correspond to
     * {@code DAY_OF_WEEK_IN_MONTH 2}, and so on.
     * {@code DAY_OF_WEEK_IN_MONTH 0} indicates the week before
     * {@code DAY_OF_WEEK_IN_MONTH 1}. Negative values count back from
     * the end of the month, so the last Sunday of a month is specified as
     * {@code DAY_OF_WEEK = SUNDAY, DAY_OF_WEEK_IN_MONTH = -1}. Because
     * negative values count backward they will usually be aligned differently
     * within the month than positive values. For example, if a month has 31
     * days, {@code DAY_OF_WEEK_IN_MONTH -1} will overlap
     * {@code DAY_OF_WEEK_IN_MONTH 5} and the end of {@code 4}.
     *
     * @see #DAY_OF_WEEK
     * @see #WEEK_OF_MONTH
     */
    public static final int DAY_OF_WEEK_IN_MONTH = 8;

    /**
     * Field number for {@code get} and {@code set} indicating
     * whether the {@code HOUR} is before or after noon. E.g., at
     * 10:04:15.250 PM the {@code AM_PM} is {@code PM}.
     *
     * @see #AM
     * @see #PM
     * @see #HOUR
     */
    public static final int AM_PM = 9;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * hour of the morning or afternoon. {@code HOUR} is used for the
     * 12-hour clock. E.g., at 10:04:15.250 PM the {@code HOUR} is 10.
     *
     * @see #AM_PM
     * @see #HOUR_OF_DAY
     */
    public static final int HOUR = 10;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * hour of the day. {@code HOUR_OF_DAY} is used for the 24-hour
     * clock. E.g., at 10:04:15.250 PM the {@code HOUR_OF_DAY} is 22.
     *
     * @see #HOUR
     */
    public static final int HOUR_OF_DAY = 11;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * minute within the hour. E.g., at 10:04:15.250 PM the {@code MINUTE}
     * is 4.
     */
    public static final int MINUTE = 12;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * second within the minute. E.g., at 10:04:15.250 PM the
     * {@code SECOND} is 15.
     */
    public static final int SECOND = 13;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * millisecond within the second. E.g., at 10:04:15.250 PM the
     * {@code MILLISECOND} is 250.
     */
    public static final int MILLISECOND = 14;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * raw (non-DST) offset from GMT in milliseconds.
     * Equivalent to {@link java.util.TimeZone#getRawOffset}.
     *
     * <p>To determine the total offset from GMT at the time represented
     * by this calendar, you will need to add the {@code ZONE_OFFSET} and
     * {@code DST_OFFSET} fields.
     */
    public static final int ZONE_OFFSET = 15;

    /**
     * Field number for {@code get} and {@code set} indicating the
     * daylight savings offset from the {@code ZONE_OFFSET} in milliseconds.
     * Equivalent to {@link java.util.TimeZone#getDSTSavings} if the represented time
     * falls inside DST, or 0 otherwise.
     *
     * <p>To determine the total offset from GMT at the time represented
     * by this calendar, you will need to add the {@code ZONE_OFFSET} and
     * {@code DST_OFFSET} fields.
     */
    public static final int DST_OFFSET = 16;

    /**
     * This is the total number of fields in this calendar.
     */
    public static final int FIELD_COUNT = 17;

    /**
     * Value of the {@code AM_PM} field indicating the period of the day
     * from midnight to just before noon.
     */
    public static final int AM = 0;

    /**
     * Value of the {@code AM_PM} field indicating the period of the day
     * from noon to just before midnight.
     */
    public static final int PM = 1;

    /**
     * Requests both {@code SHORT} and {@code LONG} styles in the map returned by
     * {@link #getDisplayNames}.
     * @since 1.6
     */
    public static final int ALL_STYLES = 0;

    /**
     * Requests short names (such as "Jan") from
     * {@link #getDisplayName} or {@link #getDisplayNames}.
     * @since 1.6
     */
    public static final int SHORT = 1;

    /**
     * Requests long names (such as "January") from
     * {@link #getDisplayName} or {@link #getDisplayNames}.
     * @since 1.6
     */
    public static final int LONG = 2;
}
