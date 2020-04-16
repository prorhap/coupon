package com.github.prorhap.coupon.play.common;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class CouponDateUtils {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Date remainOnlyDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public Date parseDate(String dateString) throws Exception {
        return dateString == null || dateString.isEmpty() ?
                null : DateUtils.parseDate(dateString, "yyyy-MM-dd");
    }

    public Date parseDateTime(String dateTimeString) throws Exception {

        return dateTimeString == null || dateTimeString.isEmpty() ?
                null : DateUtils.parseDate(dateTimeString, "yyyy-MM-dd HH:mm:s");
    }

    public Date yesterday() {
        return remainOnlyDate(DateUtils.addDays(new Date(), -1));
    }

    public Date tomorrow() {
        return tomorrow(new Date());
    }

    public Date tomorrow(Date date) {
        return remainOnlyDate(DateUtils.addDays(date, 1));
    }

    public Date now(){
        return new Date();
    }

    public Date today() {
        return remainOnlyDate(now());
    }

    public Date after(int days) {
        return DateUtils.addDays(today(), 3);
    }
}
