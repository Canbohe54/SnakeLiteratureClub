package com.snach.literatureclub.bean.attribute;

import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.annotation.AttributeType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AttributeType(Identity.VISITOR)
public abstract class UserAttribute {
}
