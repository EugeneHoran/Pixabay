package com.exercise.eugene.pixabay.main;

import com.exercise.eugene.pixabay.BaseView;
import com.exercise.eugene.pixabay.client.PixabayService;
import com.exercise.eugene.pixabay.model.Hit;

import java.util.List;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showPixabayImageAdapter(List<Hit> hitList);

        void showCategoryRecycler(boolean show);

        void resetPixabayAdapter();

        void resetEndlessScrollListener();

        void setCategoryString(String category);

        void setSearchString(String search);

        void setActionbar(boolean showNavUp, String title);

        void showNoItems();

        void showErrorView(String errorMessage, String category, int page);

        void resetErrorView();
    }

    interface Presenter {
        void start();

        void detachView();

        void loadPixabayImages(String category, PixabayService.ORDER order, String query, int page);

        void searchData(String search);
    }
}
