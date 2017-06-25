
package de.aaronoe.seek.data.model.collections;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links____ implements Parcelable
{

    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("photos")
    @Expose
    private String photos;
    @SerializedName("related")
    @Expose
    private String related;
    public final static Parcelable.Creator<Links____> CREATOR = new Creator<Links____>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Links____ createFromParcel(Parcel in) {
            Links____ instance = new Links____();
            instance.self = ((String) in.readValue((String.class.getClassLoader())));
            instance.html = ((String) in.readValue((String.class.getClassLoader())));
            instance.photos = ((String) in.readValue((String.class.getClassLoader())));
            instance.related = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Links____[] newArray(int size) {
            return (new Links____[size]);
        }

    }
    ;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(self);
        dest.writeValue(html);
        dest.writeValue(photos);
        dest.writeValue(related);
    }

    public int describeContents() {
        return  0;
    }

}
