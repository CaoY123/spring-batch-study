package com.langfeiyes.batch._22_itemreader_flat_mapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String name;
    private int age;
    private String address;   //地址
}