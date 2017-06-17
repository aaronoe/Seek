
package de.aaronoe.picsplash.data.model.photosearch;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.aaronoe.picsplash.data.model.PhotosReply;

public class PhotoSearchReply implements Parcelable
{

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<PhotosReply> results = new ArrayList<>();
    public final static Parcelable.Creator<PhotoSearchReply> CREATOR = new Creator<PhotoSearchReply>() {


        @SuppressWarnings({
            "unchecked"
        })
        public PhotoSearchReply createFromParcel(Parcel in) {
            PhotoSearchReply instance = new PhotoSearchReply();
            instance.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (de.aaronoe.picsplash.data.model.PhotosReply.class.getClassLoader()));
            return instance;
        }

        public PhotoSearchReply[] newArray(int size) {
            return (new PhotoSearchReply[size]);
        }

    }
    ;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<PhotosReply> getResults() {
        return results;
    }

    public void setResults(List<PhotosReply> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(total);
        dest.writeValue(totalPages);
        dest.writeList(results);
    }

    public int describeContents() {
        return  0;
    }

}
