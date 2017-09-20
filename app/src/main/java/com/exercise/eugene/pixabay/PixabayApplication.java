package com.exercise.eugene.pixabay;

import android.app.Application;
import android.content.Context;

import com.exercise.eugene.pixabay.client.PixabayService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class PixabayApplication extends Application {
    private PixabayService service;
    private Scheduler defaultSubscribeScheduler;

    private static PixabayApplication get(Context context) {
        return (PixabayApplication) context.getApplicationContext();
    }

    public static PixabayApplication create(Context context) {
        return PixabayApplication.get(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public PixabayService getService() {
        if (service == null) {
            service = PixabayService.Factory.create();
        }
        return service;
    }

    // “work” on io thread
    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }
}
