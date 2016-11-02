package com.congwiny.myrxjava.factory;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;

/**
 * Created by congwiny on 2016/10/11.
 * <p>
 * Observable有很多工厂方法可以创建一个事件流
 */

public class ObservableOp {

    public static void main(String[] args) throws Exception {

        /**
         * Observable.just
         * just函数创建一个发射预定义好的数据的Observable，发射完这些数据后，事件流也就结束了。
         */
        Observable<String> justObservable = Observable.just("one", "two", "three");
        justObservable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("just onComplete");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("just onError");
            }

            @Override
            public void onNext(String s) {
                System.out.println("just onNext" + s);
            }
        });

        /**
         * empty
         * 这个函数创建的 Observable 只发射一个 onCompleted 事件就结束了。
         */

        Observable<String> emptyObservable = Observable.empty();
        emptyObservable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("empty onComplete");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("empty onError");
            }

            @Override
            public void onNext(String s) {
                System.out.println("empty onComplete");
            }
        });

        /**
         * never
         * 这个 Observable 将不会发射任何事件和数据。
         */
        Observable<String> neverObservable = Observable.never();
        neverObservable.subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                System.out.println("never onComplete");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("never onError");
            }

            @Override
            public void onNext(String s) {
                System.out.println("never onComplete");
            }
        });


        /**
         * error
         * 这个 Observable 将会发射一个 error 事件，然后结束。
         */
        Observable<String> values = Observable.error(new Exception("Oops"));
        Subscription subscription = values.subscribe(
                new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("Received: " + s);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("Error: " + throwable);
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        System.out.println("Completed");
                    }
                }
        );

        /**
         * 两个 subscriber 相隔 1秒订阅这个 Observable，但是他们收到的时间数据是一样的！
         * 这是因为当订阅的时候，时间数据只调用一次。
         * */
        Observable<Long> now = Observable.just(System.currentTimeMillis());

        now.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println("now just action1=" + aLong);
            }
        });
        Thread.sleep(1000);
        now.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println("now just action2=" + aLong);
            }
        });

        /**
         * just action1=1476180989329
         * just action2=1476180989329
         */

        /**
         * 其实你希望的是，当 一个 subscriber 订阅的时候才去获取当前的时间。
         *
         * defer 的参数是一个返回一个 Observable 对象的函数。
         * 该函数返回的 Observable 对象就是 defer 返回的 Observable 对象。
         * 重点是，每当一个新的 Subscriber 订阅的时候，这个函数就重新执行一次。
         */


        Observable<Long> now2 = Observable.defer(new Func0<Observable<Long>>() {
            @Override
            public Observable<Long> call() {
                return Observable.just(System.currentTimeMillis());
            }
        });

        now2.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println("now2 just action2=" + aLong);
            }
        });
        Thread.sleep(1000);
        now2.subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                System.out.println("now2 just action2=" + aLong);
            }
        });
        /**
         * now2 just action2=1476181655172
         * now2 just action2=1476181656181
         */


        /**
         * 上面的代码在最后有个 System.in.read(); 阻塞语句，这个语句是有必要的，不然的话，程序不会打印任何内容就退出了。
         * 原因是我们的操作不是阻塞的：我们创建了一个每隔一段时间就发射数据的 Observable，然后我们注册了一个 Subscriber 来打印收到的数据。
         * 这两个操作都是非阻塞的，而 发射数据的计时器是运行在另外一个线程的，但是这个线程不会阻止 JVM 结束当前的程序，
         * 所以 如果没有 System.in.read(); 这个阻塞操作，还没发射数据则程序就已经结束运行了。
         */
        Observable<Long> values2 = Observable.interval(1000, TimeUnit.MILLISECONDS);//如果我们不调用 unsubscribe 的话，这个序列是不会停止的。

        values2.subscribe(
                new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        System.out.println("Received: " + aLong);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("throwable: " + throwable);
                    }
                }
                , new Action0() {
                    @Override
                    public void call() {
                        System.out.println("completed");
                    }
                }
        );

        //阻塞下主线程，interval内容让打印出来
        //System.in.read();

        /**
         *  Observable.timer 有两个重载函数。
         *
         *  定时任务
         *
         *  创建了一个 Observable，该 Observable 等待一段时间，然后发射数据 0 ，然后就结束了。
         *
         */
        Observable<Long> values3 = Observable.timer(1000, TimeUnit.MILLISECONDS);

        values3.subscribe(
                new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        System.out.println("Received: " + aLong);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("throwable: " + throwable);
                    }
                }
                , new Action0() {
                    @Override
                    public void call() {
                        System.out.println("completed");
                    }
                }
        );

        /**
         * 先等待一段时间，然后开始按照间隔的时间一直发射数据：
         *
         * @deprecated use {@link #interval(long, long, TimeUnit)} instead
         */
        Observable<Long> values4 = Observable.timer(2000, 1000, TimeUnit.MILLISECONDS);

        values4.subscribe(
                new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        System.out.println("Received: " + aLong);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("throwable: " + throwable);
                    }
                }
                , new Action0() {
                    @Override
                    public void call() {
                        System.out.println("completed");
                    }
                }
        );


    }

}
