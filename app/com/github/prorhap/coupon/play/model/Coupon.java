package com.github.prorhap.coupon.play.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    private String couponCode;

    private boolean used;
    private boolean issued;

    private Date validFrom;
    private Date expireAt;

    private String userId;

    private Date issuedAt;
    private Date usedAt;

    private Date createdAt;
    private Date modifiedAt;

    public Coupon(String couponCode, boolean used, boolean issued, Date validFrom, Date expireAt, String userId, Date issuedAt, Date usedAt, Date createdAt, Date modifiedAt) {
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.couponCode = couponCode;
        this.used = used;
        this.issued = issued;
        this.validFrom = validFrom;
        this.expireAt = expireAt;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
    }

    public CouponIssueValidationResult isIssueable(Date now) {
        if(isUsed()) {
            return CouponIssueValidationResult.ALREADY_USED;
        }else if(isIssued()) {
            return CouponIssueValidationResult.ALREADY_ISSUED;
        }else if(!now.before(expireAt)) {
            return CouponIssueValidationResult.ALREADY_EXPIRED;
        }
        return CouponIssueValidationResult.OK;
    }

    public CouponUseValidationResult isUseable(Date now) {
        if (isUsed()) {
            return CouponUseValidationResult.ALREADY_USED;
        } else if (!now.before(expireAt)) {
            return CouponUseValidationResult.EXPIRED;
        } else if (now.before(validFrom)) {
            return CouponUseValidationResult.NOT_STARTED;
        } else if (!isIssued()) {
            return CouponUseValidationResult.NOT_ISSUED;
        }
        return CouponUseValidationResult.OK;
    }

    public CouponCancelValidationResult isCancelable(Date now) {
        if (isUsed()) {
            return CouponCancelValidationResult.ALREADY_USED;
        }else if(!isIssued()) {
            return CouponCancelValidationResult.NOT_ISSUED;
        }else if(!now.before(expireAt)) {
            return CouponCancelValidationResult.ALREADY_EXPIRED;
        }
        return CouponCancelValidationResult.OK;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "couponCode='" + couponCode + '\'' +
                ", used=" + used +
                ", issued=" + issued +
                ", validFrom=" + validFrom +
                ", expireAt=" + expireAt +
                ", userId='" + userId + '\'' +
                ", issuedAt=" + issuedAt +
                ", usedAt=" + usedAt +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }


}
