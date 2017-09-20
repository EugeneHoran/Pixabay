package com.exercise.eugene.pixabay.main;

import android.content.Context;
import android.support.annotation.NonNull;

import com.exercise.eugene.pixabay.PixabayApplication;
import com.exercise.eugene.pixabay.client.PixabayService;
import com.exercise.eugene.pixabay.model.Pixabay;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MainPresenter implements MainContract.Presenter {
    private CompositeDisposable mDisposables;
    private MainContract.View mView;
    private PixabayApplication mPixabayApplication;
    private PixabayService mPixabayService;
    private Pixabay mPixabay;

    public MainPresenter(@NonNull MainContract.View view, Context context) {
        mView = view;
        mView.setPresenter(this);
        mPixabayApplication = PixabayApplication.create(context);
        mPixabayService = mPixabayApplication.getService();
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void start() {
        loadPixabayImages(null, PixabayService.ORDER.popular, 1);
    }

    @Override
    public void detachView() {
        mView = null;
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }

    @Override
    public void loadPixabayImages(String category, PixabayService.ORDER order, int page) {
        String mCategory = null;
        if (category != null) {
            mCategory = category.toLowerCase();
        }
        mDisposables.add(mPixabayService.getPhotos(
                true,
                PixabayService.ORDER.popular,
                mCategory,
                page)
                .subscribeOn(mPixabayApplication.defaultSubscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Pixabay>() {
                    @Override
                    public void onNext(@NonNull Pixabay pixabay) {
                        mPixabay = pixabay;
                    }

                    @Override
                    public void onComplete() {
                        mView.showPixabayImageAdapter(mPixabay.getHits());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }
                }));
    }
}
