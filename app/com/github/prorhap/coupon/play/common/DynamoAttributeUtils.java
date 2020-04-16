package com.github.prorhap.coupon.play.common;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class DynamoAttributeUtils {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String NULL_STRING = "NULL";

    public String getString(Map<String, AttributeValue> item, String key) {
        return item.get(key).s().equals(NULL_STRING) ? "" : item.get(key).s();
    }

    public Boolean getBoolean(Map<String, AttributeValue> item, String key) {
        return item.get(key).bool();
    }

    public Date getDate(Map<String, AttributeValue> item, String key, String format) {
        try {
            return (item.get(key).s().equals(NULL_STRING) ? null : DateUtils.parseDate(item.get(key).s(), format));
        } catch (ParseException e) {
            logger.error("Error while paring date. Return now()");
            return null;
        }
    }

    public AttributeValue create(String value) {
        return StringUtils.isEmpty(value) ? AttributeValue.builder().s(NULL_STRING).build() : AttributeValue.builder().s(value).build();
    }

    public AttributeValue create(boolean value) {
        return AttributeValue.builder().bool(value).build();
    }

    public AttributeValue create(int value) {
        return AttributeValue.builder().n(String.valueOf(value)).build();
    }

    public AttributeValue create(Date date, String format) {
        return Objects.isNull(date) ?
                AttributeValue.builder().s(NULL_STRING).build() :
                AttributeValue.builder().s(new SimpleDateFormat(format).format(date)).build();
    }

    public AttributeValueUpdate createUpdate(AttributeValue attributeValue) {
        return AttributeValueUpdate.builder().value(attributeValue).action(AttributeAction.PUT).build();
    }
}
