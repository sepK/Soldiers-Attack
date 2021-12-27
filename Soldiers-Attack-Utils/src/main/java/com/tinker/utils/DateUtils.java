package com.tinker.utils;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author t.k
 * @date 2021/12/2 16:59
 */
public class DateUtils {
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date toDate(LocalDate localDate, ZoneId zoneId) {
        return Date.from(localDate.atStartOfDay().atZone(zoneId).toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime, ZoneId zoneId) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
        return Instant.ofEpochMilli(date.getTime()).atZone(zoneId).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        return Instant.ofEpochMilli(date.getTime()).atZone(zoneId).toLocalDateTime();
    }

    /**
     * 获取一天的开始时间
     *
     * @param time
     * @return
     */
    public static LocalDateTime getDayStart(LocalDateTime time) {
        return LocalDateTime.of(time.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取一天的开始时间
     *
     * @param time
     * @return
     */
    public static LocalDateTime getDayStart(Date time) {
        return LocalDateTime.of(toLocalDate(time), LocalTime.MIN);
    }

    /**
     * 获取一天的结束时间
     *
     * @param time
     * @return
     */
    public static LocalDateTime getDayEnd(LocalDateTime time) {
        return LocalDateTime.of(time.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 获取一天的结束时间
     *
     * @param time
     * @return
     */
    public static LocalDateTime getDayEnd(Date time) {
        return LocalDateTime.of(toLocalDate(time), LocalTime.MAX);
    }

    /**
     * 获取指定日期的毫秒
     *
     * @param time
     * @return
     */
    public static Long getMilliByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取指定日期的毫秒
     *
     * @param time
     * @return
     */
    public static Long getMilliByTime(Date time) {
        return toLocalDateTime(time).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取指定日期的毫秒
     *
     * @param time
     * @param zoneId
     * @return
     */
    public static Long getMilliByTime(LocalDateTime time, ZoneId zoneId) {
        return time.atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 获取指定日期的毫秒
     *
     * @param time
     * @param zoneId
     * @return
     */
    public static Long getMilliByTime(Date time, ZoneId zoneId) {
        return toLocalDateTime(time, zoneId).atZone(zoneId).toInstant().toEpochMilli();
    }

    /**
     * 获取指定日期的秒
     *
     * @param time
     * @return
     */
    public static Long getSecondsByTime(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取指定日期的秒
     *
     * @param time
     * @return
     */
    public static Long getSecondsByTime(Date time) {
        return toLocalDateTime(time).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取指定日期的秒
     *
     * @param time
     * @return
     */
    public static Long getSecondsByTime(LocalDateTime time, ZoneId zoneId) {
        return time.atZone(zoneId).toInstant().getEpochSecond();
    }

    /**
     * 获取指定日期的秒
     *
     * @param time
     * @return
     */
    public static Long getSecondsByTime(Date time, ZoneId zoneId) {
        return toLocalDateTime(time, zoneId).atZone(zoneId).toInstant().getEpochSecond();
    }

    /**
     * 日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
     */
    public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
        return time.plus(number, field);
    }

    /**
     * 日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
     */
    public static LocalDateTime plus(Date time, long number, TemporalUnit field) {
        return toLocalDateTime(time).plus(number, field);
    }

    /**
     * 日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
     *
     * @param time
     * @param number
     * @param field
     * @return
     */
    public static LocalDateTime minus(LocalDateTime time, long number, TemporalUnit field) {
        return time.minus(number, field);
    }

    /**
     * 日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
     *
     * @param time
     * @param number
     * @param field
     * @return
     */
    public static LocalDateTime minus(Date time, long number, TemporalUnit field) {
        return toLocalDateTime(time).minus(number, field);
    }

    /**
     * 获取本月的第一天
     *
     * @return
     */
    public static LocalDateTime getFirstDayOfThisMonth(LocalDateTime time) {
        return time.with(TemporalAdjusters.firstDayOfMonth());
    }
}
