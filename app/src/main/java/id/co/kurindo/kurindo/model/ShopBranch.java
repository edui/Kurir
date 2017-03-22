package id.co.kurindo.kurindo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dwim on 3/16/2017.
 */

public class ShopBranch implements Parcelable {
    private Shop shop;
    private TUser pic;

    public ShopBranch(){

    }

    protected ShopBranch(Parcel in) {
    }

    public static final Creator<ShopBranch> CREATOR = new Creator<ShopBranch>() {
        @Override
        public ShopBranch createFromParcel(Parcel in) {
            return new ShopBranch(in);
        }

        @Override
        public ShopBranch[] newArray(int size) {
            return new ShopBranch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public TUser getPic() {
        return pic;
    }

    public void setPic(TUser pic) {
        this.pic = pic;
    }

}
