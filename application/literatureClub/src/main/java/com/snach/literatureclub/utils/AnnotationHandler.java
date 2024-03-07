package com.snach.literatureclub.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AnnotationHandler implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AnnotationHandler.applicationContext = applicationContext;
    }

    public static List<Class<?>> getClassWithAnnotation(Class<? extends Annotation> annotation) {
        Map<String, Object> beansWithAnnotationMap = applicationContext.getBeansWithAnnotation(annotation);
        List<Class<?>> classes = new ArrayList<>();
        for (Map.Entry<String, Object> entry : beansWithAnnotationMap.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            classes.add(clazz);
        }
        return classes;
    }
}
