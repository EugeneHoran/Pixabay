package com.exercise.eugene.pixabay.client;

public class Filter {

    public enum TYPE {
        ALL,
        CATEGORY,
        SEARCH,
    }

    public enum ORDER {
        POPULAR,
        LATEST
    }

    public static String order(ORDER order) {
        switch (order) {
            case POPULAR:
                return "popular";
            case LATEST:
                return "latest";
            default:
                return "popular";
        }
    }
}
