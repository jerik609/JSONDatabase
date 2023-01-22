package server.database;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DatabaseGsonDeserializer implements JsonDeserializer<Database> {
    @Override
    public Database deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement jsonDatabaseCapacity = jsonObject.get("capacity");
        JsonElement jsonDatabaseEmptyValue = jsonObject.get("emptyValue");
        JsonElement jsonDatabaseDatabase = jsonObject.get("database");

        final var capacity = jsonDatabaseCapacity == null ? 1000 : jsonDatabaseCapacity.getAsInt();
        final var emptyValue = jsonDatabaseEmptyValue == null ? "" : jsonDatabaseEmptyValue.getAsString();
        final HashMap<String, JsonObject> database = jsonDatabaseDatabase == null ?
                new HashMap<>(capacity) : context.deserialize(jsonDatabaseDatabase, HashMap.class);

        return new Database(capacity, database);
    }
}