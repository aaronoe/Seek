
package de.aaronoe.picsplash.data.model.singleItem;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exif implements Parcelable
{

    @SerializedName("make")
    @Expose
    private String make;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("exposure_time")
    @Expose
    private String exposureTime;
    @SerializedName("aperture")
    @Expose
    private String aperture;
    @SerializedName("focal_length")
    @Expose
    private String focalLength;
    @SerializedName("iso")
    @Expose
    private Integer iso;
    public final static Parcelable.Creator<Exif> CREATOR = new Creator<Exif>() {


        @SuppressWarnings({
            "unchecked"
        })
        public Exif createFromParcel(Parcel in) {
            Exif instance = new Exif();
            instance.make = ((String) in.readValue((String.class.getClassLoader())));
            instance.model = ((String) in.readValue((String.class.getClassLoader())));
            instance.exposureTime = ((String) in.readValue((String.class.getClassLoader())));
            instance.aperture = ((String) in.readValue((String.class.getClassLoader())));
            instance.focalLength = ((String) in.readValue((String.class.getClassLoader())));
            instance.iso = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public Exif[] newArray(int size) {
            return (new Exif[size]);
        }

    }
    ;

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(String exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = aperture;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public Integer getIso() {
        return iso;
    }

    public void setIso(Integer iso) {
        this.iso = iso;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(make);
        dest.writeValue(model);
        dest.writeValue(exposureTime);
        dest.writeValue(aperture);
        dest.writeValue(focalLength);
        dest.writeValue(iso);
    }

    public int describeContents() {
        return  0;
    }

}
