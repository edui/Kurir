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
    private String telepon;
    private String owner;
    private String link;
    private String backdrop;
    private String banner;
    private String backdropOnly;
    private String bannerOnly;
    private String status;
    private String code;
    private String category;
    private Address address;
    private List<Product> products = new ArrayList<Product>();

    public Shop(){

    }
    public Shop(int id, String code, String name, String motto, String banner, String backdrop, String telepon, String alamat, String status, String city, String cityText){
        this.id = id;
        this.code = code;
        this.name= name;
        this.banner = banner;
        this.backdrop= backdrop;
        this.bannerOnly = banner;
        this.backdropOnly= backdrop;
        this.telepon= telepon;
        this.status= status;
        this.motto= motto;
        address = new Address();
        address .setAlamat(alamat);
        address.setCity( new City(city, cityText) );
    }
    public Shop(int id, String code, String name, String motto, String bannerOnly, String backdropOnly, String banner, String backdrop, String telepon, String alamat, String status, String city, String cityText){
        this.id = id;
        this.code = code;
        this.name= name;
        this.banner = banner;
        this.backdrop= backdrop;
        this.bannerOnly = bannerOnly;
        this.backdropOnly= backdropOnly;
        this.telepon= telepon;
        this.status= status;
        this.motto= motto;
        address = new Address();
        address .setAlamat(alamat);
        address.setCity( new City(city, cityText) );
    }
///*
    protected Shop(Parcel in) {
        id = in.readInt();
        name = in.readString();
        telepon = in.readString();
        owner = in.readString();
        link = in.readString();
        backdrop = in.readString();
        banner = in.readString();
        backdropOnly = in.readString();
        bannerOnly = in.readString();
        status = in.readString();
        motto = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        products = new ArrayList<Product>();
        in.readTypedList(products, Product.CREATOR);
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
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

    ///*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt( id);
        dest.writeString( name);
        dest.writeString( telepon);
        dest.writeString( owner);
        dest.writeString( link);
        dest.writeString(backdrop);
        dest.writeString(banner);
        dest.writeString(backdropOnly);
        dest.writeString(bannerOnly);
        dest.writeString(status);
        dest.writeString(motto);
        dest.writeParcelable( address, flags);
        dest.writeTypedList(products);
    }
 //  */
}
