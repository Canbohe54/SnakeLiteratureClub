package com.snach.literatureclub.bean.attribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.annotation.AttributeType;
import com.snach.literatureclub.utils.AnnotationHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserAttributeFactory {
    private static final Map<Identity, Class<? extends UserAttribute>> attrClassMap;

    private static final ObjectMapper objectMapper;

    static {
        attrClassMap = new HashMap<>();
        List<Class<?>> classes = AnnotationHandler.getClassWithAnnotation(AttributeType.class);
        for (Class<?> clazz : classes) {
            attrClassMap.put(clazz.getAnnotation(AttributeType.class).value(), clazz.asSubclass(UserAttribute.class));
        }

        objectMapper = new ObjectMapper();
    }

    public static UserAttribute load(Identity identity, String attrJson) {
        Class<? extends UserAttribute> clazz = attrClassMap.get(identity);
        UserAttribute attributes;
        try {
            attributes = objectMapper.readValue(attrJson, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return attributes;
    }

    public static String dump(UserAttribute attributes) {
        String attrJson;
        try {
            attrJson = objectMapper.writeValueAsString(attributes);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return attrJson;
    }
}
