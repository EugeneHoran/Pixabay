package com.exercise.eugene.pixabay.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.exercise.eugene.pixabay.PixabayApplication;
import com.exercise.eugene.pixabay.client.Filter;
import com.exercise.eugene.pixabay.client.PixabayService;
import com.exercise.eugene.pixabay.model.Pixabay;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import retrofit2.Response;

public class MainPresenter implements MainContract.Presenter {
    private CompositeDisposable mDisposables;
    private MainContract.View mView;
    private PixabayApplication mPixabayApplication;
    private PixabayService mPixabayService;
    private Response<Pixabay> mPixabayResponse;
    private Realm mRealm;
    private Pixabay mPixabayDao;

    MainPresenter(@NonNull MainContract.View view, Context context) {
        mView = view;
        mView.setPresenter(this);
        mPixabayApplication = PixabayApplication.create(context);
        mPixabayService = mPixabayApplication.getService();
        mDisposables = new CompositeDisposable();
        // Create the Realm instance
        mRealm = Realm.getDefaultInstance();
        mPixabayDao = mRealm.where(Pixabay.class).findFirst();
        if (mPixabayDao != null) {
            setDaoListener();
        }
    }

    @Override
    public void start() {
        loadFeaturedImages(Filter.ORDER.POPULAR, mPixabayDao == null);
    }

    @Override
    public void searchData(String search) {
        loadSearchImages(Filter.ORDER.POPULAR, search, 1);
    }

    @Override
    public void loadFeaturedImages(Filter.ORDER order, boolean makeApiCall) {
        mView.setType(Filter.TYPE.ALL);
        final int mPage;
        if (mPixabayDao == null) {
            mPage = 1;
        } else {
            mPage = mPixabayDao.getPage() + 1;
        }
        // New Data Requested {if page = 1} (API page starts at 1)
        if (mPage == 1) {
            mView.resetPixabayAdapter();
            mView.resetEndlessScrollListener();
        }
        // Reset error view if there was an error
        mView.resetErrorView();
        // Clear any subscriptions
        clearSubscriptions();
        // Set string in fragment
        mView.setCategoryString(null);
        // Set search string in fragment
        mView.setSearchString(null);
        // Show nav up if category or query is not null , Set title
        mView.setActionbar(false, "Editors Choice");
        // Hide or show Category Adapter
        mView.showCategoryRecycler(true);
        if (!makeApiCall) {
            mView.resetPixabayAdapter();
            mView.showPixabayImageAdapter(mPixabayDao.getHits());
        } else {
            mDisposables.add(mPixabayService.getFeaturedPhotos(
                    true,
                    Filter.order(order),
                    mPage)
                    .subscribeOn(mPixabayApplication.defaultSubscribeScheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<Response<Pixabay>>() {
                        @Override
                        public void onNext(@NonNull final Response<Pixabay> pixabay) {
                            mPixabayResponse = pixabay;
                        }

                        @Override
                        public void onComplete() {
                            Pixabay mPixabay = mPixabayResponse.body();
                            mView.showPixabayImageAdapter(mPixabay.getHits());
                            if (mPixabay.getHits().size() == 0) {
                                mView.showNoItems();
                            }
                            if (mPixabayDao == null) {
                                mRealm.beginTransaction();
                                mPixabay.setPrimaryKey(1234);
                                mPixabay.setPage(mPage);
                                mRealm.copyToRealmOrUpdate(mPixabay);
                                mRealm.commitTransaction();
                                mPixabayDao = mRealm.where(Pixabay.class).findFirst();
                                setDaoListener();
                            } else {
                                mRealm.beginTransaction();
                                mPixabayDao.setPage(mPixabayDao.getPage() + 1);
                                mPixabayDao.getHits().addAll(mPixabay.getHits());
                                mRealm.copyToRealmOrUpdate(mPixabayDao);
                                mRealm.commitTransaction();
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            mView.showErrorView(e.getMessage(), null, mPage);
                        }
                    }));
        }
    }

    @Override
    public void loadCategoryImages(final Filter.ORDER order, final String category, final int page) {
        mView.setType(Filter.TYPE.CATEGORY);
        // Reset error view if there was an error
        mView.resetErrorView();
        // Clear any subscriptions
        clearSubscriptions();
        // Set string in fragment
        mView.setCategoryString(category);
        // Set search string in fragment
        mView.setSearchString(null);
        // Show nav up if category or query is not null , Set title
        mView.setActionbar(true, category);

        if (page == 1) {
            mView.resetPixabayAdapter();
            mView.resetEndlessScrollListener();
        }
        // Hide or show Category Adapter
        mView.showCategoryRecycler(false);
        mDisposables.add(mPixabayService.getCategoryPhotos(
                false,
                Filter.order(order),
                category.toLowerCase(),
                page)
                .subscribeOn(mPixabayApplication.defaultSubscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<Pixabay>>() {
                    @Override
                    public void onNext(@NonNull final Response<Pixabay> pixabay) {
                        mPixabayResponse = pixabay;
                    }

                    @Override
                    public void onComplete() {
                        Pixabay mPixabay = mPixabayResponse.body();
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
    public void loadSearchImages(Filter.ORDER order, final String query, final int page) {
        mView.setType(Filter.TYPE.SEARCH);
        // Reset error view if there was an error
        mView.resetErrorView();
        // Clear any subscriptions
        clearSubscriptions();
        // Set string in fragment
        mView.setCategoryString(null);
        // Set search string in fragment
        mView.setSearchString(query);
        // Show nav up if category or query is not null , Set title
        mView.setActionbar(true, query);

        if (page == 1) {
            mView.resetPixabayAdapter();
            mView.resetEndlessScrollListener();
        }
        // Hide or show Category Adapter
        mView.showCategoryRecycler(false);
        mDisposables.add(mPixabayService.getSearchPhotos(
                false,
                Filter.order(order),
                query.toLowerCase(),
                page)
                .subscribeOn(mPixabayApplication.defaultSubscribeScheduler())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<Pixabay>>() {
                    @Override
                    public void onNext(@NonNull final Response<Pixabay> pixabay) {
                        mPixabayResponse = pixabay;
                    }

                    @Override
                    public void onComplete() {
                        Pixabay mPixabay = mPixabayResponse.body();
                        mView.showPixabayImageAdapter(mPixabay.getHits());
                        if (mPixabay.getHits().size() == 0) {
                            mView.showNoItems();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mView.showErrorView(e.getMessage(), query, page);
                    }
                }));
    }


    private void setDaoListener() {
        mPixabayDao.addChangeListener(new RealmChangeListener<Pixabay>() {
            @Override
            public void onChange(Pixabay realmModel) {
                mPixabayDao = realmModel;
                Log.e("Testing", "changed");
            }
        });
    }

    @Override
    public void detachView() {
        mView = null;
        mPixabayDao.removeAllChangeListeners();
        if (!mRealm.isClosed()) {
            mRealm.close();
        }
        clearSubscriptions();
    }

    private void clearSubscriptions() {
        if (mDisposables != null) {
            mDisposables.clear();
        }
    }
}
