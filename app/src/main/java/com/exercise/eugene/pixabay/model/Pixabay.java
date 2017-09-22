
package com.exercise.eugene.pixabay.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Pixabay extends RealmObject {
    @PrimaryKey
    private int primaryKey;

    @SerializedName("totalHits")
    @Expose
    private Integer totalHits;

    @SerializedName("hits")
    @Expose
    private RealmList<Hit> hits = null;

    @SerializedName("total")
    @Expose
    private Integer total;

    @NonNull
    private Integer page = 1;

    private String url;


    public int getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public RealmList<Hit> getHits() {
        return hits;
    }

    public void setHits(RealmList<Hit> hits) {
        this.hits = hits;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
