package com.congwiny.myrxjava.map;

/**
 * Created by congwiny on 2016/8/31.
 * 由于 Java 的限制，无法把函数作为参数， 所以需要用一个接口（或者类）并在里面定义一个转换函数：
 */
public interface Func<T, R> {
    //有两个泛型类型定义， T 代表参数的类型； R 代表返回值的类型。
    R call(T t);
}
