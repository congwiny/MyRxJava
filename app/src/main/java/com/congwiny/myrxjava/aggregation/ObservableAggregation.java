package com.congwiny.myrxjava.aggregation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

/**
 * Created by congwiny on 2016/11/8.
 * <p>
 * 前面介绍了如何过滤掉不需要的数据、如何根据各种条件停止发射数据、如何检查数据是否符合某个条件。
 * 这些操作对数据流来说都是非常有意义的。
 * 本节介绍如何根据数据流中的数据来生成新的有意义的数据。
 * <p>
 * 本节的操作函数会使用源 Observable 中的事件流中的数据，然后把这些数据转换为其他类型的数据。
 * 返回结果是包含一个数据的 Observable。
 */

public class ObservableAggregation {
    public static void main(String[] args) throws Exception {
        /**
         * count 函数和 Java 集合中的 size 或者 length 一样。
         * 用来统计源 Observable 完成的时候一共发射了多少个数据。
         * 如果发射数据的个数超过了 int 最大值，则可以使用 countLong 函数。
         */
        Observable<Integer> rangeValues = Observable.range(0, 4);

        rangeValues.subscribe(new PrintSubscriber("Values"));
        rangeValues.count().subscribe(new PrintSubscriber("Count"));

        /**
         * first
         * first 类似于 take(1) , 发射 源 Observable 中的第一个数据。
         * 如果没有数据，则返回 ava.util.NoSuchElementException。
         * 还有一个重载的带有 过滤 参数，则返回第一个满足该条件的数据。
         */
        Observable<Long> intervalValues = Observable.interval(100, TimeUnit.MILLISECONDS);
        /**
         * First: 0
         * First: Completed
         */
        intervalValues
                .first()
                .subscribe(new PrintSubscriber("First"));

        /**
         * First: 6
         * First: Completed
         *
         * 可以使用 firstOrDefault 来避免 java.util.NoSuchElementException 错误信息，
         * 这样如果没有发现数据，则发射一个默认的数据。
         */
        intervalValues
                .first(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong > 5;
                    }
                })
                .subscribe(new PrintSubscriber("First"));

        /**
         * last 和 lastOrDefault 是和 first 一样的，区别就是当 源 Observable 完成的时候， 发射最后的数据。
         * 如果使用重载的带 过滤参数的函数，则返回最后一个满足该条件的数据。
         * 从后面开始，这种和前面功能非常类似的示例代码就省略了。但是你可以在示例代码中查看这些省略的示例。
         */

        /**
         * single
         *
         * single 只会发射源 Observable 中的一个数据，
         * 如果使用重载的带过滤条件的函数，则发射符合该过滤条件的那个数据。
         * 和 first 、last 不一样的地方是，single 会检查数据流中是否只包含一个所需要的的数据，
         * 如果有多个则会抛出一个错误信息。
         * 所以 single 用来检查数据流中是否有且仅有一个符合条件的数据。
         * 所以 single 只有在源 Observable 完成后才能返回。
         *
         * 和前面的类似，使用 singleOrDefault 可以返回一个默认值。
         */

        /**
         * result Single1: Error: java.lang.IllegalArgumentException: Sequence contains too many elements
         */
        intervalValues.take(10) // 获取前 10 个数据 的 Observable
                .single()
                .subscribe(new PrintSubscriber("Single1"));

        /**
         * result
         * Single2: 5
         * Single2: Completed
         */
        intervalValues.take(10)
                .single(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong == 5L;
                    }
                })
                .subscribe(new PrintSubscriber("Single2"));

        /**
         * 由于源 Observable 为无限的，所以这个不会打印任何东西
         */
        intervalValues
                .single(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong == 5L;
                    }
                }) // 由于源 Observable 为无限的，所以这个不会打印任何东西
                .subscribe(new PrintSubscriber("Single3"));
        //  System.in.read();
        /**
         * Custom aggregators（自定义聚合）
         */
        /**
         * reduce
         * 该思想是使用源 Observable 中的所有数据两两组合来生成一个单一的数据。
         * 在大部分重载函数中都需要一个函数用来定义如何组合两个数据变成一个。
         */
        Observable<Integer> rangeValues2 = Observable.range(0, 5);
        rangeValues2
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })//对数据流中所有整数求和
                .subscribe(new PrintSubscriber("reduce operator sum"));
        /**
         * reduce operator sum: 10
         * reduce operator sum: Completed
         */

        rangeValues2
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer > integer2 ? integer2 : integer;
                    }
                })//找出所有整数中最小的
                .subscribe(new PrintSubscriber("reduce operator min"));
        /**
         * reduce operator min: 0
         * reduce operator min: Completed
         */

        //------------
        /**
         * accumulator 参数返回的数据类型和 源 Observable 的数据类型可能是不一样的。
         * accumulator 的第一个参数为前一步 accumulator 执行的结果，而第二个参数为 下一个数据。
         * 使用一个初始化的值作为整个处理流程的开始。
         *
         * 下面的示例通过重新实现 count 函数来演示 reduce 的使用：
         * result
         *
         * reduce call string =Rx
         * reduce call string =java
         * reduce call string =is
         * reduce call string =easy
         * reduce Count: 4
         * reduce Count: Completed
         */
        Observable<String> justValues = Observable.just("Rx", "java", "is", "easy");
        justValues.reduce(0, new Func2<Integer, String, Integer>() {
            @Override
            public Integer call(Integer integer, String s) {
                System.out.println("reduce call string =" + s);
                return integer + 1;
            }
        }).subscribe(new PrintSubscriber("reduce Count"));

        /**
         * scan
         * scan和reduce很像。不一样的地方在于scan会发射所有中间的结算结果
         *
         * reduce 可以通过 scan 来实现： reduce(acc) = scan(acc).takeLast()
         * 所以 scan 比 reduce 更加通用。
         *
         * result
         * scan Sum: 0
         * scan Sum: 1
         * scan Sum: 3
         * scan Sum: 6
         * scan Sum: Completed
         *
         * 源 Observable 发射数据，经过 scan 处理后 scan 也发射一个处理后的数据，
         * 所以 scan 并不要求源 Observable 完成发射
         */
        rangeValues.scan(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }).subscribe(new PrintSubscriber("scan Sum"));

        //---------------------
        /**
         * Aggregation to collections（把数据聚合到集合中）
         * 使用 reduce 可以把源Observable 发射的数据放到一个集合中：
         *
         * reduce 的参数初始值为 new ArrayList()，
         * Lambda 表达式参数把源Observable 发射的数据添加到这个 List 中。
         * 当 源Observable 完成的时候，返回这个 List 对象
         *
         * result
         * reduce collections: [0, 1, 2, 3]
         * reduce collections: Completed
         */
        rangeValues
                .reduce(new ArrayList<Integer>(), new Func2<ArrayList<Integer>, Integer, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call(ArrayList<Integer> integers, Integer integer) {
                        integers.add(integer);
                        return integers;
                    }
                }).subscribe(new PrintSubscriber("reduce collections"));

        /**
         * 上面的示例代码其实并不太符合 Rx 操作符的原则，操作符有个原则是不能修改其他对象的状态。
         * 所以符合原则的实现应该是在每次转换中都创建一个新的 ArrayList 对象。
         * 下面是一个符合原则但是效率很低的实现：
         */
        /**
         * result
         * reduce collections: [0, 1, 2, 3]
         * reduce collections: Completed
         */
        rangeValues
                .reduce(new ArrayList<Integer>(), new Func2<ArrayList<Integer>, Integer, ArrayList<Integer>>() {
                    @Override
                    public ArrayList<Integer> call(ArrayList<Integer> integers, Integer integer) {
                        ArrayList<Integer> newIntegers = (ArrayList<Integer>) integers.clone();
                        newIntegers.add(integer);
                        return newIntegers;
                    }
                }).subscribe(new PrintSubscriber("reduce collections"));

        /**
         * collect
         *
         * 上面每一个值都创建一个新对象的性能是无法接受的。
         * 为此， Rx 提供了一个 collect 函数来实现该功能，该函数使用了一个可变的 accumulator 。
         *
         * result :
         * collect collections: [0, 1, 2, 3]
         * collect collections: Completed
         */
        rangeValues.collect(new Func0<ArrayList<Integer>>() {
            @Override
            public ArrayList<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new Action2<ArrayList<Integer>, Integer>() {
            @Override
            public void call(ArrayList<Integer> integers, Integer integer) {
                integers.add(integer);
            }
        }).subscribe(new PrintSubscriber("collect collections"));

        /**
         * 通常你不需要像这样手工的来收集数据， RxJava 提供了很多操作函数来实现这个功能。
         * toList,toSortedList,toMap,toMultimap
         */

        rangeValues.toList().subscribe(new PrintSubscriber("toList collect subscribe"));

        /**
         * toSortedList 和前面类似，返回一个排序后的 list
         */
        rangeValues.toSortedList().subscribe(new PrintSubscriber("toSortedList collect subscribe"));

        /**
         * 可以使用默认的比较方式来比较对象，也可以提供一个比较参数。该比较参数和 Comparator 接口语义一致。
         * 下面通过一个自定义的比较参数来返回一个倒序排列的整数集合：
         * toSortedList custom: [3, 2, 1, 0]
         * toSortedList custom: Completed
         */
        rangeValues.toSortedList(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer2 - integer;
            }
        }).subscribe(new PrintSubscriber("toSortedList custom"));

        /**
         * toMap
         * toMap 把数据流 T 变为一个 Map<TKey,T>。 该函数有三个重载形式
         *
         */
        Observable<Person> personValues = Observable.just(
                new Person("小鱼", 24),
                new Person("阿牛", 25),
                new Person("哈哈", 22)
        );
        /**
         * keySelector 功能是从一个值 T 中获取他对应的 key。
         * valueSelector 功能是从一个值 T 中获取需要保存 map 中的值。
         * mapFactory 功能是创建该 map 对象。
         */

        /**
         * 以Person的name为key,person对象为value
         *
         * toMap1 collect:
         * {小鱼=com.congwiny.myrxjava.aggregation.ObservableAggregation$Person@7ab2bfe1,
         * 哈哈=com.congwiny.myrxjava.aggregation.ObservableAggregation$Person@497470ed,
         * 阿牛=com.congwiny.myrxjava.aggregation.ObservableAggregation$Person@63c12fb0}
         toMap1 collect: Completed
         */
        personValues.toMap(new Func1<Person, String>() {
            @Override
            public String call(Person person) {
                return person.name;
            }
        }).subscribe(new PrintSubscriber("toMap1 collect"));

        /**
         * 还可以用 Person 的 age 作为map 的value：
         *
         * toMap2 collect: {小鱼=24, 哈哈=22, 阿牛=25}
         * toMap2 collect: Completed
         */
        personValues
                .toMap(new Func1<Person, String>() {
                    @Override
                    public String call(Person person) {
                        return person.name;
                    }
                }, new Func1<Person, Integer>() {
                    @Override
                    public Integer call(Person person) {
                        return person.age;
                    }
                }).subscribe(new PrintSubscriber("toMap2 collect"));

        /**
         * 还可以自定义如何生成这个 map 对象：这里是直接返回一个新的对象
         * 最后一个参数为工厂函数，每次一个新的 Subscriber 订阅的时候， 都会返回一个新的 map 对象。
         *
         * toMap3 collect: {小鱼=24, 哈哈=22, 阿牛=25}
         * toMap3 collect: Completed
         */
        personValues.toMap(
                new Func1<Person, String>() {
                    @Override
                    public String call(Person person) {
                        return person.name;
                    }
                },
                new Func1<Person, Integer>() {
                    @Override
                    public Integer call(Person person) {
                        return person.age;
                    }
                },
                new Func0<Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call() {
                        return new HashMap<String, Integer>();
                    }
                }
        ).subscribe(new PrintSubscriber("toMap3 collect"));

        /**
         * 通常情况下多个 value 的 key 可能是一样的。
         * 一个 key 可以映射多个 value 的数据结构为 multimap，multimap 的 value 为一个集合。
         * 该过程被称之为 “grouping” （分组）。
         */
        Observable<Person> personValues2 = Observable.just(
                new Person("小鱼", 24),
                new Person("阿牛", 25),
                new Person("哈哈", 22),
                new Person("呗呗", 24)
        );

        /**
         * 24 分组
         * result
         * toMultimap1: {22=[哈哈], 24=[小鱼, 呗呗], 25=[阿牛]}
         * toMultimap1: Completed
         */
        personValues2.toMultimap(new Func1<Person, Integer>() {
            @Override
            public Integer call(Person person) {
                return person.age;//key
            }
        }, new Func1<Person, String>() {
            @Override
            public String call(Person person) {
                return person.name;//value
            }
        }).subscribe(new PrintSubscriber("toMultimap1"));

        /**
         * toMultiMap2: {22=[哈哈], 24=[小鱼, 呗呗], 25=[阿牛]}
         * toMultiMap2: Completed
         */
        personValues2
                .toMultimap(new Func1<Person, Integer>() {
                    @Override
                    public Integer call(Person person) {
                        return person.age;
                    }
                }, new Func1<Person, String>() {
                    @Override
                    public String call(Person person) {
                        return person.name;
                    }
                }, new Func0<Map<Integer, Collection<String>>>() {
                    @Override
                    public Map<Integer, Collection<String>> call() {
                        return new HashMap<Integer, Collection<String>>();
                    }
                }, new Func1<Integer, Collection<String>>() {
                    @Override
                    public Collection<String> call(Integer integer) {
                        return new ArrayList<String>();
                    }
                }).subscribe(new PrintSubscriber("toMultiMap2"));

        /**
         * groupBy 是 toMultimap 函数的 Rx 方式的实现。
         * groupBy 根据每个源Observable 发射的值来计算一个 key，
         * 然后为每个 key 创建一个新的 Observable并把key 一样的值发射到对应的新 Observable 中。
         *
         * 返回的结果为 GroupedObservable。
         * 每次发现一个新的key，内部就生成一个新的 GroupedObservable并发射出来。
         * GroupedObservable和普通的 Observable 相比 多了一个 getKey 函数来获取 分组的 key。
         * 来自于源Observable中的值会被发射到对应 key 的 GroupedObservable 中。

         * 嵌套的 Observable 导致方法的定义比较复杂，但是提供了随时发射数据的优势，
         * 没必要等源Observable 发射完成了才能返回数据。
         */
        /**
         * 下面的示例中使用了一堆单词作为源Observable的数据，
         * 然后根据每个单词的首字母作为分组的 key，最后把每个分组的 最后一个单词打印出来：
         */

        Observable<String> orderValues = Observable.just(
                "first",
                "second",
                "third",
                "forth",
                "fifth",
                "sixth"
        );

        /**
         * result
         *
         * s: sixth
         * t: third
         * f: fifth
         */
        orderValues.groupBy(new Func1<String, Character>() {
            @Override
            public Character call(String s) {
                return s.charAt(0);
            }
        }).subscribe(new Action1<GroupedObservable<Character, String>>() {
            @Override
            public void call(final GroupedObservable<Character, String> characterStringGroupedObservable) {
                //f,s,t,获取分组后的observable的key
                // System.out.println("group getKey="+characterStringGroupedObservable.getKey());
                //获取分组后的Observable的最后一个Observable
                Observable<String> stringObservable = characterStringGroupedObservable.last();
                stringObservable.subscribe(new Action1<String>() {
                                               @Override
                                               public void call(String s) {
                                                   System.out.println(characterStringGroupedObservable.getKey() + ": " + s);
                                               }
                                           }
                );
            }
        });

        /**
         * 上面的代码使用了嵌套的 Subscriber，在 Rx 前传 中 我们介绍了 Rx 功能之一就是为了避免嵌套回调函数，
         * 所以下面演示了如何避免嵌套
         */
        /**
         * result
         *
         * groupBy-->flatMap-->map: s: sixth
         * groupBy-->flatMap-->map: t: third
         * groupBy-->flatMap-->map: f: fifth
         *  groupBy-->flatMap-->map: Completed
         */
        orderValues.groupBy(new Func1<String, Character>() {
            @Override
            public Character call(String s) {
                return s.charAt(0);
            }
        }).flatMap(new Func1<GroupedObservable<Character, String>, Observable<String>>() {
            @Override
            public Observable<String> call(final GroupedObservable<Character, String> characterStringGroupedObservable) {
                return characterStringGroupedObservable.last().map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return characterStringGroupedObservable.getKey() + ": " + s;
                    }
                });
            }
        }).subscribe(new PrintSubscriber("groupBy-->flatMap-->map"));

        /**
         * Nested observables
         * --下面的解释没看懂。。
         *
         * 嵌套的 Observable 可能一开始看起来比较迷糊，但是他们有很多使用的场景。很好很强大，比如：
         * Partitions of Data （分区数据）
         *  你可以把来至于一个源Observable 的数据分为多个 Observable并分别给多个资源去处理。分区数据对于聚合数据也是有用的，通常使用 groupBy 函数来实现该功能。
         * 在线游戏服务器
         *  例如魔兽世界有很多服务器。每个新的值代表一个服务器上线了。而嵌套的 Observable 值是每个服务器的延迟时间，这样用户就可以看到每个服务器是否可用的信息的。如果一个服务器挂了，则嵌套的Observable通过发射一个完成信号标记服务器挂了。
         * 金融数据流
         *  交易市场每天都有开市和闭市的时间。当开市了就提供每个市场的价格信息流，闭市了就标记完成了。
         * 聊天室
         *  用户可以加入一个聊天室（源 Observable 里面有很多个聊天室），加入聊天室后可以留言（聊天室本身为嵌套的 Observable，在该 Observable中保存留言数据），然后还可以离开聊天室（结束 嵌套的 Observable 数据流）。
         * 文件监视器
         *  文件夹中的文件可以监视其修改操作。嵌套的 Observable 可以代表对每个文件所做的操作，删除文件代表嵌套的 Observable 完成了。
         */

        /**
         * nest
         * 当和 嵌套的 Observable 打交道的时候，就要使用 nest 函数了。
         * nest 函数把一个普通的非 嵌套 Observable 变为一个嵌套的 Observable。 nest 把一个源 Observable 变为一个嵌套的 Observable 发射出去就结束了。
         * http://pic.goodev.org/wp-files/2016/03/Cfakepathrxnest.png
         *
         * result
         * integer num=0
         * integer num=1
         * integer num=2
         */

        Observable.range(0, 3)
                .nest()
                .subscribe(new Action1<Observable<Integer>>() {
                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        integerObservable.subscribe(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                System.out.println("integer num=" + integer);
                            }
                        });
                    }
                });
        /**
         *  上面的示例只是为了演示 nest 操作，实际没球用！！
         *  如果从其他地方获取了一个非 嵌套的 Observable，但是使用的时候需要使用 嵌套的 Observable，
         *  则你可以通过 nest 函数来转换。
         */


    }


    static class Person {
        public final String name;
        public final Integer age;

        public Person(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
