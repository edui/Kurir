package id.co.kurindo.kurindo.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DwiM on 11/9/2016.
 */
public class ImageModel implements Parcelable {

    String name, url;
    int drawable;
    public ImageModel() {

    }
    public ImageModel(String url, String name) {
        this.url = url;
        this.name = name;
    }
    public ImageModel(int drawable, String name) {
        this.drawable= drawable;
        this.name = name;
    }

    protected ImageModel(Parcel in) {
        name = in.readString();
        url = in.readString();
        drawable= in.readInt();
    }

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeInt(drawable);
    }
}