package id.co.kurindo.kurindo.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by dwim on 3/13/2017.
 */

public class ParserUtil {
    Gson gson ;
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();
    }
    public Shop parserTShop(JSONObject jObj){
        return gson.fromJson(jObj.toString(), Shop.class);
    }
    public Shop parserShop(JSONObject obj){
        Shop shop = null;
        try {
                int id = obj.getInt("id");
                String code = obj.getString("code");
                String name = obj.getString("name");
                String link = obj.getString("link");
                String banner = obj.getString("banner");
                String backdrop = obj.getString("backdrop");
                //String bannerOnly = obj.getString("bannerOnly");
                //String backdropOnly = obj.getString("backdropOnly");
                String phone = obj.getString("phone");
                String status= obj.getString("status");
                String motto = obj.getString("description");

            JSONObject jAddr = obj.getJSONObject("address");
            JSONObject jCt = jAddr.getJSONObject("city");
            String city= jCt.getString("code");
            String cityText= jCt.getString("text");

            JSONObject jLoc = jAddr.getJSONObject("location");
            double latitude= jLoc.getDouble("latitude");
            double longitude= jLoc.getDouble("longitude");
            String alamat= jAddr.getString("alamat");
            String desa= jAddr.getString("desa");
            String kecamatan= jAddr.getString("kecamatan");
            String kabupaten= jAddr.getString("kabupaten");
            String propinsi= jAddr.getString("propinsi");
            String negara= jAddr.getString("negara");
            String kodepos= jAddr.getString("kodepos");
            Address addr = new Address();
            addr.setAlamat(alamat);
            addr.setLocation(new LatLng(latitude, longitude));
            addr.setDesa(desa);
            addr.setKecamatan(kecamatan);
            addr.setKabupaten(kabupaten);
            addr.setPropinsi(propinsi);
            addr.setNegara(negara);
            addr.setKodepos(kodepos);
            addr.setCity(new City(kecamatan, kecamatan));

            shop = new Shop(id, code, name, motto, banner, backdrop, status);

            JSONObject jPic = obj.getJSONObject("pic");
            String nama= jPic.getString("name");
            String telepon= jPic.getString("phone");
            String email= jPic.getString("email");

            TUser user = new TUser();
            user.setPhone(telepon);
            user.setFirstname(nama);
            user.setEmail(email);
            user.setAddress(addr); //

            shop.setPic(user);
            shop.setAddress(addr);

        }catch (Exception e){
            e.printStackTrace();
        }
        return shop;
    }

    public TUser parserUser(JSONObject jObj) {
        return gson.fromJson(jObj.toString(), TUser.class);
    }
}
