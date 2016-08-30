package com.congwiny.myrxjava.utils;

import android.net.Uri;


import com.congwiny.catsdk.api.CatApi;
import com.congwiny.catsdk.bean.Cat;

import java.util.Collections;
import java.util.List;

/**
 * Created by congwiny on 2016/8/29.
 */
public class CatHelper {
    CatApi catApi;

    public interface CutestCatCallback1 {
        //保存成功
        void onCutestCatSaved(Uri uri);

        //查询失败
        void onQueryFailed(Exception e);
    }

    public interface CutestCatCallback2 {
        void onCutestCatSaved(Uri uri);

        void onError(Exception e);
    }

    /**
     * 现在再来看看我们的业务逻辑代码，是不是和之前的阻塞式调用那么简单、那么清晰？
     * 当然不一样了，下面的异步操作代码看起来太恐怖了！
     * 这里有太多的干扰代码了，太多的匿名类了，但是不可否认，他们的业务逻辑其实是一样的。
     * 查询猫的列表数据、找出最可爱的并保持其图片。
     *
     * 另外组合功能也不见了！ 现在你没法像阻塞操作一样来组合调用每个功能了。
     * 异步操作中，每次你都必须通过回调接口来手工的处理结果。
     *
     * 那么关于异常传递和处理呢？ 没了！
     * 异步代码中的异常不会自动传递了，我们需要手工的重新传递出去。
     * （onStoreFailed 和 onQueryFailed 就是干这事用的）
     *
     * 下面的代码非常难懂也更难发现潜在的 BUG。
     * 然后呢？我们如何处理这种情况呢？
     * 我们是不是就被困在这种无法组合的回调接口困局中呢？ 下次我们来看看如何优化该问题。
     *
     * @param query
     * @param cutestCatCallback2
     */
    public void saveTheCutestCat2(String query, final CutestCatCallback2 cutestCatCallback2) {
        catApi.queryCats2(query, new CatApi.CatsQueryCallback2() {
            @Override
            public void onCatListReceived(List<Cat> cats) {
                Cat cutest = findCutestCat(cats);
                //这样就可以等到存储完，回调传回uri
                catApi.store2(cutest, new CatApi.StoreCallback2() {
                    @Override
                    public void onCatStored(Uri uri) {
                        cutestCatCallback2.onCutestCatSaved(uri);
                    }

                    @Override
                    public void onStoreFailed(Exception e) {
                        cutestCatCallback2.onError(e);
                    }
                });
            }

            @Override
            public void onQueryFailed(Exception e) {
                cutestCatCallback2.onError(e);
            }
        });
    }

    /**
     * 我们无法使用saveTheCutestCat函数返回一个值，我们需要一个回调接口来异步的处理结果
     *
     * @param query
     * @param cutestCatCallback1
     */
    public void saveTheCutestCat1(String query, final CutestCatCallback1 cutestCatCallback1) {
        catApi.queryCats1(query, new CatApi.CatsQueryCallback1() {

            @Override
            public void onCatListReceived(List<Cat> cats) {
                Cat cutest = findCutestCat(cats);

                /**
                 * 查询完之后，以下无法使用阻塞式函数了。。
                 * 无法等到catApi.store返回，onCutestCatSaved就已经被调用了
                 */
                Uri savedUri = catApi.store(cutest);//异步
                cutestCatCallback1.onCutestCatSaved(savedUri);
            }

            @Override
            public void onError(Exception e) {
                cutestCatCallback1.onQueryFailed(e);
            }
        });
    }

    /**
     * 以下代码是阻塞的，但是我们的程序没法等待。。所以需要异步！
     *
     * @param query
     * @return
     */
    public Uri saveTheCutestCat0(String query) {
        //组合功能
        List<Cat> cats = catApi.queryCats0(query);
        Cat cutestCat = findCutestCat(cats);
        Uri saveUri = catApi.store(cutestCat);
        return saveUri;
    }

    private Cat findCutestCat(List<Cat> cats) {
        return Collections.max(cats);
    }
}
