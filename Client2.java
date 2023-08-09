
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.spi.SelectorProvider;
import java.util.PropertyResourceBundle;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.print.attribute.standard.PrinterIsAcceptingJobs;

public class Client2 extends JFrame implements ActionListener, FocusListener {
    String username;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private Socket socket;
    JTextArea txt = new JTextArea();
    JScrollPane sp = new JScrollPane(txt);
    JTextField write = new JTextField(20);
    JButton quit = new JButton("Go Offline");
    Panel panel = new Panel();
    JLabel jLabel = new JLabel();

    public Client2(Socket socket, String username) {
        setTitle("Chatting as  " + username);
        ImageIcon icon = new ImageIcon("./red.png");
        // setIconImage(icon);

        jLabel.setIcon((Icon) icon);

        txt.setEditable(true);

        panel.add(write);
        panel.add(jLabel);
        add(quit, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        quit.addActionListener(this);
        write.addActionListener(this);
        // write.addFocusListener(this);

        setSize(400, 250);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        txt.setEditable(false);
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
            // while (socket.isConnected()) {
            // // String msgTosenf = scanner.nextLine();
            // String msgTosenf = write.getText();
            // bufferedWriter.write(username + " : " + msgTosenf);
            // write.setText(" ");
            // System.out.println("Msg received");
            // ack();
            // bufferedWriter.newLine();
            // bufferedWriter.flush();

            // }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    public void ack() {
        // txt.append("Msg received\n");

        Image icon = Toolkit.getDefaultToolkit().getImage("./green.png");
        setIconImage(icon);

        ImageIcon img_icon = new ImageIcon("./green.png");
        jLabel.setIcon((Icon) img_icon);

        java.util.Timer t = new java.util.Timer();
        t.schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {

                        Image icon = Toolkit.getDefaultToolkit().getImage("./red.png");
                        setIconImage(icon);

                        ImageIcon imgo_icon = new ImageIcon("./red.png");
                        jLabel.setIcon((Icon) imgo_icon);
                        jLabel.setIcon((Icon) imgo_icon);
                        t.cancel();
                    }
                },
                2000);
    }

    public void addMsg(String msg) throws IOException {
        String preMsg = "";
        String postMsg = msg;
        if (!preMsg.equals(postMsg)) {
            txt.append(postMsg+"\n");
            preMsg = postMsg;
            postMsg = " ";
        }
    }

    public void listenForMsg() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGrpChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGrpChat = bufferedReader.readLine();
                        addMsg(msgFromGrpChat);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == write) {
            try {
                if (socket.isConnected()) {
                    // String msgTosenf = scanner.nextLine();
                    String msgTosenf = write.getText();
                    bufferedWriter.write(username + " : " + msgTosenf);
                    System.out.println("Msg received");
                    ack();
                    write.setText(" ");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            } catch (IOException ee) {
                ee.printStackTrace();
            }

        }
        if (e.getSource() == quit) {
            System.exit(ABORT);
        }

    }

    public void focusGained(FocusEvent e) {
        if (e.getSource() == write) {

            Image icon = Toolkit.getDefaultToolkit().getImage("./red.png");
            setIconImage(icon);
        }
    }

    public void focusLost(FocusEvent e) {
        if (e.getSource() == write) {

            Image icon = Toolkit.getDefaultToolkit().getImage("./red.png");
            setIconImage(icon);
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter username");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client2 client = new Client2(socket, username);
        client.listenForMsg();
        client.sendMsg();
    }

}
