package client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client {
    private static final Pattern pattern = Pattern.compile("(# \\d+)");
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34567;
    private static final int RECORD_NO = 12;

    private final Scanner scanner = new Scanner(System.in);

    private static int getRecordNoFromResponse(String response) {
        final var matcher = pattern.matcher(response);
        if (matcher.find()) {
            final var group = matcher.group(0);
            return Integer.parseInt(group.split(" ")[1]);
        }
        throw new RuntimeException("Invalid response: " + response);
    }

    public void start() {
        try (
                final var socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                final var inputStream = new DataInputStream(socket.getInputStream());
                final var outputStream = new DataOutputStream(socket.getOutputStream());
        )
        {
            System.out.println("Client started!");
            String input;
            //do {
                //System.out.println("Provide input:");
                //input = scanner.nextLine();
                input = "Give me a record # " + RECORD_NO;
                outputStream.writeUTF(input);
                System.out.println("Sent: " + input);
                final var responseRecord = getRecordNoFromResponse(inputStream.readUTF());
                System.out.println("Received: A record # " + responseRecord + " was sent!");
            //} while (!input.equals("DONE"));
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException("Client failed.");
        }
        //System.out.println("Finished!");
    }

    public static void main(String[] args) {
        var client = new Client();
        client.start();
    }
}
