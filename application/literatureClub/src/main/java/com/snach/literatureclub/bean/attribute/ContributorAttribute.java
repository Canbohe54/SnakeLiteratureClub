package com.snach.literatureclub.bean.attribute;

import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.annotation.AttributeType;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeType(Identity.学生)
public class ContributorAttribute extends UserAttribute {
    private String grade;
}
