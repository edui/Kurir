package id.co.kurindo.kurindo.util;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Created by DwiM on 5/18/2017.
 */

public class LatLngSerializer implements JsonSerializer<LatLng>, JsonDeserializer<LatLng> {

    @Override
    public JsonElement serialize(LatLng latLng, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("latitude", latLng.latitude);
        result.addProperty("longitude", latLng.longitude);

        return result;
    }

    @Override
    public LatLng deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jobject = json.getAsJsonObject();

        return new LatLng(
                jobject.get("lat").getAsDouble(),
                jobject.get("lng").getAsDouble());
    }
}