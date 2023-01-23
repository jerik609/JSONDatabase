package server.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DatabaseGsonSerializer implements JsonSerializer<Database> {


    @Override
    public JsonElement serialize(Database src, Type typeOfSrc, JsonSerializationContext context) {

        final JsonObject myJson = new JsonObject();

        final Type mapOfJsonObjects = new TypeToken<HashMap<String, JsonObject>>(){}.getType();
        final var serializedDb = context.serialize(src.getDatabase(), mapOfJsonObjects);

        myJson.addProperty("capacity", src.getCapacity());
        myJson.add("database", serializedDb);

        return myJson;
    }
}
