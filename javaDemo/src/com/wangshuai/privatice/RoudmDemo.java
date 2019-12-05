package com.wangshuai.privatice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RoudmDemo {
    public static void main(String[] args) {
        //创建一个对象
        Random df = new Random();
        //引用nextInt()方法
        int number = df.nextInt(101);
        //输出number
        System.out.println("" + number);
        List<String> list = Collections.synchronizedList(new ArrayList<>());
    }
}
