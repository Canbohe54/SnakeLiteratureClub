package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Expert extends BaseUser implements Serializable {
}
