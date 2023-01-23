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

    public void runWithParams() {
        run(true);
    }
    public void runInteractive() {
        run(false);
    }

    private static String buildJsonRequestFromFile(String filePath) throws IOException {
        final var jsonStr = Files.readString(Paths.get(THE_LOCATION + filePath));
        // read into json to validate
        final var jsonObject = gson.fromJson(jsonStr, JsonObject.class);
        System.out.print("Sent: " + jsonStr);
        return gson.toJson(jsonObject);
//        if (//json instanceof JsonObject jsonObject &&
//                json.get("value") != null &&
//                        !json.get("value").isJsonPrimitive()) {
//            System.out.println("Sent:\n" + prettyGson.toJson(json));
//        } else {
//            System.out.println("Sent: " + gson.toJson(json));
//        }
    }

    private static String buildJsonRequestFromParams(String type, String key, String value) {
        final var jsonObject = new JsonObject();
        jsonObject.add("type", new JsonPrimitive(type));
        jsonObject.add("key", new JsonPrimitive(key));
        jsonObject.add("value", new JsonPrimitive(value));
        final var jsonStr = gson.toJson(jsonObject);
        System.out.println("Sent: " + jsonStr);
        return jsonStr;
    }

    private static void sendRequest(DataOutputStream outputStream, Message request) throws IOException {
        final var wireFormat = request.getWireFormat();
        outputStream.writeUTF(wireFormat);
    }

    private void run(boolean withParams) {
        try (
                final var socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                final var inputStream = new DataInputStream(socket.getInputStream());
                final var outputStream = new DataOutputStream(socket.getOutputStream());
        ) {
            System.out.println("Client started!");
            socket.setSoTimeout(5000);

            do {
                if (!withParams) {
                    System.out.println("Provide input (or DONE for type to quit):");
                    System.out.print("type: ");
                    type = scanner.nextLine();
                    System.out.print("key: ");
                    key = scanner.nextLine();
                    System.out.print("message: ");
                    value = scanner.nextLine();
                }

                if (type == null || !type.equals("DONE")) {

                    Message msgReq;
                    if (withParams && filePath != null && !filePath.isEmpty()) {
                        msgReq = new Message(buildJsonRequestFromFile(filePath));
                    } else {
                        msgReq = new Message(buildJsonRequestFromParams(type, key, value));
                    }

                    sendRequest(outputStream, msgReq);

                    // receive response
                    final var responseStr = inputStream.readUTF();
                    final var message = Message.fromJson(responseStr);
                    final var response = message.getResponse();

                    if (response instanceof JsonObject jsonObject &&
                            jsonObject.get("value") != null &&
                            !jsonObject.get("value").isJsonPrimitive()) {
                        System.out.println("Received:\n" + prettyGson.toJson(response));
                    } else {
                        System.out.println("Received: " + gson.toJson(response));
                    }
                    System.out.println();
                }

                // if with params, stop immediately (???)
                if (withParams) {
                    type = "DONE";
                }

            } while (!type.equals("DONE"));

        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace();
            throw new RuntimeException("Client failed: " + e);
        }
        //System.out.println("Finished!");
    }
}
