package com.congwiny.myrxjava.transformation;

import com.congwiny.myrxjava.aggregation.PrintSubscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by congwiny on 2016/11/14.
 * <p>
 * http://blog.chengyunfeng.com/?p=964
 */

public class ObservableTransformation {
    public static void main(String[] args) throws Exception {
        /**
         * map
         *
         * 最基础的转换函数就是 map。
         * map 使用一个转换的参数把源Observable 中的数据转换为另外一种类型的数据。
         * 返回的 Observable 中包含了转换后的数据。
         */

        /**
         * 下面是把源 Observable 中的每个数据都加 3 然后再返回：
         *
         * result
         * Map: 3
         * Map: 4
         * Map: 5
         * Map: 6
         * Map: Completed
         *
         */

        Observable<Integer> values = Observable.range(0, 4);

        values
                .map(new Func1<Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer) {
                        return integer + 3;
                    }
                })
                .subscribe(new PrintSubscriber("Map"));

        /**
         * 上面的代码只是示例 map 的使用，并没有太大的实际意义。下面是一个比较实际的转换方式：
         */
        /**
         * result
         *
         * Map stringValues: 0
         * Map stringValues: 1
         * Map stringValues: 2
         * Map stringValues: 3
         * Map stringValues: Completed
         */

        Observable<Integer> stringValues =
                Observable.just("0", "1", "2", "3")
                        .map(new Func1<String, Integer>() {
                            @Override
                            public Integer call(String s) {
                                return Integer.parseInt(s);
                            }
                        });

        stringValues.subscribe(new PrintSubscriber("Map stringValues"));

        /**
         * cast和ofType
         *
         * cast 是把一个对象强制转换为子类型的缩写形式。 假设源 Observable为 Observable
         * 但是你知道里面的数据都是 Integer 类型，则你可以使用 cast 把里面的数据转换为 Integer：
         *
         * Map cast: 0
         * Map cast: 1
         * Map cast: 2
         * Map cast: 3
         * Map cast: Completed
         */
        Observable intValues = Observable.just(0, 1, 2, 3);

        intValues
                .cast(Integer.class)
                .subscribe(new PrintSubscriber("Map cast"));
        /**
         * 如果遇到类型不一样的对象的话,就会抛出一个 error：
         * Cast abc: 0
         * Cast abc: 1
         * Cast abc: 2
         * Cast abc: Error: java.lang.ClassCastException:
         * Cannot cast java.lang.String to java.lang.Integer
         */
        Observable abcValues = Observable.just(0, 1, 2, "3");

        abcValues.cast(Integer.class)
                .subscribe(new PrintSubscriber("Cast abc"));

        /**
         * 如果你不想处理类型不一样的对象，则可以用 ofType 。
         * 该函数用来判断数据是否为 该类型，如果不是则跳过这个数据。
         */
        /**
         * result
         * ofType mn: 0
         * ofType mn: 1
         * ofType mn: 3
         * ofType mn: Completed
         */
        Observable mnValues = Observable.just(0, 1, "2", 3);
        mnValues
                .ofType(Integer.class)
                .subscribe(new PrintSubscriber("ofType mn"));

        /**
         * timestamp 和 timeInterval
         *
         * 这两个函数可以给数据流中的数据添加额外的时间相关的信息。
         * timestamp 把数据转换为 Timestamped 类型，里面包含了原始的数据和一个原始数据是何时发射的时间戳。
         */

        /**
         * public final Observable<Timestamped<T>> timestamp()
         *
         * Timestamp: Timestamped(timestampMillis = 1479116816509, value = 0)
         * Timestamp: Timestamped(timestampMillis = 1479116816594, value = 1)
         * Timestamp: Timestamped(timestampMillis = 1479116816692, value = 2)
         * Timestamp: Completed
         */
        Observable<Long> intervalValues = Observable.interval(100, TimeUnit.MILLISECONDS);
        intervalValues.take(3)
                .timestamp()
                .subscribe(new PrintSubscriber("Timestamp"));

        /**
         * 从结果可以看到，上面的数据大概每隔100毫秒发射一个。
         * 如果你想知道前一个数据和当前数据发射直接的时间间隔，则可以使用 timeInterval 函数。
         *
         * public final Observable<TimeInterval<T>> timeInterval()
         * TimeInterval 中有个属性 intervalInMilliseconds 记录了两次数据发射直接的时间间隔。
         */

        /**
         * result
         *
         * TimeInterval: TimeInterval [intervalInMilliseconds=112, value=0]
         * TimeInterval: TimeInterval [intervalInMilliseconds=94, value=1]
         * TimeInterval: TimeInterval [intervalInMilliseconds=102, value=2]
         * TimeInterval: Completed
         */
        intervalValues.take(3)
                .timeInterval()
                .subscribe(new PrintSubscriber("TimeInterval"));


        //System.in.read();

        /**
         * materialize 和 dematerialize
         *
         * materialize
         * vt. 使具体化，使有形；使突然出现；使重物质而轻精神
         * vi. 实现，成形；突然出现
         *
         * public final Observable<Notification<T>> materialize()
         * materialize 对于记录日志也是很有用的。materialize 把数据转换为元数据发射出去
         * 元数据中包含了源 Observable 所发射的动作，是调用 onNext 还是 onComplete。注意上图中，源 Observable 结束的时候， materialize 还会发射一个 onComplete 数据，然后才发射一个结束事件
         *
         * dematerialize
         * vt. 使消失；使丧失物质形态
         * vi. 消失；丧失物质形态
         *
         * dematerialize 函数会把 materialize 转换后的Observable 再还原为 源 Observable。
         */

        /**
         * result
         *
         * Materialize: [rx.Notification@753957c8 OnNext 0]
         * Materialize: [rx.Notification@753957c9 OnNext 1]
         * Materialize: [rx.Notification@753957ca OnNext 2]
         * Materialize: [rx.Notification@64144e04 OnCompleted]
         * Materialize: Completed
         *
         * Notification 类包含了一些判断每个数据发射类型的方法，如果出错了还可以获取错误信息 Throwable 对象。
         */
        intervalValues.take(3)
                .materialize()
                .subscribe(new PrintSubscriber("Materialize"));
        //System.in.read();

        /**
         * flatMap
         *
         * map 把一个数据转换为另外一个数据。
         * 而 flatMap 把源 Observable 中的一个数据替换为任意数量的数据，可以为 0 个，也可以为无限个。
         * flatMap 把源 Observable 中的一个数据转换为一个新的 Observable 发射出去。
         *
         * flatMap: 0
         * flatMap: 1
         * flatMap: Completed
         */
        Observable<Integer> justValues = Observable.just(2);
        justValues.flatMap(new Func1<Integer, Observable<?>>() {
            @Override
            public Observable<?> call(Integer i) {
                return Observable.range(0, i);
            }
        }).subscribe(new PrintSubscriber("flatMap"));

        //System.in.read();
        /**
         * 上面的示例中，values 这个 Observable 只发射一个值 2.
         * 而 flatMap 参数把数据 2 转换为 Observable.range(0,5)，
         * 其中 Lambda 表达式中的 i 为 values Observable 发射的数据，这里也就是 2.
         * 然后订阅到 flatMap 生成的新 Observable 上。
         * 而 Observable.range(0,2) 会发射 0 和 1 两个数据，所以结果就是 0、 1 、完成。
         */

        /**
         * result
         *
         * flatMap range: 0
         *
         * flatMap range: 0
         * flatMap range: 1
         *
         * flatMap range: 0
         * flatMap range: 1
         * flatMap range: 2
         *
         * flatMap range: Completed
         */
        Observable<Integer> rangeValues = Observable.range(1, 3);

        rangeValues
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        return Observable.range(0, integer);
                    }
                })
                .subscribe(new PrintSubscriber("flatMap range"));
        //System.in.read();

        /**------没怎么看懂-------*/
        /**
         * 注意，flatMap 是把几个新的 Observable 合并为一个 Observable 返回，
         * 只要这些新的 Observable 有数据发射出来， flatMap 就会把数据立刻发射出去。
         * 所以如果这些新的 Observable 发射数据是异步的，那么 flatMap 返回的数据也是异步的。
         * 下面示例中使用 Observable.interval 来生成每个数据对应的新 Observable，
         * 由于 interval 返回的 Observable 是异步的，
         * 所以可以看到最终输出的结果是每当有 Observable 发射数据的时候， flatMap 就返回该数据。
         *
         * 下面的 Lambda 表达式 先把参数 i （这里分别为 100 和 150 这两个数字）
         * 转换为 Observable.interval(i, TimeUnit.MILLISECONDS)，
         * 每隔 i 毫秒发射一个数字，这样两个 Observable.interval 都发射同样的数字，
         * 只不过发射的时间间隔不一样，所以为了区分打印的结果，我们再用 map(v -> i) 把结果转换为 i 。
         * 结果如下：

         *
         * concatMap asyn: 100
         * concatMap asyn: 150
         * concatMap asyn: 100
         * concatMap asyn: 100
         * concatMap asyn: 150
         * concatMap asyn: 100
         * concatMap asyn: 150
         * concatMap asyn: 100
         * concatMap asyn: 100
         * concatMap asyn: 150
         * concatMap asyn: Completed
         *
         */
        Observable.just(100, 150)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(final Integer integer) {
                        return Observable
                                .interval(integer, TimeUnit.MILLISECONDS)
                                .map(new Func1<Long, Integer>() {
                                    @Override
                                    public Integer call(Long aLong) {
                                        return integer;
                                    }
                                });
                    }
                })
                .take(10)
                .subscribe(new PrintSubscriber("concatMap asyn"));

        //System.in.read();
        /**
         * concatMap
         *
         * 如果你不想把新 Observable 中的数据交织在一起发射，则可以选择使用 concatMap 函数。
         * 该函数会等第一个新的 Observable 完成后再发射下一个 新的 Observable 中的数据。
         */

        /**
         * result
         * concatMap: 100
         * concatMap: 100
         * concatMap: 100
         * concatMap: 150
         * concatMap: 150
         * concatMap: 150
         * concatMap: Completed
         */

        Observable.just(100, 150)
                .concatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(final Integer integer) {
                        return Observable
                                .interval(integer, TimeUnit.MILLISECONDS)
                                .map(new Func1<Long, Integer>() {
                                    @Override
                                    public Integer call(Long aLong) {
                                        return integer;
                                    }
                                }).take(3);
                    }
                })
                .subscribe(new PrintSubscriber("concatMap"));
        //System.in.read();
        /**
         * 所以 concatMap 要求新的Observable 不能是无限的，否则该无限 Observable 会阻碍后面的数据发射。
         * 为此，上面的示例使用 take 来结束 Observable。
         */

        /**
         * flatMapIterable
         *
         * flatMapIterable 和 flatMap 类似，区别是 flatMap 参数把每个数据转换为 一个新的 Observable，
         * 而 flatMapIterable 参数把一个数据转换为一个新的 iterable 对象。
         *
         * 例如下面是一个把参数转换为 iterable 的函数：
         *
         * flatMapIterable: 1
         *
         * flatMapIterable: 1
         * flatMapIterable: 2
         *
         * flatMapIterable: 1
         * flatMapIterable: 2
         * flatMapIterable: 3
         * flatMapIterable: Completed
         */
        Observable.range(1, 3).flatMapIterable(new Func1<Integer, Iterable<Integer>>() {
            @Override
            public Iterable<Integer> call(Integer integer) {
                return range(1, integer);
            }
        }).subscribe(new PrintSubscriber("flatMapIterable"));

        /**
         * flatMapIterable 把生成的 3 个 iterable 合并为一个 Observable 发射。
         * 作为 Rx 开发者，我们需要知道在 Rx 中应该使用 Observable 数据流来发射数据而不要混合使用传统的 iterable。
         * 但是如果你无法控制数据的来源，提供数据的一方只提供 iterable 数据，则依然可以直接使用这些数据。
         * flatMapIterable 把多个 iterable 的数据按照顺序发射出来，不会交织发射。
         */

        /**
         * flatMapIterable 还有另外一个重载函数可以用源 Observable 发射的数据来处理新的 iterable 中的每个数据：
         *
         * flatMapIterable override: 1
         * flatMapIterable override: 2
         * flatMapIterable override: 4
         * flatMapIterable override: 3
         * flatMapIterable override: 6
         * flatMapIterable override: 9
         * flatMapIterable override: Completed
         */
        Observable.range(1, 3)
                .flatMapIterable(new Func1<Integer, Iterable<Integer>>() {
                    @Override
                    public Iterable<Integer> call(Integer integer) {
                        return range(1, integer);
                    }
                }, new Func2<Integer, Integer, String>() {
                    @Override
                    public String call(Integer ori, Integer rv) {
                        return String.valueOf(ori * rv);
                    }
                }).subscribe(new PrintSubscriber("flatMapIterable override"));

        /**
         * 注意，上面的 ori 参数取值为 源 Observable 发射出来的数据，
         * 也就是 1、 2、 3. 而 rv 参数取值为 range(1, i) 参数生成的 iterable 中的每个数据，
         * 也就是分别为 [1]、[1,2]、[1,2,3]，所以最终的结果就是：[11], [12, 22], [13, 23, 33].
         */


    }

    public static Iterable<Integer> range(int start, int count) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i < start + count; i++) {
            list.add(i);
        }
        return list;
    }


}
