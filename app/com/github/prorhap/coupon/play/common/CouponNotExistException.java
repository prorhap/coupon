package com.github.prorhap.coupon.play.common;

public class CouponNotExistException extends RuntimeException {
    public CouponNotExistException() {
    }

    public CouponNotExistException(String message) {
        super(message);
    }

    public CouponNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CouponNotExistException(Throwable cause) {
        super(cause);
    }

    public CouponNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
