package com.snach.literatureclub.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Message {
    String id;
    String from;
    String to;
    String message;
}
