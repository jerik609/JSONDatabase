package client;

import com.beust.jcommander.Parameter;
import common.Message;
import common.request.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 34567;

    private final Scanner scanner = new Scanner(System.in);

    @Parameter(names={"--type", "-t"})
    String type;
    @Parameter(names={"--index", "-i"})
    String index;
    @Parameter(names={"--message", "-m"})
    String message;

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
                    System.out.print("type: ");
                    type = scanner.nextLine();
                    System.out.print("key: ");
                    index = scanner.nextLine();
                    System.out.print("message: ");
                    message = scanner.nextLine();
                } else {
                    type = "DONE";
                }

                if (!type.equals("DONE")) {
                    final var msgReq = new Message(type, index, message);
                    final var wireFormat = msgReq.getWireFormat();

                    // send request
                    outputStream.writeUTF(wireFormat);
                    System.out.println("Sent: " + wireFormat);

                    // receive response
                    final var responseStr = inputStream.readUTF();
                    final var msgResp = Message.jsonToMessage(responseStr);
                    final var remoteResponse = msgResp.getResponse();
                    System.out.println("Received: " + remoteResponse.repackage().getJson());
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
