package com.congwiny.myrxjava.aggregation;

import rx.Subscriber;

/**
 * Created by congwiny on 2016/11/8.
 *
 * 如果你从头开始阅读本系列教程，则会发现前面代码中有很多重复的地方。
 * 为了避免重复代码并且使代码更加简洁，方便我们聚焦要介绍的函数，
 * 从本节开始在示例代码中会引入一个自定义的 Subscriber 。
 * 该 Subscribe 用来订阅 Observable 并打印结果：
 *
 * 很简单的一个自定义实现，打印每个事件并使用一个 TAG 来标记是那个 Subscriber.
 */

public class PrintSubscriber extends Subscriber {
    private final String name;

    public PrintSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onCompleted() {
        System.out.println(name + ": Completed");
    }

    @Override
    public void onError(Throwable e) {
        System.out.println(name + ": Error: " + e);
    }

    @Override
    public void onNext(Object o) {
        System.out.println(name + ": " + o);
    }
}
