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

    MainPresenter(@NonNull MainContract.View view, Context context) {
        mView = view;
        mView.setPresenter(this);
        mPixabayApplication = PixabayApplication.create(context);
        mPixabayService = mPixabayApplication.getService();
        mDisposables = new CompositeDisposable();
    }

    @Override
    public void start() {
        loadPixabayImages(null, PixabayService.ORDER.popular, null, 1);
    }

    @Override
    public void searchData(String search) {
        loadPixabayImages(null, PixabayService.ORDER.popular, search, 1);
    }

    @Override
    public void loadPixabayImages(final String category, PixabayService.ORDER order, final String query, final int page) {
        // Reset error view if there was an error
        mView.resetErrorView();
        // Clear any subscriptions
        clearSubscriptions();
        // Set string in fragment
        mView.setCategoryString(category);
        // Set search string in fragment
        mView.setSearchString(query);
        // Show nav up if category or query is not null , Set title
        if (category != null || query != null) {
            String title;
            if (category != null) {
                title = category;
            } else {
                title = query;
            }
            mView.setActionbar(true, title);
        } else {
            mView.setActionbar(false, "Editors Choice");
        }
        // Category search null?
        final String mCategory = category == null ? null : category.toLowerCase();
        // New Data Requested {if page = 1} (API page starts at 1)
        if (page == 1) {
            mView.resetPixabayAdapter();
            mView.resetEndlessScrollListener();
        }
        // Hide or show Category Adapter
        mView.showCategoryRecycler(category == null && query == null);
        // API Call
        mDisposables.add(mPixabayService.getPhotos(
                false,
                order,
                mCategory,
                query,
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
                        if (mPixabay.getHits().size() == 0) {
                            mView.showNoItems();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.showErrorView(e.getMessage(), category, page);
                    }
                }));
    }

    @Override
    public void detachView() {
        mView = null;
        clearSubscriptions();
    }

    private void clearSubscriptions() {
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }
}
