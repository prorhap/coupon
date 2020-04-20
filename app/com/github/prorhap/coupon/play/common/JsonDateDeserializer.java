package com.github.prorhap.coupon.play.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

public class JsonDateDeserializer extends JsonDeserializer<Date> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private FastDateFormat format = FastDateFormat.getInstance("yyyy-MM-dd");

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            return format.parse(jsonParser.getText());
        } catch (ParseException e) {
            logger.error("Error while deserialize "+jsonParser.getText(), e);
            throw new RuntimeException(e);
        }
    }
}
