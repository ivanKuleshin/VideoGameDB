package com.ai.tester.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XmlUtil {

    private static final XmlMapper XML_MAPPER = new XmlMapper();

    public static <T> T parse(String xml, Class<T> type) {
        try {
            return XML_MAPPER.readValue(xml, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String serialize(Object obj) {
        try {
            return XML_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize object to XML", e);
        }
    }
}

