package com.congwiny.myrxjava.utils;

import android.net.Uri;

import com.congwiny.catsdk.bean.Cat;
import com.congwiny.myrxjava.async.AsyncJob;
import com.congwiny.myrxjava.map.Func;
import com.congwiny.myrxjava.wrapper.Callback;
import com.congwiny.myrxjava.wrapper.CatApiWrapper;

import java.util.Collections;
import java.util.List;

/**
 * Created by congwiny on 2016/8/30.
 */
public class CatHelper2 {
    //使用包装类取代CatApi
    CatApiWrapper catApiWrapper;

    /**
     * 模拟RxJava链式版本
     *
     * 总结
     * 通过简单的转换操作，我们可以把异步操作抽象出来。
     * 这种抽象的结果可以像操作简单的阻塞函数一样来操作异步操作并组合异步操作。
     * 这样我们就可以摆脱层层嵌套的回调接口了，并且不用手工的去处理每次异步操作的异常。
     *
     * @param query
     * @return
     */
    public AsyncJob<Uri> saveTheCutestCat33(final String query) {

        return catApiWrapper.queryCats3(query)
                .map(new Func<List<Cat>, Cat>() {
                    @Override
                    public Cat call(List<Cat> cats) {
                        return findCutestCat(cats);
                    }
                })
                .flatMap(new Func<Cat, AsyncJob<Uri>>() {
                    @Override
                    public AsyncJob<Uri> call(Cat cat) {
                        return catApiWrapper.store3(cat);
                    }
                });
    }

    /**
     * 最终的代码 看起来是不是有点眼熟啊？ 再仔细看看。还没发现？
     * 如果把匿名类修改为 Java 8 的 lambdas 表达式（逻辑是一样的，只是让代码看起来更清晰点）就很容易发现了。
     * 这个代码和刚刚开头的阻塞式代码是不是非常相似：
     *
     * @param query
     * @return
     * @see CatHelper#saveTheCutestCat0(String)
     * <p/>
     * 现在他们不仅逻辑是一样的，语义上也是一样的。 太棒了！
     * 异常处理也会传递到最终的回调接口中。
     */
    public AsyncJob<Uri> saveTheCutestCat3(final String query) {

        AsyncJob<List<Cat>> catsListAsyncJob3 = catApiWrapper.queryCats3(query);

        /**
         * 使用map进行转换
         */
        final AsyncJob<Cat> cutestCatAsyncJob3 = catsListAsyncJob3.map(new Func<List<Cat>, Cat>() {
            @Override
            public Cat call(List<Cat> cats) {
                return findCutestCat(cats);
            }
        });

        /**
         * 使用flapMap转换
         */
        AsyncJob<Uri> storedUriAsyncJob3 = cutestCatAsyncJob3.flatMap(new Func<Cat, AsyncJob<Uri>>() {
            @Override
            public AsyncJob<Uri> call(Cat cat) {
                return catApiWrapper.store3(cat);
            }
        });
        return storedUriAsyncJob3;
    }

    /**
     * 虽然代码量多了，但是看起来更加清晰了。
     * 嵌套的回调函数没那么多层级了，异步操作的名字也更容易理解了
     * （catsListAsyncJob, cutestCatAsyncJob, storedUriAsyncJob）。
     *
     * @param query
     * @return
     */

    public AsyncJob<Uri> saveTheCutestCat22(final String query) {
        //1.
        final AsyncJob<List<Cat>> catsListAsyncJob = catApiWrapper.queryCats3(query);

        /**
         * 使用map进行转换
         */

        final AsyncJob<Cat> cutestCatAsyncJob2 = catsListAsyncJob.map(new Func<List<Cat>, Cat>() {
            @Override
            public Cat call(List<Cat> cats) {
                return findCutestCat(cats);
            }
        });

        //2.
        final AsyncJob<Cat> cutestCatAsyncJob = new AsyncJob<Cat>() {
            @Override
            public void start(final Callback<Cat> callback) {

                catsListAsyncJob.start(new Callback<List<Cat>>() {
                    @Override
                    public void onResult(List<Cat> result) {
                        callback.onResult(findCutestCat(result));
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                });
            }
        };

        /**
         * 使用flapMap转换
         */
        AsyncJob<Uri> storedUriAsyncJob2 = cutestCatAsyncJob2.flatMap(new Func<Cat, AsyncJob<Uri>>() {
            @Override
            public AsyncJob<Uri> call(Cat cat) {
                return catApiWrapper.store3(cat);
            }
        });

        //3.
        AsyncJob<Uri> storedUriAsyncJob = new AsyncJob<Uri>() {
            @Override
            public void start(final Callback<Uri> cutestCatCallback) {
                cutestCatAsyncJob2.start(new Callback<Cat>() {
                    @Override
                    public void onResult(Cat cat) {
                        catApiWrapper.store3(cat).start(new Callback<Uri>() {
                            @Override
                            public void onResult(Uri result) {
                                cutestCatCallback.onResult(result);
                            }

                            @Override
                            public void onError(Exception e) {
                                cutestCatCallback.onError(e);
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        cutestCatCallback.onError(e);
                    }
                });
            }
        };
        return storedUriAsyncJob;
    }


    /**
     * 分离参数和回调接口
     * 使用 AsyncJob 来启动每个操作
     *
     * @param query
     * @return
     */
    public AsyncJob<Uri> saveTheCutestCat2(final String query) {
        return new AsyncJob<Uri>() {
            @Override
            public void start(final Callback<Uri> cutestCatCallback) {
                //async
                catApiWrapper.queryCats3(query)
                        .start(new Callback<List<Cat>>() {
                            @Override
                            public void onResult(List<Cat> cats) {
                                //sync
                                Cat cutest = findCutestCat(cats);
                                //async
                                catApiWrapper.store3(cutest)
                                        .start(new Callback<Uri>() {
                                            @Override
                                            public void onResult(Uri result) {
                                                cutestCatCallback.onResult(result);
                                            }

                                            @Override
                                            public void onError(Exception e) {
                                                cutestCatCallback.onError(e);
                                            }
                                        });
                            }

                            @Override
                            public void onError(Exception e) {
                                cutestCatCallback.onError(e);
                            }
                        });
            }
        };
    }


    /**
     * 使用此工具类 保存只需要传入两个参数
     *
     * @param query
     * @param cutestCatCallback 保存结果的回调
     */
    public void saveTheCutestCat(String query, final Callback<Uri> cutestCatCallback) {
        catApiWrapper.queryCats2(query, new Callback<List<Cat>>() {
            @Override
            public void onResult(List<Cat> cats) {
                Cat cat = findCutestCat(cats);
                /**
                 * 由于使用了泛型回调接口，这里的cutestCatCallback 可以直接设置为函数 apiWrapper.store的参数，
                 * 所以 上面的代码比前面的代码
                 * @see CatHelper#saveTheCutestCat2(String, CatHelper.CutestCatCallback2)
                 * 要少一层匿名类。看起来简单一点。
                 */
                catApiWrapper.store2(cat, cutestCatCallback);
            }

            @Override
            public void onError(Exception e) {
                cutestCatCallback.onError(e);
            }
        });
    }

    private Cat findCutestCat(List<Cat> cats) {
        return Collections.max(cats);
    }

}
