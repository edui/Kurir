package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by DwiM on 11/9/2016.
 */
public class News implements Parcelable{
    private int id;
    private String title;
    private String content;
    private String link;
    private String img;

    private String thumbnail;
    private String url;

    private int drawable;

    public News(){

    }
    public News(String title, String content, String link, String img){
        this.title = title;
        this.content = content;
        this.link = link;
        this.img = img;
    }
    public News(int id, String title, String content, String link, int img){
        this.id = id;
        this.title = title;
        this.content = content;
        this.link = link;
        this.drawable= img;
    }

    protected News(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        link = in.readString();
        img = in.readString();
        thumbnail = in.readString();
        url = in.readString();
        drawable = in.readInt();
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(link);
        dest.writeString(img);
        dest.writeString(thumbnail);
        dest.writeString(url);
        dest.writeInt(drawable);
    }
}
