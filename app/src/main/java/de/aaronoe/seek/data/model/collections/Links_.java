
package de.aaronoe.seek.data.model.collections;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links_ implements Parcelable
{

    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("photos")
    @Expose
    private String photos;
    public final static Parcelable.Creator<Links_> CREATOR = new Creator<Links_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Links_ createFromParcel(Parcel in) {
            Links_ instance = new Links_();
            instance.self = ((String) in.readValue((String.class.getClassLoader())));
            instance.photos = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Links_[] newArray(int size) {
            return (new Links_[size]);
        }

    }
    ;

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(self);
        dest.writeValue(photos);
    }

    public int describeContents() {
        return  0;
    }

}
