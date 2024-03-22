package com.snach.literatureclub.bean.attribute;

import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.annotation.AttributeType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AttributeType(Identity.AUDITOR)
public class AuditorAttribute extends  UserAttribute{
    private String occupation;
}
