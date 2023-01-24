package server.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

public class Persistence {
    private static final Logger log = Logger.getLogger(Persistence.class.getSimpleName());

    private static final String FILENAME_TEST_ENVIRONMENT = System.getProperty("user.dir") + "/src/server/data/db.json";
    private static final String FILENAME_LOCAL_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database/task/src/server/data/db.json";
    private static final String THE_LOCATION = FILENAME_TEST_ENVIRONMENT;

    private static final Gson gson = getGsonBuilder().create();

    private static GsonBuilder getGsonBuilder() {
        final var gsonBuilder = new GsonBuilder();
        return gsonBuilder
                .registerTypeAdapter(Database.class, new DatabaseGsonDeserializer())
                .registerTypeAdapter(Database.class, new DatabaseGsonSerializer())
                .excludeFieldsWithoutExposeAnnotation();
    }

    public static void persistDatabase(Database database) {
        final var serializedDb = gson.toJson(database);
        final var filePath = Paths.get(THE_LOCATION);

//        try {
//            Files.createDirectories(Paths.get(DATABASE_DIR));
//        } catch (IOException e) {
//            System.out.println("dir failed");
//            log.fine("Could not create directory: " + e);
//        }

        log.fine("Persisting DB to: " + THE_LOCATION);

        try (
                final FileWriter fw = new FileWriter(filePath.toFile());
                ) {
            fw.write(serializedDb);
            log.fine("DB persisted.");
        } catch (IOException e) {
            System.out.println("file failed");
            log.fine("Failed to persist the DB: " + e);
        }
    }

    public static Optional<Database> loadDbFromFile() {
        final var filePath = Paths.get(THE_LOCATION);

        log.fine("Trying to init DB from: " + THE_LOCATION);

        if (Files.exists(filePath)) {
            try {
                final var dbStr = Files.readString(filePath);
                log.fine(dbStr);
                log.fine("DB restored.");
                return Optional.of(gson.fromJson(dbStr, Database.class));
            } catch (IOException e) {
                log.fine("Could not read the DB file: " + e);
                return Optional.empty();
            }
        } else {
            log.fine("Can't find file: " + THE_LOCATION);
            return Optional.empty();
        }
    }
}
