
package de.aaronoe.picsplash.data.model.singleItem;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrentUserCollection implements Parcelable
{

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("published_at")
    @Expose
    private String publishedAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("curated")
    @Expose
    private Boolean curated;
    @SerializedName("cover_photo")
    @Expose
    private CoverPhoto coverPhoto;
    @SerializedName("user")
    @Expose
    private User_ user;
    @SerializedName("links")
    @Expose
    private Links____ links;
    public final static Parcelable.Creator<CurrentUserCollection> CREATOR = new Creator<CurrentUserCollection>() {


        @SuppressWarnings({
            "unchecked"
        })
        public CurrentUserCollection createFromParcel(Parcel in) {
            CurrentUserCollection instance = new CurrentUserCollection();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.publishedAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.updatedAt = ((String) in.readValue((String.class.getClassLoader())));
            instance.curated = ((Boolean) in.readValue((Boolean.class.getClassLoader())));
            instance.coverPhoto = ((CoverPhoto) in.readValue((CoverPhoto.class.getClassLoader())));
            instance.user = ((User_) in.readValue((User_.class.getClassLoader())));
            instance.links = ((Links____) in.readValue((Links____.class.getClassLoader())));
            return instance;
        }

        public CurrentUserCollection[] newArray(int size) {
            return (new CurrentUserCollection[size]);
        }

    }
    ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public CoverPhoto getCoverPhoto() {
        return coverPhoto;
    }

    public void setCoverPhoto(CoverPhoto coverPhoto) {
        this.coverPhoto = coverPhoto;
    }

    public User_ getUser() {
        return user;
    }

    public void setUser(User_ user) {
        this.user = user;
    }

    public Links____ getLinks() {
        return links;
    }

    public void setLinks(Links____ links) {
        this.links = links;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(title);
        dest.writeValue(publishedAt);
        dest.writeValue(updatedAt);
        dest.writeValue(curated);
        dest.writeValue(coverPhoto);
        dest.writeValue(user);
        dest.writeValue(links);
    }

    public int describeContents() {
        return  0;
    }

}
