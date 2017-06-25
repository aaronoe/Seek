package de.aaronoe.seek.data.model.collectionsearch;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.aaronoe.seek.data.model.collections.Collection;
import de.aaronoe.seek.data.model.photos.PhotosReply;

/**
 * Created by aaron on 19.06.17.
 *
 */


public class CollectionSearchReply implements Parcelable {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("results")
    @Expose
    private List<Collection> results = new ArrayList<>();
    public final static Parcelable.Creator<CollectionSearchReply> CREATOR = new Creator<CollectionSearchReply>() {


        @SuppressWarnings({
                "unchecked"
        })
        public CollectionSearchReply createFromParcel(Parcel in) {
            CollectionSearchReply instance = new CollectionSearchReply();
            instance.total = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.totalPages = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (PhotosReply.class.getClassLoader()));
            return instance;
        }

        public CollectionSearchReply[] newArray(int size) {
            return (new CollectionSearchReply[size]);
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

    public List<Collection> getResults() {
        return results;
    }

    public void setResults(List<Collection> results) {
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

