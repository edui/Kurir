package id.co.kurindo.kurindo.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by dwim on 3/19/2017.
 */

public class BaseHelper {
    protected Gson gson;
    {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.serializeNulls();
        gson = builder.create();
    }
}
