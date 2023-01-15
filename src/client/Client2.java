package client;


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Client2 {
    private static final Pattern pattern = Pattern.compile("(# \\d+)");
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34567;

    private final Scanner scanner = new Scanner(System.in);

    String type = "exit";
    int index = 0;
    String message = "null";

    private static int getRecordNoFromResponse(String response) {
        final var matcher = pattern.matcher(response);
        if (matcher.find()) {
            final var group = matcher.group(0);
            return Integer.parseInt(group.split(" ")[1]);
        }
        throw new RuntimeException("Invalid response: " + response);
    }

    public void run(boolean withParams) {
        try (
                final var socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                final var inputStream = new DataInputStream(socket.getInputStream());
                final var outputStream = new DataOutputStream(socket.getOutputStream());
        )
        {
            System.out.println("Client started!");

            if (withParams) {
                var command = type;
                command += index != 0 ? " " + index : "";
                command += message.equals("null") ? "" : " " + message;
                System.out.println("Sent: " + command);
                outputStream.writeUTF(command);

                // receive response
                var response = inputStream.readUTF();
                System.out.println("Received: " + response);
            } else {

                String input;
                do {
                    System.out.println("Provide input:");
                    input = scanner.nextLine();

                    System.out.println("Sent: " + input);
                    outputStream.writeUTF(input);

                    final var responseRecord = inputStream.readUTF(); //getRecordNoFromResponse(inputStream.readUTF());
                    System.out.println("Response: " + responseRecord);
                } while (!input.equals("DONE"));
            }

        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host: " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            //throw new RuntimeException("Client failed.");
        }
        //System.out.println("Finished!");
    }

    public static void main2(String[] args) {
        var client = new Client2();
        client.run(true);
    }
}
