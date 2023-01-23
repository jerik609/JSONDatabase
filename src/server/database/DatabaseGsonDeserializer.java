package server.database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DatabaseGsonDeserializer implements JsonDeserializer<Database> {
    @Override
    public Database deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement jsonDatabaseCapacity = jsonObject.get("capacity");
        JsonElement jsonDatabaseDatabase = jsonObject.get("database");

        final var capacity = jsonDatabaseCapacity == null ? 1000 : jsonDatabaseCapacity.getAsInt();

        Type mapOfJsonObjects = new TypeToken<HashMap<String, JsonObject>>(){}.getType();

        final HashMap<String, JsonElement> database = jsonDatabaseDatabase == null ?
                new HashMap<>(capacity) : context.deserialize(jsonDatabaseDatabase, mapOfJsonObjects);

        return new Database(capacity, database);
    }
}