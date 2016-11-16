package com.congwiny.myrxjava.rx0_subject;

import rx.functions.Action0;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

/**
 * http://blog.chengyunfeng.com/?p=948
 * Created by congwiny on 2016/9/29.
 */

/**
 * Subject 是 Observable 的一个扩展，同时还实现了 Observer 接口。
 * 第一眼看上去可能有点奇怪，但是在有些场合下使用 Subject 将会非常便捷。
 * 他们可以像 Observer 一样接收事件，同时还可以像 Observable 一样把接收到的事件再发射出去。
 * 这种特性非常适合 Rx 中的接入点，当你的事件来至于 Rx 框架之外的代码的时候，你可以把这些数据先放到 Subject 中，然后再把 Subject转换为一个 Observable，就可以在 Rx 中使用它们了。
 * 你可以把 Subject 当做 Rx 中的 事件管道。
 * Subject 有两个参数类型：输入参数和输出参数。这样设计是为了抽象而不是应为使用 Subject 是为了转换数据类型。
 * 转换数据应该使用转换操作函数来完成，后面我们将介绍各种操作函数。
 * Subject 有各种不同的具体实现。下面将介绍一些非常重要的实现以及他们之间的区别。
 */
public class SubjectTest {

    public static void main(String[] args) {

        /**
         * PublishSubject 是最直接的一个 Subject。
         * 当一个数据发射到 PublishSubject 中时，PublishSubject 将立刻把这个数据发射到订阅到该 subject 上的所有 subscriber 中。
         *
         * 可以看到，数据 1 并没有打印出来，原因是当我们订阅到 subject 的时候，1 已经发射出去了。
         * 当订阅到 subject 后就开始接收 发射到 subject 中的数据了。
         */
        PublishSubject<Integer> publishSubject = PublishSubject.create();
        publishSubject.onNext(1);
        publishSubject.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println(integer);//result 2 3 4
            }
        });

        publishSubject.onNext(2);
        publishSubject.onNext(3);
        publishSubject.onNext(4);

        /**
         * ReplaySubject 可以缓存所有发射给他的数据。
         * 当一个新的订阅者订阅的时候，缓存的所有数据都会发射给这个订阅者。
         * 由于使用了缓存，所以每个订阅者都会收到所以的数据：
         */
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();
        replaySubject.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("replay1----" + integer);//result 0 1 2
            }
        });

        replaySubject.onNext(0);
        replaySubject.onNext(1);

        replaySubject.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("replay2----" + integer);//result 0 1 2
            }
        });

        replaySubject.onNext(2);

        /**
         * 不管是何时订阅的，每个订阅者都收到了所有的数据。注意后一个订阅者在处理 2 之前就先收到了之前发射的数据 0和1.
         * 缓存所有的数据并不是一个十分理想的情况，如果 Observable 事件流运行很长时间，则缓存所有的数据会消耗很多内存。
         * 可以限制缓存数据的数量和时间。
         * ReplaySubject.createWithSize 限制缓存多少个数据；ReplaySubject.createWithSize(2);
         * 而 ReplaySubject.createWithTime 限制一个数据可以在缓存中保留多长时间。
         * ReplaySubject.createWithTime(150, TimeUnit.MILLISECONDS,Schedulers.immediate());
         */

        //-----------------------------------------
        /**
         * BehaviorSubject 只保留最后一个值。 等同于限制 ReplaySubject 的个数为 1 的情况。
         * 在创建的时候可以指定一个初始值，这样可以确保当订阅者订阅的时候可以立刻收到一个值。
         */
        BehaviorSubject<Integer> s = BehaviorSubject.create();
        s.onNext(0);
        s.onNext(1);
        s.onNext(2);
        s.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("behavior----" + integer);//2,3
            }
        });
        s.onNext(3);

        //-----------------------------------

        BehaviorSubject<Integer> s2 = BehaviorSubject.create();
        s2.onNext(0);
        s2.onNext(1);
        s2.onNext(2);
        s2.onCompleted();//注释掉这个输入2
        //三个构造参数
        s2.subscribe(
                new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("BehaviorSubject----onNext" + integer);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("BehaviorSubject----onError");
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        System.out.println("BehaviorSubject----onCompleted");//只输出onCompleted
                    }
                });

        //----------------------------
        //下面使用了默认初始化值，如果订阅者的发射数据之前就订阅了，则会收到这个初始化的值：

        BehaviorSubject<Integer> s3 = BehaviorSubject.create(0);
        s3.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("BehaviorSubject----onNext S3= " + integer); //result 0 1
            }
        });
        s3.onNext(1);//输出0,1
        //----------------------------
        /**
         * AsyncSubject 也缓存最后一个数据。
         * 区别是 AsyncSubject 只有当数据发送完成时（onCompleted 调用的时候）才发射这个缓存的最后一个数据。
         * 可以使用 AsyncSubject 发射一个数据并立刻结束。
         */

        AsyncSubject<Integer> s4 = AsyncSubject.create();
        s4.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("AsyncSubject----onNext S4=" + integer);//result 2
            }
        });
        s4.onNext(0);
        s4.onNext(1);
        s4.onNext(2);
        s4.onCompleted();//如果不调用 s4.onCompleted(); 则什么结果都不会打印出来。最后一个数据，要等Subject结束了

        /**
         *Rx 中有一些隐含的规则在代码中并不太容易看到。
         * 一个重要的规则就是当一个事件流结束（onError 或者 onCompleted 都会导致事件流结束）后就不会发射任何数据了。
         * 这些 Subject 的实现都遵守这个规则，subscribe 函数也拒绝违反该规则的情况。
         *
         * 但是在 Rx 实现中并没有完全确保这个规则，所以你在使用 Rx 的过程中要注意遵守该规则，否则会出现意料不到的情况。
         */
        Subject<Integer, Integer> s5= ReplaySubject.create();
        s5.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.println("ReplaySubject----onNext S5=" + integer); //result 0
            }
        });
        s5.onNext(0);
        s5.onCompleted();
        s5.onNext(1);
        s5.onNext(2);


    }
}
