package server.database;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class Database<T> {
    private static final int DATABASE_CAPACITY = 100;

    private final HashMap<String, T> database = new HashMap<>(DATABASE_CAPACITY);

    public DatabaseResult<T> get(String key) {
        var builder = new DatabaseResult.Builder<T>();

        var item = database.get(key);
        if (item == null) {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        } else {
            builder.responseCode(ResponseCode.OK);
        }
        builder.data(item);

        return builder.build();
    }

    public DatabaseResult<T> set(String key, T value) {
        database.put(key, value);
        var builder = new DatabaseResult.Builder<T>();
        builder.responseCode(ResponseCode.OK);
        builder.data(null);
        return builder.build();
    }

    public DatabaseResult<T> delete(String key) {
        var builder = new DatabaseResult.Builder<T>();

        if (database.containsKey(key)) {
            builder.responseCode(ResponseCode.OK);
            database.remove(key);
        } else {
            builder.responseCode(ResponseCode.ERROR_NO_DATA);
        }

        return builder.build();
    }
}
