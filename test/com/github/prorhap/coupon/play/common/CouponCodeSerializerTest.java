package com.github.prorhap.coupon.play.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CouponCodeSerializerTest {

    private CouponCodeSerializer serializer;

    @Before
    public void setUp() {
        serializer = new CouponCodeSerializer();
    }

    @Test
    public void itShouldSerializeCodeCodeWithCorrectFormat() throws Exception {

        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        String rawCouponCode = "1234567890ABCDEFGHI";

        serializer.serialize(rawCouponCode, jsonGenerator, serializerProvider);

        verify(jsonGenerator).writeString("12345-67890A-BCDEFGHI");
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowExceptionTheCouponCodeLengthIsShort() throws Exception {
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        String rawCouponCode = "123";

        serializer.serialize(rawCouponCode, jsonGenerator, serializerProvider);
    }
}
