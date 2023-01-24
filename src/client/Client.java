package client;

import com.beust.jcommander.Parameter;
import com.google.gson.*;
import common.Message;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {
    private static final Gson gson = new GsonBuilder().create();
    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34567;

    private static final String FILE_REQUEST_PATH_TEST_ENVIRONMENT = System.getProperty("user.dir") + "/src/client/data/";
    private static final String FILE_REQUEST_PATH_LOCAL_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database/task/src/client/data/";
    private static final String THE_LOCATION = FILE_REQUEST_PATH_TEST_ENVIRONMENT;

    private final Scanner scanner = new Scanner(System.in);

    @Parameter(names={"--type", "-t"})
    String type = "";
    @Parameter(names={"--key", "-k"})
    String key = "";
    @Parameter(names={"--value", "-v"})
    String value = "";
    @Parameter(names={"--input_file", "-in"})
    String filePath = "";

    private static JsonObject buildJsonRequestFromFile(String filePath) throws IOException {
        final var jsonStr = Files.readString(Paths.get(THE_LOCATION + filePath));
        // read into json to validate
        return gson.fromJson(jsonStr, JsonObject.class);
    }

    private static JsonObject buildJsonRequestFromParams(String type, String key, String value) {
        final var jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive(type));
        jsonObject.add("key", new JsonPrimitive(key));
        jsonObject.add("value", new JsonPrimitive(value));
        return jsonObject;
    }

    private static Message buildRequestMessage(String type, String key, String value, String filePath) throws IOException {
        JsonObject payload;
        if (filePath != null && !filePath.isEmpty()) {
            payload = buildJsonRequestFromFile(filePath);
        } else {
            payload = buildJsonRequestFromParams(type, key, value);
        }
        return new Message(payload);
    }

    private static void sendMessage(DataOutputStream outputStream, Message request) throws IOException {
        final var wireFormat = request.getWireFormat();
        outputStream.writeUTF(wireFormat);
    }

    public void run() {
        try (
                final var socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                final var inputStream = new DataInputStream(socket.getInputStream());
                final var outputStream = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");
            socket.setSoTimeout(5000);

            // send request message
            final var request = buildRequestMessage(type, key, value, filePath);
            sendMessage(outputStream, request);
            prettyPrint("Sent", request.getPayload());

            // receive response message
            final var response = Message.fromJson(inputStream.readUTF());
            final var payload = response.getPayload();
            prettyPrint("Received", payload);

            //System.out.println();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw new RuntimeException("Client failed: " + e);
        }
        //System.out.println("Finished!");
    }

    private static void prettyPrint(String op, JsonObject obj) {
//        if (obj.get("value") != null &&  !obj.get("value").isJsonPrimitive()) {
//            System.out.println(op + ":\n" + prettyGson.toJson(obj));
//        } else {
            System.out.println(op + ": " + gson.toJson(obj));
//        }
    }
}
