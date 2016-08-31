package com.congwiny.myrxjava.wrapper;

import android.net.Uri;

import com.congwiny.catsdk.api.CatApi;
import com.congwiny.catsdk.bean.Cat;
import com.congwiny.myrxjava.async.AsyncJob;

import java.util.List;

/**
 * Created by congwiny on 2016/8/30.
 * <p>
 * 使用此类来改变调用的参数
 */
public class CatApiWrapper {
    CatApi catApi;

    public AsyncJob<List<Cat>> queryCats3(final String query) {
        return new AsyncJob<List<Cat>>() {
            @Override
            public void start(final Callback<List<Cat>> callback) {
                catApi.queryCats2(query, new CatApi.CatsQueryCallback2() {
                    @Override
                    public void onCatListReceived(List<Cat> cats) {
                        callback.onResult(cats);
                    }

                    @Override
                    public void onQueryFailed(Exception e) {
                        callback.onError(e);
                    }
                });
            }
        };
    }

    public AsyncJob<Uri> store3(final Cat cat) {
        return new AsyncJob<Uri>() {
            @Override
            public void start(final Callback<Uri> callback) {
                catApi.store2(cat, new CatApi.StoreCallback2() {
                    @Override
                    public void onCatStored(Uri uri) {
                        callback.onResult(uri);
                    }

                    @Override
                    public void onStoreFailed(Exception e) {
                        callback.onError(e);
                    }
                });
            }
        };
    }

    public void queryCats2(String query, final Callback<List<Cat>> queryCatsCallback) {
        catApi.queryCats2(query, new CatApi.CatsQueryCallback2() {
            @Override
            public void onCatListReceived(List<Cat> cats) {
                queryCatsCallback.onResult(cats);
            }

            @Override
            public void onQueryFailed(Exception e) {
                queryCatsCallback.onError(e);
            }
        });
    }

    public void store2(Cat cat, final Callback<Uri> storeUriCallback) {
        catApi.store2(cat, new CatApi.StoreCallback2() {
            @Override
            public void onCatStored(Uri uri) {
                storeUriCallback.onResult(uri);
            }

            @Override
            public void onStoreFailed(Exception e) {
                storeUriCallback.onError(e);
            }
        });
    }
}
