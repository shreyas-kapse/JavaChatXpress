import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketImpl;
import java.util.ArrayList;

import javax.xml.catalog.Catalog;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandler = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientname;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientname = bufferedReader.readLine();
            clientHandler.add(this);
            broadCastmsg("SERVER : " + clientname + " Entered in chat\n");
        }
        catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
            
        }
    }

    public void run() {
        String msgFromClient;
        while (socket.isConnected()) {
            try {
                msgFromClient = bufferedReader.readLine();
                broadCastmsg(msgFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadCastmsg(String msgToSend) {
        for (ClientHandler handler : clientHandler) {
            try{
                if(!handler.clientname.equals(clientname)){
                    handler.bufferedWriter.write(msgToSend);
                    handler.bufferedWriter.newLine();
                    handler.bufferedWriter.flush();
                }
    
            }
            catch(IOException e){
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClient() {
        clientHandler.remove(this);
        broadCastmsg("SERVER : " + clientname + " left chat\n");
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter) {
        removeClient();
        try{
            if(bufferedReader!=null){
bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
