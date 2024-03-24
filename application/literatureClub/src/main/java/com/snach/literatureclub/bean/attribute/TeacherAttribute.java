package com.snach.literatureclub.bean.attribute;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.annotation.AttributeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeType(Identity.TEACHER)
public class TeacherAttribute extends UserAttribute{
    @JsonProperty("professional_title") private String professionalTitle;
}
