package common.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Request {
    Gson gson = gsonProvider();

    private static Gson gsonProvider() {
        final var gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    private static String convertToJson(Request request) {
        return gson.toJson(request);
    }

    default String getPayload() {
        return convertToJson(this);
    }
}
