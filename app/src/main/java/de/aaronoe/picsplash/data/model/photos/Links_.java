
package de.aaronoe.picsplash.data.model.photos;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Links_ implements Parcelable
{

    @SerializedName("self")
    @Expose
    private String self;
    @SerializedName("html")
    @Expose
    private String html;
    @SerializedName("download")
    @Expose
    private String download;
    @SerializedName("download_location")
    @Expose
    private String downloadLocation;
    public final static Parcelable.Creator<Links_> CREATOR = new Creator<Links_>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Links_ createFromParcel(Parcel in) {
            Links_ instance = new Links_();
            instance.self = ((String) in.readValue((String.class.getClassLoader())));
            instance.html = ((String) in.readValue((String.class.getClassLoader())));
            instance.download = ((String) in.readValue((String.class.getClassLoader())));
            instance.downloadLocation = ((String) in.readValue((String.class.getClassLoader())));
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

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getDownloadLocation() {
        return downloadLocation;
    }

    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(self);
        dest.writeValue(html);
        dest.writeValue(download);
        dest.writeValue(downloadLocation);
    }

    public int describeContents() {
        return  0;
    }

}
