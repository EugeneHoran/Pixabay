
package com.exercise.eugene.pixabay.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Hit extends RealmObject implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("type")
    @Expose
    private String type; // Possible option "all", "photo", "illustration", "vector"
    @SerializedName("pageURL")
    @Expose
    private String pageURL; // Source page on Pixabay, which provides a download link for the original image of the dimension imageWidth x imageHeight and the file size imageSize.

    /**
     * Image Preview
     * <p>
     * Low resolution images with a maximum width or height of 150 px (previewWidth x previewHeight).
     * <p>
     * Calculate Aspect Ratio
     */
    @SerializedName("previewHeight")
    @Expose
    private Integer previewHeight;
    @SerializedName("previewWidth")
    @Expose
    private Integer previewWidth;
    @SerializedName("previewURL")
    @Expose
    private String previewURL;

    public float getPreviewImageRatio() {
        return (float) getPreviewHeight() / getPreviewWidth();
    }

    /**
     * Image Medium Size
     * <p>
     * Medium sized image with a maximum width or height of 640 px (webformatWidth x webformatHeight). URL valid for 24 hours.
     * <p>
     * Calculate Aspect Ratio
     */
    @SerializedName("webformatHeight")
    @Expose
    private Integer webformatHeight;
    @SerializedName("webformatWidth")
    @Expose
    private Integer webformatWidth;
    @SerializedName("webformatURL")
    @Expose
    private String webformatURL;

    public float getWebImageFormatRatio() {
        return (float) getWebformatHeight() / getWebformatWidth();
    }

    /**
     * Original Image Size
     */
    @SerializedName("imageWidth")
    @Expose
    private Integer imageWidth;
    @SerializedName("imageHeight")
    @Expose
    private Integer imageHeight;


    /**
     * Social
     */
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("likes")
    @Expose
    private Integer likes;
    @SerializedName("favorites")
    @Expose
    private Integer favorites;
    @SerializedName("views")
    @Expose
    private Integer views;
    @SerializedName("comments")
    @Expose
    private Integer comments;
    @SerializedName("downloads")
    @Expose
    private Integer downloads;

    /**
     * Uploader Details
     */
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("userImageURL")
    @Expose
    private String userImageURL;


    public Integer getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(Integer previewHeight) {
        this.previewHeight = previewHeight;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getWebformatHeight() {
        return webformatHeight;
    }

    public void setWebformatHeight(Integer webformatHeight) {
        this.webformatHeight = webformatHeight;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getWebformatWidth() {
        return webformatWidth;
    }

    public void setWebformatWidth(Integer webformatWidth) {
        this.webformatWidth = webformatWidth;
    }

    public Integer getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(Integer previewWidth) {
        this.previewWidth = previewWidth;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public String getPageURL() {
        return pageURL;
    }

    public void setPageURL(String pageURL) {
        this.pageURL = pageURL;
    }

    public String getPreviewURL() {
        return previewURL;
    }

    public void setPreviewURL(String previewURL) {
        this.previewURL = previewURL;
    }

    public String getWebformatURL() {
        return webformatURL;
    }

    public void setWebformatURL(String webformatURL) {
        this.webformatURL = webformatURL;
    }

    public Integer getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(Integer imageWidth) {
        this.imageWidth = imageWidth;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserImageURL() {
        return userImageURL;
    }

    public void setUserImageURL(String userImageURL) {
        this.userImageURL = userImageURL;
    }

    public Integer getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(Integer imageHeight) {
        this.imageHeight = imageHeight;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.type);
        dest.writeString(this.pageURL);
        dest.writeValue(this.previewHeight);
        dest.writeValue(this.previewWidth);
        dest.writeString(this.previewURL);
        dest.writeValue(this.webformatHeight);
        dest.writeValue(this.webformatWidth);
        dest.writeString(this.webformatURL);
        dest.writeValue(this.imageWidth);
        dest.writeValue(this.imageHeight);
        dest.writeString(this.tags);
        dest.writeValue(this.likes);
        dest.writeValue(this.favorites);
        dest.writeValue(this.views);
        dest.writeValue(this.comments);
        dest.writeValue(this.downloads);
        dest.writeValue(this.userId);
        dest.writeString(this.user);
        dest.writeString(this.userImageURL);
    }

    public Hit() {
    }

    protected Hit(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.type = in.readString();
        this.pageURL = in.readString();
        this.previewHeight = (Integer) in.readValue(Integer.class.getClassLoader());
        this.previewWidth = (Integer) in.readValue(Integer.class.getClassLoader());
        this.previewURL = in.readString();
        this.webformatHeight = (Integer) in.readValue(Integer.class.getClassLoader());
        this.webformatWidth = (Integer) in.readValue(Integer.class.getClassLoader());
        this.webformatURL = in.readString();
        this.imageWidth = (Integer) in.readValue(Integer.class.getClassLoader());
        this.imageHeight = (Integer) in.readValue(Integer.class.getClassLoader());
        this.tags = in.readString();
        this.likes = (Integer) in.readValue(Integer.class.getClassLoader());
        this.favorites = (Integer) in.readValue(Integer.class.getClassLoader());
        this.views = (Integer) in.readValue(Integer.class.getClassLoader());
        this.comments = (Integer) in.readValue(Integer.class.getClassLoader());
        this.downloads = (Integer) in.readValue(Integer.class.getClassLoader());
        this.userId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.user = in.readString();
        this.userImageURL = in.readString();
    }

    public static final Parcelable.Creator<Hit> CREATOR = new Parcelable.Creator<Hit>() {
        @Override
        public Hit createFromParcel(Parcel source) {
            return new Hit(source);
        }

        @Override
        public Hit[] newArray(int size) {
            return new Hit[size];
        }
    };
}
