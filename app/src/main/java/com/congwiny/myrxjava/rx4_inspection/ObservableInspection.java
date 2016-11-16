package com.congwiny.myrxjava.rx4_inspection;


import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * http://blog.chengyunfeng.com/?p=961
 * Created by congwiny on 2016/11/7.
 * <p>
 * 前面一节介绍了如何过滤掉我们不关心的数据。有时候我们需要了解该数据流中的数据是否符合某一条件。
 * 本节来介绍一些检查数据流中数据的函数。
 */

public class ObservableInspection {
    public static void main(String[] args) throws Exception {
        /**
         * all函数用来判断observable中发射的所有数据是否都满足一个条件
         *
         * all 函数返回的是一个发射一个 布尔值的 Observable，而不是直接返回一个 布尔值。
         * 原因在于我们并不知道源 Observable 何时才结束数据流的发射，
         * 只有当源 Observable 发射结束的时候， all 函数才知道结果是否都满足条件。
         * 只要遇到一个不满足条件的数据，all 函数就立刻返回 false。
         * 只有当源 Observable 结束发射并且所发射的所有数据都满足条件的时候才会产生 true。
         * 在 observable 内返回结果可以方便的实现非阻塞操作。
         *
         * 如果源 Observable 出现了错误，则 all 操作就没有意义了，all 会直接发射一个 error 然后结束
         *
         * 如果源 Observable 在出错之前就发射了一个不满足条件的数据，
         * 则 源 Observable 的错误对 all 没有影响（ all 遇到不满足条件的数据就结束了，结束的Observable 无法再继续发射数据了）。
         */
        Observable<Integer> values = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(0);
                subscriber.onNext(2);
                subscriber.onNext(4);
                subscriber.onNext(6);
                subscriber.onCompleted();
            }
        });

        values.all(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer % 2 == 0;
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("all operator onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("all operator onError=" + e);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                System.out.println("all operator onNext=" + aBoolean);//true,onCompleted
            }
        });

        /**
         * 在下个示例中可以看到 all 在遇到不满足的数据的时候就立刻结束了。
         */
        Observable<Long> longObservable = Observable.interval(150, TimeUnit.MILLISECONDS).take(5);
        longObservable.all(new Func1<Long, Boolean>() {
            @Override
            public Boolean call(Long aLong) {
                return aLong < 3;
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("All operator onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("All operator onError=" + e);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                System.out.println("All operator onNext=" + aBoolean);
            }
        });

        longObservable.subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
                System.out.println("take operator onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("take operator onError=" + e);
            }

            @Override
            public void onNext(Long along) {
                System.out.println("take operator onNext=" + along);
            }
        });
        /**
         * take operator onNext=0
         take operator onNext=1
         take operator onNext=2
         All operator onNext=false
         All operator onCompleted
         take operator onNext=3
         take operator onNext=4
         take operator onCompleted
         */


        /**
         * exists
         * 如果源 exists 发射的数据中有一个满足条件，则 exists 就返回 true。
         * exists 和 all 一样也是返回一个 Observable 而不是直接返回 布尔值。
         */
        Observable<Integer> rangeObservable = Observable.range(0, 4);
        rangeObservable.exists(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer >= 3;
            }
        }).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                System.out.println("exists operator onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("exists operator onError=" + e);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                System.out.println("exists operator onNext=" + aBoolean);
            }
        });
        /**
         * result
         * exists operator onNext=true
         exists operator onCompleted
         */

        /**
         * isEmpty
         * 判断一个 Observable 是否是空的，也就是没有发射任何数据就结束了。
         * 只要源 Observable 发射了一个数据，isEmpty 就立刻返回 false，
         * 只有当源 Observable 完成了并且没有发射数据，isEmpty 才返回 true。
         */

        Observable<Long> timerObservable = Observable.timer(1000, TimeUnit.MILLISECONDS);
        timerObservable
                .isEmpty()
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("isEmpty operator onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("isEmpty operator onError");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        System.out.println("isEmpty operator onNext=" + aBoolean);
                    }
                });
        /**
         * contains
         * contains 使用 Object.equals 函数来判断源 Observable 是否发射了相同的数据。
         * 只要遇到相同的数据，则 contains 就立刻返回。
         *
         * contains onNext operator onNext=true
         * contains operator onCompleted
         *
         * 注意上面使用的是 contains(4L)， 而不是 contains(4)， 由于 values 是 Observable 类型的，
         * 所以需要使用 Long 类型而不能是 Integer 类型。
         *
         * 如果使用 contains(4) 则什么都不会打印出来， 由于 values 是一个无限的数据流，
         * 所以 contains 一直在等待一个相同的数据发射出来，
         * 但是在 values 里面是没有一样的数据的，导致 contains 一直等待下去。
         */
        longObservable
                .contains(4L)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("contains operator onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("contains onError operator onError");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        System.out.println("contains onNext operator onNext=" + aBoolean);
                    }
                });
        /**
         * defaultIfEmpty
         * 如果你不想单独处理没有发射任何数据的情况（需要用 isEmpty 函数来检查是否为空），
         * 则可以使用 defaultIfEmpty 函数来强制一个空的 Observable 发射一个默认数据。
         *
         * 只有当 onCompleted 事件发生了，并且 Observable 没有发射任何数据的时候，才会使用默认值；
         * 否则不会使用默认值。 如果发生了错误，则还会有错误的结果。
         */
        Observable<Integer> emptyObservable = Observable.empty();
        emptyObservable.defaultIfEmpty(2)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("defaultIfEmpty operator onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("defaultIfEmpty operator onError");
                    }

                    @Override
                    public void onNext(Integer o) {
                        System.out.println("defaultIfEmpty operator onNext=" + o);
                    }
                });
        /**
         * result
         * defaultIfEmpty operator onNext=2
         * defaultIfEmpty operator onCompleted
         */

        /**
         * elementAt
         * 从特定的位置选择一个数据发射。
         *
         * 该函数和访问数组或者集合类似，如果 Observable 发射的数据个数没有这么多，
         * 则会抛出 java.lang.IndexOutOfBoundsException 。
         * 可以使用一个默认值（elementAtOrDefault）来避免抛出 java.lang.IndexOutOfBoundsException。
         */
        Observable<Integer> rangValues = Observable.range(100,10);
        rangValues.elementAt(2)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("elementAt operator onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("elementAt operator onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("elementAt operator onNext=" + integer);

                    }
                });
        /**
         * 102
         * Completed
         */

        rangValues.elementAtOrDefault(22,-1)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("elementAtOrDefault operator onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("elementAtOrDefault operator onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("elementAtOrDefault operator onNext=" + integer);

                    }
                });
        /**
         * elementAtOrDefault operator onNext=-1
         * elementAtOrDefault operator onCompleted
         */

        /**
         * sequenceEqual
         * 这个操作函数是用来比较两个 Observable 发射的数据是否是一样的，同样位置的数据是一样的。
         * 要求两个 Observable 发射的数据个数是一样的并且每个位置上的数据也是一样的。
         * 该函数内部用 Object.equals 来比较数据，当然你也可以自己指定一个比较函数。
         *
         * 如果一个源 Observable 出现了 错误，则 比较结果的 Observable 也会出现 错误并结束。
         */
        Observable<String> strings = Observable.just("1", "2", "3");
        Observable<Integer> ints = Observable.just(1, 2, 3);
        /**
         * sequenceEqual operator onNext=false
         * sequenceEqual operator onCompleted
         */
        Observable.sequenceEqual(strings,ints)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("sequenceEqual operator onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("sequenceEqual operator onError");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        System.out.println("sequenceEqual operator onNext=" + aBoolean);
                    }
                });

       Observable.sequenceEqual(strings, ints, new Func2<Serializable, Serializable, Boolean>() {
           @Override
           public Boolean call(Serializable str, Serializable i) {
               return str.equals(i.toString());
           }
       }).subscribe(new Subscriber<Boolean>() {
           @Override
           public void onCompleted() {
               System.out.println("sequenceEqual2 operator onCompleted");
           }

           @Override
           public void onError(Throwable e) {
               System.out.println("sequenceEqual2 operator onError");
           }

           @Override
           public void onNext(Boolean aBoolean) {
               System.out.println("sequenceEqual2 operator onNext=" + aBoolean);
           }
       });
        /**
         * 自定义的比较函数，所以结果是一样的
         * sequenceEqual2 operator onNext=true
         * sequenceEqual2 operator onCompleted
         */

                System.in.read();

    }
}
