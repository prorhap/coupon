package com.github.prorhap.coupon.play.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.prorhap.coupon.play.common.JsonDateDeserializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateCouponRequest {

    private int amount;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date validFrom;

    @JsonDeserialize(using = JsonDateDeserializer.class)
    private Date expireAt;

    public static CreateCouponRequest create() {
        CreateCouponRequest createCouponRequest = new CreateCouponRequest();
        createCouponRequest.setAmount(1);
        createCouponRequest.setValidFrom(new Date());
        createCouponRequest.setExpireAt(new Date());

        return createCouponRequest;
    }
}
