package com.snach.literatureclub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Attributes:
 *     textOn(String): BaseText.id, Comment to whom(a ROOT Comment or an Article)
 *     reply (String): if `textOn` is a Comment, `reply` is the Comment that it actually replies to
 *     <p>-----------------Example-----------------</p>
 *     article
 *        |--> rootComment{textOn=article.id, reply=null}
 *        |        |--> subComment_1 {textOn=rootComment.id, reply=comment_1.id}
 *        |        |       |--> comment_3 {textOn=rootComment.id, reply=comment_2.id}
 *        |        |--> subComment_2 {textOn=rootComment.id, reply=comment_1.id}
 *        |--> ...
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Comment extends Text {

    String textOn;

    String reply;
}
