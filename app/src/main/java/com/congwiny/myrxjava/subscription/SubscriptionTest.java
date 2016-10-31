package com.congwiny.myrxjava.subscription;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;
import rx.subscriptions.Subscriptions;

/**
 * Created by congwiny on 2016/10/31.
 * <p>
 * Rx背后的理念是，无法知道时间事件流何时发射数据、也不知道何时结束发射。
 * 但你要控制何时开始和结束接受事件。
 * <p>
 * 订阅者可能使用了一些资源，这些资源需要在停止接收事件的时候释放。
 * 通过Subscription可以实现生命周期管理
 */

public class SubscriptionTest {

    public static void main(String[] args) {

        //Subscribing订阅事件
        /**
         * Observable.subscribe有6个重载函数
         *
         * 只订阅事件，不处理事件
         * Subscription    subscribe()
         * ---------
         * Subscription    subscribe(Action1<? super T> onNext)
         * Subscription    subscribe(Action1<? super T> onNext, Action1<java.lang.Throwable> onError)
         * Subscription    subscribe(Action1<? super T> onNext, Action1<java.lang.Throwable> onError, Action0 onComplete)
         * Subscription    subscribe(Observer<? super T> observer)
         * Subscription    subscribe(Subscriber<? super T> subscriber)
         */

        /**
         * 在事件流停止发射之前，可以主动停止接收事件
         * 每个subscribe函数都会返回一个Subscription
         * Subscription包含两个函数
         * Subscription#isUnsubscribed,unSubscribe
         *
         * 只要调用unSubscribe函数就终止了事件的订阅，停止接收数据
         */

        Subject<Integer, Integer> values = ReplaySubject.create();
        Subscription subscription = values.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("onNext integer="+integer);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("onError ="+throwable);
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        System.out.println("onComplete");
                    }
                }
        );
        values.onNext(0);
        values.onNext(1);
        subscription.unsubscribe();
        values.onNext(2);//0,1

        /**
         * 一个Observer调用unSubscribe取消监听Observable后，不影响同一个Observable上的其他Observer监听
         */

        Subject<Integer, Integer> values2 = ReplaySubject.create();
        Subscription subscription1 = values2.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("first integer="+integer);
                    }
                }
        );

        Subscription subscription2 = values2.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("second integer="+integer);
                    }
                }
        );
        /*  result
            first integer=0
            second integer=0
            first integer=1
            second integer=1
            second integer=2
         */
        values2.onNext(0);
        values2.onNext(1);
        subscription1.unsubscribe();
        values2.onNext(2);

        /**
         * onError和onCompleted
         *
         * onError和onCompleted意味着结束事件流，observable应该遵守该规则。
         * 在onError或者onComplete发生之后，就不应该再发射事件。
         */

        Subject<Integer, Integer> values3 = ReplaySubject.create();
        Subscription subscription3 = values3.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("third integer=" + integer);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("third onError=" + throwable);
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        System.out.println("third onComplete");
                    }
                }
        );
        values3.onNext(0);
        values3.onNext(1);
        values3.onCompleted();
        values3.onNext(2);
        /**
         * result
         * third integer=0
         * third integer=1
         * third onComplete
         */
//----------------------------
        /**
         * 释放资源
         * Subsciption和其使用的资源绑定在一起。
         * 所以你应该记得释放Subscription来释放资源。
         *
         * Subscriptions.create 函数需要一个 Action 接口类型参数，
         * 在 unsubscribe 调用的时候会执行该接口来释放资源。
         *
         * 作用我猜应该是最后的释放资源
         */
        Subscription sub = Subscriptions.create(new Action0() {
            @Override
            public void call() {
                System.out.println("clean");
            }
        });
        sub.unsubscribe();

        /**
         * Subscriptions.create 函数需要一个 Action 接口类型参数，在 unsubscribe 调用的时候会执行该接口来释放资源。 也有其他一些函数可以简化开发：
         – Subscriptions.empty() 返回一个当 unsubscribe 的时候 啥也不做的Subscription 。当要求你返回一个 Subscription ，但是你确没有资源需要释放，则可以返回这个空的 Subscription。
         – Subscriptions.from(Subscription… subscriptions)，返回的 Subscription 释放的时候，会调用所有参数 Subscription 的 unsubscribe 函数。
         – Subscriptions.unsubscribed() 返回一个已经释放过的 Subscription。

         Subscription 也有一些标准的实现：
         – BooleanSubscription
         – CompositeSubscription
         - MainThreadSubscription
         – MultipleAssignmentSubscription
         – RefCountSubscription
         – SafeSubscriber
         – Scheduler.Worker
         – SerializedSubscriber
         – SerialSubscription
         – Subscriber
         – TestSubscriber
         在后面将会看到他们的使用方式。这里注意 Subscriber 同时也实现了 Subscription。所以我们也可以直接用 Subscriber 来取消监听。
         */

    }
}
