package com.snach.literatureclub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Grade implements Serializable {
    String expert_id;
    String article_id;
    int garde_expr;
    int grade_struct;
    int grade_theme;
    int grade_all;
    String advice;
}
