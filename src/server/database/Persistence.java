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

    private static final String DATABASE_DIR = "/server/data";
    private static final String DATABASE_FILE = "db.json";

    private static final Gson gson = new GsonBuilder().create();

    public static void persistDatabase(Database<String> database) {
        final var serializedDb = gson.toJson(database);
        final var filePath = Paths.get(FILENAME_LOCAL_ENVIRONMENT);

//        try {
//            Files.createDirectories(Paths.get(DATABASE_DIR));
//        } catch (IOException e) {
//            System.out.println("dir failed");
//            log.fine("Could not create directory: " + e);
//        }

        log.fine("Persisting DB to: " + FILENAME_LOCAL_ENVIRONMENT);

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

    public static Optional<Database<String>> loadDbFromFile() {
        final var filePath = Paths.get(FILENAME_LOCAL_ENVIRONMENT);

        log.fine("Trying to init DB from: " + FILENAME_LOCAL_ENVIRONMENT);

        if (Files.exists(filePath)) {
            try {
                final var dbStr = Files.readString(filePath);
                log.fine("DB restored.");
                return Optional.of(gson.fromJson(dbStr, Database.class));
            } catch (IOException e) {
                throw new RuntimeException("Could not read the DB file: " + e);
            }
        } else {
            System.out.println("Can't find /server/data/db.json file.");
            throw new RuntimeException("no file");
            //return Optional.empty();
        }
    }
}
