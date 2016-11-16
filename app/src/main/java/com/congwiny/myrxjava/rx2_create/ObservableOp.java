package com.congwiny.myrxjava.rx2_create;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;

/**
 * Created by congwiny on 2016/10/11.
 * http://blog.chengyunfeng.com/?p=959
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

        /**
         *Observable.from
         * 在 Java 并发框架中经常使用 Future 来获取异步结果。
         * 通过使用 from 可以把 Future 的结果发射到 Observable 中：
         *
         * 当 FutureTask 执行完后， Observable 发射 Future 获取到的结果然后结束。
         * 如果任务 取消了，则 Observable 会发射一个 java.util.concurrent.CancellationException 错误信息。
         *
         * 当过了超时时间后， Future 还是没有返回结果， Observable 可以忽略其结果并发射一个 TimeoutException。
         */
        FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(2000);
                return 21;
            }
        });
        //执行
        new Thread(futureTask).start();
        Observable<Integer> futureValues = Observable.from(futureTask,3000,TimeUnit.MILLISECONDS);
        //订阅
        futureValues.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                System.out.print("future onNext = "+integer);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                System.out.print("future onError = "+throwable);
            }
        }, new Action0() {
            @Override
            public void call() {
                System.out.print("future onComplete");
            }
        });

        /**
         * Observable.create
         *  对Observable和Subject。。。下面有段话不太理解，以后去看看吧
         *
         * 当有 Subscriber 订阅到这个 Observable 时（上面示例中的 values ），这个 Subscriber 对象就是你实现的函数中的参数 Subscriber。
         * 然后你可以在你的代码中把数据发射到这个 subscriber 中。注意，当数据发射完后，你需要手工的调用 onCompleted 来表明发射完成了。
         如果之前的所有方法都不满足你的要求时，这个函数应当作为你创建自定义 Observable 的最佳方式。
         其实现方式和 第一部分我们通过 Subject 来发射事件类似，但是有几点非常重要的区别。首先：数据源被封装起来了，并和不相关的代码隔离开了。
         其次：Subject 有一些不太明显的问题，通过使用 Subject 你自己在管理状态，并且任何访问该 Subject 对象的人都可以往里面发送数据然后改变事件流。
         还一个主要的区别是执行代码的时机，使用 create 创建的 Observable，当 Observable 创建的时候，你的函数还没有执行，只有当有 Subscriber 订阅的时候才执行。
         这就意味着每次当有 Subscriber 订阅的时候，该函数就执行一次。和 defer 的功能类似。
         结果和 ReplaySubject 类似， ReplaySubject 会缓存结果 当有新的 Subscriber 订阅的时候，把缓存的结果在发射给新的 Subscriber。
         如果要使用 ReplaySubject 来实现和 create 类似的功能，如果 create 中创建数据的函数是阻塞的话，则 ReplaySubject 在创建的时候线程会阻塞住知道 创建函数执行完。
         如果不想阻塞当前线程的话，则需要手工创建一个线程来初始化数据。其实 Rx 有更加优雅的方式解决这个问题。
         其实使用 Observable.create 可以实现 前面几个工厂方法的功能。比如 上面的 create 函数的功能和 Observable.just(“hello”) 的功能是一样的。

         */


    }

}
