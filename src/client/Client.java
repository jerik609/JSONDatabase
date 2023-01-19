package client;

import com.beust.jcommander.Parameter;
import common.Message;
import common.request.DeleteRemoteRequest;
import common.request.GetRemoteRequest;
import common.request.RemoteRequest;
import common.request.SetRemoteRequest;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    private static final Pattern pattern = Pattern.compile("(# \\d+)");
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34567;

    private final Scanner scanner = new Scanner(System.in);

    @Parameter(names={"--type", "-t"})
    String type;
    @Parameter(names={"--index", "-i"})
    String index;
    @Parameter(names={"--message", "-m"})
    String message;

    private static int getRecordNoFromResponse(String response) {
        final var matcher = pattern.matcher(response);
        if (matcher.find()) {
            final var group = matcher.group(0);
            return Integer.parseInt(group.split(" ")[1]);
        }
        throw new RuntimeException("Invalid response: " + response);
    }

    private static RemoteRequest buildRequest(String requestType, String key, String value) {
        return switch (requestType) {
            case "get" -> new GetRemoteRequest(key);
            case "set" -> new SetRemoteRequest(key, value);
            case "delete" -> new DeleteRemoteRequest(key);
            default -> throw new RuntimeException("Invalid request: " + requestType);
        };
    }

    public void run(boolean withParams) {
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
                    System.out.println("type: ");
                    type = scanner.nextLine();
                    System.out.println("key: ");
                    index = scanner.nextLine();
                    System.out.println("message: ");
                    message = scanner.nextLine();
                } else {
                    type = "DONE";
                }

                final var request = buildRequest(type, index, message);
                final var message = new Message(request);
                final var wireFormat = message.getWireFormat();

                // send request
                outputStream.writeUTF(wireFormat);
                System.out.println("Sent: " + wireFormat);

                // receive response
                final var response = inputStream.readUTF();
                System.out.println("Received: " + response);
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
