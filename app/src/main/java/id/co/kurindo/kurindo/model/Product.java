package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.android.tonyvu.sc.model.Saleable;

public class Product implements Saleable, Parcelable{
    private static final long serialVersionUID = -4073256626483275668L;

    private int id;
    private String category;
    private String type = "P";
    private String code;
    private String name;
    private String description;
    private String imageName;
    private BigDecimal price = new BigDecimal(0);
    private Integer quantity = new Integer(0);
    private BigDecimal discount = new BigDecimal(0);
    private BigDecimal weight = new BigDecimal(1);
    private String status;
    private String created;
    private int drawable;
    private ArrayList images;
    private int shopid = 1;
    private String notes="";
    private String unit="pcs";

    public Product() {
        super();
    }

    public Product(int pId, String pName, BigDecimal pPrice, String pDescription, String pImageName) {
        setId(pId);
        setName(pName);
        setPrice(pPrice);
        setDescription(pDescription);
        setImageName(pImageName);
    }

    public Product(int id, String code, String name, String description, String image, String price, String stock, String discount, String status, String created) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description =description;
        this.imageName = image;
        this.price = BigDecimal.valueOf( Double.parseDouble(price));
        this.quantity = Integer.valueOf(Integer.parseInt(stock));
        this.discount = BigDecimal.valueOf( Double.parseDouble(discount));;
        this.status = status;
        this.created = created;
    }

    public Product(int id, String code, String name, String description, int image, String price, String stock, String discount, String status, String created) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description =description;
        this.drawable = image;
        this.price = BigDecimal.valueOf( Double.parseDouble(price));
        this.quantity = Integer.valueOf(Integer.parseInt(stock));
        this.discount = BigDecimal.valueOf( Double.parseDouble(discount));;
        this.status = status;
        this.created = created;
    }

    protected Product(Parcel in) {
        id = in.readInt();
        type = in.readString();
        code = in.readString();
        name = in.readString();
        imageName = in.readString();
        price = new BigDecimal( in.readDouble() );
        quantity = in.readInt();
        discount = new BigDecimal( in.readDouble() );
        weight = new BigDecimal( in.readDouble() );
        status = in.readString();
        created = in.readString();
        drawable = in.readInt();
        shopid = in.readInt();
        notes= in.readString();
        unit= in.readString();
        description = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Product)) return false;

        return (this.id == ((Product) o).getId());
    }

    public int hashCode() {
        final int prime = 31;
        int hash = 1;
        hash = hash * prime + id;
        hash = hash * prime + (name == null ? 0 : name.hashCode());
        hash = hash * prime + (price == null ? 0 : price.hashCode());
        hash = hash * prime + (description == null ? 0 : description.hashCode());

        return hash;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public ArrayList getImages() {
        return images;
    }

    public void setImages(ArrayList images) {
        this.images = images;
    }

    public int getShopid() {
        return shopid;
    }

    public void setShopid(int shopid) {
        this.shopid = shopid;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String orderNotes) {
        this.notes = orderNotes;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    ///*
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(type);
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(imageName);
        dest.writeDouble(price == null? 0:price.doubleValue());
        dest.writeInt(quantity == null ? 0 : quantity);
        dest.writeDouble(discount == null ? 0 : discount.doubleValue());
        dest.writeDouble(weight == null ? 0 : weight.doubleValue());
        dest.writeString(status);
        dest.writeString(created);
        dest.writeInt(drawable);
        dest.writeInt(shopid);
        dest.writeString(notes);
        dest.writeString(unit);
        dest.writeString(description);
    }
    //*/
}
