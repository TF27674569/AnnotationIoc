package com.formssi.annotationioc.test;

import java.io.Serializable;

/**
 * Description :
 * <p/>
 * Created : TIAN FENG
 * Date : 2017/5/27
 * Email : 27674569@qq.com
 * Version : 1.0
 */


public class Preson implements Serializable {
    public String name;
    public int age;

    public Preson(String name, int age) {
        this.age = age;
        this.name = name;
    }

    @Override
    public String toString() {
        return "name = "+name+",age = "+age;
    }
}
