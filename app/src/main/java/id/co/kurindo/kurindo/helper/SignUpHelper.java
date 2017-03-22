package id.co.kurindo.kurindo.helper;

import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by dwim on 3/18/2017.
 */

public class SignUpHelper extends BaseHelper{
    private static SignUpHelper helper;
    public static SignUpHelper getInstance(){
        if(helper == null) helper = new SignUpHelper();
        return helper;
    }

    private TUser user;

    public TUser getUser() {
        return user;
    }

    public void setUser(TUser user) {
        this.user = user;
    }
}
