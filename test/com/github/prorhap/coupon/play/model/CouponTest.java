package com.github.prorhap.coupon.play.model;

import com.github.prorhap.coupon.play.common.CouponDateUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CouponTest {

    private CouponDateUtils couponDateUtils = new CouponDateUtils();
    private CouponFactory couponFactory = new CouponFactory(new CouponDateUtils());


    @Test
    public void itShouldReturnExpiredResultWhenExpiredCouponUse() throws Exception {

        Coupon coupon = create(
                "A1B1C1", false, true,
                "2020-04-01", "2020-04-03", "bob",
                "2020-04-02 08:00:00", null);

        CouponUseValidationResult validationResult = coupon.isUseable(couponDateUtils.parseDate("2020-04-04"));
        assertThat(validationResult, equalTo(CouponUseValidationResult.EXPIRED));
    }

    @Test
    public void itShouldReturnAlreadyUsedResultWhenCouponAlreadyUsed() throws Exception {

        Coupon coupon = create(
                "A1B1C1", true, true,
                "2020-04-01", "2020-04-04", "bob",
                "2020-04-02 08:00:00", "2020-04-02 20:30:50");

        CouponUseValidationResult validationResult = coupon.isUseable(couponDateUtils.parseDate("2020-04-04"));
        assertThat(validationResult, equalTo(CouponUseValidationResult.ALREADY_USED));
    }

    private Coupon create(String couponCode, boolean used, boolean issued, String validFrom, String expireAt, String user, String issuedAt, String usedAt) throws Exception {
        return new Coupon(
                couponCode,
                used,
                issued,
                couponDateUtils.parseDate(validFrom),
                couponDateUtils.parseDate(expireAt),
                user,
                couponDateUtils.parseDateTime(issuedAt),
                couponDateUtils.parseDateTime(usedAt),
                couponDateUtils.now(),
                couponDateUtils.now()
        );
    }
}