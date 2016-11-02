package com.congwiny.myrxjava.filter;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by congwiny on 2016/11/2.
 * http://blog.chengyunfeng.com/?p=960
 * <p>
 * 到目前为止我们看到的示例都很简单。你也可以用 Rx 来处理大批量实时数据，但是如果把所有大批量数据整个打包发给你的话，使用 Rx 还有啥优势呢？
 * 本节 我们将介绍一些操作函数（operators ）来过滤数据、或者把所有数据变成一个需要的数据。
 * 如果你了解过函数式编程（functional programming）或者 Java 中的 Stream，则本节介绍的操作函数是非常眼熟的。
 * 本节中所有的操作符都返回一个不影响前一个 Observable 的新 Observable。
 * 整个 Rx 框架都遵守该原则。通过创建新的 Observable 来转换之前的 Observable而不会对之前的 Observable 造成干扰。
 * 订阅到初始 Observable 的 Subscribers 不会受到任何影响，但是在后面的章节中也会看到，开发者也需要当心该原则。
 */

public class ObservableFilterTest {
    public static void main(String[] args) throws Exception {
        //弹子图 http://pic.goodev.org/wp-files/2016/03/Cfakepathmarble_diagrams.png

        /**
         * filter 函数使用一个 predicate 函数接口来判断每个发射的值是否能通过这个判断。如果返回 true，则该数据继续往下一个（过滤后的） Observable 发射。
         */

        Observable<Integer> values = Observable.range(0, 10);
        Subscription oddNumbers = values
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer num) {
                        return num % 2 == 0;
                    }
                })
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onError e=" + e);
                    }

                    @Override
                    public void onNext(Integer num) {
                        System.out.println("onNext number=" + num);
                    }
                });
        /**
         * 0
         * 2
         * 4
         * 6
         * 8
         * Completed
         */

        /**
         * distinct 函数用来过滤掉已经出现过的数据了。
         */
        Observable<Integer> numObservable = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onNext(3);
            }
        });

        numObservable.distinct().subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer num) {
                System.out.println("numObservable onNext number=" + num);//result 1 2 3
            }
        });

        /**
         * distinct 还有一个重载函数，该函数有个生成 key 的参数。每个发射的数据都使用该参数生成一个 key，然后使用该key 来判断数据是否一样。
         *
         * “Fourth” 和 “Fifth” 字符串被过滤掉了，应为他们的 key （首字母）和 First 一样。已经发射过的数据将被过滤掉。
         */
        Observable<String> stringObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("First");
                subscriber.onNext("Second");
                subscriber.onNext("Third");
                subscriber.onNext("Fourth");
                subscriber.onNext("Fifth");
            }
        });

        /**
         * 有经验的码农知道，该函数在内部维护一个 key 集合来保存所有已经发射数据的 key，当有新的数据发射的时候，在集合中查找该 数据的key 是否存在。
         * 在使用 Rx 操作函数的时把内部细节给封装起来了，但是我们应该注意该问题来避免性能问题。（如果有大量的数据，维护一个内部的集合来保存 key 可能会占用很多内存。）
         */
        stringObservable
                .distinct(new Func1<String, Character>() {
                    @Override
                    public Character call(String s) {
                        return s.charAt(0);
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("stringObservable onNext s=" + s);
                    }
                });

        /**
         * distinct 还有个变体是 distinctUntilChanged。区别是 distinctUntilChanged 只过滤相邻的 key 一样的数据。
         * 同样 distinctUntilChanged 也可以使用一个生成 key 的参数：
         */
        numObservable
                .distinctUntilChanged()
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer number) {
                        System.out.println("numObservable distinctUntilChanged onNext num=" + number);//1,2,1,2,3
                    }
                });

        /**
         * ignoreElements
         * ignoreElements 会忽略所有发射的数据，只让 onCompleted 和 onError 可以通过。
         *
         * ignoreElements() 和使用 filter(v -> false) 是一样的效果。
         */

        Subscription subscription = values
                .ignoreElements()
                .subscribe(
                        new Subscriber<Integer>() {
                            @Override
                            public void onCompleted() {
                                System.out.println("ignoreElements onCompleted"); //result onCompleted
                            }

                            @Override
                            public void onError(Throwable e) {
                                System.out.println("ignoreElements onError=" + e);
                            }

                            @Override
                            public void onNext(Integer num) {
                                System.out.println("ignoreElements onNext=" + num);
                            }
                        }
                );

        /**
         * skip 和 take
         *
         *下面两个操作函数依据发射数据的索引来在特定的位置切断数据流，可以从头开始切断也可以从末尾开始切断。
         *  take 从头开始获取前 N 个数据，而 skip 则是从头开始 跳过 N 个数据。
         *  注意，如果发射的数据比 N 小，则这两个函数都会发射一个 error。
         */

        values.take(2).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer num) {
                System.out.println("values take onNext=" + num);
            }
        });

        values.skip(8).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer num) {
                System.out.println("values skip onNext=" + num);
            }
        });

        /**
         * 除了根据发射数据的索引来过滤数据以外，还可以使用数据流发射的时间来过滤。比如过滤掉前五秒发射的数据。
         * Observable<T>   take(long time, java.util.concurrent.TimeUnit unit)
         * Observable<T>   skip(long time, java.util.concurrent.TimeUnit unit)
         */

        Observable<Long> intervalValues = Observable.interval(100, TimeUnit.MILLISECONDS);
        intervalValues.take(250, TimeUnit.MILLISECONDS)
                .subscribe(
                        new Subscriber<Long>() {
                            @Override
                            public void onCompleted() {
                                System.out.println("take interval onCompleted");
                            }

                            @Override
                            public void onError(Throwable e) {
                                System.out.println("take interval onError" + e);
                            }

                            @Override
                            public void onNext(Long num) {
                                System.out.println("take interval onNext=" + num);
                            }
                        }
                );

        /**
         * skipWhile 和 takeWhile
         这两个函数是使用一个 predicate 参数来当做判断条件。 如果判断条件返回为 ture， 则 takeWhile 保留该数据。
         */
        values.takeWhile(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer num) {
                return num < 3;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer num) {
                System.out.println("takeWhile onNext=" + num);//result 0,1,2
            }
        });

        values.skipWhile(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer num) {
                return num < 8;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer num) {
                System.out.println("skipWhile onNext=" + num);//result 8,9
            }
        });

        /**
         * skipLast 和 takeLast
         * skip 和 take 是从头开始索引数据，而 skipLast 和 takeLast 和他们相反，是从末尾开始索引数据。
         */

        /**
         * takeUntil 和 skipUntil
         * takeUntil 和 skipUntil 这两个函数和 takeWhile 、skipWhile 刚好相反。 当判断条件为 false 的时候， takeUntil 保留该数据。
         *
         * takeUntil 和 skipUntil 还有另外一种不一样的重载函数。切断的条件为 另外一个 Observable 发射数据的时刻。
         * skipUntil 也是一样，当收到另外一个 Observable 发射数据的时候，就开始接收 源 Observable 的数据。
         *
         */
        Observable<Long> valuesInterval = Observable.interval(100, TimeUnit.MILLISECONDS);
        Observable<Long> cutoff = Observable.timer(250, TimeUnit.MILLISECONDS);

        valuesInterval.takeUntil(cutoff).subscribe(
                new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        System.out.println("values interval onNext=" + aLong);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        System.out.println("values interval onError");
                    }
                },
                new Action0() {
                    @Override
                    public void call() {
                        System.out.println("values interval onCompleted"); //0,1,completed
                    }
                });

        System.in.read();

    }
}
