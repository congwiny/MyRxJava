package com.congwiny.myrxjava.concatenation;

import com.congwiny.myrxjava.rx5_aggregation.PrintSubscriber;

import rx.Observable;
import rx.functions.Func1;
import rx.observables.GroupedObservable;

/**
 * Created by congwiny on 2016/11/15.
 *
 * 下次再分享吧。。
 * <p>
 * 组合数据流
 * 到目前为止我们介绍了如何创建数据流以及如何把数据流中的数据转换成我们需要的数据。
 * 然而，大部分应用都需要处理多个数据源的数据。需要一种把多个数据源组合一起的方法。
 * 在前面的介绍中，也看到了一些数据流会使用多个 Observable。
 * 本节介绍如何把多个数据源的数据组合为一个数据源的操作函数。
 */

public class ObservableConcatenation {


    public static void main(String[] args) throws Exception {
        /**
         * concat
         *
         * concat 操作函数把多个数据流按照顺序一个一个的发射数据。
         * 第一个数据流发射完后，继续发射下一个。 concat 函数有多个重载函数：
         */

        /**
         * result
         *
         * concat: 0
         * concat: 1
         * concat: 2
         * concat: 10
         * concat: 11
         * concat: 12
         * concat: Completed
         */

        Observable<Integer> seq1 = Observable.range(0, 3);
        Observable<Integer> seq2 = Observable.range(10, 3);

        Observable.concat(seq1, seq2)
                .subscribe(new PrintSubscriber("concat"));


        Observable<String> words = Observable.just(
                "First",
                "Second",
                "Third",
                "Fourth",
                "Fifth",
                "Sixth"
        );

        words
                .groupBy(new Func1<String, Character>() {

                    @Override
                    public Character call(String s) {
                        return s.charAt(0);
                    }
                })
                .flatMap(new Func1<GroupedObservable<Character, String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(GroupedObservable<Character, String> characterStringGroupedObservable) {
                        return characterStringGroupedObservable.map(new Func1<String, String>() {
                            @Override
                            public String call(String s) {
                                return s;
                            }
                        });
                    }
                }).subscribe(new PrintSubscriber("group by"));


        Observable
                .concat(words
                        .groupBy(new Func1<String, Character>() {
                            @Override
                            public Character call(String s) {
                                return s.charAt(0);
                            }
                        }))
                .subscribe(new PrintSubscriber("groupBy concat"));


        /**
         *
         * result
         * groupBy concat: First
         * groupBy concat: Fourth
         * groupBy concat: Fifth
         * groupBy concat: Second
         * groupBy concat: Sixth
         * groupBy concat: Third
         * groupBy concat: Completed
         */

    }
}
