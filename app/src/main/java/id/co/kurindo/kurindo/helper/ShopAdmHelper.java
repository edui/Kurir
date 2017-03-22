package id.co.kurindo.kurindo.helper;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.ShopBranch;
import id.co.kurindo.kurindo.util.ImageLoadingUtils;

/**
 * Created by dwim on 3/16/2017.
 */

public class ShopAdmHelper extends BaseHelper {
    private Shop shop;
    private ShopBranch shopBranch;
    private Bitmap bitmapBrand;
    private Bitmap bitmapBackdrop;

    private Product product;
    private Bitmap bitmapProduct;

    public boolean editMode = false;

    private static ShopAdmHelper helper;
    public static ShopAdmHelper getInstance(){
        if(helper == null) helper = new ShopAdmHelper();
        return helper;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public ShopBranch getShopBranch() {
        return shopBranch;
    }

    public void setShopBranch(ShopBranch shopBranch) {
        this.shopBranch = shopBranch;
    }

    public Bitmap getBitmapBrand() {
        return bitmapBrand;
    }

    public void setBitmapBrand(Bitmap bitmapBrand) {
        this.bitmapBrand = bitmapBrand;
    }

    public Bitmap getBitmapBackdrop() {
        return bitmapBackdrop;
    }

    public void setBitmapBackdrop(Bitmap bitmapBackdrop) {
        this.bitmapBackdrop = bitmapBackdrop;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getBitmapBrandString() {
        return (bitmapBrand ==null ? "": ImageLoadingUtils.getStringImage(bitmapBrand, 100));
    }
    public String getBitmapBackdropString() {
        return (bitmapBackdrop ==null ? "" :ImageLoadingUtils.getStringImage(bitmapBackdrop, 100) );
    }
    public String getBitmapProductString() {
        return (bitmapProduct ==null ? "" : ImageLoadingUtils.getStringImage(bitmapProduct, 100) );
    }

    public String getShopJson() {
        return gson.toJson(shop);
    }
    public String getProductJson() {
        return gson.toJson(product);
    }

    public Bitmap getBitmapProduct() {
        return bitmapProduct;
    }

    public void setBitmapProduct(Bitmap bitmapProduct) {
        this.bitmapProduct = bitmapProduct;
    }
}
