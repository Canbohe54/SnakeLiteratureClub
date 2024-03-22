package com.snach.literatureclub.bean.attribute;

import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.annotation.AttributeType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Data
@NoArgsConstructor
@AttributeType(Identity.VISITOR)
public abstract class UserAttribute {
    public <T> T get(String name) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                Object object;
                try {
                    field.setAccessible(true);
                    object = field.get(this);
                    return (T) object;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public <T> boolean set(String name, T value) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                Object object = null;
                try {
                    field.setAccessible(true);
                    field.set(this, value);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
