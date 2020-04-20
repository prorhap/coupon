package com.github.prorhap.coupon.play.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import junit.framework.TestCase;
import org.exparity.hamcrest.date.DateMatchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonDateDeserializerTest {

    private JsonDateDeserializer deserializer;

    @Before
    public void setUp() {
        deserializer = new JsonDateDeserializer();
    }

    @Test
    public void itShouldDeserializeToDateWhenCorrectJsonIsProvided() throws Exception {

        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext deserializationContext = mock(DeserializationContext.class);
        Date expected = createDate(2020, Calendar.APRIL , 1,0,0,0);

        when(jsonParser.getText()).thenReturn("2020-04-01");

        Date result = deserializer.deserialize(jsonParser, deserializationContext);

        assertThat(result, DateMatchers.sameDay(expected));
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowExceptionWhenIncorrectDateFormatIsProvided() throws Exception {

        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext deserializationContext = mock(DeserializationContext.class);
        Date expected = createDate(2020, Calendar.APRIL , 1,0,0,0);

        when(jsonParser.getText()).thenReturn("20200401010213");

        deserializer.deserialize(jsonParser, deserializationContext);

    }

    private Date createDate(int year, int month, int date, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, hourOfDay, minute, second);
        return calendar.getTime();
    }
}
