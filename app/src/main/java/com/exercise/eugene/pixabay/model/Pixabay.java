
package com.exercise.eugene.pixabay.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pixabay {

    @SerializedName("totalHits")
    @Expose
    private Integer totalHits;

    @SerializedName("hits")
    @Expose
    private List<Hit> hits = null;

    @SerializedName("total")
    @Expose
    private Integer total;

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
