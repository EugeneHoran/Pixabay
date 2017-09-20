package com.exercise.eugene.pixabay.main;

import com.exercise.eugene.pixabay.BaseView;
import com.exercise.eugene.pixabay.client.PixabayService;
import com.exercise.eugene.pixabay.model.Hit;

import java.util.List;

public interface MainContract {
    interface View extends BaseView<Presenter> {
        void showPixabayImageAdapter(List<Hit> hitList);
    }

    interface Presenter {
        void start();

        void detachView();

        void loadPixabayImages(String category, PixabayService.ORDER order, int page);
    }
}
