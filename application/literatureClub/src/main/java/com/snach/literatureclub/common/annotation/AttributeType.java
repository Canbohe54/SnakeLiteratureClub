package com.snach.literatureclub.common.annotation;

import com.snach.literatureclub.common.Identity;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Component
public @interface AttributeType {
    Identity value();
}
