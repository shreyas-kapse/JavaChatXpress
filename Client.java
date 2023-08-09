import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.PropertyResourceBundle;
import java.util.Scanner;

import javax.print.attribute.standard.PrinterIsAcceptingJobs;

public class Client {
    String username;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Socket socket;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMsg() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String msgTosenf = scanner.nextLine();
                bufferedWriter.write(username + " : " + msgTosenf);
                System.out.println("Msg received");
                
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMsg() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGrpChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGrpChat = bufferedReader.readLine();
                        System.out.println(msgFromGrpChat);
                    } catch (IOException e) {
                        closeAll(socket, bufferedReader, bufferedWriter);

                    }

                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
public static void main(String[] args) throws UnknownHostException, IOException {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter username");
    String username = scanner.nextLine();
    Socket socket = new Socket("localhost", 2340);
    Client client = new Client(socket, username);
    client.listenForMsg();
    client.sendMsg();
        
}
}

