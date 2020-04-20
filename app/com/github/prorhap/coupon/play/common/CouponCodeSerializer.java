package com.github.prorhap.coupon.play.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CouponCodeSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        try {
            jsonGenerator.writeString(new StringBuffer(value).insert(11, '-').insert(5, '-').toString());
        }catch (RuntimeException e) {
            throw new RuntimeException("Illegal coupon code length = "+value);
        }
    }
}
