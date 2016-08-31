package com.congwiny.myrxjava.wrapper;

import android.net.Uri;

import com.congwiny.catsdk.api.CatApi;
import com.congwiny.catsdk.bean.Cat;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by congwiny on 2016/8/31.
 */
public class RxCatApiWrapper {

    CatApi catApi;

    public Observable<List<Cat>> queryCats(final String query) {
        return Observable.create(new Observable.OnSubscribe<List<Cat>>() {
            @Override
            public void call(final Subscriber<? super List<Cat>> subscriber) {
                catApi.queryCats2(query, new CatApi.CatsQueryCallback2() {
                    @Override
                    public void onCatListReceived(List<Cat> cats) {
                        subscriber.onNext(cats);
                    }

                    @Override
                    public void onQueryFailed(Exception e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }

    public Observable<Uri> store(final Cat cat) {
        return Observable.create(new Observable.OnSubscribe<Uri>() {
            @Override
            public void call(final Subscriber<? super Uri> subscriber) {
                catApi.store2(cat, new CatApi.StoreCallback2() {
                    @Override
                    public void onCatStored(Uri uri) {
                        subscriber.onNext(uri);
                    }

                    @Override
                    public void onStoreFailed(Exception e) {
                        subscriber.onError(e);
                    }
                });
            }
        });
    }
}
