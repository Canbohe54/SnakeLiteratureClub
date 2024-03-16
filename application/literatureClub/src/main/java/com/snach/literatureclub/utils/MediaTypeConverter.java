package com.snach.literatureclub.utils;

import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class MediaTypeConverter {
    static private final Map<String, MediaType> stringMediaTypeMap = new HashMap<>();
    static {
        stringMediaTypeMap.put("text/plain", MediaType.TEXT_PLAIN);
        stringMediaTypeMap.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", MediaType.APPLICATION_OCTET_STREAM);

    }

    static public MediaType getMediaType(String value){
        return stringMediaTypeMap.get(value);
    }
}
