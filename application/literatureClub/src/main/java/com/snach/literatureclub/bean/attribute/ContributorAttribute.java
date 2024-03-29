package com.snach.literatureclub.bean.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.annotation.AttributeType;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeType(Identity.CONTRIBUTOR)
public class ContributorAttribute extends UserAttribute {
    private String grade;
}
