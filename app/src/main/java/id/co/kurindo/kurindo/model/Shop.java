package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aspire on 11/25/2016.
 */

public class Shop implements Parcelable{
    private int id;
    private String name;
    private String motto;
    private String link;
    private String backdrop;
    private String banner;
    private String backdropOnly;
    private String bannerOnly;
    private String status;
    private String code;
    private String category;
    private TUser pic;
    private Address address;
    private List<Product> products = new ArrayList<Product>();

    public Shop(){

    }
    public Shop(int id, String code, String name, String motto, String banner, String backdrop, String status){
        this.id = id;
        this.code = code;
        this.name= name;
        this.banner = banner;
        this.backdrop= backdrop;
        this.bannerOnly = banner;
        this.backdropOnly= backdrop;
        this.status= status;
        this.motto= motto;
    }
    public Shop(int id, String code, String name, String motto, String bannerOnly, String backdropOnly, String banner, String backdrop, String status){
        this.id = id;
        this.code = code;
        this.name= name;
        this.banner = banner;
        this.backdrop= backdrop;
        this.bannerOnly = bannerOnly;
        this.backdropOnly= backdropOnly;
        this.status= status;
        this.motto= motto;
    }
///*
    protected Shop(Parcel in) {
        id = in.readInt();
        name = in.readString();
        link = in.readString();
        backdrop = in.readString();
        banner = in.readString();
        backdropOnly = in.readString();
        bannerOnly = in.readString();
        status = in.readString();
        motto = in.readString();
        category = in.readString();
        try{
            pic = in.readParcelable(TUser.class.getClassLoader());
        }catch (Exception e){}
        try{
            address = in.readParcelable(Address.class.getClassLoader());
        }catch (Exception e){}

        try{
            products = new ArrayList<Product>();
            in.readTypedList(products, Product.CREATOR);
        }catch (Exception e){}
        }

    public static final Creator<Shop> CREATOR = new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
//*/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TUser getPic() {
        return pic;
    }

    public void setPic(TUser pic) {
        this.pic = pic;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getBackdropOnly() {
        return backdropOnly;
    }

    public void setBackdropOnly(String backdropOnly) {
        this.backdropOnly = backdropOnly;
    }

    public String getBannerOnly() {
        return bannerOnly;
    }

    public void setBannerOnly(String bannerOnly) {
        this.bannerOnly = bannerOnly;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    ///*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt( id);
        dest.writeString( name);
        dest.writeString( link);
        dest.writeString(backdrop);
        dest.writeString(banner);
        dest.writeString(backdropOnly);
        dest.writeString(bannerOnly);
        dest.writeString(status);
        dest.writeString(motto);
        dest.writeString(category);
        dest.writeParcelable( pic, flags);
        dest.writeParcelable( address, flags);
        dest.writeTypedList(products);
    }
 //  */
}
